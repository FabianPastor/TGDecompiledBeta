package com.google.android.gms.wallet;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.identity.intents.model.CountrySpecification;
import com.google.android.gms.internal.zzbfm;
import com.google.android.gms.internal.zzbfp;
import java.util.ArrayList;

public final class MaskedWalletRequest extends zzbfm implements ReflectedParcelable {
    public static final Creator<MaskedWalletRequest> CREATOR = new zzz();
    String zzctp;
    String zzkzs;
    String zzlab;
    Cart zzlal;
    boolean zzlcf;
    boolean zzlcg;
    boolean zzlch;
    String zzlci;
    String zzlcj;
    private boolean zzlck;
    boolean zzlcl;
    private CountrySpecification[] zzlcm;
    boolean zzlcn;
    boolean zzlco;
    ArrayList<CountrySpecification> zzlcp;
    PaymentMethodTokenizationParameters zzlcq;
    ArrayList<Integer> zzlcr;

    public final class Builder {
        private /* synthetic */ MaskedWalletRequest zzlcs;

        private Builder(MaskedWalletRequest maskedWalletRequest) {
            this.zzlcs = maskedWalletRequest;
        }

        public final MaskedWalletRequest build() {
            return this.zzlcs;
        }

        public final Builder setCurrencyCode(String str) {
            this.zzlcs.zzkzs = str;
            return this;
        }

        public final Builder setEstimatedTotalPrice(String str) {
            this.zzlcs.zzlci = str;
            return this;
        }

        public final Builder setPaymentMethodTokenizationParameters(PaymentMethodTokenizationParameters paymentMethodTokenizationParameters) {
            this.zzlcs.zzlcq = paymentMethodTokenizationParameters;
            return this;
        }
    }

    MaskedWalletRequest() {
        this.zzlcn = true;
        this.zzlco = true;
    }

    MaskedWalletRequest(String str, boolean z, boolean z2, boolean z3, String str2, String str3, String str4, Cart cart, boolean z4, boolean z5, CountrySpecification[] countrySpecificationArr, boolean z6, boolean z7, ArrayList<CountrySpecification> arrayList, PaymentMethodTokenizationParameters paymentMethodTokenizationParameters, ArrayList<Integer> arrayList2, String str5) {
        this.zzlab = str;
        this.zzlcf = z;
        this.zzlcg = z2;
        this.zzlch = z3;
        this.zzlci = str2;
        this.zzkzs = str3;
        this.zzlcj = str4;
        this.zzlal = cart;
        this.zzlck = z4;
        this.zzlcl = z5;
        this.zzlcm = countrySpecificationArr;
        this.zzlcn = z6;
        this.zzlco = z7;
        this.zzlcp = arrayList;
        this.zzlcq = paymentMethodTokenizationParameters;
        this.zzlcr = arrayList2;
        this.zzctp = str5;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int zze = zzbfp.zze(parcel);
        zzbfp.zza(parcel, 2, this.zzlab, false);
        zzbfp.zza(parcel, 3, this.zzlcf);
        zzbfp.zza(parcel, 4, this.zzlcg);
        zzbfp.zza(parcel, 5, this.zzlch);
        zzbfp.zza(parcel, 6, this.zzlci, false);
        zzbfp.zza(parcel, 7, this.zzkzs, false);
        zzbfp.zza(parcel, 8, this.zzlcj, false);
        zzbfp.zza(parcel, 9, this.zzlal, i, false);
        zzbfp.zza(parcel, 10, this.zzlck);
        zzbfp.zza(parcel, 11, this.zzlcl);
        zzbfp.zza(parcel, 12, this.zzlcm, i, false);
        zzbfp.zza(parcel, 13, this.zzlcn);
        zzbfp.zza(parcel, 14, this.zzlco);
        zzbfp.zzc(parcel, 15, this.zzlcp, false);
        zzbfp.zza(parcel, 16, this.zzlcq, i, false);
        zzbfp.zza(parcel, 17, this.zzlcr, false);
        zzbfp.zza(parcel, 18, this.zzctp, false);
        zzbfp.zzai(parcel, zze);
    }
}
