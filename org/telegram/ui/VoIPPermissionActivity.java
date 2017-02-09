package org.telegram.ui;

import android.app.Activity;
import android.os.Bundle;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPPermissionActivity extends Activity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 101) {
            return;
        }
        if (grantResults[0] == 0) {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().acceptIncomingCall();
            }
            finish();
        } else if (shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            finish();
        } else {
            VoIPService.getSharedInstance().declineIncomingCall();
            VoIPHelper.permissionDenied(this, new Runnable() {
                public void run() {
                    VoIPPermissionActivity.this.finish();
                }
            });
        }
    }
}
