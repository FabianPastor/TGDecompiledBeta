package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzrs;

public final class zzl
{
  public static zza<Boolean> aoC = zza.zzo("measurement.service_enabled", true);
  public static zza<Boolean> aoD = zza.zzo("measurement.service_client_enabled", true);
  public static zza<String> aoE = zza.zzk("measurement.log_tag", "FA", "FA-SVC");
  public static zza<Long> aoF = zza.zzh("measurement.ad_id_cache_time", 10000L);
  public static zza<Long> aoG = zza.zzh("measurement.monitoring.sample_period_millis", 86400000L);
  public static zza<Long> aoH = zza.zzb("measurement.config.cache_time", 86400000L, 3600000L);
  public static zza<String> aoI = zza.zzav("measurement.config.url_scheme", "https");
  public static zza<String> aoJ = zza.zzav("measurement.config.url_authority", "app-measurement.com");
  public static zza<Integer> aoK = zza.zzab("measurement.upload.max_bundles", 100);
  public static zza<Integer> aoL = zza.zzab("measurement.upload.max_batch_size", 65536);
  public static zza<Integer> aoM = zza.zzab("measurement.upload.max_bundle_size", 65536);
  public static zza<Integer> aoN = zza.zzab("measurement.upload.max_events_per_bundle", 1000);
  public static zza<Integer> aoO = zza.zzab("measurement.upload.max_events_per_day", 100000);
  public static zza<Integer> aoP = zza.zzab("measurement.upload.max_error_events_per_day", 1000);
  public static zza<Integer> aoQ = zza.zzab("measurement.upload.max_public_events_per_day", 50000);
  public static zza<Integer> aoR = zza.zzab("measurement.upload.max_conversions_per_day", 500);
  public static zza<Integer> aoS = zza.zzab("measurement.upload.max_realtime_events_per_day", 10);
  public static zza<Integer> aoT = zza.zzab("measurement.store.max_stored_events_per_app", 100000);
  public static zza<String> aoU = zza.zzav("measurement.upload.url", "https://app-measurement.com/a");
  public static zza<Long> aoV = zza.zzh("measurement.upload.backoff_period", 43200000L);
  public static zza<Long> aoW = zza.zzh("measurement.upload.window_interval", 3600000L);
  public static zza<Long> aoX = zza.zzh("measurement.upload.interval", 3600000L);
  public static zza<Long> aoY = zza.zzh("measurement.upload.realtime_upload_interval", 10000L);
  public static zza<Long> aoZ = zza.zzh("measurement.upload.minimum_delay", 500L);
  public static zza<Long> apa = zza.zzh("measurement.alarm_manager.minimum_interval", 60000L);
  public static zza<Long> apb = zza.zzh("measurement.upload.stale_data_deletion_interval", 86400000L);
  public static zza<Long> apc = zza.zzh("measurement.upload.initial_upload_delay_time", 15000L);
  public static zza<Long> apd = zza.zzh("measurement.upload.retry_time", 1800000L);
  public static zza<Integer> ape = zza.zzab("measurement.upload.retry_count", 6);
  public static zza<Long> apf = zza.zzh("measurement.upload.max_queue_time", 2419200000L);
  public static zza<Integer> apg = zza.zzab("measurement.lifetimevalue.max_currency_tracked", 4);
  public static zza<Integer> aph = zza.zzab("measurement.audience.filter_result_max_count", 200);
  public static zza<Long> api = zza.zzh("measurement.service_client.idle_disconnect_millis", 5000L);
  
  public static final class zza<V>
  {
    private final V cV;
    private final zzrs<V> cW;
    private final String zzbaf;
    
    private zza(String paramString, zzrs<V> paramzzrs, V paramV)
    {
      zzac.zzy(paramzzrs);
      this.cW = paramzzrs;
      this.cV = paramV;
      this.zzbaf = paramString;
    }
    
    static zza<Integer> zzab(String paramString, int paramInt)
    {
      return zzo(paramString, paramInt, paramInt);
    }
    
    static zza<String> zzav(String paramString1, String paramString2)
    {
      return zzk(paramString1, paramString2, paramString2);
    }
    
    static zza<Long> zzb(String paramString, long paramLong1, long paramLong2)
    {
      return new zza(paramString, zzrs.zza(paramString, Long.valueOf(paramLong2)), Long.valueOf(paramLong1));
    }
    
    static zza<Boolean> zzb(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      return new zza(paramString, zzrs.zzm(paramString, paramBoolean2), Boolean.valueOf(paramBoolean1));
    }
    
    static zza<Long> zzh(String paramString, long paramLong)
    {
      return zzb(paramString, paramLong, paramLong);
    }
    
    static zza<String> zzk(String paramString1, String paramString2, String paramString3)
    {
      return new zza(paramString1, zzrs.zzab(paramString1, paramString3), paramString2);
    }
    
    static zza<Integer> zzo(String paramString, int paramInt1, int paramInt2)
    {
      return new zza(paramString, zzrs.zza(paramString, Integer.valueOf(paramInt2)), Integer.valueOf(paramInt1));
    }
    
    static zza<Boolean> zzo(String paramString, boolean paramBoolean)
    {
      return zzb(paramString, paramBoolean, paramBoolean);
    }
    
    public V get()
    {
      return (V)this.cV;
    }
    
    public V get(V paramV)
    {
      if (paramV != null) {
        return paramV;
      }
      return (V)this.cV;
    }
    
    public String getKey()
    {
      return this.zzbaf;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */