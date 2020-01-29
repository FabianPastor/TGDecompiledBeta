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
    public void onCreate(Bundle bundle) {
        ApplicationLoader.postInitApplication();
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        requestWindowFeature(1);
        setTheme(NUM);
        super.onCreate(bundle);
        setContentView(new View(this), new ViewGroup.LayoutParams(-1, -1));
        Intent intent = getIntent();
        if (intent == null || !"android.intent.action.VIEW".equals(intent.getAction()) || intent.getData() == null) {
            finish();
            return;
        }
        Uri data = intent.getData();
        String scheme = data.getScheme();
        String uri = data.toString();
        String queryParameter = data.getQueryParameter("hash");
        if (!"tgb".equals(scheme) || !uri.toLowerCase().startsWith("tgb://share_game_score") || TextUtils.isEmpty(queryParameter)) {
            finish();
            return;
        }
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
        String string = sharedPreferences.getString(queryParameter + "_m", (String) null);
        if (TextUtils.isEmpty(string)) {
            finish();
            return;
        }
        SerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
        TLRPC.Message TLdeserialize = TLRPC.Message.TLdeserialize(serializedData, serializedData.readInt32(false), false);
        TLdeserialize.readAttachPath(serializedData, 0);
        serializedData.cleanup();
        if (TLdeserialize == null) {
            finish();
            return;
        }
        String string2 = sharedPreferences.getString(queryParameter + "_link", (String) null);
        MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, TLdeserialize, false);
        messageObject.messageOwner.with_my_score = true;
        try {
            this.visibleDialog = ShareAlert.createShareAlert(this, messageObject, (String) null, false, string2, false);
            this.visibleDialog.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                public final void onDismiss(DialogInterface dialogInterface) {
                    ShareActivity.this.lambda$onCreate$0$ShareActivity(dialogInterface);
                }
            });
            this.visibleDialog.show();
        } catch (Exception e) {
            FileLog.e((Throwable) e);
            finish();
        }
    }

    public /* synthetic */ void lambda$onCreate$0$ShareActivity(DialogInterface dialogInterface) {
        if (!isFinishing()) {
            finish();
        }
        this.visibleDialog = null;
    }

    public void onPause() {
        super.onPause();
        try {
            if (this.visibleDialog != null && this.visibleDialog.isShowing()) {
                this.visibleDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }
}
