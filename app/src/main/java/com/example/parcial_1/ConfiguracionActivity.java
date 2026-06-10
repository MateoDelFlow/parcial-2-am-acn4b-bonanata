package com.example.parcial_1;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ConfiguracionActivity extends AppCompatActivity {

    private EditText etApodo, etMoneda;
    private Button btnGuardar;
    private static final String PREFS_NAME = "CashFlowPrefs"; // Nombre del archivo XML de persistencia (Clase 09)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        etApodo = findViewById(R.id.et_config_apodo);
        etMoneda = findViewById(R.id.et_config_moneda);
        btnGuardar = findViewById(R.id.btn_config_guardar);

        // Inicializamos las SharedPreferences en modo de acceso privado (Clase 09)
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Recuperamos los datos previamente guardados. Si no existen, usamos strings por defecto (Clase 09)
        etApodo.setText(prefs.getString("user_apodo", ""));
        etMoneda.setText(prefs.getString("user_moneda", "ARS"));

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apodo = etApodo.getText().toString().trim();
                String moneda = etMoneda.getText().toString().trim();

                // Instanciamos el Editor para guardar de forma clave-valor (Clase 09)
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("user_apodo", apodo);
                editor.putString("user_moneda", moneda);
                editor.apply(); // Guarda los cambios de manera asíncrona en segundo plano (Clase 09)

                Toast.makeText(ConfiguracionActivity.this, getString(R.string.msg_ajustes_guardados), Toast.LENGTH_SHORT).show();

                // Finalizamos la actividad para retornar a la pantalla anterior del árbol (Clase 07)
                finish();
            }
        });
    }
}