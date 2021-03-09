package com.bletpms.app.ui.pair;

import android.app.Activity;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.bletpms.app.R;
import com.bletpms.app.database.Vehicle;
import com.google.android.material.card.MaterialCardView;

import java.util.Locale;

public class SearchingDeviceDialog {

    Activity activity;
    Dialog dialog;

    private CountDownTimer timer;
    private static int SEARCHING_TIME_MS;

    public SearchingDeviceDialog(Activity activity, int searchingTime) {
        this.activity = activity;
        SEARCHING_TIME_MS = searchingTime;
    }

    public void showDialog(){
        dialog  = new Dialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setContentView(R.layout.dialog_pair_device_auto_search);

        TextView countdownTextView = dialog.findViewById(R.id.countdownTextView);

        dialog.show();

        timer = new CountDownTimer(SEARCHING_TIME_MS, 1000) {

            public void onTick(long millisUntilFinished) {
                countdownTextView.setText(String.format(Locale.getDefault(), "%ds", millisUntilFinished / 1000));
            }

            public void onFinish() {
                dialog.dismiss();
            }
        };
        timer.start();
    }

    public void cancelDialog(){
        timer.cancel();
        dialog.dismiss();
    }
}