package org.telegram.messenger.exoplayer.extractor.mp4;

import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.util.Util;

public final class Track
{
  public static final int TYPE_sbtl = Util.getIntegerCodeForString("sbtl");
  public static final int TYPE_soun;
  public static final int TYPE_subt = Util.getIntegerCodeForString("subt");
  public static final int TYPE_text;
  public static final int TYPE_vide = Util.getIntegerCodeForString("vide");
  public final long durationUs;
  public final long[] editListDurations;
  public final long[] editListMediaTimes;
  public final int id;
  public final MediaFormat mediaFormat;
  public final long movieTimescale;
  public final int nalUnitLengthFieldLength;
  public final TrackEncryptionBox[] sampleDescriptionEncryptionBoxes;
  public final long timescale;
  public final int type;
  
  static
  {
    TYPE_soun = Util.getIntegerCodeForString("soun");
    TYPE_text = Util.getIntegerCodeForString("text");
  }
  
  public Track(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, MediaFormat paramMediaFormat, TrackEncryptionBox[] paramArrayOfTrackEncryptionBox, int paramInt3, long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    this.id = paramInt1;
    this.type = paramInt2;
    this.timescale = paramLong1;
    this.movieTimescale = paramLong2;
    this.durationUs = paramLong3;
    this.mediaFormat = paramMediaFormat;
    this.sampleDescriptionEncryptionBoxes = paramArrayOfTrackEncryptionBox;
    this.nalUnitLengthFieldLength = paramInt3;
    this.editListDurations = paramArrayOfLong1;
    this.editListMediaTimes = paramArrayOfLong2;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/mp4/Track.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */