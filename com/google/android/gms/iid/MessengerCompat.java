package com.google.android.gms.iid;

import android.annotation.TargetApi;
import android.os.Binder;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;

public class MessengerCompat implements Parcelable {
    public static final Creator<MessengerCompat> CREATOR = new Creator<MessengerCompat>() {
        public /* synthetic */ Object createFromParcel(Parcel parcel) {
            return zznc(parcel);
        }

        public /* synthetic */ Object[] newArray(int i) {
            return zztu(i);
        }

        public MessengerCompat zznc(Parcel parcel) {
            IBinder readStrongBinder = parcel.readStrongBinder();
            return readStrongBinder != null ? new MessengerCompat(readStrongBinder) : null;
        }

        public MessengerCompat[] zztu(int i) {
            return new MessengerCompat[i];
        }
    };
    Messenger agg;
    zzb agh;

    private final class zza extends com.google.android.gms.iid.zzb.zza {
        final /* synthetic */ MessengerCompat agi;
        Handler handler;

        zza(MessengerCompat messengerCompat, Handler handler) {
            this.agi = messengerCompat;
            this.handler = handler;
        }

        public void send(Message message) throws RemoteException {
            message.arg2 = Binder.getCallingUid();
            this.handler.dispatchMessage(message);
        }
    }

    public MessengerCompat(Handler handler) {
        if (VERSION.SDK_INT >= 21) {
            this.agg = new Messenger(handler);
        } else {
            this.agh = new zza(this, handler);
        }
    }

    public MessengerCompat(IBinder iBinder) {
        if (VERSION.SDK_INT >= 21) {
            this.agg = new Messenger(iBinder);
        } else {
            this.agh = com.google.android.gms.iid.zzb.zza.zzgw(iBinder);
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
        return this.agg != null ? this.agg.getBinder() : this.agh.asBinder();
    }

    public int hashCode() {
        return getBinder().hashCode();
    }

    public void send(Message message) throws RemoteException {
        if (this.agg != null) {
            this.agg.send(message);
        } else {
            this.agh.send(message);
        }
    }

    public void writeToParcel(Parcel parcel, int i) {
        if (this.agg != null) {
            parcel.writeStrongBinder(this.agg.getBinder());
        } else {
            parcel.writeStrongBinder(this.agh.asBinder());
        }
    }
}
