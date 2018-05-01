package org.telegram.messenger.exoplayer2.util;

import android.text.TextUtils;

public final class MimeTypes
{
  public static final String APPLICATION_CAMERA_MOTION = "application/x-camera-motion";
  public static final String APPLICATION_CEA608 = "application/cea-608";
  public static final String APPLICATION_CEA708 = "application/cea-708";
  public static final String APPLICATION_DVBSUBS = "application/dvbsubs";
  public static final String APPLICATION_EMSG = "application/x-emsg";
  public static final String APPLICATION_EXIF = "application/x-exif";
  public static final String APPLICATION_ID3 = "application/id3";
  public static final String APPLICATION_M3U8 = "application/x-mpegURL";
  public static final String APPLICATION_MP4 = "application/mp4";
  public static final String APPLICATION_MP4CEA608 = "application/x-mp4-cea-608";
  public static final String APPLICATION_MP4VTT = "application/x-mp4-vtt";
  public static final String APPLICATION_MPD = "application/dash+xml";
  public static final String APPLICATION_PGS = "application/pgs";
  public static final String APPLICATION_RAWCC = "application/x-rawcc";
  public static final String APPLICATION_SCTE35 = "application/x-scte35";
  public static final String APPLICATION_SS = "application/vnd.ms-sstr+xml";
  public static final String APPLICATION_SUBRIP = "application/x-subrip";
  public static final String APPLICATION_TTML = "application/ttml+xml";
  public static final String APPLICATION_TX3G = "application/x-quicktime-tx3g";
  public static final String APPLICATION_VOBSUB = "application/vobsub";
  public static final String APPLICATION_WEBM = "application/webm";
  public static final String AUDIO_AAC = "audio/mp4a-latm";
  public static final String AUDIO_AC3 = "audio/ac3";
  public static final String AUDIO_ALAC = "audio/alac";
  public static final String AUDIO_ALAW = "audio/g711-alaw";
  public static final String AUDIO_AMR_NB = "audio/3gpp";
  public static final String AUDIO_AMR_WB = "audio/amr-wb";
  public static final String AUDIO_DTS = "audio/vnd.dts";
  public static final String AUDIO_DTS_EXPRESS = "audio/vnd.dts.hd;profile=lbr";
  public static final String AUDIO_DTS_HD = "audio/vnd.dts.hd";
  public static final String AUDIO_E_AC3 = "audio/eac3";
  public static final String AUDIO_E_AC3_JOC = "audio/eac3-joc";
  public static final String AUDIO_FLAC = "audio/flac";
  public static final String AUDIO_MLAW = "audio/g711-mlaw";
  public static final String AUDIO_MP4 = "audio/mp4";
  public static final String AUDIO_MPEG = "audio/mpeg";
  public static final String AUDIO_MPEG_L1 = "audio/mpeg-L1";
  public static final String AUDIO_MPEG_L2 = "audio/mpeg-L2";
  public static final String AUDIO_MSGSM = "audio/gsm";
  public static final String AUDIO_OPUS = "audio/opus";
  public static final String AUDIO_RAW = "audio/raw";
  public static final String AUDIO_TRUEHD = "audio/true-hd";
  public static final String AUDIO_UNKNOWN = "audio/x-unknown";
  public static final String AUDIO_VORBIS = "audio/vorbis";
  public static final String AUDIO_WEBM = "audio/webm";
  public static final String BASE_TYPE_APPLICATION = "application";
  public static final String BASE_TYPE_AUDIO = "audio";
  public static final String BASE_TYPE_TEXT = "text";
  public static final String BASE_TYPE_VIDEO = "video";
  public static final String TEXT_SSA = "text/x-ssa";
  public static final String TEXT_VTT = "text/vtt";
  public static final String VIDEO_H263 = "video/3gpp";
  public static final String VIDEO_H264 = "video/avc";
  public static final String VIDEO_H265 = "video/hevc";
  public static final String VIDEO_MP4 = "video/mp4";
  public static final String VIDEO_MP4V = "video/mp4v-es";
  public static final String VIDEO_MPEG = "video/mpeg";
  public static final String VIDEO_MPEG2 = "video/mpeg2";
  public static final String VIDEO_UNKNOWN = "video/x-unknown";
  public static final String VIDEO_VC1 = "video/wvc1";
  public static final String VIDEO_VP8 = "video/x-vnd.on2.vp8";
  public static final String VIDEO_VP9 = "video/x-vnd.on2.vp9";
  public static final String VIDEO_WEBM = "video/webm";
  
  public static String getAudioMediaMimeType(String paramString)
  {
    if (paramString == null) {
      paramString = null;
    }
    for (;;)
    {
      return paramString;
      String[] arrayOfString = paramString.split(",");
      int i = arrayOfString.length;
      for (int j = 0;; j++)
      {
        if (j >= i) {
          break label55;
        }
        String str = getMediaMimeType(arrayOfString[j]);
        if (str != null)
        {
          paramString = str;
          if (isAudio(str)) {
            break;
          }
        }
      }
      label55:
      paramString = null;
    }
  }
  
  public static int getEncoding(String paramString)
  {
    int i = 5;
    int j = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (j)
      {
      default: 
        i = 0;
      }
      break;
    }
    for (;;)
    {
      return i;
      if (!paramString.equals("audio/ac3")) {
        break;
      }
      j = 0;
      break;
      if (!paramString.equals("audio/eac3")) {
        break;
      }
      j = 1;
      break;
      if (!paramString.equals("audio/eac3-joc")) {
        break;
      }
      j = 2;
      break;
      if (!paramString.equals("audio/vnd.dts")) {
        break;
      }
      j = 3;
      break;
      if (!paramString.equals("audio/vnd.dts.hd")) {
        break;
      }
      j = 4;
      break;
      if (!paramString.equals("audio/true-hd")) {
        break;
      }
      j = 5;
      break;
      i = 6;
      continue;
      i = 7;
      continue;
      i = 8;
      continue;
      i = 14;
    }
  }
  
  public static String getMediaMimeType(String paramString)
  {
    Object localObject = null;
    if (paramString == null) {
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      String str = paramString.trim();
      if ((str.startsWith("avc1")) || (str.startsWith("avc3")))
      {
        paramString = "video/avc";
      }
      else if ((str.startsWith("hev1")) || (str.startsWith("hvc1")))
      {
        paramString = "video/hevc";
      }
      else if ((str.startsWith("vp9")) || (str.startsWith("vp09")))
      {
        paramString = "video/x-vnd.on2.vp9";
      }
      else if ((str.startsWith("vp8")) || (str.startsWith("vp08")))
      {
        paramString = "video/x-vnd.on2.vp8";
      }
      else if (str.startsWith("mp4a"))
      {
        paramString = "audio/mp4a-latm";
      }
      else if ((str.startsWith("ac-3")) || (str.startsWith("dac3")))
      {
        paramString = "audio/ac3";
      }
      else if ((str.startsWith("ec-3")) || (str.startsWith("dec3")))
      {
        paramString = "audio/eac3";
      }
      else if (str.startsWith("ec+3"))
      {
        paramString = "audio/eac3-joc";
      }
      else if ((str.startsWith("dtsc")) || (str.startsWith("dtse")))
      {
        paramString = "audio/vnd.dts";
      }
      else if ((str.startsWith("dtsh")) || (str.startsWith("dtsl")))
      {
        paramString = "audio/vnd.dts.hd";
      }
      else if (str.startsWith("opus"))
      {
        paramString = "audio/opus";
      }
      else
      {
        paramString = (String)localObject;
        if (str.startsWith("vorbis")) {
          paramString = "audio/vorbis";
        }
      }
    }
  }
  
  private static String getTopLevelType(String paramString)
  {
    if (paramString == null) {}
    int i;
    for (paramString = null;; paramString = paramString.substring(0, i))
    {
      return paramString;
      i = paramString.indexOf('/');
      if (i == -1) {
        throw new IllegalArgumentException("Invalid mime type: " + paramString);
      }
    }
  }
  
  public static int getTrackType(String paramString)
  {
    int i = -1;
    if (TextUtils.isEmpty(paramString)) {}
    for (;;)
    {
      return i;
      if (isAudio(paramString)) {
        i = 1;
      } else if (isVideo(paramString)) {
        i = 2;
      } else if ((isText(paramString)) || ("application/cea-608".equals(paramString)) || ("application/cea-708".equals(paramString)) || ("application/x-mp4-cea-608".equals(paramString)) || ("application/x-subrip".equals(paramString)) || ("application/ttml+xml".equals(paramString)) || ("application/x-quicktime-tx3g".equals(paramString)) || ("application/x-mp4-vtt".equals(paramString)) || ("application/x-rawcc".equals(paramString)) || ("application/vobsub".equals(paramString)) || ("application/pgs".equals(paramString)) || ("application/dvbsubs".equals(paramString))) {
        i = 3;
      } else if (("application/id3".equals(paramString)) || ("application/x-emsg".equals(paramString)) || ("application/x-scte35".equals(paramString)) || ("application/x-camera-motion".equals(paramString))) {
        i = 4;
      }
    }
  }
  
  public static int getTrackTypeOfCodec(String paramString)
  {
    return getTrackType(getMediaMimeType(paramString));
  }
  
  public static String getVideoMediaMimeType(String paramString)
  {
    if (paramString == null) {
      paramString = null;
    }
    for (;;)
    {
      return paramString;
      String[] arrayOfString = paramString.split(",");
      int i = arrayOfString.length;
      for (int j = 0;; j++)
      {
        if (j >= i) {
          break label55;
        }
        String str = getMediaMimeType(arrayOfString[j]);
        if (str != null)
        {
          paramString = str;
          if (isVideo(str)) {
            break;
          }
        }
      }
      label55:
      paramString = null;
    }
  }
  
  public static boolean isApplication(String paramString)
  {
    return "application".equals(getTopLevelType(paramString));
  }
  
  public static boolean isAudio(String paramString)
  {
    return "audio".equals(getTopLevelType(paramString));
  }
  
  public static boolean isText(String paramString)
  {
    return "text".equals(getTopLevelType(paramString));
  }
  
  public static boolean isVideo(String paramString)
  {
    return "video".equals(getTopLevelType(paramString));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/util/MimeTypes.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */