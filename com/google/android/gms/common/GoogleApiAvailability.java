package com.google.android.gms.common;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.Notification.BigTextStyle;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import com.google.android.gms.R;
import com.google.android.gms.common.api.GoogleApiActivity;
import com.google.android.gms.common.api.internal.zzbx;
import com.google.android.gms.common.api.internal.zzby;
import com.google.android.gms.common.api.internal.zzcf;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.common.internal.zzu;
import com.google.android.gms.common.internal.zzv;
import com.google.android.gms.common.util.zzi;
import com.google.android.gms.common.util.zzq;

public class GoogleApiAvailability extends zzf {
    public static final int GOOGLE_PLAY_SERVICES_VERSION_CODE = zzf.GOOGLE_PLAY_SERVICES_VERSION_CODE;
    private static final Object mLock = new Object();
    private static final GoogleApiAvailability zzfku = new GoogleApiAvailability();
    private String zzfkv;

    @SuppressLint({"HandlerLeak"})
    class zza extends Handler {
        private final Context mApplicationContext;
        private /* synthetic */ GoogleApiAvailability zzfkw;

        public zza(GoogleApiAvailability googleApiAvailability, Context context) {
            this.zzfkw = googleApiAvailability;
            super(Looper.myLooper() == null ? Looper.getMainLooper() : Looper.myLooper());
            this.mApplicationContext = context.getApplicationContext();
        }

