package org.telegram.p005ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.p005ui.Components.voip.VoIPHelper;

/* renamed from: org.telegram.ui.VoIPPermissionActivity */
public class VoIPPermissionActivity extends Activity {

    /* renamed from: org.telegram.ui.VoIPPermissionActivity$1 */
    class C18071 implements Runnable {
        C18071() {
        }

        public void run() {
            VoIPPermissionActivity.this.finish();
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode != 101) {
            return;
        }
        if (grantResults.length > 0 && grantResults[0] == 0) {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().acceptIncomingCall();
            }
            finish();
            startActivity(new Intent(this, VoIPActivity.class));
        } else if (shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            finish();
        } else {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().declineIncomingCall();
            }
            VoIPHelper.permissionDenied(this, new C18071());
        }
    }
}
