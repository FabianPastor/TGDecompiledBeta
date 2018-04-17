package org.telegram.messenger.exoplayer2;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import org.telegram.messenger.exoplayer2.audio.AudioCapabilities;
import org.telegram.messenger.exoplayer2.audio.AudioProcessor;
import org.telegram.messenger.exoplayer2.audio.AudioRendererEventListener;
import org.telegram.messenger.exoplayer2.audio.MediaCodecAudioRenderer;
import org.telegram.messenger.exoplayer2.drm.DrmSessionManager;
import org.telegram.messenger.exoplayer2.drm.FrameworkMediaCrypto;
import org.telegram.messenger.exoplayer2.mediacodec.MediaCodecSelector;
import org.telegram.messenger.exoplayer2.metadata.MetadataOutput;
import org.telegram.messenger.exoplayer2.metadata.MetadataRenderer;
import org.telegram.messenger.exoplayer2.text.TextOutput;
import org.telegram.messenger.exoplayer2.text.TextRenderer;
import org.telegram.messenger.exoplayer2.video.MediaCodecVideoRenderer;
import org.telegram.messenger.exoplayer2.video.VideoRendererEventListener;

public class DefaultRenderersFactory implements RenderersFactory {
    public static final long DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS = 5000;
    public static final int EXTENSION_RENDERER_MODE_OFF = 0;
    public static final int EXTENSION_RENDERER_MODE_ON = 1;
    public static final int EXTENSION_RENDERER_MODE_PREFER = 2;
    protected static final int MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY = 50;
    private static final String TAG = "DefaultRenderersFactory";
    private final long allowedVideoJoiningTimeMs;
    private final Context context;
    private final DrmSessionManager<FrameworkMediaCrypto> drmSessionManager;
    private final int extensionRendererMode;

    @Retention(RetentionPolicy.SOURCE)
    public @interface ExtensionRendererMode {
    }

    public DefaultRenderersFactory(Context context) {
        this(context, null);
    }

    public DefaultRenderersFactory(Context context, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
        this(context, drmSessionManager, 0);
    }

