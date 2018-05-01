package org.telegram.messenger.exoplayer2.trackselection;

import java.util.Arrays;

public final class TrackSelectionArray
{
  private int hashCode;
  public final int length;
  private final TrackSelection[] trackSelections;
  
  public TrackSelectionArray(TrackSelection... paramVarArgs)
  {
    this.trackSelections = paramVarArgs;
    this.length = paramVarArgs.length;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool;
    if (this == paramObject) {
      bool = true;
    }
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (TrackSelectionArray)paramObject;
        bool = Arrays.equals(this.trackSelections, ((TrackSelectionArray)paramObject).trackSelections);
      }
    }
  }
  
  public TrackSelection get(int paramInt)
  {
    return this.trackSelections[paramInt];
  }
  
  public TrackSelection[] getAll()
  {
    return (TrackSelection[])this.trackSelections.clone();
  }
  
  public int hashCode()
  {
    if (this.hashCode == 0) {
      this.hashCode = (Arrays.hashCode(this.trackSelections) + 527);
    }
    return this.hashCode;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/trackselection/TrackSelectionArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */