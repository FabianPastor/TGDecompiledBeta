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
    private final String zzbgX;
    private final boolean zzbgY;
    private final boolean zzbgZ;
    private final int zzbha;
    private final boolean zzbhb;
    private final boolean zzbhc;
    private final zzc zzbhd;

    public static abstract class Builder {
        protected Bundle extras;
        protected String gcmTaskService;
        protected boolean isPersisted;
        protected int requiredNetworkState;
        protected boolean requiresCharging;
        protected String tag;
        protected boolean updateCurrent;
        protected zzc zzbhe = zzc.zzbgS;

        public abstract Task build();

        @CallSuper
        protected void checkConditions() {
            zzac.zzb(this.gcmTaskService != null, (Object) "Must provide an endpoint for this task by calling setService(ComponentName).");
            GcmNetworkManager.zzey(this.tag);
            Task.zza(this.zzbhe);
            if (this.isPersisted) {
                Task.zzK(this.extras);
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
        this.zzbgX = parcel.readString();
        this.mTag = parcel.readString();
        this.zzbgY = parcel.readInt() == 1;
        if (parcel.readInt() != 1) {
            z = false;
        }
        this.zzbgZ = z;
        this.zzbha = 2;
        this.zzbhb = false;
        this.zzbhc = false;
        this.zzbhd = zzc.zzbgS;
        this.mExtras = null;
    }

    Task(Builder builder) {
        this.zzbgX = builder.gcmTaskService;
        this.mTag = builder.tag;
        this.zzbgY = builder.updateCurrent;
        this.zzbgZ = builder.isPersisted;
        this.zzbha = builder.requiredNetworkState;
        this.zzbhb = builder.requiresCharging;
        this.zzbhc = false;
        this.mExtras = builder.extras;
        this.zzbhd = builder.zzbhe != null ? builder.zzbhe : zzc.zzbgS;
    }

    private static boolean zzF(Object obj) {
        return (obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Double) || (obj instanceof String) || (obj instanceof Boolean);
    }

    public static void zzK(Bundle bundle) {
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
                Object obj = bundle.get(str);
                if (!zzF(obj)) {
                    if (obj instanceof Bundle) {
                        zzK((Bundle) obj);
                    } else {
                        throw new IllegalArgumentException("Only the following extra parameter types are supported: Integer, Long, Double, String, Boolean, and nested Bundles with the same restrictions.");
                    }
                }
            }
        }
    }

    public static void zza(zzc com_google_android_gms_gcm_zzc) {
        if (com_google_android_gms_gcm_zzc != null) {
            int zzGU = com_google_android_gms_gcm_zzc.zzGU();
            if (zzGU == 1 || zzGU == 0) {
                int zzGV = com_google_android_gms_gcm_zzc.zzGV();
                int zzGW = com_google_android_gms_gcm_zzc.zzGW();
                if (zzGU == 0 && zzGV < 0) {
                    throw new IllegalArgumentException("InitialBackoffSeconds can't be negative: " + zzGV);
                } else if (zzGU == 1 && zzGV < 10) {
                    throw new IllegalArgumentException("RETRY_POLICY_LINEAR must have an initial backoff at least 10 seconds.");
                } else if (zzGW < zzGV) {
                    throw new IllegalArgumentException("MaximumBackoffSeconds must be greater than InitialBackoffSeconds: " + com_google_android_gms_gcm_zzc.zzGW());
                } else {
                    return;
                }
            }
            throw new IllegalArgumentException("Must provide a valid RetryPolicy: " + zzGU);
        }
    }

    public int describeContents() {
        return 0;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public int getRequiredNetwork() {
        return this.zzbha;
    }

    public boolean getRequiresCharging() {
        return this.zzbhb;
    }

    public String getServiceName() {
        return this.zzbgX;
    }

    public String getTag() {
        return this.mTag;
    }

    public boolean isPersisted() {
        return this.zzbgZ;
    }

    public boolean isUpdateCurrent() {
        return this.zzbgY;
    }

    public void toBundle(Bundle bundle) {
        bundle.putString("tag", this.mTag);
        bundle.putBoolean("update_current", this.zzbgY);
        bundle.putBoolean("persisted", this.zzbgZ);
        bundle.putString("service", this.zzbgX);
        bundle.putInt("requiredNetwork", this.zzbha);
        bundle.putBoolean("requiresCharging", this.zzbhb);
        bundle.putBoolean("requiresIdle", false);
        bundle.putBundle("retryStrategy", this.zzbhd.zzJ(new Bundle()));
        bundle.putBundle("extras", this.mExtras);
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2 = 1;
        parcel.writeString(this.zzbgX);
        parcel.writeString(this.mTag);
        parcel.writeInt(this.zzbgY ? 1 : 0);
        if (!this.zzbgZ) {
            i2 = 0;
        }
        parcel.writeInt(i2);
    }
}
