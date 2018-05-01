package org.telegram.messenger.exoplayer2.source;

import android.util.Pair;
import org.telegram.messenger.exoplayer2.Timeline;
import org.telegram.messenger.exoplayer2.Timeline.Period;
import org.telegram.messenger.exoplayer2.Timeline.Window;

abstract class AbstractConcatenatedTimeline
  extends Timeline
{
  private final int childCount;
  private final ShuffleOrder shuffleOrder;
  
  public AbstractConcatenatedTimeline(ShuffleOrder paramShuffleOrder)
  {
    this.shuffleOrder = paramShuffleOrder;
    this.childCount = paramShuffleOrder.getLength();
  }
  
  private int getNextChildIndex(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramInt = this.shuffleOrder.getNextIndex(paramInt);
    }
    for (;;)
    {
      return paramInt;
      if (paramInt < this.childCount - 1) {
        paramInt++;
      } else {
        paramInt = -1;
      }
    }
  }
  
  private int getPreviousChildIndex(int paramInt, boolean paramBoolean)
  {
    if (paramBoolean) {
      paramInt = this.shuffleOrder.getPreviousIndex(paramInt);
    }
    for (;;)
    {
      return paramInt;
      if (paramInt > 0) {
        paramInt--;
      } else {
        paramInt = -1;
      }
    }
  }
  
  protected abstract int getChildIndexByChildUid(Object paramObject);
  
  protected abstract int getChildIndexByPeriodIndex(int paramInt);
  
  protected abstract int getChildIndexByWindowIndex(int paramInt);
  
  protected abstract Object getChildUidByChildIndex(int paramInt);
  
  protected abstract int getFirstPeriodIndexByChildIndex(int paramInt);
  
  public int getFirstWindowIndex(boolean paramBoolean)
  {
    int i = -1;
    int j;
    if (this.childCount == 0) {
      j = i;
    }
    for (;;)
    {
      return j;
      if (paramBoolean) {
        j = this.shuffleOrder.getFirstIndex();
      }
      for (;;)
      {
        if (!getTimelineByChildIndex(j).isEmpty()) {
          break label65;
        }
        int k = getNextChildIndex(j, paramBoolean);
        j = k;
        if (k == -1)
        {
          j = i;
          break;
          j = 0;
        }
      }
      label65:
      j = getFirstWindowIndexByChildIndex(j) + getTimelineByChildIndex(j).getFirstWindowIndex(paramBoolean);
    }
  }
  
  protected abstract int getFirstWindowIndexByChildIndex(int paramInt);
  
  public final int getIndexOfPeriod(Object paramObject)
  {
    int i = -1;
    int j;
    if (!(paramObject instanceof Pair)) {
      j = i;
    }
    for (;;)
    {
      return j;
      Object localObject = (Pair)paramObject;
      paramObject = ((Pair)localObject).first;
      localObject = ((Pair)localObject).second;
      int k = getChildIndexByChildUid(paramObject);
      j = i;
      if (k != -1)
      {
        int m = getTimelineByChildIndex(k).getIndexOfPeriod(localObject);
        j = i;
        if (m != -1) {
          j = getFirstPeriodIndexByChildIndex(k) + m;
        }
      }
    }
  }
  
  public int getLastWindowIndex(boolean paramBoolean)
  {
    int i = -1;
    int j;
    if (this.childCount == 0) {
      j = i;
    }
    for (;;)
    {
      return j;
      if (paramBoolean) {
        j = this.shuffleOrder.getLastIndex();
      }
      for (;;)
      {
        if (!getTimelineByChildIndex(j).isEmpty()) {
          break label70;
        }
        int k = getPreviousChildIndex(j, paramBoolean);
        j = k;
        if (k == -1)
        {
          j = i;
          break;
          j = this.childCount - 1;
        }
      }
      label70:
      j = getFirstWindowIndexByChildIndex(j) + getTimelineByChildIndex(j).getLastWindowIndex(paramBoolean);
    }
  }
  
  public int getNextWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = getChildIndexByWindowIndex(paramInt1);
    int j = getFirstWindowIndexByChildIndex(i);
    Timeline localTimeline = getTimelineByChildIndex(i);
    int k;
    if (paramInt2 == 2)
    {
      k = 0;
      paramInt1 = localTimeline.getNextWindowIndex(paramInt1 - j, k, paramBoolean);
      if (paramInt1 == -1) {
        break label62;
      }
      paramInt1 = j + paramInt1;
    }
    for (;;)
    {
      return paramInt1;
      k = paramInt2;
      break;
      label62:
      for (paramInt1 = getNextChildIndex(i, paramBoolean); (paramInt1 != -1) && (getTimelineByChildIndex(paramInt1).isEmpty()); paramInt1 = getNextChildIndex(paramInt1, paramBoolean)) {}
      if (paramInt1 != -1) {
        paramInt1 = getFirstWindowIndexByChildIndex(paramInt1) + getTimelineByChildIndex(paramInt1).getFirstWindowIndex(paramBoolean);
      } else if (paramInt2 == 2) {
        paramInt1 = getFirstWindowIndex(paramBoolean);
      } else {
        paramInt1 = -1;
      }
    }
  }
  
  public final Timeline.Period getPeriod(int paramInt, Timeline.Period paramPeriod, boolean paramBoolean)
  {
    int i = getChildIndexByPeriodIndex(paramInt);
    int j = getFirstWindowIndexByChildIndex(i);
    int k = getFirstPeriodIndexByChildIndex(i);
    getTimelineByChildIndex(i).getPeriod(paramInt - k, paramPeriod, paramBoolean);
    paramPeriod.windowIndex += j;
    if (paramBoolean) {
      paramPeriod.uid = Pair.create(getChildUidByChildIndex(i), paramPeriod.uid);
    }
    return paramPeriod;
  }
  
  public int getPreviousWindowIndex(int paramInt1, int paramInt2, boolean paramBoolean)
  {
    int i = getChildIndexByWindowIndex(paramInt1);
    int j = getFirstWindowIndexByChildIndex(i);
    Timeline localTimeline = getTimelineByChildIndex(i);
    int k;
    if (paramInt2 == 2)
    {
      k = 0;
      paramInt1 = localTimeline.getPreviousWindowIndex(paramInt1 - j, k, paramBoolean);
      if (paramInt1 == -1) {
        break label62;
      }
      paramInt1 = j + paramInt1;
    }
    for (;;)
    {
      return paramInt1;
      k = paramInt2;
      break;
      label62:
      for (paramInt1 = getPreviousChildIndex(i, paramBoolean); (paramInt1 != -1) && (getTimelineByChildIndex(paramInt1).isEmpty()); paramInt1 = getPreviousChildIndex(paramInt1, paramBoolean)) {}
      if (paramInt1 != -1) {
        paramInt1 = getFirstWindowIndexByChildIndex(paramInt1) + getTimelineByChildIndex(paramInt1).getLastWindowIndex(paramBoolean);
      } else if (paramInt2 == 2) {
        paramInt1 = getLastWindowIndex(paramBoolean);
      } else {
        paramInt1 = -1;
      }
    }
  }
  
  protected abstract Timeline getTimelineByChildIndex(int paramInt);
  
  public final Timeline.Window getWindow(int paramInt, Timeline.Window paramWindow, boolean paramBoolean, long paramLong)
  {
    int i = getChildIndexByWindowIndex(paramInt);
    int j = getFirstWindowIndexByChildIndex(i);
    int k = getFirstPeriodIndexByChildIndex(i);
    getTimelineByChildIndex(i).getWindow(paramInt - j, paramWindow, paramBoolean, paramLong);
    paramWindow.firstPeriodIndex += k;
    paramWindow.lastPeriodIndex += k;
    return paramWindow;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/AbstractConcatenatedTimeline.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */