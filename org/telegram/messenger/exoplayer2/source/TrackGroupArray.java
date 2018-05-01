package org.telegram.messenger.exoplayer2.source;

import java.util.Arrays;

public final class TrackGroupArray
{
  public static final TrackGroupArray EMPTY = new TrackGroupArray(new TrackGroup[0]);
  private int hashCode;
  public final int length;
  private final TrackGroup[] trackGroups;
  
  public TrackGroupArray(TrackGroup... paramVarArgs)
  {
    this.trackGroups = paramVarArgs;
    this.length = paramVarArgs.length;
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
        paramObject = (TrackGroupArray)paramObject;
        if ((this.length != ((TrackGroupArray)paramObject).length) || (!Arrays.equals(this.trackGroups, ((TrackGroupArray)paramObject).trackGroups))) {
          bool = false;
        }
      }
    }
  }
  
  public TrackGroup get(int paramInt)
  {
    return this.trackGroups[paramInt];
  }
  
  public int hashCode()
  {
    if (this.hashCode == 0) {
      this.hashCode = Arrays.hashCode(this.trackGroups);
    }
    return this.hashCode;
  }
  
  public int indexOf(TrackGroup paramTrackGroup)
  {
    int i = 0;
    if (i < this.length) {
      if (this.trackGroups[i] != paramTrackGroup) {}
    }
    for (;;)
    {
      return i;
      i++;
      break;
      i = -1;
    }
  }
  
  public boolean isEmpty()
  {
    if (this.length == 0) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/TrackGroupArray.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */