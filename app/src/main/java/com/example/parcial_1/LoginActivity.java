package com.example.parcial_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnIngresar;
    private TextView tvRegisterLink;
    private FirebaseAuth mAuth;
    private boolean isLoginMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Vinculamos componentes visuales
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnIngresar = findViewById(R.id.btn_login_ingresar);
        tvRegisterLink = findViewById(R.id.tv_login_register_link);

        mAuth = FirebaseAuth.getInstance();

        //Click para el acceso
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ejecutarLogin();
            }
        });

        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isLoginMode = !isLoginMode;
                if (isLoginMode) {
                    btnIngresar.setText(getString(R.string.btn_ingresar));
                    tvRegisterLink.setText(getString(R.string.btn_registro_link));
                } else {
                    btnIngresar.setText(getString(R.string.btn_registrarse));
                    tvRegisterLink.setText(getString(R.string.btn_login_link));
                }
            }
        });
    }

    private void ejecutarLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Evitar campos vacíos
        if (!email.isEmpty() && !password.isEmpty()) {

            if (password.length() < 6) {
                Toast.makeText(this, getString(R.string.msg_error_password_corta), Toast.LENGTH_SHORT).show();
                return;
            }

            if (isLoginMode) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    irAlDashboard(email);
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.msg_credenciales_invalidas), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } else {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    irAlDashboard(email);
                                } else {
                                    Toast.makeText(LoginActivity.this, getString(R.string.msg_auth_failed), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

        } else {
            Toast.makeText(this, getString(R.string.msg_campos_vacios), Toast.LENGTH_SHORT).show();
        }
    }

    private void irAlDashboard(String email) {
        // Inicialización de Intent
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        //Pasaje de parámetros por clave-valor
        intent.putExtra("USER_EMAIL", email);

        startActivity(intent);

        //Finalizamos Login para que el usuario no vuelva atrás al pulsar el botón nativo
        finish();
    }
}