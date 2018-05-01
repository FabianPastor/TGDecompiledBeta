package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TxxxFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<TxxxFrame> CREATOR = new Parcelable.Creator()
  {
    public TxxxFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TxxxFrame(paramAnonymousParcel);
    }
    
    public TxxxFrame[] newArray(int paramAnonymousInt)
    {
      return new TxxxFrame[paramAnonymousInt];
    }
  };
  public static final String ID = "TXXX";
  public final String description;
  public final String value;
  
  TxxxFrame(Parcel paramParcel)
  {
    super("TXXX");
    this.description = paramParcel.readString();
    this.value = paramParcel.readString();
  }
  
  public TxxxFrame(String paramString1, String paramString2)
  {
    super("TXXX");
    this.description = paramString1;
    this.value = paramString2;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {}
    do
    {
      return true;
      if ((paramObject == null) || (getClass() != paramObject.getClass())) {
        return false;
      }
      paramObject = (TxxxFrame)paramObject;
    } while ((Util.areEqual(this.description, ((TxxxFrame)paramObject).description)) && (Util.areEqual(this.value, ((TxxxFrame)paramObject).value)));
    return false;
  }
  
  public int hashCode()
  {
    int j = 0;
    if (this.description != null) {}
    for (int i = this.description.hashCode();; i = 0)
    {
      if (this.value != null) {
        j = this.value.hashCode();
      }
      return (i + 527) * 31 + j;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.description);
    paramParcel.writeString(this.value);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/TxxxFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */