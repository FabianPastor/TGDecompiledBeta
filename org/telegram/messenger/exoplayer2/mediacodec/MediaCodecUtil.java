package org.telegram.messenger.exoplayer2.mediacodec;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaCodecInfo.CodecProfileLevel;
import android.media.MediaCodecList;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseIntArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.util.Util;

@SuppressLint({"InlinedApi"})
@TargetApi(16)
public final class MediaCodecUtil
{
  private static final SparseIntArray AVC_LEVEL_NUMBER_TO_CONST;
  private static final SparseIntArray AVC_PROFILE_NUMBER_TO_CONST;
  private static final String CODEC_ID_AVC1 = "avc1";
  private static final String CODEC_ID_AVC2 = "avc2";
  private static final String CODEC_ID_HEV1 = "hev1";
  private static final String CODEC_ID_HVC1 = "hvc1";
  private static final String GOOGLE_RAW_DECODER_NAME = "OMX.google.raw.decoder";
  private static final Map<String, Integer> HEVC_CODEC_STRING_TO_PROFILE_LEVEL;
  private static final String MTK_RAW_DECODER_NAME = "OMX.MTK.AUDIO.DECODER.RAW";
  private static final MediaCodecInfo PASSTHROUGH_DECODER_INFO = MediaCodecInfo.newPassthroughInstance("OMX.google.raw.decoder");
  private static final Pattern PROFILE_PATTERN = Pattern.compile("^\\D?(\\d+)$");
  private static final String TAG = "MediaCodecUtil";
  private static final HashMap<CodecKey, List<MediaCodecInfo>> decoderInfosCache = new HashMap();
  private static int maxH264DecodableFrameSize = -1;
  
  static
  {
    AVC_PROFILE_NUMBER_TO_CONST = new SparseIntArray();
    AVC_PROFILE_NUMBER_TO_CONST.put(66, 1);
    AVC_PROFILE_NUMBER_TO_CONST.put(77, 2);
    AVC_PROFILE_NUMBER_TO_CONST.put(88, 4);
    AVC_PROFILE_NUMBER_TO_CONST.put(100, 8);
    AVC_LEVEL_NUMBER_TO_CONST = new SparseIntArray();
    AVC_LEVEL_NUMBER_TO_CONST.put(10, 1);
    AVC_LEVEL_NUMBER_TO_CONST.put(11, 4);
    AVC_LEVEL_NUMBER_TO_CONST.put(12, 8);
    AVC_LEVEL_NUMBER_TO_CONST.put(13, 16);
    AVC_LEVEL_NUMBER_TO_CONST.put(20, 32);
    AVC_LEVEL_NUMBER_TO_CONST.put(21, 64);
    AVC_LEVEL_NUMBER_TO_CONST.put(22, 128);
    AVC_LEVEL_NUMBER_TO_CONST.put(30, 256);
    AVC_LEVEL_NUMBER_TO_CONST.put(31, 512);
    AVC_LEVEL_NUMBER_TO_CONST.put(32, 1024);
    AVC_LEVEL_NUMBER_TO_CONST.put(40, 2048);
    AVC_LEVEL_NUMBER_TO_CONST.put(41, 4096);
    AVC_LEVEL_NUMBER_TO_CONST.put(42, 8192);
    AVC_LEVEL_NUMBER_TO_CONST.put(50, 16384);
    AVC_LEVEL_NUMBER_TO_CONST.put(51, 32768);
    AVC_LEVEL_NUMBER_TO_CONST.put(52, 65536);
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL = new HashMap();
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L30", Integer.valueOf(1));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L60", Integer.valueOf(4));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L63", Integer.valueOf(16));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L90", Integer.valueOf(64));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L93", Integer.valueOf(256));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L120", Integer.valueOf(1024));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L123", Integer.valueOf(4096));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L150", Integer.valueOf(16384));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L153", Integer.valueOf(65536));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L156", Integer.valueOf(262144));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L180", Integer.valueOf(1048576));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L183", Integer.valueOf(4194304));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("L186", Integer.valueOf(16777216));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H30", Integer.valueOf(2));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H60", Integer.valueOf(8));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H63", Integer.valueOf(32));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H90", Integer.valueOf(128));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H93", Integer.valueOf(512));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H120", Integer.valueOf(2048));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H123", Integer.valueOf(8192));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H150", Integer.valueOf(32768));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H153", Integer.valueOf(131072));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H156", Integer.valueOf(524288));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H180", Integer.valueOf(2097152));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H183", Integer.valueOf(8388608));
    HEVC_CODEC_STRING_TO_PROFILE_LEVEL.put("H186", Integer.valueOf(33554432));
  }
  
  private static void applyWorkarounds(List<MediaCodecInfo> paramList)
  {
    if ((Util.SDK_INT < 26) && (paramList.size() > 1) && ("OMX.MTK.AUDIO.DECODER.RAW".equals(((MediaCodecInfo)paramList.get(0)).name))) {}
    for (int i = 1;; i++) {
      if (i < paramList.size())
      {
        MediaCodecInfo localMediaCodecInfo = (MediaCodecInfo)paramList.get(i);
        if ("OMX.google.raw.decoder".equals(localMediaCodecInfo.name))
        {
          paramList.remove(i);
          paramList.add(0, localMediaCodecInfo);
        }
      }
      else
      {
        return;
      }
    }
  }
  
  private static int avcLevelToMaxFrameSize(int paramInt)
  {
    int i = 25344;
    switch (paramInt)
    {
    default: 
      i = -1;
    }
    for (;;)
    {
      return i;
      i = 101376;
      continue;
      i = 101376;
      continue;
      i = 101376;
      continue;
      i = 202752;
      continue;
      i = 414720;
      continue;
      i = 414720;
      continue;
      i = 921600;
      continue;
      i = 1310720;
      continue;
      i = 2097152;
      continue;
      i = 2097152;
      continue;
      i = 2228224;
      continue;
      i = 5652480;
      continue;
      i = 9437184;
      continue;
      i = 9437184;
    }
  }
  
  private static boolean codecNeedsDisableAdaptationWorkaround(String paramString)
  {
    if ((Util.SDK_INT <= 22) && ((Util.MODEL.equals("ODROID-XU3")) || (Util.MODEL.equals("Nexus 10"))) && (("OMX.Exynos.AVC.Decoder".equals(paramString)) || ("OMX.Exynos.AVC.Decoder.secure".equals(paramString)))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  private static Pair<Integer, Integer> getAvcProfileAndLevel(String paramString, String[] paramArrayOfString)
  {
    Object localObject = null;
    if (paramArrayOfString.length < 2)
    {
      Log.w("MediaCodecUtil", "Ignoring malformed AVC codec string: " + paramString);
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      Integer localInteger;
      try
      {
        if (paramArrayOfString[1].length() == 6)
        {
          localInteger = Integer.valueOf(Integer.parseInt(paramArrayOfString[1].substring(0, 2), 16));
          int i = Integer.parseInt(paramArrayOfString[1].substring(4), 16);
          paramString = Integer.valueOf(i);
        }
        for (paramArrayOfString = localInteger;; paramArrayOfString = localInteger)
        {
          localInteger = Integer.valueOf(AVC_PROFILE_NUMBER_TO_CONST.get(paramArrayOfString.intValue()));
          if (localInteger != null) {
            break label232;
          }
          Log.w("MediaCodecUtil", "Unknown AVC profile: " + paramArrayOfString);
          paramString = (String)localObject;
          break;
          if (paramArrayOfString.length < 3) {
            break label169;
          }
          localInteger = Integer.valueOf(Integer.parseInt(paramArrayOfString[1]));
          paramArrayOfString = Integer.valueOf(Integer.parseInt(paramArrayOfString[2]));
          paramString = paramArrayOfString;
        }
        label169:
        paramArrayOfString = new java/lang/StringBuilder;
        paramArrayOfString.<init>();
        Log.w("MediaCodecUtil", "Ignoring malformed AVC codec string: " + paramString);
        paramString = (String)localObject;
      }
      catch (NumberFormatException paramArrayOfString)
      {
        Log.w("MediaCodecUtil", "Ignoring malformed AVC codec string: " + paramString);
        paramString = (String)localObject;
      }
      continue;
      label232:
      paramArrayOfString = Integer.valueOf(AVC_LEVEL_NUMBER_TO_CONST.get(paramString.intValue()));
      if (paramArrayOfString == null)
      {
        Log.w("MediaCodecUtil", "Unknown AVC level: " + paramString);
        paramString = (String)localObject;
      }
      else
      {
        paramString = new Pair(localInteger, paramArrayOfString);
      }
    }
  }
  
  public static Pair<Integer, Integer> getCodecProfileAndLevel(String paramString)
  {
    Object localObject = null;
    int i = 0;
    if (paramString == null) {
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      String[] arrayOfString = paramString.split("\\.");
      String str = arrayOfString[0];
      switch (str.hashCode())
      {
      default: 
        label72:
        i = -1;
      }
      for (;;)
      {
        switch (i)
        {
        default: 
          paramString = (String)localObject;
          break;
        case 0: 
        case 1: 
          paramString = getHevcProfileAndLevel(paramString, arrayOfString);
          break;
          if (!str.equals("hev1")) {
            break label72;
          }
          continue;
          if (!str.equals("hvc1")) {
            break label72;
          }
          i = 1;
          continue;
          if (!str.equals("avc1")) {
            break label72;
          }
          i = 2;
          continue;
          if (!str.equals("avc2")) {
            break label72;
          }
          i = 3;
        }
      }
      paramString = getAvcProfileAndLevel(paramString, arrayOfString);
    }
  }
  
  public static MediaCodecInfo getDecoderInfo(String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    paramString = getDecoderInfos(paramString, paramBoolean);
    if (paramString.isEmpty()) {}
    for (paramString = null;; paramString = (MediaCodecInfo)paramString.get(0)) {
      return paramString;
    }
  }
  
  /* Error */
  public static List<MediaCodecInfo> getDecoderInfos(String paramString, boolean paramBoolean)
    throws MediaCodecUtil.DecoderQueryException
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: new 8	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$CodecKey
    //   6: astore_2
    //   7: aload_2
    //   8: aload_0
    //   9: iload_1
    //   10: invokespecial 328	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$CodecKey:<init>	(Ljava/lang/String;Z)V
    //   13: getstatic 90	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil:decoderInfosCache	Ljava/util/HashMap;
    //   16: aload_2
    //   17: invokevirtual 331	java/util/HashMap:get	(Ljava/lang/Object;)Ljava/lang/Object;
    //   20: checkcast 190	java/util/List
    //   23: astore_3
    //   24: aload_3
    //   25: ifnull +10 -> 35
    //   28: aload_3
    //   29: astore_0
    //   30: ldc 2
    //   32: monitorexit
    //   33: aload_0
    //   34: areturn
    //   35: getstatic 188	org/telegram/messenger/exoplayer2/util/Util:SDK_INT	I
    //   38: bipush 21
    //   40: if_icmplt +228 -> 268
    //   43: new 20	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$MediaCodecListCompatV21
    //   46: astore_3
    //   47: aload_3
    //   48: iload_1
    //   49: invokespecial 334	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$MediaCodecListCompatV21:<init>	(Z)V
    //   52: aload_2
    //   53: aload_3
    //   54: aload_0
    //   55: invokestatic 338	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil:getDecoderInfosInternal	(Lorg/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$CodecKey;Lorg/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$MediaCodecListCompat;Ljava/lang/String;)Ljava/util/ArrayList;
    //   58: astore 4
    //   60: aload 4
    //   62: astore 5
    //   64: aload_3
    //   65: astore 6
    //   67: iload_1
    //   68: ifeq +139 -> 207
    //   71: aload 4
    //   73: astore 5
    //   75: aload_3
    //   76: astore 6
    //   78: aload 4
    //   80: invokevirtual 341	java/util/ArrayList:isEmpty	()Z
    //   83: ifeq +124 -> 207
    //   86: aload 4
    //   88: astore 5
    //   90: aload_3
    //   91: astore 6
    //   93: bipush 21
    //   95: getstatic 188	org/telegram/messenger/exoplayer2/util/Util:SDK_INT	I
    //   98: if_icmpgt +109 -> 207
    //   101: aload 4
    //   103: astore 5
    //   105: aload_3
    //   106: astore 6
    //   108: getstatic 188	org/telegram/messenger/exoplayer2/util/Util:SDK_INT	I
    //   111: bipush 23
    //   113: if_icmpgt +94 -> 207
    //   116: new 17	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$MediaCodecListCompatV16
    //   119: astore_3
    //   120: aload_3
    //   121: aconst_null
    //   122: invokespecial 344	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$MediaCodecListCompatV16:<init>	(Lorg/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$1;)V
    //   125: aload_2
    //   126: aload_3
    //   127: aload_0
    //   128: invokestatic 338	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil:getDecoderInfosInternal	(Lorg/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$CodecKey;Lorg/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$MediaCodecListCompat;Ljava/lang/String;)Ljava/util/ArrayList;
    //   131: astore 4
    //   133: aload 4
    //   135: astore 5
    //   137: aload_3
    //   138: astore 6
    //   140: aload 4
    //   142: invokevirtual 341	java/util/ArrayList:isEmpty	()Z
    //   145: ifne +62 -> 207
    //   148: new 245	java/lang/StringBuilder
    //   151: astore 6
    //   153: aload 6
    //   155: invokespecial 246	java/lang/StringBuilder:<init>	()V
    //   158: ldc 58
    //   160: aload 6
    //   162: ldc_w 346
    //   165: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   168: aload_0
    //   169: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   172: ldc_w 348
    //   175: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   178: aload 4
    //   180: iconst_0
    //   181: invokevirtual 349	java/util/ArrayList:get	(I)Ljava/lang/Object;
    //   184: checkcast 67	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecInfo
    //   187: getfield 201	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecInfo:name	Ljava/lang/String;
    //   190: invokevirtual 252	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   193: invokevirtual 256	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   196: invokestatic 262	android/util/Log:w	(Ljava/lang/String;Ljava/lang/String;)I
    //   199: pop
    //   200: aload_3
    //   201: astore 6
    //   203: aload 4
    //   205: astore 5
    //   207: ldc_w 351
    //   210: aload_0
    //   211: invokevirtual 207	java/lang/String:equals	(Ljava/lang/Object;)Z
    //   214: ifeq +31 -> 245
    //   217: new 8	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$CodecKey
    //   220: astore_3
    //   221: aload_3
    //   222: ldc_w 353
    //   225: aload_2
    //   226: getfield 357	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$CodecKey:secure	Z
    //   229: invokespecial 328	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$CodecKey:<init>	(Ljava/lang/String;Z)V
    //   232: aload 5
    //   234: aload_3
    //   235: aload 6
    //   237: aload_0
    //   238: invokestatic 338	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil:getDecoderInfosInternal	(Lorg/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$CodecKey;Lorg/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$MediaCodecListCompat;Ljava/lang/String;)Ljava/util/ArrayList;
    //   241: invokevirtual 361	java/util/ArrayList:addAll	(Ljava/util/Collection;)Z
    //   244: pop
    //   245: aload 5
    //   247: invokestatic 363	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil:applyWorkarounds	(Ljava/util/List;)V
    //   250: aload 5
    //   252: invokestatic 369	java/util/Collections:unmodifiableList	(Ljava/util/List;)Ljava/util/List;
    //   255: astore_0
    //   256: getstatic 90	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil:decoderInfosCache	Ljava/util/HashMap;
    //   259: aload_2
    //   260: aload_0
    //   261: invokevirtual 370	java/util/HashMap:put	(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;
    //   264: pop
    //   265: goto -235 -> 30
    //   268: new 17	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$MediaCodecListCompatV16
    //   271: dup
    //   272: aconst_null
    //   273: invokespecial 344	org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$MediaCodecListCompatV16:<init>	(Lorg/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil$1;)V
    //   276: astore_3
    //   277: goto -225 -> 52
    //   280: astore_0
    //   281: ldc 2
    //   283: monitorexit
    //   284: aload_0
    //   285: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	286	0	paramString	String
    //   0	286	1	paramBoolean	boolean
    //   6	254	2	localCodecKey	CodecKey
    //   23	254	3	localObject1	Object
    //   58	146	4	localArrayList1	ArrayList
    //   62	189	5	localArrayList2	ArrayList
    //   65	171	6	localObject2	Object
    // Exception table:
    //   from	to	target	type
    //   3	24	280	finally
    //   35	52	280	finally
    //   52	60	280	finally
    //   78	86	280	finally
    //   93	101	280	finally
    //   108	133	280	finally
    //   140	200	280	finally
    //   207	245	280	finally
    //   245	265	280	finally
    //   268	277	280	finally
  }
  
  private static ArrayList<MediaCodecInfo> getDecoderInfosInternal(CodecKey paramCodecKey, MediaCodecListCompat paramMediaCodecListCompat, String paramString)
    throws MediaCodecUtil.DecoderQueryException
  {
    for (;;)
    {
      int j;
      String str2;
      Object localObject;
      try
      {
        ArrayList localArrayList = new java/util/ArrayList;
        localArrayList.<init>();
        String str1 = paramCodecKey.mimeType;
        int i = paramMediaCodecListCompat.getCodecCount();
        boolean bool1 = paramMediaCodecListCompat.secureDecodersExplicit();
        j = 0;
        android.media.MediaCodecInfo localMediaCodecInfo;
        int m;
        boolean bool2;
        if (j < i)
        {
          localMediaCodecInfo = paramMediaCodecListCompat.getCodecInfoAt(j);
          str2 = localMediaCodecInfo.getName();
          if (!isCodecUsableDecoder(localMediaCodecInfo, str2, bool1, paramString)) {
            break label361;
          }
          String[] arrayOfString = localMediaCodecInfo.getSupportedTypes();
          int k = arrayOfString.length;
          m = 0;
          if (m >= k) {
            break label361;
          }
          localObject = arrayOfString[m];
          bool2 = ((String)localObject).equalsIgnoreCase(str1);
          if (!bool2) {}
        }
        try
        {
          MediaCodecInfo.CodecCapabilities localCodecCapabilities = localMediaCodecInfo.getCapabilitiesForType((String)localObject);
          bool2 = paramMediaCodecListCompat.isSecurePlaybackSupported(str1, localCodecCapabilities);
          boolean bool3 = codecNeedsDisableAdaptationWorkaround(str2);
          if (((bool1) && (paramCodecKey.secure == bool2)) || ((!bool1) && (!paramCodecKey.secure)))
          {
            localArrayList.add(MediaCodecInfo.newInstance(str2, str1, localCodecCapabilities, bool3, false));
            m++;
            continue;
          }
          if ((bool1) || (!bool2)) {
            continue;
          }
          StringBuilder localStringBuilder = new java/lang/StringBuilder;
          localStringBuilder.<init>();
          localArrayList.add(MediaCodecInfo.newInstance(str2 + ".secure", str1, localCodecCapabilities, bool3, true));
          return localArrayList;
        }
        catch (Exception localException)
        {
          if (Util.SDK_INT > 23) {
            break label312;
          }
        }
        if (!localArrayList.isEmpty())
        {
          localObject = new java/lang/StringBuilder;
          ((StringBuilder)localObject).<init>();
          Log.e("MediaCodecUtil", "Skipping codec " + str2 + " (failed to query capabilities)");
          continue;
        }
        paramCodecKey = new java/lang/StringBuilder;
      }
      catch (Exception paramCodecKey)
      {
        throw new DecoderQueryException(paramCodecKey, null);
      }
      label312:
      paramCodecKey.<init>();
      Log.e("MediaCodecUtil", "Failed to query codec " + str2 + " (" + (String)localObject + ")");
      throw localException;
      label361:
      j++;
    }
  }
  
  private static Pair<Integer, Integer> getHevcProfileAndLevel(String paramString, String[] paramArrayOfString)
  {
    Object localObject = null;
    if (paramArrayOfString.length < 4)
    {
      Log.w("MediaCodecUtil", "Ignoring malformed HEVC codec string: " + paramString);
      paramString = (String)localObject;
    }
    for (;;)
    {
      return paramString;
      Matcher localMatcher = PROFILE_PATTERN.matcher(paramArrayOfString[1]);
      if (!localMatcher.matches())
      {
        Log.w("MediaCodecUtil", "Ignoring malformed HEVC codec string: " + paramString);
        paramString = (String)localObject;
      }
      else
      {
        paramString = localMatcher.group(1);
        if ("1".equals(paramString)) {}
        for (int i = 1;; i = 2)
        {
          paramString = (Integer)HEVC_CODEC_STRING_TO_PROFILE_LEVEL.get(paramArrayOfString[3]);
          if (paramString != null) {
            break label206;
          }
          Log.w("MediaCodecUtil", "Unknown HEVC level string: " + localMatcher.group(1));
          paramString = (String)localObject;
          break;
          if (!"2".equals(paramString)) {
            break label175;
          }
        }
        label175:
        Log.w("MediaCodecUtil", "Unknown HEVC profile string: " + paramString);
        paramString = (String)localObject;
        continue;
        label206:
        paramString = new Pair(Integer.valueOf(i), paramString);
      }
    }
  }
  
  public static MediaCodecInfo getPassthroughDecoderInfo()
  {
    return PASSTHROUGH_DECODER_INFO;
  }
  
  private static boolean isCodecUsableDecoder(android.media.MediaCodecInfo paramMediaCodecInfo, String paramString1, boolean paramBoolean, String paramString2)
  {
    boolean bool1 = false;
    boolean bool2 = bool1;
    if (!paramMediaCodecInfo.isEncoder())
    {
      if ((paramBoolean) || (!paramString1.endsWith(".secure"))) {
        break label35;
      }
      bool2 = bool1;
    }
    for (;;)
    {
      return bool2;
      label35:
      if (Util.SDK_INT < 21)
      {
        bool2 = bool1;
        if (!"CIPAACDecoder".equals(paramString1))
        {
          bool2 = bool1;
          if (!"CIPMP3Decoder".equals(paramString1))
          {
            bool2 = bool1;
            if (!"CIPVorbisDecoder".equals(paramString1))
            {
              bool2 = bool1;
              if (!"CIPAMRNBDecoder".equals(paramString1))
              {
                bool2 = bool1;
                if (!"AACDecoder".equals(paramString1))
                {
                  bool2 = bool1;
                  if ("MP3Decoder".equals(paramString1)) {}
                }
              }
            }
          }
        }
      }
      else if (Util.SDK_INT < 18)
      {
        bool2 = bool1;
        if ("OMX.SEC.MP3.Decoder".equals(paramString1)) {}
      }
      else
      {
        if ((Util.SDK_INT < 18) && ("OMX.MTK.AUDIO.DECODER.AAC".equals(paramString1)))
        {
          bool2 = bool1;
          if ("a70".equals(Util.DEVICE)) {
            continue;
          }
          if ("Xiaomi".equals(Util.MANUFACTURER))
          {
            bool2 = bool1;
            if (Util.DEVICE.startsWith("HM")) {
              continue;
            }
          }
        }
        if ((Util.SDK_INT == 16) && ("OMX.qcom.audio.decoder.mp3".equals(paramString1)))
        {
          bool2 = bool1;
          if (!"dlxu".equals(Util.DEVICE))
          {
            bool2 = bool1;
            if (!"protou".equals(Util.DEVICE))
            {
              bool2 = bool1;
              if (!"ville".equals(Util.DEVICE))
              {
                bool2 = bool1;
                if (!"villeplus".equals(Util.DEVICE))
                {
                  bool2 = bool1;
                  if (!"villec2".equals(Util.DEVICE))
                  {
                    bool2 = bool1;
                    if (!Util.DEVICE.startsWith("gee"))
                    {
                      bool2 = bool1;
                      if (!"C6602".equals(Util.DEVICE))
                      {
                        bool2 = bool1;
                        if (!"C6603".equals(Util.DEVICE))
                        {
                          bool2 = bool1;
                          if (!"C6606".equals(Util.DEVICE))
                          {
                            bool2 = bool1;
                            if (!"C6616".equals(Util.DEVICE))
                            {
                              bool2 = bool1;
                              if (!"L36h".equals(Util.DEVICE))
                              {
                                bool2 = bool1;
                                if ("SO-02E".equals(Util.DEVICE)) {}
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
        else if ((Util.SDK_INT == 16) && ("OMX.qcom.audio.decoder.aac".equals(paramString1)))
        {
          bool2 = bool1;
          if (!"C1504".equals(Util.DEVICE))
          {
            bool2 = bool1;
            if (!"C1505".equals(Util.DEVICE))
            {
              bool2 = bool1;
              if (!"C1604".equals(Util.DEVICE))
              {
                bool2 = bool1;
                if ("C1605".equals(Util.DEVICE)) {}
              }
            }
          }
        }
        else if ((Util.SDK_INT < 24) && (("OMX.SEC.aac.dec".equals(paramString1)) || ("OMX.Exynos.AAC.Decoder".equals(paramString1))) && (Util.MANUFACTURER.equals("samsung")))
        {
          bool2 = bool1;
          if (!Util.DEVICE.startsWith("zeroflte"))
          {
            bool2 = bool1;
            if (!Util.DEVICE.startsWith("zerolte"))
            {
              bool2 = bool1;
              if (!Util.DEVICE.startsWith("zenlte"))
              {
                bool2 = bool1;
                if (!Util.DEVICE.equals("SC-05G"))
                {
                  bool2 = bool1;
                  if (!Util.DEVICE.equals("marinelteatt"))
                  {
                    bool2 = bool1;
                    if (!Util.DEVICE.equals("404SC"))
                    {
                      bool2 = bool1;
                      if (!Util.DEVICE.equals("SC-04G"))
                      {
                        bool2 = bool1;
                        if (Util.DEVICE.equals("SCV31")) {}
                      }
                    }
                  }
                }
              }
            }
          }
        }
        else if ((Util.SDK_INT <= 19) && ("OMX.SEC.vp8.dec".equals(paramString1)) && ("samsung".equals(Util.MANUFACTURER)))
        {
          bool2 = bool1;
          if (!Util.DEVICE.startsWith("d2"))
          {
            bool2 = bool1;
            if (!Util.DEVICE.startsWith("serrano"))
            {
              bool2 = bool1;
              if (!Util.DEVICE.startsWith("jflte"))
              {
                bool2 = bool1;
                if (!Util.DEVICE.startsWith("santos"))
                {
                  bool2 = bool1;
                  if (Util.DEVICE.startsWith("t0")) {}
                }
              }
            }
          }
        }
        else if ((Util.SDK_INT <= 19) && (Util.DEVICE.startsWith("jflte")))
        {
          bool2 = bool1;
          if ("OMX.qcom.video.decoder.vp8".equals(paramString1)) {}
        }
        else if ("audio/eac3-joc".equals(paramString2))
        {
          bool2 = bool1;
          if ("OMX.MTK.AUDIO.DECODER.DSPAC3".equals(paramString1)) {}
        }
        else
        {
          bool2 = true;
        }
      }
    }
  }
  
  public static int maxH264DecodableFrameSize()
    throws MediaCodecUtil.DecoderQueryException
  {
    int i = 0;
    int j;
    if (maxH264DecodableFrameSize == -1)
    {
      j = 0;
      int k = 0;
      Object localObject = getDecoderInfo("video/avc", false);
      if (localObject != null)
      {
        localObject = ((MediaCodecInfo)localObject).getProfileLevels();
        int m = localObject.length;
        j = k;
        while (i < m)
        {
          j = Math.max(avcLevelToMaxFrameSize(localObject[i].level), j);
          i++;
        }
        if (Util.SDK_INT < 21) {
          break label88;
        }
      }
    }
    label88:
    for (i = 345600;; i = 172800)
    {
      j = Math.max(j, i);
      maxH264DecodableFrameSize = j;
      return maxH264DecodableFrameSize;
    }
  }
  
  public static void warmDecoderInfoCache(String paramString, boolean paramBoolean)
  {
    try
    {
      getDecoderInfos(paramString, paramBoolean);
      return;
    }
    catch (DecoderQueryException paramString)
    {
      for (;;)
      {
        Log.e("MediaCodecUtil", "Codec warming failed", paramString);
      }
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
      boolean bool = true;
      if (this == paramObject) {}
      for (;;)
      {
        return bool;
        if ((paramObject == null) || (paramObject.getClass() != CodecKey.class))
        {
          bool = false;
        }
        else
        {
          paramObject = (CodecKey)paramObject;
          if ((!TextUtils.equals(this.mimeType, ((CodecKey)paramObject).mimeType)) || (this.secure != ((CodecKey)paramObject).secure)) {
            bool = false;
          }
        }
      }
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
    extends Exception
  {
    private DecoderQueryException(Throwable paramThrowable)
    {
      super(paramThrowable);
    }
  }
  
  private static abstract interface MediaCodecListCompat
  {
    public abstract int getCodecCount();
    
    public abstract android.media.MediaCodecInfo getCodecInfoAt(int paramInt);
    
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
    
    public android.media.MediaCodecInfo getCodecInfoAt(int paramInt)
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
    private android.media.MediaCodecInfo[] mediaCodecInfos;
    
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
    
    public android.media.MediaCodecInfo getCodecInfoAt(int paramInt)
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


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/mediacodec/MediaCodecUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */