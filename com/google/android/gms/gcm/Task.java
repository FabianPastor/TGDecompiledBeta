package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.CallSuper;
import android.util.Log;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.zzbo;

public class Task implements ReflectedParcelable {
    public static final int EXTRAS_LIMIT_BYTES = 10240;
    public static final int NETWORK_STATE_ANY = 2;
    public static final int NETWORK_STATE_CONNECTED = 0;
    public static final int NETWORK_STATE_UNMETERED = 1;
    protected static final long UNINITIALIZED = -1;
    private final Bundle mExtras;
    private final String mTag;
    private final String zzbgg;
    private final boolean zzbgh;
    private final boolean zzbgi;
    private final int zzbgj;
    private final boolean zzbgk;
    private final boolean zzbgl;
    private final zzi zzbgm;

    public static abstract class Builder {
        protected Bundle extras;
        protected String gcmTaskService;
        protected boolean isPersisted;
        protected int requiredNetworkState;
        protected boolean requiresCharging;
        protected String tag;
        protected boolean updateCurrent;
        protected zzi zzbgn = zzi.zzbgb;

        public abstract Task build();

        @CallSuper
        protected void checkConditions() {
            zzbo.zzb(this.gcmTaskService != null, (Object) "Must provide an endpoint for this task by calling setService(ComponentName).");
            GcmNetworkManager.zzdn(this.tag);
            zzi com_google_android_gms_gcm_zzi = this.zzbgn;
            if (com_google_android_gms_gcm_zzi != null) {
                int zzvE = com_google_android_gms_gcm_zzi.zzvE();
                if (zzvE == 1 || zzvE == 0) {
                    int zzvF = com_google_android_gms_gcm_zzi.zzvF();
                    int zzvG = com_google_android_gms_gcm_zzi.zzvG();
                    if (zzvE == 0 && zzvF < 0) {
                        throw new IllegalArgumentException("InitialBackoffSeconds can't be negative: " + zzvF);
                    } else if (zzvE == 1 && zzvF < 10) {
                        throw new IllegalArgumentException("RETRY_POLICY_LINEAR must have an initial backoff at least 10 seconds.");
                    } else if (zzvG < zzvF) {
                        throw new IllegalArgumentException("MaximumBackoffSeconds must be greater than InitialBackoffSeconds: " + com_google_android_gms_gcm_zzi.zzvG());
                    }
                }
                throw new IllegalArgumentException("Must provide a valid RetryPolicy: " + zzvE);
            }
            if (this.isPersisted) {
                Task.zzy(this.extras);
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
        this.zzbgg = parcel.readString();
        this.mTag = parcel.readString();
        this.zzbgh = parcel.readInt() == 1;
        if (parcel.readInt() != 1) {
            z = false;
        }
        this.zzbgi = z;
        this.zzbgj = 2;
        this.zzbgk = false;
        this.zzbgl = false;
        this.zzbgm = zzi.zzbgb;
        this.mExtras = null;
    }

    Task(Builder builder) {
        this.zzbgg = builder.gcmTaskService;
        this.mTag = builder.tag;
        this.zzbgh = builder.updateCurrent;
        this.zzbgi = builder.isPersisted;
        this.zzbgj = builder.requiredNetworkState;
        this.zzbgk = builder.requiresCharging;
        this.zzbgl = false;
        this.mExtras = builder.extras;
        this.zzbgm = builder.zzbgn != null ? builder.zzbgn : zzi.zzbgb;
    }

    public static void zzy(Bundle bundle) {
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
                dataSize = ((obj instanceof Integer) || (obj instanceof Long) || (obj instanceof Double) || (obj instanceof String) || (obj instanceof Boolean)) ? 1 : 0;
                if (dataSize == 0) {
                    if (obj instanceof Bundle) {
                        zzy((Bundle) obj);
                    } else {
                        throw new IllegalArgumentException("Only the following extra parameter types are supported: Integer, Long, Double, String, Boolean, and nested Bundles with the same restrictions.");
                    }
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
        return this.zzbgj;
    }

    public boolean getRequiresCharging() {
        return this.zzbgk;
    }

    public String getServiceName() {
        return this.zzbgg;
    }

    public String getTag() {
        return this.mTag;
    }

    public boolean isPersisted() {
        return this.zzbgi;
    }

    public boolean isUpdateCurrent() {
        return this.zzbgh;
    }

    public void toBundle(Bundle bundle) {
        bundle.putString("tag", this.mTag);
        bundle.putBoolean("update_current", this.zzbgh);
        bundle.putBoolean("persisted", this.zzbgi);
        bundle.putString("service", this.zzbgg);
        bundle.putInt("requiredNetwork", this.zzbgj);
        bundle.putBoolean("requiresCharging", this.zzbgk);
        bundle.putBoolean("requiresIdle", false);
        bundle.putBundle("retryStrategy", this.zzbgm.zzx(new Bundle()));
        bundle.putBundle("extras", this.mExtras);
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2 = 1;
        parcel.writeString(this.zzbgg);
        parcel.writeString(this.mTag);
        parcel.writeInt(this.zzbgh ? 1 : 0);
        if (!this.zzbgi) {
            i2 = 0;
        }
        parcel.writeInt(i2);
    }
}
