package com.google.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.internal.zzam;
import com.google.android.gms.common.util.zzv;

public final class FirebaseOptions {
    private final String zzalR;
    private final String zzbUL;
    private final String zzbUM;
    private final String zzbUN;
    private final String zzbUO;
    private final String zzbUP;

    public static final class Builder {
        private String zzalR;
        private String zzbUL;
        private String zzbUM;
        private String zzbUN;
        private String zzbUO;
        private String zzbUP;

        public Builder(FirebaseOptions firebaseOptions) {
            this.zzalR = firebaseOptions.zzalR;
            this.zzbUL = firebaseOptions.zzbUL;
            this.zzbUM = firebaseOptions.zzbUM;
            this.zzbUN = firebaseOptions.zzbUN;
            this.zzbUO = firebaseOptions.zzbUO;
            this.zzbUP = firebaseOptions.zzbUP;
        }

        public FirebaseOptions build() {
            return new FirebaseOptions(this.zzalR, this.zzbUL, this.zzbUM, this.zzbUN, this.zzbUO, this.zzbUP);
        }

        public Builder setApiKey(@NonNull String str) {
            this.zzbUL = zzac.zzh(str, "ApiKey must be set.");
            return this;
        }

        public Builder setApplicationId(@NonNull String str) {
            this.zzalR = zzac.zzh(str, "ApplicationId must be set.");
            return this;
        }

        public Builder setDatabaseUrl(@Nullable String str) {
            this.zzbUM = str;
            return this;
        }

        public Builder setGcmSenderId(@Nullable String str) {
            this.zzbUO = str;
            return this;
        }

        public Builder setStorageBucket(@Nullable String str) {
            this.zzbUP = str;
            return this;
        }
    }

    private FirebaseOptions(@NonNull String str, @NonNull String str2, @Nullable String str3, @Nullable String str4, @Nullable String str5, @Nullable String str6) {
        zzac.zza(!zzv.zzdD(str), (Object) "ApplicationId must be set.");
        this.zzalR = str;
        this.zzbUL = str2;
        this.zzbUM = str3;
        this.zzbUN = str4;
        this.zzbUO = str5;
        this.zzbUP = str6;
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
        return zzaa.equal(this.zzalR, firebaseOptions.zzalR) && zzaa.equal(this.zzbUL, firebaseOptions.zzbUL) && zzaa.equal(this.zzbUM, firebaseOptions.zzbUM) && zzaa.equal(this.zzbUN, firebaseOptions.zzbUN) && zzaa.equal(this.zzbUO, firebaseOptions.zzbUO) && zzaa.equal(this.zzbUP, firebaseOptions.zzbUP);
    }

    public String getApiKey() {
        return this.zzbUL;
    }

    public String getApplicationId() {
        return this.zzalR;
    }

    public String getDatabaseUrl() {
        return this.zzbUM;
    }

    public String getGcmSenderId() {
        return this.zzbUO;
    }

    public String getStorageBucket() {
        return this.zzbUP;
    }

    public int hashCode() {
        return zzaa.hashCode(this.zzalR, this.zzbUL, this.zzbUM, this.zzbUN, this.zzbUO, this.zzbUP);
    }

    public String toString() {
        return zzaa.zzv(this).zzg("applicationId", this.zzalR).zzg("apiKey", this.zzbUL).zzg("databaseUrl", this.zzbUM).zzg("gcmSenderId", this.zzbUO).zzg("storageBucket", this.zzbUP).toString();
    }
}
