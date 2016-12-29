package org.telegram.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.Components.LayoutHelper;

public class VoIPFeedbackActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().addFlags(524288);
        super.onCreate(savedInstanceState);
        overridePendingTransition(0, 0);
        setContentView(new View(this));
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(1);
        int pad = AndroidUtilities.dp(16.0f);
        ll.setPadding(pad, pad, pad, pad);
        TextView text = new TextView(this);
        text.setTextSize(2, 16.0f);
        text.setTextColor(-16777216);
        text.setGravity(17);
        text.setText(LocaleController.getString("VoipRateCallAlert", R.string.VoipRateCallAlert));
        ll.addView(text);
        RatingBar bar = new RatingBar(this);
        bar.setNumStars(5);
        bar.setIsIndicator(false);
        bar.setStepSize(1.0f);
        ll.addView(bar, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        AlertDialog alert = new Builder(this).setTitle(LocaleController.getString("AppName", R.string.AppName)).setView(ll).setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                VoIPFeedbackActivity.this.suggestMoreFeedback();
            }
        }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                VoIPFeedbackActivity.this.finish();
            }
        }).show();
        alert.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                VoIPFeedbackActivity.this.finish();
            }
        });
        final Button btn = alert.getButton(-1);
        btn.setEnabled(false);
        bar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                btn.setEnabled(rating > 0.0f);
            }
        });
    }

    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void suggestMoreFeedback() {
        new Builder(this).setTitle(LocaleController.getString("AppName", R.string.AppName)).setMessage(LocaleController.getString("VoipFeedbackAlert", R.string.VoipFeedbackAlert)).setPositiveButton(LocaleController.getString("Yes", R.string.Yes), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                User user = new User();
                user.id = 4244000;
                user.flags = 2;
                user.first_name = "VoIP Support";
                MessagesController.getInstance().putUser(user, true);
                Intent intent = new Intent(ApplicationLoader.applicationContext, LaunchActivity.class);
                intent.setAction("com.tmessages.openchat" + Math.random() + ConnectionsManager.DEFAULT_DATACENTER_ID);
                intent.setFlags(32768);
                intent.putExtra("userId", 4244000);
                VoIPFeedbackActivity.this.startActivity(intent);
            }
        }).setNegativeButton(LocaleController.getString("No", R.string.No), null).show().setOnDismissListener(new OnDismissListener() {
            public void onDismiss(DialogInterface dialog) {
                VoIPFeedbackActivity.this.finish();
            }
        });
    }
}
