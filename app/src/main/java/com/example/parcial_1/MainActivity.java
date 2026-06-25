package com.example.parcial_1;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.android.gms.tasks.OnFailureListener;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Declaramos las variables
    private EditText etDescripcion, etMonto;
    private TextView tvTotalDisplay;
    private Button btnGuardar, btnIrReportes;
    private LinearLayout containerGastos; // Variable para el contenedor dinámico
    private View ivTopLogo;
    private double totalAcumulado = 0.0;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Se vincula Java con XML
        etDescripcion = findViewById(R.id.et_descripcion);
        etMonto = findViewById(R.id.et_monto);
        tvTotalDisplay = findViewById(R.id.tv_total_display);
        btnGuardar = findViewById(R.id.btn_guardar);
        btnIrReportes = findViewById(R.id.btn_ir_reportes);
        containerGastos = findViewById(R.id.container_gastos); // Vinculación del contenedor
        ivTopLogo = findViewById(R.id.iv_top_logo);

        db = FirebaseFirestore.getInstance();

        String userId = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        db.collection("gastos")
                .whereEqualTo("userId", userId)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Toast.makeText(MainActivity.this, getString(R.string.msg_error_leer), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (value != null) {
                            containerGastos.removeAllViews();
                            totalAcumulado = 0.0;

                            java.util.List<QueryDocumentSnapshot> docs = new java.util.ArrayList<>();
                            for (QueryDocumentSnapshot doc : value) {
                                docs.add(doc);
                            }
                            java.util.Collections.sort(docs, new java.util.Comparator<QueryDocumentSnapshot>() {
                                @Override
                                public int compare(QueryDocumentSnapshot o1, QueryDocumentSnapshot o2) {
                                    Long t1 = o1.getLong("timestamp");
                                    Long t2 = o2.getLong("timestamp");
                                    if (t1 == null) t1 = 0L;
                                    if (t2 == null) t2 = 0L;
                                    return t1.compareTo(t2);
                                }
                            });

                            for (QueryDocumentSnapshot doc : docs) {
                                String desc = doc.getString("descripcion");
                                Double monto = doc.getDouble("monto");
                                if (desc != null && monto != null) {
                                    totalAcumulado += monto;

                                    // Creación dinámica
                                    TextView nuevoGasto = new TextView(MainActivity.this);
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
                                }
                            }

                            // Se actualiza el texto en pantalla
                            tvTotalDisplay.setText(getString(R.string.label_total_update, String.valueOf(totalAcumulado)));
                        }
                    }
                });

        // Evento del botón
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarGasto();
            }
        });

        ivTopLogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentConfig = new Intent(MainActivity.this, ConfiguracionActivity.class);
                startActivity(intentConfig);
            }
        });

        btnIrReportes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentReportes = new Intent(MainActivity.this, ReportesActivity.class);
                startActivity(intentReportes);
            }
        });
    }

    private void cargarGasto() {
        String desc = etDescripcion.getText().toString().trim();
        String montoStr = etMonto.getText().toString().trim();

        if (!desc.isEmpty() && !montoStr.isEmpty()) {
            try {
                double monto = Double.parseDouble(montoStr);

                Map<String, Object> gasto = new HashMap<>();
                gasto.put("descripcion", desc);
                gasto.put("monto", monto);
                gasto.put("timestamp", System.currentTimeMillis());

                String currentUserId = "";
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                }
                gasto.put("userId", currentUserId);

                db.collection("gastos")
                        .add(gasto)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, getString(R.string.msg_error_guardar), Toast.LENGTH_SHORT).show();
                            }
                        });

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