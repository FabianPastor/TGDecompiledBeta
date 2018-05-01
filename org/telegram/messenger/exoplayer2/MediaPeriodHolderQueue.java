package org.telegram.messenger.exoplayer2;

import org.telegram.messenger.exoplayer2.util.Assertions;

final class MediaPeriodHolderQueue
{
  private int length;
  private MediaPeriodHolder loading;
  private MediaPeriodHolder playing;
  private MediaPeriodHolder reading;
  
  public MediaPeriodHolder advancePlayingPeriod()
  {
    if (this.playing != null)
    {
      if (this.playing == this.reading) {
        this.reading = this.playing.next;
      }
      this.playing.release();
      this.playing = this.playing.next;
      this.length -= 1;
      if (this.length == 0) {
        this.loading = null;
      }
    }
    for (;;)
    {
      return this.playing;
      this.playing = this.loading;
      this.reading = this.loading;
    }
  }
  
  public MediaPeriodHolder advanceReadingPeriod()
  {
    if ((this.reading != null) && (this.reading.next != null)) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.reading = this.reading.next;
      return this.reading;
    }
  }
  
  public void clear()
  {
    MediaPeriodHolder localMediaPeriodHolder = getFrontPeriod();
    if (localMediaPeriodHolder != null)
    {
      localMediaPeriodHolder.release();
      removeAfter(localMediaPeriodHolder);
    }
    this.playing = null;
    this.loading = null;
    this.reading = null;
    this.length = 0;
  }
  
  public void enqueueLoadingPeriod(MediaPeriodHolder paramMediaPeriodHolder)
  {
    if (paramMediaPeriodHolder != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      if (this.loading != null)
      {
        Assertions.checkState(hasPlayingPeriod());
        this.loading.next = paramMediaPeriodHolder;
      }
      this.loading = paramMediaPeriodHolder;
      this.length += 1;
      return;
    }
  }
  
  public MediaPeriodHolder getFrontPeriod()
  {
    if (hasPlayingPeriod()) {}
    for (MediaPeriodHolder localMediaPeriodHolder = this.playing;; localMediaPeriodHolder = this.loading) {
      return localMediaPeriodHolder;
    }
  }
  
  public int getLength()
  {
    return this.length;
  }
  
  public MediaPeriodHolder getLoadingPeriod()
  {
    return this.loading;
  }
  
  public MediaPeriodHolder getPlayingPeriod()
  {
    return this.playing;
  }
  
  public MediaPeriodHolder getReadingPeriod()
  {
    return this.reading;
  }
  
  public boolean hasPlayingPeriod()
  {
    if (this.playing != null) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public boolean removeAfter(MediaPeriodHolder paramMediaPeriodHolder)
  {
    if (paramMediaPeriodHolder != null) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      bool = false;
      this.loading = paramMediaPeriodHolder;
      while (paramMediaPeriodHolder.next != null)
      {
        paramMediaPeriodHolder = paramMediaPeriodHolder.next;
        if (paramMediaPeriodHolder == this.reading)
        {
          this.reading = this.playing;
          bool = true;
        }
        paramMediaPeriodHolder.release();
        this.length -= 1;
      }
    }
    this.loading.next = null;
    return bool;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/MediaPeriodHolderQueue.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */