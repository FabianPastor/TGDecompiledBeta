package com.google.android.gms.internal.measurement;

import android.os.Bundle;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.measurement.AppMeasurement.Event;
import com.google.android.gms.measurement.AppMeasurement.Param;
import com.google.android.gms.measurement.AppMeasurement.UserProperty;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

public final class zzfe
  extends zzhk
{
  private static final AtomicReference<String[]> zzaij = new AtomicReference();
  private static final AtomicReference<String[]> zzaik = new AtomicReference();
  private static final AtomicReference<String[]> zzail = new AtomicReference();
  
  zzfe(zzgl paramzzgl)
  {
    super(paramzzgl);
  }
  
  private static String zza(String paramString, String[] paramArrayOfString1, String[] paramArrayOfString2, AtomicReference<String[]> paramAtomicReference)
  {
    int i = 0;
    Preconditions.checkNotNull(paramArrayOfString1);
    Preconditions.checkNotNull(paramArrayOfString2);
    Preconditions.checkNotNull(paramAtomicReference);
    boolean bool;
    if (paramArrayOfString1.length == paramArrayOfString2.length)
    {
      bool = true;
      Preconditions.checkArgument(bool);
    }
    for (;;)
    {
      Object localObject = paramString;
      if (i < paramArrayOfString1.length)
      {
        if (!zzjv.zzs(paramString, paramArrayOfString1[i])) {}
      }
      else
      {
        try
        {
          localObject = (String[])paramAtomicReference.get();
          paramString = (String)localObject;
          if (localObject == null)
          {
            paramString = new String[paramArrayOfString2.length];
            paramAtomicReference.set(paramString);
          }
          if (paramString[i] == null)
          {
            localObject = new java/lang/StringBuilder;
            ((StringBuilder)localObject).<init>();
            ((StringBuilder)localObject).append(paramArrayOfString2[i]);
            ((StringBuilder)localObject).append("(");
            ((StringBuilder)localObject).append(paramArrayOfString1[i]);
            ((StringBuilder)localObject).append(")");
            paramString[i] = ((StringBuilder)localObject).toString();
          }
          localObject = paramString[i];
          return (String)localObject;
        }
        finally {}
        bool = false;
        break;
      }
      i++;
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt)
  {
    for (int i = 0; i < paramInt; i++) {
      paramStringBuilder.append("  ");
    }
  }
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, zzka paramzzka)
  {
    if (paramzzka == null) {}
    for (;;)
    {
      return;
      zza(paramStringBuilder, paramInt);
      paramStringBuilder.append("filter {\n");
      zza(paramStringBuilder, paramInt, "complement", paramzzka.zzars);
      zza(paramStringBuilder, paramInt, "param_name", zzbf(paramzzka.zzart));
      int i = paramInt + 1;
      Object localObject = paramzzka.zzarq;
      if (localObject != null)
      {
        zza(paramStringBuilder, i);
        paramStringBuilder.append("string_filter");
        paramStringBuilder.append(" {\n");
        String str;
        if (((zzkd)localObject).zzasc != null)
        {
          str = "UNKNOWN_MATCH_TYPE";
          switch (((zzkd)localObject).zzasc.intValue())
          {
          }
        }
        for (;;)
        {
          zza(paramStringBuilder, i, "match_type", str);
          zza(paramStringBuilder, i, "expression", ((zzkd)localObject).zzasd);
          zza(paramStringBuilder, i, "case_sensitive", ((zzkd)localObject).zzase);
          if (((zzkd)localObject).zzasf.length <= 0) {
            break label302;
          }
          zza(paramStringBuilder, i + 1);
          paramStringBuilder.append("expression_list {\n");
          for (str : ((zzkd)localObject).zzasf)
          {
            zza(paramStringBuilder, i + 2);
            paramStringBuilder.append(str);
            paramStringBuilder.append("\n");
          }
          str = "REGEXP";
          continue;
          str = "BEGINS_WITH";
          continue;
          str = "ENDS_WITH";
          continue;
          str = "PARTIAL";
          continue;
          str = "EXACT";
          continue;
          str = "IN_LIST";
        }
        paramStringBuilder.append("}\n");
        label302:
        zza(paramStringBuilder, i);
        paramStringBuilder.append("}\n");
      }
      zza(paramStringBuilder, paramInt + 1, "number_filter", paramzzka.zzarr);
      zza(paramStringBuilder, paramInt);
      paramStringBuilder.append("}\n");
    }
  }
  
  private final void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzkb paramzzkb)
  {
    if (paramzzkb == null) {
      return;
    }
    zza(paramStringBuilder, paramInt);
    paramStringBuilder.append(paramString);
    paramStringBuilder.append(" {\n");
    if (paramzzkb.zzaru != null)
    {
      paramString = "UNKNOWN_COMPARISON_TYPE";
      switch (paramzzkb.zzaru.intValue())
      {
      }
    }
    for (;;)
    {
      zza(paramStringBuilder, paramInt, "comparison_type", paramString);
      zza(paramStringBuilder, paramInt, "match_as_float", paramzzkb.zzarv);
      zza(paramStringBuilder, paramInt, "comparison_value", paramzzkb.zzarw);
      zza(paramStringBuilder, paramInt, "min_comparison_value", paramzzkb.zzarx);
      zza(paramStringBuilder, paramInt, "max_comparison_value", paramzzkb.zzary);
      zza(paramStringBuilder, paramInt);
      paramStringBuilder.append("}\n");
      break;
      paramString = "LESS_THAN";
      continue;
      paramString = "GREATER_THAN";
      continue;
      paramString = "EQUAL";
      continue;
      paramString = "BETWEEN";
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, zzkm paramzzkm)
  {
    if (paramzzkm == null) {}
    for (;;)
    {
      return;
      zza(paramStringBuilder, 3);
      paramStringBuilder.append(paramString);
      paramStringBuilder.append(" {\n");
      int i;
      int j;
      long l;
      if (paramzzkm.zzaug != null)
      {
        zza(paramStringBuilder, 4);
        paramStringBuilder.append("results: ");
        paramString = paramzzkm.zzaug;
        i = paramString.length;
        j = 0;
        for (paramInt = 0; j < i; paramInt++)
        {
          l = paramString[j];
          if (paramInt != 0) {
            paramStringBuilder.append(", ");
          }
          paramStringBuilder.append(Long.valueOf(l));
          j++;
        }
        paramStringBuilder.append('\n');
      }
      if (paramzzkm.zzauf != null)
      {
        zza(paramStringBuilder, 4);
        paramStringBuilder.append("status: ");
        paramString = paramzzkm.zzauf;
        i = paramString.length;
        j = 0;
        for (paramInt = 0; j < i; paramInt++)
        {
          l = paramString[j];
          if (paramInt != 0) {
            paramStringBuilder.append(", ");
          }
          paramStringBuilder.append(Long.valueOf(l));
          j++;
        }
        paramStringBuilder.append('\n');
      }
      zza(paramStringBuilder, 3);
      paramStringBuilder.append("}\n");
    }
  }
  
  private static void zza(StringBuilder paramStringBuilder, int paramInt, String paramString, Object paramObject)
  {
    if (paramObject == null) {}
    for (;;)
    {
      return;
      zza(paramStringBuilder, paramInt + 1);
      paramStringBuilder.append(paramString);
      paramStringBuilder.append(": ");
      paramStringBuilder.append(paramObject);
      paramStringBuilder.append('\n');
    }
  }
  
  private final boolean zzik()
  {
    return this.zzacr.zzgg().isLoggable(3);
  }
  
  protected final String zza(zzjz paramzzjz)
  {
    int i = 0;
    if (paramzzjz == null) {}
    StringBuilder localStringBuilder;
    for (paramzzjz = "null";; paramzzjz = localStringBuilder.toString())
    {
      return paramzzjz;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("\nevent_filter {\n");
      zza(localStringBuilder, 0, "filter_id", paramzzjz.zzark);
      zza(localStringBuilder, 0, "event_name", zzbe(paramzzjz.zzarl));
      zza(localStringBuilder, 1, "event_count_filter", paramzzjz.zzaro);
      localStringBuilder.append("  filters {\n");
      paramzzjz = paramzzjz.zzarm;
      int j = paramzzjz.length;
      while (i < j)
      {
        zza(localStringBuilder, 2, paramzzjz[i]);
        i++;
      }
      zza(localStringBuilder, 1);
      localStringBuilder.append("}\n}\n");
    }
  }
  
  protected final String zza(zzkc paramzzkc)
  {
    if (paramzzkc == null) {}
    StringBuilder localStringBuilder;
    for (paramzzkc = "null";; paramzzkc = localStringBuilder.toString())
    {
      return paramzzkc;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("\nproperty_filter {\n");
      zza(localStringBuilder, 0, "filter_id", paramzzkc.zzark);
      zza(localStringBuilder, 0, "property_name", zzbg(paramzzkc.zzasa));
      zza(localStringBuilder, 1, paramzzkc.zzasb);
      localStringBuilder.append("}\n");
    }
  }
  
  protected final String zza(zzkk paramzzkk)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("\nbatch {\n");
    if (paramzzkk.zzata != null) {
      for (zzki[] arrayOfzzki : paramzzkk.zzata) {
        if ((arrayOfzzki != null) && (arrayOfzzki != null))
        {
          zza(localStringBuilder, 1);
          localStringBuilder.append("bundle {\n");
          zza(localStringBuilder, 1, "protocol_version", arrayOfzzki.zzatc);
          zza(localStringBuilder, 1, "platform", arrayOfzzki.zzatk);
          zza(localStringBuilder, 1, "gmp_version", arrayOfzzki.zzato);
          zza(localStringBuilder, 1, "uploading_gmp_version", arrayOfzzki.zzatp);
          zza(localStringBuilder, 1, "config_version", arrayOfzzki.zzaua);
          zza(localStringBuilder, 1, "gmp_app_id", arrayOfzzki.zzadh);
          zza(localStringBuilder, 1, "app_id", arrayOfzzki.zztd);
          zza(localStringBuilder, 1, "app_version", arrayOfzzki.zztc);
          zza(localStringBuilder, 1, "app_version_major", arrayOfzzki.zzatw);
          zza(localStringBuilder, 1, "firebase_instance_id", arrayOfzzki.zzadj);
          zza(localStringBuilder, 1, "dev_cert_hash", arrayOfzzki.zzats);
          zza(localStringBuilder, 1, "app_store", arrayOfzzki.zzado);
          zza(localStringBuilder, 1, "upload_timestamp_millis", arrayOfzzki.zzatf);
          zza(localStringBuilder, 1, "start_timestamp_millis", arrayOfzzki.zzatg);
          zza(localStringBuilder, 1, "end_timestamp_millis", arrayOfzzki.zzath);
          zza(localStringBuilder, 1, "previous_bundle_start_timestamp_millis", arrayOfzzki.zzati);
          zza(localStringBuilder, 1, "previous_bundle_end_timestamp_millis", arrayOfzzki.zzatj);
          zza(localStringBuilder, 1, "app_instance_id", arrayOfzzki.zzadg);
          zza(localStringBuilder, 1, "resettable_device_id", arrayOfzzki.zzatq);
          zza(localStringBuilder, 1, "device_id", arrayOfzzki.zzatz);
          zza(localStringBuilder, 1, "limited_ad_tracking", arrayOfzzki.zzatr);
          zza(localStringBuilder, 1, "os_version", arrayOfzzki.zzatl);
          zza(localStringBuilder, 1, "device_model", arrayOfzzki.zzatm);
          zza(localStringBuilder, 1, "user_default_language", arrayOfzzki.zzafl);
          zza(localStringBuilder, 1, "time_zone_offset_minutes", arrayOfzzki.zzatn);
          zza(localStringBuilder, 1, "bundle_sequential_index", arrayOfzzki.zzatt);
          zza(localStringBuilder, 1, "service_upload", arrayOfzzki.zzatu);
          zza(localStringBuilder, 1, "health_monitor", arrayOfzzki.zzaef);
          if ((arrayOfzzki.zzaub != null) && (arrayOfzzki.zzaub.longValue() != 0L)) {
            zza(localStringBuilder, 1, "android_id", arrayOfzzki.zzaub);
          }
          if (arrayOfzzki.zzaue != null) {
            zza(localStringBuilder, 1, "retry_counter", arrayOfzzki.zzaue);
          }
          Object localObject1 = arrayOfzzki.zzate;
          int k;
          int m;
          if (localObject1 != null)
          {
            k = localObject1.length;
            for (m = 0; m < k; m++)
            {
              localObject2 = localObject1[m];
              if (localObject2 != null)
              {
                zza(localStringBuilder, 2);
                localStringBuilder.append("user_property {\n");
                zza(localStringBuilder, 2, "set_timestamp_millis", ((zzkn)localObject2).zzaui);
                zza(localStringBuilder, 2, "name", zzbg(((zzkn)localObject2).name));
                zza(localStringBuilder, 2, "string_value", ((zzkn)localObject2).zzajf);
                zza(localStringBuilder, 2, "int_value", ((zzkn)localObject2).zzasz);
                zza(localStringBuilder, 2, "double_value", ((zzkn)localObject2).zzaqx);
                zza(localStringBuilder, 2);
                localStringBuilder.append("}\n");
              }
            }
          }
          Object localObject2 = arrayOfzzki.zzatv;
          if (localObject2 != null)
          {
            k = localObject2.length;
            for (m = 0; m < k; m++)
            {
              localObject1 = localObject2[m];
              if (localObject1 != null)
              {
                zza(localStringBuilder, 2);
                localStringBuilder.append("audience_membership {\n");
                zza(localStringBuilder, 2, "audience_id", ((zzkh)localObject1).zzarg);
                zza(localStringBuilder, 2, "new_audience", ((zzkh)localObject1).zzast);
                zza(localStringBuilder, 2, "current_data", ((zzkh)localObject1).zzasr);
                zza(localStringBuilder, 2, "previous_data", ((zzkh)localObject1).zzass);
                zza(localStringBuilder, 2);
                localStringBuilder.append("}\n");
              }
            }
          }
          arrayOfzzki = arrayOfzzki.zzatd;
          if (arrayOfzzki != null)
          {
            int n = arrayOfzzki.length;
            for (m = 0; m < n; m++)
            {
              localObject1 = arrayOfzzki[m];
              if (localObject1 != null)
              {
                zza(localStringBuilder, 2);
                localStringBuilder.append("event {\n");
                zza(localStringBuilder, 2, "name", zzbe(((zzki)localObject1).name));
                zza(localStringBuilder, 2, "timestamp_millis", ((zzki)localObject1).zzasw);
                zza(localStringBuilder, 2, "previous_timestamp_millis", ((zzki)localObject1).zzasx);
                zza(localStringBuilder, 2, "count", ((zzki)localObject1).count);
                localObject2 = ((zzki)localObject1).zzasv;
                if (localObject2 != null)
                {
                  int i1 = localObject2.length;
                  for (k = 0; k < i1; k++)
                  {
                    localObject1 = localObject2[k];
                    if (localObject1 != null)
                    {
                      zza(localStringBuilder, 3);
                      localStringBuilder.append("param {\n");
                      zza(localStringBuilder, 3, "name", zzbf(((zzkj)localObject1).name));
                      zza(localStringBuilder, 3, "string_value", ((zzkj)localObject1).zzajf);
                      zza(localStringBuilder, 3, "int_value", ((zzkj)localObject1).zzasz);
                      zza(localStringBuilder, 3, "double_value", ((zzkj)localObject1).zzaqx);
                      zza(localStringBuilder, 3);
                      localStringBuilder.append("}\n");
                    }
                  }
                }
                zza(localStringBuilder, 2);
                localStringBuilder.append("}\n");
              }
            }
          }
          zza(localStringBuilder, 1);
          localStringBuilder.append("}\n");
        }
      }
    }
    localStringBuilder.append("}\n");
    return localStringBuilder.toString();
  }
  
  protected final String zzb(Bundle paramBundle)
  {
    if (paramBundle == null) {
      paramBundle = null;
    }
    for (;;)
    {
      return paramBundle;
      if (!zzik())
      {
        paramBundle = paramBundle.toString();
      }
      else
      {
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
            localStringBuilder.append(zzbf(str));
            localStringBuilder.append("=");
            localStringBuilder.append(paramBundle.get(str));
            break;
            localStringBuilder.append("Bundle[{");
          }
        }
        localStringBuilder.append("}]");
        paramBundle = localStringBuilder.toString();
      }
    }
  }
  
  protected final String zzbe(String paramString)
  {
    String str;
    if (paramString == null) {
      str = null;
    }
    for (;;)
    {
      return str;
      str = paramString;
      if (zzik()) {
        str = zza(paramString, AppMeasurement.Event.zzact, AppMeasurement.Event.zzacs, zzaij);
      }
    }
  }
  
  protected final String zzbf(String paramString)
  {
    String str;
    if (paramString == null) {
      str = null;
    }
    for (;;)
    {
      return str;
      str = paramString;
      if (zzik()) {
        str = zza(paramString, AppMeasurement.Param.zzacv, AppMeasurement.Param.zzacu, zzaik);
      }
    }
  }
  
  protected final String zzbg(String paramString)
  {
    Object localObject;
    if (paramString == null) {
      localObject = null;
    }
    for (;;)
    {
      return (String)localObject;
      localObject = paramString;
      if (zzik()) {
        if (paramString.startsWith("_exp_"))
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("experiment_id");
          ((StringBuilder)localObject).append("(");
          ((StringBuilder)localObject).append(paramString);
          ((StringBuilder)localObject).append(")");
          localObject = ((StringBuilder)localObject).toString();
        }
        else
        {
          localObject = zza(paramString, AppMeasurement.UserProperty.zzacx, AppMeasurement.UserProperty.zzacw, zzail);
        }
      }
    }
  }
  
  protected final boolean zzhh()
  {
    return false;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/measurement/zzfe.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */