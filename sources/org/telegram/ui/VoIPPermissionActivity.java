package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPPermissionActivity extends Activity {

    /* renamed from: org.telegram.ui.VoIPPermissionActivity$1 */
    class C17691 implements Runnable {
        C17691() {
        }

        public void run() {
            VoIPPermissionActivity.this.finish();
        }
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 101) {
            if (iArr.length > 0 && iArr[0] == 0) {
                if (VoIPService.getSharedInstance() != 0) {
                    VoIPService.getSharedInstance().acceptIncomingCall();
                }
                finish();
                startActivity(new Intent(this, VoIPActivity.class));
            } else if (shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO") == 0) {
                if (VoIPService.getSharedInstance() != 0) {
                    VoIPService.getSharedInstance().declineIncomingCall();
                }
                VoIPHelper.permissionDenied(this, new C17691());
            } else {
                finish();
            }
        }
    }
}
