package com.danielblandon.carrito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginAdminActivity extends AppCompatActivity {

    private Button btnEnviarCodigo,btnIniciar;

    private EditText numero,codigo;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private PhoneAuthProvider.ForceResendingToken resendingToken;
    private String VerificacionId;
    private ProgressDialog dialog;
    private String phoneNumber;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        btnEnviarCodigo = findViewById(R.id.BtnEnviarCodAdmin);
        btnIniciar = findViewById(R.id.BtnIniciarAdmin);
        numero = findViewById(R.id.EtnumeroAdmin);
        codigo = findViewById(R.id.EtcodigoAdmin);


        auth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);


        btnEnviarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                phoneNumber = numero.getText().toString();

                if(TextUtils.isEmpty(phoneNumber)){
                    Toast.makeText(LoginAdminActivity.this,"Ingresa primero tu número",Toast.LENGTH_SHORT).show();
                }else{
                    dialog.setTitle("Validando Número");
                    dialog.setMessage("Espera mientas se hace la validación");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);

                    PhoneAuthOptions opciones = PhoneAuthOptions.newBuilder(auth)
                            .setPhoneNumber(phoneNumber)
                            .setTimeout(60L, TimeUnit.SECONDS)
                            .setActivity(LoginAdminActivity.this)
                            .setCallbacks(callbacks)
                            .build();

                    PhoneAuthProvider.verifyPhoneNumber(opciones);
                }
            }
        });
        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numero.setVisibility(View.GONE);
                btnEnviarCodigo.setVisibility(View.GONE);

                String VerificacionCodigo = codigo.getText().toString();

                if(TextUtils.isEmpty(VerificacionCodigo)){
                    Toast.makeText(LoginAdminActivity.this,"Ingresa El Código",Toast.LENGTH_SHORT).show();
                }else{
                    dialog.setTitle("Verificando");
                    dialog.setMessage("Espera por favor");
                    dialog.show();
                    dialog.setCanceledOnTouchOutside(true);

                    PhoneAuthCredential credencial = PhoneAuthProvider.getCredential(VerificacionId,VerificacionCodigo);
                    IngresadoConExito(credencial);
                }

            }
        });
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                IngresadoConExito(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

                dialog.dismiss();
                Toast.makeText(LoginAdminActivity.this,"Error en el inicio\n Causas: Número Invalido - Sin conexion ",Toast.LENGTH_SHORT ).show();
                numero.setVisibility(View.VISIBLE);
                btnEnviarCodigo.setVisibility(View.VISIBLE);
                codigo.setVisibility(View.GONE);
                btnIniciar.setVisibility(View.GONE);

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                VerificacionId = s;
                resendingToken = token;
                dialog.dismiss();
                Toast.makeText(LoginAdminActivity.this,"Código Enviado, Revisa tu bandeja de entrada",Toast.LENGTH_SHORT).show();
                numero.setVisibility(View.GONE);
                btnEnviarCodigo.setVisibility(View.GONE);
                codigo.setVisibility(View.VISIBLE);
                btnIniciar.setVisibility(View.VISIBLE);
            }
        };
    }

    private void IngresadoConExito(PhoneAuthCredential Credential) {

        auth.signInWithCredential(Credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(LoginAdminActivity.this,"Ingreso Exitoso",Toast.LENGTH_SHORT).show();
                    EnviarALaPrincipal();
                }else {
                    String e = task.getException().toString();
                    Toast.makeText(LoginAdminActivity.this, "Error:"+ e, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser != null){
            EnviarALaPrincipal();
        }
    }

    private void EnviarALaPrincipal() {

        Intent intent = new Intent(LoginAdminActivity.this,PrincipalActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("phone"," phoneNumber");
        startActivity(intent);
        finish();
    }

}