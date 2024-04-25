package com.aksha.unnatishop.activityfile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aksha.unnatishop.BottomSheetFragment;
import com.aksha.unnatishop.CustomProgressBar;
import com.aksha.unnatishop.R;
import com.aksha.unnatishop.Retrofitclass.APIService;
import com.aksha.unnatishop.Retrofitclass.ApiClient;
import com.aksha.unnatishop.Retrofitclass.Modellist.purchase_order_list.PoItem;
import com.aksha.unnatishop.Retrofitclass.Modellist.purchase_order_list.PurchaseOrder;
import com.aksha.unnatishop.Web.MCrypt;
import com.aksha.unnatishop.Web.WebUtils;
import com.aksha.unnatishop.adapter.PaymentRvAdapter;
import com.aksha.unnatishop.adapter.Recycler_Adapter_NoData;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements PaymentResultWithDataListener {

    private AlertDialog.Builder alertDialogBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pyament);
        alertDialogBuilder = new AlertDialog.Builder(PaymentActivity.this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Payment Result");
        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            //do nothing
        });

        Toolbar toolbar1 = findViewById(R.id.toolbar);
        toolbar1.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar1);

        Window w = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            w.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_arrow);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        loadData();
    }

    private void loadData() {
        MCrypt mCrypt = new MCrypt();
        String api_id = WebUtils.check_Pref(PaymentActivity.this);
        int user_id = 0;
        try {
            user_id = Integer.parseInt(mCrypt.Decrypt(api_id));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        APIService service = ApiClient.getClient().create(APIService.class);

        Log.d("uttamkkkk", "loadData: " + user_id);
        Call<PurchaseOrder> call = service.getPoList("2013-09-04", "2024-09-04", user_id, 1);
        CustomProgressBar dialog;
        dialog = new CustomProgressBar(PaymentActivity.this);
        dialog.startlodingdiloge();
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(Call<PurchaseOrder> call, Response<PurchaseOrder> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    PurchaseOrder poListResponse = response.body();
                    assert poListResponse != null;
                    List<PoItem> poList = poListResponse.getPolist();
                    RecyclerView recyclerView = findViewById(R.id.rvReport);
                    if (poList.size() > 0) {

                        LinearLayoutManager layoutManager = new LinearLayoutManager(PaymentActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                        PaymentRvAdapter adapter = new PaymentRvAdapter(PaymentActivity.this, poList, new PaymentRvAdapter.OpenBottomSheet() {
                            @Override
                            public void openBottomSheet() {
                                BottomSheetFragment bottomSheetFragment = new BottomSheetFragment(amount -> openRpay(amount));
                                bottomSheetFragment.show(getSupportFragmentManager(), "BottomSheetDialogFragmentPayment");
                            }
                        });
                        recyclerView.setAdapter(adapter);


                    } else {
                        int img = R.drawable.empty_list;
                        Recycler_Adapter_NoData adap = new Recycler_Adapter_NoData(PaymentActivity.this, img, "No List to show");
                        recyclerView.setLayoutManager(new LinearLayoutManager(PaymentActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(adap);
                        dialog.dismiss();

                    }


                }
            }

            @Override
            public void onFailure(Call<PurchaseOrder> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
                dialog.dismiss();

            }
        });
    }

    private void openRpay(String amount) {
        Checkout checkout = new Checkout();
        // DIBUGE
        checkout.setKeyID("rzp_test_cEHAcqGj020gd1");
        Activity activity = this;
        //LIVE
        //  checkout.setKeyID("rzp_live_f1fW6GbJxfa5XK");
        checkout.setImage(R.drawable.unnati1);
        try {
            JSONObject options = new JSONObject();
            options.put("name", "Atharv Mishra");
            options.put("description", "Order ID-ABCD");
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.jpg");
            options.put("theme.color", getResources().getColor(R.color.colorPrimary));
            options.put("currency", "INR");
            options.put("amount", amount);
            options.put("prefill.email", "atharvmishra77@gmail.com");
            options.put("prefill.contact", "8755328239");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);
            checkout.open(activity, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Intent intent = new Intent(PaymentActivity.this, PaymentSuccessfulActivity.class);
        startActivity(intent);
//        try {
//            alertDialogBuilder.setMessage("Payment Successful :\nPayment ID: " + s + "\nPayment Data: " + paymentData.getData());
//            alertDialogBuilder.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Intent intent = new Intent(PaymentActivity.this, PaymentFailureActivity.class);
        startActivity(intent);
//        try {
//            alertDialogBuilder.setMessage("Payment Failed:\nPayment Data: " + paymentData.getData());
//            alertDialogBuilder.show();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
