package com.aksha.unnatishop.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.aksha.unnatishop.R;
import com.aksha.unnatishop.Retrofitclass.Modellist.purchase_order_list.PoItem;
import com.aksha.unnatishop.Web.WebUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PaymentRvAdapter extends RecyclerView.Adapter<PaymentRvAdapter.NumberViewHolder> {


    private final List<PoItem> orders;
    private final Context context;

    private final OpenBottomSheet openBottomSheet;

    public PaymentRvAdapter(Context context, List<PoItem> orders, OpenBottomSheet openBottomSheet) {
        this.openBottomSheet = openBottomSheet;
        this.orders = orders;
        this.context = context;
    }

    @NonNull
    @Override
    public NumberViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_rv_adapter, parent, false);
        return new NumberViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NumberViewHolder holder, @SuppressLint("RecyclerView") int position) {
//        holder.bind(numbers.get(position));

        holder.textView.setText(String.format("Inv No. : %s", orders.get(position).getinvoice_number()));
        holder.tvProduct.setText(orders.get(position).getProduct_name());
        String orderTotal = orders.get(position).getOrder_total();

        if (orderTotal == null || Double.parseDouble(orderTotal) < 0) {

            double price = Double.parseDouble(orders.get(position).getP_qnty()) * Double.parseDouble(orders.get(position).getP_price());
            holder.tvAmount.setText(String.format("₹%s", formatAmount(price)));
            holder.ivNext.setVisibility(View.GONE);
        } else {
            float toltotal = WebUtils.round_convert(String.valueOf(orders.get(position).getOrder_total()));
            @SuppressLint("DefaultLocale") String tol_total = String.format("%.2f", toltotal);
            holder.tvAmount.setText(String.format("₹%s", formatAmount(Double.parseDouble(tol_total))));
        }
        if (orders.get(position).getPending_bit().equals("0")) {
            holder.tvStatus.setText("Approved");
            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark)); // Example: Set text color to red

        } else if (orders.get(position).getPending_bit().equals("1")) {
            holder.tvStatus.setText("Pending");
            holder.tvStatus.setTextColor(Color.RED);
        }


        String dateString = orders.get(position).getOrder_date();
        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat outputFormatYear = new SimpleDateFormat("yyyy", Locale.US);
        DateFormat outputFormatMonth = new SimpleDateFormat("MMM", Locale.US);
        DateFormat outputFormatDay = new SimpleDateFormat("dd", Locale.US);

        try {
            Date date = inputFormat.parse(dateString);
            String year = outputFormatYear.format(date);
            String month = outputFormatMonth.format(date);
            String day = outputFormatDay.format(date);

            holder.tvDay.setText(day);
            holder.tvMonth.setText(month);
            holder.tvYear.setText(year);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.ivNext.setOnClickListener(view -> {
            openBottomSheet.openBottomSheet();
        });


    }


    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class NumberViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final TextView tvProduct;
        private final TextView tvAmount;
        private final TextView tvDay;
        private final TextView tvMonth;
        private final TextView tvYear;
        private final TextView tvStatus;
        private final CardView ivNext;


        public NumberViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tvOrder);
            tvProduct = itemView.findViewById(R.id.tvProduct);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvDay = itemView.findViewById(R.id.txtDay);
            tvMonth = itemView.findViewById(R.id.txtMonth);
            tvYear = itemView.findViewById(R.id.txtYear);
            ivNext = itemView.findViewById(R.id.ivNext);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }

    }

    private String formatAmount(double amount) {
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.getDefault());
        DecimalFormat decimalFormat = (DecimalFormat) numberFormat;
        decimalFormat.applyPattern("#,##0.00");
        return decimalFormat.format(amount);
    }


    public interface OpenBottomSheet {
        void openBottomSheet();

    }


}