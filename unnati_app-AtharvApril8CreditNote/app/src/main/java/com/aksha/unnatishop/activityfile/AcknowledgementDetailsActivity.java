package com.aksha.unnatishop.activityfile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aksha.unnatishop.AcknowledgementDialogBox;
import com.aksha.unnatishop.CustomProgressBar;
import com.aksha.unnatishop.R;
import com.aksha.unnatishop.Retrofitclass.APIService;
import com.aksha.unnatishop.Retrofitclass.ApiClient;
import com.aksha.unnatishop.Retrofitclass.Modellist.purorderdetils.PoItemdetails;
import com.aksha.unnatishop.Retrofitclass.Modellist.purorderdetils.PurchaseOrderdetls;
import com.aksha.unnatishop.Web.WebUtils;
import com.aksha.unnatishop.adapter.PurchaseitemAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AcknowledgementDetailsActivity extends AppCompatActivity {

    String orderId;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acknowledgement_details);

        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar);
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


        Intent intent = getIntent(); // or getActivity().getIntent() if you're in a Fragment
        orderId = intent.getStringExtra("orderId");
        String totalAmount = intent.getStringExtra("totalAmount");
        String productName = intent.getStringExtra("productName");
        String status = intent.getStringExtra("status");
        String date = intent.getStringExtra("date");
        String quantity = intent.getStringExtra("quantity");
        String taxrate = intent.getStringExtra("taxrate");
        String appvovedate = intent.getStringExtra("approvedate");
        String poId = intent.getStringExtra("poId");
        String dispachdate = intent.getStringExtra("dispatchedate");
        String delivrydate = intent.getStringExtra("deliverydate");
        String lr_no = intent.getStringExtra("lr_no");
        String ackdate = intent.getStringExtra("ackdate");

        taxrate = "0";


        TextView orderPlaced = findViewById(R.id.tvOrderPlaced);
        TextView tvOrderPending = findViewById(R.id.tvOrderPending);
        TextView tvOrderDispatched = findViewById(R.id.tvOrderDispatched);
        TextView tvOrderDispatchedStatus = findViewById(R.id.tvOrderDispatchedStatus);
        TextView tvOrderConfirmedStatus = findViewById(R.id.tvOrderConfirmedStatus);
        TextView tvOrderDelivered = findViewById(R.id.tvOrderDelivered);
        TextView tvOrderAcknowledgement = findViewById(R.id.tvOrderAcknowledgement);
        TextView tvOrderAcknowledgementStatus = findViewById(R.id.tvOrderAcknowledgementStatus);
        ImageView ivDotAcknowledgement = findViewById(R.id.ivDotAcknowledgement);
        ImageView ivDot_approved = findViewById(R.id.ivDot_approved);
        ImageView ivDelivered = findViewById(R.id.ivDelivered);
        ImageView ivDot_dispach = findViewById(R.id.ivDot_dispach);


        assert status != null;
        if (status.equalsIgnoreCase("Pending")) {

            ivDot_approved.setImageResource(R.drawable.red_dot);
            ivDotAcknowledgement.setImageResource(R.drawable.red_dot);
            ivDot_dispach.setImageResource(R.drawable.red_dot);
            ivDelivered.setImageResource(R.drawable.red_dot);
            int color = ContextCompat.getColor(this, R.color.gray);
            tvOrderPending.setTextColor(color);
            tvOrderPending.setText("Pending");
            tvOrderConfirmedStatus.setText("Pending");
            tvOrderAcknowledgementStatus.setText("Pending");
            tvOrderDispatchedStatus.setText("Pending");
        } else if (status.equalsIgnoreCase("Approved")) {

            ivDot_approved.setImageResource(R.drawable.blue_dot);
            ivDotAcknowledgement.setImageResource(R.drawable.red_dot);
            ivDot_dispach.setImageResource(R.drawable.red_dot);
            ivDelivered.setImageResource(R.drawable.red_dot);
            int color = ContextCompat.getColor(this, R.color.gray);
            tvOrderPending.setTextColor(color);
            tvOrderPending.setText(appvovedate);

            tvOrderDispatchedStatus.setText("Pending");
            tvOrderConfirmedStatus.setText("Pending");
            tvOrderAcknowledgementStatus.setText("Pending");
            int orange = ContextCompat.getColor(this, R.color.orange);
            tvOrderDispatchedStatus.setTextColor(orange);
            tvOrderConfirmedStatus.setTextColor(orange);
            tvOrderDispatchedStatus.setTextColor(orange);

        } else if (status.equalsIgnoreCase("Dispatched")) {
            ivDot_approved.setImageResource(R.drawable.blue_dot);
            ivDotAcknowledgement.setImageResource(R.drawable.blue_dot);
            ivDot_dispach.setImageResource(R.drawable.blue_dot);
            ivDelivered.setImageResource(R.drawable.red_dot);
            int color = ContextCompat.getColor(this, R.color.gray);
            tvOrderPending.setTextColor(color);
            tvOrderDispatchedStatus.setTextColor(color);
            tvOrderPending.setText(appvovedate);
            tvOrderDispatchedStatus.setTextColor(color);
            assert lr_no != null;
            if (lr_no.isEmpty()) {
                tvOrderDispatchedStatus.setText(dispachdate);
            } else {
                tvOrderDispatchedStatus.setText(dispachdate + " (" + lr_no + ")");
            }
            tvOrderConfirmedStatus.setText("Pending");
            tvOrderAcknowledgementStatus.setText("Pending");

            int orange = ContextCompat.getColor(this, R.color.orange);
            tvOrderConfirmedStatus.setTextColor(orange);
            tvOrderDispatchedStatus.setTextColor(orange);
        } else if (status.equalsIgnoreCase("Delivered")) {
            ivDot_approved.setImageResource(R.drawable.blue_dot);
            ivDotAcknowledgement.setImageResource(R.drawable.red_dot);
            ivDot_dispach.setImageResource(R.drawable.blue_dot);
            ivDelivered.setImageResource(R.drawable.blue_dot);
            int color = ContextCompat.getColor(this, R.color.gray);
            tvOrderPending.setTextColor(color);
            tvOrderDispatchedStatus.setTextColor(color);
            tvOrderConfirmedStatus.setTextColor(color);
            tvOrderPending.setText(appvovedate);
            tvOrderDispatchedStatus.setTextColor(color);
            assert lr_no != null;
            if (lr_no.isEmpty()) {
                tvOrderDispatchedStatus.setText(dispachdate);
            } else {
                tvOrderDispatchedStatus.setText(dispachdate + " (" + lr_no + ")");
            }

            tvOrderConfirmedStatus.setText(delivrydate);
            tvOrderAcknowledgementStatus.setText("Pending");
            int orange = ContextCompat.getColor(this, R.color.orange);
            tvOrderAcknowledgementStatus.setTextColor(orange);
        } else if (status.equalsIgnoreCase("Acknowledged")) {
            ivDot_approved.setImageResource(R.drawable.blue_dot);
            ivDotAcknowledgement.setImageResource(R.drawable.blue_dot);
            ivDot_dispach.setImageResource(R.drawable.blue_dot);
            ivDelivered.setImageResource(R.drawable.blue_dot);
            int color = ContextCompat.getColor(this, R.color.gray);
            tvOrderPending.setTextColor(color);
            tvOrderDispatchedStatus.setTextColor(color);
            tvOrderConfirmedStatus.setTextColor(color);
            tvOrderPending.setText(appvovedate);
            tvOrderDispatchedStatus.setTextColor(color);
            assert lr_no != null;
            if (lr_no.isEmpty()) {
                tvOrderDispatchedStatus.setText(dispachdate);
            } else {
                tvOrderDispatchedStatus.setText(dispachdate + " (" + lr_no + ")");
            }
            tvOrderAcknowledgementStatus.setTextColor(color);
            tvOrderConfirmedStatus.setText(delivrydate);
            tvOrderAcknowledgementStatus.setText(ackdate);
        }


