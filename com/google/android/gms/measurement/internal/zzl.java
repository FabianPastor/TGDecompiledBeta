package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzaa;
import com.google.android.gms.internal.zzsi;

public final class zzl
{
  public static zza<Boolean> arM = zza.zzm("measurement.service_enabled", true);
  public static zza<Boolean> arN = zza.zzm("measurement.service_client_enabled", true);
  public static zza<Boolean> arO = zza.zzm("measurement.log_installs_enabled", false);
  public static zza<String> arP = zza.zzk("measurement.log_tag", "FA", "FA-SVC");
  public static zza<Long> arQ = zza.zzf("measurement.ad_id_cache_time", 10000L);
  public static zza<Long> arR = zza.zzf("measurement.monitoring.sample_period_millis", 86400000L);
  public static zza<Long> arS = zza.zzb("measurement.config.cache_time", 86400000L, 3600000L);
  public static zza<String> arT = zza.zzav("measurement.config.url_scheme", "https");
  public static zza<String> arU = zza.zzav("measurement.config.url_authority", "app-measurement.com");
  public static zza<Integer> arV = zza.zzab("measurement.upload.max_bundles", 100);
  public static zza<Integer> arW = zza.zzab("measurement.upload.max_batch_size", 65536);
  public static zza<Integer> arX = zza.zzab("measurement.upload.max_bundle_size", 65536);
  public static zza<Integer> arY = zza.zzab("measurement.upload.max_events_per_bundle", 1000);
  public static zza<Integer> arZ = zza.zzab("measurement.upload.max_events_per_day", 100000);
  public static zza<Integer> asa = zza.zzab("measurement.upload.max_error_events_per_day", 1000);
  public static zza<Integer> asb = zza.zzab("measurement.upload.max_public_events_per_day", 50000);
  public static zza<Integer> asc = zza.zzab("measurement.upload.max_conversions_per_day", 500);
  public static zza<Integer> asd = zza.zzab("measurement.upload.max_realtime_events_per_day", 10);
  public static zza<Integer> ase = zza.zzab("measurement.store.max_stored_events_per_app", 100000);
  public static zza<String> asf = zza.zzav("measurement.upload.url", "https://app-measurement.com/a");
  public static zza<Long> asg = zza.zzf("measurement.upload.backoff_period", 43200000L);
  public static zza<Long> ash = zza.zzf("measurement.upload.window_interval", 3600000L);
  public static zza<Long> asi = zza.zzf("measurement.upload.interval", 3600000L);
  public static zza<Long> asj = zza.zzf("measurement.upload.realtime_upload_interval", 10000L);
  public static zza<Long> ask = zza.zzf("measurement.upload.minimum_delay", 500L);
  public static zza<Long> asl = zza.zzf("measurement.alarm_manager.minimum_interval", 60000L);
  public static zza<Long> asm = zza.zzf("measurement.upload.stale_data_deletion_interval", 86400000L);
  public static zza<Long> asn = zza.zzf("measurement.upload.refresh_blacklisted_config_interval", 604800000L);
  public static zza<Long> aso = zza.zzf("measurement.upload.initial_upload_delay_time", 15000L);
  public static zza<Long> asp = zza.zzf("measurement.upload.retry_time", 1800000L);
  public static zza<Integer> asq = zza.zzab("measurement.upload.retry_count", 6);
  public static zza<Long> asr = zza.zzf("measurement.upload.max_queue_time", 2419200000L);
  public static zza<Integer> ass = zza.zzab("measurement.lifetimevalue.max_currency_tracked", 4);
  public static zza<Integer> ast = zza.zzab("measurement.audience.filter_result_max_count", 200);
  public static zza<Long> asu = zza.zzf("measurement.service_client.idle_disconnect_millis", 5000L);
  
  public static final class zza<V>
  {
    private final V fc;
    private final zzsi<V> fd;
    private final String zzbcn;
    
    private zza(String paramString, zzsi<V> paramzzsi, V paramV)
    {
      zzaa.zzy(paramzzsi);
      this.fd = paramzzsi;
      this.fc = paramV;
      this.zzbcn = paramString;
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
      return new zza(paramString, zzsi.zza(paramString, Long.valueOf(paramLong2)), Long.valueOf(paramLong1));
    }
    
    static zza<Boolean> zzb(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      return new zza(paramString, zzsi.zzk(paramString, paramBoolean2), Boolean.valueOf(paramBoolean1));
    }
    
    static zza<Long> zzf(String paramString, long paramLong)
    {
      return zzb(paramString, paramLong, paramLong);
    }
    
    static zza<String> zzk(String paramString1, String paramString2, String paramString3)
    {
      return new zza(paramString1, zzsi.zzaa(paramString1, paramString3), paramString2);
    }
    
    static zza<Boolean> zzm(String paramString, boolean paramBoolean)
    {
      return zzb(paramString, paramBoolean, paramBoolean);
    }
    
    static zza<Integer> zzo(String paramString, int paramInt1, int paramInt2)
    {
      return new zza(paramString, zzsi.zza(paramString, Integer.valueOf(paramInt2)), Integer.valueOf(paramInt1));
    }
    
    public V get()
    {
      return (V)this.fc;
    }
    
    public V get(V paramV)
    {
      if (paramV != null) {
        return paramV;
      }
      return (V)this.fc;
    }
    
    public String getKey()
    {
      return this.zzbcn;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/measurement/internal/zzl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */