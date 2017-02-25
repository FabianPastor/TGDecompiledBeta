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

public final class zzabg extends Fragment implements zzabf {
    private static WeakHashMap<Activity, WeakReference<zzabg>> zzaCS = new WeakHashMap();
    private int zzJO = 0;
    private Map<String, zzabe> zzaCT = new ArrayMap();
    private Bundle zzaCU;

    private void zzb(final String str, @NonNull final zzabe com_google_android_gms_internal_zzabe) {
        if (this.zzJO > 0) {
            new Handler(Looper.getMainLooper()).post(new Runnable(this) {
                final /* synthetic */ zzabg zzaCW;

                public void run() {
                    if (this.zzaCW.zzJO >= 1) {
                        com_google_android_gms_internal_zzabe.onCreate(this.zzaCW.zzaCU != null ? this.zzaCW.zzaCU.getBundle(str) : null);
                    }
                    if (this.zzaCW.zzJO >= 2) {
                        com_google_android_gms_internal_zzabe.onStart();
                    }
                    if (this.zzaCW.zzJO >= 3) {
                        com_google_android_gms_internal_zzabe.onStop();
                    }
                    if (this.zzaCW.zzJO >= 4) {
                        com_google_android_gms_internal_zzabe.onDestroy();
                    }
                }
            });
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static zzabg zzt(Activity activity) {
        WeakReference weakReference = (WeakReference) zzaCS.get(activity);
        if (weakReference != null) {
            zzabg com_google_android_gms_internal_zzabg = (zzabg) weakReference.get();
        }
        try {
            com_google_android_gms_internal_zzabg = (zzabg) activity.getFragmentManager().findFragmentByTag("LifecycleFragmentImpl");
            if (com_google_android_gms_internal_zzabg == null || com_google_android_gms_internal_zzabg.isRemoving()) {
                com_google_android_gms_internal_zzabg = new zzabg();
                activity.getFragmentManager().beginTransaction().add(com_google_android_gms_internal_zzabg, "LifecycleFragmentImpl").commitAllowingStateLoss();
            }
            zzaCS.put(activity, new WeakReference(com_google_android_gms_internal_zzabg));
            return com_google_android_gms_internal_zzabg;
        } catch (Throwable e) {
            throw new IllegalStateException("Fragment with tag LifecycleFragmentImpl is not a LifecycleFragmentImpl", e);
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

    public Activity zzwV() {
        return getActivity();
    }
}
