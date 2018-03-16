package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.zzn;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;
import java.io.InputStream;

final class zzgs extends zzgm<GetInputStreamResult> {
    private final zzbr zzllf;

    public zzgs(zzn<GetInputStreamResult> com_google_android_gms_common_api_internal_zzn_com_google_android_gms_wearable_Channel_GetInputStreamResult, zzbr com_google_android_gms_wearable_internal_zzbr) {
        super(com_google_android_gms_common_api_internal_zzn_com_google_android_gms_wearable_Channel_GetInputStreamResult);
        this.zzllf = (zzbr) zzbq.checkNotNull(com_google_android_gms_wearable_internal_zzbr);
    }

    public final void zza(zzdm com_google_android_gms_wearable_internal_zzdm) {
        InputStream inputStream = null;
        if (com_google_android_gms_wearable_internal_zzdm.zzlkg != null) {
            inputStream = new zzbj(new AutoCloseInputStream(com_google_android_gms_wearable_internal_zzdm.zzlkg));
            this.zzllf.zza(new zzbk(inputStream));
        }
        zzav(new zzbg(new Status(com_google_android_gms_wearable_internal_zzdm.statusCode), inputStream));
    }
}
