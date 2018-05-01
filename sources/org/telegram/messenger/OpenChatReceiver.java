package org.telegram.messenger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.telegram.ui.LaunchActivity;

public class OpenChatReceiver extends Activity {
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        bundle = getIntent();
        if (bundle == null) {
            finish();
        }
        if (bundle.getAction() != null) {
            if (bundle.getAction().startsWith("com.tmessages.openchat")) {
                Intent intent = new Intent(this, LaunchActivity.class);
                intent.setAction(bundle.getAction());
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
                return;
            }
        }
        finish();
    }
}
