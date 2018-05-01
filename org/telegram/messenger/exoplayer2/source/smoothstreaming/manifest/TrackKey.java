package org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;

public final class TrackKey
  implements Parcelable, Comparable<TrackKey>
{
  public static final Parcelable.Creator<TrackKey> CREATOR = new Parcelable.Creator()
  {
    public TrackKey createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TrackKey(paramAnonymousParcel.readInt(), paramAnonymousParcel.readInt());
    }
    
    public TrackKey[] newArray(int paramAnonymousInt)
    {
      return new TrackKey[paramAnonymousInt];
    }
  };
  public final int streamElementIndex;
  public final int trackIndex;
  
  public TrackKey(int paramInt1, int paramInt2)
  {
    this.streamElementIndex = paramInt1;
    this.trackIndex = paramInt2;
  }
  
  public int compareTo(TrackKey paramTrackKey)
  {
    int i = this.streamElementIndex - paramTrackKey.streamElementIndex;
    int j = i;
    if (i == 0) {
      j = this.trackIndex - paramTrackKey.trackIndex;
    }
    return j;
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public String toString()
  {
    return this.streamElementIndex + "." + this.trackIndex;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeInt(this.streamElementIndex);
    paramParcel.writeInt(this.trackIndex);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/smoothstreaming/manifest/TrackKey.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */