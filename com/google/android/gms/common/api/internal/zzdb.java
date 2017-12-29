package com.google.android.gms.common.api.internal;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.ArrayMap;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

public final class zzdb extends Fragment implements zzcf {
    private static WeakHashMap<FragmentActivity, WeakReference<zzdb>> zzfue = new WeakHashMap();
    private int zzcbc = 0;
    private Map<String, LifecycleCallback> zzfuf = new ArrayMap();
    private Bundle zzfug;

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zzdb zza(FragmentActivity fragmentActivity) {
        WeakReference weakReference = (WeakReference) zzfue.get(fragmentActivity);
        if (weakReference != null) {
            zzdb com_google_android_gms_common_api_internal_zzdb = (zzdb) weakReference.get();
        }
        try {
            com_google_android_gms_common_api_internal_zzdb = (zzdb) fragmentActivity.getSupportFragmentManager().findFragmentByTag("SupportLifecycleFragmentImpl");
            if (com_google_android_gms_common_api_internal_zzdb == null || com_google_android_gms_common_api_internal_zzdb.isRemoving()) {
                com_google_android_gms_common_api_internal_zzdb = new zzdb();
                fragmentActivity.getSupportFragmentManager().beginTransaction().add(com_google_android_gms_common_api_internal_zzdb, "SupportLifecycleFragmentImpl").commitAllowingStateLoss();
            }
            zzfue.put(fragmentActivity, new WeakReference(com_google_android_gms_common_api_internal_zzdb));
            return com_google_android_gms_common_api_internal_zzdb;
        } catch (Throwable e) {
            throw new IllegalStateException("Fragment with tag SupportLifecycleFragmentImpl is not a SupportLifecycleFragmentImpl", e);
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
            new Handler(Looper.getMainLooper()).post(new zzdc(this, lifecycleCallback, str));
        }
    }

    public final /* synthetic */ Activity zzajn() {
        return getActivity();
    }
}
