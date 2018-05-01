package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.Util;

public final class CommentFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<CommentFrame> CREATOR = new Parcelable.Creator()
  {
    public CommentFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new CommentFrame(paramAnonymousParcel);
    }
    
    public CommentFrame[] newArray(int paramAnonymousInt)
    {
      return new CommentFrame[paramAnonymousInt];
    }
  };
  public static final String ID = "COMM";
  public final String description;
  public final String language;
  public final String text;
  
  CommentFrame(Parcel paramParcel)
  {
    super("COMM");
    this.language = paramParcel.readString();
    this.description = paramParcel.readString();
    this.text = paramParcel.readString();
  }
  
  public CommentFrame(String paramString1, String paramString2, String paramString3)
  {
    super("COMM");
    this.language = paramString1;
    this.description = paramString2;
    this.text = paramString3;
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
        paramObject = (CommentFrame)paramObject;
        if ((!Util.areEqual(this.description, ((CommentFrame)paramObject).description)) || (!Util.areEqual(this.language, ((CommentFrame)paramObject).language)) || (!Util.areEqual(this.text, ((CommentFrame)paramObject).text))) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    int i = 0;
    int j;
    if (this.language != null)
    {
      j = this.language.hashCode();
      if (this.description == null) {
        break label68;
      }
    }
    label68:
    for (int k = this.description.hashCode();; k = 0)
    {
      if (this.text != null) {
        i = this.text.hashCode();
      }
      return ((j + 527) * 31 + k) * 31 + i;
      j = 0;
      break;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeString(this.language);
    paramParcel.writeString(this.text);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/CommentFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */