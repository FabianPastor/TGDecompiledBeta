package com.google.android.gms.internal;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

public final class zzbeo extends Fragment implements zzbdt {
    private static WeakHashMap<FragmentActivity, WeakReference<zzbeo>> zzaEH = new WeakHashMap();
    private int zzLe = 0;
    private Map<String, zzbds> zzaEI = new ArrayMap();
    private Bundle zzaEJ;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zzbeo zza(FragmentActivity fragmentActivity) {
        WeakReference weakReference = (WeakReference) zzaEH.get(fragmentActivity);
        if (weakReference != null) {
            zzbeo com_google_android_gms_internal_zzbeo = (zzbeo) weakReference.get();
        }
        try {
            com_google_android_gms_internal_zzbeo = (zzbeo) fragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
            if (com_google_android_gms_internal_zzbeo == null || com_google_android_gms_internal_zzbeo.isRemoving()) {
                com_google_android_gms_internal_zzbeo = new zzbeo();
                fragmentActivity.getSupportFragmentManager().beginTransaction().add(com_google_android_gms_internal_zzbeo, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
            }
            zzaEH.put(fragmentActivity, new WeakReference(com_google_android_gms_internal_zzbeo));
            return com_google_android_gms_internal_zzbeo;
        } catch (Throwable e) {
            throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", e);
        }
    }

    public final void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        for (zzbds dump : this.zzaEI.values()) {
            dump.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public final void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        for (zzbds onActivityResult : this.zzaEI.values()) {
            onActivityResult.onActivityResult(i, i2, intent);
        }
    }

    public final void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzLe = 1;
        this.zzaEJ = bundle;
        for (Entry entry : this.zzaEI.entrySet()) {
            ((zzbds) entry.getValue()).onCreate(bundle != null ? bundle.getBundle((String) entry.getKey()) : null);
        }
    }

    public final void onDestroy() {
        super.onDestroy();
        this.zzLe = 5;
        for (zzbds onDestroy : this.zzaEI.values()) {
            onDestroy.onDestroy();
        }
    }

    public final void onResume() {
        super.onResume();
        this.zzLe = 3;
        for (zzbds onResume : this.zzaEI.values()) {
            onResume.onResume();
        }
    }

    public final void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            for (Entry entry : this.zzaEI.entrySet()) {
                Bundle bundle2 = new Bundle();
                ((zzbds) entry.getValue()).onSaveInstanceState(bundle2);
                bundle.putBundle((String) entry.getKey(), bundle2);
            }
        }
    }

    public final void onStart() {
        super.onStart();
        this.zzLe = 2;
        for (zzbds onStart : this.zzaEI.values()) {
            onStart.onStart();
        }
    }

    public final void onStop() {
        super.onStop();
        this.zzLe = 4;
        for (zzbds onStop : this.zzaEI.values()) {
            onStop.onStop();
        }
    }

    public final <T extends zzbds> T zza(String str, Class<T> cls) {
        return (zzbds) cls.cast(this.zzaEI.get(str));
    }

    public final void zza(String str, @NonNull zzbds com_google_android_gms_internal_zzbds) {
        if (this.zzaEI.containsKey(str)) {
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 59).append("LifecycleCallback with tag ").append(str).append(" already added to this fragment.").toString());
        }
        this.zzaEI.put(str, com_google_android_gms_internal_zzbds);
        if (this.zzLe > 0) {
            new Handler(Looper.getMainLooper()).post(new zzbep(this, com_google_android_gms_internal_zzbds, str));
        }
    }

    public final /* synthetic */ Activity zzqF() {
        return getActivity();
    }
}
