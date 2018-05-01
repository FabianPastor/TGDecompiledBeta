package org.telegram.messenger.exoplayer2.video;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class ColorInfo
  implements Parcelable
{
  public static final Parcelable.Creator<ColorInfo> CREATOR = new Parcelable.Creator()
  {
    public ColorInfo createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ColorInfo(paramAnonymousParcel);
    }
    
    public ColorInfo[] newArray(int paramAnonymousInt)
    {
      return new ColorInfo[0];
    }
  };
  public final int colorRange;
  public final int colorSpace;
  public final int colorTransfer;
  private int hashCode;
  public final byte[] hdrStaticInfo;
  
  public ColorInfo(int paramInt1, int paramInt2, int paramInt3, byte[] paramArrayOfByte)
  {
    this.colorSpace = paramInt1;
    this.colorRange = paramInt2;
    this.colorTransfer = paramInt3;
    this.hdrStaticInfo = paramArrayOfByte;
  }
  
  ColorInfo(Parcel paramParcel)
  {
    this.colorSpace = paramParcel.readInt();
    this.colorRange = paramParcel.readInt();
    this.colorTransfer = paramParcel.readInt();
    int i;
    if (paramParcel.readInt() != 0)
    {
      i = 1;
      if (i == 0) {
        break label57;
      }
    }
    label57:
    for (paramParcel = paramParcel.createByteArray();; paramParcel = null)
    {
      this.hdrStaticInfo = paramParcel;
      return;
      i = 0;
      break;
    }
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (ColorInfo)paramObject;
        if ((this.colorSpace != ((ColorInfo)paramObject).colorSpace) || (this.colorRange != ((ColorInfo)paramObject).colorRange) || (this.colorTransfer != ((ColorInfo)paramObject).colorTransfer) || (!Arrays.equals(this.hdrStaticInfo, ((ColorInfo)paramObject).hdrStaticInfo))) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    if (this.hashCode == 0) {
      this.hashCode = ((((this.colorSpace + 527) * 31 + this.colorRange) * 31 + this.colorTransfer) * 31 + Arrays.hashCode(this.hdrStaticInfo));
    }
    return this.hashCode;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append("ColorInfo(").append(this.colorSpace).append(", ").append(this.colorRange).append(", ").append(this.colorTransfer).append(", ");
    if (this.hdrStaticInfo != null) {}
    for (boolean bool = true;; bool = false) {
      return bool + ")";
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.colorSpace);
    paramParcel.writeInt(this.colorRange);
    paramParcel.writeInt(this.colorTransfer);
    if (this.hdrStaticInfo != null) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      if (this.hdrStaticInfo != null) {
        paramParcel.writeByteArray(this.hdrStaticInfo);
      }
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/video/ColorInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */