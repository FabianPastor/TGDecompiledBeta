package org.telegram.messenger.exoplayer;

import android.annotation.TargetApi;
import android.media.MediaCodecInfo;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecInfo.VideoCapabilities;
import android.media.MediaCodecList;
import android.text.TextUtils;
import android.util.Log;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

@TargetApi(16)
public final class MediaCodecUtil
{
  private static final DecoderInfo PASSTHROUGH_DECODER_INFO = new DecoderInfo("OMX.google.raw.decoder", null);
  private static final String TAG = "MediaCodecUtil";
  private static final Map<CodecKey, List<DecoderInfo>> decoderInfosCache = new HashMap();
  private static int maxH264DecodableFrameSize = -1;
  
  private static int avcLevelToMaxFrameSize(int paramInt)
  {
    int i = 25344;
    switch (paramInt)
    {
    default: 
      i = -1;
    case 1: 
    case 2: 
      return i;
    case 8: 
      return 101376;
    case 16: 
      return 101376;
    case 32: 
      return 101376;
    case 64: 
      return 202752;
    case 128: 
      return 414720;
    case 256: 
      return 414720;
    case 512: 
      return 921600;
    case 1024: 
      return 1310720;
    case 2048: 
      return 2097152;
    case 4096: 
      return 2097152;
    case 8192: 
      return 2228224;
    case 16384: 
      return 5652480;
    }
    return 9437184;
  }
  
  public static DecoderInfo getDecoderInfo(String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    paramString = getDecoderInfos(paramString, paramBoolean);
    if (paramString.isEmpty()) {
      return null;
    }
    return (DecoderInfo)paramString.get(0);
  }
  
  /* Error */
  public static List<DecoderInfo> getDecoderInfos(String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: new 8	org/telegram/messenger/exoplayer/MediaCodecUtil$CodecKey
    //   6: dup
    //   7: aload_0
    //   8: iload_1
    //   9: invokespecial 88	org/telegram/messenger/exoplayer/MediaCodecUtil$CodecKey:<init>	(Ljava/lang/String;Z)V
    //   12: astore 4
    //   14: getstatic 53	org/telegram/messenger/exoplayer/MediaCodecUtil:decoderInfosCache	Ljava/util/Map;
    //   17: aload 4
    //   19: invokeinterface 93 2 0
    //   24: checkcast 76	java/util/List
    //   27: astore_2
    //   28: aload_2
    //   29: ifnull +10 -> 39
    //   32: aload_2
    //   33: astore_0
    //   34: ldc 2
    //   36: monitorexit
    //   37: aload_0
    //   38: areturn
    //   39: getstatic 98	org/telegram/messenger/exoplayer/util/Util:SDK_INT	I
    //   42: bipush 21
    //   44: if_icmplt +149 -> 193
    //   47: new 20	org/telegram/messenger/exoplayer/MediaCodecUtil$MediaCodecListCompatV21
    //   50: dup
    //   51: iload_1
    //   52: invokespecial 101	org/telegram/messenger/exoplayer/MediaCodecUtil$MediaCodecListCompatV21:<init>	(Z)V
    //   55: astore_2
    //   56: aload 4
    //   58: aload_2
    //   59: invokestatic 105	org/telegram/messenger/exoplayer/MediaCodecUtil:getDecoderInfosInternal	(Lorg/telegram/messenger/exoplayer/MediaCodecUtil$CodecKey;Lorg/telegram/messenger/exoplayer/MediaCodecUtil$MediaCodecListCompat;)Ljava/util/List;
    //   62: astore_3
    //   63: aload_3
    //   64: astore_2
    //   65: iload_1
    //   66: ifeq +107 -> 173
    //   69: aload_3
    //   70: astore_2
    //   71: aload_3
    //   72: invokeinterface 80 1 0
    //   77: ifeq +96 -> 173
    //   80: aload_3
    //   81: astore_2
    //   82: bipush 21
    //   84: getstatic 98	org/telegram/messenger/exoplayer/util/Util:SDK_INT	I
    //   87: if_icmpgt +86 -> 173
    //   90: aload_3
    //   91: astore_2
    //   92: getstatic 98	org/telegram/messenger/exoplayer/util/Util:SDK_INT	I
    //   95: bipush 23
    //   97: if_icmpgt +76 -> 173
    //   100: aload 4
    //   102: new 17	org/telegram/messenger/exoplayer/MediaCodecUtil$MediaCodecListCompatV16
    //   105: dup
    //   106: aconst_null
    //   107: invokespecial 108	org/telegram/messenger/exoplayer/MediaCodecUtil$MediaCodecListCompatV16:<init>	(Lorg/telegram/messenger/exoplayer/MediaCodecUtil$1;)V
    //   110: invokestatic 105	org/telegram/messenger/exoplayer/MediaCodecUtil:getDecoderInfosInternal	(Lorg/telegram/messenger/exoplayer/MediaCodecUtil$CodecKey;Lorg/telegram/messenger/exoplayer/MediaCodecUtil$MediaCodecListCompat;)Ljava/util/List;
    //   113: astore_3
    //   114: aload_3
    //   115: astore_2
    //   116: aload_3
    //   117: invokeinterface 80 1 0
    //   122: ifne +51 -> 173
    //   125: ldc 30
    //   127: new 110	java/lang/StringBuilder
    //   130: dup
    //   131: invokespecial 111	java/lang/StringBuilder:<init>	()V
    //   134: ldc 113
    //   136: invokevirtual 117	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   139: aload_0
    //   140: invokevirtual 117	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   143: ldc 119
    //   145: invokevirtual 117	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   148: aload_3
    //   149: iconst_0
    //   150: invokeinterface 84 2 0
    //   155: checkcast 39	org/telegram/messenger/exoplayer/DecoderInfo
    //   158: getfield 122	org/telegram/messenger/exoplayer/DecoderInfo:name	Ljava/lang/String;
    //   161: invokevirtual 117	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   164: invokevirtual 126	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   167: invokestatic 132	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   170: pop
    //   171: aload_3
    //   172: astore_2
    //   173: aload_2
    //   174: invokestatic 138	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   177: astore_0
    //   178: getstatic 53	org/telegram/messenger/exoplayer/MediaCodecUtil:decoderInfosCache	Ljava/util/Map;
    //   181: aload 4
    //   183: aload_0
    //   184: invokeinterface 142 3 0
    //   189: pop
    //   190: goto -156 -> 34
    //   193: new 17	org/telegram/messenger/exoplayer/MediaCodecUtil$MediaCodecListCompatV16
    //   196: dup
    //   197: aconst_null
    //   198: invokespecial 108	org/telegram/messenger/exoplayer/MediaCodecUtil$MediaCodecListCompatV16:<init>	(Lorg/telegram/messenger/exoplayer/MediaCodecUtil$1;)V
    //   201: astore_2
    //   202: goto -146 -> 56
    //   205: astore_0
    //   206: ldc 2
    //   208: monitorexit
    //   209: aload_0
    //   210: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	211	0	paramString	String
    //   0	211	1	paramBoolean	boolean
    //   27	175	2	localObject	Object
    //   62	110	3	localList	List
    //   12	170	4	localCodecKey	CodecKey
    // Exception table:
    //   from	to	target	type
    //   3	28	205	finally
    //   39	56	205	finally
    //   56	63	205	finally
    //   71	80	205	finally
    //   82	90	205	finally
    //   92	114	205	finally
    //   116	171	205	finally
    //   173	190	205	finally
    //   193	202	205	finally
  }
  
  private static List<DecoderInfo> getDecoderInfosInternal(CodecKey paramCodecKey, MediaCodecListCompat paramMediaCodecListCompat)
    throws MediaCodecUtil.DecoderQueryException
  {
    for (;;)
    {
      int i;
      String str2;
      int j;
      String str3;
      try
      {
        ArrayList localArrayList = new ArrayList();
        String str1 = paramCodecKey.mimeType;
        int k = paramMediaCodecListCompat.getCodecCount();
        boolean bool1 = paramMediaCodecListCompat.secureDecodersExplicit();
        i = 0;
        MediaCodecInfo localMediaCodecInfo;
        boolean bool2;
        if (i < k)
        {
          localMediaCodecInfo = paramMediaCodecListCompat.getCodecInfoAt(i);
          str2 = localMediaCodecInfo.getName();
          if (!isCodecUsableDecoder(localMediaCodecInfo, str2, bool1)) {
            break label335;
          }
          String[] arrayOfString = localMediaCodecInfo.getSupportedTypes();
          int m = arrayOfString.length;
          j = 0;
          if (j >= m) {
            break label335;
          }
          str3 = arrayOfString[j];
          bool2 = str3.equalsIgnoreCase(str1);
          if (!bool2) {
            break label342;
          }
        }
        try
        {
          MediaCodecInfo.CodecCapabilities localCodecCapabilities = localMediaCodecInfo.getCapabilitiesForType(str3);
          bool2 = paramMediaCodecListCompat.isSecurePlaybackSupported(str1, localCodecCapabilities);
          if (((bool1) && (paramCodecKey.secure == bool2)) || ((!bool1) && (!paramCodecKey.secure)))
          {
            localArrayList.add(new DecoderInfo(str2, localCodecCapabilities));
            break label342;
          }
          if ((bool1) || (!bool2)) {
            break label342;
          }
          localArrayList.add(new DecoderInfo(str2 + ".secure", localCodecCapabilities));
          return localArrayList;
        }
        catch (Exception localException)
        {
          if (Util.SDK_INT > 23) {
            break label291;
          }
        }
        if (!localArrayList.isEmpty()) {
          Log.e("MediaCodecUtil", "Skipping codec " + str2 + " (failed to query capabilities)");
        }
      }
      catch (Exception paramCodecKey)
      {
        throw new DecoderQueryException(paramCodecKey, null);
      }
      label291:
      Log.e("MediaCodecUtil", "Failed to query codec " + str2 + " (" + str3 + ")");
      throw localException;
      label335:
      i += 1;
      continue;
      label342:
      j += 1;
    }
  }
  
  public static DecoderInfo getPassthroughDecoderInfo()
  {
    return PASSTHROUGH_DECODER_INFO;
  }
  
  @TargetApi(21)
  private static MediaCodecInfo.VideoCapabilities getVideoCapabilitiesV21(String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    paramString = getDecoderInfo(paramString, paramBoolean);
    if (paramString == null) {
      return null;
    }
    return paramString.capabilities.getVideoCapabilities();
  }
  
  private static boolean isCodecUsableDecoder(MediaCodecInfo paramMediaCodecInfo, String paramString, boolean paramBoolean)
  {
    if ((paramMediaCodecInfo.isEncoder()) || ((!paramBoolean) && (paramString.endsWith(".secure")))) {}
    while (((Util.SDK_INT < 21) && (("CIPAACDecoder".equals(paramString)) || ("CIPMP3Decoder".equals(paramString)) || ("CIPVorbisDecoder".equals(paramString)) || ("AACDecoder".equals(paramString)) || ("MP3Decoder".equals(paramString)))) || ((Util.SDK_INT < 18) && ("OMX.SEC.MP3.Decoder".equals(paramString))) || ((Util.SDK_INT < 18) && ("OMX.MTK.AUDIO.DECODER.AAC".equals(paramString)) && ("a70".equals(Util.DEVICE))) || ((Util.SDK_INT == 16) && (Util.DEVICE != null) && ("OMX.qcom.audio.decoder.mp3".equals(paramString)) && (("dlxu".equals(Util.DEVICE)) || ("protou".equals(Util.DEVICE)) || ("ville".equals(Util.DEVICE)) || ("villeplus".equals(Util.DEVICE)) || ("villec2".equals(Util.DEVICE)) || (Util.DEVICE.startsWith("gee")) || ("C6602".equals(Util.DEVICE)) || ("C6603".equals(Util.DEVICE)) || ("C6606".equals(Util.DEVICE)) || ("C6616".equals(Util.DEVICE)) || ("L36h".equals(Util.DEVICE)) || ("SO-02E".equals(Util.DEVICE)))) || ((Util.SDK_INT == 16) && ("OMX.qcom.audio.decoder.aac".equals(paramString)) && (("C1504".equals(Util.DEVICE)) || ("C1505".equals(Util.DEVICE)) || ("C1604".equals(Util.DEVICE)) || ("C1605".equals(Util.DEVICE)))) || ((Util.SDK_INT <= 19) && (Util.DEVICE != null) && ((Util.DEVICE.startsWith("d2")) || (Util.DEVICE.startsWith("serrano")) || (Util.DEVICE.startsWith("jflte")) || (Util.DEVICE.startsWith("santos"))) && ("samsung".equals(Util.MANUFACTURER)) && (paramString.equals("OMX.SEC.vp8.dec"))) || ((Util.SDK_INT <= 19) && (Util.DEVICE != null) && (Util.DEVICE.startsWith("jflte")) && ("OMX.qcom.video.decoder.vp8".equals(paramString)))) {
      return false;
    }
    return true;
  }
  
  @Deprecated
  public static boolean isH264ProfileSupported(int paramInt1, int paramInt2)
    throws MediaCodecUtil.DecoderQueryException
  {
    Object localObject1 = getDecoderInfo("video/avc", false);
    if (localObject1 == null) {}
    for (;;)
    {
      return false;
      localObject1 = ((DecoderInfo)localObject1).capabilities.profileLevels;
      int j = localObject1.length;
      int i = 0;
      while (i < j)
      {
        Object localObject2 = localObject1[i];
        if ((((MediaCodecInfo.CodecProfileLevel)localObject2).profile == paramInt1) && (((MediaCodecInfo.CodecProfileLevel)localObject2).level >= paramInt2)) {
          return true;
        }
        i += 1;
      }
    }
  }
  
  @TargetApi(21)
  public static boolean isSizeAndRateSupportedV21(String paramString, boolean paramBoolean, int paramInt1, int paramInt2, double paramDouble)
    throws MediaCodecUtil.DecoderQueryException
  {
    if (Util.SDK_INT >= 21) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      paramString = getVideoCapabilitiesV21(paramString, paramBoolean);
      if ((paramString == null) || (!paramString.areSizeAndRateSupported(paramInt1, paramInt2, paramDouble))) {
        break;
      }
      return true;
    }
    return false;
  }
  
  @TargetApi(21)
  public static boolean isSizeSupportedV21(String paramString, boolean paramBoolean, int paramInt1, int paramInt2)
    throws MediaCodecUtil.DecoderQueryException
  {
    if (Util.SDK_INT >= 21) {}
    for (boolean bool = true;; bool = false)
    {
      Assertions.checkState(bool);
      paramString = getVideoCapabilitiesV21(paramString, paramBoolean);
      if ((paramString == null) || (!paramString.isSizeSupported(paramInt1, paramInt2))) {
        break;
      }
      return true;
    }
    return false;
  }
  
  public static int maxH264DecodableFrameSize()
    throws MediaCodecUtil.DecoderQueryException
  {
    int k = 0;
    if (maxH264DecodableFrameSize == -1)
    {
      int i = 0;
      int j = 0;
      Object localObject = getDecoderInfo("video/avc", false);
      if (localObject != null)
      {
        localObject = ((DecoderInfo)localObject).capabilities.profileLevels;
        int m = localObject.length;
        i = k;
        while (i < m)
        {
          j = Math.max(avcLevelToMaxFrameSize(localObject[i].level), j);
          i += 1;
        }
        i = Math.max(j, 172800);
      }
      maxH264DecodableFrameSize = i;
    }
    return maxH264DecodableFrameSize;
  }
  
  public static void warmCodec(String paramString, boolean paramBoolean)
  {
    try
    {
      getDecoderInfos(paramString, paramBoolean);
      return;
    }
    catch (DecoderQueryException paramString)
    {
      Log.e("MediaCodecUtil", "Codec warming failed", paramString);
    }
  }
  
  private static final class CodecKey
  {
    public final String mimeType;
    public final boolean secure;
    
    public CodecKey(String paramString, boolean paramBoolean)
    {
      this.mimeType = paramString;
      this.secure = paramBoolean;
    }
    
    public boolean equals(Object paramObject)
    {
      if (this == paramObject) {}
      do
      {
        return true;
        if ((paramObject == null) || (paramObject.getClass() != CodecKey.class)) {
          return false;
        }
        paramObject = (CodecKey)paramObject;
      } while ((TextUtils.equals(this.mimeType, ((CodecKey)paramObject).mimeType)) && (this.secure == ((CodecKey)paramObject).secure));
      return false;
    }
    
    public int hashCode()
    {
      int i;
      if (this.mimeType == null)
      {
        i = 0;
        if (!this.secure) {
          break label41;
        }
      }
      label41:
      for (int j = 1231;; j = 1237)
      {
        return (i + 31) * 31 + j;
        i = this.mimeType.hashCode();
        break;
      }
    }
  }
  
  public static class DecoderQueryException
    extends IOException
  {
    private DecoderQueryException(Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
  
  private static abstract interface MediaCodecListCompat
  {
    public abstract int getCodecCount();
    
    public abstract MediaCodecInfo getCodecInfoAt(int paramInt);
    
    public abstract boolean isSecurePlaybackSupported(String paramString, MediaCodecInfo.CodecCapabilities paramCodecCapabilities);
    
    public abstract boolean secureDecodersExplicit();
  }
  
  private static final class MediaCodecListCompatV16
    implements MediaCodecUtil.MediaCodecListCompat
  {
    public int getCodecCount()
    {
      return MediaCodecList.getCodecCount();
    }
    
    public MediaCodecInfo getCodecInfoAt(int paramInt)
    {
      return MediaCodecList.getCodecInfoAt(paramInt);
    }
    
    public boolean isSecurePlaybackSupported(String paramString, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
    {
      return "video/avc".equals(paramString);
    }
    
    public boolean secureDecodersExplicit()
    {
      return false;
    }
  }
  
  @TargetApi(21)
  private static final class MediaCodecListCompatV21
    implements MediaCodecUtil.MediaCodecListCompat
  {
    private final int codecKind;
    private MediaCodecInfo[] mediaCodecInfos;
    
    public MediaCodecListCompatV21(boolean paramBoolean)
    {
      if (paramBoolean) {}
      for (int i = 1;; i = 0)
      {
        this.codecKind = i;
        return;
      }
    }
    
    private void ensureMediaCodecInfosInitialized()
    {
      if (this.mediaCodecInfos == null) {
        this.mediaCodecInfos = new MediaCodecList(this.codecKind).getCodecInfos();
      }
    }
    
    public int getCodecCount()
    {
      ensureMediaCodecInfosInitialized();
      return this.mediaCodecInfos.length;
    }
    
    public MediaCodecInfo getCodecInfoAt(int paramInt)
    {
      ensureMediaCodecInfosInitialized();
      return this.mediaCodecInfos[paramInt];
    }
    
    public boolean isSecurePlaybackSupported(String paramString, MediaCodecInfo.CodecCapabilities paramCodecCapabilities)
    {
      return paramCodecCapabilities.isFeatureSupported("secure-playback");
    }
    
    public boolean secureDecodersExplicit()
    {
      return true;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/MediaCodecUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */