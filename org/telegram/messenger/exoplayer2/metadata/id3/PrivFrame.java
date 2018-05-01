package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Util;

public final class PrivFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<PrivFrame> CREATOR = new Parcelable.Creator()
  {
    public PrivFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new PrivFrame(paramAnonymousParcel);
    }
    
    public PrivFrame[] newArray(int paramAnonymousInt)
    {
      return new PrivFrame[paramAnonymousInt];
    }
  };
  public static final String ID = "PRIV";
  public final String owner;
  public final byte[] privateData;
  
  PrivFrame(Parcel paramParcel)
  {
    super("PRIV");
    this.owner = paramParcel.readString();
    this.privateData = paramParcel.createByteArray();
  }
  
  public PrivFrame(String paramString, byte[] paramArrayOfByte)
  {
    super("PRIV");
    this.owner = paramString;
    this.privateData = paramArrayOfByte;
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
        paramObject = (PrivFrame)paramObject;
        if ((!Util.areEqual(this.owner, ((PrivFrame)paramObject).owner)) || (!Arrays.equals(this.privateData, ((PrivFrame)paramObject).privateData))) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    if (this.owner != null) {}
    for (int i = this.owner.hashCode();; i = 0) {
      return (i + 527) * 31 + Arrays.hashCode(this.privateData);
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.owner);
    paramParcel.writeByteArray(this.privateData);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/PrivFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */