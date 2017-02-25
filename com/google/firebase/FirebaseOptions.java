package com.google.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzam;
import com.google.android.gms.common.util.zzw;

public final class FirebaseOptions {
    private final String zzamX;
    private final String zzbWR;
    private final String zzbWS;
    private final String zzbWT;
    private final String zzbWU;
    private final String zzbWV;

    public static final class Builder {
        private String zzamX;
        private String zzbWR;
        private String zzbWS;
        private String zzbWT;
        private String zzbWU;
        private String zzbWV;

        public Builder(FirebaseOptions firebaseOptions) {
            this.zzamX = firebaseOptions.zzamX;
            this.zzbWR = firebaseOptions.zzbWR;
            this.zzbWS = firebaseOptions.zzbWS;
            this.zzbWT = firebaseOptions.zzbWT;
            this.zzbWU = firebaseOptions.zzbWU;
            this.zzbWV = firebaseOptions.zzbWV;
        }

        public FirebaseOptions build() {
            return new FirebaseOptions(this.zzamX, this.zzbWR, this.zzbWS, this.zzbWT, this.zzbWU, this.zzbWV);
        }

        public Builder setApiKey(@NonNull String str) {
            this.zzbWR = zzac.zzh(str, "ApiKey must be set.");
            return this;
        }

        public Builder setApplicationId(@NonNull String str) {
            this.zzamX = zzac.zzh(str, "ApplicationId must be set.");
            return this;
        }

        public Builder setDatabaseUrl(@Nullable String str) {
            this.zzbWS = str;
            return this;
        }

        public Builder setGcmSenderId(@Nullable String str) {
            this.zzbWU = str;
            return this;
        }

        public Builder setStorageBucket(@Nullable String str) {
            this.zzbWV = str;
            return this;
        }
    }

    private FirebaseOptions(@NonNull String str, @NonNull String str2, @Nullable String str3, @Nullable String str4, @Nullable String str5, @Nullable String str6) {
        zzac.zza(!zzw.zzdz(str), (Object) "ApplicationId must be set.");
        this.zzamX = str;
        this.zzbWR = str2;
        this.zzbWS = str3;
        this.zzbWT = str4;
        this.zzbWU = str5;
        this.zzbWV = str6;
    }

    public static FirebaseOptions fromResource(Context context) {
        zzam com_google_android_gms_common_internal_zzam = new zzam(context);
        Object string = com_google_android_gms_common_internal_zzam.getString("google_app_id");
        return TextUtils.isEmpty(string) ? null : new FirebaseOptions(string, com_google_android_gms_common_internal_zzam.getString("google_api_key"), com_google_android_gms_common_internal_zzam.getString("firebase_database_url"), com_google_android_gms_common_internal_zzam.getString("ga_trackingId"), com_google_android_gms_common_internal_zzam.getString("gcm_defaultSenderId"), com_google_android_gms_common_internal_zzam.getString("google_storage_bucket"));
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof FirebaseOptions)) {
            return false;
        }
        FirebaseOptions firebaseOptions = (FirebaseOptions) obj;
        return zzaa.equal(this.zzamX, firebaseOptions.zzamX) && zzaa.equal(this.zzbWR, firebaseOptions.zzbWR) && zzaa.equal(this.zzbWS, firebaseOptions.zzbWS) && zzaa.equal(this.zzbWT, firebaseOptions.zzbWT) && zzaa.equal(this.zzbWU, firebaseOptions.zzbWU) && zzaa.equal(this.zzbWV, firebaseOptions.zzbWV);
    }

    public String getApiKey() {
        return this.zzbWR;
    }

    public String getApplicationId() {
        return this.zzamX;
    }

    public String getDatabaseUrl() {
        return this.zzbWS;
    }

    public String getGcmSenderId() {
        return this.zzbWU;
    }

    public String getStorageBucket() {
        return this.zzbWV;
    }

    public int hashCode() {
        return zzaa.hashCode(this.zzamX, this.zzbWR, this.zzbWS, this.zzbWT, this.zzbWU, this.zzbWV);
    }

    public String toString() {
        return zzaa.zzv(this).zzg("applicationId", this.zzamX).zzg("apiKey", this.zzbWR).zzg("databaseUrl", this.zzbWS).zzg("gcmSenderId", this.zzbWU).zzg("storageBucket", this.zzbWV).toString();
    }
}
