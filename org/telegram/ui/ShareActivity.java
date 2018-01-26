package org.telegram.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.Components.ShareAlert;

public class ShareActivity extends Activity {
    private Dialog visibleDialog;

    protected void onCreate(Bundle savedInstanceState) {
        ApplicationLoader.postInitApplication();
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        requestWindowFeature(1);
        setTheme(R.style.Theme.TMessages.Transparent);
        super.onCreate(savedInstanceState);
        setContentView(new View(this), new LayoutParams(-1, -1));
        Intent intent = getIntent();
        if (intent == null || !"android.intent.action.VIEW".equals(intent.getAction()) || intent.getData() == null) {
            finish();
            return;
        }
        Uri data = intent.getData();
        String scheme = data.getScheme();
        String url = data.toString();
        String hash = data.getQueryParameter("hash");
        if ("tgb".equals(scheme) && url.toLowerCase().startsWith("tgb://share_game_score") && !TextUtils.isEmpty(hash)) {
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
            String message = sharedPreferences.getString(hash + "_m", null);
            if (TextUtils.isEmpty(message)) {
                finish();
                return;
            }
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(message));
            Message mess = Message.TLdeserialize(serializedData, serializedData.readInt32(false), false);
            mess.readAttachPath(serializedData);
            if (mess == null) {
                finish();
                return;
            }
            String link = sharedPreferences.getString(hash + "_link", null);
            MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, mess, false);
            messageObject.messageOwner.with_my_score = true;
            try {
                this.visibleDialog = ShareAlert.createShareAlert(this, messageObject, null, false, link, false);
                this.visibleDialog.setCanceledOnTouchOutside(true);
                this.visibleDialog.setOnDismissListener(new OnDismissListener() {
                    public void onDismiss(DialogInterface dialog) {
                        if (!ShareActivity.this.isFinishing()) {
                            ShareActivity.this.finish();
                        }
                        ShareActivity.this.visibleDialog = null;
                    }
                });
                this.visibleDialog.show();
                return;
            } catch (Throwable e) {
                FileLog.e(e);
                finish();
                return;
            }
        }
        finish();
    }

    public void onPause() {
        super.onPause();
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Throwable e) {
            FileLog.e(e);
        }
    }
}
