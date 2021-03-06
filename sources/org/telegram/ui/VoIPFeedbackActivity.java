package org.telegram.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPFeedbackActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        getWindow().addFlags(524288);
        super.onCreate(bundle);
        overridePendingTransition(0, 0);
        setContentView(new View(this));
        VoIPHelper.showRateAlert(this, new Runnable() {
            public final void run() {
                VoIPFeedbackActivity.this.finish();
            }
        }, getIntent().getBooleanExtra("call_video", false), getIntent().getLongExtra("call_id", 0), getIntent().getLongExtra("call_access_hash", 0), getIntent().getIntExtra("account", 0), false);
    }

    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}
