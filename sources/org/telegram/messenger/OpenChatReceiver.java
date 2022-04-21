package org.telegram.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.telegram.ui.LaunchActivity;

public class OpenChatReceiver extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        } else if (intent.getAction() == null || !intent.getAction().startsWith("com.tmessages.openchat")) {
            finish();
        } else {
            try {
                long chatId = intent.getLongExtra("chatId", (long) intent.getIntExtra("chatId", 0));
                long userId = intent.getLongExtra("userId", (long) intent.getIntExtra("userId", 0));
                int encId = intent.getIntExtra("encId", 0);
                if (chatId != 0 || userId != 0 || encId != 0) {
                    Intent intent2 = new Intent(this, LaunchActivity.class);
                    intent2.setAction(intent.getAction());
                    intent2.putExtras(intent);
                    startActivity(intent2);
                    finish();
                }
            } catch (Throwable e) {
                FileLog.e(e);
            }
        }
    }
}
