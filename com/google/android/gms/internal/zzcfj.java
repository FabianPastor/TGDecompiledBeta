package com.google.android.gms.internal;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.google.android.gms.common.internal.zzbo;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import java.util.Iterator;
import java.util.Set;

public final class zzcfj
  extends zzchj
{
  private static String[] zzbqI = new String[AppMeasurement.Event.zzbof.length];
  private static String[] zzbqJ = new String[AppMeasurement.Param.zzboh.length];
  private static String[] zzbqK = new String[AppMeasurement.UserProperty.zzbom.length];
  
  zzcfj(zzcgl paramzzcgl)
  {
    super(paramzzcgl);
  }
  
  @Nullable
  private static String zza(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, String[] paramArrayOfString3)
  {
    boolean bool2 = true;
    int i = 0;
    zzbo.zzu(paramArrayOfString1);
    zzbo.zzu(paramArrayOfString2);
    zzbo.zzu(paramArrayOfString3);
    if (paramArrayOfString1.length == paramArrayOfString2.length)
    {
      bool1 = true;
      zzbo.zzaf(bool1);
      if (paramArrayOfString1.length != paramArrayOfString3.length) {
        break label158;
      }
    }
    label158:
    for (boolean bool1 = bool2;; bool1 = false)
    {
      zzbo.zzaf(bool1);
      while (i < paramArrayOfString1.length)
      {
        if (zzcjl.zzR(paramString, paramArrayOfString1[i]))
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
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, zzcjo paramzzcjo)
  {
    if (paramzzcjo == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("filter {\n");
    zza(paramStringBuilder, paramInt, "complement", paramzzcjo.zzbuU);
    zza(paramStringBuilder, paramInt, "param_name", zzdX(paramzzcjo.zzbuV));
    int j = paramInt + 1;
    zzcjr localzzcjr = paramzzcjo.zzbuS;
    if (localzzcjr != null)
    {
      zza(paramStringBuilder, j);
      paramStringBuilder.append("string_filter");
      paramStringBuilder.append(" {\n");
      Object localObject;
      if (localzzcjr.zzbve != null)
      {
        localObject = "UNKNOWN_MATCH_TYPE";
        switch (localzzcjr.zzbve.intValue())
        {
        }
      }
      for (;;)
      {
        zza(paramStringBuilder, j, "match_type", localObject);
        zza(paramStringBuilder, j, "expression", localzzcjr.zzbvf);
        zza(paramStringBuilder, j, "case_sensitive", localzzcjr.zzbvg);
        if (localzzcjr.zzbvh.length <= 0) {
          break label305;
        }
        zza(paramStringBuilder, j + 1);
        paramStringBuilder.append("expression_list {\n");
        localObject = localzzcjr.zzbvh;
        int k = localObject.length;
        int i = 0;
        while (i < k)
        {
          localzzcjr = localObject[i];
          zza(paramStringBuilder, j + 2);
          paramStringBuilder.append(localzzcjr);
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
    zza(paramStringBuilder, paramInt + 1, "number_filter", paramzzcjo.zzbuT);
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append("}\n");
  }
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzcjp paramzzcjp)
  {
    if (paramzzcjp == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzcjp.zzbuW != null)
    {
      paramString = "UNKNOWN_COMPARISON_TYPE";
      switch (paramzzcjp.zzbuW.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "comparison_type", paramString);
      zza(paramStringBuilder, paramInt, "match_as_float", paramzzcjp.zzbuX);
      zza(paramStringBuilder, paramInt, "comparison_value", paramzzcjp.zzbuY);
      zza(paramStringBuilder, paramInt, "min_comparison_value", paramzzcjp.zzbuZ);
      zza(paramStringBuilder, paramInt, "max_comparison_value", paramzzcjp.zzbva);
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
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzcka paramzzcka)
  {
    int j = 0;
    if (paramzzcka == null) {
      return;
    }
    int k = paramInt + 1;
    zza(paramStringBuilder, k);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    int m;
    int i;
    long l;
    if (paramzzcka.zzbwf != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("results: ");
      paramString = paramzzcka.zzbwf;
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
    if (paramzzcka.zzbwe != null)
    {
      zza(paramStringBuilder, k + 1);
      paramStringBuilder.append("status: ");
      paramString = paramzzcka.zzbwe;
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
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, zzcjv[] paramArrayOfzzcjv)
  {
    if (paramArrayOfzzcjv == null) {}
    for (;;)
    {
      return;
      int i = paramArrayOfzzcjv.length;
      paramInt = 0;
      while (paramInt < i)
      {
        zzcjv localzzcjv = paramArrayOfzzcjv[paramInt];
        if (localzzcjv != null)
        {
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("audience_membership {\n");
          zza(paramStringBuilder, 2, "audience_id", localzzcjv.zzbuI);
          zza(paramStringBuilder, 2, "new_audience", localzzcjv.zzbvu);
          zza(paramStringBuilder, 2, "current_data", localzzcjv.zzbvs);
          zza(paramStringBuilder, 2, "previous_data", localzzcjv.zzbvt);
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("}\n");
        }
        paramInt += 1;
      }
    }
  }
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, zzcjw[] paramArrayOfzzcjw)
  {
    if (paramArrayOfzzcjw == null) {}
    for (;;)
    {
      return;
      int j = paramArrayOfzzcjw.length;
      paramInt = 0;
      while (paramInt < j)
      {
        Object localObject1 = paramArrayOfzzcjw[paramInt];
        if (localObject1 != null)
        {
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("event {\n");
          zza(paramStringBuilder, 2, "name", zzdW(((zzcjw)localObject1).name));
          zza(paramStringBuilder, 2, "timestamp_millis", ((zzcjw)localObject1).zzbvx);
          zza(paramStringBuilder, 2, "previous_timestamp_millis", ((zzcjw)localObject1).zzbvy);
          zza(paramStringBuilder, 2, "count", ((zzcjw)localObject1).count);
          localObject1 = ((zzcjw)localObject1).zzbvw;
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
                zza(paramStringBuilder, 3, "name", zzdX(((zzcjx)localObject2).name));
                zza(paramStringBuilder, 3, "string_value", ((zzcjx)localObject2).zzaIF);
                zza(paramStringBuilder, 3, "int_value", ((zzcjx)localObject2).zzbvA);
                zza(paramStringBuilder, 3, "double_value", ((zzcjx)localObject2).zzbuB);
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
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, zzckb[] paramArrayOfzzckb)
  {
    if (paramArrayOfzzckb == null) {}
    for (;;)
    {
      return;
      int i = paramArrayOfzzckb.length;
      paramInt = 0;
      while (paramInt < i)
      {
        zzckb localzzckb = paramArrayOfzzckb[paramInt];
        if (localzzckb != null)
        {
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("user_property {\n");
          zza(paramStringBuilder, 2, "set_timestamp_millis", localzzckb.zzbwh);
          zza(paramStringBuilder, 2, "name", zzdY(localzzckb.name));
          zza(paramStringBuilder, 2, "string_value", localzzckb.zzaIF);
          zza(paramStringBuilder, 2, "int_value", localzzckb.zzbvA);
          zza(paramStringBuilder, 2, "double_value", localzzckb.zzbuB);
          zza(paramStringBuilder, 2);
          paramStringBuilder.append("}\n");
        }
        paramInt += 1;
      }
    }
  }
  
  @Nullable
  private final String zzb(zzcew paramzzcew)
  {
    if (paramzzcew == null) {
      return null;
    }
    if (!zzyw()) {
      return paramzzcew.toString();
    }
    return zzA(paramzzcew.zzyt());
  }
  
  private final boolean zzyw()
  {
    return this.zzboe.zzwF().zzz(3);
  }
  
  @Nullable
  protected final String zzA(Bundle paramBundle)
  {
    if (paramBundle == null) {
      return null;
    }
    if (!zzyw()) {
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
        localStringBuilder.append(zzdX(str));
        localStringBuilder.append("=");
        localStringBuilder.append(paramBundle.get(str));
        break;
        localStringBuilder.append("Bundle[{");
      }
    }
    localStringBuilder.append("}]");
    return localStringBuilder.toString();
  }
  
  @Nullable
  protected final String zza(zzceu paramzzceu)
  {
    if (paramzzceu == null) {
      return null;
    }
    if (!zzyw()) {
      return paramzzceu.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("Event{appId='");
    localStringBuilder.append(paramzzceu.mAppId);
    localStringBuilder.append("', name='");
    localStringBuilder.append(zzdW(paramzzceu.mName));
    localStringBuilder.append("', params=");
    localStringBuilder.append(zzb(paramzzceu.zzbpF));
    localStringBuilder.append("}");
    return localStringBuilder.toString();
  }
  
  protected final String zza(zzcjn paramzzcjn)
  {
    int i = 0;
    if (paramzzcjn == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nevent_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzzcjn.zzbuM);
    zza(localStringBuilder, 0, "event_name", zzdW(paramzzcjn.zzbuN));
    zza(localStringBuilder, 1, "event_count_filter", paramzzcjn.zzbuQ);
    localStringBuilder.append("  filters {\n");
    paramzzcjn = paramzzcjn.zzbuO;
    int j = paramzzcjn.length;
    while (i < j)
    {
      zza(localStringBuilder, 2, paramzzcjn[i]);
      i += 1;
    }
    zza(localStringBuilder, 1);
    localStringBuilder.append("}\n}\n");
    return localStringBuilder.toString();
  }
  
  protected final String zza(zzcjq paramzzcjq)
  {
    if (paramzzcjq == null) {
      return "null";
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nproperty_filter {\n");
    zza(localStringBuilder, 0, "filter_id", paramzzcjq.zzbuM);
    zza(localStringBuilder, 0, "property_name", zzdY(paramzzcjq.zzbvc));
    zza(localStringBuilder, 1, paramzzcjq.zzbvd);
    localStringBuilder.append("}\n");
    return localStringBuilder.toString();
  }
  
  protected final String zza(zzcjy paramzzcjy)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nbatch {\n");
    if (paramzzcjy.zzbvB != null)
    {
      paramzzcjy = paramzzcjy.zzbvB;
      int j = paramzzcjy.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramzzcjy[i];
        if ((localObject != null) && (localObject != null))
        {
          zza(localStringBuilder, 1);
          localStringBuilder.append("bundle {\n");
          zza(localStringBuilder, 1, "protocol_version", ((zzcjz)localObject).zzbvD);
          zza(localStringBuilder, 1, "platform", ((zzcjz)localObject).zzbvL);
          zza(localStringBuilder, 1, "gmp_version", ((zzcjz)localObject).zzbvP);
          zza(localStringBuilder, 1, "uploading_gmp_version", ((zzcjz)localObject).zzbvQ);
          zza(localStringBuilder, 1, "config_version", ((zzcjz)localObject).zzbwb);
          zza(localStringBuilder, 1, "gmp_app_id", ((zzcjz)localObject).zzboQ);
          zza(localStringBuilder, 1, "app_id", ((zzcjz)localObject).zzaH);
          zza(localStringBuilder, 1, "app_version", ((zzcjz)localObject).zzbgW);
          zza(localStringBuilder, 1, "app_version_major", ((zzcjz)localObject).zzbvY);
          zza(localStringBuilder, 1, "firebase_instance_id", ((zzcjz)localObject).zzboY);
          zza(localStringBuilder, 1, "dev_cert_hash", ((zzcjz)localObject).zzbvU);
          zza(localStringBuilder, 1, "app_store", ((zzcjz)localObject).zzboR);
          zza(localStringBuilder, 1, "upload_timestamp_millis", ((zzcjz)localObject).zzbvG);
          zza(localStringBuilder, 1, "start_timestamp_millis", ((zzcjz)localObject).zzbvH);
          zza(localStringBuilder, 1, "end_timestamp_millis", ((zzcjz)localObject).zzbvI);
          zza(localStringBuilder, 1, "previous_bundle_start_timestamp_millis", ((zzcjz)localObject).zzbvJ);
          zza(localStringBuilder, 1, "previous_bundle_end_timestamp_millis", ((zzcjz)localObject).zzbvK);
          zza(localStringBuilder, 1, "app_instance_id", ((zzcjz)localObject).zzbvT);
          zza(localStringBuilder, 1, "resettable_device_id", ((zzcjz)localObject).zzbvR);
          zza(localStringBuilder, 1, "limited_ad_tracking", ((zzcjz)localObject).zzbvS);
          zza(localStringBuilder, 1, "os_version", ((zzcjz)localObject).zzaY);
          zza(localStringBuilder, 1, "device_model", ((zzcjz)localObject).zzbvM);
          zza(localStringBuilder, 1, "user_default_language", ((zzcjz)localObject).zzbvN);
          zza(localStringBuilder, 1, "time_zone_offset_minutes", ((zzcjz)localObject).zzbvO);
          zza(localStringBuilder, 1, "bundle_sequential_index", ((zzcjz)localObject).zzbvV);
          zza(localStringBuilder, 1, "service_upload", ((zzcjz)localObject).zzbvW);
          zza(localStringBuilder, 1, "health_monitor", ((zzcjz)localObject).zzboU);
          if (((zzcjz)localObject).zzbwc.longValue() != 0L) {
            zza(localStringBuilder, 1, "android_id", ((zzcjz)localObject).zzbwc);
          }
          zza(localStringBuilder, 1, ((zzcjz)localObject).zzbvF);
          zza(localStringBuilder, 1, ((zzcjz)localObject).zzbvX);
          zza(localStringBuilder, 1, ((zzcjz)localObject).zzbvE);
          zza(localStringBuilder, 1);
          localStringBuilder.append("}\n");
        }
        i += 1;
      }
    }
    localStringBuilder.append("}\n");
    return localStringBuilder.toString();
  }
  
  @Nullable
  protected final String zzb(zzcez paramzzcez)
  {
    if (paramzzcez == null) {
      return null;
    }
    if (!zzyw()) {
      return paramzzcez.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("origin=");
    localStringBuilder.append(paramzzcez.zzbpc);
    localStringBuilder.append(",name=");
    localStringBuilder.append(zzdW(paramzzcez.name));
    localStringBuilder.append(",params=");
    localStringBuilder.append(zzb(paramzzcez.zzbpM));
    return localStringBuilder.toString();
  }
  
  @Nullable
  protected final String zzdW(String paramString)
  {
    String str;
    if (paramString == null) {
      str = null;
    }
    do
    {
      return str;
      str = paramString;
    } while (!zzyw());
    return zza(paramString, AppMeasurement.Event.zzbog, AppMeasurement.Event.zzbof, zzbqI);
  }
  
  @Nullable
  protected final String zzdX(String paramString)
  {
    String str;
    if (paramString == null) {
      str = null;
    }
    do
    {
      return str;
      str = paramString;
    } while (!zzyw());
    return zza(paramString, AppMeasurement.Param.zzboi, AppMeasurement.Param.zzboh, zzbqJ);
  }
  
  @Nullable
  protected final String zzdY(String paramString)
  {
    Object localObject;
    if (paramString == null) {
      localObject = null;
    }
    do
    {
      return (String)localObject;
      localObject = paramString;
    } while (!zzyw());
    if (paramString.startsWith("_exp_"))
    {
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("experiment_id");
      ((StringBuilder)localObject).append("(");
      ((StringBuilder)localObject).append(paramString);
      ((StringBuilder)localObject).append(")");
      return ((StringBuilder)localObject).toString();
    }
    return zza(paramString, AppMeasurement.UserProperty.zzbon, AppMeasurement.UserProperty.zzbom, zzbqK);
  }
  
  protected final void zzjD() {}
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzcfj.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */