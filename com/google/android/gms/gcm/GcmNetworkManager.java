package com.google.android.gms.gcm;

import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;
import com.google.firebase.analytics.FirebaseAnalytics.Param;
import java.util.List;

public class GcmNetworkManager {
    public static final int RESULT_FAILURE = 2;
    public static final int RESULT_RESCHEDULE = 1;
    public static final int RESULT_SUCCESS = 0;
    private static GcmNetworkManager zzbgp;
    private Context mContext;
    private final PendingIntent mPendingIntent = PendingIntent.getBroadcast(this.mContext, 0, new Intent().setPackage("com.google.example.invalidpackage"), 0);

    private GcmNetworkManager(Context context) {
        this.mContext = context;
    }

    public static GcmNetworkManager getInstance(Context context) {
        GcmNetworkManager gcmNetworkManager;
        synchronized (GcmNetworkManager.class) {
            if (zzbgp == null) {
                zzbgp = new GcmNetworkManager(context.getApplicationContext());
            }
            gcmNetworkManager = zzbgp;
        }
        return gcmNetworkManager;
    }

    private Intent zzGO() {
        String zzbv = GoogleCloudMessaging.zzbv(this.mContext);
        int i = -1;
        if (zzbv != null) {
            i = GoogleCloudMessaging.zzbw(this.mContext);
        }
        if (zzbv == null || i < GoogleCloudMessaging.zzbgB) {
            Log.e("GcmNetworkManager", "Google Play Services is not available, dropping GcmNetworkManager request. code=" + i);
            return null;
        }
        Intent intent = new Intent("com.google.android.gms.gcm.ACTION_SCHEDULE");
        intent.setPackage(zzbv);
        intent.putExtra("app", this.mPendingIntent);
        intent.putExtra(Param.SOURCE, 4);
        intent.putExtra("source_version", 10298000);
        return intent;
    }

    private void zza(ComponentName componentName) {
        zzez(componentName.getClassName());
        Intent zzGO = zzGO();
        if (zzGO != null) {
            zzGO.putExtra("scheduler_action", "CANCEL_ALL");
            zzGO.putExtra("component", componentName);
            this.mContext.sendBroadcast(zzGO);
        }
    }

    private void zza(String str, ComponentName componentName) {
        zzey(str);
        zzez(componentName.getClassName());
        Intent zzGO = zzGO();
        if (zzGO != null) {
            zzGO.putExtra("scheduler_action", "CANCEL_TASK");
            zzGO.putExtra("tag", str);
            zzGO.putExtra("component", componentName);
            this.mContext.sendBroadcast(zzGO);
        }
    }

    static void zzey(String str) {
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("Must provide a valid tag.");
        } else if (100 < str.length()) {
            throw new IllegalArgumentException("Tag is larger than max permissible tag length (100)");
        }
    }

    private void zzez(String str) {
        boolean z = true;
        zzac.zzb((Object) str, (Object) "GcmTaskService must not be null.");
        Intent intent = new Intent(GcmTaskService.SERVICE_ACTION_EXECUTE_TASK);
        intent.setPackage(this.mContext.getPackageName());
        List<ResolveInfo> queryIntentServices = this.mContext.getPackageManager().queryIntentServices(intent, 0);
        boolean z2 = (queryIntentServices == null || queryIntentServices.size() == 0) ? false : true;
        zzac.zzb(z2, (Object) "There is no GcmTaskService component registered within this package. Have you extended GcmTaskService correctly?");
        for (ResolveInfo resolveInfo : queryIntentServices) {
            if (resolveInfo.serviceInfo.name.equals(str)) {
                break;
            }
        }
        z = false;
        zzac.zzb(z, new StringBuilder(String.valueOf(str).length() + 119).append("The GcmTaskService class you provided ").append(str).append(" does not seem to support receiving com.google.android.gms.gcm.ACTION_TASK_READY.").toString());
    }

    public void cancelAllTasks(Class<? extends GcmTaskService> cls) {
        zzd(cls);
    }

    public void cancelTask(String str, Class<? extends GcmTaskService> cls) {
        zzb(str, cls);
    }

    public void schedule(Task task) {
        zzez(task.getServiceName());
        Intent zzGO = zzGO();
        if (zzGO != null) {
            Bundle extras = zzGO.getExtras();
            extras.putString("scheduler_action", "SCHEDULE_TASK");
            task.toBundle(extras);
            zzGO.putExtras(extras);
            this.mContext.sendBroadcast(zzGO);
        }
    }

    public void zzb(String str, Class<? extends Service> cls) {
        zza(str, new ComponentName(this.mContext, cls));
    }

    public void zzd(Class<? extends Service> cls) {
        zza(new ComponentName(this.mContext, cls));
    }
}
