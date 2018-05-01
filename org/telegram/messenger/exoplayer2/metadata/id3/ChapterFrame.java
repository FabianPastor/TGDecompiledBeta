package org.telegram.messenger.exoplayer2.metadata.id3;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.util.Util;

public final class ChapterFrame
  extends Id3Frame
{
  public static final Parcelable.Creator<ChapterFrame> CREATOR = new Parcelable.Creator()
  {
    public ChapterFrame createFromParcel(Parcel paramAnonymousParcel)
    {
      return new ChapterFrame(paramAnonymousParcel);
    }
    
    public ChapterFrame[] newArray(int paramAnonymousInt)
    {
      return new ChapterFrame[paramAnonymousInt];
    }
  };
  public static final String ID = "CHAP";
  public final String chapterId;
  public final long endOffset;
  public final int endTimeMs;
  public final long startOffset;
  public final int startTimeMs;
  private final Id3Frame[] subFrames;
  
  ChapterFrame(Parcel paramParcel)
  {
    super("CHAP");
    this.chapterId = paramParcel.readString();
    this.startTimeMs = paramParcel.readInt();
    this.endTimeMs = paramParcel.readInt();
    this.startOffset = paramParcel.readLong();
    this.endOffset = paramParcel.readLong();
    int i = paramParcel.readInt();
    this.subFrames = new Id3Frame[i];
    for (int j = 0; j < i; j++) {
      this.subFrames[j] = ((Id3Frame)paramParcel.readParcelable(Id3Frame.class.getClassLoader()));
    }
  }
  
  public ChapterFrame(String paramString, int paramInt1, int paramInt2, long paramLong1, long paramLong2, Id3Frame[] paramArrayOfId3Frame)
  {
    super("CHAP");
    this.chapterId = paramString;
    this.startTimeMs = paramInt1;
    this.endTimeMs = paramInt2;
    this.startOffset = paramLong1;
    this.endOffset = paramLong2;
    this.subFrames = paramArrayOfId3Frame;
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
        paramObject = (ChapterFrame)paramObject;
        if ((this.startTimeMs != ((ChapterFrame)paramObject).startTimeMs) || (this.endTimeMs != ((ChapterFrame)paramObject).endTimeMs) || (this.startOffset != ((ChapterFrame)paramObject).startOffset) || (this.endOffset != ((ChapterFrame)paramObject).endOffset) || (!Util.areEqual(this.chapterId, ((ChapterFrame)paramObject).chapterId)) || (!Arrays.equals(this.subFrames, ((ChapterFrame)paramObject).subFrames))) {
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
    int i = this.startTimeMs;
    int j = this.endTimeMs;
    int k = (int)this.startOffset;
    int m = (int)this.endOffset;
    if (this.chapterId != null) {}
    for (int n = this.chapterId.hashCode();; n = 0) {
      return ((((i + 527) * 31 + j) * 31 + k) * 31 + m) * 31 + n;
    }
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.chapterId);
    paramParcel.writeInt(this.startTimeMs);
    paramParcel.writeInt(this.endTimeMs);
    paramParcel.writeLong(this.startOffset);
    paramParcel.writeLong(this.endOffset);
    paramParcel.writeInt(this.subFrames.length);
    Id3Frame[] arrayOfId3Frame = this.subFrames;
    int i = arrayOfId3Frame.length;
    for (paramInt = 0; paramInt < i; paramInt++) {
      paramParcel.writeParcelable(arrayOfId3Frame[paramInt], 0);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/id3/ChapterFrame.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */