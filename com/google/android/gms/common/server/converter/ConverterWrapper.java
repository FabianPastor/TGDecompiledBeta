package com.google.android.gms.common.server.converter;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.server.response.FastJsonResponse.zza;

public class ConverterWrapper extends AbstractSafeParcelable {
    public static final Creator<ConverterWrapper> CREATOR = new zza();
    private final StringToIntConverter Fa;
    final int mVersionCode;

    ConverterWrapper(int i, StringToIntConverter stringToIntConverter) {
        this.mVersionCode = i;
        this.Fa = stringToIntConverter;
    }

    private ConverterWrapper(StringToIntConverter stringToIntConverter) {
        this.mVersionCode = 1;
        this.Fa = stringToIntConverter;
    }

    public static ConverterWrapper zza(zza<?, ?> com_google_android_gms_common_server_response_FastJsonResponse_zza___) {
        if (com_google_android_gms_common_server_response_FastJsonResponse_zza___ instanceof StringToIntConverter) {
            return new ConverterWrapper((StringToIntConverter) com_google_android_gms_common_server_response_FastJsonResponse_zza___);
        }
        throw new IllegalArgumentException("Unsupported safe parcelable field converter class.");
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }

    StringToIntConverter zzaww() {
        return this.Fa;
    }

    public zza<?, ?> zzawx() {
        if (this.Fa != null) {
            return this.Fa;
        }
        throw new IllegalStateException("There was no converter wrapped in this ConverterWrapper.");
    }
}