        public final void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    int isGooglePlayServicesAvailable = this.zzfkw.isGooglePlayServicesAvailable(this.mApplicationContext);
                    if (this.zzfkw.isUserResolvableError(isGooglePlayServicesAvailable)) {
                        this.zzfkw.showErrorNotification(this.mApplicationContext, isGooglePlayServicesAvailable);
                        return;
                    }
                    return;
                default:
                    Log.w("GoogleApiAvailability", "Don't know how to handle this message: " + message.what);
                    return;
            }
        }
    }

    GoogleApiAvailability() {
    }

    public static GoogleApiAvailability getInstance() {
        return zzfku;
    }

    public static Dialog zza(Activity activity, OnCancelListener onCancelListener) {
        View progressBar = new ProgressBar(activity, null, 16842874);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(0);
        Builder builder = new Builder(activity);
        builder.setView(progressBar);
        builder.setMessage(zzu.zzi(activity, 18));
        builder.setPositiveButton(TtmlNode.ANONYMOUS_REGION_ID, null);
        Dialog create = builder.create();
        zza(activity, create, "GooglePlayServicesUpdatingDialog", onCancelListener);
        return create;
    }

    static Dialog zza(Context context, int i, zzv com_google_android_gms_common_internal_zzv, OnCancelListener onCancelListener) {
        Builder builder = null;
        if (i == 0) {
            return null;
        }
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(16843529, typedValue, true);
        if ("Theme.Dialog.Alert".equals(context.getResources().getResourceEntryName(typedValue.resourceId))) {
            builder = new Builder(context, 5);
        }
        if (builder == null) {
            builder = new Builder(context);
        }
        builder.setMessage(zzu.zzi(context, i));
        if (onCancelListener != null) {
            builder.setOnCancelListener(onCancelListener);
        }
        CharSequence zzk = zzu.zzk(context, i);
        if (zzk != null) {
            builder.setPositiveButton(zzk, com_google_android_gms_common_internal_zzv);
        }
        zzk = zzu.zzg(context, i);
        if (zzk != null) {
            builder.setTitle(zzk);
        }
        return builder.create();
    }

    public static zzbx zza(Context context, zzby com_google_android_gms_common_api_internal_zzby) {
        IntentFilter intentFilter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
        intentFilter.addDataScheme("package");
        BroadcastReceiver com_google_android_gms_common_api_internal_zzbx = new zzbx(com_google_android_gms_common_api_internal_zzby);
        context.registerReceiver(com_google_android_gms_common_api_internal_zzbx, intentFilter);
        com_google_android_gms_common_api_internal_zzbx.setContext(context);
        if (zzp.zzv(context, "com.google.android.gms")) {
            return com_google_android_gms_common_api_internal_zzbx;
        }
        com_google_android_gms_common_api_internal_zzby.zzahg();
        com_google_android_gms_common_api_internal_zzbx.unregister();
        return null;
    }

    @TargetApi(26)
    private final String zza(Context context, NotificationManager notificationManager) {
        zzbq.checkState(zzq.isAtLeastO());
        String zzafx = zzafx();
        if (zzafx == null) {
            zzafx = "com.google.android.gms.availability";
            NotificationChannel notificationChannel = notificationManager.getNotificationChannel(zzafx);
            CharSequence zzcn = zzu.zzcn(context);
            if (notificationChannel == null) {
                notificationManager.createNotificationChannel(new NotificationChannel(zzafx, zzcn, 4));
            } else if (!zzcn.equals(notificationChannel.getName())) {
                notificationChannel.setName(zzcn);
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        return zzafx;
    }

    static void zza(Activity activity, Dialog dialog, String str, OnCancelListener onCancelListener) {
        if (activity instanceof FragmentActivity) {
            SupportErrorDialogFragment.newInstance(dialog, onCancelListener).show(((FragmentActivity) activity).getSupportFragmentManager(), str);
            return;
        }
        ErrorDialogFragment.newInstance(dialog, onCancelListener).show(activity.getFragmentManager(), str);
    }

    @TargetApi(20)
    private final void zza(Context context, int i, String str, PendingIntent pendingIntent) {
        if (i == 18) {
            zzcc(context);
        } else if (pendingIntent != null) {
            Notification build;
            int i2;
            CharSequence zzh = zzu.zzh(context, i);
            CharSequence zzj = zzu.zzj(context, i);
            Resources resources = context.getResources();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            if (zzi.zzct(context)) {
                zzbq.checkState(zzq.zzamm());
                Notification.Builder addAction = new Notification.Builder(context).setSmallIcon(context.getApplicationInfo().icon).setPriority(2).setAutoCancel(true).setContentTitle(zzh).setStyle(new BigTextStyle().bigText(zzj)).addAction(R.drawable.common_full_open_on_phone, resources.getString(R.string.common_open_on_phone), pendingIntent);
                if (zzq.isAtLeastO() && zzq.isAtLeastO()) {
                    addAction.setChannelId(zza(context, notificationManager));
                }
                build = addAction.build();
            } else {
                NotificationCompat.Builder style = new NotificationCompat.Builder(context).setSmallIcon(17301642).setTicker(resources.getString(R.string.common_google_play_services_notification_ticker)).setWhen(System.currentTimeMillis()).setAutoCancel(true).setContentIntent(pendingIntent).setContentTitle(zzh).setContentText(zzj).setLocalOnly(true).setStyle(new NotificationCompat.BigTextStyle().bigText(zzj));
                if (zzq.isAtLeastO() && zzq.isAtLeastO()) {
                    style.setChannelId(zza(context, notificationManager));
                }
                build = style.build();
            }
            switch (i) {
                case 1:
                case 2:
                case 3:
                    i2 = 10436;
                    zzp.zzfln.set(false);
                    break;
                default:
                    i2 = 39789;
                    break;
            }
            notificationManager.notify(i2, build);
        } else if (i == 6) {
            Log.w("GoogleApiAvailability", "Missing resolution for ConnectionResult.RESOLUTION_REQUIRED. Call GoogleApiAvailability#showErrorNotification(Context, ConnectionResult) instead.");
        }
    }

    private final String zzafx() {
        String str;
        synchronized (mLock) {
            str = this.zzfkv;
        }
        return str;
    }

    public Dialog getErrorDialog(Activity activity, int i, int i2, OnCancelListener onCancelListener) {
        return zza((Context) activity, i, zzv.zza(activity, zzf.zza(activity, i, "d"), i2), onCancelListener);
    }

    public PendingIntent getErrorResolutionPendingIntent(Context context, int i, int i2) {
        return super.getErrorResolutionPendingIntent(context, i, i2);
    }

    public PendingIntent getErrorResolutionPendingIntent(Context context, ConnectionResult connectionResult) {
        return connectionResult.hasResolution() ? connectionResult.getResolution() : getErrorResolutionPendingIntent(context, connectionResult.getErrorCode(), 0);
    }

    public final String getErrorString(int i) {
        return super.getErrorString(i);
    }

    public int isGooglePlayServicesAvailable(Context context) {
        return super.isGooglePlayServicesAvailable(context);
    }

    public final boolean isUserResolvableError(int i) {
        return super.isUserResolvableError(i);
    }

    public boolean showErrorDialogFragment(Activity activity, int i, int i2, OnCancelListener onCancelListener) {
        Dialog errorDialog = getErrorDialog(activity, i, i2, onCancelListener);
        if (errorDialog == null) {
            return false;
        }
        zza(activity, errorDialog, "GooglePlayServicesErrorDialog", onCancelListener);
        return true;
    }

    public void showErrorNotification(Context context, int i) {
        zza(context, i, null, zza(context, i, 0, "n"));
    }

    public final boolean zza(Activity activity, zzcf com_google_android_gms_common_api_internal_zzcf, int i, int i2, OnCancelListener onCancelListener) {
        Dialog zza = zza((Context) activity, i, zzv.zza(com_google_android_gms_common_api_internal_zzcf, zzf.zza(activity, i, "d"), 2), onCancelListener);
        if (zza == null) {
            return false;
        }
        zza(activity, zza, "GooglePlayServicesErrorDialog", onCancelListener);
        return true;
    }

    public final boolean zza(Context context, ConnectionResult connectionResult, int i) {
        PendingIntent errorResolutionPendingIntent = getErrorResolutionPendingIntent(context, connectionResult);
        if (errorResolutionPendingIntent == null) {
            return false;
        }
        zza(context, connectionResult.getErrorCode(), null, GoogleApiActivity.zza(context, errorResolutionPendingIntent, i));
        return true;
    }

    final void zzcc(Context context) {
        new zza(this, context).sendEmptyMessageDelayed(1, 120000);
    }
}
