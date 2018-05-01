package org.telegram.messenger.exoplayer2.source;

import java.util.Arrays;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class TrackGroup
{
  private final Format[] formats;
  private int hashCode;
  public final int length;
  
  public TrackGroup(Format... paramVarArgs)
  {
    if (paramVarArgs.length > 0) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      this.formats = paramVarArgs;
      this.length = paramVarArgs.length;
      return;
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
        paramObject = (TrackGroup)paramObject;
        if ((this.length != ((TrackGroup)paramObject).length) || (!Arrays.equals(this.formats, ((TrackGroup)paramObject).formats))) {
          bool = false;
        }
      }
    }
  }
  
  public Format getFormat(int paramInt)
  {
    return this.formats[paramInt];
  }
  
  public int hashCode()
  {
    if (this.hashCode == 0) {
      this.hashCode = (Arrays.hashCode(this.formats) + 527);
    }
    return this.hashCode;
  }
  
  public int indexOf(Format paramFormat)
  {
    int i = 0;
    if (i < this.formats.length) {
      if (paramFormat != this.formats[i]) {}
    }
    for (;;)
    {
      return i;
      i++;
      break;
      i = -1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/TrackGroup.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */