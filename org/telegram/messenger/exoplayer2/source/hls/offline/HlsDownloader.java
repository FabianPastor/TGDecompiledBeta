package org.telegram.messenger.exoplayer2.source.hls.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.DownloaderConstructorHelper;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloader;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloader.Segment;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.util.UriUtil;

public final class HlsDownloader
  extends SegmentDownloader<HlsMasterPlaylist, String>
{
  public HlsDownloader(Uri paramUri, DownloaderConstructorHelper paramDownloaderConstructorHelper)
  {
    super(paramUri, paramDownloaderConstructorHelper);
  }
  
  private static void addSegment(ArrayList<SegmentDownloader.Segment> paramArrayList, HlsMediaPlaylist paramHlsMediaPlaylist, HlsMediaPlaylist.Segment paramSegment, HashSet<Uri> paramHashSet)
    throws IOException, InterruptedException
  {
    long l = paramHlsMediaPlaylist.startTimeUs + paramSegment.relativeStartTimeUs;
    if (paramSegment.fullSegmentEncryptionKeyUri != null)
    {
      Uri localUri = UriUtil.resolveToUri(paramHlsMediaPlaylist.baseUri, paramSegment.fullSegmentEncryptionKeyUri);
      if (paramHashSet.add(localUri)) {
        paramArrayList.add(new SegmentDownloader.Segment(l, new DataSpec(localUri)));
      }
    }
    paramArrayList.add(new SegmentDownloader.Segment(l, new DataSpec(UriUtil.resolveToUri(paramHlsMediaPlaylist.baseUri, paramSegment.url), paramSegment.byterangeOffset, paramSegment.byterangeLength, null)));
  }
  
  private static void extractUrls(List<HlsMasterPlaylist.HlsUrl> paramList, ArrayList<String> paramArrayList)
  {
    for (int i = 0; i < paramList.size(); i++) {
      paramArrayList.add(((HlsMasterPlaylist.HlsUrl)paramList.get(i)).url);
    }
  }
  
  private HlsPlaylist loadManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException
  {
    paramDataSource = new ParsingLoadable(paramDataSource, new DataSpec(paramUri, 3), 4, new HlsPlaylistParser());
    paramDataSource.load();
    return (HlsPlaylist)paramDataSource.getResult();
  }
  
  protected List<SegmentDownloader.Segment> getAllSegments(DataSource paramDataSource, HlsMasterPlaylist paramHlsMasterPlaylist, boolean paramBoolean)
    throws InterruptedException, IOException
  {
    ArrayList localArrayList = new ArrayList();
    extractUrls(paramHlsMasterPlaylist.variants, localArrayList);
    extractUrls(paramHlsMasterPlaylist.audios, localArrayList);
    extractUrls(paramHlsMasterPlaylist.subtitles, localArrayList);
    return getSegments(paramDataSource, paramHlsMasterPlaylist, (String[])localArrayList.toArray(new String[localArrayList.size()]), paramBoolean);
  }
  
  protected HlsMasterPlaylist getManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException
  {
    paramDataSource = loadManifest(paramDataSource, paramUri);
    if ((paramDataSource instanceof HlsMasterPlaylist)) {}
    for (paramDataSource = (HlsMasterPlaylist)paramDataSource;; paramDataSource = HlsMasterPlaylist.createSingleVariantMasterPlaylist(paramDataSource.baseUri)) {
      return paramDataSource;
    }
  }
  
  protected List<SegmentDownloader.Segment> getSegments(DataSource paramDataSource, HlsMasterPlaylist paramHlsMasterPlaylist, String[] paramArrayOfString, boolean paramBoolean)
    throws InterruptedException, IOException
  {
    HashSet localHashSet = new HashSet();
    ArrayList localArrayList = new ArrayList();
    int i = paramArrayOfString.length;
    int j = 0;
    if (j < i)
    {
      Object localObject1 = paramArrayOfString[j];
      Object localObject3 = null;
      Uri localUri = UriUtil.resolveToUri(paramHlsMasterPlaylist.baseUri, (String)localObject1);
      long l;
      try
      {
        localObject1 = (HlsMediaPlaylist)loadManifest(paramDataSource, localUri);
        localObject3 = localObject1;
      }
      catch (IOException localIOException)
      {
        label80:
        while (paramBoolean) {}
        throw localIOException;
      }
      if (localObject3 != null)
      {
        l = ((HlsMediaPlaylist)localObject3).startTimeUs;
        localArrayList.add(new SegmentDownloader.Segment(l, new DataSpec(localUri)));
        if (localObject3 != null) {
          break label133;
        }
      }
      for (;;)
      {
        j++;
        break;
        l = Long.MIN_VALUE;
        break label80;
        label133:
        Object localObject2 = ((HlsMediaPlaylist)localObject3).initializationSegment;
        if (localObject2 != null) {
          addSegment(localArrayList, (HlsMediaPlaylist)localObject3, (HlsMediaPlaylist.Segment)localObject2, localHashSet);
        }
        localObject2 = ((HlsMediaPlaylist)localObject3).segments;
        for (int k = 0; k < ((List)localObject2).size(); k++) {
          addSegment(localArrayList, (HlsMediaPlaylist)localObject3, (HlsMediaPlaylist.Segment)((List)localObject2).get(k), localHashSet);
        }
      }
    }
    return localArrayList;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/offline/HlsDownloader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */