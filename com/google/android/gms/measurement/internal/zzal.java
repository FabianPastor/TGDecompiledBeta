package com.google.android.gms.measurement.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.internal.zzart;
import com.google.android.gms.internal.zzwa.zzb;
import com.google.android.gms.internal.zzwa.zzc;
import com.google.android.gms.internal.zzwa.zzd;
import com.google.android.gms.internal.zzwa.zze;
import com.google.android.gms.internal.zzwa.zzf;
import com.google.android.gms.internal.zzwc.zza;
import com.google.android.gms.internal.zzwc.zzb;
import com.google.android.gms.internal.zzwc.zzc;
import com.google.android.gms.internal.zzwc.zzd;
import com.google.android.gms.internal.zzwc.zze;
import com.google.android.gms.internal.zzwc.zzf;
import com.google.android.gms.internal.zzwc.zzg;
import com.google.android.gms.measurement.AppMeasurement.zza;
import com.google.android.gms.measurement.AppMeasurement.zzg;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import javax.security.auth.x500.X500Principal;

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
  
  public static String zza(zzwa.zzb paramzzb)
  {
    int i = 0;
    if (paramzzb == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nevent_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzzb.awd);
    zza(localStringBuilder, 0, "event_name", paramzzb.awe);
    zza(localStringBuilder, 1, "event_count_filter", paramzzb.awh);
    localStringBuilder.append("  filters {\n");
    paramzzb = paramzzb.awf;
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
  
  public static String zza(zzwa.zze paramzze)
  {
    if (paramzze == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nproperty_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzze.awd);
    zza(localStringBuilder, 0, "property_name", paramzze.awt);
    zza(localStringBuilder, 1, paramzze.awu);
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
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzwa.zzc paramzzc)
  {
    if (paramzzc == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("filter {\n");
    zza(paramStringBuilder, paramInt, "complement", paramzzc.awl);
    zza(paramStringBuilder, paramInt, "param_name", paramzzc.awm);
    zza(paramStringBuilder, paramInt + 1, "string_filter", paramzzc.awj);
    zza(paramStringBuilder, paramInt + 1, "number_filter", paramzzc.awk);
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzwc.zze paramzze)
  {
    if (paramzze == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("bundle {\n");
    zza(paramStringBuilder, paramInt, "protocol_version", paramzze.awU);
    zza(paramStringBuilder, paramInt, "platform", paramzze.axc);
    zza(paramStringBuilder, paramInt, "gmp_version", paramzze.axg);
    zza(paramStringBuilder, paramInt, "uploading_gmp_version", paramzze.axh);
    zza(paramStringBuilder, paramInt, "config_version", paramzze.axt);
    zza(paramStringBuilder, paramInt, "gmp_app_id", paramzze.aqZ);
    zza(paramStringBuilder, paramInt, "app_id", paramzze.zzcs);
    zza(paramStringBuilder, paramInt, "app_version", paramzze.aii);
    zza(paramStringBuilder, paramInt, "app_version_major", paramzze.axp);
    zza(paramStringBuilder, paramInt, "firebase_instance_id", paramzze.arh);
    zza(paramStringBuilder, paramInt, "dev_cert_hash", paramzze.axl);
    zza(paramStringBuilder, paramInt, "app_store", paramzze.ara);
    zza(paramStringBuilder, paramInt, "upload_timestamp_millis", paramzze.awX);
    zza(paramStringBuilder, paramInt, "start_timestamp_millis", paramzze.awY);
    zza(paramStringBuilder, paramInt, "end_timestamp_millis", paramzze.awZ);
    zza(paramStringBuilder, paramInt, "previous_bundle_start_timestamp_millis", paramzze.axa);
    zza(paramStringBuilder, paramInt, "previous_bundle_end_timestamp_millis", paramzze.axb);
    zza(paramStringBuilder, paramInt, "app_instance_id", paramzze.axk);
    zza(paramStringBuilder, paramInt, "resettable_device_id", paramzze.axi);
    zza(paramStringBuilder, paramInt, "device_id", paramzze.axs);
    zza(paramStringBuilder, paramInt, "limited_ad_tracking", paramzze.axj);
    zza(paramStringBuilder, paramInt, "os_version", paramzze.zzdb);
    zza(paramStringBuilder, paramInt, "device_model", paramzze.axd);
    zza(paramStringBuilder, paramInt, "user_default_language", paramzze.axe);
    zza(paramStringBuilder, paramInt, "time_zone_offset_minutes", paramzze.axf);
    zza(paramStringBuilder, paramInt, "bundle_sequential_index", paramzze.axm);
    zza(paramStringBuilder, paramInt, "service_upload", paramzze.axn);
    zza(paramStringBuilder, paramInt, "health_monitor", paramzze.ard);
    zza(paramStringBuilder, paramInt, paramzze.awW);
    zza(paramStringBuilder, paramInt, paramzze.axo);
    zza(paramStringBuilder, paramInt, paramzze.awV);
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzwa.zzd paramzzd)
  {
    if (paramzzd == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzd.awn != null)
    {
      paramString = "UNKNOWN_COMPARISON_TYPE";
      switch (paramzzd.awn.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "comparison_type", paramString);
      zza(paramStringBuilder, paramInt, "match_as_float", paramzzd.awo);
      zza(paramStringBuilder, paramInt, "comparison_value", paramzzd.awp);
      zza(paramStringBuilder, paramInt, "min_comparison_value", paramzzd.awq);
      zza(paramStringBuilder, paramInt, "max_comparison_value", paramzzd.awr);
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
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzwa.zzf paramzzf)
  {
    if (paramzzf == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzf.awv != null)
    {
      paramString = "UNKNOWN_MATCH_TYPE";
      switch (paramzzf.awv.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "match_type", paramString);
      zza(paramStringBuilder, paramInt, "expression", paramzzf.aww);
      zza(paramStringBuilder, paramInt, "case_sensitive", paramzzf.awx);
      if (paramzzf.awy.length <= 0) {
        break label239;
      }
      zza(paramStringBuilder, paramInt + 1);
      paramStringBuilder.append("expression_list {\n");
      paramString = paramzzf.awy;
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
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzwc.zzf paramzzf)
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
    if (paramzzf.axv != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("results: ");
      paramString = paramzzf.axv;
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
    if (paramzzf.axu != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("status: ");
      paramString = paramzzf.axu;
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
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzwc.zza[] paramArrayOfzza)
  {
    if (paramArrayOfzza == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzza.length;
    paramInt = 0;
    label15:
    zzwc.zza localzza;
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
      zza(paramStringBuilder, i, "audience_id", localzza.avZ);
      zza(paramStringBuilder, i, "new_audience", localzza.awL);
      zza(paramStringBuilder, i, "current_data", localzza.awJ);
      zza(paramStringBuilder, i, "previous_data", localzza.awK);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzwc.zzb[] paramArrayOfzzb)
  {
    if (paramArrayOfzzb == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzb.length;
    paramInt = 0;
    label15:
    zzwc.zzb localzzb;
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
      zza(paramStringBuilder, i, "timestamp_millis", localzzb.awO);
      zza(paramStringBuilder, i, "previous_timestamp_millis", localzzb.awP);
      zza(paramStringBuilder, i, "count", localzzb.count);
      zza(paramStringBuilder, i, localzzb.awN);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzwc.zzc[] paramArrayOfzzc)
  {
    if (paramArrayOfzzc == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzc.length;
    paramInt = 0;
    label15:
    zzwc.zzc localzzc;
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
      zza(paramStringBuilder, i, "string_value", localzzc.Fe);
      zza(paramStringBuilder, i, "int_value", localzzc.awR);
      zza(paramStringBuilder, i, "double_value", localzzc.avW);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzwc.zzg[] paramArrayOfzzg)
  {
    if (paramArrayOfzzg == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzg.length;
    paramInt = 0;
    label15:
    zzwc.zzg localzzg;
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
      zza(paramStringBuilder, i, "set_timestamp_millis", localzzg.axx);
      zza(paramStringBuilder, i, "name", localzzg.name);
      zza(paramStringBuilder, i, "string_value", localzzg.Fe);
      zza(paramStringBuilder, i, "int_value", localzzg.awR);
      zza(paramStringBuilder, i, "double_value", localzzg.avW);
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
  
  public static String zzb(zzwc.zzd paramzzd)
  {
    if (paramzzd == null) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nbatch {\n");
    if (paramzzd.awS != null)
    {
      paramzzd = paramzzd.awS;
      int j = paramzzd.length;
      int i = 0;
      if (i < j)
      {
        zzwc.zze localzze = paramzzd[i];
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
  
  @WorkerThread
  static boolean zzc(EventParcel paramEventParcel, AppMetadata paramAppMetadata)
  {
    zzaa.zzy(paramEventParcel);
    zzaa.zzy(paramAppMetadata);
    return (!TextUtils.isEmpty(paramAppMetadata.aqZ)) || ("_in".equals(paramEventParcel.name));
  }
  
  static MessageDigest zzfl(String paramString)
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
  
  static boolean zzmu(String paramString)
  {
    boolean bool = false;
    zzaa.zzib(paramString);
    if (paramString.charAt(0) != '_') {
      bool = true;
    }
    return bool;
  }
  
  private int zznd(String paramString)
  {
    if ("_ldl".equals(paramString)) {
      return zzbwd().zzbuj();
    }
    return zzbwd().zzbui();
  }
  
  public static boolean zzne(String paramString)
  {
    return (!TextUtils.isEmpty(paramString)) && (paramString.startsWith("_"));
  }
  
  static boolean zzng(String paramString)
  {
    return (paramString != null) && (paramString.matches("(\\+|-)?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)")) && (paramString.length() <= 310);
  }
  
  public static boolean zzr(Context paramContext, String paramString)
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
  
  static long zzy(byte[] paramArrayOfByte)
  {
    int j = 0;
    zzaa.zzy(paramArrayOfByte);
    if (paramArrayOfByte.length > 0) {}
    long l;
    for (boolean bool = true;; bool = false)
    {
      zzaa.zzbs(bool);
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
    int i;
    String str1;
    int j;
    if (paramBundle != null)
    {
      localBundle = new Bundle(paramBundle);
      zzbwd().zzbuc();
      Iterator localIterator = paramBundle.keySet().iterator();
      i = 0;
      if (localIterator.hasNext())
      {
        str1 = (String)localIterator.next();
        if ((paramList != null) && (paramList.contains(str1))) {
          break label356;
        }
        if (!paramBoolean) {
          break label350;
        }
        j = zzmz(str1);
        label89:
        k = j;
        if (j != 0) {}
      }
    }
    label350:
    label356:
    for (int k = zzna(str1);; k = 0)
    {
      if (k != 0)
      {
        if (zzd(localBundle, k))
        {
          localBundle.putString("_ev", zza(str1, zzbwd().zzbuf(), true));
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
          localBundle.putString("_ev", zza(str1, zzbwd().zzbuf(), true));
          zzb(localBundle, paramBundle.get(str1));
        }
        localBundle.remove(str1);
        break;
      }
      j = i;
      if (zzmu(str1))
      {
        i += 1;
        j = i;
        if (i > 25)
        {
          String str2 = 48 + "Event can't contain more then " + 25 + " params";
          zzbwb().zzbwy().zze(str2, paramString, paramBundle);
          zzd(localBundle, 5);
          localBundle.remove(str1);
          break;
        }
      }
      i = j;
      break;
      return localBundle;
      j = 0;
      break label89;
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
    this.aqw.zzbvq().zzf("auto", "_err", localBundle);
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
      zzbwb().zzbxb().zze("Not putting event parameter. Invalid value type. name, type", paramString, paramBundle);
      return;
    }
  }
  
  public void zza(zzwc.zzc paramzzc, Object paramObject)
  {
    zzaa.zzy(paramObject);
    paramzzc.Fe = null;
    paramzzc.awR = null;
    paramzzc.avW = null;
    if ((paramObject instanceof String))
    {
      paramzzc.Fe = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramzzc.awR = ((Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramzzc.avW = ((Double)paramObject);
      return;
    }
    zzbwb().zzbwy().zzj("Ignoring invalid (type) event param value", paramObject);
  }
  
  public void zza(zzwc.zzg paramzzg, Object paramObject)
  {
    zzaa.zzy(paramObject);
    paramzzg.Fe = null;
    paramzzg.awR = null;
    paramzzg.avW = null;
    if ((paramObject instanceof String))
    {
      paramzzg.Fe = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramzzg.awR = ((Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramzzg.avW = ((Double)paramObject);
      return;
    }
    zzbwb().zzbwy().zzj("Ignoring invalid (type) user attribute value", paramObject);
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
    zzbwb().zzbxa().zzd("Value is too long; discarded. Value kind, name, value length", paramString1, paramString2, Integer.valueOf(((String)paramObject).length()));
    return false;
    return false;
  }
  
  public byte[] zza(zzwc.zzd paramzzd)
  {
    try
    {
      byte[] arrayOfByte = new byte[paramzzd.cz()];
      zzart localzzart = zzart.zzbe(arrayOfByte);
      paramzzd.zza(localzzart);
      localzzart.cm();
      return arrayOfByte;
    }
    catch (IOException paramzzd)
    {
      zzbwb().zzbwy().zzj("Data loss. Failed to serialize batch", paramzzd);
    }
    return null;
  }
  
  @WorkerThread
  long zzad(Context paramContext, String paramString)
  {
    zzzx();
    zzaa.zzy(paramContext);
    zzaa.zzib(paramString);
    PackageManager localPackageManager = paramContext.getPackageManager();
    MessageDigest localMessageDigest = zzfl("MD5");
    if (localMessageDigest == null)
    {
      zzbwb().zzbwy().log("Could not get MD5 instance");
      return -1L;
    }
    if (localPackageManager != null) {
      try
      {
        if (!zzae(paramContext, paramString))
        {
          paramContext = localPackageManager.getPackageInfo(getContext().getPackageName(), 64);
          if ((paramContext.signatures != null) && (paramContext.signatures.length > 0)) {
            return zzy(localMessageDigest.digest(paramContext.signatures[0].toByteArray()));
          }
          zzbwb().zzbxa().log("Could not get signatures");
          return -1L;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        zzbwb().zzbwy().zzj("Package name not found", paramContext);
      }
    }
    return 0L;
  }
  
  boolean zzae(Context paramContext, String paramString)
  {
    X500Principal localX500Principal = new X500Principal("CN=Android Debug,O=Android,C=US");
    try
    {
      paramContext = paramContext.getPackageManager().getPackageInfo(paramString, 64);
      if ((paramContext != null) && (paramContext.signatures != null) && (paramContext.signatures.length > 0))
      {
        paramContext = paramContext.signatures[0];
        boolean bool = ((X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(paramContext.toByteArray()))).getSubjectX500Principal().equals(localX500Principal);
        return bool;
      }
    }
    catch (CertificateException paramContext)
    {
      zzbwb().zzbwy().zzj("Error obtaining certificate", paramContext);
      return true;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        zzbwb().zzbwy().zzj("Package name not found", paramContext);
      }
    }
  }
  
  boolean zzaz(String paramString1, String paramString2)
  {
    boolean bool2 = true;
    boolean bool1;
    if (paramString2 == null)
    {
      zzbwb().zzbwy().zzj("Name is required and can't be null. Type", paramString1);
      bool1 = false;
      return bool1;
    }
    if (paramString2.length() == 0)
    {
      zzbwb().zzbwy().zzj("Name is required and can't be empty. Type", paramString1);
      return false;
    }
    if (!Character.isLetter(paramString2.charAt(0)))
    {
      zzbwb().zzbwy().zze("Name must start with a letter. Type, name", paramString1, paramString2);
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
        zzbwb().zzbwy().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
        return false;
      }
      i += 1;
    }
  }
  
  public void zzb(Bundle paramBundle, Object paramObject)
  {
    zzaa.zzy(paramBundle);
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
      zzbwb().zzbwy().zzj("Name is required and can't be null. Type", paramString1);
      bool1 = false;
      return bool1;
    }
    if (paramString2.length() == 0)
    {
      zzbwb().zzbwy().zzj("Name is required and can't be empty. Type", paramString1);
      return false;
    }
    char c = paramString2.charAt(0);
    if ((!Character.isLetter(c)) && (c != '_'))
    {
      zzbwb().zzbwy().zze("Name must start with a letter or _ (underscores). Type, name", paramString1, paramString2);
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
        zzbwb().zzbwy().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
        return false;
      }
      i += 1;
    }
  }
  
  boolean zzc(String paramString1, int paramInt, String paramString2)
  {
    if (paramString2 == null)
    {
      zzbwb().zzbwy().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.length() > paramInt)
    {
      zzbwb().zzbwy().zzd("Name is too long. Type, maximum supported length, name", paramString1, Integer.valueOf(paramInt), paramString2);
      return false;
    }
    return true;
  }
  
  boolean zzc(String paramString1, Map<String, String> paramMap, String paramString2)
  {
    if (paramString2 == null)
    {
      zzbwb().zzbwy().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.startsWith("firebase_"))
    {
      zzbwb().zzbwy().zze("Name starts with reserved prefix. Type, name", paramString1, paramString2);
      return false;
    }
    if ((paramMap != null) && (paramMap.containsKey(paramString2)))
    {
      zzbwb().zzbwy().zze("Name is reserved. Type, name", paramString1, paramString2);
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
  public boolean zzez(String paramString)
  {
    zzzx();
    if (getContext().checkCallingOrSelfPermission(paramString) == 0) {
      return true;
    }
    zzbwb().zzbxd().zzj("Permission not granted", paramString);
    return false;
  }
  
  public boolean zzf(long paramLong1, long paramLong2)
  {
    if ((paramLong1 == 0L) || (paramLong2 <= 0L)) {}
    while (Math.abs(zzabz().currentTimeMillis() - paramLong1) > paramLong2) {
      return true;
    }
    return false;
  }
  
  public boolean zzk(String paramString, Object paramObject)
  {
    if (zzne(paramString)) {
      return zza("param", paramString, zzbwd().zzbuh(), paramObject);
    }
    return zza("param", paramString, zzbwd().zzbug(), paramObject);
  }
  
  public byte[] zzk(byte[] paramArrayOfByte)
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
      zzbwb().zzbwy().zzj("Failed to gzip content", paramArrayOfByte);
      throw paramArrayOfByte;
    }
  }
  
  public Object zzl(String paramString, Object paramObject)
  {
    if ("_ev".equals(paramString)) {
      return zza(zzbwd().zzbuh(), paramObject, true);
    }
    if (zzne(paramString)) {}
    for (int i = zzbwd().zzbuh();; i = zzbwd().zzbug()) {
      return zza(i, paramObject, false);
    }
  }
  
  public int zzm(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {}
    for (boolean bool = zza("user property referrer", paramString, zznd(paramString), paramObject); bool; bool = zza("user property", paramString, zznd(paramString), paramObject)) {
      return 0;
    }
    return 7;
  }
  
  public int zzmv(String paramString)
  {
    if (!zzaz("event", paramString)) {}
    do
    {
      return 2;
      if (!zzc("event", AppMeasurement.zza.aqx, paramString)) {
        return 13;
      }
    } while (!zzc("event", zzbwd().zzbud(), paramString));
    return 0;
  }
  
  public int zzmw(String paramString)
  {
    if (!zzba("event", paramString)) {}
    do
    {
      return 2;
      if (!zzc("event", AppMeasurement.zza.aqx, paramString)) {
        return 13;
      }
    } while (!zzc("event", zzbwd().zzbud(), paramString));
    return 0;
  }
  
  public int zzmx(String paramString)
  {
    if (!zzaz("user property", paramString)) {}
    do
    {
      return 6;
      if (!zzc("user property", AppMeasurement.zzg.aqC, paramString)) {
        return 15;
      }
    } while (!zzc("user property", zzbwd().zzbue(), paramString));
    return 0;
  }
  
  public int zzmy(String paramString)
  {
    if (!zzba("user property", paramString)) {}
    do
    {
      return 6;
      if (!zzc("user property", AppMeasurement.zzg.aqC, paramString)) {
        return 15;
      }
    } while (!zzc("user property", zzbwd().zzbue(), paramString));
    return 0;
  }
  
  public int zzmz(String paramString)
  {
    if (!zzaz("event param", paramString)) {}
    do
    {
      return 3;
      if (!zzc("event param", null, paramString)) {
        return 14;
      }
    } while (!zzc("event param", zzbwd().zzbuf(), paramString));
    return 0;
  }
  
  public Object zzn(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {
      return zza(zznd(paramString), paramObject, true);
    }
    return zza(zznd(paramString), paramObject, false);
  }
  
  public int zzna(String paramString)
  {
    if (!zzba("event param", paramString)) {}
    do
    {
      return 3;
      if (!zzc("event param", null, paramString)) {
        return 14;
      }
    } while (!zzc("event param", zzbwd().zzbuf(), paramString));
    return 0;
  }
  
  public boolean zznb(String paramString)
  {
    if (TextUtils.isEmpty(paramString))
    {
      zzbwb().zzbwy().log("Measurement Service called without google_app_id");
      return false;
    }
    if (!paramString.matches("^\\d+:.*"))
    {
      zzbwb().zzbxa().zzj("Measurement Service called with invalid id version", paramString);
      return false;
    }
    if (!paramString.startsWith("1:"))
    {
      zzbwb().zzbxa().zzj("Measurement Service called with unknown id version", paramString);
      return true;
    }
    if (!zznc(paramString))
    {
      zzbwb().zzbwy().zzj("Invalid google_app_id. Firebase Analytics disabled. See", "https://goo.gl/FZRIUV");
      return false;
    }
    return true;
  }
  
  boolean zznc(String paramString)
  {
    zzaa.zzy(paramString);
    return paramString.matches("^1:\\d+:android:[a-f0-9]+$");
  }
  
  public boolean zznf(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    String str = zzbwd().zzbvn();
    zzbwd().zzayi();
    return str.equals(paramString);
  }
  
  boolean zznh(String paramString)
  {
    return "1".equals(zzbvy().zzaw(paramString, "measurement.upload.blacklist_internal"));
  }
  
  boolean zzni(String paramString)
  {
    return "1".equals(zzbvy().zzaw(paramString, "measurement.upload.blacklist_public"));
  }
  
  public Bundle zzu(@NonNull Uri paramUri)
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
        zzbwb().zzbxa().zzj("Install referrer url isn't a hierarchical URI", paramUri);
        return null;
      }
      String str1 = null;
      String str2 = null;
      String str3 = null;
      String str4 = null;
    }
  }
  
  /* Error */
  public byte[] zzx(byte[] paramArrayOfByte)
    throws IOException
  {
    // Byte code:
    //   0: new 957	java/io/ByteArrayInputStream
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 960	java/io/ByteArrayInputStream:<init>	([B)V
    //   8: astore_1
    //   9: new 1253	java/util/zip/GZIPInputStream
    //   12: dup
    //   13: aload_1
    //   14: invokespecial 1256	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
    //   17: astore_3
    //   18: new 1102	java/io/ByteArrayOutputStream
    //   21: dup
    //   22: invokespecial 1103	java/io/ByteArrayOutputStream:<init>	()V
    //   25: astore 4
    //   27: sipush 1024
    //   30: newarray <illegal type>
    //   32: astore 5
    //   34: aload_3
    //   35: aload 5
    //   37: invokevirtual 1260	java/util/zip/GZIPInputStream:read	([B)I
    //   40: istore_2
    //   41: iload_2
    //   42: ifgt +17 -> 59
    //   45: aload_3
    //   46: invokevirtual 1261	java/util/zip/GZIPInputStream:close	()V
    //   49: aload_1
    //   50: invokevirtual 1262	java/io/ByteArrayInputStream:close	()V
    //   53: aload 4
    //   55: invokevirtual 1116	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   58: areturn
    //   59: aload 4
    //   61: aload 5
    //   63: iconst_0
    //   64: iload_2
    //   65: invokevirtual 1265	java/io/ByteArrayOutputStream:write	([BII)V
    //   68: goto -34 -> 34
    //   71: astore_1
    //   72: aload_0
    //   73: invokevirtual 764	com/google/android/gms/measurement/internal/zzal:zzbwb	()Lcom/google/android/gms/measurement/internal/zzq;
    //   76: invokevirtual 770	com/google/android/gms/measurement/internal/zzq:zzbwy	()Lcom/google/android/gms/measurement/internal/zzq$zza;
    //   79: ldc_w 1267
    //   82: aload_1
    //   83: invokevirtual 849	com/google/android/gms/measurement/internal/zzq$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
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
  
  @WorkerThread
  public long zzz(byte[] paramArrayOfByte)
  {
    zzaa.zzy(paramArrayOfByte);
    zzzx();
    MessageDigest localMessageDigest = zzfl("MD5");
    if (localMessageDigest == null)
    {
      zzbwb().zzbwy().log("Failed to get MD5");
      return 0L;
    }
    return zzy(localMessageDigest.digest(paramArrayOfByte));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzal.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */