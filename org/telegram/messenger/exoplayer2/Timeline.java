package org.telegram.messenger.exoplayer2;

import android.util.Pair;
import org.telegram.messenger.exoplayer2.source.ads.AdPlaybackState;
import org.telegram.messenger.exoplayer2.source.ads.AdPlaybackState.AdGroup;
import org.telegram.messenger.exoplayer2.util.Assertions;

public abstract class Timeline
{
  public static final Timeline EMPTY = new Timeline()
  {
    public int getIndexOfPeriod(Object paramAnonymousObject)
    {
      return -1;
    }
    
    public Timeline.Period getPeriod(int paramAnonymousInt, Timeline.Period paramAnonymousPeriod, boolean paramAnonymousBoolean)
    {
      throw new IndexOutOfBoundsException();
    }
    
    public int getPeriodCount()
    {
      return 0;
    }
    
    public Timeline.Window getWindow(int paramAnonymousInt, Timeline.Window paramAnonymousWindow, boolean paramAnonymousBoolean, long paramAnonymousLong)
    {
      throw new IndexOutOfBoundsException();
    }
    
    public int getWindowCount()
    {
      return 0;
    }
  };
  
  public int getFirstWindowIndex(boolean paramBoolean)
  {
    if (isEmpty()) {}
    for (int i = -1;; i = 0) {
      return i;
    }
  }
  
  public abstract int getIndexOfPeriod(Object paramObject);
  
  public int getLastWindowIndex(boolean paramBoolean)
  {
    if (isEmpty()) {}
    for (int i = -1;; i = getWindowCount() - 1) {
      return i;
    }
  }
  
  public final int getNextPeriodIndex(int paramInt1, Period paramPeriod, Window paramWindow, int paramInt2, boolean paramBoolean)
  {
    int i = -1;
    int j = getPeriod(paramInt1, paramPeriod).windowIndex;
    if (getWindow(j, paramWindow).lastPeriodIndex == paramInt1)
    {
      paramInt1 = getNextWindowIndex(j, paramInt2, paramBoolean);
      if (paramInt1 == -1) {
        paramInt1 = i;
      }
    }
    for (;;)
    {
      return paramInt1;
      paramInt1 = getWindow(paramInt1, paramWindow).firstPeriodIndex;
      continue;
      paramInt1++;
    }
  }
  
  public int getNextWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    switch (paramInt2)
    {
    default: 
      throw new IllegalStateException();
    case 0: 
      if (paramInt1 == getLastWindowIndex(paramBoolean)) {
        paramInt1 = -1;
      }
      break;
    }
    for (;;)
    {
      return paramInt1;
      paramInt1++;
      continue;
      continue;
      if (paramInt1 == getLastWindowIndex(paramBoolean)) {
        paramInt1 = getFirstWindowIndex(paramBoolean);
      } else {
        paramInt1++;
      }
    }
  }
  
  public final Period getPeriod(int paramInt, Period paramPeriod)
  {
    return getPeriod(paramInt, paramPeriod, false);
  }
  
  public abstract Period getPeriod(int paramInt, Period paramPeriod, boolean paramBoolean);
  
  public abstract int getPeriodCount();
  
  public final Pair<Integer, Long> getPeriodPosition(Window paramWindow, Period paramPeriod, int paramInt, long paramLong)
  {
    return getPeriodPosition(paramWindow, paramPeriod, paramInt, paramLong, 0L);
  }
  
  public final Pair<Integer, Long> getPeriodPosition(Window paramWindow, Period paramPeriod, int paramInt, long paramLong1, long paramLong2)
  {
    Assertions.checkIndex(paramInt, 0, getWindowCount());
    getWindow(paramInt, paramWindow, false, paramLong2);
    paramLong2 = paramLong1;
    if (paramLong1 == -9223372036854775807L)
    {
      paramLong1 = paramWindow.getDefaultPositionUs();
      paramLong2 = paramLong1;
      if (paramLong1 != -9223372036854775807L) {}
    }
    for (paramWindow = null;; paramWindow = Pair.create(Integer.valueOf(paramInt), Long.valueOf(paramLong1)))
    {
      return paramWindow;
      paramInt = paramWindow.firstPeriodIndex;
      paramLong1 = paramWindow.getPositionInFirstPeriodUs() + paramLong2;
      for (paramLong2 = getPeriod(paramInt, paramPeriod).getDurationUs(); (paramLong2 != -9223372036854775807L) && (paramLong1 >= paramLong2) && (paramInt < paramWindow.lastPeriodIndex); paramLong2 = getPeriod(paramInt, paramPeriod).getDurationUs())
      {
        paramLong1 -= paramLong2;
        paramInt++;
      }
    }
  }
  
  public int getPreviousWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    switch (paramInt2)
    {
    default: 
      throw new IllegalStateException();
    case 0: 
      if (paramInt1 == getFirstWindowIndex(paramBoolean)) {
        paramInt1 = -1;
      }
      break;
    }
    for (;;)
    {
      return paramInt1;
      paramInt1--;
      continue;
      continue;
      if (paramInt1 == getFirstWindowIndex(paramBoolean)) {
        paramInt1 = getLastWindowIndex(paramBoolean);
      } else {
        paramInt1--;
      }
    }
  }
  
  public final Window getWindow(int paramInt, Window paramWindow)
  {
    return getWindow(paramInt, paramWindow, false);
  }
  
  public final Window getWindow(int paramInt, Window paramWindow, boolean paramBoolean)
  {
    return getWindow(paramInt, paramWindow, paramBoolean, 0L);
  }
  
  public abstract Window getWindow(int paramInt, Window paramWindow, boolean paramBoolean, long paramLong);
  
  public abstract int getWindowCount();
  
  public final boolean isEmpty()
  {
    if (getWindowCount() == 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public final boolean isLastPeriod(int paramInt1, Period paramPeriod, Window paramWindow, int paramInt2, boolean paramBoolean)
  {
    if (getNextPeriodIndex(paramInt1, paramPeriod, paramWindow, paramInt2, paramBoolean) == -1) {}
    for (paramBoolean = true;; paramBoolean = false) {
      return paramBoolean;
    }
  }
  
  public static final class Period
  {
    private AdPlaybackState adPlaybackState;
    public long durationUs;
    public Object id;
    private long positionInWindowUs;
    public Object uid;
    public int windowIndex;
    
    public int getAdCountInAdGroup(int paramInt)
    {
      return this.adPlaybackState.adGroups[paramInt].count;
    }
    
    public long getAdDurationUs(int paramInt1, int paramInt2)
    {
      return this.adPlaybackState.adGroups[paramInt1].durationsUs[paramInt2];
    }
    
    public int getAdGroupCount()
    {
      return this.adPlaybackState.adGroupCount;
    }
    
    public int getAdGroupIndexAfterPositionUs(long paramLong)
    {
      int i = -1;
      long[] arrayOfLong = this.adPlaybackState.adGroupTimesUs;
      if (arrayOfLong == null) {}
      do
      {
        return i;
        for (i = 0; (i < arrayOfLong.length) && (arrayOfLong[i] != Long.MIN_VALUE) && ((paramLong >= arrayOfLong[i]) || (hasPlayedAdGroup(i))); i++) {}
      } while (i < arrayOfLong.length);
      for (;;)
      {
        i = -1;
      }
    }
    
    public int getAdGroupIndexForPositionUs(long paramLong)
    {
      int i = -1;
      long[] arrayOfLong = this.adPlaybackState.adGroupTimesUs;
      if (arrayOfLong == null) {}
      do
      {
        return i;
        for (i = arrayOfLong.length - 1; (i >= 0) && ((arrayOfLong[i] == Long.MIN_VALUE) || (arrayOfLong[i] > paramLong)); i--) {}
      } while ((i >= 0) && (!hasPlayedAdGroup(i)));
      for (;;)
      {
        i = -1;
      }
    }
    
    public long getAdGroupTimeUs(int paramInt)
    {
      return this.adPlaybackState.adGroupTimesUs[paramInt];
    }
    
    public long getAdResumePositionUs()
    {
      return this.adPlaybackState.adResumePositionUs;
    }
    
    public long getDurationMs()
    {
      return C.usToMs(this.durationUs);
    }
    
    public long getDurationUs()
    {
      return this.durationUs;
    }
    
    public int getNextAdIndexToPlay(int paramInt)
    {
      return this.adPlaybackState.adGroups[paramInt].nextAdIndexToPlay;
    }
    
    public long getPositionInWindowMs()
    {
      return C.usToMs(this.positionInWindowUs);
    }
    
    public long getPositionInWindowUs()
    {
      return this.positionInWindowUs;
    }
    
    public boolean hasPlayedAdGroup(int paramInt)
    {
      AdPlaybackState.AdGroup localAdGroup = this.adPlaybackState.adGroups[paramInt];
      if (localAdGroup.nextAdIndexToPlay == localAdGroup.count) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public boolean isAdAvailable(int paramInt1, int paramInt2)
    {
      AdPlaybackState.AdGroup localAdGroup = this.adPlaybackState.adGroups[paramInt1];
      if ((localAdGroup.count != -1) && (localAdGroup.states[paramInt2] != 0)) {}
      for (boolean bool = true;; bool = false) {
        return bool;
      }
    }
    
    public Period set(Object paramObject1, Object paramObject2, int paramInt, long paramLong1, long paramLong2)
    {
      return set(paramObject1, paramObject2, paramInt, paramLong1, paramLong2, AdPlaybackState.NONE);
    }
    
    public Period set(Object paramObject1, Object paramObject2, int paramInt, long paramLong1, long paramLong2, AdPlaybackState paramAdPlaybackState)
    {
      this.id = paramObject1;
      this.uid = paramObject2;
      this.windowIndex = paramInt;
      this.durationUs = paramLong1;
      this.positionInWindowUs = paramLong2;
      this.adPlaybackState = paramAdPlaybackState;
      return this;
    }
  }
  
  public static final class Window
  {
    public long defaultPositionUs;
    public long durationUs;
    public int firstPeriodIndex;
    public Object id;
    public boolean isDynamic;
    public boolean isSeekable;
    public int lastPeriodIndex;
    public long positionInFirstPeriodUs;
    public long presentationStartTimeMs;
    public long windowStartTimeMs;
    
    public long getDefaultPositionMs()
    {
      return C.usToMs(this.defaultPositionUs);
    }
    
    public long getDefaultPositionUs()
    {
      return this.defaultPositionUs;
    }
    
    public long getDurationMs()
    {
      return C.usToMs(this.durationUs);
    }
    
    public long getDurationUs()
    {
      return this.durationUs;
    }
    
    public long getPositionInFirstPeriodMs()
    {
      return C.usToMs(this.positionInFirstPeriodUs);
    }
    
    public long getPositionInFirstPeriodUs()
    {
      return this.positionInFirstPeriodUs;
    }
    
    public Window set(Object paramObject, long paramLong1, long paramLong2, boolean paramBoolean1, boolean paramBoolean2, long paramLong3, long paramLong4, int paramInt1, int paramInt2, long paramLong5)
    {
      this.id = paramObject;
      this.presentationStartTimeMs = paramLong1;
      this.windowStartTimeMs = paramLong2;
      this.isSeekable = paramBoolean1;
      this.isDynamic = paramBoolean2;
      this.defaultPositionUs = paramLong3;
      this.durationUs = paramLong4;
      this.firstPeriodIndex = paramInt1;
      this.lastPeriodIndex = paramInt2;
      this.positionInFirstPeriodUs = paramLong5;
      return this;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/Timeline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */