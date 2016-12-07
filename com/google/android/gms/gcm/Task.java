package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.util.Log;
import com.google.android.gms.common.internal.zzaa;

public abstract class Task implements Parcelable {
    public static final int EXTRAS_LIMIT_BYTES = 10240;
    public static final int NETWORK_STATE_ANY = 2;
    public static final int NETWORK_STATE_CONNECTED = 0;
    public static final int NETWORK_STATE_UNMETERED = 1;
    protected static final long UNINITIALIZED = -1;
    private final String aht;
    private final boolean ahu;
    private final boolean ahv;
    private final int ahw;
    private final boolean ahx;
    private final boolean ahy;
    private final zzc ahz;
    private final Bundle mExtras;
    private final String mTag;

    public static abstract class Builder {
        protected zzc ahA = zzc.aho;
        protected Bundle extras;
        protected String gcmTaskService;
        protected boolean isPersisted;
        protected int requiredNetworkState;
        protected boolean requiresCharging;
        protected String tag;
        protected boolean updateCurrent;

        public abstract Task build();

        @CallSuper
        protected void checkConditions() {
            zzaa.zzb(this.gcmTaskService != null, (Object) "Must provide an endpoint for this task by calling setService(ComponentName).");
            GcmNetworkManager.zzki(this.tag);
            Task.zza(this.ahA);
            if (this.isPersisted) {
                Task.zzak(this.extras);
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
        this.aht = parcel.readString();
        this.mTag = parcel.readString();
        this.ahu = parcel.readInt() == 1;
        if (parcel.readInt() != 1) {
            z = false;
        }
        this.ahv = z;
        this.ahw = 2;
        this.ahx = false;
        this.ahy = false;
        this.ahz = zzc.aho;
        this.mExtras = null;
    }

    Task(Builder builder) {
        this.aht = builder.gcmTaskService;
        this.mTag = builder.tag;
        this.ahu = builder.updateCurrent;
        this.ahv = builder.isPersisted;
        this.ahw = builder.requiredNetworkState;
        this.ahx = builder.requiresCharging;
        this.ahy = false;
        this.mExtras = builder.extras;
        this.ahz = builder.ahA != null ? builder.ahA : zzc.aho;
    }

    public static void zza(zzc com_google_android_gms_gcm_zzc) {
        if (com_google_android_gms_gcm_zzc != null) {
            int zzbnv = com_google_android_gms_gcm_zzc.zzbnv();
            if (zzbnv == 1 || zzbnv == 0) {
                int zzbnw = com_google_android_gms_gcm_zzc.zzbnw();
                int zzbnx = com_google_android_gms_gcm_zzc.zzbnx();
                if (zzbnv == 0 && zzbnw < 0) {
                    throw new IllegalArgumentException("InitialBackoffSeconds can't be negative: " + zzbnw);
                } else if (zzbnv == 1 && zzbnw < 10) {
                    throw new IllegalArgumentException("RETRY_POLICY_LINEAR must have an initial backoff at least 10 seconds.");
                } else if (zzbnx < zzbnw) {
                    throw new IllegalArgumentException("MaximumBackoffSeconds must be greater than InitialBackoffSeconds: " + com_google_android_gms_gcm_zzc.zzbnx());
                } else {
                    return;
                }
            }
            throw new IllegalArgumentException("Must provide a valid RetryPolicy: " + zzbnv);
        }
    }

    private static boolean zzah(Object obj) {
        return (obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Double) || (obj instanceof String) || (obj instanceof Boolean);
    }

    public static void zzak(Bundle bundle) {
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
                if (!zzah(bundle.get(str))) {
                    throw new IllegalArgumentException("Only the following extra parameter types are supported: Integer, Long, Double, String, and Boolean. ");
                }
            }
        }
    }

    public int describeContents() {
        return 0;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public int getRequiredNetwork() {
        return this.ahw;
    }

    public boolean getRequiresCharging() {
        return this.ahx;
    }

    public String getServiceName() {
        return this.aht;
    }

    public String getTag() {
        return this.mTag;
    }

    public boolean isPersisted() {
        return this.ahv;
    }

    public boolean isUpdateCurrent() {
        return this.ahu;
    }

    public void toBundle(Bundle bundle) {
        bundle.putString("tag", this.mTag);
        bundle.putBoolean("update_current", this.ahu);
        bundle.putBoolean("persisted", this.ahv);
        bundle.putString("service", this.aht);
        bundle.putInt("requiredNetwork", this.ahw);
        bundle.putBoolean("requiresCharging", this.ahx);
        bundle.putBoolean("requiresIdle", this.ahy);
        bundle.putBundle("retryStrategy", this.ahz.zzaj(new Bundle()));
        bundle.putBundle("extras", this.mExtras);
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2 = 1;
        parcel.writeString(this.aht);
        parcel.writeString(this.mTag);
        parcel.writeInt(this.ahu ? 1 : 0);
        if (!this.ahv) {
            i2 = 0;
        }
        parcel.writeInt(i2);
    }
}
