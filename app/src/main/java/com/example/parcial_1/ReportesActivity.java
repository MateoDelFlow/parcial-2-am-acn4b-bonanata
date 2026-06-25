package com.example.parcial_1;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import androidx.annotation.NonNull;

public class ReportesActivity extends AppCompatActivity {

    private TextView tvCantidadGastos, tvMontoPromedio;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportes);

        tvCantidadGastos = findViewById(R.id.tv_cantidad_gastos);
        tvMontoPromedio = findViewById(R.id.tv_monto_promedio);
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
                            if (result != null) {
                                for (QueryDocumentSnapshot doc : result) {
                                    Double monto = doc.getDouble("monto");
                                    if (monto != null) {
                                        sum += monto;
                                        count++;
                                    }
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