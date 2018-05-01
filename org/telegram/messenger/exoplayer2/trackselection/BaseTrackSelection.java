package org.telegram.messenger.exoplayer2.trackselection;

import android.os.SystemClock;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.chunk.MediaChunk;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class BaseTrackSelection
  implements TrackSelection
{
  private final long[] blacklistUntilTimes;
  private final Format[] formats;
  protected final TrackGroup group;
  private int hashCode;
  protected final int length;
  protected final int[] tracks;
  
  public BaseTrackSelection(TrackGroup paramTrackGroup, int... paramVarArgs)
  {
    if (paramVarArgs.length > 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.group = ((TrackGroup)Assertions.checkNotNull(paramTrackGroup));
      this.length = paramVarArgs.length;
      this.formats = new Format[this.length];
      for (i = 0; i < paramVarArgs.length; i++) {
        this.formats[i] = paramTrackGroup.getFormat(paramVarArgs[i]);
      }
    }
    Arrays.sort(this.formats, new DecreasingBandwidthComparator(null));
    this.tracks = new int[this.length];
    for (int i = 0; i < this.length; i++) {
      this.tracks[i] = paramTrackGroup.indexOf(this.formats[i]);
    }
    this.blacklistUntilTimes = new long[this.length];
  }
  
  public final boolean blacklist(int paramInt, long paramLong)
  {
    boolean bool1 = false;
    long l = SystemClock.elapsedRealtime();
    boolean bool2 = isBlacklisted(paramInt, l);
    int i = 0;
    if ((i < this.length) && (!bool2))
    {
      if ((i != paramInt) && (!isBlacklisted(i, l))) {}
      for (bool2 = true;; bool2 = false)
      {
        i++;
        break;
      }
    }
    if (!bool2) {}
    for (bool2 = bool1;; bool2 = true)
    {
      return bool2;
      this.blacklistUntilTimes[paramInt] = Math.max(this.blacklistUntilTimes[paramInt], l + paramLong);
    }
  }
  
  public void disable() {}
  
  public void enable() {}
  
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
        paramObject = (BaseTrackSelection)paramObject;
        if ((this.group != ((BaseTrackSelection)paramObject).group) || (!Arrays.equals(this.tracks, ((BaseTrackSelection)paramObject).tracks))) {
          bool = false;
        }
      }
    }
  }
  
  public int evaluateQueueSize(long paramLong, List<? extends MediaChunk> paramList)
  {
    return paramList.size();
  }
  
  public final Format getFormat(int paramInt)
  {
    return this.formats[paramInt];
  }
  
  public final int getIndexInTrackGroup(int paramInt)
  {
    return this.tracks[paramInt];
  }
  
  public final Format getSelectedFormat()
  {
    return this.formats[getSelectedIndex()];
  }
  
  public final int getSelectedIndexInTrackGroup()
  {
    return this.tracks[getSelectedIndex()];
  }
  
  public final TrackGroup getTrackGroup()
  {
    return this.group;
  }
  
  public int hashCode()
  {
    if (this.hashCode == 0) {
      this.hashCode = (System.identityHashCode(this.group) * 31 + Arrays.hashCode(this.tracks));
    }
    return this.hashCode;
  }
  
  public final int indexOf(int paramInt)
  {
    int i = 0;
    if (i < this.length) {
      if (this.tracks[i] != paramInt) {}
    }
    for (;;)
    {
      return i;
      i++;
      break;
      i = -1;
    }
  }
  
  public final int indexOf(Format paramFormat)
  {
    int i = 0;
    if (i < this.length) {
      if (this.formats[i] != paramFormat) {}
    }
    for (;;)
    {
      return i;
      i++;
      break;
      i = -1;
    }
  }
  
  protected final boolean isBlacklisted(int paramInt, long paramLong)
  {
    if (this.blacklistUntilTimes[paramInt] > paramLong) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final int length()
  {
    return this.tracks.length;
  }
  
  public void onPlaybackSpeed(float paramFloat) {}
  
  private static final class DecreasingBandwidthComparator
    implements Comparator<Format>
  {
    public int compare(Format paramFormat1, Format paramFormat2)
    {
      return paramFormat2.bitrate - paramFormat1.bitrate;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/trackselection/BaseTrackSelection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */