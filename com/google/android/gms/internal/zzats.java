package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;

public final class zzats
{
  public static zza<Boolean> zzbrJ = zza.zzl("measurement.service_enabled", true);
  public static zza<Boolean> zzbrK = zza.zzl("measurement.service_client_enabled", true);
  public static zza<Boolean> zzbrL = zza.zzl("measurement.log_third_party_store_events_enabled", false);
  public static zza<Boolean> zzbrM = zza.zzl("measurement.log_installs_enabled", false);
  public static zza<Boolean> zzbrN = zza.zzl("measurement.log_upgrades_enabled", false);
  public static zza<Boolean> zzbrO = zza.zzl("measurement.log_androidId_enabled", false);
  public static zza<String> zzbrP = zza.zzm("measurement.log_tag", "FA", "FA-SVC");
  public static zza<Long> zzbrQ = zza.zzj("measurement.ad_id_cache_time", 10000L);
  public static zza<Long> zzbrR = zza.zzj("measurement.monitoring.sample_period_millis", 86400000L);
  public static zza<Long> zzbrS = zza.zzb("measurement.config.cache_time", 86400000L, 3600000L);
  public static zza<String> zzbrT = zza.zzY("measurement.config.url_scheme", "https");
  public static zza<String> zzbrU = zza.zzY("measurement.config.url_authority", "app-measurement.com");
  public static zza<Integer> zzbrV = zza.zzB("measurement.upload.max_bundles", 100);
  public static zza<Integer> zzbrW = zza.zzB("measurement.upload.max_batch_size", 65536);
  public static zza<Integer> zzbrX = zza.zzB("measurement.upload.max_bundle_size", 65536);
  public static zza<Integer> zzbrY = zza.zzB("measurement.upload.max_events_per_bundle", 1000);
  public static zza<Integer> zzbrZ = zza.zzB("measurement.upload.max_events_per_day", 100000);
  public static zza<Integer> zzbsa = zza.zzB("measurement.upload.max_error_events_per_day", 1000);
  public static zza<Integer> zzbsb = zza.zzB("measurement.upload.max_public_events_per_day", 50000);
  public static zza<Integer> zzbsc = zza.zzB("measurement.upload.max_conversions_per_day", 500);
  public static zza<Integer> zzbsd = zza.zzB("measurement.upload.max_realtime_events_per_day", 10);
  public static zza<Integer> zzbse = zza.zzB("measurement.store.max_stored_events_per_app", 100000);
  public static zza<String> zzbsf = zza.zzY("measurement.upload.url", "https://app-measurement.com/a");
  public static zza<Long> zzbsg = zza.zzj("measurement.upload.backoff_period", 43200000L);
  public static zza<Long> zzbsh = zza.zzj("measurement.upload.window_interval", 3600000L);
  public static zza<Long> zzbsi = zza.zzj("measurement.upload.interval", 3600000L);
  public static zza<Long> zzbsj = zza.zzj("measurement.upload.realtime_upload_interval", 10000L);
  public static zza<Long> zzbsk = zza.zzj("measurement.upload.debug_upload_interval", 1000L);
  public static zza<Long> zzbsl = zza.zzj("measurement.upload.minimum_delay", 500L);
  public static zza<Long> zzbsm = zza.zzj("measurement.alarm_manager.minimum_interval", 60000L);
  public static zza<Long> zzbsn = zza.zzj("measurement.upload.stale_data_deletion_interval", 86400000L);
  public static zza<Long> zzbso = zza.zzj("measurement.upload.refresh_blacklisted_config_interval", 604800000L);
  public static zza<Long> zzbsp = zza.zzj("measurement.upload.initial_upload_delay_time", 15000L);
  public static zza<Long> zzbsq = zza.zzj("measurement.upload.retry_time", 1800000L);
  public static zza<Integer> zzbsr = zza.zzB("measurement.upload.retry_count", 6);
  public static zza<Long> zzbss = zza.zzj("measurement.upload.max_queue_time", 2419200000L);
  public static zza<Integer> zzbst = zza.zzB("measurement.lifetimevalue.max_currency_tracked", 4);
  public static zza<Integer> zzbsu = zza.zzB("measurement.audience.filter_result_max_count", 200);
  public static zza<Long> zzbsv = zza.zzj("measurement.service_client.idle_disconnect_millis", 5000L);
  
  public static final class zza<V>
  {
    private final String zzAX;
    private final V zzaga;
    private final zzaca<V> zzagb;
    
    private zza(String paramString, zzaca<V> paramzzaca, V paramV)
    {
      zzac.zzw(paramzzaca);
      this.zzagb = paramzzaca;
      this.zzaga = paramV;
      this.zzAX = paramString;
    }
    
    static zza<Integer> zzB(String paramString, int paramInt)
    {
      return zzo(paramString, paramInt, paramInt);
    }
    
    static zza<String> zzY(String paramString1, String paramString2)
    {
      return zzm(paramString1, paramString2, paramString2);
    }
    
    static zza<Long> zzb(String paramString, long paramLong1, long paramLong2)
    {
      return new zza(paramString, zzaca.zza(paramString, Long.valueOf(paramLong2)), Long.valueOf(paramLong1));
    }
    
    static zza<Boolean> zzb(String paramString, boolean paramBoolean1, boolean paramBoolean2)
    {
      return new zza(paramString, zzaca.zzj(paramString, paramBoolean2), Boolean.valueOf(paramBoolean1));
    }
    
    static zza<Long> zzj(String paramString, long paramLong)
    {
      return zzb(paramString, paramLong, paramLong);
    }
    
    static zza<Boolean> zzl(String paramString, boolean paramBoolean)
    {
      return zzb(paramString, paramBoolean, paramBoolean);
    }
    
    static zza<String> zzm(String paramString1, String paramString2, String paramString3)
    {
      return new zza(paramString1, zzaca.zzB(paramString1, paramString3), paramString2);
    }
    
    static zza<Integer> zzo(String paramString, int paramInt1, int paramInt2)
    {
      return new zza(paramString, zzaca.zza(paramString, Integer.valueOf(paramInt2)), Integer.valueOf(paramInt1));
    }
    
    public V get()
    {
      return (V)this.zzaga;
    }
    
    public V get(V paramV)
    {
      if (paramV != null) {
        return paramV;
      }
      return (V)this.zzaga;
    }
    
    public String getKey()
    {
      return this.zzAX;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/google/android/gms/internal/zzats.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */