package org.telegram.messenger.exoplayer2.source.hls.playlist;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;

public final class HlsMediaPlaylist
  extends HlsPlaylist
{
  public static final int PLAYLIST_TYPE_EVENT = 2;
  public static final int PLAYLIST_TYPE_UNKNOWN = 0;
  public static final int PLAYLIST_TYPE_VOD = 1;
  public final int discontinuitySequence;
  public final DrmInitData drmInitData;
  public final long durationUs;
  public final boolean hasDiscontinuitySequence;
  public final boolean hasEndTag;
  public final boolean hasIndependentSegmentsTag;
  public final boolean hasProgramDateTime;
  public final Segment initializationSegment;
  public final int mediaSequence;
  public final int playlistType;
  public final List<Segment> segments;
  public final long startOffsetUs;
  public final long startTimeUs;
  public final long targetDurationUs;
  public final int version;
  
  public HlsMediaPlaylist(int paramInt1, String paramString, List<String> paramList, long paramLong1, long paramLong2, boolean paramBoolean1, int paramInt2, int paramInt3, int paramInt4, long paramLong3, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, DrmInitData paramDrmInitData, Segment paramSegment, List<Segment> paramList1)
  {
    super(paramString, paramList);
    this.playlistType = paramInt1;
    this.startTimeUs = paramLong2;
    this.hasDiscontinuitySequence = paramBoolean1;
    this.discontinuitySequence = paramInt2;
    this.mediaSequence = paramInt3;
    this.version = paramInt4;
    this.targetDurationUs = paramLong3;
    this.hasIndependentSegmentsTag = paramBoolean2;
    this.hasEndTag = paramBoolean3;
    this.hasProgramDateTime = paramBoolean4;
    this.drmInitData = paramDrmInitData;
    this.initializationSegment = paramSegment;
    this.segments = Collections.unmodifiableList(paramList1);
    if (!paramList1.isEmpty())
    {
      paramString = (Segment)paramList1.get(paramList1.size() - 1);
      this.durationUs = (paramString.relativeStartTimeUs + paramString.durationUs);
      if (paramLong1 != -9223372036854775807L) {
        break label158;
      }
      paramLong2 = -9223372036854775807L;
    }
    for (;;)
    {
      this.startOffsetUs = paramLong2;
      return;
      this.durationUs = 0L;
      break;
      label158:
      paramLong2 = paramLong1;
      if (paramLong1 < 0L) {
        paramLong2 = paramLong1 + this.durationUs;
      }
    }
  }
  
  public HlsMediaPlaylist copyWith(long paramLong, int paramInt)
  {
    return new HlsMediaPlaylist(this.playlistType, this.baseUri, this.tags, this.startOffsetUs, paramLong, true, paramInt, this.mediaSequence, this.version, this.targetDurationUs, this.hasIndependentSegmentsTag, this.hasEndTag, this.hasProgramDateTime, this.drmInitData, this.initializationSegment, this.segments);
  }
  
  public HlsMediaPlaylist copyWithEndTag()
  {
    if (this.hasEndTag) {}
    for (HlsMediaPlaylist localHlsMediaPlaylist = this;; localHlsMediaPlaylist = new HlsMediaPlaylist(this.playlistType, this.baseUri, this.tags, this.startOffsetUs, this.startTimeUs, this.hasDiscontinuitySequence, this.discontinuitySequence, this.mediaSequence, this.version, this.targetDurationUs, this.hasIndependentSegmentsTag, true, this.hasProgramDateTime, this.drmInitData, this.initializationSegment, this.segments)) {
      return localHlsMediaPlaylist;
    }
  }
  
  public long getEndTimeUs()
  {
    return this.startTimeUs + this.durationUs;
  }
  
  public boolean isNewerThan(HlsMediaPlaylist paramHlsMediaPlaylist)
  {
    boolean bool1 = false;
    boolean bool2;
    if ((paramHlsMediaPlaylist == null) || (this.mediaSequence > paramHlsMediaPlaylist.mediaSequence)) {
      bool2 = true;
    }
    for (;;)
    {
      return bool2;
      bool2 = bool1;
      if (this.mediaSequence >= paramHlsMediaPlaylist.mediaSequence)
      {
        int i = this.segments.size();
        int j = paramHlsMediaPlaylist.segments.size();
        if (i <= j)
        {
          bool2 = bool1;
          if (i == j)
          {
            bool2 = bool1;
            if (this.hasEndTag)
            {
              bool2 = bool1;
              if (paramHlsMediaPlaylist.hasEndTag) {}
            }
          }
        }
        else
        {
          bool2 = true;
        }
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface PlaylistType {}
  
  public static final class Segment
    implements Comparable<Long>
  {
    public final long byterangeLength;
    public final long byterangeOffset;
    public final long durationUs;
    public final String encryptionIV;
    public final String fullSegmentEncryptionKeyUri;
    public final int relativeDiscontinuitySequence;
    public final long relativeStartTimeUs;
    public final String url;
    
    public Segment(String paramString1, long paramLong1, int paramInt, long paramLong2, String paramString2, String paramString3, long paramLong3, long paramLong4)
    {
      this.url = paramString1;
      this.durationUs = paramLong1;
      this.relativeDiscontinuitySequence = paramInt;
      this.relativeStartTimeUs = paramLong2;
      this.fullSegmentEncryptionKeyUri = paramString2;
      this.encryptionIV = paramString3;
      this.byterangeOffset = paramLong3;
      this.byterangeLength = paramLong4;
    }
    
    public Segment(String paramString, long paramLong1, long paramLong2)
    {
      this(paramString, 0L, -1, -9223372036854775807L, null, null, paramLong1, paramLong2);
    }
    
    public int compareTo(Long paramLong)
    {
      int i;
      if (this.relativeStartTimeUs > paramLong.longValue()) {
        i = 1;
      }
      for (;;)
      {
        return i;
        if (this.relativeStartTimeUs < paramLong.longValue()) {
          i = -1;
        } else {
          i = 0;
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/playlist/HlsMediaPlaylist.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */