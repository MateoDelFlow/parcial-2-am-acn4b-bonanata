package com.example.parcial_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.bumptech.glide.Glide;

public class ConfiguracionActivity extends AppCompatActivity {

    private EditText etApodo, etMoneda;
    private Button btnGuardar;
    private TextView tvWelcome;
    private ImageView ivAvatar;
    private static final String PREFS_NAME = "CashFlowPrefs"; // Nombre del archivo XML de persistencia (Clase 09)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        etApodo = findViewById(R.id.et_config_apodo);
        etMoneda = findViewById(R.id.et_config_moneda);
        btnGuardar = findViewById(R.id.btn_config_guardar);
        tvWelcome = findViewById(R.id.tv_config_welcome);
        ivAvatar = findViewById(R.id.iv_config_avatar);

        // Inicializamos las SharedPreferences en modo de acceso privado (Clase 09)
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        final String currentUid = currentUser != null ? currentUser.getUid() : "";

        if (!currentUid.isEmpty()) {
            Glide.with(this)
                    .load("https://api.dicebear.com/7.x/bottts/png?seed=" + currentUid)
                    .into(ivAvatar);
        }

        // Recuperamos los datos previamente guardados. Si no existen, usamos strings por defecto (Clase 09)
        String apodoGuardado = prefs.getString(currentUid + "_user_apodo", "");
        String apodoParaMostrar = apodoGuardado;
        if (apodoParaMostrar.isEmpty()) {
            if (currentUser != null && currentUser.getEmail() != null) {
                String email = currentUser.getEmail();
                apodoParaMostrar = email.split("@")[0];
            }
        }
        etApodo.setText(apodoGuardado.isEmpty() ? apodoParaMostrar : apodoGuardado);
        etMoneda.setText(prefs.getString(currentUid + "_user_moneda", "ARS"));

        if (!apodoParaMostrar.isEmpty()) {
            tvWelcome.setText(getString(R.string.label_config_welcome, apodoParaMostrar));
        } else {
            tvWelcome.setText(getString(R.string.label_config_welcome_default));
        }

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apodo = etApodo.getText().toString().trim();
                String moneda = etMoneda.getText().toString().trim();

                // Instanciamos el Editor para guardar de forma clave-valor (Clase 09)
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString(currentUid + "_user_apodo", apodo);
                editor.putString(currentUid + "_user_moneda", moneda);
                editor.apply(); // Guarda los cambios de manera asíncrona en segundo plano (Clase 09)

                Toast.makeText(ConfiguracionActivity.this, getString(R.string.msg_ajustes_guardados), Toast.LENGTH_SHORT).show();

                // Finalizamos la actividad para retornar a la pantalla anterior del árbol (Clase 07)
                finish();
            }
        });
    }
}