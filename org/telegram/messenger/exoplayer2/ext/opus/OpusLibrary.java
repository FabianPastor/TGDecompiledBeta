package org.telegram.messenger.exoplayer2.ext.opus;

import org.telegram.messenger.exoplayer2.ExoPlayerLibraryInfo;

public final class OpusLibrary
{
  static
  {
    ExoPlayerLibraryInfo.registerModule("goog.exo.opus");
  }
  
  public static String getVersion()
  {
    return opusGetVersion();
  }
  
  public static native String opusGetVersion();
  
  public static native boolean opusIsSecureDecodeSupported();
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/ext/opus/OpusLibrary.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */