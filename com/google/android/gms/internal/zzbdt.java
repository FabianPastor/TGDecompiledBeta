package com.google.android.gms.internal;

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

public final class zzbdt extends Fragment implements zzbds {
    private static WeakHashMap<Activity, WeakReference<zzbdt>> zzaEH = new WeakHashMap();
    private int zzLg = 0;
    private Map<String, zzbdr> zzaEI = new ArrayMap();
    private Bundle zzaEJ;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zzbdt zzo(Activity activity) {
        WeakReference weakReference = (WeakReference) zzaEH.get(activity);
        if (weakReference != null) {
            zzbdt com_google_android_gms_internal_zzbdt = (zzbdt) weakReference.get();
        }
        try {
            com_google_android_gms_internal_zzbdt = (zzbdt) activity.getFragmentManager().findFragmentByTag("LifecycleFragmentImpl");
            if (com_google_android_gms_internal_zzbdt == null || com_google_android_gms_internal_zzbdt.isRemoving()) {
                com_google_android_gms_internal_zzbdt = new zzbdt();
                activity.getFragmentManager().beginTransaction().add(com_google_android_gms_internal_zzbdt, "LifecycleFragmentImpl").commitAllowingStateLoss();
            }
            zzaEH.put(activity, new WeakReference(com_google_android_gms_internal_zzbdt));
            return com_google_android_gms_internal_zzbdt;
        } catch (Throwable e) {
            throw new IllegalStateException("Fragment with tag LifecycleFragmentImpl is not a LifecycleFragmentImpl", e);
        }
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        for (zzbdr dump : this.zzaEI.values()) {
            dump.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public final void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        for (zzbdr onActivityResult : this.zzaEI.values()) {
            onActivityResult.onActivityResult(i, i2, intent);
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzLg = 1;
        this.zzaEJ = bundle;
        for (Entry entry : this.zzaEI.entrySet()) {
            ((zzbdr) entry.getValue()).onCreate(bundle != null ? bundle.getBundle((String) entry.getKey()) : null);
        }
    }

    public final void onDestroy() {
        super.onDestroy();
        this.zzLg = 5;
        for (zzbdr onDestroy : this.zzaEI.values()) {
            onDestroy.onDestroy();
        }
    }

    public final void onResume() {
        super.onResume();
        this.zzLg = 3;
        for (zzbdr onResume : this.zzaEI.values()) {
            onResume.onResume();
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            for (Entry entry : this.zzaEI.entrySet()) {
                Bundle bundle2 = new Bundle();
                ((zzbdr) entry.getValue()).onSaveInstanceState(bundle2);
                bundle.putBundle((String) entry.getKey(), bundle2);
            }
        }
    }

    public final void onStart() {
        super.onStart();
        this.zzLg = 2;
        for (zzbdr onStart : this.zzaEI.values()) {
            onStart.onStart();
        }
    }

    public final void onStop() {
        super.onStop();
        this.zzLg = 4;
        for (zzbdr onStop : this.zzaEI.values()) {
            onStop.onStop();
        }
    }

    public final <T extends zzbdr> T zza(String str, Class<T> cls) {
        return (zzbdr) cls.cast(this.zzaEI.get(str));
    }

    public final void zza(String str, @NonNull zzbdr com_google_android_gms_internal_zzbdr) {
        if (this.zzaEI.containsKey(str)) {
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 59).append("LifecycleCallback with tag ").append(str).append(" already added to this fragment.").toString());
        }
        this.zzaEI.put(str, com_google_android_gms_internal_zzbdr);
        if (this.zzLg > 0) {
            new Handler(Looper.getMainLooper()).post(new zzbdu(this, com_google_android_gms_internal_zzbdr, str));
        }
    }

    public final Activity zzqF() {
        return getActivity();
    }
}
