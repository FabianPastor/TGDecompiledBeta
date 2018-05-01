package org.telegram.messenger.exoplayer2.source.smoothstreaming.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.DownloaderConstructorHelper;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloader;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloader.Segment;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.TrackKey;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;

public final class SsDownloader
  extends SegmentDownloader<SsManifest, TrackKey>
{
  public SsDownloader(Uri paramUri, DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    super(paramUri, paramDownloaderConstructorHelper);
  }
  
  protected List<SegmentDownloader.Segment> getAllSegments(DataSource paramDataSource, SsManifest paramSsManifest, boolean paramBoolean)
    throws InterruptedException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    for (int i = 0; i < paramSsManifest.streamElements.length; i++)
    {
      SsManifest.StreamElement localStreamElement = paramSsManifest.streamElements[i];
      for (int j = 0; j < localStreamElement.formats.length; j++) {
        localArrayList.addAll(getSegments(paramDataSource, paramSsManifest, new TrackKey[] { new TrackKey(i, j) }, paramBoolean));
      }
    }
    return localArrayList;
  }
  
  public SsManifest getManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException
  {
    paramDataSource = new ParsingLoadable(paramDataSource, new DataSpec(paramUri, 3), 4, new SsManifestParser());
    paramDataSource.load();
    return (SsManifest)paramDataSource.getResult();
  }
  
  protected List<SegmentDownloader.Segment> getSegments(DataSource paramDataSource, SsManifest paramSsManifest, TrackKey[] paramArrayOfTrackKey, boolean paramBoolean)
    throws InterruptedException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    int i = paramArrayOfTrackKey.length;
    for (int j = 0; j < i; j++)
    {
      paramDataSource = paramArrayOfTrackKey[j];
      SsManifest.StreamElement localStreamElement = paramSsManifest.streamElements[paramDataSource.streamElementIndex];
      for (int k = 0; k < localStreamElement.chunkCount; k++) {
        localArrayList.add(new SegmentDownloader.Segment(localStreamElement.getStartTimeUs(k), new DataSpec(localStreamElement.buildRequestUri(paramDataSource.trackIndex, k))));
      }
    }
    return localArrayList;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/smoothstreaming/offline/SsDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */