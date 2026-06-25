package com.example.parcial_1;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.LinearLayout;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;
import java.util.Map;

public class ReportesActivity extends AppCompatActivity {

    private TextView tvCantidadGastos, tvMontoPromedio;
    private LinearLayout containerGastos;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        tvCantidadGastos = findViewById(R.id.tv_cantidad_gastos);
        tvMontoPromedio = findViewById(R.id.tv_monto_promedio);
        containerGastos = findViewById(R.id.container_gastos_reportes);
        db = FirebaseFirestore.getInstance();

        String userId = "";
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        db.collection("gastos")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            double sum = 0.0;
                            QuerySnapshot result = task.getResult();
                            containerGastos.removeAllViews();

                            java.util.HashMap<String, Double> categoryTotals = new java.util.HashMap<>();

                            if (result != null) {
                                for (QueryDocumentSnapshot doc : result) {
                                    Double monto = doc.getDouble("monto");
                                    String cat = doc.getString("categoria");
                                    if (monto != null) {
                                        sum += monto;
                                        count++;
                                        if (cat == null || cat.isEmpty()) {
                                            cat = "📦 Otros";
                                        }
                                        Double currentSum = categoryTotals.get(cat);
                                        if (currentSum == null) {
                                            currentSum = 0.0;
                                        }
                                        categoryTotals.put(cat, currentSum + monto);
                                    }
                                }
                            }

                            java.util.List<Map.Entry<String, Double>> sortedCategories = new java.util.ArrayList<>(categoryTotals.entrySet());
                            java.util.Collections.sort(sortedCategories, new java.util.Comparator<Map.Entry<String, Double>>() {
                                @Override
                                public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                                    return o2.getValue().compareTo(o1.getValue());
                                }
                            });

                            for (Map.Entry<String, Double> entry : sortedCategories) {
                                String catName = entry.getKey();
                                Double totalCat = entry.getValue();
                                if (totalCat > 0) {
                                    TextView nuevoGasto = new TextView(ReportesActivity.this);
                                    nuevoGasto.setText(catName + ": $" + String.format("%.2f", totalCat));
                                    nuevoGasto.setTextSize(17);
                                    nuevoGasto.setTypeface(null, Typeface.BOLD);
                                    nuevoGasto.setTextColor(getResources().getColor(R.color.text_black));
                                    int padH = (int) getResources().getDimension(R.dimen.padding_card_h);
                                    int padV = (int) getResources().getDimension(R.dimen.padding_card_v);
                                    nuevoGasto.setPadding(padH, padV, padH, padV);

                                    nuevoGasto.setBackgroundResource(R.drawable.item_gasto_bg);

                                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                    );
                                    int marginB = (int) getResources().getDimension(R.dimen.margin_card);
                                    params.setMargins(0, 0, 0, marginB);
                                    nuevoGasto.setLayoutParams(params);

                                    containerGastos.addView(nuevoGasto);
                                }
                            }

                            double promedio = count > 0 ? sum / count : 0.0;
                            tvCantidadGastos.setText(getString(R.string.label_cantidad_gastos, String.valueOf(count)));
                            tvMontoPromedio.setText(getString(R.string.label_monto_promedio, String.format("%.2f", promedio)));
                        } else {
                            Toast.makeText(ReportesActivity.this, getString(R.string.msg_error_leer), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}