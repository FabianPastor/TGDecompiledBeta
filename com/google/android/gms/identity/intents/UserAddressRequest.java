package com.google.android.gms.identity.intents;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.identity.intents.model.CountrySpecification;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public final class UserAddressRequest extends zza implements ReflectedParcelable {
    public static final Creator<UserAddressRequest> CREATOR = new zza();
    List<CountrySpecification> zzbhr;

    public final class Builder {
        final /* synthetic */ UserAddressRequest zzbhs;

        private Builder(UserAddressRequest userAddressRequest) {
            this.zzbhs = userAddressRequest;
        }

        public Builder addAllowedCountrySpecification(CountrySpecification countrySpecification) {
            if (this.zzbhs.zzbhr == null) {
                this.zzbhs.zzbhr = new ArrayList();
            }
            this.zzbhs.zzbhr.add(countrySpecification);
            return this;
        }

        public Builder addAllowedCountrySpecifications(Collection<CountrySpecification> collection) {
            if (this.zzbhs.zzbhr == null) {
                this.zzbhs.zzbhr = new ArrayList();
            }
            this.zzbhs.zzbhr.addAll(collection);
            return this;
        }

        public UserAddressRequest build() {
            if (this.zzbhs.zzbhr != null) {
                this.zzbhs.zzbhr = Collections.unmodifiableList(this.zzbhs.zzbhr);
            }
            return this.zzbhs;
        }
    }

    UserAddressRequest() {
    }

    UserAddressRequest(List<CountrySpecification> list) {
        this.zzbhr = list;
    }

    public static Builder newBuilder() {
        UserAddressRequest userAddressRequest = new UserAddressRequest();
        userAddressRequest.getClass();
        return new Builder();
    }

    public void writeToParcel(Parcel parcel, int i) {
        zza.zza(this, parcel, i);
    }
}
