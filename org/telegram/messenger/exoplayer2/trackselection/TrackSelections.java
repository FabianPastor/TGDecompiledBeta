package org.telegram.messenger.exoplayer2.trackselection;

import java.util.Arrays;

public final class TrackSelections<T>
{
  private int hashCode;
  public final T info;
  public final int length;
  private final TrackSelection[] trackSelections;
  
  public TrackSelections(T paramT, TrackSelection... paramVarArgs)
  {
    this.info = paramT;
    this.trackSelections = paramVarArgs;
    this.length = paramVarArgs.length;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (TrackSelections)paramObject;
    return Arrays.equals(this.trackSelections, ((TrackSelections)paramObject).trackSelections);
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/trackselection/TrackSelections.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */