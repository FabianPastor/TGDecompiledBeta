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
public final class zzrq extends Fragment implements zzrp {
    private static WeakHashMap<Activity, WeakReference<zzrq>> Bg = new WeakHashMap();
    private Map<String, zzro> Bh = new ArrayMap();
    private Bundle Bi;
    private int zzbtt = 0;

    private void zzb(final String str, @NonNull final zzro com_google_android_gms_internal_zzro) {
        if (this.zzbtt > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable(this) {
                final /* synthetic */ zzrq Bk;

                public void run() {
                    if (this.Bk.zzbtt >= 1) {
                        com_google_android_gms_internal_zzro.onCreate(this.Bk.Bi != null ? this.Bk.Bi.getBundle(str) : null);
                    }
                    if (this.Bk.zzbtt >= 2) {
                        com_google_android_gms_internal_zzro.onStart();
                    }
                    if (this.Bk.zzbtt >= 3) {
                        com_google_android_gms_internal_zzro.onStop();
                    }
                    if (this.Bk.zzbtt >= 4) {
                        com_google_android_gms_internal_zzro.onDestroy();
                    }
                }
            });
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zzrq zzt(Activity activity) {
        WeakReference weakReference = (WeakReference) Bg.get(activity);
        if (weakReference != null) {
            zzrq com_google_android_gms_internal_zzrq = (zzrq) weakReference.get();
        }
        try {
            com_google_android_gms_internal_zzrq = (zzrq) activity.getFragmentManager().findFragmentByTag("LifecycleFragmentImpl");
            if (com_google_android_gms_internal_zzrq == null || com_google_android_gms_internal_zzrq.isRemoving()) {
                com_google_android_gms_internal_zzrq = new zzrq();
                activity.getFragmentManager().beginTransaction().add(com_google_android_gms_internal_zzrq, "LifecycleFragmentImpl").commitAllowingStateLoss();
            }
            Bg.put(activity, new WeakReference(com_google_android_gms_internal_zzrq));
            return com_google_android_gms_internal_zzrq;
        } catch (Throwable e) {
            throw new IllegalStateException("Fragment with tag LifecycleFragmentImpl is not a LifecycleFragmentImpl", e);
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        for (zzro dump : this.Bh.values()) {
            dump.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        for (zzro onActivityResult : this.Bh.values()) {
            onActivityResult.onActivityResult(i, i2, intent);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzbtt = 1;
        this.Bi = bundle;
        for (Entry entry : this.Bh.entrySet()) {
            ((zzro) entry.getValue()).onCreate(bundle != null ? bundle.getBundle((String) entry.getKey()) : null);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.zzbtt = 4;
        for (zzro onDestroy : this.Bh.values()) {
            onDestroy.onDestroy();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            for (Entry entry : this.Bh.entrySet()) {
                Bundle bundle2 = new Bundle();
                ((zzro) entry.getValue()).onSaveInstanceState(bundle2);
                bundle.putBundle((String) entry.getKey(), bundle2);
            }
        }
    }

    public void onStart() {
        super.onStart();
        this.zzbtt = 2;
        for (zzro onStart : this.Bh.values()) {
            onStart.onStart();
        }
    }

    public void onStop() {
        super.onStop();
        this.zzbtt = 3;
        for (zzro onStop : this.Bh.values()) {
            onStop.onStop();
        }
    }

    public <T extends zzro> T zza(String str, Class<T> cls) {
        return (zzro) cls.cast(this.Bh.get(str));
    }

    public void zza(String str, @NonNull zzro com_google_android_gms_internal_zzro) {
        if (this.Bh.containsKey(str)) {
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 59).append("LifecycleCallback with tag ").append(str).append(" already added to this fragment.").toString());
        }
        this.Bh.put(str, com_google_android_gms_internal_zzro);
        zzb(str, com_google_android_gms_internal_zzro);
    }

    public Activity zzaty() {
        return getActivity();
    }
}
