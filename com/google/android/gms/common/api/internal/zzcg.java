package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

public final class zzcg extends Fragment implements zzcf {
    private static WeakHashMap<Activity, WeakReference<zzcg>> zzfue = new WeakHashMap();
    private int zzcbc = 0;
    private Map<String, LifecycleCallback> zzfuf = new ArrayMap();
    private Bundle zzfug;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zzcg zzo(Activity activity) {
        WeakReference weakReference = (WeakReference) zzfue.get(activity);
        if (weakReference != null) {
            zzcg com_google_android_gms_common_api_internal_zzcg = (zzcg) weakReference.get();
        }
        try {
            com_google_android_gms_common_api_internal_zzcg = (zzcg) activity.getFragmentManager().findFragmentByTag("LifecycleFragmentImpl");
            if (com_google_android_gms_common_api_internal_zzcg == null || com_google_android_gms_common_api_internal_zzcg.isRemoving()) {
                com_google_android_gms_common_api_internal_zzcg = new zzcg();
                activity.getFragmentManager().beginTransaction().add(com_google_android_gms_common_api_internal_zzcg, "LifecycleFragmentImpl").commitAllowingStateLoss();
            }
            zzfue.put(activity, new WeakReference(com_google_android_gms_common_api_internal_zzcg));
            return com_google_android_gms_common_api_internal_zzcg;
        } catch (Throwable e) {
            throw new IllegalStateException("Fragment with tag LifecycleFragmentImpl is not a LifecycleFragmentImpl", e);
        }
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        for (LifecycleCallback dump : this.zzfuf.values()) {
            dump.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public final void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        for (LifecycleCallback onActivityResult : this.zzfuf.values()) {
            onActivityResult.onActivityResult(i, i2, intent);
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzcbc = 1;
        this.zzfug = bundle;
        for (Entry entry : this.zzfuf.entrySet()) {
            ((LifecycleCallback) entry.getValue()).onCreate(bundle != null ? bundle.getBundle((String) entry.getKey()) : null);
        }
    }

    public final void onDestroy() {
        super.onDestroy();
        this.zzcbc = 5;
        for (LifecycleCallback onDestroy : this.zzfuf.values()) {
            onDestroy.onDestroy();
        }
    }

    public final void onResume() {
        super.onResume();
        this.zzcbc = 3;
        for (LifecycleCallback onResume : this.zzfuf.values()) {
            onResume.onResume();
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            for (Entry entry : this.zzfuf.entrySet()) {
                Bundle bundle2 = new Bundle();
                ((LifecycleCallback) entry.getValue()).onSaveInstanceState(bundle2);
                bundle.putBundle((String) entry.getKey(), bundle2);
            }
        }
    }

    public final void onStart() {
        super.onStart();
        this.zzcbc = 2;
        for (LifecycleCallback onStart : this.zzfuf.values()) {
            onStart.onStart();
        }
    }

    public final void onStop() {
        super.onStop();
        this.zzcbc = 4;
        for (LifecycleCallback onStop : this.zzfuf.values()) {
            onStop.onStop();
        }
    }

    public final <T extends LifecycleCallback> T zza(String str, Class<T> cls) {
        return (LifecycleCallback) cls.cast(this.zzfuf.get(str));
    }

    public final void zza(String str, LifecycleCallback lifecycleCallback) {
        if (this.zzfuf.containsKey(str)) {
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 59).append("LifecycleCallback with tag ").append(str).append(" already added to this fragment.").toString());
        }
        this.zzfuf.put(str, lifecycleCallback);
        if (this.zzcbc > 0) {
            new Handler(Looper.getMainLooper()).post(new zzch(this, lifecycleCallback, str));
        }
    }

    public final Activity zzajn() {
        return getActivity();
    }
}
