package org.telegram.messenger.exoplayer2.ext.ffmpeg;

import org.telegram.messenger.exoplayer2.ExoPlayerLibraryInfo;

public final class FfmpegLibrary
{
  static
  {
    ExoPlayerLibraryInfo.registerModule("goog.exo.ffmpeg");
  }
  
  private static native String ffmpegGetVersion();
  
  private static native boolean ffmpegHasDecoder(String paramString);
  
  static String getCodecName(String paramString)
  {
    int i = -1;
    switch (paramString.hashCode())
    {
    default: 
      switch (i)
      {
      default: 
        paramString = null;
      }
      break;
    }
    for (;;)
    {
      return paramString;
      if (!paramString.equals("audio/mp4a-latm")) {
        break;
      }
      i = 0;
      break;
      if (!paramString.equals("audio/mpeg")) {
        break;
      }
      i = 1;
      break;
      if (!paramString.equals("audio/mpeg-L1")) {
        break;
      }
      i = 2;
      break;
      if (!paramString.equals("audio/mpeg-L2")) {
        break;
      }
      i = 3;
      break;
      if (!paramString.equals("audio/ac3")) {
        break;
      }
      i = 4;
      break;
      if (!paramString.equals("audio/eac3")) {
        break;
      }
      i = 5;
      break;
      if (!paramString.equals("audio/true-hd")) {
        break;
      }
      i = 6;
      break;
      if (!paramString.equals("audio/vnd.dts")) {
        break;
      }
      i = 7;
      break;
      if (!paramString.equals("audio/vnd.dts.hd")) {
        break;
      }
      i = 8;
      break;
      if (!paramString.equals("audio/vorbis")) {
        break;
      }
      i = 9;
      break;
      if (!paramString.equals("audio/opus")) {
        break;
      }
      i = 10;
      break;
      if (!paramString.equals("audio/3gpp")) {
        break;
      }
      i = 11;
      break;
      if (!paramString.equals("audio/amr-wb")) {
        break;
      }
      i = 12;
      break;
      if (!paramString.equals("audio/flac")) {
        break;
      }
      i = 13;
      break;
      if (!paramString.equals("audio/alac")) {
        break;
      }
      i = 14;
      break;
      paramString = "aac";
      continue;
      paramString = "mp3";
      continue;
      paramString = "ac3";
      continue;
      paramString = "eac3";
      continue;
      paramString = "truehd";
      continue;
      paramString = "dca";
      continue;
      paramString = "vorbis";
      continue;
      paramString = "opus";
      continue;
      paramString = "amrnb";
      continue;
      paramString = "amrwb";
      continue;
      paramString = "flac";
      continue;
      paramString = "alac";
    }
  }
  
  public static String getVersion()
  {
    return ffmpegGetVersion();
  }
  
  public static boolean supportsFormat(String paramString)
  {
    paramString = getCodecName(paramString);
    if ((paramString != null) && (ffmpegHasDecoder(paramString))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ext/ffmpeg/FfmpegLibrary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */