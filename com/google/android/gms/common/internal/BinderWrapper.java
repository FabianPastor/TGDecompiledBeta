package com.google.android.gms.common.internal;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.annotation.KeepName;

@KeepName
public final class BinderWrapper implements Parcelable {
    public static final Creator<BinderWrapper> CREATOR = new Creator<BinderWrapper>() {
        public /* synthetic */ Object createFromParcel(Parcel parcel) {
            return zzcj(parcel);
        }

        public /* synthetic */ Object[] newArray(int i) {
            return zzgm(i);
        }

        public BinderWrapper zzcj(Parcel parcel) {
            return new BinderWrapper(parcel);
        }

        public BinderWrapper[] zzgm(int i) {
            return new BinderWrapper[i];
        }
    };
    private IBinder Bz;

    public BinderWrapper() {
        this.Bz = null;
    }

    public BinderWrapper(IBinder iBinder) {
        this.Bz = null;
        this.Bz = iBinder;
    }

    private BinderWrapper(Parcel parcel) {
        this.Bz = null;
        this.Bz = parcel.readStrongBinder();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.Bz);
    }
}
