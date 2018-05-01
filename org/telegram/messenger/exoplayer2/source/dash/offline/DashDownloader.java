package org.telegram.messenger.exoplayer2.source.dash.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.offline.DownloadException;
import org.telegram.messenger.exoplayer2.offline.DownloaderConstructorHelper;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloader;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloader.Segment;
import org.telegram.messenger.exoplayer2.source.dash.DashSegmentIndex;
import org.telegram.messenger.exoplayer2.source.dash.DashUtil;
import org.telegram.messenger.exoplayer2.source.dash.DashWrappingSegmentIndex;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Period;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RepresentationKey;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;

public final class DashDownloader
  extends SegmentDownloader<DashManifest, RepresentationKey>
{
  public DashDownloader(Uri paramUri, DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    super(paramUri, paramDownloaderConstructorHelper);
  }
  
  private static void addSegment(ArrayList<SegmentDownloader.Segment> paramArrayList, long paramLong, String paramString, RangedUri paramRangedUri)
  {
    paramArrayList.add(new SegmentDownloader.Segment(paramLong, new DataSpec(paramRangedUri.resolveUri(paramString), paramRangedUri.start, paramRangedUri.length, null)));
  }
  
  private DashSegmentIndex getSegmentIndex(DataSource paramDataSource, DashManifest paramDashManifest, RepresentationKey paramRepresentationKey)
    throws IOException, InterruptedException
  {
    AdaptationSet localAdaptationSet = (AdaptationSet)paramDashManifest.getPeriod(paramRepresentationKey.periodIndex).adaptationSets.get(paramRepresentationKey.adaptationSetIndex);
    paramRepresentationKey = (Representation)localAdaptationSet.representations.get(paramRepresentationKey.representationIndex);
    paramDashManifest = paramRepresentationKey.getIndex();
    if (paramDashManifest != null)
    {
      paramDataSource = paramDashManifest;
      return paramDataSource;
    }
    paramDataSource = DashUtil.loadChunkIndex(paramDataSource, localAdaptationSet.type, paramRepresentationKey);
    if (paramDataSource == null) {}
    for (paramDataSource = null;; paramDataSource = new DashWrappingSegmentIndex(paramDataSource)) {
      break;
    }
  }
  
  protected List<SegmentDownloader.Segment> getAllSegments(DataSource paramDataSource, DashManifest paramDashManifest, boolean paramBoolean)
    throws InterruptedException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramDashManifest.getPeriodCount(); i++)
    {
      List localList = paramDashManifest.getPeriod(i).adaptationSets;
      for (int j = 0; j < localList.size(); j++)
      {
        RepresentationKey[] arrayOfRepresentationKey = new RepresentationKey[((AdaptationSet)localList.get(j)).representations.size()];
        for (int k = 0; k < arrayOfRepresentationKey.length; k++) {
          arrayOfRepresentationKey[k] = new RepresentationKey(i, j, k);
        }
        localArrayList.addAll(getSegments(paramDataSource, paramDashManifest, arrayOfRepresentationKey, paramBoolean));
      }
    }
    return localArrayList;
  }
  
  public DashManifest getManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException
  {
    return DashUtil.loadManifest(paramDataSource, paramUri);
  }
  
  protected List<SegmentDownloader.Segment> getSegments(DataSource paramDataSource, DashManifest paramDashManifest, RepresentationKey[] paramArrayOfRepresentationKey, boolean paramBoolean)
    throws InterruptedException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramArrayOfRepresentationKey.length;
    int j = 0;
    if (j < i)
    {
      Object localObject1 = paramArrayOfRepresentationKey[j];
      Object localObject3;
      try
      {
        Object localObject2 = getSegmentIndex(paramDataSource, paramDashManifest, (RepresentationKey)localObject1);
        if (localObject2 == null)
        {
          localObject2 = new org/telegram/messenger/exoplayer2/offline/DownloadException;
          localObject3 = new java/lang/StringBuilder;
          ((StringBuilder)localObject3).<init>();
          ((DownloadException)localObject2).<init>("No index for representation: " + localObject1);
          throw ((Throwable)localObject2);
        }
      }
      catch (IOException localIOException)
      {
        if (!paramBoolean) {}
      }
      for (;;)
      {
        j++;
        break;
        throw localIOException;
        int k = localIOException.getSegmentCount(-9223372036854775807L);
        if (k == -1) {
          throw new DownloadException("Unbounded index for representation: " + localObject1);
        }
        localObject3 = paramDashManifest.getPeriod(((RepresentationKey)localObject1).periodIndex);
        localObject1 = (Representation)((AdaptationSet)((Period)localObject3).adaptationSets.get(((RepresentationKey)localObject1).adaptationSetIndex)).representations.get(((RepresentationKey)localObject1).representationIndex);
        long l = C.msToUs(((Period)localObject3).startMs);
        localObject3 = ((Representation)localObject1).baseUrl;
        RangedUri localRangedUri = ((Representation)localObject1).getInitializationUri();
        if (localRangedUri != null) {
          addSegment(localArrayList, l, (String)localObject3, localRangedUri);
        }
        localObject1 = ((Representation)localObject1).getIndexUri();
        if (localObject1 != null) {
          addSegment(localArrayList, l, (String)localObject3, (RangedUri)localObject1);
        }
        int m = localIOException.getFirstSegmentNum();
        for (int n = m; n <= m + k - 1; n++) {
          addSegment(localArrayList, localIOException.getTimeUs(n) + l, (String)localObject3, localIOException.getSegmentUrl(n));
        }
      }
    }
    return localArrayList;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/offline/DashDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */