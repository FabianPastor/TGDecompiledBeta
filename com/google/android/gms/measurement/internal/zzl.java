package com.google.android.gms.measurement.internal;

import com.google.android.gms.common.internal.zzac;
import com.google.android.gms.internal.zzrs;
import org.telegram.messenger.exoplayer.hls.HlsChunkSource;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public final class zzl {
    public static zza<Boolean> aoC = zza.zzo("measurement.service_enabled", true);
    public static zza<Boolean> aoD = zza.zzo("measurement.service_client_enabled", true);
    public static zza<String> aoE = zza.zzk("measurement.log_tag", "FA", "FA-SVC");
    public static zza<Long> aoF = zza.zzh("measurement.ad_id_cache_time", 10000);
    public static zza<Long> aoG = zza.zzh("measurement.monitoring.sample_period_millis", 86400000);
    public static zza<Long> aoH = zza.zzb("measurement.config.cache_time", 86400000, 3600000);
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
    public static zza<Long> aoV = zza.zzh("measurement.upload.backoff_period", 43200000);
    public static zza<Long> aoW = zza.zzh("measurement.upload.window_interval", 3600000);
    public static zza<Long> aoX = zza.zzh("measurement.upload.interval", 3600000);
    public static zza<Long> aoY = zza.zzh("measurement.upload.realtime_upload_interval", 10000);
    public static zza<Long> aoZ = zza.zzh("measurement.upload.minimum_delay", 500);
    public static zza<Long> apa = zza.zzh("measurement.alarm_manager.minimum_interval", HlsChunkSource.DEFAULT_PLAYLIST_BLACKLIST_MS);
    public static zza<Long> apb = zza.zzh("measurement.upload.stale_data_deletion_interval", 86400000);
    public static zza<Long> apc = zza.zzh("measurement.upload.initial_upload_delay_time", 15000);
    public static zza<Long> apd = zza.zzh("measurement.upload.retry_time", 1800000);
    public static zza<Integer> ape = zza.zzab("measurement.upload.retry_count", 6);
    public static zza<Long> apf = zza.zzh("measurement.upload.max_queue_time", 2419200000L);
    public static zza<Integer> apg = zza.zzab("measurement.lifetimevalue.max_currency_tracked", 4);
    public static zza<Integer> aph = zza.zzab("measurement.audience.filter_result_max_count", Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    public static zza<Long> api = zza.zzh("measurement.service_client.idle_disconnect_millis", HlsChunkSource.DEFAULT_MIN_BUFFER_TO_SWITCH_UP_MS);

    public static final class zza<V> {
        private final V cV;
        private final zzrs<V> cW;
        private final String zzbaf;

        private zza(String str, zzrs<V> com_google_android_gms_internal_zzrs_V, V v) {
            zzac.zzy(com_google_android_gms_internal_zzrs_V);
            this.cW = com_google_android_gms_internal_zzrs_V;
            this.cV = v;
            this.zzbaf = str;
        }

        static zza<Integer> zzab(String str, int i) {
            return zzo(str, i, i);
        }

        static zza<String> zzav(String str, String str2) {
            return zzk(str, str2, str2);
        }

        static zza<Long> zzb(String str, long j, long j2) {
            return new zza(str, zzrs.zza(str, Long.valueOf(j2)), Long.valueOf(j));
        }

        static zza<Boolean> zzb(String str, boolean z, boolean z2) {
            return new zza(str, zzrs.zzm(str, z2), Boolean.valueOf(z));
        }

        static zza<Long> zzh(String str, long j) {
            return zzb(str, j, j);
        }

        static zza<String> zzk(String str, String str2, String str3) {
            return new zza(str, zzrs.zzab(str, str3), str2);
        }

        static zza<Integer> zzo(String str, int i, int i2) {
            return new zza(str, zzrs.zza(str, Integer.valueOf(i2)), Integer.valueOf(i));
        }

        static zza<Boolean> zzo(String str, boolean z) {
            return zzb(str, z, z);
        }

        public V get() {
            return this.cV;
        }

        public V get(V v) {
            return v != null ? v : this.cV;
        }

        public String getKey() {
            return this.zzbaf;
        }
    }
}
