package com.google.android.gms.gcm;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable.Creator;

public class OneoffTask extends Task {
    public static final Creator<OneoffTask> CREATOR = new Creator<OneoffTask>() {
        public /* synthetic */ Object createFromParcel(Parcel parcel) {
            return zzgh(parcel);
        }

        public /* synthetic */ Object[] newArray(int i) {
            return zzju(i);
        }

        public OneoffTask zzgh(Parcel parcel) {
            return new OneoffTask(parcel);
        }

        public OneoffTask[] zzju(int i) {
            return new OneoffTask[i];
        }
    };
    private final long zzbgg;
    private final long zzbgh;

    public static class Builder extends com.google.android.gms.gcm.Task.Builder {
        private long zzbgi;
        private long zzbgj;

        public Builder() {
            this.zzbgi = -1;
            this.zzbgj = -1;
            this.isPersisted = false;
        }

        public OneoffTask build() {
            checkConditions();
            return new OneoffTask();
        }

        protected void checkConditions() {
            super.checkConditions();
            if (this.zzbgi == -1 || this.zzbgj == -1) {
                throw new IllegalArgumentException("Must specify an execution window using setExecutionWindow.");
            } else if (this.zzbgi >= this.zzbgj) {
                throw new IllegalArgumentException("Window start must be shorter than window end.");
            }
        }

        public Builder setExecutionWindow(long j, long j2) {
            this.zzbgi = j;
            this.zzbgj = j2;
            return this;
        }

        public Builder setExtras(Bundle bundle) {
            this.extras = bundle;
            return this;
        }

        public Builder setPersisted(boolean z) {
            this.isPersisted = z;
            return this;
        }

        public Builder setRequiredNetwork(int i) {
            this.requiredNetworkState = i;
            return this;
        }

        public Builder setRequiresCharging(boolean z) {
            this.requiresCharging = z;
            return this;
        }

        public Builder setService(Class<? extends GcmTaskService> cls) {
            this.gcmTaskService = cls.getName();
            return this;
        }

        public Builder setTag(String str) {
            this.tag = str;
            return this;
        }

        public Builder setUpdateCurrent(boolean z) {
            this.updateCurrent = z;
            return this;
        }
    }

    @Deprecated
    private OneoffTask(Parcel parcel) {
        super(parcel);
        this.zzbgg = parcel.readLong();
        this.zzbgh = parcel.readLong();
    }

    private OneoffTask(Builder builder) {
        super((com.google.android.gms.gcm.Task.Builder) builder);
        this.zzbgg = builder.zzbgi;
        this.zzbgh = builder.zzbgj;
    }

    public long getWindowEnd() {
        return this.zzbgh;
    }

    public long getWindowStart() {
        return this.zzbgg;
    }

    public void toBundle(Bundle bundle) {
        super.toBundle(bundle);
        bundle.putLong("window_start", this.zzbgg);
        bundle.putLong("window_end", this.zzbgh);
    }

    public String toString() {
        String valueOf = String.valueOf(super.toString());
        long windowStart = getWindowStart();
        return new StringBuilder(String.valueOf(valueOf).length() + 64).append(valueOf).append(" windowStart=").append(windowStart).append(" windowEnd=").append(getWindowEnd()).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        super.writeToParcel(parcel, i);
        parcel.writeLong(this.zzbgg);
        parcel.writeLong(this.zzbgh);
    }
}
