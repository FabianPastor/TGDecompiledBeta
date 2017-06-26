package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbay;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;
import java.io.InputStream;

final class zzfi extends zzfc<GetInputStreamResult> {
    private final zzbd zzbTd;

    public zzfi(zzbay<GetInputStreamResult> com_google_android_gms_internal_zzbay_com_google_android_gms_wearable_Channel_GetInputStreamResult, zzbd com_google_android_gms_wearable_internal_zzbd) {
        super(com_google_android_gms_internal_zzbay_com_google_android_gms_wearable_Channel_GetInputStreamResult);
        this.zzbTd = (zzbd) zzbo.zzu(com_google_android_gms_wearable_internal_zzbd);
    }

    public final void zza(zzck com_google_android_gms_wearable_internal_zzck) {
        InputStream inputStream = null;
        if (com_google_android_gms_wearable_internal_zzck.zzbSG != null) {
            inputStream = new zzav(new AutoCloseInputStream(com_google_android_gms_wearable_internal_zzck.zzbSG));
            this.zzbTd.zza(new zzaw(inputStream));
        }
        zzR(new zzas(new Status(com_google_android_gms_wearable_internal_zzck.statusCode), inputStream));
    }
}
