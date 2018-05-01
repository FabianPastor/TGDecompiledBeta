package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TextInformationFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<TextInformationFrame> CREATOR = new Parcelable.Creator()
  {
    public TextInformationFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TextInformationFrame(paramAnonymousParcel);
    }
    
    public TextInformationFrame[] newArray(int paramAnonymousInt)
    {
      return new TextInformationFrame[paramAnonymousInt];
    }
  };
  public final String description;
  public final String value;
  
  TextInformationFrame(Parcel paramParcel)
  {
    super(paramParcel.readString());
    this.description = paramParcel.readString();
    this.value = paramParcel.readString();
  }
  
  public TextInformationFrame(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1);
    this.description = paramString2;
    this.value = paramString3;
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
        paramObject = (TextInformationFrame)paramObject;
        if ((!this.id.equals(((TextInformationFrame)paramObject).id)) || (!Util.areEqual(this.description, ((TextInformationFrame)paramObject).description)) || (!Util.areEqual(this.value, ((TextInformationFrame)paramObject).value))) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    int i = 0;
    int j = this.id.hashCode();
    if (this.description != null) {}
    for (int k = this.description.hashCode();; k = 0)
    {
      if (this.value != null) {
        i = this.value.hashCode();
      }
      return ((j + 527) * 31 + k) * 31 + i;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeString(this.description);
    paramParcel.writeString(this.value);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/TextInformationFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */