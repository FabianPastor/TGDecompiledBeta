package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor.AutoCloseOutputStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.internal.zzn;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.wearable.Channel.GetOutputStreamResult;
import java.io.OutputStream;

final class zzgt extends zzgm<GetOutputStreamResult> {
    private final zzbr zzllf;

    public zzgt(zzn<GetOutputStreamResult> com_google_android_gms_common_api_internal_zzn_com_google_android_gms_wearable_Channel_GetOutputStreamResult, zzbr com_google_android_gms_wearable_internal_zzbr) {
        super(com_google_android_gms_common_api_internal_zzn_com_google_android_gms_wearable_Channel_GetOutputStreamResult);
        this.zzllf = (zzbr) zzbq.checkNotNull(com_google_android_gms_wearable_internal_zzbr);
    }

    public final void zza(zzdo com_google_android_gms_wearable_internal_zzdo) {
        OutputStream outputStream = null;
        if (com_google_android_gms_wearable_internal_zzdo.zzlkg != null) {
            outputStream = new zzbl(new AutoCloseOutputStream(com_google_android_gms_wearable_internal_zzdo.zzlkg));
            this.zzllf.zza(new zzbm(outputStream));
        }
        zzav(new zzbh(new Status(com_google_android_gms_wearable_internal_zzdo.statusCode), outputStream));
    }
}
