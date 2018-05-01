package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Util;

public final class ChapterTocFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<ChapterTocFrame> CREATOR = new Parcelable.Creator()
  {
    public ChapterTocFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ChapterTocFrame(paramAnonymousParcel);
    }
    
    public ChapterTocFrame[] newArray(int paramAnonymousInt)
    {
      return new ChapterTocFrame[paramAnonymousInt];
    }
  };
  public static final String ID = "CTOC";
  public final String[] children;
  public final String elementId;
  public final boolean isOrdered;
  public final boolean isRoot;
  private final Id3Frame[] subFrames;
  
  ChapterTocFrame(Parcel paramParcel)
  {
    super("CTOC");
    this.elementId = paramParcel.readString();
    if (paramParcel.readByte() != 0)
    {
      bool2 = true;
      this.isRoot = bool2;
      if (paramParcel.readByte() == 0) {
        break label107;
      }
    }
    label107:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      this.isOrdered = bool2;
      this.children = paramParcel.createStringArray();
      int i = paramParcel.readInt();
      this.subFrames = new Id3Frame[i];
      for (int j = 0; j < i; j++) {
        this.subFrames[j] = ((Id3Frame)paramParcel.readParcelable(Id3Frame.class.getClassLoader()));
      }
      bool2 = false;
      break;
    }
  }
  
  public ChapterTocFrame(String paramString, boolean paramBoolean1, boolean paramBoolean2, String[] paramArrayOfString, Id3Frame[] paramArrayOfId3Frame)
  {
    super("CTOC");
    this.elementId = paramString;
    this.isRoot = paramBoolean1;
    this.isOrdered = paramBoolean2;
    this.children = paramArrayOfString;
    this.subFrames = paramArrayOfId3Frame;
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
        paramObject = (ChapterTocFrame)paramObject;
        if ((this.isRoot != ((ChapterTocFrame)paramObject).isRoot) || (this.isOrdered != ((ChapterTocFrame)paramObject).isOrdered) || (!Util.areEqual(this.elementId, ((ChapterTocFrame)paramObject).elementId)) || (!Arrays.equals(this.children, ((ChapterTocFrame)paramObject).children)) || (!Arrays.equals(this.subFrames, ((ChapterTocFrame)paramObject).subFrames))) {
          bool = false;
        }
      }
    }
  }
  
  public Id3Frame getSubFrame(int paramInt)
  {
    return this.subFrames[paramInt];
  }
  
  public int getSubFrameCount()
  {
    return this.subFrames.length;
  }
  
  public int hashCode()
  {
    int i = 1;
    int j = 0;
    int k;
    if (this.isRoot)
    {
      k = 1;
      if (!this.isOrdered) {
        break label56;
      }
    }
    for (;;)
    {
      if (this.elementId != null) {
        j = this.elementId.hashCode();
      }
      return ((k + 527) * 31 + i) * 31 + j;
      k = 0;
      break;
      label56:
      i = 0;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    int i = 1;
    paramParcel.writeString(this.elementId);
    if (this.isRoot)
    {
      paramInt = 1;
      paramParcel.writeByte((byte)paramInt);
      if (!this.isOrdered) {
        break label94;
      }
    }
    label94:
    for (paramInt = i;; paramInt = 0)
    {
      paramParcel.writeByte((byte)paramInt);
      paramParcel.writeStringArray(this.children);
      paramParcel.writeInt(this.subFrames.length);
      Id3Frame[] arrayOfId3Frame = this.subFrames;
      i = arrayOfId3Frame.length;
      for (paramInt = 0; paramInt < i; paramInt++) {
        paramParcel.writeParcelable(arrayOfId3Frame[paramInt], 0);
      }
      paramInt = 0;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/ChapterTocFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */