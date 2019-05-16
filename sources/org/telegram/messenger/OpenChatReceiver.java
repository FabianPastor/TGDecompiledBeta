package org.telegram.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.telegram.ui.LaunchActivity;

public class OpenChatReceiver extends Activity {
    /* Access modifiers changed, original: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Intent intent = getIntent();
        if (intent == null) {
            finish();
        }
        if (intent.getAction() == null || !intent.getAction().startsWith("com.tmessages.openchat")) {
            finish();
            return;
        }
        try {
            int intExtra = intent.getIntExtra("chatId", 0);
            int intExtra2 = intent.getIntExtra("userId", 0);
            int intExtra3 = intent.getIntExtra("encId", 0);
            if (intExtra != 0 || intExtra2 != 0 || intExtra3 != 0) {
                Intent intent2 = new Intent(this, LaunchActivity.class);
                intent2.setAction(intent.getAction());
                intent2.putExtras(intent);
                startActivity(intent2);
                finish();
            }
        } catch (Throwable unused) {
        }
    }
}
