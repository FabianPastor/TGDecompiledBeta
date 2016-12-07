package com.google.android.gms.common.api;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender.SendIntentException;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.zzab;

public final class Status extends AbstractSafeParcelable implements Result, ReflectedParcelable {
    public static final Creator<Status> CREATOR = new zzh();
    public static final Status vY = new Status(0);
    public static final Status vZ = new Status(14);
    public static final Status wa = new Status(8);
    public static final Status wb = new Status(15);
    public static final Status wc = new Status(16);
    public static final Status wd = new Status(17);
    public static final Status we = new Status(18);
    private final PendingIntent mPendingIntent;
    private final int mVersionCode;
    private final int rR;
    private final String uK;

    public Status(int i) {
        this(i, null);
    }

    Status(int i, int i2, String str, PendingIntent pendingIntent) {
        this.mVersionCode = i;
        this.rR = i2;
        this.uK = str;
        this.mPendingIntent = pendingIntent;
    }

    public Status(int i, String str) {
        this(1, i, str, null);
    }

    public Status(int i, String str, PendingIntent pendingIntent) {
        this(1, i, str, pendingIntent);
    }

    private String zzaqi() {
        return this.uK != null ? this.uK : CommonStatusCodes.getStatusCodeString(this.rR);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Status)) {
            return false;
        }
        Status status = (Status) obj;
        return this.mVersionCode == status.mVersionCode && this.rR == status.rR && zzab.equal(this.uK, status.uK) && zzab.equal(this.mPendingIntent, status.mPendingIntent);
    }

    public PendingIntent getResolution() {
        return this.mPendingIntent;
    }

    public Status getStatus() {
        return this;
    }

    public int getStatusCode() {
        return this.rR;
    }

    @Nullable
    public String getStatusMessage() {
        return this.uK;
    }

    int getVersionCode() {
        return this.mVersionCode;
    }

    public boolean hasResolution() {
        return this.mPendingIntent != null;
    }

    public int hashCode() {
        return zzab.hashCode(Integer.valueOf(this.mVersionCode), Integer.valueOf(this.rR), this.uK, this.mPendingIntent);
    }

    public boolean isCanceled() {
        return this.rR == 16;
    }

    public boolean isInterrupted() {
        return this.rR == 14;
    }

    public boolean isSuccess() {
        return this.rR <= 0;
    }

    public void startResolutionForResult(Activity activity, int i) throws SendIntentException {
        if (hasResolution()) {
            activity.startIntentSenderForResult(this.mPendingIntent.getIntentSender(), i, null, 0, 0, 0);
        }
    }

    public String toString() {
        return zzab.zzx(this).zzg("statusCode", zzaqi()).zzg("resolution", this.mPendingIntent).toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zzh.zza(this, parcel, i);
    }

    PendingIntent zzaqh() {
        return this.mPendingIntent;
    }
}
