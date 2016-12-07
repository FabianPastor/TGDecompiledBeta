package com.google.android.gms.internal;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

@TargetApi(11)
public final class zzaay extends Fragment implements zzaax {
    private static WeakHashMap<Activity, WeakReference<zzaay>> zzaBt = new WeakHashMap();
    private int zzJh = 0;
    private Map<String, zzaaw> zzaBu = new ArrayMap();
    private Bundle zzaBv;

    private void zzb(final String str, @NonNull final zzaaw com_google_android_gms_internal_zzaaw) {
        if (this.zzJh > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable(this) {
                final /* synthetic */ zzaay zzaBx;

                public void run() {
                    if (this.zzaBx.zzJh >= 1) {
                        com_google_android_gms_internal_zzaaw.onCreate(this.zzaBx.zzaBv != null ? this.zzaBx.zzaBv.getBundle(str) : null);
                    }
                    if (this.zzaBx.zzJh >= 2) {
                        com_google_android_gms_internal_zzaaw.onStart();
                    }
                    if (this.zzaBx.zzJh >= 3) {
                        com_google_android_gms_internal_zzaaw.onStop();
                    }
                    if (this.zzaBx.zzJh >= 4) {
                        com_google_android_gms_internal_zzaaw.onDestroy();
                    }
                }
            });
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zzaay zzt(Activity activity) {
        WeakReference weakReference = (WeakReference) zzaBt.get(activity);
        if (weakReference != null) {
            zzaay com_google_android_gms_internal_zzaay = (zzaay) weakReference.get();
        }
        try {
            com_google_android_gms_internal_zzaay = (zzaay) activity.getFragmentManager().findFragmentByTag("LifecycleFragmentImpl");
            if (com_google_android_gms_internal_zzaay == null || com_google_android_gms_internal_zzaay.isRemoving()) {
                com_google_android_gms_internal_zzaay = new zzaay();
                activity.getFragmentManager().beginTransaction().add(com_google_android_gms_internal_zzaay, "LifecycleFragmentImpl").commitAllowingStateLoss();
            }
            zzaBt.put(activity, new WeakReference(com_google_android_gms_internal_zzaay));
            return com_google_android_gms_internal_zzaay;
        } catch (Throwable e) {
            throw new IllegalStateException("Fragment with tag LifecycleFragmentImpl is not a LifecycleFragmentImpl", e);
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        for (zzaaw dump : this.zzaBu.values()) {
            dump.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        for (zzaaw onActivityResult : this.zzaBu.values()) {
            onActivityResult.onActivityResult(i, i2, intent);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzJh = 1;
        this.zzaBv = bundle;
        for (Entry entry : this.zzaBu.entrySet()) {
            ((zzaaw) entry.getValue()).onCreate(bundle != null ? bundle.getBundle((String) entry.getKey()) : null);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.zzJh = 4;
        for (zzaaw onDestroy : this.zzaBu.values()) {
            onDestroy.onDestroy();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            for (Entry entry : this.zzaBu.entrySet()) {
                Bundle bundle2 = new Bundle();
                ((zzaaw) entry.getValue()).onSaveInstanceState(bundle2);
                bundle.putBundle((String) entry.getKey(), bundle2);
            }
        }
    }

    public void onStart() {
        super.onStart();
        this.zzJh = 2;
        for (zzaaw onStart : this.zzaBu.values()) {
            onStart.onStart();
        }
    }

    public void onStop() {
        super.onStop();
        this.zzJh = 3;
        for (zzaaw onStop : this.zzaBu.values()) {
            onStop.onStop();
        }
    }

    public <T extends zzaaw> T zza(String str, Class<T> cls) {
        return (zzaaw) cls.cast(this.zzaBu.get(str));
    }

    public void zza(String str, @NonNull zzaaw com_google_android_gms_internal_zzaaw) {
        if (this.zzaBu.containsKey(str)) {
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 59).append("LifecycleCallback with tag ").append(str).append(" already added to this fragment.").toString());
        }
        this.zzaBu.put(str, com_google_android_gms_internal_zzaaw);
        zzb(str, com_google_android_gms_internal_zzaaw);
    }

    public Activity zzwo() {
        return getActivity();
    }
}
