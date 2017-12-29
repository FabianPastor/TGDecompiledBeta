package com.google.android.gms.common;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.IntentSender.SendIntentException;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.zzbg;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.Arrays;

public final class ConnectionResult extends zzbfm {
    public static final Creator<ConnectionResult> CREATOR = new zzb();
    public static final ConnectionResult zzfkr = new ConnectionResult(0);
    private final int zzcd;
    private int zzeck;
    private final PendingIntent zzeeo;
    private final String zzfks;

    public ConnectionResult(int i) {
        this(i, null, null);
    }

    ConnectionResult(int i, int i2, PendingIntent pendingIntent, String str) {
        this.zzeck = i;
        this.zzcd = i2;
        this.zzeeo = pendingIntent;
        this.zzfks = str;
    }

    public ConnectionResult(int i, PendingIntent pendingIntent) {
        this(i, pendingIntent, null);
    }

    public ConnectionResult(int i, PendingIntent pendingIntent, String str) {
        this(1, i, pendingIntent, str);
    }

    static String getStatusString(int i) {
        switch (i) {
            case -1:
                return "UNKNOWN";
            case 0:
                return "SUCCESS";
            case 1:
                return "SERVICE_MISSING";
            case 2:
                return "SERVICE_VERSION_UPDATE_REQUIRED";
            case 3:
                return "SERVICE_DISABLED";
            case 4:
                return "SIGN_IN_REQUIRED";
            case 5:
                return "INVALID_ACCOUNT";
            case 6:
                return "RESOLUTION_REQUIRED";
            case 7:
                return "NETWORK_ERROR";
            case 8:
                return "INTERNAL_ERROR";
            case 9:
                return "SERVICE_INVALID";
            case 10:
                return "DEVELOPER_ERROR";
            case 11:
                return "LICENSE_CHECK_FAILED";
            case 13:
                return "CANCELED";
            case 14:
                return "TIMEOUT";
            case 15:
                return "INTERRUPTED";
            case 16:
                return "API_UNAVAILABLE";
            case 17:
                return "SIGN_IN_FAILED";
            case 18:
                return "SERVICE_UPDATING";
            case 19:
                return "SERVICE_MISSING_PERMISSION";
            case 20:
                return "RESTRICTED_PROFILE";
            case 21:
                return "API_VERSION_UPDATE_REQUIRED";
            case 99:
                return "UNFINISHED";
            case 1500:
                return "DRIVE_EXTERNAL_STORAGE_REQUIRED";
            default:
                return "UNKNOWN_ERROR_CODE(" + i + ")";
        }
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ConnectionResult)) {
            return false;
        }
        ConnectionResult connectionResult = (ConnectionResult) obj;
        return this.zzcd == connectionResult.zzcd && zzbg.equal(this.zzeeo, connectionResult.zzeeo) && zzbg.equal(this.zzfks, connectionResult.zzfks);
    }

    public final int getErrorCode() {
        return this.zzcd;
    }

    public final String getErrorMessage() {
        return this.zzfks;
    }

    public final PendingIntent getResolution() {
        return this.zzeeo;
    }

    public final boolean hasResolution() {
        return (this.zzcd == 0 || this.zzeeo == null) ? false : true;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{Integer.valueOf(this.zzcd), this.zzeeo, this.zzfks});
    }

    public final boolean isSuccess() {
        return this.zzcd == 0;
    }

    public final void startResolutionForResult(Activity activity, int i) throws SendIntentException {
        if (hasResolution()) {
            activity.startIntentSenderForResult(this.zzeeo.getIntentSender(), i, null, 0, 0, 0);
        }
    }

    public final String toString() {
        return zzbg.zzx(this).zzg("statusCode", getStatusString(this.zzcd)).zzg("resolution", this.zzeeo).zzg("message", this.zzfks).toString();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zzc(parcel, 1, this.zzeck);
        zzbfp.zzc(parcel, 2, getErrorCode());
        zzbfp.zza(parcel, 3, getResolution(), i, false);
        zzbfp.zza(parcel, 4, getErrorMessage(), false);
        zzbfp.zzai(parcel, zze);
    }
}
