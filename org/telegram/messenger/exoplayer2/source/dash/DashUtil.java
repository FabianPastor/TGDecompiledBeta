package org.telegram.messenger.exoplayer2.source.dash;

import android.net.Uri;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor;
import org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper;
import org.telegram.messenger.exoplayer2.source.chunk.InitializationChunk;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifestParser;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Period;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;

public final class DashUtil
{
  private static Representation getFirstRepresentation(Period paramPeriod, int paramInt)
  {
    Object localObject = null;
    paramInt = paramPeriod.getAdaptationSetIndex(paramInt);
    if (paramInt == -1)
    {
      paramPeriod = (Period)localObject;
      return paramPeriod;
    }
    paramPeriod = ((AdaptationSet)paramPeriod.adaptationSets.get(paramInt)).representations;
    if (paramPeriod.isEmpty()) {}
    for (paramPeriod = null;; paramPeriod = (Representation)paramPeriod.get(0)) {
      break;
    }
  }
  
  public static ChunkIndex loadChunkIndex(DataSource paramDataSource, int paramInt, Representation paramRepresentation)
    throws IOException, InterruptedException
  {
    paramDataSource = loadInitializationData(paramDataSource, paramInt, paramRepresentation, true);
    if (paramDataSource == null) {}
    for (paramDataSource = null;; paramDataSource = (ChunkIndex)paramDataSource.getSeekMap()) {
      return paramDataSource;
    }
  }
  
  public static DrmInitData loadDrmInitData(DataSource paramDataSource, Period paramPeriod)
    throws IOException, InterruptedException
  {
    int i = 2;
    Representation localRepresentation = getFirstRepresentation(paramPeriod, 2);
    Object localObject = localRepresentation;
    if (localRepresentation == null)
    {
      i = 1;
      paramPeriod = getFirstRepresentation(paramPeriod, 1);
      localObject = paramPeriod;
      if (paramPeriod == null) {
        paramDataSource = null;
      }
    }
    for (;;)
    {
      return paramDataSource;
      paramPeriod = ((Representation)localObject).format;
      paramDataSource = loadSampleFormat(paramDataSource, i, (Representation)localObject);
      if (paramDataSource == null) {
        paramDataSource = paramPeriod.drmInitData;
      } else {
        paramDataSource = paramDataSource.copyWithManifestFormatInfo(paramPeriod).drmInitData;
      }
    }
  }
  
  private static ChunkExtractorWrapper loadInitializationData(DataSource paramDataSource, int paramInt, Representation paramRepresentation, boolean paramBoolean)
    throws IOException, InterruptedException
  {
    RangedUri localRangedUri1 = paramRepresentation.getInitializationUri();
    if (localRangedUri1 == null) {}
    ChunkExtractorWrapper localChunkExtractorWrapper;
    RangedUri localRangedUri2;
    for (paramDataSource = null;; paramDataSource = null)
    {
      return paramDataSource;
      localChunkExtractorWrapper = newWrappedExtractor(paramInt, paramRepresentation.format);
      if (!paramBoolean) {
        break label95;
      }
      localRangedUri2 = paramRepresentation.getIndexUri();
      if (localRangedUri2 != null) {
        break;
      }
    }
    RangedUri localRangedUri3 = localRangedUri1.attemptMerge(localRangedUri2, paramRepresentation.baseUrl);
    RangedUri localRangedUri4 = localRangedUri3;
    if (localRangedUri3 == null) {
      loadInitializationData(paramDataSource, paramRepresentation, localChunkExtractorWrapper, localRangedUri1);
    }
    label95:
    for (localRangedUri4 = localRangedUri2;; localRangedUri4 = localRangedUri1)
    {
      loadInitializationData(paramDataSource, paramRepresentation, localChunkExtractorWrapper, localRangedUri4);
      paramDataSource = localChunkExtractorWrapper;
      break;
    }
  }
  
  private static void loadInitializationData(DataSource paramDataSource, Representation paramRepresentation, ChunkExtractorWrapper paramChunkExtractorWrapper, RangedUri paramRangedUri)
    throws IOException, InterruptedException
  {
    new InitializationChunk(paramDataSource, new DataSpec(paramRangedUri.resolveUri(paramRepresentation.baseUrl), paramRangedUri.start, paramRangedUri.length, paramRepresentation.getCacheKey()), paramRepresentation.format, 0, null, paramChunkExtractorWrapper).load();
  }
  
  public static DashManifest loadManifest(DataSource paramDataSource, Uri paramUri)
    throws IOException
  {
    paramDataSource = new ParsingLoadable(paramDataSource, new DataSpec(paramUri, 3), 4, new DashManifestParser());
    paramDataSource.load();
    return (DashManifest)paramDataSource.getResult();
  }
  
  public static Format loadSampleFormat(DataSource paramDataSource, int paramInt, Representation paramRepresentation)
    throws IOException, InterruptedException
  {
    paramDataSource = loadInitializationData(paramDataSource, paramInt, paramRepresentation, false);
    if (paramDataSource == null) {}
    for (paramDataSource = null;; paramDataSource = paramDataSource.getSampleFormats()[0]) {
      return paramDataSource;
    }
  }
  
  private static ChunkExtractorWrapper newWrappedExtractor(int paramInt, Format paramFormat)
  {
    Object localObject = paramFormat.containerMimeType;
    int i;
    if ((((String)localObject).startsWith("video/webm")) || (((String)localObject).startsWith("audio/webm")))
    {
      i = 1;
      if (i == 0) {
        break label53;
      }
    }
    label53:
    for (localObject = new MatroskaExtractor();; localObject = new FragmentedMp4Extractor())
    {
      return new ChunkExtractorWrapper((Extractor)localObject, paramInt, paramFormat);
      i = 0;
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/dash/DashUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */