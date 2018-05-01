package com.google.android.gms.internal;

import android.os.Bundle;
import com.google.android.gms.common.internal.zzbq;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import java.util.Iterator;
import java.util.Set;

public final class zzchk
  extends zzcjl
{
  private static String[] zzjbq = new String[AppMeasurement.Event.zziwg.length];
  private static String[] zzjbr = new String[AppMeasurement.Param.zziwi.length];
  private static String[] zzjbs = new String[AppMeasurement.UserProperty.zziwn.length];
  
  zzchk(zzcim paramzzcim)
  {
    super(paramzzcim);
  }
  
  private static String zza(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
  {
    boolean bool2 = true;
    int i = 0;
    zzbq.checkNotNull(paramArrayOfString1);
    zzbq.checkNotNull(paramArrayOfString2);
    zzbq.checkNotNull(paramArrayOfString3);
    if (paramArrayOfString1.length == paramArrayOfString2.length)
    {
      bool1 = true;
      zzbq.checkArgument(bool1);
      if (paramArrayOfString1.length != paramArrayOfString3.length) {
        break label158;
      }
    }
    label158:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzbq.checkArgument(bool1);
      while (i < paramArrayOfString1.length)
      {
        if (zzclq.zzas(paramString, paramArrayOfString1[i]))
        {
          if (paramArrayOfString3[i] == null) {}
          try
          {
            paramString = new StringBuilder();
            paramString.append(paramArrayOfString2[i]);
            paramString.append("(");
            paramString.append(paramArrayOfString1[i]);
            paramString.append(")");
            paramArrayOfString3[i] = paramString.toString();
            paramString = paramArrayOfString3[i];
            return paramString;
          }
          finally {}
        }
        i += 1;
      }
      return paramString;
      bool1 = false;
      break;
    }
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
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, zzclt paramzzclt)
  {
    if (paramzzclt == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("filter {\n");
    zza(paramStringBuilder, paramInt, "complement", paramzzclt.zzjke);
    zza(paramStringBuilder, paramInt, "param_name", zzji(paramzzclt.zzjkf));
    int j = paramInt + 1;
    zzclw localzzclw = paramzzclt.zzjkc;
    if (localzzclw != null)
    {
      zza(paramStringBuilder, j);
      paramStringBuilder.append("string_filter");
      paramStringBuilder.append(" {\n");
      Object localObject;
      if (localzzclw.zzjko != null)
      {
        localObject = "UNKNOWN_MATCH_TYPE";
        switch (localzzclw.zzjko.intValue())
        {
        }
      }
      for (;;)
      {
        zza(paramStringBuilder, j, "match_type", localObject);
        zza(paramStringBuilder, j, "expression", localzzclw.zzjkp);
        zza(paramStringBuilder, j, "case_sensitive", localzzclw.zzjkq);
        if (localzzclw.zzjkr.length <= 0) {
          break label305;
        }
        zza(paramStringBuilder, j + 1);
        paramStringBuilder.append("expression_list {\n");
        localObject = localzzclw.zzjkr;
        int k = localObject.length;
        int i = 0;
        while (i < k)
        {
          localzzclw = localObject[i];
          zza(paramStringBuilder, j + 2);
          paramStringBuilder.append(localzzclw);
          paramStringBuilder.append("\n");
          i += 1;
        }
        localObject = "REGEXP";
        continue;
        localObject = "BEGINS_WITH";
        continue;
        localObject = "ENDS_WITH";
        continue;
        localObject = "PARTIAL";
        continue;
        localObject = "EXACT";
        continue;
        localObject = "IN_LIST";
      }
      paramStringBuilder.append("}\n");
      label305:
      zza(paramStringBuilder, j);
      paramStringBuilder.append("}\n");
    }
    zza(paramStringBuilder, paramInt + 1, "number_filter", paramzzclt.zzjkd);
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzclu paramzzclu)
  {
    if (paramzzclu == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzclu.zzjkg != null)
    {
      paramString = "UNKNOWN_COMPARISON_TYPE";
      switch (paramzzclu.zzjkg.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "comparison_type", paramString);
      zza(paramStringBuilder, paramInt, "match_as_float", paramzzclu.zzjkh);
      zza(paramStringBuilder, paramInt, "comparison_value", paramzzclu.zzjki);
      zza(paramStringBuilder, paramInt, "min_comparison_value", paramzzclu.zzjkj);
      zza(paramStringBuilder, paramInt, "max_comparison_value", paramzzclu.zzjkk);
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
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzcmf paramzzcmf)
  {
    int j = 0;
    if (paramzzcmf == null) {
      return;
    }
    int k = paramInt + 1;
    zza(paramStringBuilder, k);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    int m;
    int i;
    long l;
    if (paramzzcmf.zzjmq != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("results: ");
      paramString = paramzzcmf.zzjmq;
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
    if (paramzzcmf.zzjmp != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("status: ");
      paramString = paramzzcmf.zzjmp;
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
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, zzcma[] paramArrayOfzzcma)
  {
    if (paramArrayOfzzcma == null) {}
    for (;;)
    {
      return;
      int i = paramArrayOfzzcma.length;
      paramInt = 0;
      while (paramInt < i)
      {
        zzcma localzzcma = paramArrayOfzzcma[paramInt];
        if (localzzcma != null)
        {
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("audience_membership {\n");
          zza(paramStringBuilder, 2, "audience_id", localzzcma.zzjjs);
          zza(paramStringBuilder, 2, "new_audience", localzzcma.zzjlf);
          zza(paramStringBuilder, 2, "current_data", localzzcma.zzjld);
          zza(paramStringBuilder, 2, "previous_data", localzzcma.zzjle);
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("}\n");
        }
        paramInt += 1;
      }
    }
  }
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, zzcmb[] paramArrayOfzzcmb)
  {
    if (paramArrayOfzzcmb == null) {}
    for (;;)
    {
      return;
      int j = paramArrayOfzzcmb.length;
      paramInt = 0;
      while (paramInt < j)
      {
        Object localObject1 = paramArrayOfzzcmb[paramInt];
        if (localObject1 != null)
        {
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("event {\n");
          zza(paramStringBuilder, 2, "name", zzjh(((zzcmb)localObject1).name));
          zza(paramStringBuilder, 2, "timestamp_millis", ((zzcmb)localObject1).zzjli);
          zza(paramStringBuilder, 2, "previous_timestamp_millis", ((zzcmb)localObject1).zzjlj);
          zza(paramStringBuilder, 2, "count", ((zzcmb)localObject1).count);
          localObject1 = ((zzcmb)localObject1).zzjlh;
          if (localObject1 != null)
          {
            int k = localObject1.length;
            int i = 0;
            while (i < k)
            {
              Object localObject2 = localObject1[i];
              if (localObject2 != null)
              {
                zza(paramStringBuilder, 3);
                paramStringBuilder.append("param {\n");
                zza(paramStringBuilder, 3, "name", zzji(((zzcmc)localObject2).name));
                zza(paramStringBuilder, 3, "string_value", ((zzcmc)localObject2).zzgcc);
                zza(paramStringBuilder, 3, "int_value", ((zzcmc)localObject2).zzjll);
                zza(paramStringBuilder, 3, "double_value", ((zzcmc)localObject2).zzjjl);
                zza(paramStringBuilder, 3);
                paramStringBuilder.append("}\n");
              }
              i += 1;
            }
          }
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("}\n");
        }
        paramInt += 1;
      }
    }
  }
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, zzcmg[] paramArrayOfzzcmg)
  {
    if (paramArrayOfzzcmg == null) {}
    for (;;)
    {
      return;
      int i = paramArrayOfzzcmg.length;
      paramInt = 0;
      while (paramInt < i)
      {
        zzcmg localzzcmg = paramArrayOfzzcmg[paramInt];
        if (localzzcmg != null)
        {
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("user_property {\n");
          zza(paramStringBuilder, 2, "set_timestamp_millis", localzzcmg.zzjms);
          zza(paramStringBuilder, 2, "name", zzjj(localzzcmg.name));
          zza(paramStringBuilder, 2, "string_value", localzzcmg.zzgcc);
          zza(paramStringBuilder, 2, "int_value", localzzcmg.zzjll);
          zza(paramStringBuilder, 2, "double_value", localzzcmg.zzjjl);
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("}\n");
        }
        paramInt += 1;
      }
    }
  }
  
  private final boolean zzazc()
  {
    return this.zziwf.zzawy().zzae(3);
  }
  
  private final String zzb(zzcgx paramzzcgx)
  {
    if (paramzzcgx == null) {
      return null;
    }
    if (!zzazc()) {
      return paramzzcgx.toString();
    }
    return zzx(paramzzcgx.zzayx());
  }
  
  protected final String zza(zzcgv paramzzcgv)
  {
    if (paramzzcgv == null) {
      return null;
    }
    if (!zzazc()) {
      return paramzzcgv.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Event{appId='");
    localStringBuilder.append(paramzzcgv.mAppId);
    localStringBuilder.append("', name='");
    localStringBuilder.append(zzjh(paramzzcgv.mName));
    localStringBuilder.append("', params=");
    localStringBuilder.append(zzb(paramzzcgv.zzizj));
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  protected final String zza(zzcls paramzzcls)
  {
    int i = 0;
    if (paramzzcls == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nevent_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzzcls.zzjjw);
    zza(localStringBuilder, 0, "event_name", zzjh(paramzzcls.zzjjx));
    zza(localStringBuilder, 1, "event_count_filter", paramzzcls.zzjka);
    localStringBuilder.append("  filters {\n");
    paramzzcls = paramzzcls.zzjjy;
    int j = paramzzcls.length;
    while (i < j)
    {
      zza(localStringBuilder, 2, paramzzcls[i]);
      i += 1;
    }
    zza(localStringBuilder, 1);
    localStringBuilder.append("}\n}\n");
    return localStringBuilder.toString();
  }
  
  protected final String zza(zzclv paramzzclv)
  {
    if (paramzzclv == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nproperty_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzzclv.zzjjw);
    zza(localStringBuilder, 0, "property_name", zzjj(paramzzclv.zzjkm));
    zza(localStringBuilder, 1, paramzzclv.zzjkn);
    localStringBuilder.append("}\n");
    return localStringBuilder.toString();
  }
  
  protected final String zza(zzcmd paramzzcmd)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nbatch {\n");
    if (paramzzcmd.zzjlm != null)
    {
      paramzzcmd = paramzzcmd.zzjlm;
      int j = paramzzcmd.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzcmd[i];
        if ((localObject != null) && (localObject != null))
        {
          zza(localStringBuilder, 1);
          localStringBuilder.append("bundle {\n");
          zza(localStringBuilder, 1, "protocol_version", ((zzcme)localObject).zzjlo);
          zza(localStringBuilder, 1, "platform", ((zzcme)localObject).zzjlw);
          zza(localStringBuilder, 1, "gmp_version", ((zzcme)localObject).zzjma);
          zza(localStringBuilder, 1, "uploading_gmp_version", ((zzcme)localObject).zzjmb);
          zza(localStringBuilder, 1, "config_version", ((zzcme)localObject).zzjmn);
          zza(localStringBuilder, 1, "gmp_app_id", ((zzcme)localObject).zzixs);
          zza(localStringBuilder, 1, "app_id", ((zzcme)localObject).zzcn);
          zza(localStringBuilder, 1, "app_version", ((zzcme)localObject).zzifm);
          zza(localStringBuilder, 1, "app_version_major", ((zzcme)localObject).zzjmj);
          zza(localStringBuilder, 1, "firebase_instance_id", ((zzcme)localObject).zziya);
          zza(localStringBuilder, 1, "dev_cert_hash", ((zzcme)localObject).zzjmf);
          zza(localStringBuilder, 1, "app_store", ((zzcme)localObject).zzixt);
          zza(localStringBuilder, 1, "upload_timestamp_millis", ((zzcme)localObject).zzjlr);
          zza(localStringBuilder, 1, "start_timestamp_millis", ((zzcme)localObject).zzjls);
          zza(localStringBuilder, 1, "end_timestamp_millis", ((zzcme)localObject).zzjlt);
          zza(localStringBuilder, 1, "previous_bundle_start_timestamp_millis", ((zzcme)localObject).zzjlu);
          zza(localStringBuilder, 1, "previous_bundle_end_timestamp_millis", ((zzcme)localObject).zzjlv);
          zza(localStringBuilder, 1, "app_instance_id", ((zzcme)localObject).zzjme);
          zza(localStringBuilder, 1, "resettable_device_id", ((zzcme)localObject).zzjmc);
          zza(localStringBuilder, 1, "device_id", ((zzcme)localObject).zzjmm);
          zza(localStringBuilder, 1, "limited_ad_tracking", ((zzcme)localObject).zzjmd);
          zza(localStringBuilder, 1, "os_version", ((zzcme)localObject).zzdb);
          zza(localStringBuilder, 1, "device_model", ((zzcme)localObject).zzjlx);
          zza(localStringBuilder, 1, "user_default_language", ((zzcme)localObject).zzjly);
          zza(localStringBuilder, 1, "time_zone_offset_minutes", ((zzcme)localObject).zzjlz);
          zza(localStringBuilder, 1, "bundle_sequential_index", ((zzcme)localObject).zzjmg);
          zza(localStringBuilder, 1, "service_upload", ((zzcme)localObject).zzjmh);
          zza(localStringBuilder, 1, "health_monitor", ((zzcme)localObject).zzixw);
          if (((zzcme)localObject).zzfkk.longValue() != 0L) {
            zza(localStringBuilder, 1, "android_id", ((zzcme)localObject).zzfkk);
          }
          zza(localStringBuilder, 1, ((zzcme)localObject).zzjlq);
          zza(localStringBuilder, 1, ((zzcme)localObject).zzjmi);
          zza(localStringBuilder, 1, ((zzcme)localObject).zzjlp);
          zza(localStringBuilder, 1);
          localStringBuilder.append("}\n");
        }
        i += 1;
      }
    }
    localStringBuilder.append("}\n");
    return localStringBuilder.toString();
  }
  
  protected final boolean zzaxz()
  {
    return false;
  }
  
  protected final String zzb(zzcha paramzzcha)
  {
    if (paramzzcha == null) {
      return null;
    }
    if (!zzazc()) {
      return paramzzcha.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("origin=");
    localStringBuilder.append(paramzzcha.zziyf);
    localStringBuilder.append(",name=");
    localStringBuilder.append(zzjh(paramzzcha.name));
    localStringBuilder.append(",params=");
    localStringBuilder.append(zzb(paramzzcha.zzizt));
    return localStringBuilder.toString();
  }
  
  protected final String zzjh(String paramString)
  {
    String str;
    if (paramString == null) {
      str = null;
    }
    do
    {
      return str;
      str = paramString;
    } while (!zzazc());
    return zza(paramString, AppMeasurement.Event.zziwh, AppMeasurement.Event.zziwg, zzjbq);
  }
  
  protected final String zzji(String paramString)
  {
    String str;
    if (paramString == null) {
      str = null;
    }
    do
    {
      return str;
      str = paramString;
    } while (!zzazc());
    return zza(paramString, AppMeasurement.Param.zziwj, AppMeasurement.Param.zziwi, zzjbr);
  }
  
  protected final String zzjj(String paramString)
  {
    Object localObject;
    if (paramString == null) {
      localObject = null;
    }
    do
    {
      return (String)localObject;
      localObject = paramString;
    } while (!zzazc());
    if (paramString.startsWith("_exp_"))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("experiment_id");
      ((StringBuilder)localObject).append("(");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(")");
      return ((StringBuilder)localObject).toString();
    }
    return zza(paramString, AppMeasurement.UserProperty.zziwo, AppMeasurement.UserProperty.zziwn, zzjbs);
  }
  
  protected final String zzx(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return null;
    }
    if (!zzazc()) {
      return paramBundle.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    Iterator localIterator = paramBundle.keySet().iterator();
    if (localIterator.hasNext())
    {
      String str = (String)localIterator.next();
      if (localStringBuilder.length() != 0) {
        localStringBuilder.append(", ");
      }
      for (;;)
      {
        localStringBuilder.append(zzji(str));
        localStringBuilder.append("=");
        localStringBuilder.append(paramBundle.get(str));
        break;
        localStringBuilder.append("Bundle[{");
      }
    }
    localStringBuilder.append("}]");
    return localStringBuilder.toString();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzchk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */