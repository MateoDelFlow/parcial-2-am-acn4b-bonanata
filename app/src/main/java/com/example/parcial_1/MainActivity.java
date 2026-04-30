package com.example.parcial_1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declaramos las variables
    private EditText etDescripcion, etMonto;
    private TextView tvTotalDisplay;
    private Button btnGuardar;
    private double totalAcumulado = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Se vincula Java con XML
        etDescripcion = findViewById(R.id.et_descripcion);
        etMonto = findViewById(R.id.et_monto);
        tvTotalDisplay = findViewById(R.id.tv_total_display);
        btnGuardar = findViewById(R.id.btn_guardar);

        //Evento del botón
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarGasto();
            }
        });
    }

    private void cargarGasto() {
        String desc = etDescripcion.getText().toString();
        String montoStr = etMonto.getText().toString();

        if (!desc.isEmpty() && !montoStr.isEmpty()) {
            double monto = Double.parseDouble(montoStr);
            totalAcumulado += monto;

            // Actualizamos el texto en pantalla
            tvTotalDisplay.setText("Total acumulado: $" + totalAcumulado);

            // Limpiamos los campos para el próximo gasto
            etDescripcion.setText("");
            etMonto.setText("");

            Toast.makeText(this, "Gasto cargado: " + desc, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
        }
    }
}