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
            return zzck(parcel);
        }

        public /* synthetic */ Object[] newArray(int i) {
            return zzgl(i);
        }

        public BinderWrapper zzck(Parcel parcel) {
            return new BinderWrapper(parcel);
        }

        public BinderWrapper[] zzgl(int i) {
            return new BinderWrapper[i];
        }
    };
    private IBinder DI;

    public BinderWrapper() {
        this.DI = null;
    }

    public BinderWrapper(IBinder iBinder) {
        this.DI = null;
        this.DI = iBinder;
    }

    private BinderWrapper(Parcel parcel) {
        this.DI = null;
        this.DI = parcel.readStrongBinder();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStrongBinder(this.DI);
    }
}
