package com.google.android.gms.wearable;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.common.internal.ReflectedParcelable;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;

public class ConnectionConfiguration
  extends AbstractSafeParcelable
  implements ReflectedParcelable
{
  public static final Parcelable.Creator<ConnectionConfiguration> CREATOR = new zzg();
  private final String name;
  private final int type;
  private final String zzi;
  private final int zzj;
  private final boolean zzk;
  private volatile boolean zzl;
  private volatile String zzm;
  private boolean zzn;
  private String zzo;
  
  ConnectionConfiguration(String paramString1, String paramString2, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2, String paramString3, boolean paramBoolean3, String paramString4)
  {
    this.name = paramString1;
    this.zzi = paramString2;
    this.type = paramInt1;
    this.zzj = paramInt2;
    this.zzk = paramBoolean1;
    this.zzl = paramBoolean2;
    this.zzm = paramString3;
    this.zzn = paramBoolean3;
    this.zzo = paramString4;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool1 = false;
    boolean bool2;
    if (!(paramObject instanceof ConnectionConfiguration)) {
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      paramObject = (ConnectionConfiguration)paramObject;
      bool2 = bool1;
      if (Objects.equal(this.name, ((ConnectionConfiguration)paramObject).name))
      {
        bool2 = bool1;
        if (Objects.equal(this.zzi, ((ConnectionConfiguration)paramObject).zzi))
        {
          bool2 = bool1;
          if (Objects.equal(Integer.valueOf(this.type), Integer.valueOf(((ConnectionConfiguration)paramObject).type)))
          {
            bool2 = bool1;
            if (Objects.equal(Integer.valueOf(this.zzj), Integer.valueOf(((ConnectionConfiguration)paramObject).zzj)))
            {
              bool2 = bool1;
              if (Objects.equal(Boolean.valueOf(this.zzk), Boolean.valueOf(((ConnectionConfiguration)paramObject).zzk)))
              {
                bool2 = bool1;
                if (Objects.equal(Boolean.valueOf(this.zzn), Boolean.valueOf(((ConnectionConfiguration)paramObject).zzn))) {
                  bool2 = true;
                }
              }
            }
          }
        }
      }
    }
  }
  
  public int hashCode()
  {
    return Objects.hashCode(new Object[] { this.name, this.zzi, Integer.valueOf(this.type), Integer.valueOf(this.zzj), Boolean.valueOf(this.zzk), Boolean.valueOf(this.zzn) });
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder("ConnectionConfiguration[ ");
    String str = String.valueOf(this.name);
    if (str.length() != 0)
    {
      str = "mName=".concat(str);
      localStringBuilder.append(str);
      str = String.valueOf(this.zzi);
      if (str.length() == 0) {
        break label308;
      }
      str = ", mAddress=".concat(str);
      label60:
      localStringBuilder.append(str);
      int i = this.type;
      localStringBuilder.append(19 + ", mType=" + i);
      i = this.zzj;
      localStringBuilder.append(19 + ", mRole=" + i);
      boolean bool = this.zzk;
      localStringBuilder.append(16 + ", mEnabled=" + bool);
      bool = this.zzl;
      localStringBuilder.append(20 + ", mIsConnected=" + bool);
      str = String.valueOf(this.zzm);
      if (str.length() == 0) {
        break label321;
      }
      str = ", mPeerNodeId=".concat(str);
      label216:
      localStringBuilder.append(str);
      bool = this.zzn;
      localStringBuilder.append(21 + ", mBtlePriority=" + bool);
      str = String.valueOf(this.zzo);
      if (str.length() == 0) {
        break label334;
      }
    }
    label308:
    label321:
    label334:
    for (str = ", mNodeId=".concat(str);; str = new String(", mNodeId="))
    {
      localStringBuilder.append(str);
      localStringBuilder.append("]");
      return localStringBuilder.toString();
      str = new String("mName=");
      break;
      str = new String(", mAddress=");
      break label60;
      str = new String(", mPeerNodeId=");
      break label216;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramInt = SafeParcelWriter.beginObjectHeader(paramParcel);
    SafeParcelWriter.writeString(paramParcel, 2, this.name, false);
    SafeParcelWriter.writeString(paramParcel, 3, this.zzi, false);
    SafeParcelWriter.writeInt(paramParcel, 4, this.type);
    SafeParcelWriter.writeInt(paramParcel, 5, this.zzj);
    SafeParcelWriter.writeBoolean(paramParcel, 6, this.zzk);
    SafeParcelWriter.writeBoolean(paramParcel, 7, this.zzl);
    SafeParcelWriter.writeString(paramParcel, 8, this.zzm, false);
    SafeParcelWriter.writeBoolean(paramParcel, 9, this.zzn);
    SafeParcelWriter.writeString(paramParcel, 10, this.zzo, false);
    SafeParcelWriter.finishObjectHeader(paramParcel, paramInt);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/ConnectionConfiguration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */