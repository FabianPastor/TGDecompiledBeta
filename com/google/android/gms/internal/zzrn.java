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

public final class zzrn extends Fragment implements zzrb {
    private static WeakHashMap<FragmentActivity, WeakReference<zzrn>> yZ = new WeakHashMap();
    private Map<String, zzra> za = new ArrayMap();
    private Bundle zb;
    private int zzbqm = 0;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zzrn zza(FragmentActivity fragmentActivity) {
        WeakReference weakReference = (WeakReference) yZ.get(fragmentActivity);
        if (weakReference != null) {
            zzrn com_google_android_gms_internal_zzrn = (zzrn) weakReference.get();
        }
        try {
            com_google_android_gms_internal_zzrn = (zzrn) fragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
            if (com_google_android_gms_internal_zzrn == null || com_google_android_gms_internal_zzrn.isRemoving()) {
                com_google_android_gms_internal_zzrn = new zzrn();
                fragmentActivity.getSupportFragmentManager().beginTransaction().add(com_google_android_gms_internal_zzrn, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
            }
            yZ.put(fragmentActivity, new WeakReference(com_google_android_gms_internal_zzrn));
            return com_google_android_gms_internal_zzrn;
        } catch (Throwable e) {
            throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", e);
        }
    }

    private void zzb(final String str, @NonNull final zzra com_google_android_gms_internal_zzra) {
        if (this.zzbqm > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable(this) {
                final /* synthetic */ zzrn zj;

                public void run() {
                    if (this.zj.zzbqm >= 1) {
                        com_google_android_gms_internal_zzra.onCreate(this.zj.zb != null ? this.zj.zb.getBundle(str) : null);
                    }
                    if (this.zj.zzbqm >= 2) {
                        com_google_android_gms_internal_zzra.onStart();
                    }
                    if (this.zj.zzbqm >= 3) {
                        com_google_android_gms_internal_zzra.onStop();
                    }
                    if (this.zj.zzbqm >= 4) {
                        com_google_android_gms_internal_zzra.onDestroy();
                    }
                }
            });
        }
    }

    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        super.dump(str, fileDescriptor, printWriter, strArr);
        for (zzra dump : this.za.values()) {
            dump.dump(str, fileDescriptor, printWriter, strArr);
        }
    }

    public void onActivityResult(int i, int i2, Intent intent) {
        super.onActivityResult(i, i2, intent);
        for (zzra onActivityResult : this.za.values()) {
            onActivityResult.onActivityResult(i, i2, intent);
        }
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.zzbqm = 1;
        this.zb = bundle;
        for (Entry entry : this.za.entrySet()) {
            ((zzra) entry.getValue()).onCreate(bundle != null ? bundle.getBundle((String) entry.getKey()) : null);
        }
    }

    public void onDestroy() {
        super.onStop();
        this.zzbqm = 4;
        for (zzra onDestroy : this.za.values()) {
            onDestroy.onDestroy();
        }
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (bundle != null) {
            for (Entry entry : this.za.entrySet()) {
                Bundle bundle2 = new Bundle();
                ((zzra) entry.getValue()).onSaveInstanceState(bundle2);
                bundle.putBundle((String) entry.getKey(), bundle2);
            }
        }
    }

    public void onStart() {
        super.onStop();
        this.zzbqm = 2;
        for (zzra onStart : this.za.values()) {
            onStart.onStart();
        }
    }

    public void onStop() {
        super.onStop();
        this.zzbqm = 3;
        for (zzra onStop : this.za.values()) {
            onStop.onStop();
        }
    }

    public <T extends zzra> T zza(String str, Class<T> cls) {
        return (zzra) cls.cast(this.za.get(str));
    }

    public void zza(String str, @NonNull zzra com_google_android_gms_internal_zzra) {
        if (this.za.containsKey(str)) {
            throw new IllegalArgumentException(new StringBuilder(String.valueOf(str).length() + 59).append("LifecycleCallback with tag ").append(str).append(" already added to this fragment.").toString());
        }
        this.za.put(str, com_google_android_gms_internal_zzra);
        zzb(str, com_google_android_gms_internal_zzra);
    }

    public /* synthetic */ Activity zzasq() {
        return zzass();
    }

    public FragmentActivity zzass() {
        return getActivity();
    }
}
