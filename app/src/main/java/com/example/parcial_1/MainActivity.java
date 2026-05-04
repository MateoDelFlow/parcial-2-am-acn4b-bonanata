package com.example.parcial_1;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Declaramos las variables
    private EditText etDescripcion, etMonto;
    private TextView tvTotalDisplay;
    private Button btnGuardar;
    private LinearLayout containerGastos; // Variable para el contenedor dinámico
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
        containerGastos = findViewById(R.id.container_gastos); // Vinculación del contenedor

        // Evento del botón
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarGasto();
            }
        });
    }

    private void cargarGasto() {
        String desc = etDescripcion.getText().toString().trim();
        String montoStr = etMonto.getText().toString().trim();

        if (!desc.isEmpty() && !montoStr.isEmpty()) {
            try {
                double monto = Double.parseDouble(montoStr);
                totalAcumulado += monto;

                // Se actualiza el texto en pantalla
                tvTotalDisplay.setText(getString(R.string.label_total_update, String.valueOf(totalAcumulado)));

                // Creación dinámica
                TextView nuevoGasto = new TextView(this);
                nuevoGasto.setText(desc + "\n$" + monto); // Descripción y monto con salto de línea
                nuevoGasto.setTextSize(17);
                nuevoGasto.setTypeface(null, Typeface.BOLD); // Texto en negrita para realismo
                nuevoGasto.setTextColor(getResources().getColor(R.color.text_black));
                int padH = (int) getResources().getDimension(R.dimen.padding_card_h);
                int padV = (int) getResources().getDimension(R.dimen.padding_card_v);
                nuevoGasto.setPadding(padH, padV, padH, padV); // Espaciado interno de la tarjeta

                //Fondo con bordes redondeados
                nuevoGasto.setBackgroundResource(R.drawable.item_gasto_bg);

                //Márgenes entre tarjetas
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                int marginB = (int) getResources().getDimension(R.dimen.margin_card);
                params.setMargins(0, 0, 0, marginB); // Margen de 25px abajo de cada tarjeta
                nuevoGasto.setLayoutParams(params);

                // Agregado al inicio de la lista (el ultimo arriba)
                containerGastos.addView(nuevoGasto, 0);

                // Limpiamos campos para siguiente gasto
                etDescripcion.setText("");
                etMonto.setText("");

                Toast.makeText(this, getString(R.string.msg_gasto_cargado, desc), Toast.LENGTH_SHORT).show();
            } catch (NumberFormatException e) {
                Toast.makeText(this, getString(R.string.msg_monto_invalido), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.msg_campos_vacios), Toast.LENGTH_SHORT).show();
        }
    }
}