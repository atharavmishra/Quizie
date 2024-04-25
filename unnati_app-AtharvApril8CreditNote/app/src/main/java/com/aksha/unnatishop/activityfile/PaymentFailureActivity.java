package com.aksha.unnatishop.activityfile;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.aksha.unnatishop.R;

public class PaymentFailureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_failure);
        Button btn = findViewById(R.id.btnPayNow);
        btn.setOnClickListener(view -> finish());
    }
}