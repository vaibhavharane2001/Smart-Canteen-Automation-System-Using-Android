package com.student.canteen.UserComponent.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.student.canteen.R;
import com.student.canteen.ReserveTable.ReserveTableActivity;
import com.student.canteen.UserComponent.dashboard.DashboardFragment;

public class ConfirmationScreenTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirmationscreen_table);

        Button reserveTableButton = findViewById(R.id.reserveTableButton);
        Button skipButton = findViewById(R.id.skipButton);

        reserveTableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmationScreenTableActivity.this, ReserveTableActivity.class);
                startActivity(intent);
            }
        });

        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConfirmationScreenTableActivity.this, DashboardFragment.class);
                startActivity(intent);

            }
        });
    }
}

