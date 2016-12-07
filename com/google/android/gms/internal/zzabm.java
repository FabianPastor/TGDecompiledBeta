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

public final class zzabm extends Fragment implements zzaax {
    private static WeakHashMap<FragmentActivity, WeakReference<zzabm>> zzaBt = new WeakHashMap();
    private int zzJh = 0;
    private Map<String, zzaaw> zzaBu = new ArrayMap();
    private Bundle zzaBv;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zzabm zza(FragmentActivity fragmentActivity) {
        WeakReference weakReference = (WeakReference) zzaBt.get(fragmentActivity);
        if (weakReference != null) {
            zzabm com_google_android_gms_internal_zzabm = (zzabm) weakReference.get();
        }
        try {
            com_google_android_gms_internal_zzabm = (zzabm) fragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
            if (com_google_android_gms_internal_zzabm == null || com_google_android_gms_internal_zzabm.isRemoving()) {
                com_google_android_gms_internal_zzabm = new zzabm();
                fragmentActivity.getSupportFragmentManager().beginTransaction().add(com_google_android_gms_internal_zzabm, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
            }
            zzaBt.put(fragmentActivity, new WeakReference(com_google_android_gms_internal_zzabm));
            return com_google_android_gms_internal_zzabm;
        } catch (Throwable e) {
            throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", e);
        }
    }

    private void zzb(final String str, @NonNull final zzaaw com_google_android_gms_internal_zzaaw) {
        if (this.zzJh > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable(this) {
                final /* synthetic */ zzabm zzaBL;

                public void run() {
                    if (this.zzaBL.zzJh >= 1) {
                        com_google_android_gms_internal_zzaaw.onCreate(this.zzaBL.zzaBv != null ? this.zzaBL.zzaBv.getBundle(str) : null);
                    }
                    if (this.zzaBL.zzJh >= 2) {
                        com_google_android_gms_internal_zzaaw.onStart();
                    }
                    if (this.zzaBL.zzJh >= 3) {
                        com_google_android_gms_internal_zzaaw.onStop();
                    }
                    if (this.zzaBL.zzJh >= 4) {
                        com_google_android_gms_internal_zzaaw.onDestroy();
                    }
                }
            });
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

    public /* synthetic */ Activity zzwo() {
        return zzws();
    }

    public FragmentActivity zzws() {
        return getActivity();
    }
}
