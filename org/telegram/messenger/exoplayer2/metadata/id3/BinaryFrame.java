package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;

public final class BinaryFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<BinaryFrame> CREATOR = new Parcelable.Creator()
  {
    public BinaryFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new BinaryFrame(paramAnonymousParcel);
    }
    
    public BinaryFrame[] newArray(int paramAnonymousInt)
    {
      return new BinaryFrame[paramAnonymousInt];
    }
  };
  public final byte[] data;
  
  BinaryFrame(Parcel paramParcel)
  {
    super(paramParcel.readString());
    this.data = paramParcel.createByteArray();
  }
  
  public BinaryFrame(String paramString, byte[] paramArrayOfByte)
  {
    super(paramString);
    this.data = paramArrayOfByte;
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
        paramObject = (BinaryFrame)paramObject;
        if ((!this.id.equals(((BinaryFrame)paramObject).id)) || (!Arrays.equals(this.data, ((BinaryFrame)paramObject).data))) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    return (this.id.hashCode() + 527) * 31 + Arrays.hashCode(this.data);
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeByteArray(this.data);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/BinaryFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */