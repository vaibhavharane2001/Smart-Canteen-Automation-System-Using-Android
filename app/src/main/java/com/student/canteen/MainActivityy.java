package com.student.canteen;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

public class MainActivityy extends AppCompatActivity implements PaymentResultListener {


    //    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_dashboard);
//
//        findViewById(R.id.pay).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startPayment();
//
//            }
//        });
//
//    }
    public void startPayment(Context context,String amount,String username) {
        Checkout checkout = new Checkout();

        checkout.setImage(R.mipmap.add_food_logo);
        final Activity activity = this;
        double totalamount = Double.parseDouble(amount);
        double multipliedAmount = totalamount * 100;
        String multipliedTotalAmount = String.valueOf(multipliedAmount);

        try{
            JSONObject options = new JSONObject();
            options.put("name",username);
            options.put("description","Payment for anything");
            options.put("send_sms_hash","true");
            options.put("allow_rotation","false");
            options.put("currency","INR");
            options.put("amount",multipliedTotalAmount);

            JSONObject preFill = new JSONObject();
            preFill.put("email"," ");
            preFill.put("contact"," ");

            options.put("preFill",preFill);

            checkout.open((Activity) context, options);
        }
        catch(Exception e){
            Toast.makeText(activity, "Error in payment :" + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();

        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        Toast.makeText(this, "Payment Success! "+s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment Error! "+s, Toast.LENGTH_SHORT).show();

    }
}
