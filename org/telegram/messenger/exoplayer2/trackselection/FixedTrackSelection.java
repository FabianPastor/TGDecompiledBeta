package org.telegram.messenger.exoplayer2.trackselection;

import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class FixedTrackSelection
  extends BaseTrackSelection
{
  private final Object data;
  private final int reason;
  
  public FixedTrackSelection(TrackGroup paramTrackGroup, int paramInt)
  {
    this(paramTrackGroup, paramInt, 0, null);
  }
  
  public FixedTrackSelection(TrackGroup paramTrackGroup, int paramInt1, int paramInt2, Object paramObject)
  {
    super(paramTrackGroup, new int[] { paramInt1 });
    this.reason = paramInt2;
    this.data = paramObject;
  }
  
  public int getSelectedIndex()
  {
    return 0;
  }
  
  public Object getSelectionData()
  {
    return this.data;
  }
  
  public int getSelectionReason()
  {
    return this.reason;
  }
  
  public void updateSelectedTrack(long paramLong1, long paramLong2, long paramLong3) {}
  
  public static final class Factory
    implements TrackSelection.Factory
  {
    private final Object data;
    private final int reason;
    
    public Factory()
    {
      this.reason = 0;
      this.data = null;
    }
    
    public Factory(int paramInt, Object paramObject)
    {
      this.reason = paramInt;
      this.data = paramObject;
    }
    
    public FixedTrackSelection createTrackSelection(TrackGroup paramTrackGroup, int... paramVarArgs)
    {
      boolean bool = true;
      if (paramVarArgs.length == 1) {}
      for (;;)
      {
        Assertions.checkArgument(bool);
        return new FixedTrackSelection(paramTrackGroup, paramVarArgs[0], this.reason, this.data);
        bool = false;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/trackselection/FixedTrackSelection.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */