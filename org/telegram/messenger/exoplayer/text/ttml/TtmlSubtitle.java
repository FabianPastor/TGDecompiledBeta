package org.telegram.messenger.exoplayer.text.ttml;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer.text.Cue;
import org.telegram.messenger.exoplayer.text.Subtitle;
import org.telegram.messenger.exoplayer.util.Util;

public final class TtmlSubtitle
  implements Subtitle
{
  private final long[] eventTimesUs;
  private final Map<String, TtmlStyle> globalStyles;
  private final Map<String, TtmlRegion> regionMap;
  private final TtmlNode root;
  
  public TtmlSubtitle(TtmlNode paramTtmlNode, Map<String, TtmlStyle> paramMap, Map<String, TtmlRegion> paramMap1)
  {
    this.root = paramTtmlNode;
    this.regionMap = paramMap1;
    if (paramMap != null) {}
    for (paramMap = Collections.unmodifiableMap(paramMap);; paramMap = Collections.emptyMap())
    {
      this.globalStyles = paramMap;
      this.eventTimesUs = paramTtmlNode.getEventTimesUs();
      return;
    }
  }
  
  public List<Cue> getCues(long paramLong)
  {
    return this.root.getCues(paramLong, this.globalStyles, this.regionMap);
  }
  
  public long getEventTime(int paramInt)
  {
    return this.eventTimesUs[paramInt];
  }
  
  public int getEventTimeCount()
  {
    return this.eventTimesUs.length;
  }
  
  Map<String, TtmlStyle> getGlobalStyles()
  {
    return this.globalStyles;
  }
  
  public long getLastEventTime()
  {
    if (this.eventTimesUs.length == 0) {
      return -1L;
    }
    return this.eventTimesUs[(this.eventTimesUs.length - 1)];
  }
  
  public int getNextEventTimeIndex(long paramLong)
  {
    int i = Util.binarySearchCeil(this.eventTimesUs, paramLong, false, false);
    if (i < this.eventTimesUs.length) {
      return i;
    }
    return -1;
  }
  
  TtmlNode getRoot()
  {
    return this.root;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/text/ttml/TtmlSubtitle.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */