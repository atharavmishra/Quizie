package com.aksha.unnatishop;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

public class AcknowledgementDialogBox {
    Activity activity;
    Dialog dialog;

    public AcknowledgementDialogBox(Activity myactivity) {
        activity = myactivity;
    }

    public void startDialog() {

        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.customdailog_alert);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();


    }

    public void dismiss() {
        dialog.dismiss();
    }


}
