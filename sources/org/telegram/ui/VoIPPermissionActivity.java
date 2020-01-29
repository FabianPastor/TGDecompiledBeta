package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPPermissionActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        requestPermissions(new String[]{"android.permission.RECORD_AUDIO"}, 101);
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i != 101) {
            return;
        }
        if (iArr.length > 0 && iArr[0] == 0) {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().acceptIncomingCall();
            }
            finish();
            startActivity(new Intent(this, VoIPActivity.class));
        } else if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
            if (VoIPService.getSharedInstance() != null) {
                VoIPService.getSharedInstance().declineIncomingCall();
            }
            VoIPHelper.permissionDenied(this, new Runnable() {
                public void run() {
                    VoIPPermissionActivity.this.finish();
                }
            });
        } else {
            finish();
        }
    }
}
