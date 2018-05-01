package org.telegram.messenger.exoplayer2.mediacodec;

import android.annotation.TargetApi;
import android.graphics.Point;
import android.media.MediaCodecInfo.AudioCapabilities;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.util.Log;
import android.util.Pair;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(16)
public final class MediaCodecInfo
{
  public static final String TAG = "MediaCodecInfo";
  public final boolean adaptive;
  private final MediaCodecInfo.CodecCapabilities capabilities;
  private final String mimeType;
  public final String name;
  public final boolean secure;
  public final boolean tunneling;
  
  private MediaCodecInfo(String paramString1, String paramString2, MediaCodecInfo.CodecCapabilities paramCodecCapabilities, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.name = ((String)Assertions.checkNotNull(paramString1));
    this.mimeType = paramString2;
    this.capabilities = paramCodecCapabilities;
    if ((!paramBoolean1) && (paramCodecCapabilities != null) && (isAdaptive(paramCodecCapabilities)))
    {
      paramBoolean1 = true;
      this.adaptive = paramBoolean1;
      if ((paramCodecCapabilities == null) || (!isTunneling(paramCodecCapabilities))) {
        break label113;
      }
    }
    label113:
    for (paramBoolean1 = true;; paramBoolean1 = false)
    {
      this.tunneling = paramBoolean1;
      if (!paramBoolean2)
      {
        paramBoolean1 = bool;
        if (paramCodecCapabilities != null)
        {
          paramBoolean1 = bool;
          if (!isSecure(paramCodecCapabilities)) {}
        }
      }
      else
      {
        paramBoolean1 = true;
      }
      this.secure = paramBoolean1;
      return;
      paramBoolean1 = false;
      break;
    }
  }
  
  private static int adjustMaxInputChannelCount(String paramString1, String paramString2, int paramInt)
  {
    int i = paramInt;
    if (paramInt <= 1)
    {
      if ((Util.SDK_INT < 26) || (paramInt <= 0)) {
        break label23;
      }
      i = paramInt;
    }
    label23:
    do
    {
      do
      {
        do
        {
          do
          {
            do
            {
              do
              {
                do
                {
                  do
                  {
                    do
                    {
                      do
                      {
                        do
                        {
                          return i;
                          i = paramInt;
                        } while ("audio/mpeg".equals(paramString2));
                        i = paramInt;
                      } while ("audio/3gpp".equals(paramString2));
                      i = paramInt;
                    } while ("audio/amr-wb".equals(paramString2));
                    i = paramInt;
                  } while ("audio/mp4a-latm".equals(paramString2));
                  i = paramInt;
                } while ("audio/vorbis".equals(paramString2));
                i = paramInt;
              } while ("audio/opus".equals(paramString2));
              i = paramInt;
            } while ("audio/raw".equals(paramString2));
            i = paramInt;
          } while ("audio/flac".equals(paramString2));
          i = paramInt;
        } while ("audio/g711-alaw".equals(paramString2));
        i = paramInt;
      } while ("audio/g711-mlaw".equals(paramString2));
      i = paramInt;
    } while ("audio/gsm".equals(paramString2));
    if ("audio/ac3".equals(paramString2)) {
      i = 6;
    }
    for (;;)
    {
      Log.w("MediaCodecInfo", "AssumedMaxChannelAdjustment: " + paramString1 + ", [" + paramInt + " to " + i + "]");
      break;
      if ("audio/eac3".equals(paramString2)) {
        i = 16;
      } else {
        i = 30;
      }
    }
  }
  
  @TargetApi(21)
  private static boolean areSizeAndRateSupportedV21(MediaCodecInfo.VideoCapabilities paramVideoCapabilities, int paramInt1, int paramInt2, double paramDouble)
  {
    if ((paramDouble == -1.0D) || (paramDouble <= 0.0D)) {}
    for (boolean bool = paramVideoCapabilities.isSizeSupported(paramInt1, paramInt2);; bool = paramVideoCapabilities.areSizeAndRateSupported(paramInt1, paramInt2, paramDouble)) {
      return bool;
    }
  }
  
  private static boolean isAdaptive(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    if ((Util.SDK_INT >= 19) && (isAdaptiveV19(paramCodecCapabilities))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @TargetApi(19)
  private static boolean isAdaptiveV19(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.isFeatureSupported("adaptive-playback");
  }
  
  private static boolean isSecure(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    if ((Util.SDK_INT >= 21) && (isSecureV21(paramCodecCapabilities))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @TargetApi(21)
  private static boolean isSecureV21(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.isFeatureSupported("secure-playback");
  }
  
  private static boolean isTunneling(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    if ((Util.SDK_INT >= 21) && (isTunnelingV21(paramCodecCapabilities))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  @TargetApi(21)
  private static boolean isTunnelingV21(MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return paramCodecCapabilities.isFeatureSupported("tunneled-playback");
  }
  
  private void logAssumedSupport(String paramString)
  {
    Log.d("MediaCodecInfo", "AssumedSupport [" + paramString + "] [" + this.name + ", " + this.mimeType + "] [" + Util.DEVICE_DEBUG_INFO + "]");
  }
  
  private void logNoSupport(String paramString)
  {
    Log.d("MediaCodecInfo", "NoSupport [" + paramString + "] [" + this.name + ", " + this.mimeType + "] [" + Util.DEVICE_DEBUG_INFO + "]");
  }
  
  public static MediaCodecInfo newInstance(String paramString1, String paramString2, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
  {
    return new MediaCodecInfo(paramString1, paramString2, paramCodecCapabilities, false, false);
  }
  
  public static MediaCodecInfo newInstance(String paramString1, String paramString2, MediaCodecInfo.CodecCapabilities paramCodecCapabilities, boolean paramBoolean1, boolean paramBoolean2)
  {
    return new MediaCodecInfo(paramString1, paramString2, paramCodecCapabilities, paramBoolean1, paramBoolean2);
  }
  
  public static MediaCodecInfo newPassthroughInstance(String paramString)
  {
    return new MediaCodecInfo(paramString, null, null, false, false);
  }
  
  @TargetApi(21)
  public Point alignVideoSizeV21(int paramInt1, int paramInt2)
  {
    Point localPoint = null;
    if (this.capabilities == null) {
      logNoSupport("align.caps");
    }
    for (;;)
    {
      return localPoint;
      MediaCodecInfo.VideoCapabilities localVideoCapabilities = this.capabilities.getVideoCapabilities();
      if (localVideoCapabilities == null)
      {
        logNoSupport("align.vCaps");
      }
      else
      {
        int i = localVideoCapabilities.getWidthAlignment();
        int j = localVideoCapabilities.getHeightAlignment();
        localPoint = new Point(Util.ceilDivide(paramInt1, i) * i, Util.ceilDivide(paramInt2, j) * j);
      }
    }
  }
  
  public MediaCodecInfo.CodecProfileLevel[] getProfileLevels()
  {
    if ((this.capabilities == null) || (this.capabilities.profileLevels == null)) {}
    for (MediaCodecInfo.CodecProfileLevel[] arrayOfCodecProfileLevel = new MediaCodecInfo.CodecProfileLevel[0];; arrayOfCodecProfileLevel = this.capabilities.profileLevels) {
      return arrayOfCodecProfileLevel;
    }
  }
  
  @TargetApi(21)
  public boolean isAudioChannelCountSupportedV21(int paramInt)
  {
    boolean bool = false;
    if (this.capabilities == null) {
      logNoSupport("channelCount.caps");
    }
    for (;;)
    {
      return bool;
      MediaCodecInfo.AudioCapabilities localAudioCapabilities = this.capabilities.getAudioCapabilities();
      if (localAudioCapabilities == null) {
        logNoSupport("channelCount.aCaps");
      } else if (adjustMaxInputChannelCount(this.name, this.mimeType, localAudioCapabilities.getMaxInputChannelCount()) < paramInt) {
        logNoSupport("channelCount.support, " + paramInt);
      } else {
        bool = true;
      }
    }
  }
  
  @TargetApi(21)
  public boolean isAudioSampleRateSupportedV21(int paramInt)
  {
    boolean bool = false;
    if (this.capabilities == null) {
      logNoSupport("sampleRate.caps");
    }
    for (;;)
    {
      return bool;
      MediaCodecInfo.AudioCapabilities localAudioCapabilities = this.capabilities.getAudioCapabilities();
      if (localAudioCapabilities == null) {
        logNoSupport("sampleRate.aCaps");
      } else if (!localAudioCapabilities.isSampleRateSupported(paramInt)) {
        logNoSupport("sampleRate.support, " + paramInt);
      } else {
        bool = true;
      }
    }
  }
  
  public boolean isCodecSupported(String paramString)
  {
    boolean bool;
    if ((paramString == null) || (this.mimeType == null)) {
      bool = true;
    }
    for (;;)
    {
      return bool;
      String str = MimeTypes.getMediaMimeType(paramString);
      if (str == null)
      {
        bool = true;
      }
      else if (!this.mimeType.equals(str))
      {
        logNoSupport("codec.mime " + paramString + ", " + str);
        bool = false;
      }
      else
      {
        Pair localPair = MediaCodecUtil.getCodecProfileAndLevel(paramString);
        if (localPair == null)
        {
          bool = true;
        }
        else
        {
          MediaCodecInfo.CodecProfileLevel[] arrayOfCodecProfileLevel = getProfileLevels();
          int i = arrayOfCodecProfileLevel.length;
          for (int j = 0;; j++)
          {
            if (j >= i) {
              break label171;
            }
            MediaCodecInfo.CodecProfileLevel localCodecProfileLevel = arrayOfCodecProfileLevel[j];
            if ((localCodecProfileLevel.profile == ((Integer)localPair.first).intValue()) && (localCodecProfileLevel.level >= ((Integer)localPair.second).intValue()))
            {
              bool = true;
              break;
            }
          }
          label171:
          logNoSupport("codec.profileLevel, " + paramString + ", " + str);
          bool = false;
        }
      }
    }
  }
  
  @TargetApi(21)
  public boolean isVideoSizeAndRateSupportedV21(int paramInt1, int paramInt2, double paramDouble)
  {
    boolean bool = false;
    if (this.capabilities == null) {
      logNoSupport("sizeAndRate.caps");
    }
    for (;;)
    {
      return bool;
      MediaCodecInfo.VideoCapabilities localVideoCapabilities = this.capabilities.getVideoCapabilities();
      if (localVideoCapabilities == null) {
        logNoSupport("sizeAndRate.vCaps");
      } else if (!areSizeAndRateSupportedV21(localVideoCapabilities, paramInt1, paramInt2, paramDouble))
      {
        if ((paramInt1 >= paramInt2) || (!areSizeAndRateSupportedV21(localVideoCapabilities, paramInt2, paramInt1, paramDouble))) {
          logNoSupport("sizeAndRate.support, " + paramInt1 + "x" + paramInt2 + "x" + paramDouble);
        } else {
          logAssumedSupport("sizeAndRate.rotated, " + paramInt1 + "x" + paramInt2 + "x" + paramDouble);
        }
      }
      else {
        bool = true;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/mediacodec/MediaCodecInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */