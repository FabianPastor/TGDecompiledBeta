package org.telegram.messenger.exoplayer2.trackselection;

import android.os.SystemClock;
import java.util.Random;
import org.telegram.messenger.exoplayer2.source.TrackGroup;

public final class RandomTrackSelection
  extends BaseTrackSelection
{
  private final Random random;
  private int selectedIndex;
  
  public RandomTrackSelection(TrackGroup paramTrackGroup, int... paramVarArgs)
  {
    super(paramTrackGroup, paramVarArgs);
    this.random = new Random();
    this.selectedIndex = this.random.nextInt(this.length);
  }
  
  public RandomTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, long paramLong)
  {
    this(paramTrackGroup, paramArrayOfInt, new Random(paramLong));
  }
  
  public RandomTrackSelection(TrackGroup paramTrackGroup, int[] paramArrayOfInt, Random paramRandom)
  {
    super(paramTrackGroup, paramArrayOfInt);
    this.random = paramRandom;
    this.selectedIndex = paramRandom.nextInt(this.length);
  }
  
  public int getSelectedIndex()
  {
    return this.selectedIndex;
  }
  
  public Object getSelectionData()
  {
    return null;
  }
  
  public int getSelectionReason()
  {
    return 3;
  }
  
  public void updateSelectedTrack(long paramLong1, long paramLong2, long paramLong3)
  {
    paramLong1 = SystemClock.elapsedRealtime();
    int i = 0;
    int j = 0;
    int k;
    while (j < this.length)
    {
      k = i;
      if (!isBlacklisted(j, paramLong1)) {
        k = i + 1;
      }
      j++;
      i = k;
    }
    this.selectedIndex = this.random.nextInt(i);
    if (i != this.length)
    {
      k = 0;
      j = 0;
    }
    for (;;)
    {
      if (j < this.length)
      {
        i = k;
        if (isBlacklisted(j, paramLong1)) {
          break label122;
        }
        if (this.selectedIndex == k) {
          this.selectedIndex = j;
        }
      }
      else
      {
        return;
      }
      i = k + 1;
      label122:
      j++;
      k = i;
    }
  }
  
  public static final class Factory
    implements TrackSelection.Factory
  {
    private final Random random;
    
    public Factory()
    {
      this.random = new Random();
    }
    
    public Factory(int paramInt)
    {
      this.random = new Random(paramInt);
    }
    
    public RandomTrackSelection createTrackSelection(TrackGroup paramTrackGroup, int... paramVarArgs)
    {
      return new RandomTrackSelection(paramTrackGroup, paramVarArgs, this.random);
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/trackselection/RandomTrackSelection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */