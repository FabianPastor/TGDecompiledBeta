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
    public static final int REQUEST_CODE_ATTACH_CONTACT = 5;
    public static final int REQUEST_CODE_CALLS = 7;
    public static final int REQUEST_CODE_EXTERNAL_STORAGE = 4;
    public static final int REQUEST_CODE_EXTERNAL_STORAGE_FOR_AVATAR = 151;
    public static final int REQUEST_CODE_GEOLOCATION = 2;
    public static final int REQUEST_CODE_OPEN_CAMERA = 20;
    public static final int REQUEST_CODE_VIDEO_MESSAGE = 150;
    protected int currentAccount = -1;

    /* access modifiers changed from: protected */
    public boolean checkPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int[] grantResults2;
        String[] permissions2;
        String str;
        int i = requestCode;
        if (grantResults == null) {
            grantResults2 = new int[0];
        } else {
            grantResults2 = grantResults;
        }
        if (permissions == null) {
            permissions2 = new String[0];
        } else {
            permissions2 = permissions;
        }
        boolean granted = grantResults2.length > 0 && grantResults2[0] == 0;
        if (i == 104) {
            if (!granted) {
                showPermissionErrorAlert(NUM, LocaleController.getString("VoipNeedCameraPermission", NUM));
                return true;
            } else if (GroupCallActivity.groupCallInstance == null) {
                return true;
            } else {
                GroupCallActivity.groupCallInstance.enableCamera();
                return true;
            }
        } else if (i == 4 || i == 151) {
            if (!granted) {
                if (i == 151) {
                    str = LocaleController.getString("PermissionNoStorageAvatar", NUM);
                } else {
                    str = LocaleController.getString("PermissionStorageWithHint", NUM);
                }
                showPermissionErrorAlert(NUM, str);
                return true;
            }
            ImageLoader.getInstance().checkMediaPaths();
            return true;
        } else if (i == 5) {
            if (!granted) {
                showPermissionErrorAlert(NUM, LocaleController.getString("PermissionNoContactsSharing", NUM));
                return false;
            }
            ContactsController.getInstance(this.currentAccount).forceImportContacts();
            return true;
        } else if (i == 3 || i == 150) {
            boolean audioGranted = true;
            boolean cameraGranted = true;
            int size = Math.min(permissions2.length, grantResults2.length);
            for (int i2 = 0; i2 < size; i2++) {
                if ("android.permission.RECORD_AUDIO".equals(permissions2[i2])) {
                    audioGranted = grantResults2[i2] == 0;
                } else if ("android.permission.CAMERA".equals(permissions2[i2])) {
                    cameraGranted = grantResults2[i2] == 0;
                }
            }
            if (i == 150 && (!audioGranted || !cameraGranted)) {
                showPermissionErrorAlert(NUM, LocaleController.getString("PermissionNoCameraMicVideo", NUM));
                return true;
            } else if (!audioGranted) {
                showPermissionErrorAlert(NUM, LocaleController.getString("PermissionNoAudioWithHint", NUM));
                return true;
            } else if (!cameraGranted) {
                showPermissionErrorAlert(NUM, LocaleController.getString("PermissionNoCameraWithHint", NUM));
                return true;
            } else {
                if (SharedConfig.inappCamera) {
                    CameraController.getInstance().initCamera((Runnable) null);
                }
                return false;
            }
        } else if (i == 18 || i == 19 || i == 20 || i == 22) {
            if (granted) {
                return true;
            }
            showPermissionErrorAlert(NUM, LocaleController.getString("PermissionNoCameraWithHint", NUM));
            return true;
        } else if (i != 2) {
            return true;
        } else {
            NotificationCenter.getGlobalInstance().postNotificationName(granted ? NotificationCenter.locationPermissionGranted : NotificationCenter.locationPermissionDenied, new Object[0]);
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public AlertDialog createPermissionErrorAlert(int animationId, String message) {
        return new AlertDialog.Builder((Context) this).setTopAnimation(animationId, 72, false, Theme.getColor("dialogTopBackground")).setMessage(AndroidUtilities.replaceTags(message)).setPositiveButton(LocaleController.getString("PermissionOpenSettings", NUM), new BasePermissionsActivity$$ExternalSyntheticLambda0(this)).setNegativeButton(LocaleController.getString("ContactsPermissionAlertNotNow", NUM), (DialogInterface.OnClickListener) null).create();
    }

    /* renamed from: lambda$createPermissionErrorAlert$0$org-telegram-ui-BasePermissionsActivity  reason: not valid java name */
    public /* synthetic */ void m2720xa1ae6110(DialogInterface dialogInterface, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e((Throwable) e);
        }
    }

    private void showPermissionErrorAlert(int animationId, String message) {
        createPermissionErrorAlert(animationId, message).show();
    }
}
