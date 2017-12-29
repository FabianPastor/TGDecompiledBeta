package com.google.android.gms.internal;

import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.DefaultRenderersFactory;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkedTrackBlacklistUtil;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

public final class zzchc {
    private static zzchd<Boolean> zzizv = zzchd.zzb("measurement.service_enabled", true, true);
    private static zzchd<Boolean> zzizw = zzchd.zzb("measurement.service_client_enabled", true, true);
    private static zzchd<Boolean> zzizx = zzchd.zzb("measurement.log_third_party_store_events_enabled", false, false);
    private static zzchd<Boolean> zzizy = zzchd.zzb("measurement.log_installs_enabled", false, false);
    private static zzchd<Boolean> zzizz = zzchd.zzb("measurement.log_upgrades_enabled", false, false);
    private static zzchd<Boolean> zzjaa = zzchd.zzb("measurement.log_androidId_enabled", false, false);
    public static zzchd<Boolean> zzjab = zzchd.zzb("measurement.upload_dsid_enabled", false, false);
    public static zzchd<Boolean> zzjac = zzchd.zzb("measurement.event_sampling_enabled", false, false);
    public static zzchd<String> zzjad = zzchd.zzi("measurement.log_tag", "FA", "FA-SVC");
    public static zzchd<Long> zzjae = zzchd.zzb("measurement.ad_id_cache_time", 10000, 10000);
    public static zzchd<Long> zzjaf = zzchd.zzb("measurement.monitoring.sample_period_millis", 86400000, 86400000);
    public static zzchd<Long> zzjag = zzchd.zzb("measurement.config.cache_time", 86400000, 3600000);
    public static zzchd<String> zzjah;
    public static zzchd<String> zzjai;
    public static zzchd<Integer> zzjaj = zzchd.zzm("measurement.upload.max_bundles", 100, 100);
    public static zzchd<Integer> zzjak = zzchd.zzm("measurement.upload.max_batch_size", C.DEFAULT_BUFFER_SEGMENT_SIZE, C.DEFAULT_BUFFER_SEGMENT_SIZE);
    public static zzchd<Integer> zzjal = zzchd.zzm("measurement.upload.max_bundle_size", C.DEFAULT_BUFFER_SEGMENT_SIZE, C.DEFAULT_BUFFER_SEGMENT_SIZE);
    public static zzchd<Integer> zzjam = zzchd.zzm("measurement.upload.max_events_per_bundle", 1000, 1000);
    public static zzchd<Integer> zzjan = zzchd.zzm("measurement.upload.max_events_per_day", DefaultOggSeeker.MATCH_BYTE_RANGE, DefaultOggSeeker.MATCH_BYTE_RANGE);
    public static zzchd<Integer> zzjao = zzchd.zzm("measurement.upload.max_error_events_per_day", 1000, 1000);
    public static zzchd<Integer> zzjap = zzchd.zzm("measurement.upload.max_public_events_per_day", 50000, 50000);
    public static zzchd<Integer> zzjaq = zzchd.zzm("measurement.upload.max_conversions_per_day", 500, 500);
    public static zzchd<Integer> zzjar = zzchd.zzm("measurement.upload.max_realtime_events_per_day", 10, 10);
    public static zzchd<Integer> zzjas = zzchd.zzm("measurement.store.max_stored_events_per_app", DefaultOggSeeker.MATCH_BYTE_RANGE, DefaultOggSeeker.MATCH_BYTE_RANGE);
    public static zzchd<String> zzjat;
    public static zzchd<Long> zzjau = zzchd.zzb("measurement.upload.backoff_period", 43200000, 43200000);
    public static zzchd<Long> zzjav = zzchd.zzb("measurement.upload.window_interval", 3600000, 3600000);
    public static zzchd<Long> zzjaw = zzchd.zzb("measurement.upload.interval", 3600000, 3600000);
    public static zzchd<Long> zzjax = zzchd.zzb("measurement.upload.realtime_upload_interval", 10000, 10000);
    public static zzchd<Long> zzjay = zzchd.zzb("measurement.upload.debug_upload_interval", 1000, 1000);
    public static zzchd<Long> zzjaz = zzchd.zzb("measurement.upload.minimum_delay", 500, 500);
    public static zzchd<Long> zzjba = zzchd.zzb("measurement.alarm_manager.minimum_interval", (long) ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS, (long) ChunkedTrackBlacklistUtil.DEFAULT_TRACK_BLACKLIST_MS);
    public static zzchd<Long> zzjbb = zzchd.zzb("measurement.upload.stale_data_deletion_interval", 86400000, 86400000);
    public static zzchd<Long> zzjbc = zzchd.zzb("measurement.upload.refresh_blacklisted_config_interval", 604800000, 604800000);
    public static zzchd<Long> zzjbd = zzchd.zzb("measurement.upload.initial_upload_delay_time", 15000, 15000);
    public static zzchd<Long> zzjbe = zzchd.zzb("measurement.upload.retry_time", 1800000, 1800000);
    public static zzchd<Integer> zzjbf = zzchd.zzm("measurement.upload.retry_count", 6, 6);
    public static zzchd<Long> zzjbg = zzchd.zzb("measurement.upload.max_queue_time", 2419200000L, 2419200000L);
    public static zzchd<Integer> zzjbh = zzchd.zzm("measurement.lifetimevalue.max_currency_tracked", 4, 4);
    public static zzchd<Integer> zzjbi = zzchd.zzm("measurement.audience.filter_result_max_count", Callback.DEFAULT_DRAG_ANIMATION_DURATION, Callback.DEFAULT_DRAG_ANIMATION_DURATION);
    public static zzchd<Long> zzjbj = zzchd.zzb("measurement.service_client.idle_disconnect_millis", (long) DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS, (long) DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);

    static {
        String str = "https";
        zzjah = zzchd.zzi("measurement.config.url_scheme", str, str);
        str = "app-measurement.com";
        zzjai = zzchd.zzi("measurement.config.url_authority", str, str);
        str = "https://app-measurement.com/a";
        zzjat = zzchd.zzi("measurement.upload.url", str, str);
    }
}
