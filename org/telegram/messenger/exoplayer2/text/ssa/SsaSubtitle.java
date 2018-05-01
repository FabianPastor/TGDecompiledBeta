package org.telegram.messenger.exoplayer2.text.ssa;

import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.Subtitle;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class SsaSubtitle
  implements Subtitle
{
  private final long[] cueTimesUs;
  private final Cue[] cues;
  
  public SsaSubtitle(Cue[] paramArrayOfCue, long[] paramArrayOfLong)
  {
    this.cues = paramArrayOfCue;
    this.cueTimesUs = paramArrayOfLong;
  }
  
  public List<Cue> getCues(long paramLong)
  {
    int i = Util.binarySearchFloor(this.cueTimesUs, paramLong, true, false);
    if ((i == -1) || (this.cues[i] == null)) {}
    for (List localList = Collections.emptyList();; localList = Collections.singletonList(this.cues[i])) {
      return localList;
    }
  }
  
  public long getEventTime(int paramInt)
  {
    boolean bool1 = true;
    if (paramInt >= 0)
    {
      bool2 = true;
      Assertions.checkArgument(bool2);
      if (paramInt >= this.cueTimesUs.length) {
        break label39;
      }
    }
    label39:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      Assertions.checkArgument(bool2);
      return this.cueTimesUs[paramInt];
      bool2 = false;
      break;
    }
  }
  
  public int getEventTimeCount()
  {
    return this.cueTimesUs.length;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    int i = Util.binarySearchCeil(this.cueTimesUs, paramLong, false, false);
    if (i < this.cueTimesUs.length) {}
    for (;;)
    {
      return i;
      i = -1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/ssa/SsaSubtitle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */