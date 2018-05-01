package com.google.android.gms.internal;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ServiceInfo;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.text.TextUtils;
import com.google.android.gms.common.internal.safeparcel.zzb.zza;
import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.common.util.zze;
import com.google.android.gms.common.util.zzf;
import com.google.android.gms.measurement.AppMeasurement.zza;
import com.google.android.gms.measurement.AppMeasurement.zzg;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.GZIPOutputStream;
import javax.security.auth.x500.X500Principal;

public class zzaut
  extends zzauh
{
  private final AtomicLong zzbwk = new AtomicLong(0L);
  private int zzbwl;
  
  zzaut(zzaue paramzzaue)
  {
    super(paramzzaue);
  }
  
  /* Error */
  public static Object zzI(Object paramObject)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnonnull +5 -> 6
    //   4: aconst_null
    //   5: areturn
    //   6: new 28	java/io/ByteArrayOutputStream
    //   9: dup
    //   10: invokespecial 31	java/io/ByteArrayOutputStream:<init>	()V
    //   13: astore_1
    //   14: new 33	java/io/ObjectOutputStream
    //   17: dup
    //   18: aload_1
    //   19: invokespecial 36	java/io/ObjectOutputStream:<init>	(Ljava/io/OutputStream;)V
    //   22: astore_2
    //   23: aload_2
    //   24: aload_0
    //   25: invokevirtual 40	java/io/ObjectOutputStream:writeObject	(Ljava/lang/Object;)V
    //   28: aload_2
    //   29: invokevirtual 43	java/io/ObjectOutputStream:flush	()V
    //   32: new 45	java/io/ObjectInputStream
    //   35: dup
    //   36: new 47	java/io/ByteArrayInputStream
    //   39: dup
    //   40: aload_1
    //   41: invokevirtual 51	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   44: invokespecial 54	java/io/ByteArrayInputStream:<init>	([B)V
    //   47: invokespecial 57	java/io/ObjectInputStream:<init>	(Ljava/io/InputStream;)V
    //   50: astore_1
    //   51: aload_1
    //   52: invokevirtual 61	java/io/ObjectInputStream:readObject	()Ljava/lang/Object;
    //   55: astore_0
    //   56: aload_2
    //   57: invokevirtual 64	java/io/ObjectOutputStream:close	()V
    //   60: aload_1
    //   61: invokevirtual 65	java/io/ObjectInputStream:close	()V
    //   64: aload_0
    //   65: areturn
    //   66: aload_2
    //   67: ifnull +7 -> 74
    //   70: aload_2
    //   71: invokevirtual 64	java/io/ObjectOutputStream:close	()V
    //   74: aload_1
    //   75: ifnull +7 -> 82
    //   78: aload_1
    //   79: invokevirtual 65	java/io/ObjectInputStream:close	()V
    //   82: aload_0
    //   83: athrow
    //   84: astore_0
    //   85: aconst_null
    //   86: areturn
    //   87: astore_0
    //   88: aconst_null
    //   89: areturn
    //   90: astore_0
    //   91: aconst_null
    //   92: astore_1
    //   93: goto -27 -> 66
    //   96: astore_0
    //   97: goto -31 -> 66
    //   100: astore_0
    //   101: aconst_null
    //   102: astore_1
    //   103: aconst_null
    //   104: astore_2
    //   105: goto -39 -> 66
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	108	0	paramObject	Object
    //   13	90	1	localObject	Object
    //   22	83	2	localObjectOutputStream	java.io.ObjectOutputStream
    // Exception table:
    //   from	to	target	type
    //   56	64	84	java/io/IOException
    //   70	74	84	java/io/IOException
    //   78	82	84	java/io/IOException
    //   82	84	84	java/io/IOException
    //   56	64	87	java/lang/ClassNotFoundException
    //   70	74	87	java/lang/ClassNotFoundException
    //   78	82	87	java/lang/ClassNotFoundException
    //   82	84	87	java/lang/ClassNotFoundException
    //   23	51	90	finally
    //   51	56	96	finally
    //   6	23	100	finally
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
  
  public static String zza(zzauu.zzb paramzzb)
  {
    int i = 0;
    if (paramzzb == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nevent_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzzb.zzbwr);
    zza(localStringBuilder, 0, "event_name", paramzzb.zzbws);
    zza(localStringBuilder, 1, "event_count_filter", paramzzb.zzbwv);
    localStringBuilder.append("  filters {\n");
    paramzzb = paramzzb.zzbwt;
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
  
  public static String zza(zzauu.zze paramzze)
  {
    if (paramzze == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nproperty_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzze.zzbwr);
    zza(localStringBuilder, 0, "property_name", paramzze.zzbwH);
    zza(localStringBuilder, 1, paramzze.zzbwI);
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
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauu.zzc paramzzc)
  {
    if (paramzzc == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("filter {\n");
    zza(paramStringBuilder, paramInt, "complement", paramzzc.zzbwz);
    zza(paramStringBuilder, paramInt, "param_name", paramzzc.zzbwA);
    zza(paramStringBuilder, paramInt + 1, "string_filter", paramzzc.zzbwx);
    zza(paramStringBuilder, paramInt + 1, "number_filter", paramzzc.zzbwy);
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauw.zze paramzze)
  {
    if (paramzze == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("bundle {\n");
    zza(paramStringBuilder, paramInt, "protocol_version", paramzze.zzbxi);
    zza(paramStringBuilder, paramInt, "platform", paramzze.zzbxq);
    zza(paramStringBuilder, paramInt, "gmp_version", paramzze.zzbxu);
    zza(paramStringBuilder, paramInt, "uploading_gmp_version", paramzze.zzbxv);
    zza(paramStringBuilder, paramInt, "config_version", paramzze.zzbxH);
    zza(paramStringBuilder, paramInt, "gmp_app_id", paramzze.zzbqK);
    zza(paramStringBuilder, paramInt, "app_id", paramzze.zzaS);
    zza(paramStringBuilder, paramInt, "app_version", paramzze.zzbhN);
    zza(paramStringBuilder, paramInt, "app_version_major", paramzze.zzbxD);
    zza(paramStringBuilder, paramInt, "app_version_minor", paramzze.zzbxE);
    zza(paramStringBuilder, paramInt, "app_version_release", paramzze.zzbxF);
    zza(paramStringBuilder, paramInt, "firebase_instance_id", paramzze.zzbqS);
    zza(paramStringBuilder, paramInt, "dev_cert_hash", paramzze.zzbxz);
    zza(paramStringBuilder, paramInt, "app_store", paramzze.zzbqL);
    zza(paramStringBuilder, paramInt, "upload_timestamp_millis", paramzze.zzbxl);
    zza(paramStringBuilder, paramInt, "start_timestamp_millis", paramzze.zzbxm);
    zza(paramStringBuilder, paramInt, "end_timestamp_millis", paramzze.zzbxn);
    zza(paramStringBuilder, paramInt, "previous_bundle_start_timestamp_millis", paramzze.zzbxo);
    zza(paramStringBuilder, paramInt, "previous_bundle_end_timestamp_millis", paramzze.zzbxp);
    zza(paramStringBuilder, paramInt, "app_instance_id", paramzze.zzbxy);
    zza(paramStringBuilder, paramInt, "resettable_device_id", paramzze.zzbxw);
    zza(paramStringBuilder, paramInt, "device_id", paramzze.zzbxG);
    zza(paramStringBuilder, paramInt, "limited_ad_tracking", paramzze.zzbxx);
    zza(paramStringBuilder, paramInt, "os_version", paramzze.zzbb);
    zza(paramStringBuilder, paramInt, "device_model", paramzze.zzbxr);
    zza(paramStringBuilder, paramInt, "user_default_language", paramzze.zzbxs);
    zza(paramStringBuilder, paramInt, "time_zone_offset_minutes", paramzze.zzbxt);
    zza(paramStringBuilder, paramInt, "bundle_sequential_index", paramzze.zzbxA);
    zza(paramStringBuilder, paramInt, "service_upload", paramzze.zzbxB);
    zza(paramStringBuilder, paramInt, "health_monitor", paramzze.zzbqO);
    if (paramzze.zzbxI.longValue() != 0L) {
      zza(paramStringBuilder, paramInt, "android_id", paramzze.zzbxI);
    }
    zza(paramStringBuilder, paramInt, paramzze.zzbxk);
    zza(paramStringBuilder, paramInt, paramzze.zzbxC);
    zza(paramStringBuilder, paramInt, paramzze.zzbxj);
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzauu.zzd paramzzd)
  {
    if (paramzzd == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzd.zzbwB != null)
    {
      paramString = "UNKNOWN_COMPARISON_TYPE";
      switch (paramzzd.zzbwB.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "comparison_type", paramString);
      zza(paramStringBuilder, paramInt, "match_as_float", paramzzd.zzbwC);
      zza(paramStringBuilder, paramInt, "comparison_value", paramzzd.zzbwD);
      zza(paramStringBuilder, paramInt, "min_comparison_value", paramzzd.zzbwE);
      zza(paramStringBuilder, paramInt, "max_comparison_value", paramzzd.zzbwF);
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
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzauu.zzf paramzzf)
  {
    if (paramzzf == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzf.zzbwJ != null)
    {
      paramString = "UNKNOWN_MATCH_TYPE";
      switch (paramzzf.zzbwJ.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "match_type", paramString);
      zza(paramStringBuilder, paramInt, "expression", paramzzf.zzbwK);
      zza(paramStringBuilder, paramInt, "case_sensitive", paramzzf.zzbwL);
      if (paramzzf.zzbwM.length <= 0) {
        break label239;
      }
      zza(paramStringBuilder, paramInt + 1);
      paramStringBuilder.append("expression_list {\n");
      paramString = paramzzf.zzbwM;
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
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzauw.zzf paramzzf)
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
    if (paramzzf.zzbxK != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("results: ");
      paramString = paramzzf.zzbxK;
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
    if (paramzzf.zzbxJ != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("status: ");
      paramString = paramzzf.zzbxJ;
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
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauw.zza[] paramArrayOfzza)
  {
    if (paramArrayOfzza == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzza.length;
    paramInt = 0;
    label15:
    zzauw.zza localzza;
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
      zza(paramStringBuilder, i, "audience_id", localzza.zzbwn);
      zza(paramStringBuilder, i, "new_audience", localzza.zzbwZ);
      zza(paramStringBuilder, i, "current_data", localzza.zzbwX);
      zza(paramStringBuilder, i, "previous_data", localzza.zzbwY);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauw.zzb[] paramArrayOfzzb)
  {
    if (paramArrayOfzzb == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzb.length;
    paramInt = 0;
    label15:
    zzauw.zzb localzzb;
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
      zza(paramStringBuilder, i, "timestamp_millis", localzzb.zzbxc);
      zza(paramStringBuilder, i, "previous_timestamp_millis", localzzb.zzbxd);
      zza(paramStringBuilder, i, "count", localzzb.count);
      zza(paramStringBuilder, i, localzzb.zzbxb);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauw.zzc[] paramArrayOfzzc)
  {
    if (paramArrayOfzzc == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzc.length;
    paramInt = 0;
    label15:
    zzauw.zzc localzzc;
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
      zza(paramStringBuilder, i, "string_value", localzzc.zzaGV);
      zza(paramStringBuilder, i, "int_value", localzzc.zzbxf);
      zza(paramStringBuilder, i, "double_value", localzzc.zzbwi);
      zza(paramStringBuilder, i);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, zzauw.zzg[] paramArrayOfzzg)
  {
    if (paramArrayOfzzg == null) {
      return;
    }
    int i = paramInt + 1;
    int j = paramArrayOfzzg.length;
    paramInt = 0;
    label15:
    zzauw.zzg localzzg;
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
      zza(paramStringBuilder, i, "set_timestamp_millis", localzzg.zzbxM);
      zza(paramStringBuilder, i, "name", localzzg.name);
      zza(paramStringBuilder, i, "string_value", localzzg.zzaGV);
      zza(paramStringBuilder, i, "int_value", localzzg.zzbxf);
      zza(paramStringBuilder, i, "double_value", localzzg.zzbwi);
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
  
  public static boolean zzae(String paramString1, String paramString2)
  {
    if ((paramString1 == null) && (paramString2 == null)) {
      return true;
    }
    if (paramString1 == null) {
      return false;
    }
    return paramString1.equals(paramString2);
  }
  
  public static String zzb(zzauw.zzd paramzzd)
  {
    if (paramzzd == null) {
      return "";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nbatch {\n");
    if (paramzzd.zzbxg != null)
    {
      paramzzd = paramzzd.zzbxg;
      int j = paramzzd.length;
      int i = 0;
      if (i < j)
      {
        zzauw.zze localzze = paramzzd[i];
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
  
  static MessageDigest zzch(String paramString)
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
  
  static boolean zzfT(String paramString)
  {
    boolean bool = false;
    zzac.zzdr(paramString);
    if ((paramString.charAt(0) != '_') || (paramString.equals("_ep"))) {
      bool = true;
    }
    return bool;
  }
  
  private int zzgc(String paramString)
  {
    if ("_ldl".equals(paramString)) {
      return zzKn().zzKT();
    }
    return zzKn().zzKS();
  }
  
  public static boolean zzgd(String paramString)
  {
    return (!TextUtils.isEmpty(paramString)) && (paramString.startsWith("_"));
  }
  
  static boolean zzgf(String paramString)
  {
    return (paramString != null) && (paramString.matches("(\\+|-)?([0-9]+\\.?[0-9]*|[0-9]*\\.?[0-9]+)")) && (paramString.length() <= 310);
  }
  
  static long zzy(byte[] paramArrayOfByte)
  {
    int j = 0;
    zzac.zzw(paramArrayOfByte);
    if (paramArrayOfByte.length > 0) {}
    long l;
    for (boolean bool = true;; bool = false)
    {
      zzac.zzav(bool);
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
  
  public static boolean zzy(Context paramContext, String paramString)
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
  
  public boolean zzA(Intent paramIntent)
  {
    paramIntent = paramIntent.getStringExtra("android.intent.extra.REFERRER_NAME");
    return ("android-app://com.google.android.googlequicksearchbox/https/www.google.com".equals(paramIntent)) || ("https://www.google.com".equals(paramIntent)) || ("android-app://com.google.appcrawler".equals(paramIntent));
  }
  
  public Bundle[] zzH(Object paramObject)
  {
    if ((paramObject instanceof Bundle)) {
      return new Bundle[] { (Bundle)paramObject };
    }
    if ((paramObject instanceof Parcelable[])) {
      return (Bundle[])Arrays.copyOf((Parcelable[])paramObject, ((Parcelable[])paramObject).length, Bundle[].class);
    }
    if ((paramObject instanceof ArrayList))
    {
      paramObject = (ArrayList)paramObject;
      return (Bundle[])((ArrayList)paramObject).toArray(new Bundle[((ArrayList)paramObject).size()]);
    }
    return null;
  }
  
  @WorkerThread
  long zzM(Context paramContext, String paramString)
  {
    zzmR();
    zzac.zzw(paramContext);
    zzac.zzdr(paramString);
    PackageManager localPackageManager = paramContext.getPackageManager();
    MessageDigest localMessageDigest = zzch("MD5");
    if (localMessageDigest == null)
    {
      zzKl().zzLZ().log("Could not get MD5 instance");
      return -1L;
    }
    if (localPackageManager != null) {
      try
      {
        if (!zzN(paramContext, paramString))
        {
          paramContext = zzadg.zzbi(paramContext).getPackageInfo(getContext().getPackageName(), 64);
          if ((paramContext.signatures != null) && (paramContext.signatures.length > 0)) {
            return zzy(localMessageDigest.digest(paramContext.signatures[0].toByteArray()));
          }
          zzKl().zzMb().log("Could not get signatures");
          return -1L;
        }
      }
      catch (PackageManager.NameNotFoundException paramContext)
      {
        zzKl().zzLZ().zzj("Package name not found", paramContext);
      }
    }
    return 0L;
  }
  
  Bundle zzN(Bundle paramBundle)
  {
    Bundle localBundle = new Bundle();
    if (paramBundle != null)
    {
      Iterator localIterator = paramBundle.keySet().iterator();
      while (localIterator.hasNext())
      {
        String str = (String)localIterator.next();
        Object localObject = zzk(str, paramBundle.get(str));
        if (localObject == null) {
          zzKl().zzMb().zzj("Param value can't be null", str);
        } else {
          zza(localBundle, str, localObject);
        }
      }
    }
    return localBundle;
  }
  
  boolean zzN(Context paramContext, String paramString)
  {
    X500Principal localX500Principal = new X500Principal("CN=Android Debug,O=Android,C=US");
    try
    {
      paramContext = zzadg.zzbi(paramContext).getPackageInfo(paramString, 64);
      if ((paramContext != null) && (paramContext.signatures != null) && (paramContext.signatures.length > 0))
      {
        paramContext = paramContext.signatures[0];
        boolean bool = ((X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(paramContext.toByteArray()))).getSubjectX500Principal().equals(localX500Principal);
        return bool;
      }
    }
    catch (CertificateException paramContext)
    {
      zzKl().zzLZ().zzj("Error obtaining certificate", paramContext);
      return true;
    }
    catch (PackageManager.NameNotFoundException paramContext)
    {
      for (;;)
      {
        zzKl().zzLZ().zzj("Package name not found", paramContext);
      }
    }
  }
  
  public long zzNk()
  {
    long l1;
    if (this.zzbwk.get() == 0L) {
      synchronized (this.zzbwk)
      {
        l1 = new Random(System.nanoTime() ^ zznR().currentTimeMillis()).nextLong();
        int i = this.zzbwl + 1;
        this.zzbwl = i;
        long l2 = i;
        return l1 + l2;
      }
    }
    synchronized (this.zzbwk)
    {
      this.zzbwk.compareAndSet(-1L, 1L);
      l1 = this.zzbwk.getAndIncrement();
      return l1;
    }
  }
  
  public int zza(String paramString, Object paramObject, boolean paramBoolean)
  {
    if ((paramBoolean) && (!zza("param", paramString, zzKn().zzKR(), paramObject))) {
      return 17;
    }
    if (zzgd(paramString)) {}
    for (paramBoolean = zza("param", paramString, zzKn().zzKQ(), paramObject, paramBoolean); paramBoolean; paramBoolean = zza("param", paramString, zzKn().zzKP(), paramObject, paramBoolean)) {
      return 0;
    }
    return 4;
  }
  
  public Bundle zza(String paramString, Bundle paramBundle, @Nullable List<String> paramList, boolean paramBoolean1, boolean paramBoolean2)
  {
    Bundle localBundle = null;
    int i;
    String str1;
    int j;
    if (paramBundle != null)
    {
      localBundle = new Bundle(paramBundle);
      zzKn().zzKL();
      Iterator localIterator = paramBundle.keySet().iterator();
      i = 0;
      if (localIterator.hasNext())
      {
        str1 = (String)localIterator.next();
        if ((paramList != null) && (paramList.contains(str1))) {
          break label363;
        }
        if (!paramBoolean1) {
          break label357;
        }
        j = zzfY(str1);
        label89:
        k = j;
        if (j != 0) {}
      }
    }
    label357:
    label363:
    for (int k = zzfZ(str1);; k = 0)
    {
      if (k != 0)
      {
        if (zzd(localBundle, k))
        {
          localBundle.putString("_ev", zza(str1, zzKn().zzKO(), true));
          if (k == 3) {
            zzb(localBundle, str1);
          }
        }
        localBundle.remove(str1);
        break;
      }
      j = zza(str1, paramBundle.get(str1), paramBoolean2);
      if ((j != 0) && (!"_ev".equals(str1)))
      {
        if (zzd(localBundle, j))
        {
          localBundle.putString("_ev", zza(str1, zzKn().zzKO(), true));
          zzb(localBundle, paramBundle.get(str1));
        }
        localBundle.remove(str1);
        break;
      }
      j = i;
      if (zzfT(str1))
      {
        i += 1;
        j = i;
        if (i > 25)
        {
          String str2 = 48 + "Event can't contain more then " + 25 + " params";
          zzKl().zzLZ().zze(str2, paramString, paramBundle);
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
  
  zzatq zza(String paramString1, Bundle paramBundle, String paramString2, long paramLong, boolean paramBoolean1, boolean paramBoolean2)
  {
    if (TextUtils.isEmpty(paramString1)) {
      return null;
    }
    if (zzfV(paramString1) != 0)
    {
      zzKl().zzLZ().zzj("Invalid conditional property event name", paramString1);
      throw new IllegalArgumentException();
    }
    if (paramBundle != null)
    {
      paramBundle = new Bundle(paramBundle);
      paramBundle.putString("_o", paramString2);
      paramBundle = zza(paramString1, paramBundle, zzf.zzx("_o"), paramBoolean2, false);
      if (!paramBoolean1) {
        break label118;
      }
      paramBundle = zzN(paramBundle);
    }
    label118:
    for (;;)
    {
      return new zzatq(paramString1, new zzato(paramBundle), paramString2, paramLong);
      paramBundle = new Bundle();
      break;
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
    zza(null, paramInt1, paramString1, paramString2, paramInt2);
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
      zzKl().zzMc().zze("Not putting event parameter. Invalid value type. name, type", paramString, paramBundle);
      return;
    }
  }
  
  public void zza(zzauw.zzc paramzzc, Object paramObject)
  {
    zzac.zzw(paramObject);
    paramzzc.zzaGV = null;
    paramzzc.zzbxf = null;
    paramzzc.zzbwi = null;
    if ((paramObject instanceof String))
    {
      paramzzc.zzaGV = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramzzc.zzbxf = ((Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramzzc.zzbwi = ((Double)paramObject);
      return;
    }
    zzKl().zzLZ().zzj("Ignoring invalid (type) event param value", paramObject);
  }
  
  public void zza(zzauw.zzg paramzzg, Object paramObject)
  {
    zzac.zzw(paramObject);
    paramzzg.zzaGV = null;
    paramzzg.zzbxf = null;
    paramzzg.zzbwi = null;
    if ((paramObject instanceof String))
    {
      paramzzg.zzaGV = ((String)paramObject);
      return;
    }
    if ((paramObject instanceof Long))
    {
      paramzzg.zzbxf = ((Long)paramObject);
      return;
    }
    if ((paramObject instanceof Double))
    {
      paramzzg.zzbwi = ((Double)paramObject);
      return;
    }
    zzKl().zzLZ().zzj("Ignoring invalid (type) user attribute value", paramObject);
  }
  
  public void zza(String paramString1, int paramInt1, String paramString2, String paramString3, int paramInt2)
  {
    paramString1 = new Bundle();
    zzd(paramString1, paramInt1);
    if (!TextUtils.isEmpty(paramString2)) {
      paramString1.putString(paramString2, paramString3);
    }
    if ((paramInt1 == 6) || (paramInt1 == 7) || (paramInt1 == 2)) {
      paramString1.putLong("_el", paramInt2);
    }
    this.zzbqb.zzKn().zzLh();
    this.zzbqb.zzKa().zze("auto", "_err", paramString1);
  }
  
  boolean zza(String paramString1, String paramString2, int paramInt, Object paramObject)
  {
    boolean bool2 = true;
    if ((paramObject instanceof Parcelable[])) {}
    for (int i = ((Parcelable[])paramObject).length;; i = ((ArrayList)paramObject).size())
    {
      boolean bool1 = bool2;
      if (i > paramInt)
      {
        zzKl().zzMb().zzd("Parameter array is too long; discarded. Value kind, name, array length", paramString1, paramString2, Integer.valueOf(i));
        bool1 = false;
      }
      do
      {
        return bool1;
        bool1 = bool2;
      } while (!(paramObject instanceof ArrayList));
    }
  }
  
  boolean zza(String paramString1, String paramString2, int paramInt, Object paramObject, boolean paramBoolean)
  {
    if (paramObject == null) {}
    do
    {
      while (!paramString1.hasNext())
      {
        for (;;)
        {
          return true;
          if ((!(paramObject instanceof Long)) && (!(paramObject instanceof Float)) && (!(paramObject instanceof Integer)) && (!(paramObject instanceof Byte)) && (!(paramObject instanceof Short)) && (!(paramObject instanceof Boolean)) && (!(paramObject instanceof Double))) {
            if (((paramObject instanceof String)) || ((paramObject instanceof Character)) || ((paramObject instanceof CharSequence)))
            {
              paramObject = String.valueOf(paramObject);
              if (((String)paramObject).length() > paramInt)
              {
                zzKl().zzMb().zzd("Value is too long; discarded. Value kind, name, value length", paramString1, paramString2, Integer.valueOf(((String)paramObject).length()));
                return false;
              }
            }
            else if ((!(paramObject instanceof Bundle)) || (!paramBoolean))
            {
              if ((!(paramObject instanceof Parcelable[])) || (!paramBoolean)) {
                break;
              }
              paramString1 = (Parcelable[])paramObject;
              int i = paramString1.length;
              paramInt = 0;
              while (paramInt < i)
              {
                paramObject = paramString1[paramInt];
                if (!(paramObject instanceof Bundle))
                {
                  zzKl().zzMb().zze("All Parcelable[] elements must be of type Bundle. Value type, name", paramObject.getClass(), paramString2);
                  return false;
                }
                paramInt += 1;
              }
            }
          }
        }
        if ((!(paramObject instanceof ArrayList)) || (!paramBoolean)) {
          break;
        }
        paramString1 = ((ArrayList)paramObject).iterator();
      }
      paramObject = paramString1.next();
    } while ((paramObject instanceof Bundle));
    zzKl().zzMb().zze("All ArrayList elements must be of type Bundle. Value type, name", paramObject.getClass(), paramString2);
    return false;
    return false;
  }
  
  byte[] zza(Parcelable paramParcelable)
  {
    if (paramParcelable == null) {
      return null;
    }
    Parcel localParcel = Parcel.obtain();
    try
    {
      paramParcelable.writeToParcel(localParcel, 0);
      paramParcelable = localParcel.marshall();
      return paramParcelable;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  public byte[] zza(zzauw.zzd paramzzd)
  {
    try
    {
      byte[] arrayOfByte = new byte[paramzzd.zzafB()];
      zzbyc localzzbyc = zzbyc.zzah(arrayOfByte);
      paramzzd.zza(localzzbyc);
      localzzbyc.zzafo();
      return arrayOfByte;
    }
    catch (IOException paramzzd)
    {
      zzKl().zzLZ().zzj("Data loss. Failed to serialize batch", paramzzd);
    }
    return null;
  }
  
  boolean zzac(String paramString1, String paramString2)
  {
    if (paramString2 == null)
    {
      zzKl().zzLZ().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.length() == 0)
    {
      zzKl().zzLZ().zzj("Name is required and can't be empty. Type", paramString1);
      return false;
    }
    int i = paramString2.codePointAt(0);
    if (!Character.isLetter(i))
    {
      zzKl().zzLZ().zze("Name must start with a letter. Type, name", paramString1, paramString2);
      return false;
    }
    int j = paramString2.length();
    i = Character.charCount(i);
    while (i < j)
    {
      int k = paramString2.codePointAt(i);
      if ((k != 95) && (!Character.isLetterOrDigit(k)))
      {
        zzKl().zzLZ().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
        return false;
      }
      i += Character.charCount(k);
    }
    return true;
  }
  
  boolean zzad(String paramString1, String paramString2)
  {
    if (paramString2 == null)
    {
      zzKl().zzLZ().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.length() == 0)
    {
      zzKl().zzLZ().zzj("Name is required and can't be empty. Type", paramString1);
      return false;
    }
    int i = paramString2.codePointAt(0);
    if ((!Character.isLetter(i)) && (i != 95))
    {
      zzKl().zzLZ().zze("Name must start with a letter or _ (underscore). Type, name", paramString1, paramString2);
      return false;
    }
    int j = paramString2.length();
    i = Character.charCount(i);
    while (i < j)
    {
      int k = paramString2.codePointAt(i);
      if ((k != 95) && (!Character.isLetterOrDigit(k)))
      {
        zzKl().zzLZ().zze("Name must consist of letters, digits or _ (underscores). Type, name", paramString1, paramString2);
        return false;
      }
      i += Character.charCount(k);
    }
    return true;
  }
  
  <T extends Parcelable> T zzb(byte[] paramArrayOfByte, Parcelable.Creator<T> paramCreator)
  {
    if (paramArrayOfByte == null) {
      return null;
    }
    Parcel localParcel = Parcel.obtain();
    try
    {
      localParcel.unmarshall(paramArrayOfByte, 0, paramArrayOfByte.length);
      localParcel.setDataPosition(0);
      paramArrayOfByte = (Parcelable)paramCreator.createFromParcel(localParcel);
      return paramArrayOfByte;
    }
    catch (zzb.zza paramArrayOfByte)
    {
      zzKl().zzLZ().log("Failed to load parcelable from buffer");
      return null;
    }
    finally
    {
      localParcel.recycle();
    }
  }
  
  public void zzb(Bundle paramBundle, Object paramObject)
  {
    zzac.zzw(paramBundle);
    if ((paramObject != null) && (((paramObject instanceof String)) || ((paramObject instanceof CharSequence)))) {
      paramBundle.putLong("_el", String.valueOf(paramObject).length());
    }
  }
  
  boolean zzb(String paramString1, int paramInt, String paramString2)
  {
    if (paramString2 == null)
    {
      zzKl().zzLZ().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.length() > paramInt)
    {
      zzKl().zzLZ().zzd("Name is too long. Type, maximum supported length, name", paramString1, Integer.valueOf(paramInt), paramString2);
      return false;
    }
    return true;
  }
  
  @WorkerThread
  public boolean zzbW(String paramString)
  {
    zzmR();
    if (zzadg.zzbi(getContext()).checkCallingOrSelfPermission(paramString) == 0) {
      return true;
    }
    zzKl().zzMe().zzj("Permission not granted", paramString);
    return false;
  }
  
  boolean zzc(String paramString1, Map<String, String> paramMap, String paramString2)
  {
    if (paramString2 == null)
    {
      zzKl().zzLZ().zzj("Name is required and can't be null. Type", paramString1);
      return false;
    }
    if (paramString2.startsWith("firebase_"))
    {
      zzKl().zzLZ().zze("Name starts with reserved prefix. Type, name", paramString1, paramString2);
      return false;
    }
    if ((paramMap != null) && (paramMap.containsKey(paramString2)))
    {
      zzKl().zzLZ().zze("Name is reserved. Type, name", paramString1, paramString2);
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
  boolean zzd(zzatq paramzzatq, zzatd paramzzatd)
  {
    zzac.zzw(paramzzatq);
    zzac.zzw(paramzzatd);
    if (TextUtils.isEmpty(paramzzatd.zzbqK))
    {
      zzKn().zzLh();
      return false;
    }
    return true;
  }
  
  public int zzfU(String paramString)
  {
    if (!zzac("event", paramString)) {}
    do
    {
      return 2;
      if (!zzc("event", AppMeasurement.zza.zzbqc, paramString)) {
        return 13;
      }
    } while (!zzb("event", zzKn().zzKM(), paramString));
    return 0;
  }
  
  public int zzfV(String paramString)
  {
    if (!zzad("event", paramString)) {}
    do
    {
      return 2;
      if (!zzc("event", AppMeasurement.zza.zzbqc, paramString)) {
        return 13;
      }
    } while (!zzb("event", zzKn().zzKM(), paramString));
    return 0;
  }
  
  public int zzfW(String paramString)
  {
    if (!zzac("user property", paramString)) {}
    do
    {
      return 6;
      if (!zzc("user property", AppMeasurement.zzg.zzbqh, paramString)) {
        return 15;
      }
    } while (!zzb("user property", zzKn().zzKN(), paramString));
    return 0;
  }
  
  public int zzfX(String paramString)
  {
    if (!zzad("user property", paramString)) {}
    do
    {
      return 6;
      if (!zzc("user property", AppMeasurement.zzg.zzbqh, paramString)) {
        return 15;
      }
    } while (!zzb("user property", zzKn().zzKN(), paramString));
    return 0;
  }
  
  public int zzfY(String paramString)
  {
    if (!zzac("event param", paramString)) {}
    do
    {
      return 3;
      if (!zzc("event param", null, paramString)) {
        return 14;
      }
    } while (!zzb("event param", zzKn().zzKO(), paramString));
    return 0;
  }
  
  public int zzfZ(String paramString)
  {
    if (!zzad("event param", paramString)) {}
    do
    {
      return 3;
      if (!zzc("event param", null, paramString)) {
        return 14;
      }
    } while (!zzb("event param", zzKn().zzKO(), paramString));
    return 0;
  }
  
  public boolean zzga(String paramString)
  {
    if (TextUtils.isEmpty(paramString))
    {
      zzKl().zzLZ().log("Missing google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI");
      return false;
    }
    if (!zzgb(paramString))
    {
      zzKl().zzLZ().zzj("Invalid google_app_id. Firebase Analytics disabled. See https://goo.gl/NAOOOI. provided id", paramString);
      return false;
    }
    return true;
  }
  
  boolean zzgb(String paramString)
  {
    zzac.zzw(paramString);
    return paramString.matches("^1:\\d+:android:[a-f0-9]+$");
  }
  
  public boolean zzge(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return false;
    }
    String str = zzKn().zzLD();
    zzKn().zzLh();
    return str.equals(paramString);
  }
  
  boolean zzgg(String paramString)
  {
    return "1".equals(zzKi().zzZ(paramString, "measurement.upload.blacklist_internal"));
  }
  
  boolean zzgh(String paramString)
  {
    return "1".equals(zzKi().zzZ(paramString, "measurement.upload.blacklist_public"));
  }
  
  @WorkerThread
  boolean zzgi(String paramString)
  {
    boolean bool = true;
    zzac.zzdr(paramString);
    int i = -1;
    switch (paramString.hashCode())
    {
    }
    for (;;)
    {
      switch (i)
      {
      default: 
        bool = false;
      }
      return bool;
      if (paramString.equals("_in"))
      {
        i = 0;
        continue;
        if (paramString.equals("_ui"))
        {
          i = 1;
          continue;
          if (paramString.equals("_ug")) {
            i = 2;
          }
        }
      }
    }
  }
  
  public boolean zzh(long paramLong1, long paramLong2)
  {
    if ((paramLong1 == 0L) || (paramLong2 <= 0L)) {}
    while (Math.abs(zznR().currentTimeMillis() - paramLong1) > paramLong2) {
      return true;
    }
    return false;
  }
  
  public Object zzk(String paramString, Object paramObject)
  {
    if ("_ev".equals(paramString)) {
      return zza(zzKn().zzKQ(), paramObject, true);
    }
    if (zzgd(paramString)) {}
    for (int i = zzKn().zzKQ();; i = zzKn().zzKP()) {
      return zza(i, paramObject, false);
    }
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
      zzKl().zzLZ().zzj("Failed to gzip content", paramArrayOfByte);
      throw paramArrayOfByte;
    }
  }
  
  public int zzl(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {}
    for (boolean bool = zza("user property referrer", paramString, zzgc(paramString), paramObject, false); bool; bool = zza("user property", paramString, zzgc(paramString), paramObject, false)) {
      return 0;
    }
    return 7;
  }
  
  public Object zzm(String paramString, Object paramObject)
  {
    if ("_ldl".equals(paramString)) {
      return zza(zzgc(paramString), paramObject, true);
    }
    return zza(zzgc(paramString), paramObject, false);
  }
  
  protected void zzmS()
  {
    SecureRandom localSecureRandom = new SecureRandom();
    long l2 = localSecureRandom.nextLong();
    long l1 = l2;
    if (l2 == 0L)
    {
      l2 = localSecureRandom.nextLong();
      l1 = l2;
      if (l2 == 0L)
      {
        zzKl().zzMb().log("Utils falling back to Random for random id");
        l1 = l2;
      }
    }
    this.zzbwk.set(l1);
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
        zzKl().zzMb().zzj("Install referrer url isn't a hierarchical URI", paramUri);
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
    //   0: new 47	java/io/ByteArrayInputStream
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 54	java/io/ByteArrayInputStream:<init>	([B)V
    //   8: astore_1
    //   9: new 1477	java/util/zip/GZIPInputStream
    //   12: dup
    //   13: aload_1
    //   14: invokespecial 1478	java/util/zip/GZIPInputStream:<init>	(Ljava/io/InputStream;)V
    //   17: astore_3
    //   18: new 28	java/io/ByteArrayOutputStream
    //   21: dup
    //   22: invokespecial 31	java/io/ByteArrayOutputStream:<init>	()V
    //   25: astore 4
    //   27: sipush 1024
    //   30: newarray <illegal type>
    //   32: astore 5
    //   34: aload_3
    //   35: aload 5
    //   37: invokevirtual 1482	java/util/zip/GZIPInputStream:read	([B)I
    //   40: istore_2
    //   41: iload_2
    //   42: ifgt +17 -> 59
    //   45: aload_3
    //   46: invokevirtual 1483	java/util/zip/GZIPInputStream:close	()V
    //   49: aload_1
    //   50: invokevirtual 1484	java/io/ByteArrayInputStream:close	()V
    //   53: aload 4
    //   55: invokevirtual 51	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   58: areturn
    //   59: aload 4
    //   61: aload 5
    //   63: iconst_0
    //   64: iload_2
    //   65: invokevirtual 1486	java/io/ByteArrayOutputStream:write	([BII)V
    //   68: goto -34 -> 34
    //   71: astore_1
    //   72: aload_0
    //   73: invokevirtual 862	com/google/android/gms/internal/zzaut:zzKl	()Lcom/google/android/gms/internal/zzatx;
    //   76: invokevirtual 868	com/google/android/gms/internal/zzatx:zzLZ	()Lcom/google/android/gms/internal/zzatx$zza;
    //   79: ldc_w 1488
    //   82: aload_1
    //   83: invokevirtual 923	com/google/android/gms/internal/zzatx$zza:zzj	(Ljava/lang/String;Ljava/lang/Object;)V
    //   86: aload_1
    //   87: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	88	0	this	zzaut
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
    zzac.zzw(paramArrayOfByte);
    zzmR();
    MessageDigest localMessageDigest = zzch("MD5");
    if (localMessageDigest == null)
    {
      zzKl().zzLZ().log("Failed to get MD5");
      return 0L;
    }
    return zzy(localMessageDigest.digest(paramArrayOfByte));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzaut.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */