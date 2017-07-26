package com.google.android.gms.wearable.internal;

import android.os.ParcelFileDescriptor.AutoCloseInputStream;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.internal.zzbaz;
import com.google.android.gms.wearable.Channel.GetInputStreamResult;
import java.io.InputStream;

final class zzfi extends zzfc<GetInputStreamResult> {
    private final zzbd zzbTf;

    public zzfi(zzbaz<GetInputStreamResult> com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_Channel_GetInputStreamResult, zzbd com_google_android_gms_wearable_internal_zzbd) {
        super(com_google_android_gms_internal_zzbaz_com_google_android_gms_wearable_Channel_GetInputStreamResult);
        this.zzbTf = (zzbd) zzbo.zzu(com_google_android_gms_wearable_internal_zzbd);
    }

    public final void zza(zzck com_google_android_gms_wearable_internal_zzck) {
        InputStream inputStream = null;
        if (com_google_android_gms_wearable_internal_zzck.zzbSI != null) {
            inputStream = new zzav(new AutoCloseInputStream(com_google_android_gms_wearable_internal_zzck.zzbSI));
            this.zzbTf.zza(new zzaw(inputStream));
        }
        zzR(new zzas(new Status(com_google_android_gms_wearable_internal_zzck.statusCode), inputStream));
    }
}
