package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class UrlLinkFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<UrlLinkFrame> CREATOR = new Parcelable.Creator()
  {
    public UrlLinkFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new UrlLinkFrame(paramAnonymousParcel);
    }
    
    public UrlLinkFrame[] newArray(int paramAnonymousInt)
    {
      return new UrlLinkFrame[paramAnonymousInt];
    }
  };
  public final String description;
  public final String url;
  
  UrlLinkFrame(Parcel paramParcel)
  {
    super(paramParcel.readString());
    this.description = paramParcel.readString();
    this.url = paramParcel.readString();
  }
  
  public UrlLinkFrame(String paramString1, String paramString2, String paramString3)
  {
    super(paramString1);
    this.description = paramString2;
    this.url = paramString3;
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
        paramObject = (UrlLinkFrame)paramObject;
        if ((!this.id.equals(((UrlLinkFrame)paramObject).id)) || (!Util.areEqual(this.description, ((UrlLinkFrame)paramObject).description)) || (!Util.areEqual(this.url, ((UrlLinkFrame)paramObject).url))) {
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
      if (this.url != null) {
        i = this.url.hashCode();
      }
      return ((j + 527) * 31 + k) * 31 + i;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeString(this.description);
    paramParcel.writeString(this.url);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/UrlLinkFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */