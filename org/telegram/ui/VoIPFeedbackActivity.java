package org.telegram.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_inputPhoneCall;
import org.telegram.tgnet.TLRPC.TL_phone_setCallRating;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.AlertDialog.Builder;
import org.telegram.ui.Components.BetterRatingView;
import org.telegram.ui.Components.BetterRatingView.OnRatingChangeListener;
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
        final BetterRatingView bar = new BetterRatingView(this);
        ll.addView(bar, LayoutHelper.createLinear(-2, -2, 1, 0, 16, 0, 0));
        final EditText commentBox = new EditText(this);
        commentBox.setHint(LocaleController.getString("VoipFeedbackCommentHint", R.string.VoipFeedbackCommentHint));
        commentBox.setInputType(147457);
        commentBox.setVisibility(8);
        ll.addView(commentBox, LayoutHelper.createLinear(-1, -2, 1, 0, 16, 0, 0));
        AlertDialog alert = new Builder(this).setTitle(LocaleController.getString("AppName", R.string.AppName)).setView(ll).setPositiveButton(LocaleController.getString("OK", R.string.OK), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                TL_phone_setCallRating req = new TL_phone_setCallRating();
                req.rating = bar.getRating();
                if (req.rating < 5) {
                    req.comment = commentBox.getText().toString();
                }
                req.peer = new TL_inputPhoneCall();
                req.peer.access_hash = VoIPFeedbackActivity.this.getIntent().getLongExtra("call_access_hash", 0);
                req.peer.id = VoIPFeedbackActivity.this.getIntent().getLongExtra("call_id", 0);
                ConnectionsManager.getInstance().sendRequest(req, new RequestDelegate() {
                    public void run(TLObject response, TL_error error) {
                        if (response instanceof TL_updates) {
                            MessagesController.getInstance().processUpdates((TL_updates) response, false);
                        }
                    }
                });
                VoIPFeedbackActivity.this.finish();
            }
        }).setNegativeButton(LocaleController.getString("Cancel", R.string.Cancel), new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                VoIPFeedbackActivity.this.finish();
            }
        }).show();
        alert.setCanceledOnTouchOutside(true);
        alert.setOnCancelListener(new OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                VoIPFeedbackActivity.this.finish();
            }
        });
        final View btn = alert.getButton(-1);
        btn.setEnabled(false);
        bar.setOnRatingChangeListener(new OnRatingChangeListener() {
            public void onRatingChanged(int rating) {
                int i;
                btn.setEnabled(rating > 0);
                EditText editText = commentBox;
                if (rating >= 5 || rating <= 0) {
                    i = 8;
                } else {
                    i = 0;
                }
                editText.setVisibility(i);
                if (commentBox.getVisibility() == 8) {
                    ((InputMethodManager) VoIPFeedbackActivity.this.getSystemService("input_method")).hideSoftInputFromWindow(commentBox.getWindowToken(), 0);
                }
            }
        });
    }

    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
