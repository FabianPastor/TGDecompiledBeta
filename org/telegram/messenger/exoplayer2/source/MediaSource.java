package org.telegram.messenger.exoplayer2.source;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.ExoPlayer;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.upstream.Allocator;

public abstract interface MediaSource
{
  public static final String MEDIA_SOURCE_REUSED_ERROR_MESSAGE = "MediaSource instances are not allowed to be reused.";
  
  public abstract MediaPeriod createPeriod(MediaPeriodId paramMediaPeriodId, Allocator paramAllocator);
  
  public abstract void maybeThrowSourceInfoRefreshError()
    throws IOException;
  
  public abstract void prepareSource(ExoPlayer paramExoPlayer, boolean paramBoolean, Listener paramListener);
  
  public abstract void releasePeriod(MediaPeriod paramMediaPeriod);
  
  public abstract void releaseSource();
  
  public static abstract interface Listener
  {
    public abstract void onSourceInfoRefreshed(MediaSource paramMediaSource, Timeline paramTimeline, Object paramObject);
  }
  
  public static final class MediaPeriodId
  {
    public static final MediaPeriodId UNSET = new MediaPeriodId(-1, -1, -1);
    public final int adGroupIndex;
    public final int adIndexInAdGroup;
    public final int periodIndex;
    
    public MediaPeriodId(int paramInt)
    {
      this(paramInt, -1, -1);
    }
    
    public MediaPeriodId(int paramInt1, int paramInt2, int paramInt3)
    {
      this.periodIndex = paramInt1;
      this.adGroupIndex = paramInt2;
      this.adIndexInAdGroup = paramInt3;
    }
    
    public MediaPeriodId copyWithPeriodIndex(int paramInt)
    {
      if (this.periodIndex == paramInt) {}
      for (MediaPeriodId localMediaPeriodId = this;; localMediaPeriodId = new MediaPeriodId(paramInt, this.adGroupIndex, this.adIndexInAdGroup)) {
        return localMediaPeriodId;
      }
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
          paramObject = (MediaPeriodId)paramObject;
          if ((this.periodIndex != ((MediaPeriodId)paramObject).periodIndex) || (this.adGroupIndex != ((MediaPeriodId)paramObject).adGroupIndex) || (this.adIndexInAdGroup != ((MediaPeriodId)paramObject).adIndexInAdGroup)) {
            bool = false;
          }
        }
      }
    }
    
    public int hashCode()
    {
      return ((this.periodIndex + 527) * 31 + this.adGroupIndex) * 31 + this.adIndexInAdGroup;
    }
    
    public boolean isAd()
    {
      if (this.adGroupIndex != -1) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/MediaSource.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */