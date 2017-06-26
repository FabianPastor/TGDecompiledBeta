package com.google.android.gms.iid;

import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.common.internal.ReflectedParcelable;

public class MessengerCompat implements ReflectedParcelable {
    public static final Creator<MessengerCompat> CREATOR = new zzd();
    private Messenger zzbgX;
    private zzb zzbgY;

    public MessengerCompat(IBinder iBinder) {
        if (VERSION.SDK_INT >= 21) {
            this.zzbgX = new Messenger(iBinder);
            return;
        }
        zzb com_google_android_gms_iid_zzb;
        if (iBinder == null) {
            com_google_android_gms_iid_zzb = null;
        } else {
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.iid.IMessengerCompat");
            com_google_android_gms_iid_zzb = queryLocalInterface instanceof zzb ? (zzb) queryLocalInterface : new zzc(iBinder);
        }
        this.zzbgY = com_google_android_gms_iid_zzb;
    }

    private final IBinder getBinder() {
        return this.zzbgX != null ? this.zzbgX.getBinder() : this.zzbgY.asBinder();
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

    public int hashCode() {
        return getBinder().hashCode();
    }

    public final void send(Message message) throws RemoteException {
        if (this.zzbgX != null) {
            this.zzbgX.send(message);
        } else {
            this.zzbgY.send(message);
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.zzbgX != null) {
            parcel.writeStrongBinder(this.zzbgX.getBinder());
        } else {
            parcel.writeStrongBinder(this.zzbgY.asBinder());
        }
    }
}
