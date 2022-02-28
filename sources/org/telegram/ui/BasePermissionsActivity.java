package org.telegram.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.camera.CameraController;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;

public class BasePermissionsActivity extends Activity {
    protected int currentAccount = -1;

    /* access modifiers changed from: protected */
    public boolean checkPermissionsResult(int i, String[] strArr, int[] iArr) {
        String str;
        if (iArr == null) {
            iArr = new int[0];
        }
        if (strArr == null) {
            strArr = new String[0];
        }
        boolean z = iArr.length > 0 && iArr[0] == 0;
        if (i == 104) {
            if (z) {
                GroupCallActivity groupCallActivity = GroupCallActivity.groupCallInstance;
                if (groupCallActivity != null) {
                    groupCallActivity.enableCamera();
                }
            } else {
                showPermissionErrorAlert(NUM, LocaleController.getString("VoipNeedCameraPermission", NUM));
            }
        } else if (i == 4 || i == 151) {
            if (!z) {
                if (i == 151) {
                    str = LocaleController.getString("PermissionNoStorageAvatar", NUM);
                } else {
                    str = LocaleController.getString("PermissionStorageWithHint", NUM);
                }
                showPermissionErrorAlert(NUM, str);
            } else {
                ImageLoader.getInstance().checkMediaPaths();
            }
        } else if (i == 5) {
            if (!z) {
                showPermissionErrorAlert(NUM, LocaleController.getString("PermissionNoContactsSharing", NUM));
                return false;
            }
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
        } else if (i == 3 || i == 150) {
            int min = Math.min(strArr.length, iArr.length);
            boolean z2 = true;
            boolean z3 = true;
            for (int i2 = 0; i2 < min; i2++) {
                if ("android.permission.RECORD_AUDIO".equals(strArr[i2])) {
                    z2 = iArr[i2] == 0;
                } else if ("android.permission.CAMERA".equals(strArr[i2])) {
                    z3 = iArr[i2] == 0;
                }
            }
            if (i == 150 && (!z2 || !z3)) {
                showPermissionErrorAlert(NUM, LocaleController.getString("PermissionNoCameraMicVideo", NUM));
            } else if (!z2) {
                showPermissionErrorAlert(NUM, LocaleController.getString("PermissionNoAudioWithHint", NUM));
            } else if (!z3) {
                showPermissionErrorAlert(NUM, LocaleController.getString("PermissionNoCameraWithHint", NUM));
            } else {
                if (SharedConfig.inappCamera) {
                    CameraController.getInstance().initCamera((Runnable) null);
                }
                return false;
            }
        } else if (i == 18 || i == 19 || i == 20 || i == 22) {
            if (!z) {
                showPermissionErrorAlert(NUM, LocaleController.getString("PermissionNoCameraWithHint", NUM));
            }
        } else if (i == 2) {
            NotificationCenter.getGlobalInstance().postNotificationName(z ? NotificationCenter.locationPermissionGranted : NotificationCenter.locationPermissionDenied, new Object[0]);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public AlertDialog createPermissionErrorAlert(int i, String str) {
        return new AlertDialog.Builder((Context) this).setTopAnimation(i, 72, false, Theme.getColor("dialogTopBackground")).setMessage(AndroidUtilities.replaceTags(str)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new BasePermissionsActivity$$ExternalSyntheticLambda0(this)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), (DialogInterface.OnClickListener) null).create();
    }

    /* access modifiers changed from: private */
    public /* synthetic */ void lambda$createPermissionErrorAlert$0(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void showPermissionErrorAlert(int i, String str) {
        createPermissionErrorAlert(i, str).show();
    }
}
