package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.mp3.Mp3Extractor;
import org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer2.extractor.ts.Ac3Extractor;
import org.telegram.messenger.exoplayer2.extractor.ts.AdtsExtractor;
import org.telegram.messenger.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class DefaultHlsExtractorFactory
  implements HlsExtractorFactory
{
  public static final String AAC_FILE_EXTENSION = ".aac";
  public static final String AC3_FILE_EXTENSION = ".ac3";
  public static final String EC3_FILE_EXTENSION = ".ec3";
  public static final String M4_FILE_EXTENSION_PREFIX = ".m4";
  public static final String MP3_FILE_EXTENSION = ".mp3";
  public static final String MP4_FILE_EXTENSION = ".mp4";
  public static final String MP4_FILE_EXTENSION_PREFIX = ".mp4";
  public static final String VTT_FILE_EXTENSION = ".vtt";
  public static final String WEBVTT_FILE_EXTENSION = ".webvtt";
  
  public Pair<Extractor, Boolean> createExtractor(Extractor paramExtractor, Uri paramUri, Format paramFormat, List<Format> paramList, DrmInitData paramDrmInitData, TimestampAdjuster paramTimestampAdjuster)
  {
    paramUri = paramUri.getLastPathSegment();
    boolean bool = false;
    if (("text/vtt".equals(paramFormat.sampleMimeType)) || (paramUri.endsWith(".webvtt")) || (paramUri.endsWith(".vtt"))) {
      paramExtractor = new WebvttExtractor(paramFormat.language, paramTimestampAdjuster);
    }
    do
    {
      for (;;)
      {
        return Pair.create(paramExtractor, Boolean.valueOf(bool));
        if (paramUri.endsWith(".aac"))
        {
          bool = true;
          paramExtractor = new AdtsExtractor();
        }
        else if ((paramUri.endsWith(".ac3")) || (paramUri.endsWith(".ec3")))
        {
          bool = true;
          paramExtractor = new Ac3Extractor();
        }
        else
        {
          if (!paramUri.endsWith(".mp3")) {
            break;
          }
          bool = true;
          paramExtractor = new Mp3Extractor(0, 0L);
        }
      }
    } while (paramExtractor != null);
    if ((paramUri.endsWith(".mp4")) || (paramUri.startsWith(".m4", paramUri.length() - 4)) || (paramUri.startsWith(".mp4", paramUri.length() - 5)))
    {
      if (paramList != null) {}
      for (;;)
      {
        paramExtractor = new FragmentedMp4Extractor(0, paramTimestampAdjuster, null, paramDrmInitData, paramList);
        break;
        paramList = Collections.emptyList();
      }
    }
    int i = 16;
    if (paramList != null) {
      i = 0x10 | 0x20;
    }
    for (;;)
    {
      paramExtractor = paramFormat.codecs;
      int j = i;
      if (!TextUtils.isEmpty(paramExtractor))
      {
        int k = i;
        if (!"audio/mp4a-latm".equals(MimeTypes.getAudioMediaMimeType(paramExtractor))) {
          k = i | 0x2;
        }
        j = k;
        if (!"video/avc".equals(MimeTypes.getVideoMediaMimeType(paramExtractor))) {
          j = k | 0x4;
        }
      }
      paramExtractor = new TsExtractor(2, paramTimestampAdjuster, new DefaultTsPayloadReaderFactory(j, paramList));
      break;
      paramList = Collections.emptyList();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/source/hls/DefaultHlsExtractorFactory.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */