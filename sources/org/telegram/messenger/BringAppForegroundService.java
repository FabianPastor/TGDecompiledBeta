package org.telegram.messenger;

import android.app.IntentService;
import android.content.Intent;
import com.google.android.exoplayer2.C0012C;
import org.telegram.ui.LaunchActivity;

public class BringAppForegroundService extends IntentService {
    public BringAppForegroundService() {
        super("BringAppForegroundService");
    }

    protected void onHandleIntent(Intent intent) {
        Intent intent2 = new Intent(this, LaunchActivity.class);
        intent2.setFlags(C0012C.ENCODING_PCM_MU_LAW);
        startActivity(intent2);
    }
}
