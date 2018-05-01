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

public class MessengerCompat
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<MessengerCompat> CREATOR = new zzk();
  private Messenger zzifn;
  private zzi zzifo;
  
  public MessengerCompat(IBinder paramIBinder)
  {
    if (Build.VERSION.SDK_INT >= 21)
    {
      this.zzifn = new Messenger(paramIBinder);
      return;
    }
    if (paramIBinder == null) {
      paramIBinder = null;
    }
    for (;;)
    {
      this.zzifo = paramIBinder;
      return;
      IInterface localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.iid.IMessengerCompat");
      if ((localIInterface instanceof zzi)) {
        paramIBinder = (zzi)localIInterface;
      } else {
        paramIBinder = new zzj(paramIBinder);
      }
    }
  }
  
  private final IBinder getBinder()
  {
    if (this.zzifn != null) {
      return this.zzifn.getBinder();
    }
    return this.zzifo.asBinder();
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (paramObject == null) {
      return false;
    }
    try
    {
      boolean bool = getBinder().equals(((MessengerCompat)paramObject).getBinder());
      return bool;
    }
    catch (ClassCastException paramObject) {}
    return false;
  }
  
  public int hashCode()
  {
    return getBinder().hashCode();
  }
  
  public final void send(Message paramMessage)
    throws RemoteException
  {
    if (this.zzifn != null)
    {
      this.zzifn.send(paramMessage);
      return;
    }
    this.zzifo.send(paramMessage);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.zzifn != null)
    {
      paramParcel.writeStrongBinder(this.zzifn.getBinder());
      return;
    }
    paramParcel.writeStrongBinder(this.zzifo.asBinder());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/iid/MessengerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */