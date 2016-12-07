package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.CallSuper;
import android.util.Log;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzac;

public abstract class Task implements ReflectedParcelable {
    public static final int EXTRAS_LIMIT_BYTES = 10240;
    public static final int NETWORK_STATE_ANY = 2;
    public static final int NETWORK_STATE_CONNECTED = 0;
    public static final int NETWORK_STATE_UNMETERED = 1;
    protected static final long UNINITIALIZED = -1;
    private final Bundle mExtras;
    private final String mTag;
    private final String zzbgr;
    private final boolean zzbgs;
    private final boolean zzbgt;
    private final int zzbgu;
    private final boolean zzbgv;
    private final boolean zzbgw;
    private final zzc zzbgx;

    public static abstract class Builder {
        protected Bundle extras;
        protected String gcmTaskService;
        protected boolean isPersisted;
        protected int requiredNetworkState;
        protected boolean requiresCharging;
        protected String tag;
        protected boolean updateCurrent;
        protected zzc zzbgy = zzc.zzbgm;

        public abstract Task build();

        @CallSuper
        protected void checkConditions() {
            zzac.zzb(this.gcmTaskService != null, (Object) "Must provide an endpoint for this task by calling setService(ComponentName).");
            GcmNetworkManager.zzeC(this.tag);
            Task.zza(this.zzbgy);
            if (this.isPersisted) {
                Task.zzL(this.extras);
            }
        }

        public abstract Builder setExtras(Bundle bundle);

        public abstract Builder setPersisted(boolean z);

        public abstract Builder setRequiredNetwork(int i);

        public abstract Builder setRequiresCharging(boolean z);

        public abstract Builder setService(Class<? extends GcmTaskService> cls);

        public abstract Builder setTag(String str);

        public abstract Builder setUpdateCurrent(boolean z);
    }

    @Deprecated
    Task(Parcel parcel) {
        boolean z = true;
        Log.e("Task", "Constructing a Task object using a parcel.");
        this.zzbgr = parcel.readString();
        this.mTag = parcel.readString();
        this.zzbgs = parcel.readInt() == 1;
        if (parcel.readInt() != 1) {
            z = false;
        }
        this.zzbgt = z;
        this.zzbgu = 2;
        this.zzbgv = false;
        this.zzbgw = false;
        this.zzbgx = zzc.zzbgm;
        this.mExtras = null;
    }

    Task(Builder builder) {
        this.zzbgr = builder.gcmTaskService;
        this.mTag = builder.tag;
        this.zzbgs = builder.updateCurrent;
        this.zzbgt = builder.isPersisted;
        this.zzbgu = builder.requiredNetworkState;
        this.zzbgv = builder.requiresCharging;
        this.zzbgw = false;
        this.mExtras = builder.extras;
        this.zzbgx = builder.zzbgy != null ? builder.zzbgy : zzc.zzbgm;
    }

    private static boolean zzF(Object obj) {
        return (obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Double) || (obj instanceof String) || (obj instanceof Boolean);
    }

    public static void zzL(Bundle bundle) {
        if (bundle != null) {
            Parcel obtain = Parcel.obtain();
            bundle.writeToParcel(obtain, 0);
            int dataSize = obtain.dataSize();
            if (dataSize > EXTRAS_LIMIT_BYTES) {
                obtain.recycle();
                String valueOf = String.valueOf("Extras exceeding maximum size(10240 bytes): ");
                throw new IllegalArgumentException(new StringBuilder(String.valueOf(valueOf).length() + 11).append(valueOf).append(dataSize).toString());
            }
            obtain.recycle();
            for (String str : bundle.keySet()) {
                if (!zzF(bundle.get(str))) {
                    throw new IllegalArgumentException("Only the following extra parameter types are supported: Integer, Long, Double, String, and Boolean. ");
                }
            }
        }
    }

    public static void zza(zzc com_google_android_gms_gcm_zzc) {
        if (com_google_android_gms_gcm_zzc != null) {
            int zzGg = com_google_android_gms_gcm_zzc.zzGg();
            if (zzGg == 1 || zzGg == 0) {
                int zzGh = com_google_android_gms_gcm_zzc.zzGh();
                int zzGi = com_google_android_gms_gcm_zzc.zzGi();
                if (zzGg == 0 && zzGh < 0) {
                    throw new IllegalArgumentException("InitialBackoffSeconds can't be negative: " + zzGh);
                } else if (zzGg == 1 && zzGh < 10) {
                    throw new IllegalArgumentException("RETRY_POLICY_LINEAR must have an initial backoff at least 10 seconds.");
                } else if (zzGi < zzGh) {
                    throw new IllegalArgumentException("MaximumBackoffSeconds must be greater than InitialBackoffSeconds: " + com_google_android_gms_gcm_zzc.zzGi());
                } else {
                    return;
                }
            }
            throw new IllegalArgumentException("Must provide a valid RetryPolicy: " + zzGg);
        }
    }

    public int describeContents() {
        return 0;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public int getRequiredNetwork() {
        return this.zzbgu;
    }

    public boolean getRequiresCharging() {
        return this.zzbgv;
    }

    public String getServiceName() {
        return this.zzbgr;
    }

    public String getTag() {
        return this.mTag;
    }

    public boolean isPersisted() {
        return this.zzbgt;
    }

    public boolean isUpdateCurrent() {
        return this.zzbgs;
    }

    public void toBundle(Bundle bundle) {
        bundle.putString("tag", this.mTag);
        bundle.putBoolean("update_current", this.zzbgs);
        bundle.putBoolean("persisted", this.zzbgt);
        bundle.putString("service", this.zzbgr);
        bundle.putInt("requiredNetwork", this.zzbgu);
        bundle.putBoolean("requiresCharging", this.zzbgv);
        bundle.putBoolean("requiresIdle", false);
        bundle.putBundle("retryStrategy", this.zzbgx.zzK(new Bundle()));
        bundle.putBundle("extras", this.mExtras);
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2 = 1;
        parcel.writeString(this.zzbgr);
        parcel.writeString(this.mTag);
        parcel.writeInt(this.zzbgs ? 1 : 0);
        if (!this.zzbgt) {
            i2 = 0;
        }
        parcel.writeInt(i2);
    }
}
