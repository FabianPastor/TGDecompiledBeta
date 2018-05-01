package com.google.android.gms.wearable.internal;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.util.Log;
import com.google.android.gms.common.GooglePlayServicesUtilLight;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.wrappers.PackageManagerWrapper;
import com.google.android.gms.common.wrappers.Wrappers;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

public class zzhp
{
  private static zzhp zzfs;
  private static final byte[] zzfu = zzd("0\005a0\003K\002\006\001DÓ0\013\006\t*H÷\r\001\001\0050v1\0130\t\006\003U\004\006\023\002US1\0230\021\006\003U\004\b\023\nCalifornia1\0260\024\006\003U\004\007\023\rMountain View1\0240\022\006\003U\004\n\023\013Google Inc.1\0200\016\006\003U\004\013\023\007Android1\0220\020\006\003U\004\003\023\tClockWork0\036\027\r140307220225Z\027\r380119031407Z0v1\0130\t\006\003U\004\006\023\002US1\0230\021\006\003U\004\b\023\nCalifornia1\0260\024\006\003U\004\007\023\rMountain View1\0240\022\006\003U\004\n\023\013Google Inc.1\0200\016\006\003U\004\013\023\007Android1\0220\020\006\003U\004\003\023\tClockWork0\002\"0\r\006\t*H÷\r\001\001\001\005\000\003\002\017\0000\002\n\002\002\001\000º<9\013þYb¼ü<Æ'Z\025íÜÝ7:Uj\013âýC÷\030³\001Ò@'ãr\tÎýâ|&° Þ6}\032ßãN§®7óõà&rzN\b(;ïvøöC¼\025'6 H?É·«R<ó½{f-*'L\000Øç\021è°&_í©uÜÈåB\023jbq.\013/9yQÛ$±W¡¿çÅkÎJ8\013%ú¹&c>¨\0048à¶\013¹~.ú\005<2)Ùao¤½!{7C\037ÍØí!§òðFà\034¼ZbãJ\025káZ\027ÿ\002\027dDÖ\023±\036×_\030î´ýäZã\034ä¯¤68¶,\\ÒÛ\n\001Ä2a(äÅ\031z¾¬ÌmÂè­¤B_\017Õ¥¥X$a¿x\021á.Î\016ê\006\003?T9íàqÿÄl òß¾##:dÁÎ\t­¡ËÎkö¼¢.JÀÉjluOì\030qØ{\020Á Þ`¼}wÞ0ÕN¸GÎk\022|\031\036§o\nFÁFó6¹4êºZ_\034\003d·RUD2Pýcªå{ë«à&?\t\bM\031D\006\f:Ù»ºyôÞ<+-7º³\rK¹\021ÜQià¯RôÓ=³òË\034R\002Rpa¿\001°BÐ~ä\021©ª 'ðDÚ(ÅÝØSW§\0369»Q³Wëor\030üÌ\027\030¦0gF1àU9\032zgòZ b\001Ö\"¸Ð\tÝ\021Õ\006¢\003\017$'®gØ\03347yy\002\003\001\000\0010\013\006\t*H÷\r\001\001\005\003\002\001\000¤Ä4aÈ5¥±\nÍ\001$7jÚ'C¬0\003Hg\013 +­ã?/º*\007d\003µ\013èqÊ*²¾½»Ä\006Û\t9AÉ\027j\016Fÿÿ\000\026\026\004DnÜá0þ\020\036ã\005·~=©¢­4©Ò´Ú\033&ýZ[p\034Õlþédzä\024;¦|\002e±\024ò2¥ï\027ád¡I\027\0340½Z6«øóBÈã¯¼oICs\007}j\021×9\"\rZ×µ\031/\034þJr±¸Tuàé¾hrfe±+ôîÃ\"VTõáò+ëU¾fwÖ_\t-ù^þï\017ÇêÊ]\016¾\035A\004\037ç Ë20~9.\023ñ 9Ti0\002\027@-öÇ rçß8ºÃ×\"5oæTj|WßgÉ=+5T5ðù¡\023Î-ìÍm¡ÃKAì®Ö ëR0%Åà\004ì´Q¼EáHZÌ6¶I¯YLU\033\013É8ËÖ\032ÕgY ÷:eá©È¤Û¬\036ë\f)\t^ÞA\005{<®êN\026Å¹EKâY\021´¢\037?z¿Àgô\030.A¤ä4ð/í¯WrJU3WÚ_³ÍüùTÿØÉQwçu\004¦B¾\\Û á\000eü|h\022í'³¨\004×¤ÍÙ\fÓìË\005¨È`ÐV N´\036\005ý9\\\037§{³\035¥$4^\n½N\001µ\006OêºBÓ-Ôg>ÏÀ\027\035&éÍ\\FïÐ");
  private static final byte[] zzfv = zzd("0\003¿0\002§ \003\002\001\002\002\t\000ÚÃÙ\025sÓï0\r\006\t*H÷\r\001\001\005\005\0000v1\0130\t\006\003U\004\006\023\002US1\0230\021\006\003U\004\b\f\nCalifornia1\0260\024\006\003U\004\007\f\rMountain View1\0240\022\006\003U\004\n\f\013Google Inc.1\0200\016\006\003U\004\013\f\007Android1\0220\020\006\003U\004\003\f\tClockWork0\036\027\r140307220118Z\027\r410723220118Z0v1\0130\t\006\003U\004\006\023\002US1\0230\021\006\003U\004\b\f\nCalifornia1\0260\024\006\003U\004\007\f\rMountain View1\0240\022\006\003U\004\n\f\013Google Inc.1\0200\016\006\003U\004\013\f\007Android1\0220\020\006\003U\004\003\f\tClockWork0\001\"0\r\006\t*H÷\r\001\001\001\005\000\003\001\017\0000\001\n\002\001\001\000Ü\035oK(í80\024²öÚÿÓ\035Þ{\036c\b@e\013X±e£j®¶,qS.\004E\t¯\037ºO\030dÃ§µÖSÌ\000\025\000\020áåfú7ªÿ\0306]®{JÝ±óÌGp¢>bþµrÁi1Z¯Nôê¥®\037ÍÖçåêÔ1\023tFF\f|(û2,\\\\z¨wÃp?à·~¶ n¬krê ­!\n°*\037ÜüvbttA©?<ê\026ô\"Áã2A2~ÂÉ÷01.\033ïî)\013E\0324,¬ï[\024rÖÙ~ùT(ÌÕï\004¸Äñõ\rÒBÕ]rXfP[^K\033\036Y­\035/ H\025g;ÆæC)ìÄêÔÛd©k1ÛÉ\007\002\003\001\000\001£P0N0\035\006\003U\035\016\004\026\004\024G\020¤<³êø?«!b \000Î,z0\037\006\003U\035#\004\0300\026\024G\020¤<³êø?«!b \000Î,z0\f\006\003U\035\023\004\0050\003\001\001ÿ0\r\006\t*H÷\r\001\001\005\005\000\003\001\001\000\0079b\013¢}*\017TC­\033`\034)Ù\001(êü?Ö(__bj>ðWæî²¬\\¢æ\005Ê=3õk\0002ÄGæP\017%½\027Êù\0039@ÈîlÜµ;íä±òHçÐ çÊê¥2ÏÚþJ¥í@@ND÷[ïÒÊÛ5¸²\033xF^\027\"òzû+\013n\025DÄ«\fOe{\031×}SÉÏ¹î-OE¶Tà\022¼éäÂâÃÓQ\003Ø®M,ÁÈbxW®u?\035{\002£§\005xÆ\005ã\005\034l\035©I\032Î\023»Ð}}Ô&Q®¤G5\rë@^ò«óf®/ÊXÒö¿\035¿K\034Hà \001TßÏ\002%\022õ¡Ç\"s\035ãðGÖø");
  private final Context zzft;
  
  private zzhp(Context paramContext)
  {
    this.zzft = paramContext.getApplicationContext();
  }
  
  public static zzhp zza(Context paramContext)
  {
    Preconditions.checkNotNull(paramContext);
    try
    {
      if (zzfs == null)
      {
        zzhp localzzhp = new com/google/android/gms/wearable/internal/zzhp;
        localzzhp.<init>(paramContext);
        zzfs = localzzhp;
      }
      return zzfs;
    }
    finally {}
  }
  
  private static boolean zza(PackageInfo paramPackageInfo, boolean paramBoolean)
  {
    boolean bool = false;
    if (paramPackageInfo.signatures.length != 1)
    {
      Log.w("WearSignatureVerifier", "Package has more than one signature.");
      paramBoolean = bool;
    }
    for (;;)
    {
      return paramBoolean;
      paramPackageInfo.signatures[0].toByteArray();
      if (paramBoolean) {
        paramBoolean = zza(paramPackageInfo, new byte[][] { zzfu, zzfv });
      } else {
        paramBoolean = zza(paramPackageInfo, new byte[][] { zzfu });
      }
    }
  }
  
  private static boolean zza(PackageInfo paramPackageInfo, byte[]... paramVarArgs)
  {
    boolean bool1 = false;
    if (paramPackageInfo.signatures == null) {}
    for (boolean bool2 = bool1;; bool2 = bool1)
    {
      return bool2;
      if (paramPackageInfo.signatures.length == 1) {
        break;
      }
      Log.w("WearSignatureVerifier", "Package has more than one signature.");
    }
    paramPackageInfo = paramPackageInfo.signatures[0].toByteArray();
    for (int i = 0;; i++)
    {
      bool2 = bool1;
      if (i >= paramVarArgs.length) {
        break;
      }
      if (Arrays.equals(paramVarArgs[i], paramPackageInfo))
      {
        bool2 = true;
        break;
      }
    }
  }
  
  private static byte[] zzd(String paramString)
  {
    try
    {
      paramString = paramString.getBytes("ISO-8859-1");
      return paramString;
    }
    catch (UnsupportedEncodingException paramString)
    {
      throw new AssertionError(paramString);
    }
  }
  
  private final PackageInfo zzf(String paramString)
  {
    try
    {
      paramString = Wrappers.packageManager(this.zzft).getPackageInfo(paramString, 64);
      return paramString;
    }
    catch (PackageManager.NameNotFoundException paramString)
    {
      for (;;)
      {
        paramString = null;
      }
    }
  }
  
  public final boolean zze(String paramString)
  {
    boolean bool1 = false;
    paramString = zzf(paramString);
    if (paramString == null) {}
    for (;;)
    {
      return bool1;
      if (GooglePlayServicesUtilLight.honorsDebugCertificates(this.zzft))
      {
        bool1 = zza(paramString, true);
      }
      else
      {
        boolean bool2 = zza(paramString, false);
        bool1 = bool2;
        if (!bool2)
        {
          bool1 = bool2;
          if (zza(paramString, true))
          {
            Log.w("WearSignatureVerifier", "Test-keys aren't accepted on this build.");
            bool1 = bool2;
          }
        }
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/wearable/internal/zzhp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */