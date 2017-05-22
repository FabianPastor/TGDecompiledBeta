package com.google.android.gms.internal;

import com.google.android.gms.common.internal.zzac;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public final class zzats {
    public static zza<Boolean> zzbrJ = zza.zzl("measurement.service_enabled", true);
    public static zza<Boolean> zzbrK = zza.zzl("measurement.service_client_enabled", true);
    public static zza<Boolean> zzbrL = zza.zzl("measurement.log_third_party_store_events_enabled", false);
    public static zza<Boolean> zzbrM = zza.zzl("measurement.log_installs_enabled", false);
    public static zza<Boolean> zzbrN = zza.zzl("measurement.log_upgrades_enabled", false);
    public static zza<Boolean> zzbrO = zza.zzl("measurement.log_androidId_enabled", false);
    public static zza<String> zzbrP = zza.zzm("measurement.log_tag", "FA", "FA-SVC");
    public static zza<Long> zzbrQ = zza.zzj("measurement.ad_id_cache_time", 10000);
    public static zza<Long> zzbrR = zza.zzj("measurement.monitoring.sample_period_millis", 86400000);
    public static zza<Long> zzbrS = zza.zzb("measurement.config.cache_time", 86400000, 3600000);
    public static zza<String> zzbrT = zza.zzY("measurement.config.url_scheme", "https");
    public static zza<String> zzbrU = zza.zzY("measurement.config.url_authority", "app-measurement.com");
    public static zza<Integer> zzbrV = zza.zzB("measurement.upload.max_bundles", 100);
    public static zza<Integer> zzbrW = zza.zzB("measurement.upload.max_batch_size", 65536);
    public static zza<Integer> zzbrX = zza.zzB("measurement.upload.max_bundle_size", 65536);
    public static zza<Integer> zzbrY = zza.zzB("measurement.upload.max_events_per_bundle", 1000);
    public static zza<Integer> zzbrZ = zza.zzB("measurement.upload.max_events_per_day", DefaultOggSeeker.MATCH_BYTE_RANGE);
    public static zza<Integer> zzbsa = zza.zzB("measurement.upload.max_error_events_per_day", 1000);
    public static zza<Integer> zzbsb = zza.zzB("measurement.upload.max_public_events_per_day", 50000);
    public static zza<Integer> zzbsc = zza.zzB("measurement.upload.max_conversions_per_day", 500);
    public static zza<Integer> zzbsd = zza.zzB("measurement.upload.max_realtime_events_per_day", 10);
    public static zza<Integer> zzbse = zza.zzB("measurement.store.max_stored_events_per_app", DefaultOggSeeker.MATCH_BYTE_RANGE);
    public static zza<String> zzbsf = zza.zzY("measurement.upload.url", "https://app-measurement.com/a");
    public static zza<Long> zzbsg = zza.zzj("measurement.upload.backoff_period", 43200000);
    public static zza<Long> zzbsh = zza.zzj("measurement.upload.window_interval", 3600000);
    public static zza<Long> zzbsi = zza.zzj("measurement.upload.interval", 3600000);
    public static zza<Long> zzbsj = zza.zzj("measurement.upload.realtime_upload_interval", 10000);
    public static zza<Long> zzbsk = zza.zzj("measurement.upload.debug_upload_interval", 1000);
    public static zza<Long> zzbsl = zza.zzj("measurement.upload.minimum_delay", 500);
    public static zza<Long> zzbsm = zza.zzj("measurement.alarm_manager.minimum_interval", ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
    public static zza<Long> zzbsn = zza.zzj("measurement.upload.stale_data_deletion_interval", 86400000);
    public static zza<Long> zzbso = zza.zzj("measurement.upload.refresh_blacklisted_config_interval", 604800000);
    public static zza<Long> zzbsp = zza.zzj("measurement.upload.initial_upload_delay_time", 15000);
    public static zza<Long> zzbsq = zza.zzj("measurement.upload.retry_time", 1800000);
    public static zza<Integer> zzbsr = zza.zzB("measurement.upload.retry_count", 6);
    public static zza<Long> zzbss = zza.zzj("measurement.upload.max_queue_time", 2419200000L);
    public static zza<Integer> zzbst = zza.zzB("measurement.lifetimevalue.max_currency_tracked", 4);
    public static zza<Integer> zzbsu = zza.zzB("measurement.audience.filter_result_max_count", Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    public static zza<Long> zzbsv = zza.zzj("measurement.service_client.idle_disconnect_millis", DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);

    public static final class zza<V> {
        private final String zzAX;
        private final V zzaga;
        private final zzaca<V> zzagb;

        private zza(String str, zzaca<V> com_google_android_gms_internal_zzaca_V, V v) {
            zzac.zzw(com_google_android_gms_internal_zzaca_V);
            this.zzagb = com_google_android_gms_internal_zzaca_V;
            this.zzaga = v;
            this.zzAX = str;
        }

        static zza<Integer> zzB(String str, int i) {
            return zzo(str, i, i);
        }

        static zza<String> zzY(String str, String str2) {
            return zzm(str, str2, str2);
        }

        static zza<Long> zzb(String str, long j, long j2) {
            return new zza(str, zzaca.zza(str, Long.valueOf(j2)), Long.valueOf(j));
        }

        static zza<Boolean> zzb(String str, boolean z, boolean z2) {
            return new zza(str, zzaca.zzj(str, z2), Boolean.valueOf(z));
        }

        static zza<Long> zzj(String str, long j) {
            return zzb(str, j, j);
        }

        static zza<Boolean> zzl(String str, boolean z) {
            return zzb(str, z, z);
        }

        static zza<String> zzm(String str, String str2, String str3) {
            return new zza(str, zzaca.zzB(str, str3), str2);
        }

        static zza<Integer> zzo(String str, int i, int i2) {
            return new zza(str, zzaca.zza(str, Integer.valueOf(i2)), Integer.valueOf(i));
        }

        public V get() {
            return this.zzaga;
        }

        public V get(V v) {
            return v != null ? v : this.zzaga;
        }

        public String getKey() {
            return this.zzAX;
        }
    }
}
