package com.example.parcial_1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Vinculamos componentes visuales
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnIngresar = findViewById(R.id.btn_login_ingresar);

        //Click para el acceso
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ejecutarLogin();
            }
        });
    }

    private void ejecutarLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Evitar campos vacíos
        if (!email.isEmpty() && !password.isEmpty()) {

            // Inicialización de Intent
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

            //Pasaje de parámetros por clave-valor
            intent.putExtra("USER_EMAIL", email);

            startActivity(intent);

            //Finalizamos Login para que el usuario no vuelva atrás al pulsar el botón nativo
            finish();

        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}