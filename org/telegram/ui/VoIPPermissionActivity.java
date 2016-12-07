package org.telegram.ui;

import android.app.Activity;
import android.os.Bundle;
import org.telegram.messenger.voip.VoIPService;

public class VoIPPermissionActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101 && grantResults[0] == 0 && VoIPService.getSharedInstance() != null) {
            VoIPService.getSharedInstance().acceptIncomingCall();
        }
        finish();
    }
}