    public DefaultRenderersFactory(Context context, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode) {
        this(context, drmSessionManager, extensionRendererMode, DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
    }

    public DefaultRenderersFactory(Context context, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode, long allowedVideoJoiningTimeMs) {
        this.context = context;
        this.drmSessionManager = drmSessionManager;
        this.extensionRendererMode = extensionRendererMode;
        this.allowedVideoJoiningTimeMs = allowedVideoJoiningTimeMs;
    }

    public Renderer[] createRenderers(Handler eventHandler, VideoRendererEventListener videoRendererEventListener, AudioRendererEventListener audioRendererEventListener, TextOutput textRendererOutput, MetadataOutput metadataRendererOutput) {
        ArrayList<Renderer> renderersList = new ArrayList();
        buildVideoRenderers(this.context, this.drmSessionManager, this.allowedVideoJoiningTimeMs, eventHandler, videoRendererEventListener, this.extensionRendererMode, renderersList);
        buildAudioRenderers(this.context, this.drmSessionManager, buildAudioProcessors(), eventHandler, audioRendererEventListener, this.extensionRendererMode, renderersList);
        ArrayList<Renderer> arrayList = renderersList;
        buildTextRenderers(this.context, textRendererOutput, eventHandler.getLooper(), this.extensionRendererMode, arrayList);
        buildMetadataRenderers(this.context, metadataRendererOutput, eventHandler.getLooper(), this.extensionRendererMode, arrayList);
        buildMiscellaneousRenderers(this.context, eventHandler, this.extensionRendererMode, renderersList);
        return (Renderer[]) renderersList.toArray(new Renderer[renderersList.size()]);
    }

    protected void buildVideoRenderers(Context context, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, long allowedVideoJoiningTimeMs, Handler eventHandler, VideoRendererEventListener eventListener, int extensionRendererMode, ArrayList<Renderer> out) {
        Exception e;
        int i = extensionRendererMode;
        ArrayList<Renderer> arrayList = out;
        arrayList.add(new MediaCodecVideoRenderer(context, MediaCodecSelector.DEFAULT, allowedVideoJoiningTimeMs, drmSessionManager, false, eventHandler, eventListener, MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY));
        if (i != 0) {
            int extensionRendererIndex = out.size();
            if (i == 2) {
                extensionRendererIndex--;
            }
            int extensionRendererIndex2;
            try {
                extensionRendererIndex2 = extensionRendererIndex + 1;
                try {
                    arrayList.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.vp9.LibvpxVideoRenderer").getConstructor(new Class[]{Boolean.TYPE, Long.TYPE, Handler.class, VideoRendererEventListener.class, Integer.TYPE}).newInstance(new Object[]{Boolean.valueOf(true), Long.valueOf(allowedVideoJoiningTimeMs), eventHandler, eventListener, Integer.valueOf(MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY)}));
                    Log.i(TAG, "Loaded LibvpxVideoRenderer.");
                } catch (ClassNotFoundException e2) {
                } catch (Exception e3) {
                    e = e3;
                    throw new RuntimeException(e);
                }
            } catch (ClassNotFoundException e4) {
                extensionRendererIndex2 = extensionRendererIndex;
            } catch (Exception e5) {
                e = e5;
                extensionRendererIndex2 = extensionRendererIndex;
                throw new RuntimeException(e);
            }
        }
    }

    protected void buildAudioRenderers(Context context, DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, AudioProcessor[] audioProcessors, Handler eventHandler, AudioRendererEventListener eventListener, int extensionRendererMode, ArrayList<Renderer> out) {
        Exception e;
        int extensionRendererIndex;
        Exception e2;
        int extensionRendererIndex2;
        int i = extensionRendererMode;
        ArrayList<Renderer> arrayList = out;
        arrayList.add(new MediaCodecAudioRenderer(MediaCodecSelector.DEFAULT, drmSessionManager, true, eventHandler, eventListener, AudioCapabilities.getCapabilities(context), audioProcessors));
        if (i != 0) {
            int extensionRendererIndex3;
            int extensionRendererIndex4 = out.size();
            if (i == 2) {
                extensionRendererIndex4--;
            }
            try {
                extensionRendererIndex3 = extensionRendererIndex4 + 1;
                try {
                    arrayList.add(extensionRendererIndex4, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.opus.LibopusAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{eventHandler, eventListener, audioProcessors}));
                    Log.i(TAG, "Loaded LibopusAudioRenderer.");
                } catch (ClassNotFoundException e3) {
                } catch (Exception e4) {
                    e = e4;
                    throw new RuntimeException(e);
                }
            } catch (ClassNotFoundException e5) {
                extensionRendererIndex3 = extensionRendererIndex4;
            } catch (Exception e6) {
                e = e6;
                extensionRendererIndex3 = extensionRendererIndex4;
                throw new RuntimeException(e);
            }
            try {
                extensionRendererIndex = extensionRendererIndex3 + 1;
                try {
                    arrayList.add(extensionRendererIndex3, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.flac.LibflacAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{eventHandler, eventListener, audioProcessors}));
                    Log.i(TAG, "Loaded LibflacAudioRenderer.");
                } catch (ClassNotFoundException e7) {
                } catch (Exception e8) {
                    e2 = e8;
                    extensionRendererIndex3 = extensionRendererIndex;
                    throw new RuntimeException(e2);
                }
            } catch (ClassNotFoundException e9) {
                extensionRendererIndex = extensionRendererIndex3;
            } catch (Exception e82) {
                e2 = e82;
                throw new RuntimeException(e2);
            }
            try {
                extensionRendererIndex2 = extensionRendererIndex + 1;
                try {
                    arrayList.add(extensionRendererIndex, (Renderer) Class.forName("org.telegram.messenger.exoplayer2.ext.ffmpeg.FfmpegAudioRenderer").getConstructor(new Class[]{Handler.class, AudioRendererEventListener.class, AudioProcessor[].class}).newInstance(new Object[]{eventHandler, eventListener, audioProcessors}));
                    Log.i(TAG, "Loaded FfmpegAudioRenderer.");
                } catch (ClassNotFoundException e10) {
                } catch (Exception e822) {
                    e2 = e822;
                    extensionRendererIndex = extensionRendererIndex2;
                    throw new RuntimeException(e2);
                }
            } catch (ClassNotFoundException e11) {
                extensionRendererIndex2 = extensionRendererIndex;
            } catch (Exception e8222) {
                e2 = e8222;
                throw new RuntimeException(e2);
            }
        }
    }

    protected void buildTextRenderers(Context context, TextOutput output, Looper outputLooper, int extensionRendererMode, ArrayList<Renderer> out) {
        out.add(new TextRenderer(output, outputLooper));
    }

    protected void buildMetadataRenderers(Context context, MetadataOutput output, Looper outputLooper, int extensionRendererMode, ArrayList<Renderer> out) {
        out.add(new MetadataRenderer(output, outputLooper));
    }

    protected void buildMiscellaneousRenderers(Context context, Handler eventHandler, int extensionRendererMode, ArrayList<Renderer> arrayList) {
    }

    protected AudioProcessor[] buildAudioProcessors() {
        return new AudioProcessor[0];
    }
}
