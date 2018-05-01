package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.util.Pair;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public abstract interface HlsExtractorFactory
{
  public static final HlsExtractorFactory DEFAULT = new DefaultHlsExtractorFactory();
  
  public abstract Pair<Extractor, Boolean> createExtractor(Extractor paramExtractor, Uri paramUri, Format paramFormat, List<Format> paramList, DrmInitData paramDrmInitData, TimestampAdjuster paramTimestampAdjuster);
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/HlsExtractorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */