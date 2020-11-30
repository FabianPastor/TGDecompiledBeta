package org.telegram.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.ui.Components.voip.VoIPHelper;

@TargetApi(23)
public class VoIPPermissionActivity extends Activity {
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:2:0x000a, code lost:
        r5 = r5.privateCall;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onCreate(android.os.Bundle r5) {
        /*
            r4 = this;
            super.onCreate(r5)
            org.telegram.messenger.voip.VoIPService r5 = org.telegram.messenger.voip.VoIPService.getSharedInstance()
            r0 = 0
            if (r5 == 0) goto L_0x0014
            org.telegram.tgnet.TLRPC$PhoneCall r5 = r5.privateCall
            if (r5 == 0) goto L_0x0014
            boolean r5 = r5.video
            if (r5 == 0) goto L_0x0014
            r5 = 1
            goto L_0x0015
        L_0x0014:
            r5 = 0
        L_0x0015:
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.lang.String r2 = "android.permission.RECORD_AUDIO"
            int r3 = r4.checkSelfPermission(r2)
            if (r3 == 0) goto L_0x0025
            r1.add(r2)
        L_0x0025:
            if (r5 == 0) goto L_0x0032
            java.lang.String r2 = "android.permission.CAMERA"
            int r3 = r4.checkSelfPermission(r2)
            if (r3 == 0) goto L_0x0032
            r1.add(r2)
        L_0x0032:
            boolean r2 = r1.isEmpty()
            if (r2 == 0) goto L_0x004a
            java.lang.String[] r0 = new java.lang.String[r0]
            java.lang.Object[] r0 = r1.toArray(r0)
            java.lang.String[] r0 = (java.lang.String[]) r0
            if (r5 == 0) goto L_0x0045
            r5 = 102(0x66, float:1.43E-43)
            goto L_0x0047
        L_0x0045:
            r5 = 101(0x65, float:1.42E-43)
        L_0x0047:
            r4.requestPermissions(r0, r5)
        L_0x004a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.VoIPPermissionActivity.onCreate(android.os.Bundle):void");
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        if (i == 101 || i == 102) {
            boolean z = false;
            int i2 = 0;
            while (true) {
                if (i2 >= iArr.length) {
                    z = true;
                    break;
                } else if (iArr[i2] != 0) {
                    break;
                } else {
                    i2++;
                }
            }
            if (iArr.length > 0 && z) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().acceptIncomingCall();
                }
                finish();
                startActivity(new Intent(this, LaunchActivity.class).setAction("voip"));
            } else if (!shouldShowRequestPermissionRationale("android.permission.RECORD_AUDIO")) {
                if (VoIPService.getSharedInstance() != null) {
                    VoIPService.getSharedInstance().declineIncomingCall();
                }
                VoIPHelper.permissionDenied(this, new Runnable() {
                    public final void run() {
                        VoIPPermissionActivity.this.finish();
                    }
                }, i);
            } else {
                finish();
            }
        }
    }
}
