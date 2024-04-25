package com.aksha.unnatishop;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.viewmodel.CreationExtras;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private static final String TAG = BottomSheetFragment.class.getSimpleName();
    private AlertDialog.Builder alertDialogBuilder;
    private StartPayment startPayment;

    public BottomSheetFragment(StartPayment startPayment) {
        this.startPayment = startPayment;
    }

    public static BottomSheetDialogFragment newInstance() {
        BottomSheetDialogFragment fragment = new BottomSheetDialogFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.payment_bottom_sheet_fragment, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setTitle("Payment Result");
        alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
            //do nothing
        });
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));
        Button btn = view.findViewById(R.id.btnPayNow);
        btn.setOnClickListener(view1 -> {
            startPayment.makePayment("100");
        });

        ImageView closeBtn = view.findViewById(R.id.closeBtn);

        closeBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                dismiss();
             }
         });



        //other views ...
    }


    @NonNull
    @Override
    public CreationExtras getDefaultViewModelCreationExtras() {
        return super.getDefaultViewModelCreationExtras();
    }

    public interface StartPayment {
        public void makePayment(String amount);

    }
}