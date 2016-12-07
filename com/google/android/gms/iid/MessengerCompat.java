package com.google.android.gms.iid;

import android.annotation.TargetApi;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import com.google.android.gms.common.internal.ReflectedParcelable;

public class MessengerCompat implements ReflectedParcelable {
    public static final Creator<MessengerCompat> CREATOR = new Creator<MessengerCompat>() {
        public /* synthetic */ Object createFromParcel(Parcel parcel) {
            return zzgn(parcel);
        }

        public /* synthetic */ Object[] newArray(int i) {
            return zzjB(i);
        }

        public MessengerCompat zzgn(Parcel parcel) {
            IBinder readStrongBinder = parcel.readStrongBinder();
            return readStrongBinder != null ? new MessengerCompat(readStrongBinder) : null;
        }

        public MessengerCompat[] zzjB(int i) {
            return new MessengerCompat[i];
        }
    };
    Messenger zzbho;
    zzb zzbhp;

    private final class zza extends com.google.android.gms.iid.zzb.zza {
        Handler handler;

        zza(MessengerCompat messengerCompat, Handler handler) {
            this.handler = handler;
        }

        public void send(Message message) throws RemoteException {
            message.arg2 = Binder.getCallingUid();
            this.handler.dispatchMessage(message);
        }
    }

    public MessengerCompat(Handler handler) {
        if (VERSION.SDK_INT >= 21) {
            this.zzbho = new Messenger(handler);
        } else {
            this.zzbhp = new zza(this, handler);
        }
    }

    public MessengerCompat(IBinder iBinder) {
        if (VERSION.SDK_INT >= 21) {
            this.zzbho = new Messenger(iBinder);
        } else {
            this.zzbhp = com.google.android.gms.iid.zzb.zza.zzcZ(iBinder);
        }
    }

    public static int zzc(Message message) {
        return VERSION.SDK_INT >= 21 ? zzd(message) : message.arg2;
    }

    @TargetApi(21)
    private static int zzd(Message message) {
        return message.sendingUid;
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
        return this.zzbho != null ? this.zzbho.getBinder() : this.zzbhp.asBinder();
    }

    public int hashCode() {
        return getBinder().hashCode();
    }

    public void send(Message message) throws RemoteException {
        if (this.zzbho != null) {
            this.zzbho.send(message);
        } else {
            this.zzbhp.send(message);
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.zzbho != null) {
            parcel.writeStrongBinder(this.zzbho.getBinder());
        } else {
            parcel.writeStrongBinder(this.zzbhp.asBinder());
        }
    }
}
