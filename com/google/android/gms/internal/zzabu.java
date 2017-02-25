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

public final class zzabu extends Fragment implements zzabf {
    private static WeakHashMap<FragmentActivity, WeakReference<zzabu>> zzaCS = new WeakHashMap();
    private int zzJO = 0;
    private Map<String, zzabe> zzaCT = new ArrayMap();
    private Bundle zzaCU;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zzabu zza(FragmentActivity fragmentActivity) {
        WeakReference weakReference = (WeakReference) zzaCS.get(fragmentActivity);
        if (weakReference != null) {
            zzabu com_google_android_gms_internal_zzabu = (zzabu) weakReference.get();
        }
        try {
            com_google_android_gms_internal_zzabu = (zzabu) fragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
            if (com_google_android_gms_internal_zzabu == null || com_google_android_gms_internal_zzabu.isRemoving()) {
                com_google_android_gms_internal_zzabu = new zzabu();
                fragmentActivity.getSupportFragmentManager().beginTransaction().add(com_google_android_gms_internal_zzabu, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
            }
            zzaCS.put(fragmentActivity, new WeakReference(com_google_android_gms_internal_zzabu));
            return com_google_android_gms_internal_zzabu;
        } catch (Throwable e) {
            throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", e);
        }
    }

    private void zzb(final String str, @NonNull final zzabe com_google_android_gms_internal_zzabe) {
        if (this.zzJO > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable(this) {
                final /* synthetic */ zzabu zzaDk;

                public void run() {
                    if (this.zzaDk.zzJO >= 1) {
                        com_google_android_gms_internal_zzabe.onCreate(this.zzaDk.zzaCU != null ? this.zzaDk.zzaCU.getBundle(str) : null);
                    }
                    if (this.zzaDk.zzJO >= 2) {
                        com_google_android_gms_internal_zzabe.onStart();
                    }
                    if (this.zzaDk.zzJO >= 3) {
                        com_google_android_gms_internal_zzabe.onStop();
                    }
                    if (this.zzaDk.zzJO >= 4) {
                        com_google_android_gms_internal_zzabe.onDestroy();
                    }
                }
            });
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        for (zzabe dump : this.zzaCT.values()) {
            dump.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        for (zzabe onActivityResult : this.zzaCT.values()) {
            onActivityResult.onActivityResult(i, i2, intent);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzJO = 1;
        this.zzaCU = bundle;
        for (Entry entry : this.zzaCT.entrySet()) {
            ((zzabe) entry.getValue()).onCreate(bundle != null ? bundle.getBundle((String) entry.getKey()) : null);
        }
    }

    public void onDestroy() {
        super.onDestroy();
        this.zzJO = 4;
        for (zzabe onDestroy : this.zzaCT.values()) {
            onDestroy.onDestroy();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            for (Entry entry : this.zzaCT.entrySet()) {
                Bundle bundle2 = new Bundle();
                ((zzabe) entry.getValue()).onSaveInstanceState(bundle2);
                bundle.putBundle((String) entry.getKey(), bundle2);
            }
        }
    }

    public void onStart() {
        super.onStart();
        this.zzJO = 2;
        for (zzabe onStart : this.zzaCT.values()) {
            onStart.onStart();
        }
    }

    public void onStop() {
        super.onStop();
        this.zzJO = 3;
        for (zzabe onStop : this.zzaCT.values()) {
            onStop.onStop();
        }
    }

    public <T extends zzabe> T zza(String str, Class<T> cls) {
        return (zzabe) cls.cast(this.zzaCT.get(str));
    }

    public void zza(String str, @NonNull zzabe com_google_android_gms_internal_zzabe) {
        if (this.zzaCT.containsKey(str)) {
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 59).append("LifecycleCallback with tag ").append(str).append(" already added to this fragment.").toString());
        }
        this.zzaCT.put(str, com_google_android_gms_internal_zzabe);
        zzb(str, com_google_android_gms_internal_zzabe);
    }

    public /* synthetic */ Activity zzwV() {
        return zzwZ();
    }

    public FragmentActivity zzwZ() {
        return getActivity();
    }
}
