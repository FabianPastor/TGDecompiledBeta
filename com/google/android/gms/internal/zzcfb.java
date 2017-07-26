package com.google.android.gms.internal;

import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public final class zzcfb {
    private static zzcfc<Boolean> zzbpO = zzcfc.zzb("measurement.service_enabled", true, true);
    private static zzcfc<Boolean> zzbpP = zzcfc.zzb("measurement.service_client_enabled", true, true);
    private static zzcfc<Boolean> zzbpQ = zzcfc.zzb("measurement.log_third_party_store_events_enabled", false, false);
    private static zzcfc<Boolean> zzbpR = zzcfc.zzb("measurement.log_installs_enabled", false, false);
    private static zzcfc<Boolean> zzbpS = zzcfc.zzb("measurement.log_upgrades_enabled", false, false);
    private static zzcfc<Boolean> zzbpT = zzcfc.zzb("measurement.log_androidId_enabled", false, false);
    public static zzcfc<Boolean> zzbpU = zzcfc.zzb("measurement.upload_dsid_enabled", false, false);
    public static zzcfc<String> zzbpV = zzcfc.zzj("measurement.log_tag", "FA", "FA-SVC");
    public static zzcfc<Long> zzbpW = zzcfc.zzb("measurement.ad_id_cache_time", 10000, 10000);
    public static zzcfc<Long> zzbpX = zzcfc.zzb("measurement.monitoring.sample_period_millis", 86400000, 86400000);
    public static zzcfc<Long> zzbpY = zzcfc.zzb("measurement.config.cache_time", 86400000, 3600000);
    public static zzcfc<String> zzbpZ;
    public static zzcfc<Integer> zzbqA = zzcfc.zzm("measurement.audience.filter_result_max_count", Callback.DEFAULT_DRAG_ANIMATION_DURATION, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    public static zzcfc<Long> zzbqB = zzcfc.zzb("measurement.service_client.idle_disconnect_millis", (long) DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS, (long) DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    public static zzcfc<String> zzbqa;
    public static zzcfc<Integer> zzbqb = zzcfc.zzm("measurement.upload.max_bundles", 100, 100);
    public static zzcfc<Integer> zzbqc = zzcfc.zzm("measurement.upload.max_batch_size", 65536, 65536);
    public static zzcfc<Integer> zzbqd = zzcfc.zzm("measurement.upload.max_bundle_size", 65536, 65536);
    public static zzcfc<Integer> zzbqe = zzcfc.zzm("measurement.upload.max_events_per_bundle", 1000, 1000);
    public static zzcfc<Integer> zzbqf = zzcfc.zzm("measurement.upload.max_events_per_day", DefaultOggSeeker.MATCH_BYTE_RANGE, DefaultOggSeeker.MATCH_BYTE_RANGE);
    public static zzcfc<Integer> zzbqg = zzcfc.zzm("measurement.upload.max_error_events_per_day", 1000, 1000);
    public static zzcfc<Integer> zzbqh = zzcfc.zzm("measurement.upload.max_public_events_per_day", 50000, 50000);
    public static zzcfc<Integer> zzbqi = zzcfc.zzm("measurement.upload.max_conversions_per_day", 500, 500);
    public static zzcfc<Integer> zzbqj = zzcfc.zzm("measurement.upload.max_realtime_events_per_day", 10, 10);
    public static zzcfc<Integer> zzbqk = zzcfc.zzm("measurement.store.max_stored_events_per_app", DefaultOggSeeker.MATCH_BYTE_RANGE, DefaultOggSeeker.MATCH_BYTE_RANGE);
    public static zzcfc<String> zzbql;
    public static zzcfc<Long> zzbqm = zzcfc.zzb("measurement.upload.backoff_period", 43200000, 43200000);
    public static zzcfc<Long> zzbqn = zzcfc.zzb("measurement.upload.window_interval", 3600000, 3600000);
    public static zzcfc<Long> zzbqo = zzcfc.zzb("measurement.upload.interval", 3600000, 3600000);
    public static zzcfc<Long> zzbqp = zzcfc.zzb("measurement.upload.realtime_upload_interval", 10000, 10000);
    public static zzcfc<Long> zzbqq = zzcfc.zzb("measurement.upload.debug_upload_interval", 1000, 1000);
    public static zzcfc<Long> zzbqr = zzcfc.zzb("measurement.upload.minimum_delay", 500, 500);
    public static zzcfc<Long> zzbqs = zzcfc.zzb("measurement.alarm_manager.minimum_interval", (long) ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS, (long) ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
    public static zzcfc<Long> zzbqt = zzcfc.zzb("measurement.upload.stale_data_deletion_interval", 86400000, 86400000);
    public static zzcfc<Long> zzbqu = zzcfc.zzb("measurement.upload.refresh_blacklisted_config_interval", 604800000, 604800000);
    public static zzcfc<Long> zzbqv = zzcfc.zzb("measurement.upload.initial_upload_delay_time", 15000, 15000);
    public static zzcfc<Long> zzbqw = zzcfc.zzb("measurement.upload.retry_time", 1800000, 1800000);
    public static zzcfc<Integer> zzbqx = zzcfc.zzm("measurement.upload.retry_count", 6, 6);
    public static zzcfc<Long> zzbqy = zzcfc.zzb("measurement.upload.max_queue_time", 2419200000L, 2419200000L);
    public static zzcfc<Integer> zzbqz = zzcfc.zzm("measurement.lifetimevalue.max_currency_tracked", 4, 4);

    static {
        String str = "https";
        zzbpZ = zzcfc.zzj("measurement.config.url_scheme", str, str);
        str = "app-measurement.com";
        zzbqa = zzcfc.zzj("measurement.config.url_authority", str, str);
        str = "https://app-measurement.com/a";
        zzbql = zzcfc.zzj("measurement.upload.url", str, str);
    }
}
