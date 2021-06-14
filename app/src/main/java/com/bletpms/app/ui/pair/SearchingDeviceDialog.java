package com.bletpms.app.ui.pair;

import android.app.Activity;
import android.app.Dialog;
import android.os.CountDownTimer;
import android.view.Window;
import android.widget.TextView;

import com.bletpms.app.R;

import java.util.Locale;

public class SearchingDeviceDialog {

    final Activity activity;
    Dialog dialog;

    private CountDownTimer timer;
    private static int SEARCHING_TIME_MS;

    public SearchingDeviceDialog(Activity activity, int searchingTime) {
        this.activity = activity;
        SEARCHING_TIME_MS = searchingTime;
    }

    public Dialog showDialog(){
        dialog  = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialog.getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);

        dialog.setContentView(R.layout.dialog_pair_device_auto_search);

        TextView countdownTextView = dialog.findViewById(R.id.countdownTextView);

        dialog.show();

        timer = new CountDownTimer(SEARCHING_TIME_MS, 1000) {

            public void onTick(long millisUntilFinished) {
                countdownTextView.setText(String.format(Locale.getDefault(), "%ds", millisUntilFinished / 1000));
            }

            public void onFinish() {
            }
        };
        timer.start();

        return dialog;
    }

    public void cancelDialog(){
        timer.cancel();
        dialog.dismiss();
    }
}
