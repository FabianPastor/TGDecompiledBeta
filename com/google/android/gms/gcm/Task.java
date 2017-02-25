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
    private final String zzbgW;
    private final boolean zzbgX;
    private final boolean zzbgY;
    private final int zzbgZ;
    private final boolean zzbha;
    private final boolean zzbhb;
    private final zzc zzbhc;

    public static abstract class Builder {
        protected Bundle extras;
        protected String gcmTaskService;
        protected boolean isPersisted;
        protected int requiredNetworkState;
        protected boolean requiresCharging;
        protected String tag;
        protected boolean updateCurrent;
        protected zzc zzbhd = zzc.zzbgR;

        public abstract Task build();

        @CallSuper
        protected void checkConditions() {
            zzac.zzb(this.gcmTaskService != null, (Object) "Must provide an endpoint for this task by calling setService(ComponentName).");
            GcmNetworkManager.zzey(this.tag);
            Task.zza(this.zzbhd);
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
        this.zzbgW = parcel.readString();
        this.mTag = parcel.readString();
        this.zzbgX = parcel.readInt() == 1;
        if (parcel.readInt() != 1) {
            z = false;
        }
        this.zzbgY = z;
        this.zzbgZ = 2;
        this.zzbha = false;
        this.zzbhb = false;
        this.zzbhc = zzc.zzbgR;
        this.mExtras = null;
    }

    Task(Builder builder) {
        this.zzbgW = builder.gcmTaskService;
        this.mTag = builder.tag;
        this.zzbgX = builder.updateCurrent;
        this.zzbgY = builder.isPersisted;
        this.zzbgZ = builder.requiredNetworkState;
        this.zzbha = builder.requiresCharging;
        this.zzbhb = false;
        this.mExtras = builder.extras;
        this.zzbhc = builder.zzbhd != null ? builder.zzbhd : zzc.zzbgR;
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
                Object obj = bundle.get(str);
                if (!zzF(obj)) {
                    if (obj instanceof Bundle) {
                        zzL((Bundle) obj);
                    } else {
                        throw new IllegalArgumentException("Only the following extra parameter types are supported: Integer, Long, Double, String, Boolean, and nested Bundles with the same restrictions.");
                    }
                }
            }
        }
    }

    public static void zza(zzc com_google_android_gms_gcm_zzc) {
        if (com_google_android_gms_gcm_zzc != null) {
            int zzGT = com_google_android_gms_gcm_zzc.zzGT();
            if (zzGT == 1 || zzGT == 0) {
                int zzGU = com_google_android_gms_gcm_zzc.zzGU();
                int zzGV = com_google_android_gms_gcm_zzc.zzGV();
                if (zzGT == 0 && zzGU < 0) {
                    throw new IllegalArgumentException("InitialBackoffSeconds can't be negative: " + zzGU);
                } else if (zzGT == 1 && zzGU < 10) {
                    throw new IllegalArgumentException("RETRY_POLICY_LINEAR must have an initial backoff at least 10 seconds.");
                } else if (zzGV < zzGU) {
                    throw new IllegalArgumentException("MaximumBackoffSeconds must be greater than InitialBackoffSeconds: " + com_google_android_gms_gcm_zzc.zzGV());
                } else {
                    return;
                }
            }
            throw new IllegalArgumentException("Must provide a valid RetryPolicy: " + zzGT);
        }
    }

    public int describeContents() {
        return 0;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public int getRequiredNetwork() {
        return this.zzbgZ;
    }

    public boolean getRequiresCharging() {
        return this.zzbha;
    }

    public String getServiceName() {
        return this.zzbgW;
    }

    public String getTag() {
        return this.mTag;
    }

    public boolean isPersisted() {
        return this.zzbgY;
    }

    public boolean isUpdateCurrent() {
        return this.zzbgX;
    }

    public void toBundle(Bundle bundle) {
        bundle.putString("tag", this.mTag);
        bundle.putBoolean("update_current", this.zzbgX);
        bundle.putBoolean("persisted", this.zzbgY);
        bundle.putString("service", this.zzbgW);
        bundle.putInt("requiredNetwork", this.zzbgZ);
        bundle.putBoolean("requiresCharging", this.zzbha);
        bundle.putBoolean("requiresIdle", false);
        bundle.putBundle("retryStrategy", this.zzbhc.zzK(new Bundle()));
        bundle.putBundle("extras", this.mExtras);
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2 = 1;
        parcel.writeString(this.zzbgW);
        parcel.writeString(this.mTag);
        parcel.writeInt(this.zzbgX ? 1 : 0);
        if (!this.zzbgY) {
            i2 = 0;
        }
        parcel.writeInt(i2);
    }
}
