package org.telegram.messenger.exoplayer2.source.dash.manifest;

import android.net.Uri;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;

public class DashManifest
{
  public final long availabilityStartTimeMs;
  public final long durationMs;
  public final boolean dynamic;
  public final Uri location;
  public final long minBufferTimeMs;
  public final long minUpdatePeriodMs;
  private final List<Period> periods;
  public final long publishTimeMs;
  public final long suggestedPresentationDelayMs;
  public final long timeShiftBufferDepthMs;
  public final UtcTimingElement utcTiming;
  
  public DashManifest(long paramLong1, long paramLong2, long paramLong3, boolean paramBoolean, long paramLong4, long paramLong5, long paramLong6, long paramLong7, UtcTimingElement paramUtcTimingElement, Uri paramUri, List<Period> paramList)
  {
    this.availabilityStartTimeMs = paramLong1;
    this.durationMs = paramLong2;
    this.minBufferTimeMs = paramLong3;
    this.dynamic = paramBoolean;
    this.minUpdatePeriodMs = paramLong4;
    this.timeShiftBufferDepthMs = paramLong5;
    this.suggestedPresentationDelayMs = paramLong6;
    this.publishTimeMs = paramLong7;
    this.utcTiming = paramUtcTimingElement;
    this.location = paramUri;
    paramUtcTimingElement = paramList;
    if (paramList == null) {
      paramUtcTimingElement = Collections.emptyList();
    }
    this.periods = paramUtcTimingElement;
  }
  
  private static ArrayList<AdaptationSet> copyAdaptationSets(List<AdaptationSet> paramList, LinkedList<RepresentationKey> paramLinkedList)
  {
    Object localObject = (RepresentationKey)paramLinkedList.poll();
    int i = ((RepresentationKey)localObject).periodIndex;
    ArrayList localArrayList1 = new ArrayList();
    RepresentationKey localRepresentationKey;
    do
    {
      int j = ((RepresentationKey)localObject).adaptationSetIndex;
      AdaptationSet localAdaptationSet = (AdaptationSet)paramList.get(j);
      List localList = localAdaptationSet.representations;
      ArrayList localArrayList2 = new ArrayList();
      do
      {
        localArrayList2.add((Representation)localList.get(((RepresentationKey)localObject).representationIndex));
        localRepresentationKey = (RepresentationKey)paramLinkedList.poll();
        if (localRepresentationKey.periodIndex != i) {
          break;
        }
        localObject = localRepresentationKey;
      } while (localRepresentationKey.adaptationSetIndex == j);
      localArrayList1.add(new AdaptationSet(localAdaptationSet.id, localAdaptationSet.type, localArrayList2, localAdaptationSet.accessibilityDescriptors, localAdaptationSet.supplementalProperties));
      localObject = localRepresentationKey;
    } while (localRepresentationKey.periodIndex == i);
    paramLinkedList.addFirst(localRepresentationKey);
    return localArrayList1;
  }
  
  public final DashManifest copy(List<RepresentationKey> paramList)
  {
    LinkedList localLinkedList = new LinkedList(paramList);
    Collections.sort(localLinkedList);
    localLinkedList.add(new RepresentationKey(-1, -1, -1));
    ArrayList localArrayList = new ArrayList();
    long l1 = 0L;
    int i = 0;
    if (i < getPeriodCount())
    {
      long l2;
      if (((RepresentationKey)localLinkedList.peek()).periodIndex != i)
      {
        l2 = getPeriodDurationMs(i);
        l3 = l1;
        if (l2 == -9223372036854775807L) {}
      }
      for (l3 = l1 + l2;; l3 = l1)
      {
        i++;
        l1 = l3;
        break;
        Period localPeriod = getPeriod(i);
        paramList = copyAdaptationSets(localPeriod.adaptationSets, localLinkedList);
        localArrayList.add(new Period(localPeriod.id, localPeriod.startMs - l1, paramList, localPeriod.eventStreams));
      }
    }
    if (this.durationMs != -9223372036854775807L) {}
    for (long l3 = this.durationMs - l1;; l3 = -9223372036854775807L) {
      return new DashManifest(this.availabilityStartTimeMs, l3, this.minBufferTimeMs, this.dynamic, this.minUpdatePeriodMs, this.timeShiftBufferDepthMs, this.suggestedPresentationDelayMs, this.publishTimeMs, this.utcTiming, this.location, localArrayList);
    }
  }
  
  public final Period getPeriod(int paramInt)
  {
    return (Period)this.periods.get(paramInt);
  }
  
  public final int getPeriodCount()
  {
    return this.periods.size();
  }
  
  public final long getPeriodDurationMs(int paramInt)
  {
    long l = -9223372036854775807L;
    if (paramInt == this.periods.size() - 1) {
      if (this.durationMs != -9223372036854775807L) {}
    }
    for (;;)
    {
      return l;
      l = this.durationMs - ((Period)this.periods.get(paramInt)).startMs;
      continue;
      l = ((Period)this.periods.get(paramInt + 1)).startMs - ((Period)this.periods.get(paramInt)).startMs;
    }
  }
  
  public final long getPeriodDurationUs(int paramInt)
  {
    return C.msToUs(getPeriodDurationMs(paramInt));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/manifest/DashManifest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */