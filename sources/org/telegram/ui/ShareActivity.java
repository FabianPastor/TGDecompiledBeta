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
import android.view.ViewGroup.LayoutParams;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLRPC.Message;
import org.telegram.ui.Components.ShareAlert;

public class ShareActivity extends Activity {
    private Dialog visibleDialog;

    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        ApplicationLoader.postInitApplication();
        AndroidUtilities.checkDisplaySize(this, getResources().getConfiguration());
        requestWindowFeature(1);
        setTheme(NUM);
        super.onCreate(bundle);
        setContentView(new View(this), new LayoutParams(-1, -1));
        Intent intent = getIntent();
        if (intent != null) {
            if ("android.intent.action.VIEW".equals(intent.getAction()) && intent.getData() != null) {
                Uri data = intent.getData();
                String scheme = data.getScheme();
                String uri = data.toString();
                String queryParameter = data.getQueryParameter("hash");
                if ("tgb".equals(scheme) && uri.toLowerCase().startsWith("tgb://share_game_score") && !TextUtils.isEmpty(queryParameter)) {
                    SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("botshare", 0);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(queryParameter);
                    stringBuilder.append("_m");
                    String string = sharedPreferences.getString(stringBuilder.toString(), null);
                    if (TextUtils.isEmpty(string)) {
                        finish();
                        return;
                    }
                    SerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
                    Message TLdeserialize = Message.TLdeserialize(serializedData, serializedData.readInt32(false), false);
                    TLdeserialize.readAttachPath(serializedData, 0);
                    serializedData.cleanup();
                    if (TLdeserialize == null) {
                        finish();
                        return;
                    }
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append(queryParameter);
                    stringBuilder2.append("_link");
                    String string2 = sharedPreferences.getString(stringBuilder2.toString(), null);
                    MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, TLdeserialize, false);
                    messageObject.messageOwner.with_my_score = true;
                    try {
                        this.visibleDialog = ShareAlert.createShareAlert(this, messageObject, null, false, string2, false);
                        this.visibleDialog.setCanceledOnTouchOutside(true);
                        this.visibleDialog.setOnDismissListener(new -$$Lambda$ShareActivity$8CDJt1az5uGqAsSjal6N7RJDepQ(this));
                        this.visibleDialog.show();
                    } catch (Exception e) {
                        FileLog.e(e);
                        finish();
                    }
                    return;
                }
                finish();
                return;
            }
        }
        finish();
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
            FileLog.e(e);
        }
    }
}