//        Double productprice = Double.valueOf(pr);
//        float new_price_total = WebUtils.round_convert(String.valueOf(productprice));
//        @SuppressLint("DefaultLocale") String Prodprice = String.format("%.2f", new_price_total);

        String formattedNullDate = changeDateFormat(date);
        TextView inv_date = findViewById(R.id.inv_date);
        inv_date.setText(formattedNullDate);


//

        TextView tcsrate = findViewById(R.id.tcsrate);
//        tcsrate.setText(tcs_rate);

        TextView tcsamt = findViewById(R.id.tcsamt);

//        float totaltaxtcs = WebUtils.round_convert(String.valueOf(tcs_value));
//        @SuppressLint("DefaultLocale") String tcs_amt = String.format("%.2f", totaltaxtcs);
//        tcsamt.setText("₹" + formatAmount(Double.parseDouble(tcs_amt)));
        if (ackdate.equals("Pending")) {
            TextView tvAcknowledge = findViewById(R.id.tvAcknowledge);
            tvAcknowledge.setVisibility(View.VISIBLE);
            ImageView ivBox = findViewById(R.id.ivBox);
            ivBox.setVisibility(View.VISIBLE);
        }

        TextView tvOrderId = this.findViewById(R.id.tvOrderId);
        tvOrderId.setText(String.format(orderId));


        TextView tvDate = findViewById(R.id.tvDate);
        String foDate = changeDateFormat(date);
        tvDate.setText(foDate);

        TextView tvTotalAmount = findViewById(R.id.tvTotalAmount);
        float toltotal = WebUtils.round_convert(String.valueOf(totalAmount));
        @SuppressLint("DefaultLocale") String tol_total = String.format("%.2f", toltotal);
        tvTotalAmount.setText("₹" + formatAmount(Double.parseDouble(tol_total)));


        toolbar1.setNavigationOnClickListener(view -> finish());

        ImageView ivBox = findViewById(R.id.ivBox);

        ivBox.setOnClickListener(v -> {
            APIService service = ApiClient.getClient().create(APIService.class);
            Call<Object> call = service.acknowledgeDelivery(poId);
            CustomProgressBar dialog;
            AcknowledgementDialogBox acknowledgementDialogBox;
            acknowledgementDialogBox = new AcknowledgementDialogBox(AcknowledgementDetailsActivity.this);
            dialog = new CustomProgressBar(AcknowledgementDetailsActivity.this);
            dialog.startlodingdiloge();
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<Object> call, Response<Object> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();

                        JSONObject jsonObject = new JSONObject((Map) response.body());
                        String status = null;
                        try {
                            status = jsonObject.getString("status");
                            showAcknowledgementDialog(AcknowledgementDetailsActivity.this, status);
//                            new SweetAlertDialog(AcknowledgementDetailsActivity.this, SweetAlertDialog.SUCCESS_TYPE)
//                                    .setTitleText("Success")
//                                    .setContentText("Acknowledgement Successful")
//                                    .setConfirmText("Ok")
//                                    .showCancelButton(true).setConfirmClickListener(sweetAlertDialog -> {
//                                        sweetAlertDialog.dismiss();
//                                        finish();
//                                    });

                        } catch (JSONException e) {
                            throw new RuntimeException(e);

                        }

//                        dialog.dismiss();
//                        DownloadInvoiceResponse poListResponse = response.body();
//                        assert poListResponse != null;
//
//                        Log.d("polistkkk", "onResponse: " + poListResponse.getFile_url());
//
//                        Toast.makeText(AcknowledgementDetailsActivity.this, "Invoice Downloading Completed", Toast.LENGTH_SHORT).show();
//
//                        if (poListResponse != null) {
//                            savePdfFile(poListResponse.getFile_url(), "invoice_my_purchase.pdf");
//
//                        } else {
//
//                        }


                    } else {
                        new SweetAlertDialog(AcknowledgementDetailsActivity.this, SweetAlertDialog.WARNING_TYPE).setTitleText("Error").setContentText("Something Went Wrong").setConfirmText("Ok").showCancelButton(true).setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.dismiss();
                            }
                        }).show();
                    }
                }

                @Override
                public void onFailure(Call<Object> call, Throwable t) {
                    System.out.println("Error: " + t.getMessage());
                    dialog.dismiss();

                }
            });

        });


        try {
            loadData();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public void savePdfFile(String pdfUrl, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));
        request.setTitle(fileName);
        request.setMimeType("application/pdf");
        request.allowScanningByMediaScanner();
        request.setAllowedOverMetered(true);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        long downloadId = downloadManager.enqueue(request);
        BroadcastReceiver onComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long receivedDownloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (receivedDownloadId == downloadId) {

                    context.unregisterReceiver(this);
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), Context.RECEIVER_NOT_EXPORTED);
        }
    }

    public static String changeDateFormat(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(dateString);

            SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            assert date != null;
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void showAcknowledgementDialog(Activity activity, String msg) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_acknowledgement);

        Button dialogButton = dialog.findViewById(R.id.btnOkay);
        dialogButton.setOnClickListener(v -> {
            dialog.dismiss();
            finish();
        });

        dialog.show();


    }

    private void loadData() throws Exception {

        APIService service = ApiClient.getClient().create(APIService.class);
        Call<PurchaseOrderdetls> call = service.getpodetials(orderId, "0");
        CustomProgressBar dialog;
        dialog = new CustomProgressBar(AcknowledgementDetailsActivity.this);
        dialog.startlodingdiloge();
        call.enqueue(new Callback<>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<PurchaseOrderdetls> call, Response<PurchaseOrderdetls> response) {
                if (response.isSuccessful()) {
                    dialog.dismiss();
                    PurchaseOrderdetls poListResponse = response.body();
                    assert poListResponse != null;
                    List<PoItemdetails> poList = poListResponse.getPolist();
                    RecyclerView recyclerView = findViewById(R.id.rvReport);

                    double cargoTotalValue = 0.0;
                    double totalTax = 0.0;

                    for (PoItemdetails item : poList) {
                        String cargoValueStr = item.getCargo_value();
                        String totalTaxStr = item.getP_total_tax();
                        if (cargoValueStr != null && !cargoValueStr.isEmpty()) {
                            cargoTotalValue += Double.parseDouble(cargoValueStr);
                        }
                        if (totalTaxStr != null && !totalTaxStr.isEmpty()) {
                            totalTax += Double.parseDouble(totalTaxStr);
                        }
                    }

                    float toltotal = WebUtils.round_convert(String.valueOf(cargoTotalValue));
                    @SuppressLint("DefaultLocale") String tol_total = String.format("%.2f", toltotal);

                    float toltotaltax = WebUtils.round_convert(String.valueOf(totalTax));
                    @SuppressLint("DefaultLocale") String ttotaltax = String.format("%.2f", toltotaltax);

                    TextView tvamt = findViewById(R.id.tvamt);
                    tvamt.setText("₹" + formatAmount(Double.parseDouble(tol_total)));

                    TextView taxtoalamt = findViewById(R.id.taxtoalamt);
                    taxtoalamt.setText("₹" + formatAmount(Double.parseDouble(ttotaltax)));


                    LinearLayoutManager layoutManager = new LinearLayoutManager(AcknowledgementDetailsActivity.this);
                    layoutManager.setReverseLayout(true);
                    layoutManager.setStackFromEnd(true);
                    recyclerView.setLayoutManager(layoutManager);
                    PurchaseitemAdapter adapter = new PurchaseitemAdapter(AcknowledgementDetailsActivity.this, poList, "1");
                    recyclerView.setAdapter(adapter);


                }
            }

            @Override
            public void onFailure(Call<PurchaseOrderdetls> call, Throwable t) {
                System.out.println("Error: " + t.getMessage());
                dialog.dismiss();

            }
        });


    }

    private String formatAmount(double amount) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyPattern("#,##0.00");
        return decimalFormat.format(amount);
    }
}
