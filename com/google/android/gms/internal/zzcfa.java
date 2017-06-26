package com.google.android.gms.internal;

import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public final class zzcfa {
    private static zzcfb<Boolean> zzbpO = zzcfb.zzb("measurement.service_enabled", true, true);
    private static zzcfb<Boolean> zzbpP = zzcfb.zzb("measurement.service_client_enabled", true, true);
    private static zzcfb<Boolean> zzbpQ = zzcfb.zzb("measurement.log_third_party_store_events_enabled", false, false);
    private static zzcfb<Boolean> zzbpR = zzcfb.zzb("measurement.log_installs_enabled", false, false);
    private static zzcfb<Boolean> zzbpS = zzcfb.zzb("measurement.log_upgrades_enabled", false, false);
    private static zzcfb<Boolean> zzbpT = zzcfb.zzb("measurement.log_androidId_enabled", false, false);
    public static zzcfb<Boolean> zzbpU = zzcfb.zzb("measurement.upload_dsid_enabled", false, false);
    public static zzcfb<String> zzbpV = zzcfb.zzj("measurement.log_tag", "FA", "FA-SVC");
    public static zzcfb<Long> zzbpW = zzcfb.zzb("measurement.ad_id_cache_time", 10000, 10000);
    public static zzcfb<Long> zzbpX = zzcfb.zzb("measurement.monitoring.sample_period_millis", 86400000, 86400000);
    public static zzcfb<Long> zzbpY = zzcfb.zzb("measurement.config.cache_time", 86400000, 3600000);
    public static zzcfb<String> zzbpZ;
    public static zzcfb<Integer> zzbqA = zzcfb.zzm("measurement.audience.filter_result_max_count", Callback.DEFAULT_DRAG_ANIMATION_DURATION, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    public static zzcfb<Long> zzbqB = zzcfb.zzb("measurement.service_client.idle_disconnect_millis", (long) DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS, (long) DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    public static zzcfb<String> zzbqa;
    public static zzcfb<Integer> zzbqb = zzcfb.zzm("measurement.upload.max_bundles", 100, 100);
    public static zzcfb<Integer> zzbqc = zzcfb.zzm("measurement.upload.max_batch_size", 65536, 65536);
    public static zzcfb<Integer> zzbqd = zzcfb.zzm("measurement.upload.max_bundle_size", 65536, 65536);
    public static zzcfb<Integer> zzbqe = zzcfb.zzm("measurement.upload.max_events_per_bundle", 1000, 1000);
    public static zzcfb<Integer> zzbqf = zzcfb.zzm("measurement.upload.max_events_per_day", DefaultOggSeeker.MATCH_BYTE_RANGE, DefaultOggSeeker.MATCH_BYTE_RANGE);
    public static zzcfb<Integer> zzbqg = zzcfb.zzm("measurement.upload.max_error_events_per_day", 1000, 1000);
    public static zzcfb<Integer> zzbqh = zzcfb.zzm("measurement.upload.max_public_events_per_day", 50000, 50000);
    public static zzcfb<Integer> zzbqi = zzcfb.zzm("measurement.upload.max_conversions_per_day", 500, 500);
    public static zzcfb<Integer> zzbqj = zzcfb.zzm("measurement.upload.max_realtime_events_per_day", 10, 10);
    public static zzcfb<Integer> zzbqk = zzcfb.zzm("measurement.store.max_stored_events_per_app", DefaultOggSeeker.MATCH_BYTE_RANGE, DefaultOggSeeker.MATCH_BYTE_RANGE);
    public static zzcfb<String> zzbql;
    public static zzcfb<Long> zzbqm = zzcfb.zzb("measurement.upload.backoff_period", 43200000, 43200000);
    public static zzcfb<Long> zzbqn = zzcfb.zzb("measurement.upload.window_interval", 3600000, 3600000);
    public static zzcfb<Long> zzbqo = zzcfb.zzb("measurement.upload.interval", 3600000, 3600000);
    public static zzcfb<Long> zzbqp = zzcfb.zzb("measurement.upload.realtime_upload_interval", 10000, 10000);
    public static zzcfb<Long> zzbqq = zzcfb.zzb("measurement.upload.debug_upload_interval", 1000, 1000);
    public static zzcfb<Long> zzbqr = zzcfb.zzb("measurement.upload.minimum_delay", 500, 500);
    public static zzcfb<Long> zzbqs = zzcfb.zzb("measurement.alarm_manager.minimum_interval", (long) ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS, (long) ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
    public static zzcfb<Long> zzbqt = zzcfb.zzb("measurement.upload.stale_data_deletion_interval", 86400000, 86400000);
    public static zzcfb<Long> zzbqu = zzcfb.zzb("measurement.upload.refresh_blacklisted_config_interval", 604800000, 604800000);
    public static zzcfb<Long> zzbqv = zzcfb.zzb("measurement.upload.initial_upload_delay_time", 15000, 15000);
    public static zzcfb<Long> zzbqw = zzcfb.zzb("measurement.upload.retry_time", 1800000, 1800000);
    public static zzcfb<Integer> zzbqx = zzcfb.zzm("measurement.upload.retry_count", 6, 6);
    public static zzcfb<Long> zzbqy = zzcfb.zzb("measurement.upload.max_queue_time", 2419200000L, 2419200000L);
    public static zzcfb<Integer> zzbqz = zzcfb.zzm("measurement.lifetimevalue.max_currency_tracked", 4, 4);

    static {
        String str = "https";
        zzbpZ = zzcfb.zzj("measurement.config.url_scheme", str, str);
        str = "app-measurement.com";
        zzbqa = zzcfb.zzj("measurement.config.url_authority", str, str);
        str = "https://app-measurement.com/a";
        zzbql = zzcfb.zzj("measurement.upload.url", str, str);
    }
}
