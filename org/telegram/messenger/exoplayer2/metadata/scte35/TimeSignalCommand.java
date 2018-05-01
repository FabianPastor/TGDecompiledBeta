package org.telegram.messenger.exoplayer2.metadata.scte35;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class TimeSignalCommand
  extends SpliceCommand
{
  public static final Parcelable.Creator<TimeSignalCommand> CREATOR = new Parcelable.Creator()
  {
    public TimeSignalCommand createFromParcel(Parcel paramAnonymousParcel)
    {
      return new TimeSignalCommand(paramAnonymousParcel.readLong(), paramAnonymousParcel.readLong(), null);
    }
    
    public TimeSignalCommand[] newArray(int paramAnonymousInt)
    {
      return new TimeSignalCommand[paramAnonymousInt];
    }
  };
  public final long playbackPositionUs;
  public final long ptsTime;
  
  private TimeSignalCommand(long paramLong1, long paramLong2)
  {
    this.ptsTime = paramLong1;
    this.playbackPositionUs = paramLong2;
  }
  
  static TimeSignalCommand parseFromSection(ParsableByteArray paramParsableByteArray, long paramLong, TimestampAdjuster paramTimestampAdjuster)
  {
    paramLong = parseSpliceTime(paramParsableByteArray, paramLong);
    return new TimeSignalCommand(paramLong, paramTimestampAdjuster.adjustTsTimestamp(paramLong));
  }
  
  static long parseSpliceTime(ParsableByteArray paramParsableByteArray, long paramLong)
  {
    long l1 = paramParsableByteArray.readUnsignedByte();
    long l2 = -9223372036854775807L;
    if ((0x80 & l1) != 0L) {
      l2 = ((1L & l1) << 32 | paramParsableByteArray.readUnsignedInt()) + paramLong & 0x1FFFFFFFF;
    }
    return l2;
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeLong(this.ptsTime);
    paramParcel.writeLong(this.playbackPositionUs);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/scte35/TimeSignalCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */