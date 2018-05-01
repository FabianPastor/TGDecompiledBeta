package com.google.firebase.iid;

import android.os.Build.VERSION;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.RemoteException;
import android.util.Log;
import com.google.android.gms.internal.measurement.zzdu;
import com.google.android.gms.internal.measurement.zzdv;

public class zzi
  implements Parcelable
{
  public static final Parcelable.Creator<zzi> CREATOR = new zzj();
  private Messenger zzbqg;
  private zzdu zzbqh;
  
  public zzi(IBinder paramIBinder)
  {
    if (Build.VERSION.SDK_INT >= 21) {
      this.zzbqg = new Messenger(paramIBinder);
    }
    for (;;)
    {
      return;
      this.zzbqh = zzdv.zzb(paramIBinder);
    }
  }
  
  private final IBinder getBinder()
  {
    if (this.zzbqg != null) {}
    for (IBinder localIBinder = this.zzbqg.getBinder();; localIBinder = this.zzbqh.asBinder()) {
      return localIBinder;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = false;
    if (paramObject == null) {}
    for (;;)
    {
      return bool1;
      try
      {
        boolean bool2 = getBinder().equals(((zzi)paramObject).getBinder());
        bool1 = bool2;
      }
      catch (ClassCastException paramObject) {}
    }
  }
  
  public int hashCode()
  {
    return getBinder().hashCode();
  }
  
  public final void send(Message paramMessage)
    throws RemoteException
  {
    if (this.zzbqg != null) {
      this.zzbqg.send(paramMessage);
    }
    for (;;)
    {
      return;
      this.zzbqh.send(paramMessage);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    if (this.zzbqg != null) {
      paramParcel.writeStrongBinder(this.zzbqg.getBinder());
    }
    for (;;)
    {
      return;
      paramParcel.writeStrongBinder(this.zzbqh.asBinder());
    }
  }
  
  public static final class zza
    extends ClassLoader
  {
    protected final Class<?> loadClass(String paramString, boolean paramBoolean)
      throws ClassNotFoundException
    {
      if ("com.google.android.gms.iid.MessengerCompat".equals(paramString)) {
        if (FirebaseInstanceId.zzsj()) {
          Log.d("FirebaseInstanceId", "Using renamed FirebaseIidMessengerCompat class");
        }
      }
      for (paramString = zzi.class;; paramString = super.loadClass(paramString, paramBoolean)) {
        return paramString;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/firebase/iid/zzi.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */