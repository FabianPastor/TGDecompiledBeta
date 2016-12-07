package org.telegram.messenger.exoplayer2;

import android.content.Context;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.trackselection.TrackSelector;

public final class ExoPlayerFactory {
    public static final long DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS = 5000;

    private ExoPlayerFactory() {
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, TrackSelector<?> trackSelector, LoadControl loadControl) {
        return newSimpleInstance(context, trackSelector, loadControl, null);
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, TrackSelector<?> trackSelector, LoadControl loadControl, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        return newSimpleInstance(context, trackSelector, loadControl, drmSessionManager, false);
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, TrackSelector<?> trackSelector, LoadControl loadControl, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean preferExtensionDecoders) {
        return newSimpleInstance(context, trackSelector, loadControl, drmSessionManager, preferExtensionDecoders, DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public static SimpleExoPlayer newSimpleInstance(Context context, TrackSelector<?> trackSelector, LoadControl loadControl, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean preferExtensionDecoders, long allowedVideoJoiningTimeMs) {
        return new SimpleExoPlayer(context, trackSelector, loadControl, drmSessionManager, preferExtensionDecoders, allowedVideoJoiningTimeMs);
    }

    public static ExoPlayer newInstance(Renderer[] renderers, TrackSelector<?> trackSelector) {
        return newInstance(renderers, trackSelector, new DefaultLoadControl());
    }

    public static ExoPlayer newInstance(Renderer[] renderers, TrackSelector<?> trackSelector, LoadControl loadControl) {
        return new ExoPlayerImpl(renderers, trackSelector, loadControl);
    }
}
