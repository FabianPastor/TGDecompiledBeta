package org.telegram.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ShareAlert;

public class ShareActivity extends Activity {
    private Dialog visibleDialog;

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        ApplicationLoader.postInitApplication();
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        requestWindowFeature(1);
        setTheme(NUM);
        super.onCreate(savedInstanceState);
        setContentView(new View(this), new ViewGroup.LayoutParams(-1, -1));
        Intent intent = getIntent();
        if (intent == null || !"android.intent.action.VIEW".equals(intent.getAction()) || intent.getData() == null) {
            finish();
            return;
        }
        Uri data = intent.getData();
        String scheme = data.getScheme();
        String url = data.toString();
        String hash = data.getQueryParameter("hash");
        if (!"tgb".equals(scheme) || !url.toLowerCase().startsWith("tgb://share_game_score") || TextUtils.isEmpty(hash)) {
            finish();
            return;
        }
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
        String message = sharedPreferences.getString(hash + "_m", (String) null);
        if (TextUtils.isEmpty(message)) {
            finish();
            return;
        }
        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(message));
        TLRPC.Message mess = TLRPC.Message.TLdeserialize(serializedData, serializedData.readInt32(false), false);
        if (mess == null) {
            finish();
            return;
        }
        mess.readAttachPath(serializedData, 0);
        serializedData.cleanup();
        String link = sharedPreferences.getString(hash + "_link", (String) null);
        MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, mess, false, true);
        messageObject.messageOwner.with_my_score = true;
        MessageObject messageObject2 = messageObject;
        TLRPC.Message message2 = mess;
        try {
            ShareAlert createShareAlert = ShareAlert.createShareAlert(this, messageObject, (String) null, false, link, false);
            this.visibleDialog = createShareAlert;
            createShareAlert.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new ShareActivity$$ExternalSyntheticLambda0(this));
            this.visibleDialog.show();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            finish();
        }
    }

    /* renamed from: lambda$onCreate$0$org-telegram-ui-ShareActivity  reason: not valid java name */
    public /* synthetic */ void m3871lambda$onCreate$0$orgtelegramuiShareActivity(DialogInterface dialog) {
        if (!isFinishing()) {
            finish();
        }
        this.visibleDialog = null;
    }

    public void onPause() {
        super.onPause();
        try {
            Dialog dialog = this.visibleDialog;
            if (dialog != null && dialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }
}
