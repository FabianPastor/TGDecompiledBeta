package org.telegram.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.voip.VoIPHelper;

public class VoIPPermissionActivity extends Activity {
    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VoIPService service = VoIPService.getSharedInstance();
        boolean isVideoCall = (service == null || service.privateCall == null || !service.privateCall.video) ? false : true;
        ArrayList<String> permissions = new ArrayList<>();
        if (checkSelfPermission("android.permission.RECORD_AUDIO") != 0) {
            permissions.add("android.permission.RECORD_AUDIO");
        }
        if (isVideoCall && checkSelfPermission("android.permission.CAMERA") != 0) {
            permissions.add("android.permission.CAMERA");
        }
        if (!permissions.isEmpty()) {
            try {
                requestPermissions((String[]) permissions.toArray(new String[0]), isVideoCall ? 102 : 101);
            } catch (Exception e) {
                FileLog.e((Throwable) e);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 101 || requestCode == 102) {
            boolean allGranted = true;
            int a = 0;
            while (true) {
                if (a >= grantResults.length) {
                    break;
                } else if (grantResults[a] != 0) {
                    allGranted = false;
                    break;
                } else {
                    a++;
                }
            }
            if (grantResults.length > 0 && allGranted) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().acceptIncomingCall();
                }
                finish();
                startActivity(new Intent(this, LaunchActivity.class).setAction("voip"));
            } else if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().declineIncomingCall();
                }
                VoIPHelper.permissionDenied(this, new VoIPPermissionActivity$$ExternalSyntheticLambda0(this), requestCode);
            } else {
                finish();
            }
        }
    }
}
