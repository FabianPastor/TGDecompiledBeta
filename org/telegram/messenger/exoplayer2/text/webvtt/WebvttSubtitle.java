package org.telegram.messenger.exoplayer2.text.webvtt;

import android.text.SpannableStringBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.text.Cue;
import org.telegram.messenger.exoplayer2.text.Subtitle;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

final class WebvttSubtitle
  implements Subtitle
{
  private final long[] cueTimesUs;
  private final List<WebvttCue> cues;
  private final int numCues;
  private final long[] sortedCueTimesUs;
  
  public WebvttSubtitle(List<WebvttCue> paramList)
  {
    this.cues = paramList;
    this.numCues = paramList.size();
    this.cueTimesUs = new long[this.numCues * 2];
    for (int i = 0; i < this.numCues; i++)
    {
      WebvttCue localWebvttCue = (WebvttCue)paramList.get(i);
      int j = i * 2;
      this.cueTimesUs[j] = localWebvttCue.startTime;
      this.cueTimesUs[(j + 1)] = localWebvttCue.endTime;
    }
    this.sortedCueTimesUs = Arrays.copyOf(this.cueTimesUs, this.cueTimesUs.length);
    Arrays.sort(this.sortedCueTimesUs);
  }
  
  public List<Cue> getCues(long paramLong)
  {
    Object localObject1 = null;
    Object localObject2 = null;
    Object localObject3 = null;
    int i = 0;
    if (i < this.numCues)
    {
      Object localObject4 = localObject2;
      Object localObject5 = localObject1;
      Object localObject6 = localObject3;
      if (this.cueTimesUs[(i * 2)] <= paramLong)
      {
        localObject4 = localObject2;
        localObject5 = localObject1;
        localObject6 = localObject3;
        if (paramLong < this.cueTimesUs[(i * 2 + 1)])
        {
          localObject5 = localObject1;
          if (localObject1 == null) {
            localObject5 = new ArrayList();
          }
          localObject4 = (WebvttCue)this.cues.get(i);
          if (!((WebvttCue)localObject4).isNormalCue()) {
            break label210;
          }
          if (localObject2 != null) {
            break label138;
          }
          localObject6 = localObject3;
        }
      }
      for (;;)
      {
        i++;
        localObject2 = localObject4;
        localObject1 = localObject5;
        localObject3 = localObject6;
        break;
        label138:
        if (localObject3 == null)
        {
          localObject6 = new SpannableStringBuilder();
          ((SpannableStringBuilder)localObject6).append(((WebvttCue)localObject2).text).append("\n").append(((WebvttCue)localObject4).text);
          localObject4 = localObject2;
        }
        else
        {
          ((SpannableStringBuilder)localObject3).append("\n").append(((WebvttCue)localObject4).text);
          localObject4 = localObject2;
          localObject6 = localObject3;
          continue;
          label210:
          ((ArrayList)localObject5).add(localObject4);
          localObject4 = localObject2;
          localObject6 = localObject3;
        }
      }
    }
    if (localObject3 != null)
    {
      ((ArrayList)localObject1).add(new WebvttCue((CharSequence)localObject3));
      if (localObject1 == null) {
        break label269;
      }
    }
    for (;;)
    {
      return (List<Cue>)localObject1;
      if (localObject2 == null) {
        break;
      }
      ((ArrayList)localObject1).add(localObject2);
      break;
      label269:
      localObject1 = Collections.emptyList();
    }
  }
  
  public long getEventTime(int paramInt)
  {
    boolean bool1 = true;
    if (paramInt >= 0)
    {
      bool2 = true;
      Assertions.checkArgument(bool2);
      if (paramInt >= this.sortedCueTimesUs.length) {
        break label39;
      }
    }
    label39:
    for (boolean bool2 = bool1;; bool2 = false)
    {
      Assertions.checkArgument(bool2);
      return this.sortedCueTimesUs[paramInt];
      bool2 = false;
      break;
    }
  }
  
  public int getEventTimeCount()
  {
    return this.sortedCueTimesUs.length;
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    int i = Util.binarySearchCeil(this.sortedCueTimesUs, paramLong, false, false);
    if (i < this.sortedCueTimesUs.length) {}
    for (;;)
    {
      return i;
      i = -1;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/text/webvtt/WebvttSubtitle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */