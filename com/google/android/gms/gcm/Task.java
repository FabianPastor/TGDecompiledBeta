package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.util.Log;
import com.google.android.gms.common.internal.zzac;

public abstract class Task implements Parcelable {
    public static final int EXTRAS_LIMIT_BYTES = 10240;
    public static final int NETWORK_STATE_ANY = 2;
    public static final int NETWORK_STATE_CONNECTED = 0;
    public static final int NETWORK_STATE_UNMETERED = 1;
    protected static final long UNINITIALIZED = -1;
    private final String afk;
    private final boolean afl;
    private final boolean afm;
    private final int afn;
    private final boolean afo;
    private final zzc afp;
    private final Bundle mExtras;
    private final String mTag;

    public static abstract class Builder {
        protected zzc afq = zzc.aff;
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
            zzac.zzb(this.gcmTaskService != null, (Object) "Must provide an endpoint for this task by calling setService(ComponentName).");
            GcmNetworkManager.zzki(this.tag);
            Task.zza(this.afq);
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
        this.afk = parcel.readString();
        this.mTag = parcel.readString();
        this.afl = parcel.readInt() == 1;
        if (parcel.readInt() != 1) {
            z = false;
        }
        this.afm = z;
        this.afn = 2;
        this.afo = false;
        this.afp = zzc.aff;
        this.mExtras = null;
    }

    Task(Builder builder) {
        this.afk = builder.gcmTaskService;
        this.mTag = builder.tag;
        this.afl = builder.updateCurrent;
        this.afm = builder.isPersisted;
        this.afn = builder.requiredNetworkState;
        this.afo = builder.requiresCharging;
        this.mExtras = builder.extras;
        this.afp = builder.afq != null ? builder.afq : zzc.aff;
    }

    public static void zza(zzc com_google_android_gms_gcm_zzc) {
        if (com_google_android_gms_gcm_zzc != null) {
            int zzboc = com_google_android_gms_gcm_zzc.zzboc();
            if (zzboc == 1 || zzboc == 0) {
                int zzbod = com_google_android_gms_gcm_zzc.zzbod();
                int zzboe = com_google_android_gms_gcm_zzc.zzboe();
                if (zzboc == 0 && zzbod < 0) {
                    throw new IllegalArgumentException("InitialBackoffSeconds can't be negative: " + zzbod);
                } else if (zzboc == 1 && zzbod < 10) {
                    throw new IllegalArgumentException("RETRY_POLICY_LINEAR must have an initial backoff at least 10 seconds.");
                } else if (zzboe < zzbod) {
                    throw new IllegalArgumentException("MaximumBackoffSeconds must be greater than InitialBackoffSeconds: " + com_google_android_gms_gcm_zzc.zzboe());
                } else {
                    return;
                }
            }
            throw new IllegalArgumentException("Must provide a valid RetryPolicy: " + zzboc);
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
        return this.afn;
    }

    public boolean getRequiresCharging() {
        return this.afo;
    }

    public String getServiceName() {
        return this.afk;
    }

    public String getTag() {
        return this.mTag;
    }

    public boolean isPersisted() {
        return this.afm;
    }

    public boolean isUpdateCurrent() {
        return this.afl;
    }

    public void toBundle(Bundle bundle) {
        bundle.putString("tag", this.mTag);
        bundle.putBoolean("update_current", this.afl);
        bundle.putBoolean("persisted", this.afm);
        bundle.putString("service", this.afk);
        bundle.putInt("requiredNetwork", this.afn);
        bundle.putBoolean("requiresCharging", this.afo);
        bundle.putBundle("retryStrategy", this.afp.zzaj(new Bundle()));
        bundle.putBundle("extras", this.mExtras);
    }

    public void writeToParcel(Parcel parcel, int i) {
        int i2 = 1;
        parcel.writeString(this.afk);
        parcel.writeString(this.mTag);
        parcel.writeInt(this.afl ? 1 : 0);
        if (!this.afm) {
            i2 = 0;
        }
        parcel.writeInt(i2);
    }
}
