package com.google.android.gms.gcm;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.iid.zze;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.util.List;

public class GcmNetworkManager {
    public static final int RESULT_FAILURE = 2;
    public static final int RESULT_RESCHEDULE = 1;
    public static final int RESULT_SUCCESS = 0;
    private static GcmNetworkManager zzbfv;
    private Context mContext;
    private final PendingIntent mPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent().setPackage("com.google.example.invalidpackage"), 0);

    private GcmNetworkManager(Context context) {
        this.mContext = context;
    }

    public static GcmNetworkManager getInstance(Context context) {
        GcmNetworkManager gcmNetworkManager;
        synchronized (GcmNetworkManager.class) {
            if (zzbfv == null) {
                zzbfv = new GcmNetworkManager(context.getApplicationContext());
            }
            gcmNetworkManager = zzbfv;
        }
        return gcmNetworkManager;
    }

    static void zzdn(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Must provide a valid tag.");
        } else if (100 < str.length()) {
            throw new IllegalArgumentException("Tag is larger than max permissible tag length (100)");
        }
    }

    private final void zzdo(String str) {
        boolean z = true;
        zzbo.zzb((Object) str, (Object) "GcmTaskService must not be null.");
        Intent intent = new Intent(GcmTaskService.SERVICE_ACTION_EXECUTE_TASK);
        intent.setPackage(this.mContext.getPackageName());
        List<ResolveInfo> queryIntentServices = this.mContext.getPackageManager().queryIntentServices(intent, 0);
        boolean z2 = (queryIntentServices == null || queryIntentServices.size() == 0) ? false : true;
        zzbo.zzb(z2, (Object) "There is no GcmTaskService component registered within this package. Have you extended GcmTaskService correctly?");
        for (ResolveInfo resolveInfo : queryIntentServices) {
            if (resolveInfo.serviceInfo.name.equals(str)) {
                break;
            }
        }
        z = false;
        zzbo.zzb(z, new StringBuilder(String.valueOf(str).length() + 119).append("The GcmTaskService class you provided ").append(str).append(" does not seem to support receiving com.google.android.gms.gcm.ACTION_TASK_READY.").toString());
    }

    private final Intent zzvB() {
        String zzbd = zze.zzbd(this.mContext);
        int i = -1;
        if (zzbd != null) {
            i = GoogleCloudMessaging.zzaZ(this.mContext);
        }
        if (zzbd == null || i < GoogleCloudMessaging.zzbfL) {
            Log.e("GcmNetworkManager", "Google Play Services is not available, dropping GcmNetworkManager request. code=" + i);
            return null;
        }
        Intent intent = new Intent("com.google.android.gms.gcm.ACTION_SCHEDULE");
        intent.setPackage(zzbd);
        intent.putExtra("app", this.mPendingIntent);
        intent.putExtra(Param.SOURCE, 4);
        intent.putExtra("source_version", 11020000);
        return intent;
    }

    public void cancelAllTasks(Class<? extends GcmTaskService> cls) {
        Parcelable componentName = new ComponentName(this.mContext, cls);
        zzdo(componentName.getClassName());
        Intent zzvB = zzvB();
        if (zzvB != null) {
            zzvB.putExtra("scheduler_action", "CANCEL_ALL");
            zzvB.putExtra("component", componentName);
            this.mContext.sendBroadcast(zzvB);
        }
    }

    public void cancelTask(String str, Class<? extends GcmTaskService> cls) {
        Parcelable componentName = new ComponentName(this.mContext, cls);
        zzdn(str);
        zzdo(componentName.getClassName());
        Intent zzvB = zzvB();
        if (zzvB != null) {
            zzvB.putExtra("scheduler_action", "CANCEL_TASK");
            zzvB.putExtra("tag", str);
            zzvB.putExtra("component", componentName);
            this.mContext.sendBroadcast(zzvB);
        }
    }

    public void schedule(Task task) {
        zzdo(task.getServiceName());
        Intent zzvB = zzvB();
        if (zzvB != null) {
            Bundle extras = zzvB.getExtras();
            extras.putString("scheduler_action", "SCHEDULE_TASK");
            task.toBundle(extras);
            zzvB.putExtras(extras);
            this.mContext.sendBroadcast(zzvB);
        }
    }
}
