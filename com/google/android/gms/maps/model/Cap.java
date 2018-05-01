package com.google.android.gms.maps.model;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import com.google.android.gms.common.internal.safeparcel.zza;
import com.google.android.gms.common.internal.safeparcel.zzd;
import com.google.android.gms.common.internal.zzbe;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.dynamic.IObjectWrapper;
import java.util.Arrays;

public class Cap
  extends zza
{
  public static final Parcelable.Creator<Cap> CREATOR = new zzb();
  private static final String TAG = Cap.class.getSimpleName();
  @Nullable
  private final BitmapDescriptor bitmapDescriptor;
  private final int type;
  @Nullable
  private final Float zzbng;
  
  protected Cap(int paramInt)
  {
    this(paramInt, null, null);
  }
  
  Cap(int paramInt, @Nullable IBinder paramIBinder, @Nullable Float paramFloat) {}
  
  private Cap(int paramInt, @Nullable BitmapDescriptor paramBitmapDescriptor, @Nullable Float paramFloat)
  {
    if ((paramFloat != null) && (paramFloat.floatValue() > 0.0F)) {}
    for (int i = 1;; i = 0)
    {
      boolean bool1;
      if (paramInt == 3)
      {
        bool1 = bool2;
        if (paramBitmapDescriptor != null)
        {
          bool1 = bool2;
          if (i == 0) {}
        }
      }
      else
      {
        bool1 = true;
      }
      String str1 = String.valueOf(paramBitmapDescriptor);
      String str2 = String.valueOf(paramFloat);
      zzbo.zzb(bool1, String.valueOf(str1).length() + 63 + String.valueOf(str2).length() + "Invalid Cap: type=" + paramInt + " bitmapDescriptor=" + str1 + " bitmapRefWidth=" + str2);
      this.type = paramInt;
      this.bitmapDescriptor = paramBitmapDescriptor;
      this.zzbng = paramFloat;
      return;
    }
  }
  
  protected Cap(@NonNull BitmapDescriptor paramBitmapDescriptor, float paramFloat)
  {
    this(3, paramBitmapDescriptor, Float.valueOf(paramFloat));
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if (!(paramObject instanceof Cap)) {
        return false;
      }
      paramObject = (Cap)paramObject;
    } while ((this.type == ((Cap)paramObject).type) && (zzbe.equal(this.bitmapDescriptor, ((Cap)paramObject).bitmapDescriptor)) && (zzbe.equal(this.zzbng, ((Cap)paramObject).zzbng)));
    return false;
  }
  
  public int hashCode()
  {
    return Arrays.hashCode(new Object[] { Integer.valueOf(this.type), this.bitmapDescriptor, this.zzbng });
  }
  
  public String toString()
  {
    int i = this.type;
    return 23 + "[Cap: type=" + i + "]";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = zzd.zze(paramParcel);
    zzd.zzc(paramParcel, 2, this.type);
    if (this.bitmapDescriptor == null) {}
    for (IBinder localIBinder = null;; localIBinder = this.bitmapDescriptor.zzwe().asBinder())
    {
      zzd.zza(paramParcel, 3, localIBinder, false);
      zzd.zza(paramParcel, 4, this.zzbng, false);
      zzd.zzI(paramParcel, paramInt);
      return;
    }
  }
  
  final Cap zzwk()
  {
    switch (this.type)
    {
    default: 
      String str = TAG;
      int i = this.type;
      Log.w(str, 29 + "Unknown Cap type: " + i);
      return this;
    case 0: 
      return new ButtCap();
    case 1: 
      return new SquareCap();
    case 2: 
      return new RoundCap();
    }
    return new CustomCap(this.bitmapDescriptor, this.zzbng.floatValue());
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/maps/model/Cap.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */