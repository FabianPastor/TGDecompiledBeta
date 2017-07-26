package com.google.firebase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.common.internal.zzby;
import com.google.android.gms.common.util.zzt;
import java.util.Arrays;

public final class FirebaseOptions {
    private final String zzaoM;
    private final String zzbVj;
    private final String zzbVk;
    private final String zzbVl;
    private final String zzbVm;
    private final String zzbVn;
    private final String zzbVo;

    public static final class Builder {
        private String zzaoM;
        private String zzbVj;
        private String zzbVk;
        private String zzbVl;
        private String zzbVm;
        private String zzbVn;
        private String zzbVo;

        public Builder(FirebaseOptions firebaseOptions) {
            this.zzaoM = firebaseOptions.zzaoM;
            this.zzbVj = firebaseOptions.zzbVj;
            this.zzbVk = firebaseOptions.zzbVk;
            this.zzbVl = firebaseOptions.zzbVl;
            this.zzbVm = firebaseOptions.zzbVm;
            this.zzbVn = firebaseOptions.zzbVn;
            this.zzbVo = firebaseOptions.zzbVo;
        }

        public final FirebaseOptions build() {
            return new FirebaseOptions(this.zzaoM, this.zzbVj, this.zzbVk, this.zzbVl, this.zzbVm, this.zzbVn, this.zzbVo);
        }

        public final Builder setApiKey(@NonNull String str) {
            this.zzbVj = zzbo.zzh(str, "ApiKey must be set.");
            return this;
        }

        public final Builder setApplicationId(@NonNull String str) {
            this.zzaoM = zzbo.zzh(str, "ApplicationId must be set.");
            return this;
        }

        public final Builder setDatabaseUrl(@Nullable String str) {
            this.zzbVk = str;
            return this;
        }

        public final Builder setGcmSenderId(@Nullable String str) {
            this.zzbVm = str;
            return this;
        }

        public final Builder setProjectId(@Nullable String str) {
            this.zzbVo = str;
            return this;
        }

        public final Builder setStorageBucket(@Nullable String str) {
            this.zzbVn = str;
            return this;
        }
    }

    private FirebaseOptions(@NonNull String str, @NonNull String str2, @Nullable String str3, @Nullable String str4, @Nullable String str5, @Nullable String str6, @Nullable String str7) {
        zzbo.zza(!zzt.zzcL(str), (Object) "ApplicationId must be set.");
        this.zzaoM = str;
        this.zzbVj = str2;
        this.zzbVk = str3;
        this.zzbVl = str4;
        this.zzbVm = str5;
        this.zzbVn = str6;
        this.zzbVo = str7;
    }

    public static FirebaseOptions fromResource(Context context) {
        zzby com_google_android_gms_common_internal_zzby = new zzby(context);
        Object string = com_google_android_gms_common_internal_zzby.getString("google_app_id");
        return TextUtils.isEmpty(string) ? null : new FirebaseOptions(string, com_google_android_gms_common_internal_zzby.getString("google_api_key"), com_google_android_gms_common_internal_zzby.getString("firebase_database_url"), com_google_android_gms_common_internal_zzby.getString("ga_trackingId"), com_google_android_gms_common_internal_zzby.getString("gcm_defaultSenderId"), com_google_android_gms_common_internal_zzby.getString("google_storage_bucket"), com_google_android_gms_common_internal_zzby.getString("project_id"));
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof FirebaseOptions)) {
            return false;
        }
        FirebaseOptions firebaseOptions = (FirebaseOptions) obj;
        return zzbe.equal(this.zzaoM, firebaseOptions.zzaoM) && zzbe.equal(this.zzbVj, firebaseOptions.zzbVj) && zzbe.equal(this.zzbVk, firebaseOptions.zzbVk) && zzbe.equal(this.zzbVl, firebaseOptions.zzbVl) && zzbe.equal(this.zzbVm, firebaseOptions.zzbVm) && zzbe.equal(this.zzbVn, firebaseOptions.zzbVn) && zzbe.equal(this.zzbVo, firebaseOptions.zzbVo);
    }

    public final String getApiKey() {
        return this.zzbVj;
    }

    public final String getApplicationId() {
        return this.zzaoM;
    }

    public final String getDatabaseUrl() {
        return this.zzbVk;
    }

    public final String getGcmSenderId() {
        return this.zzbVm;
    }

    public final String getProjectId() {
        return this.zzbVo;
    }

    public final String getStorageBucket() {
        return this.zzbVn;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{this.zzaoM, this.zzbVj, this.zzbVk, this.zzbVl, this.zzbVm, this.zzbVn, this.zzbVo});
    }

    public final String toString() {
        return zzbe.zzt(this).zzg("applicationId", this.zzaoM).zzg("apiKey", this.zzbVj).zzg("databaseUrl", this.zzbVk).zzg("gcmSenderId", this.zzbVm).zzg("storageBucket", this.zzbVn).zzg("projectId", this.zzbVo).toString();
    }
}
