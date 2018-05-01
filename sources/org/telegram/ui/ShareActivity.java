package org.telegram.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.Components.ShareAlert;

public class ShareActivity extends Activity {
    private Dialog visibleDialog;

    /* renamed from: org.telegram.ui.ShareActivity$1 */
    class C17011 implements OnDismissListener {
        C17011() {
        }

        public void onDismiss(DialogInterface dialogInterface) {
            if (ShareActivity.this.isFinishing() == null) {
                ShareActivity.this.finish();
            }
            ShareActivity.this.visibleDialog = null;
        }
    }

    protected void onCreate(Bundle bundle) {
        ApplicationLoader.postInitApplication();
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        requestWindowFeature(1);
        setTheme(C0446R.style.Theme.TMessages.Transparent);
        super.onCreate(bundle);
        setContentView(new View(this), new LayoutParams(-1, -1));
        bundle = getIntent();
        if (bundle != null && "android.intent.action.VIEW".equals(bundle.getAction())) {
            if (bundle.getData() != null) {
                bundle = bundle.getData();
                String scheme = bundle.getScheme();
                String uri = bundle.toString();
                bundle = bundle.getQueryParameter("hash");
                if ("tgb".equals(scheme) && uri.toLowerCase().startsWith("tgb://share_game_score")) {
                    if (!TextUtils.isEmpty(bundle)) {
                        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(bundle);
                        stringBuilder.append("_m");
                        Object string = sharedPreferences.getString(stringBuilder.toString(), null);
                        if (TextUtils.isEmpty(string)) {
                            finish();
                            return;
                        }
                        AbstractSerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
                        Message TLdeserialize = Message.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                        TLdeserialize.readAttachPath(serializedData, 0);
                        if (TLdeserialize == null) {
                            finish();
                            return;
                        }
                        StringBuilder stringBuilder2 = new StringBuilder();
                        stringBuilder2.append(bundle);
                        stringBuilder2.append("_link");
                        String string2 = sharedPreferences.getString(stringBuilder2.toString(), null);
                        MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, TLdeserialize, false);
                        messageObject.messageOwner.with_my_score = true;
                        try {
                            this.visibleDialog = ShareAlert.createShareAlert(this, messageObject, null, false, string2, false);
                            this.visibleDialog.setCanceledOnTouchOutside(true);
                            this.visibleDialog.setOnDismissListener(new C17011());
                            this.visibleDialog.show();
                        } catch (Throwable e) {
                            FileLog.m3e(e);
                            finish();
                        }
                        return;
                    }
                }
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
            FileLog.m3e(e);
        }
    }
}
