package com.google.android.gms.wearable.internal;

import android.content.IntentFilter;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import javax.annotation.Nullable;

public final class zzd
  extends AbstractSafeParcelable
{
  public static final Parcelable.Creator<zzd> CREATOR = new zze();
  private final zzem zzaz;
  private final IntentFilter[] zzba;
  @Nullable
  private final String zzbb;
  @Nullable
  private final String zzbc;
  
  zzd(IBinder paramIBinder, IntentFilter[] paramArrayOfIntentFilter, @Nullable String paramString1, @Nullable String paramString2)
  {
    if (paramIBinder != null) {
      if (paramIBinder == null) {
        paramIBinder = localIInterface;
      }
    }
    for (this.zzaz = paramIBinder;; this.zzaz = null)
    {
      this.zzba = paramArrayOfIntentFilter;
      this.zzbb = paramString1;
      this.zzbc = paramString2;
      return;
      localIInterface = paramIBinder.queryLocalInterface("com.google.android.gms.wearable.internal.IWearableListener");
      if ((localIInterface instanceof zzem))
      {
        paramIBinder = (zzem)localIInterface;
        break;
      }
      paramIBinder = new zzeo(paramIBinder);
      break;
    }
  }
  
  public zzd(zzhk paramzzhk)
  {
    this.zzaz = paramzzhk;
    this.zzba = paramzzhk.zze();
    this.zzbb = paramzzhk.zzf();
    this.zzbc = null;
  }
  
  public final void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = SafeParcelWriter.beginObjectHeader(paramParcel);
    if (this.zzaz == null) {}
    for (IBinder localIBinder = null;; localIBinder = this.zzaz.asBinder())
    {
      SafeParcelWriter.writeIBinder(paramParcel, 2, localIBinder, false);
      SafeParcelWriter.writeTypedArray(paramParcel, 3, this.zzba, paramInt, false);
      SafeParcelWriter.writeString(paramParcel, 4, this.zzbb, false);
      SafeParcelWriter.writeString(paramParcel, 5, this.zzbc, false);
      SafeParcelWriter.finishObjectHeader(paramParcel, i);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzd.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */