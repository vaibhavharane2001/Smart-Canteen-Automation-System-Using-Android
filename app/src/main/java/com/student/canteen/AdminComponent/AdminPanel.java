package com.student.canteen.AdminComponent;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.student.canteen.AdminAlgorithm;
import com.student.canteen.FoodList.Food;
import com.student.canteen.History.HistoryModel;
import com.student.canteen.History.itemHistory;
import com.student.canteen.MainActivity;
import com.student.canteen.MainActivityUp;
import com.student.canteen.OrderList.OrderIteam;
import com.student.canteen.QrCodeImplementation.QrCamera;
import com.student.canteen.R;
import com.student.canteen.Registration.Registration;
import com.student.canteen.RegistrationUp.RegistrationUp;
import com.student.canteen.StartingActivity;
import com.student.canteen.Util.CanteenUtil;
import com.student.canteen.constant.CanteenConstant;
import com.student.canteen.constant.FireBaseConstant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class AdminPanel extends AppCompatActivity {
  CardView viewOderCard, addUserCard, addFoodCard, viewLogCard, viewAnalysisCard, qrLogoCard, algoLogoCard;
  DatabaseReference databaseReference;
  FirebaseDatabase firebaseDatabase;
  FirebaseAuth firebaseAuth;
  TextView adminPanelTotalAmount;
  int netIncome = 0;
  Button logout;
  private static final DecimalFormat df = new DecimalFormat("0.00");

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.admin_panel);
    viewOderCard = findViewById(R.id.viewOrderLogoCard);
    addUserCard = findViewById(R.id.addUserLogoCard);
    addFoodCard = findViewById(R.id.addFoodLogoCard);
    viewLogCard = findViewById(R.id.logLogoCard);
    qrLogoCard = findViewById(R.id.QRLogoCard);
    algoLogoCard = findViewById(R.id.AlgoLogoCard);
    viewAnalysisCard = findViewById(R.id.analysisLogoCard);
    firebaseDatabase = FirebaseDatabase.getInstance();
    firebaseAuth = FirebaseAuth.getInstance();
    databaseReference = firebaseDatabase.getReference(FireBaseConstant.HISTORY);
    adminPanelTotalAmount = findViewById(R.id.adminPanelTotalAmount);
    logout = findViewById(R.id.logoutt);
    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_DENIED) {
      ActivityCompat.requestPermissions(AdminPanel.this, new String[]{Manifest.permission.CAMERA}, 1);
    }
    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(@NonNull DataSnapshot snapshot) {
        if (snapshot.getValue() != null) {
          netIncome = 0;
          for (DataSnapshot s : snapshot.getChildren()) {
            if (CanteenUtil.getYearAndDayFromMilliSecond(System.currentTimeMillis()).equals(CanteenUtil.getYearAndDayFromMilliSecond(Long.parseLong(s.child("time").getValue().toString())))) {
              HistoryModel historyModel = s.getValue(HistoryModel.class);
              if (null != historyModel && historyModel.getComment().contains(CanteenConstant.ADMIN)) {
                netIncome = netIncome + Integer.parseInt(historyModel.getTotal());
              }
            } else {
              databaseReference.child(s.getKey()).removeValue();
            }
          }
          adminPanelTotalAmount.setText("Rs." + NumberFormat.getInstance().format(Float.parseFloat(df.format(netIncome))));
          databaseReference.removeEventListener(this);
        }
        databaseReference.removeEventListener(this);
      }

      @Override
      public void onCancelled(@NonNull DatabaseError error) {
        databaseReference.removeEventListener(this);
      }
    });

    viewOderCard.setOnClickListener(v -> {
      Intent intent = new Intent(getApplicationContext(), OrderIteam.class);
      startActivity(intent);
    });
    addUserCard.setOnClickListener(v -> {
      Intent intent = new Intent(getApplicationContext(), Registration.class);
      startActivity(intent);
    });

    addFoodCard.setOnClickListener(v -> {
      Intent intent = new Intent(getApplicationContext(), Food.class);
      startActivity(intent);
    });
    viewLogCard.setOnClickListener(v -> {
      Intent intent = new Intent(getApplicationContext(), itemHistory.class);
      startActivity(intent);
    });
    viewAnalysisCard.setOnClickListener(v -> {
      Intent intent = new Intent(getApplicationContext(), AdminFilterPage.class);
      startActivity(intent);
    });

    qrLogoCard.setOnClickListener(v -> {
      Intent intent = new Intent(getApplicationContext(), QrCamera.class);
      startActivity(intent);
    });
    algoLogoCard.setOnClickListener(v -> {
      Intent intent = new Intent(getApplicationContext(), AdminAlgorithm.class);
      startActivity(intent);
    });

    logout.setOnClickListener(view -> {
      firebaseAuth.signOut();
      Intent intent = new Intent(getApplicationContext(), StartingActivity.class);
      intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
      startActivity(intent);
    });


  }
  private void showExitConfirmationDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage("Do you want to exit?");
    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        firebaseAuth.signOut();

        finishAffinity();
//                Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
//                startActivity(intent);
      }
    });
    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialogInterface, int i) {
        // Do nothing, just dismiss the dialog
      }
    });
    builder.show();
  }



  @Override
  public void onBackPressed() {
    showExitConfirmationDialog();
  }

//    @Override
//    public void onBackPressed() {
//      super.onBackPressed();
//      Intent intent = new Intent(getApplicationContext(), StartingActivity.class);
//      startActivity(intent);
//    }


  }
