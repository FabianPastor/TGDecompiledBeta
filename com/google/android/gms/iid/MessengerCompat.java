package com.google.android.gms.iid;

import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.iid.zzb.zza;

public class MessengerCompat implements ReflectedParcelable {
    public static final Creator<MessengerCompat> CREATOR = new Creator<MessengerCompat>() {
        public /* synthetic */ Object createFromParcel(Parcel parcel) {
            return zzgs(parcel);
        }

        public /* synthetic */ Object[] newArray(int i) {
            return zzjK(i);
        }

        public MessengerCompat zzgs(Parcel parcel) {
            IBinder readStrongBinder = parcel.readStrongBinder();
            return readStrongBinder != null ? new MessengerCompat(readStrongBinder) : null;
        }

        public MessengerCompat[] zzjK(int i) {
            return new MessengerCompat[i];
        }
    };
    Messenger zzbhO;
    zzb zzbhP;

    public MessengerCompat(IBinder iBinder) {
        if (VERSION.SDK_INT >= 21) {
            this.zzbhO = new Messenger(iBinder);
        } else {
            this.zzbhP = zza.zzcZ(iBinder);
        }
    }

    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (obj != null) {
            try {
                z = getBinder().equals(((MessengerCompat) obj).getBinder());
            } catch (ClassCastException e) {
            }
        }
        return z;
    }

    public IBinder getBinder() {
        return this.zzbhO != null ? this.zzbhO.getBinder() : this.zzbhP.asBinder();
    }

    public int hashCode() {
        return getBinder().hashCode();
    }

    public void send(Message message) throws RemoteException {
        if (this.zzbhO != null) {
            this.zzbhO.send(message);
        } else {
            this.zzbhP.send(message);
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.zzbhO != null) {
            parcel.writeStrongBinder(this.zzbhO.getBinder());
        } else {
            parcel.writeStrongBinder(this.zzbhP.asBinder());
        }
    }
}
