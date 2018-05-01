package org.telegram.messenger.exoplayer2.extractor.mp4;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.Format;

public final class Track
{
  public static final int TRANSFORMATION_CEA608_CDAT = 1;
  public static final int TRANSFORMATION_NONE = 0;
  public final long durationUs;
  public final long[] editListDurations;
  public final long[] editListMediaTimes;
  public final Format format;
  public final int id;
  public final long movieTimescale;
  public final int nalUnitLengthFieldLength;
  private final TrackEncryptionBox[] sampleDescriptionEncryptionBoxes;
  public final int sampleTransformation;
  public final long timescale;
  public final int type;
  
  public Track(int paramInt1, int paramInt2, long paramLong1, long paramLong2, long paramLong3, Format paramFormat, int paramInt3, TrackEncryptionBox[] paramArrayOfTrackEncryptionBox, int paramInt4, long[] paramArrayOfLong1, long[] paramArrayOfLong2)
  {
    this.id = paramInt1;
    this.type = paramInt2;
    this.timescale = paramLong1;
    this.movieTimescale = paramLong2;
    this.durationUs = paramLong3;
    this.format = paramFormat;
    this.sampleTransformation = paramInt3;
    this.sampleDescriptionEncryptionBoxes = paramArrayOfTrackEncryptionBox;
    this.nalUnitLengthFieldLength = paramInt4;
    this.editListDurations = paramArrayOfLong1;
    this.editListMediaTimes = paramArrayOfLong2;
  }
  
  public TrackEncryptionBox getSampleDescriptionEncryptionBox(int paramInt)
  {
    if (this.sampleDescriptionEncryptionBoxes == null) {}
    for (TrackEncryptionBox localTrackEncryptionBox = null;; localTrackEncryptionBox = this.sampleDescriptionEncryptionBoxes[paramInt]) {
      return localTrackEncryptionBox;
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Transformation {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/mp4/Track.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */