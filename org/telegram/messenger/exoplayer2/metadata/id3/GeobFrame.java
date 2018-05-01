package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Util;

public final class GeobFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<GeobFrame> CREATOR = new Parcelable.Creator()
  {
    public GeobFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new GeobFrame(paramAnonymousParcel);
    }
    
    public GeobFrame[] newArray(int paramAnonymousInt)
    {
      return new GeobFrame[paramAnonymousInt];
    }
  };
  public static final String ID = "GEOB";
  public final byte[] data;
  public final String description;
  public final String filename;
  public final String mimeType;
  
  GeobFrame(Parcel paramParcel)
  {
    super("GEOB");
    this.mimeType = paramParcel.readString();
    this.filename = paramParcel.readString();
    this.description = paramParcel.readString();
    this.data = paramParcel.createByteArray();
  }
  
  public GeobFrame(String paramString1, String paramString2, String paramString3, byte[] paramArrayOfByte)
  {
    super("GEOB");
    this.mimeType = paramString1;
    this.filename = paramString2;
    this.description = paramString3;
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
        paramObject = (GeobFrame)paramObject;
        if ((!Util.areEqual(this.mimeType, ((GeobFrame)paramObject).mimeType)) || (!Util.areEqual(this.filename, ((GeobFrame)paramObject).filename)) || (!Util.areEqual(this.description, ((GeobFrame)paramObject).description)) || (!Arrays.equals(this.data, ((GeobFrame)paramObject).data))) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    int i = 0;
    int j;
    if (this.mimeType != null)
    {
      j = this.mimeType.hashCode();
      if (this.filename == null) {
        break label79;
      }
    }
    label79:
    for (int k = this.filename.hashCode();; k = 0)
    {
      if (this.description != null) {
        i = this.description.hashCode();
      }
      return (((j + 527) * 31 + k) * 31 + i) * 31 + Arrays.hashCode(this.data);
      j = 0;
      break;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.mimeType);
    paramParcel.writeString(this.filename);
    paramParcel.writeString(this.description);
    paramParcel.writeByteArray(this.data);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/GeobFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */