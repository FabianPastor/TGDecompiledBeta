package org.telegram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo.CodecCapabilities;
import org.telegram.messenger.exoplayer.util.Util;

@TargetApi(16)
public final class DecoderInfo
{
  public final boolean adaptive;
  public final MediaCodecInfo.CodecCapabilities capabilities;
  public final String name;
  
  DecoderInfo(String paramString, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    this.name = paramString;
    this.capabilities = paramCodecCapabilities;
    this.adaptive = isAdaptive(paramCodecCapabilities);
  }
  
  private static boolean isAdaptive(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return (paramCodecCapabilities != null) && (Util.SDK_INT >= 19) && (isAdaptiveV19(paramCodecCapabilities));
  }
  
  @TargetApi(19)
  private static boolean isAdaptiveV19(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.isFeatureSupported("adaptive-playback");
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/DecoderInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */