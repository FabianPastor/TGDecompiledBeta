package com.google.android.gms.measurement.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzard;
import com.google.android.gms.internal.zzvk.zzb;
import com.google.android.gms.internal.zzvk.zzc;
import com.google.android.gms.internal.zzvk.zzd;
import com.google.android.gms.internal.zzvk.zze;
import com.google.android.gms.internal.zzvk.zzf;
import com.google.android.gms.internal.zzvm.zza;
import com.google.android.gms.internal.zzvm.zzb;
import com.google.android.gms.internal.zzvm.zzc;
import com.google.android.gms.internal.zzvm.zzd;
import com.google.android.gms.internal.zzvm.zze;
import com.google.android.gms.internal.zzvm.zzf;
import com.google.android.gms.internal.zzvm.zzg;
import com.google.android.gms.measurement.AppMeasurement.zza;
import com.google.android.gms.measurement.AppMeasurement.zze;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

public class zzal
  extends zzz
{
  zzal(zzx paramzzx)
  {
    super(paramzzx);
  }
  
  private Object zza(int paramInt, Object paramObject, boolean paramBoolean)
  {
    Object localObject;
    if (paramObject == null) {
      localObject = null;
    }
    do
    {
      do
      {
        return localObject;
        localObject = paramObject;
      } while ((paramObject instanceof Long));
      localObject = paramObject;
    } while ((paramObject instanceof Double));
    if ((paramObject instanceof Integer)) {
      return Long.valueOf(((Integer)paramObject).intValue());
    }
    if ((paramObject instanceof Byte)) {
      return Long.valueOf(((Byte)paramObject).byteValue());
    }
    if ((paramObject instanceof Short)) {
      return Long.valueOf(((Short)paramObject).shortValue());
    }
    if ((paramObject instanceof Boolean))
    {
      if (((Boolean)paramObject).booleanValue()) {}
      for (long l = 1L;; l = 0L) {
        return Long.valueOf(l);
      }
    }
    if ((paramObject instanceof Float)) {
      return Double.valueOf(((Float)paramObject).doubleValue());
    }
    if (((paramObject instanceof String)) || ((paramObject instanceof Character)) || ((paramObject instanceof CharSequence))) {
      return zza(String.valueOf(paramObject), paramInt, paramBoolean);
    }
    return null;
  }
  
  public static String zza(zzvk.zzb paramzzb)
  {
    int i = 0;
    if (paramzzb == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nevent_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzzb.asE);
    zza(localStringBuilder, 0, "event_name", paramzzb.asF);
    zza(localStringBuilder, 1, "event_count_filter", paramzzb.asI);
    localStringBuilder.append("  filters {\n");
    paramzzb = paramzzb.asG;
    int j = paramzzb.length;
    while (i < j)
    {
      zza(localStringBuilder, 2, paramzzb[i]);
      i += 1;
    }
    zza(localStringBuilder, 1);
    localStringBuilder.append("}\n}\n");
    return localStringBuilder.toString();
  }
  
  public static String zza(zzvk.zze paramzze)
  {
    if (paramzze == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nproperty_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzze.asE);
    zza(localStringBuilder, 0, "property_name", paramzze.asU);
    zza(localStringBuilder, 1, paramzze.asV);
    localStringBuilder.append("}\n");
    return localStringBuilder.toString();
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt)
  {
    int i = 0;
    while (i < paramInt)
    {
      paramStringBuilder.append("  ");
      i += 1;
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzvk.zzc paramzzc)
  {
    if (paramzzc == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("filter {\n");
    zza(paramStringBuilder, paramInt, "complement", paramzzc.asM);
    zza(paramStringBuilder, paramInt, "param_name", paramzzc.asN);
    zza(paramStringBuilder, paramInt + 1, "string_filter", paramzzc.asK);
    zza(paramStringBuilder, paramInt + 1, "number_filter", paramzzc.asL);
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzvm.zze paramzze)
  {
    if (paramzze == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("bundle {\n");
    zza(paramStringBuilder, paramInt, "protocol_version", paramzze.atv);
    zza(paramStringBuilder, paramInt, "platform", paramzze.atD);
    zza(paramStringBuilder, paramInt, "gmp_version", paramzze.atH);
    zza(paramStringBuilder, paramInt, "uploading_gmp_version", paramzze.atI);
    zza(paramStringBuilder, paramInt, "gmp_app_id", paramzze.anQ);
    zza(paramStringBuilder, paramInt, "app_id", paramzze.zzck);
    zza(paramStringBuilder, paramInt, "app_version", paramzze.afY);
    zza(paramStringBuilder, paramInt, "app_version_major", paramzze.atQ);
    zza(paramStringBuilder, paramInt, "firebase_instance_id", paramzze.anY);
    zza(paramStringBuilder, paramInt, "dev_cert_hash", paramzze.atM);
    zza(paramStringBuilder, paramInt, "app_store", paramzze.anR);
    zza(paramStringBuilder, paramInt, "upload_timestamp_millis", paramzze.aty);
    zza(paramStringBuilder, paramInt, "start_timestamp_millis", paramzze.atz);
    zza(paramStringBuilder, paramInt, "end_timestamp_millis", paramzze.atA);
    zza(paramStringBuilder, paramInt, "previous_bundle_start_timestamp_millis", paramzze.atB);
    zza(paramStringBuilder, paramInt, "previous_bundle_end_timestamp_millis", paramzze.atC);
    zza(paramStringBuilder, paramInt, "app_instance_id", paramzze.atL);
    zza(paramStringBuilder, paramInt, "resettable_device_id", paramzze.atJ);
    zza(paramStringBuilder, paramInt, "device_id", paramzze.atT);
    zza(paramStringBuilder, paramInt, "limited_ad_tracking", paramzze.atK);
    zza(paramStringBuilder, paramInt, "os_version", paramzze.zzct);
    zza(paramStringBuilder, paramInt, "device_model", paramzze.atE);
    zza(paramStringBuilder, paramInt, "user_default_language", paramzze.atF);
    zza(paramStringBuilder, paramInt, "time_zone_offset_minutes", paramzze.atG);
    zza(paramStringBuilder, paramInt, "bundle_sequential_index", paramzze.atN);
    zza(paramStringBuilder, paramInt, "service_upload", paramzze.atO);
    zza(paramStringBuilder, paramInt, "health_monitor", paramzze.anU);
    zza(paramStringBuilder, paramInt, paramzze.atx);
    zza(paramStringBuilder, paramInt, paramzze.atP);
    zza(paramStringBuilder, paramInt, paramzze.atw);
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzvk.zzd paramzzd)
  {
    if (paramzzd == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzd.asO != null)
    {
      paramString = "UNKNOWN_COMPARISON_TYPE";
      switch (paramzzd.asO.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "comparison_type", paramString);
      zza(paramStringBuilder, paramInt, "match_as_float", paramzzd.asP);
      zza(paramStringBuilder, paramInt, "comparison_value", paramzzd.asQ);
      zza(paramStringBuilder, paramInt, "min_comparison_value", paramzzd.asR);
      zza(paramStringBuilder, paramInt, "max_comparison_value", paramzzd.asS);
      zza(paramStringBuilder, paramInt);
      paramStringBuilder.append("}\n");
      return;
      paramString = "LESS_THAN";
      continue;
      paramString = "GREATER_THAN";
      continue;
      paramString = "EQUAL";
      continue;
      paramString = "BETWEEN";
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzvk.zzf paramzzf)
  {
    if (paramzzf == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzf.asW != null)
    {
      paramString = "UNKNOWN_MATCH_TYPE";
      switch (paramzzf.asW.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "match_type", paramString);
      zza(paramStringBuilder, paramInt, "expression", paramzzf.asX);
      zza(paramStringBuilder, paramInt, "case_sensitive", paramzzf.asY);
      if (paramzzf.asZ.length <= 0) {
        break label239;
      }
      zza(paramStringBuilder, paramInt + 1);
      paramStringBuilder.append("expression_list {\n");
      paramString = paramzzf.asZ;
      int j = paramString.length;
      int i = 0;
      while (i < j)
      {
        paramzzf = paramString[i];
        zza(paramStringBuilder, paramInt + 2);
        paramStringBuilder.append(paramzzf);
        paramStringBuilder.append("\n");
        i += 1;
      }
      paramString = "REGEXP";
      continue;
      paramString = "BEGINS_WITH";
      continue;
      paramString = "ENDS_WITH";
      continue;
      paramString = "PARTIAL";
      continue;
      paramString = "EXACT";
      continue;
      paramString = "IN_LIST";
    }
    paramStringBuilder.append("}\n");
    label239:
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzvm.zzf paramzzf)
  {
    int j = 0;
    if (paramzzf == null) {
      return;
    }
    int k = paramInt + 1;
    zza(paramStringBuilder, k);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    int m;
    int i;
    long l;
    if (paramzzf.atV != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("results: ");
      paramString = paramzzf.atV;
      m = paramString.length;
      i = 0;
      paramInt = 0;
      while (i < m)
      {
        l = paramString[i];
        if (paramInt != 0) {
          paramStringBuilder.append(", ");
        }
        paramStringBuilder.append(Long.valueOf(l));
        i += 1;
        paramInt += 1;
      }
      paramStringBuilder.append('\n');
    }
    if (paramzzf.atU != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("status: ");
      paramString = paramzzf.atU;
      m = paramString.length;
      paramInt = 0;
      i = j;
      while (i < m)
      {
        l = paramString[i];
        if (paramInt != 0) {
          paramStringBuilder.append(", ");
        }
        paramStringBuilder.append(Long.valueOf(l));
        i += 1;
        paramInt += 1;
      }
      paramStringBuilder.append('\n');
    }
    zza(paramStringBuilder, k);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, Object paramObject)
  {
    if (paramObject == null) {
      return;
    }
    zza(paramStringBuilder, paramInt + 1);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(": ");
    paramStringBuilder.append(paramObject);
    paramStringBuilder.append('\n');
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzvm.zza[] paramArrayOfzza)
  {
    if (paramArrayOfzza == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzza.length;
    paramInt = 0;
    label15:
    zzvm.zza localzza;
    if (paramInt < j)
    {
      localzza = paramArrayOfzza[paramInt];
      if (localzza != null) {
        break label38;
      }
    }
    for (;;)
    {
      paramInt += 1;
      break label15;
      break;
      label38:
      zza(paramStringBuilder, i);
      paramStringBuilder.append("audience_membership {\n");
      zza(paramStringBuilder, i, "audience_id", localzza.asA);
      zza(paramStringBuilder, i, "new_audience", localzza.atm);
      zza(paramStringBuilder, i, "current_data", localzza.atk);
      zza(paramStringBuilder, i, "previous_data", localzza.atl);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzvm.zzb[] paramArrayOfzzb)
  {
    if (paramArrayOfzzb == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzb.length;
    paramInt = 0;
    label15:
    zzvm.zzb localzzb;
    if (paramInt < j)
    {
      localzzb = paramArrayOfzzb[paramInt];
      if (localzzb != null) {
        break label38;
      }
    }
    for (;;)
    {
      paramInt += 1;
      break label15;
      break;
      label38:
      zza(paramStringBuilder, i);
      paramStringBuilder.append("event {\n");
      zza(paramStringBuilder, i, "name", localzzb.name);
      zza(paramStringBuilder, i, "timestamp_millis", localzzb.atp);
      zza(paramStringBuilder, i, "previous_timestamp_millis", localzzb.atq);
      zza(paramStringBuilder, i, "count", localzzb.count);
      zza(paramStringBuilder, i, localzzb.ato);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzvm.zzc[] paramArrayOfzzc)
  {
    if (paramArrayOfzzc == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzc.length;
    paramInt = 0;
    label15:
    zzvm.zzc localzzc;
    if (paramInt < j)
    {
      localzzc = paramArrayOfzzc[paramInt];
      if (localzzc != null) {
        break label38;
      }
    }
    for (;;)
    {
      paramInt += 1;
      break label15;
      break;
      label38:
      zza(paramStringBuilder, i);
      paramStringBuilder.append("param {\n");
      zza(paramStringBuilder, i, "name", localzzc.name);
      zza(paramStringBuilder, i, "string_value", localzzc.Dr);
      zza(paramStringBuilder, i, "int_value", localzzc.ats);
      zza(paramStringBuilder, i, "double_value", localzzc.asx);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzvm.zzg[] paramArrayOfzzg)
  {
    if (paramArrayOfzzg == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzg.length;
    paramInt = 0;
    label15:
    zzvm.zzg localzzg;
    if (paramInt < j)
    {
      localzzg = paramArrayOfzzg[paramInt];
      if (localzzg != null) {
        break label38;
      }
    }
    for (;;)
    {
      paramInt += 1;
      break label15;
      break;
      label38:
      zza(paramStringBuilder, i);
      paramStringBuilder.append("user_property {\n");
      zza(paramStringBuilder, i, "set_timestamp_millis", localzzg.atX);
      zza(paramStringBuilder, i, "name", localzzg.name);
      zza(paramStringBuilder, i, "string_value", localzzg.Dr);
      zza(paramStringBuilder, i, "int_value", localzzg.ats);
      zza(paramStringBuilder, i, "double_value", localzzg.asx);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  public static boolean zza(Context paramContext, String paramString, boolean paramBoolean)
  {
    try
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      if (localPackageManager == null) {
        return false;
      }
      paramContext = localPackageManager.getReceiverInfo(new ComponentName(paramContext, paramString), 2);
      if ((paramContext != null) && (paramContext.enabled)) {
        if (paramBoolean)
        {
          paramBoolean = paramContext.exported;
          if (!paramBoolean) {}
        }
        else
        {
          return true;
        }
      }
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  public static boolean zza(long[] paramArrayOfLong, int paramInt)
  {
    if (paramInt >= paramArrayOfLong.length * 64) {}
    while ((paramArrayOfLong[(paramInt / 64)] & 1L << paramInt % 64) == 0L) {
      return false;
    }
    return true;
  }
  
  public static long[] zza(BitSet paramBitSet)
  {
    int k = (paramBitSet.length() + 63) / 64;
    long[] arrayOfLong = new long[k];
    int i = 0;
    if (i < k)
    {
      arrayOfLong[i] = 0L;
      int j = 0;
      for (;;)
      {
        if ((j >= 64) || (i * 64 + j >= paramBitSet.length()))
        {
          i += 1;
          break;
        }
        if (paramBitSet.get(i * 64 + j)) {
          arrayOfLong[i] |= 1L << j;
        }
        j += 1;
      }
    }
    return arrayOfLong;
  }
  
  public static String zzb(zzvm.zzd paramzzd)
  {
    if (paramzzd == null) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nbatch {\n");
    if (paramzzd.att != null)
    {
      paramzzd = paramzzd.att;
      int j = paramzzd.length;
      int i = 0;
      if (i < j)
      {
        zzvm.zze localzze = paramzzd[i];
        if (localzze == null) {}
        for (;;)
        {
          i += 1;
          break;
          zza(localStringBuilder, 1, localzze);
        }
      }
    }
    localStringBuilder.append("}\n");
    return localStringBuilder.toString();
  }
  
  public static boolean zzbb(String paramString1, String paramString2)
  {
    if ((paramString1 == null) && (paramString2 == null)) {
      return true;
    }
    if (paramString1 == null) {
      return false;
    }
    return paramString1.equals(paramString2);
  }
  
  static MessageDigest zzfi(String paramString)
  {
    int i = 0;
    while (i < 2) {
      try
      {
        MessageDigest localMessageDigest = MessageDigest.getInstance(paramString);
        if (localMessageDigest != null) {
          return localMessageDigest;
        }
      }
      catch (NoSuchAlgorithmException localNoSuchAlgorithmException)
      {
        i += 1;
      }
    }
    return null;
  }
  
  static boolean zzmx(String paramString)
  {
    boolean bool = false;
    com.google.android.gms.common.internal.zzac.zzhz(paramString);
    if (paramString.charAt(0) != '_') {
      bool = true;
    }
    return bool;
  }
  
  private int zzng(String paramString)
  {
    if ("_ldl".equals(paramString)) {
      return zzbvi().zzbtt();
    }
    return zzbvi().zzbts();
  }
  
  public static boolean zznh(String paramString)
  {
    return (!TextUtils.isEmpty(paramString)) && (paramString.startsWith("_"));
  }
  
  static boolean zznj(String paramString)
  {
    return (paramString != null) && (paramString.matches("(\\+|-)?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)")) && (paramString.length() <= 310);
  }
  
  public static boolean zzq(Context paramContext, String paramString)
  {
    try
    {
      PackageManager localPackageManager = paramContext.getPackageManager();
      if (localPackageManager == null) {
        return false;
      }
      paramContext = localPackageManager.getServiceInfo(new ComponentName(paramContext, paramString), 4);
      if (paramContext != null)
      {
        boolean bool = paramContext.enabled;
        if (bool) {
          return true;
        }
      }
    }
    catch (PackageManager.NameNotFoundException paramContext) {}
    return false;
  }
  
  static long zzx(byte[] paramArrayOfByte)
  {
    int j = 0;
    com.google.android.gms.common.internal.zzac.zzy(paramArrayOfByte);
    if (paramArrayOfByte.length > 0) {}
    long l;
    for (boolean bool = true;; bool = false)
    {
      com.google.android.gms.common.internal.zzac.zzbr(bool);
      l = 0L;
      int i = paramArrayOfByte.length - 1;
      while ((i >= 0) && (i >= paramArrayOfByte.length - 8))
      {
        l += ((paramArrayOfByte[i] & 0xFF) << j);
        j += 8;
        i -= 1;
      }
    }
    return l;
  }
  
  public Bundle zza(String paramString, Bundle paramBundle, @Nullable List<String> paramList, boolean paramBoolean)
  {
    Bundle localBundle = null;
    int m;
    int i;
    String str1;
    int j;
    if (paramBundle != null)
    {
      localBundle = new Bundle(paramBundle);
      m = zzbvi().zzbtm();
      Iterator localIterator = paramBundle.keySet().iterator();
      i = 0;
      if (localIterator.hasNext())
      {
        str1 = (String)localIterator.next();
        if ((paramList != null) && (paramList.contains(str1))) {
          break label357;
        }
        if (!paramBoolean) {
          break label351;
        }
        j = zznc(str1);
        label90:
        k = j;
        if (j != 0) {}
      }
    }
    label351:
    label357:
    for (int k = zznd(str1);; k = 0)
    {
      if (k != 0)
      {
        if (zzd(localBundle, k))
        {
          localBundle.putString("_ev", zza(str1, zzbvi().zzbtp(), true));
          if (k == 3) {
            zzb(localBundle, str1);
          }
        }
        localBundle.remove(str1);
        break;
      }
      if ((!zzk(str1, paramBundle.get(str1))) && (!"_ev".equals(str1)))
      {
        if (zzd(localBundle, 4))
        {
          localBundle.putString("_ev", zza(str1, zzbvi().zzbtp(), true));
          zzb(localBundle, paramBundle.get(str1));
        }
        localBundle.remove(str1);
        break;
      }
      j = i;
      if (zzmx(str1))
      {
        i += 1;
        j = i;
        if (i > m)
        {
          String str2 = 48 + "Event can't contain more then " + m + " params";
          zzbvg().zzbwc().zze(str2, paramString, paramBundle);
          zzd(localBundle, 5);
          localBundle.remove(str1);
          break;
        }
      }
      i = j;
      break;
      return localBundle;
      j = 0;
      break label90;
    }
  }
  
  public String zza(String paramString, int paramInt, boolean paramBoolean)
  {
    String str = paramString;
    if (paramString.length() > paramInt)
    {
      if (paramBoolean) {
        str = String.valueOf(paramString.substring(0, paramInt)).concat("...");
      }
    }
    else {
      return str;
    }
    return null;
  }
  
  public void zza(int paramInt1, String paramString1, String paramString2, int paramInt2)
  {
    Bundle localBundle = new Bundle();
    zzd(localBundle, paramInt1);
    if (!TextUtils.isEmpty(paramString1)) {
      localBundle.putString(paramString1, paramString2);
    }
    if ((paramInt1 == 6) || (paramInt1 == 7) || (paramInt1 == 2)) {
      localBundle.putLong("_el", paramInt2);
    }
    this.anq.zzbux().zzf("auto", "_err", localBundle);
  }
  
  public void zza(Bundle paramBundle, String paramString, Object paramObject)
  {
    if (paramBundle == null) {}
    do
    {
      return;
      if ((paramObject instanceof Long))
      {
        paramBundle.putLong(paramString, ((Long)paramObject).longValue());
        return;
      }
      if ((paramObject instanceof String))
      {
        paramBundle.putString(paramString, String.valueOf(paramObject));
        return;
      }
      if ((paramObject instanceof Double))
      {
        paramBundle.putDouble(paramString, ((Double)paramObject).doubleValue());
        return;
      }
    } while (paramString == null);
    if (paramObject != null) {}
    for (paramBundle = paramObject.getClass().getSimpleName();; paramBundle = null)
    {
      zzbvg().zzbwg().zze("Not putting event parameter. Invalid value type. name, type", paramString, paramBundle);
      return;
    }
  }
  
  public void zza(zzvm.zzc paramzzc, Object paramObject)
  {
    com.google.android.gms.common.internal.zzac.zzy(paramObject);
    paramzzc.Dr = null;
    paramzzc.ats = null;
    paramzzc.asx = null;
    if ((paramObject instanceof String))
    {
      paramzzc.Dr = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramzzc.ats = ((Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramzzc.asx = ((Double)paramObject);
      return;
    }
    zzbvg().zzbwc().zzj("Ignoring invalid (type) event param value", paramObject);
  }
  
  public void zza(zzvm.zzg paramzzg, Object paramObject)
  {
    com.google.android.gms.common.internal.zzac.zzy(paramObject);
    paramzzg.Dr = null;
    paramzzg.ats = null;
    paramzzg.asx = null;
    if ((paramObject instanceof String))
    {
      paramzzg.Dr = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramzzg.ats = ((Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramzzg.asx = ((Double)paramObject);
      return;
    }
    zzbvg().zzbwc().zzj("Ignoring invalid (type) user attribute value", paramObject);
  }
  
  boolean zza(String paramString1, String paramString2, int paramInt, Object paramObject)
  {
    if (paramObject == null) {}
    do
    {
      do
      {
        return true;
      } while (((paramObject instanceof Long)) || ((paramObject instanceof Float)) || ((paramObject instanceof Integer)) || ((paramObject instanceof Byte)) || ((paramObject instanceof Short)) || ((paramObject instanceof Boolean)) || ((paramObject instanceof Double)));
      if ((!(paramObject instanceof String)) && (!(paramObject instanceof Character)) && (!(paramObject instanceof CharSequence))) {
        break;
      }
      paramObject = String.valueOf(paramObject);
    } while (((String)paramObject).length() <= paramInt);
    zzbvg().zzbwe().zzd("Value is too long; discarded. Value kind, name, value length", paramString1, paramString2, Integer.valueOf(((String)paramObject).length()));
    return false;
    return false;
  }
  
  public byte[] zza(zzvm.zzd paramzzd)
  {
    try
    {
      byte[] arrayOfByte = new byte[paramzzd.db()];
      zzard localzzard = zzard.zzbe(arrayOfByte);
      paramzzd.zza(localzzard);
      localzzard.cO();
      return arrayOfByte;
    }
    catch (IOException paramzzd)
    {
      zzbvg().zzbwc().zzj("Data loss. Failed to serialize batch", paramzzd);
    }
    return null;
  }
  
  boolean zzaz(String paramString1, String paramString2)
  {
    boolean bool2 = true;
    boolean bool1;
    if (paramString2 == null)
    {
      zzbvg().zzbwc().zzj("Name is required and can't be null. Type", paramString1);
      bool1 = false;
      return bool1;
    }
    if (paramString2.length() == 0)
    {
      zzbvg().zzbwc().zzj("Name is required and can't be empty. Type", paramString1);
      return false;
    }
    if (!Character.isLetter(paramString2.charAt(0)))
    {
      zzbvg().zzbwc().zze("Name must start with a letter. Type, name", paramString1, paramString2);
      return false;
    }
    int i = 1;
    for (;;)
    {
      bool1 = bool2;
      if (i >= paramString2.length()) {
        break;
      }
      char c = paramString2.charAt(i);
      if ((c != '_') && (!Character.isLetterOrDigit(c)))
      {
        zzbvg().zzbwc().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
        return false;
      }
      i += 1;
    }
  }
  
  public void zzb(Bundle paramBundle, Object paramObject)
  {
    com.google.android.gms.common.internal.zzac.zzy(paramBundle);
    if ((paramObject != null) && (((paramObject instanceof String)) || ((paramObject instanceof CharSequence)))) {
      paramBundle.putLong("_el", String.valueOf(paramObject).length());
    }
  }
  
  boolean zzba(String paramString1, String paramString2)
  {
    boolean bool2 = true;
    boolean bool1;
    if (paramString2 == null)
    {
      zzbvg().zzbwc().zzj("Name is required and can't be null. Type", paramString1);
      bool1 = false;
      return bool1;
    }
    if (paramString2.length() == 0)
    {
      zzbvg().zzbwc().zzj("Name is required and can't be empty. Type", paramString1);
      return false;
    }
    char c = paramString2.charAt(0);
    if ((!Character.isLetter(c)) && (c != '_'))
    {
      zzbvg().zzbwc().zze("Name must start with a letter or _ (underscores). Type, name", paramString1, paramString2);
      return false;
    }
    int i = 1;
    for (;;)
    {
      bool1 = bool2;
      if (i >= paramString2.length()) {
        break;
      }
      c = paramString2.charAt(i);
      if ((c != '_') && (!Character.isLetterOrDigit(c)))
      {
        zzbvg().zzbwc().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
        return false;
      }
      i += 1;
    }
  }
  
  boolean zzc(String paramString1, int paramInt, String paramString2)
  {
    if (paramString2 == null)
    {
      zzbvg().zzbwc().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.length() > paramInt)
    {
      zzbvg().zzbwc().zzd("Name is too long. Type, maximum supported length, name", paramString1, Integer.valueOf(paramInt), paramString2);
      return false;
    }
    return true;
  }
  
  boolean zzc(String paramString1, Map<String, String> paramMap, String paramString2)
  {
    if (paramString2 == null)
    {
      zzbvg().zzbwc().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.startsWith("firebase_"))
    {
      zzbvg().zzbwc().zze("Name starts with reserved prefix. Type, name", paramString1, paramString2);
      return false;
    }
    if ((paramMap != null) && (paramMap.containsKey(paramString2)))
    {
      zzbvg().zzbwc().zze("Name is reserved. Type, name", paramString1, paramString2);
      return false;
    }
    return true;
  }
  
  public boolean zzd(Bundle paramBundle, int paramInt)
  {
    if (paramBundle == null) {}
    while (paramBundle.getLong("_err") != 0L) {
      return false;
    }
    paramBundle.putLong("_err", paramInt);
    return true;
  }
  
  @WorkerThread
  public boolean zzew(String paramString)
  {
    zzyl();
    if (getContext().checkCallingOrSelfPermission(paramString) == 0) {
      return true;
    }
    zzbvg().zzbwi().zzj("Permission not granted", paramString);
    return false;
  }
  
  public boolean zzg(long paramLong1, long paramLong2)
  {
    if ((paramLong1 == 0L) || (paramLong2 <= 0L)) {}
    while (Math.abs(zzaan().currentTimeMillis() - paramLong1) > paramLong2) {
      return true;
    }
    return false;
  }
  
  public byte[] zzj(byte[] paramArrayOfByte)
    throws IOException
  {
    try
    {
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      GZIPOutputStream localGZIPOutputStream = new GZIPOutputStream(localByteArrayOutputStream);
      localGZIPOutputStream.write(paramArrayOfByte);
      localGZIPOutputStream.close();
      localByteArrayOutputStream.close();
      paramArrayOfByte = localByteArrayOutputStream.toByteArray();
      return paramArrayOfByte;
    }
    catch (IOException paramArrayOfByte)
    {
      zzbvg().zzbwc().zzj("Failed to gzip content", paramArrayOfByte);
      throw paramArrayOfByte;
    }
  }
  
  public boolean zzk(String paramString, Object paramObject)
  {
    if (zznh(paramString)) {
      return zza("param", paramString, zzbvi().zzbtr(), paramObject);
    }
    return zza("param", paramString, zzbvi().zzbtq(), paramObject);
  }
  
  public Object zzl(String paramString, Object paramObject)
  {
    if ("_ev".equals(paramString)) {
      return zza(zzbvi().zzbtr(), paramObject, true);
    }
    if (zznh(paramString)) {}
    for (int i = zzbvi().zzbtr();; i = zzbvi().zzbtq()) {
      return zza(i, paramObject, false);
    }
  }
  
  public int zzm(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {}
    for (boolean bool = zza("user property referrer", paramString, zzng(paramString), paramObject); bool; bool = zza("user property", paramString, zzng(paramString), paramObject)) {
      return 0;
    }
    return 7;
  }
  
  public int zzmy(String paramString)
  {
    if (!zzaz("event", paramString)) {}
    do
    {
      return 2;
      if (!zzc("event", AppMeasurement.zza.anr, paramString)) {
        return 13;
      }
    } while (!zzc("event", zzbvi().zzbtn(), paramString));
    return 0;
  }
  
  public int zzmz(String paramString)
  {
    if (!zzba("event", paramString)) {}
    do
    {
      return 2;
      if (!zzc("event", AppMeasurement.zza.anr, paramString)) {
        return 13;
      }
    } while (!zzc("event", zzbvi().zzbtn(), paramString));
    return 0;
  }
  
  public Object zzn(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {
      return zza(zzng(paramString), paramObject, true);
    }
    return zza(zzng(paramString), paramObject, false);
  }
  
  public int zzna(String paramString)
  {
    if (!zzaz("user property", paramString)) {}
    do
    {
      return 6;
      if (!zzc("user property", AppMeasurement.zze.ant, paramString)) {
        return 15;
      }
    } while (!zzc("user property", zzbvi().zzbto(), paramString));
    return 0;
  }
  
  public int zznb(String paramString)
  {
    if (!zzba("user property", paramString)) {}
    do
    {
      return 6;
      if (!zzc("user property", AppMeasurement.zze.ant, paramString)) {
        return 15;
      }
    } while (!zzc("user property", zzbvi().zzbto(), paramString));
    return 0;
  }
  
  public int zznc(String paramString)
  {
    if (!zzaz("event param", paramString)) {}
    do
    {
      return 3;
      if (!zzc("event param", null, paramString)) {
        return 14;
      }
    } while (!zzc("event param", zzbvi().zzbtp(), paramString));
    return 0;
  }
  
  public int zznd(String paramString)
  {
    if (!zzba("event param", paramString)) {}
    do
    {
      return 3;
      if (!zzc("event param", null, paramString)) {
        return 14;
      }
    } while (!zzc("event param", zzbvi().zzbtp(), paramString));
    return 0;
  }
  
  public boolean zzne(String paramString)
  {
    if (TextUtils.isEmpty(paramString))
    {
      zzbvg().zzbwc().log("Measurement Service called without google_app_id");
      return false;
    }
    if (!paramString.startsWith("1:"))
    {
      zzbvg().zzbwe().zzj("Measurement Service called with unknown id version", paramString);
      return true;
    }
    if (!zznf(paramString))
    {
      zzbvg().zzbwc().zzj("Invalid google_app_id. Firebase Analytics disabled. See", "https://goo.gl/FZRIUV");
      return false;
    }
    return true;
  }
  
  boolean zznf(String paramString)
  {
    com.google.android.gms.common.internal.zzac.zzy(paramString);
    return paramString.matches("^1:\\d+:android:[a-f0-9]+$");
  }
  
  public boolean zzni(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    return zzbvi().zzbuu().equals(paramString);
  }
  
  public Bundle zzt(@NonNull Uri paramUri)
  {
    Object localObject = null;
    if (paramUri == null) {
      return (Bundle)localObject;
    }
    for (;;)
    {
      try
      {
        if (paramUri.isHierarchical())
        {
          str4 = paramUri.getQueryParameter("utm_campaign");
          str3 = paramUri.getQueryParameter("utm_source");
          str2 = paramUri.getQueryParameter("utm_medium");
          str1 = paramUri.getQueryParameter("gclid");
          if ((TextUtils.isEmpty(str4)) && (TextUtils.isEmpty(str3)) && (TextUtils.isEmpty(str2)) && (TextUtils.isEmpty(str1))) {
            break;
          }
          Bundle localBundle = new Bundle();
          if (!TextUtils.isEmpty(str4)) {
            localBundle.putString("campaign", str4);
          }
          if (!TextUtils.isEmpty(str3)) {
            localBundle.putString("source", str3);
          }
          if (!TextUtils.isEmpty(str2)) {
            localBundle.putString("medium", str2);
          }
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("gclid", str1);
          }
          str1 = paramUri.getQueryParameter("utm_term");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("term", str1);
          }
          str1 = paramUri.getQueryParameter("utm_content");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("content", str1);
          }
          str1 = paramUri.getQueryParameter("aclid");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("aclid", str1);
          }
          str1 = paramUri.getQueryParameter("cp1");
          if (!TextUtils.isEmpty(str1)) {
            localBundle.putString("cp1", str1);
          }
          paramUri = paramUri.getQueryParameter("anid");
          localObject = localBundle;
          if (TextUtils.isEmpty(paramUri)) {
            break;
          }
          localBundle.putString("anid", paramUri);
          return localBundle;
        }
      }
      catch (UnsupportedOperationException paramUri)
      {
        zzbvg().zzbwe().zzj("Install referrer url isn't a hierarchical URI", paramUri);
        return null;
      }
      String str1 = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
    }
  }
  
  /* Error */
  public byte[] zzw(byte[] paramArrayOfByte)
    throws IOException
  {
    // Byte code:
    //   0: new 1141	java/io/ByteArrayInputStream
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 1143	java/io/ByteArrayInputStream:<init>	([B)V
    //   8: astore_1
    //   9: new 1145	java/util/zip/GZIPInputStream
    //   12: dup
    //   13: aload_1
    //   14: invokespecial 1148	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
    //   17: astore_3
    //   18: new 993	java/io/ByteArrayOutputStream
    //   21: dup
    //   22: invokespecial 994	java/io/ByteArrayOutputStream:<init>	()V
    //   25: astore 4
    //   27: sipush 1024
    //   30: newarray <illegal type>
    //   32: astore 5
    //   34: aload_3
    //   35: aload 5
    //   37: invokevirtual 1152	java/util/zip/GZIPInputStream:read	([B)I
    //   40: istore_2
    //   41: iload_2
    //   42: ifgt +17 -> 59
    //   45: aload_3
    //   46: invokevirtual 1153	java/util/zip/GZIPInputStream:close	()V
    //   49: aload_1
    //   50: invokevirtual 1154	java/io/ByteArrayInputStream:close	()V
    //   53: aload 4
    //   55: invokevirtual 1011	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   58: areturn
    //   59: aload 4
    //   61: aload 5
    //   63: iconst_0
    //   64: iload_2
    //   65: invokevirtual 1157	java/io/ByteArrayOutputStream:write	([BII)V
    //   68: goto -34 -> 34
    //   71: astore_1
    //   72: aload_0
    //   73: invokevirtual 748	com/google/android/gms/measurement/internal/zzal:zzbvg	()Lcom/google/android/gms/measurement/internal/zzp;
    //   76: invokevirtual 754	com/google/android/gms/measurement/internal/zzp:zzbwc	()Lcom/google/android/gms/measurement/internal/zzp$zza;
    //   79: ldc_w 1159
    //   82: aload_1
    //   83: invokevirtual 833	com/google/android/gms/measurement/internal/zzp$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   86: aload_1
    //   87: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	zzal
    //   0	88	1	paramArrayOfByte	byte[]
    //   40	25	2	i	int
    //   17	29	3	localGZIPInputStream	java.util.zip.GZIPInputStream
    //   25	35	4	localByteArrayOutputStream	ByteArrayOutputStream
    //   32	30	5	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   0	34	71	java/io/IOException
    //   34	41	71	java/io/IOException
    //   45	59	71	java/io/IOException
    //   59	68	71	java/io/IOException
  }
  
  public long zzy(byte[] paramArrayOfByte)
  {
    com.google.android.gms.common.internal.zzac.zzy(paramArrayOfByte);
    MessageDigest localMessageDigest = zzfi("MD5");
    if (localMessageDigest == null)
    {
      zzbvg().zzbwc().log("Failed to get MD5");
      return 0L;
    }
    return zzx(localMessageDigest.digest(paramArrayOfByte));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */