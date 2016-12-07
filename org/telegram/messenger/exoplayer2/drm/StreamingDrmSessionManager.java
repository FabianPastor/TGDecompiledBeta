package org.telegram.messenger.exoplayer2.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.DeniedByServerException;
import android.media.NotProvisionedException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import java.util.HashMap;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.KeyRequest;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.OnEventListener;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.ProvisionRequest;
import org.telegram.messenger.exoplayer2.extractor.mp4.PsshAtomUtil;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(18)
public class StreamingDrmSessionManager<T extends ExoMediaCrypto> implements DrmSessionManager<T>, DrmSession<T> {
    private static final int MSG_KEYS = 1;
    private static final int MSG_PROVISION = 0;
    public static final String PLAYREADY_CUSTOM_DATA_KEY = "PRCustomData";
    final MediaDrmCallback callback;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private Exception lastException;
    private T mediaCrypto;
    private final ExoMediaDrm<T> mediaDrm;
    MediaDrmHandler mediaDrmHandler;
    private int openCount;
    private final HashMap<String, String> optionalKeyRequestParameters;
    private Looper playbackLooper;
    private Handler postRequestHandler;
    PostResponseHandler postResponseHandler;
    private boolean provisioningInProgress;
    private HandlerThread requestHandlerThread;
    private SchemeData schemeData;
    private byte[] sessionId;
    private int state = 1;
    final UUID uuid;

    public interface EventListener {
        void onDrmKeysLoaded();

        void onDrmSessionManagerError(Exception exception);
    }

    @SuppressLint({"HandlerLeak"})
    private class MediaDrmHandler extends Handler {
        public MediaDrmHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (StreamingDrmSessionManager.this.openCount == 0) {
                return;
            }
            if (StreamingDrmSessionManager.this.state == 3 || StreamingDrmSessionManager.this.state == 4) {
                switch (msg.what) {
                    case 1:
                        StreamingDrmSessionManager.this.state = 3;
                        StreamingDrmSessionManager.this.postProvisionRequest();
                        return;
                    case 2:
                        StreamingDrmSessionManager.this.postKeyRequest();
                        return;
                    case 3:
                        StreamingDrmSessionManager.this.state = 3;
                        StreamingDrmSessionManager.this.onError(new KeysExpiredException());
                        return;
                    default:
                        return;
                }
            }
        }
    }

    @SuppressLint({"HandlerLeak"})
    private class PostRequestHandler extends Handler {
        public PostRequestHandler(Looper backgroundLooper) {
            super(backgroundLooper);
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(Message msg) {
            Object response;
            try {
                switch (msg.what) {
                    case 0:
                        response = StreamingDrmSessionManager.this.callback.executeProvisionRequest(StreamingDrmSessionManager.this.uuid, (ProvisionRequest) msg.obj);
                        break;
                    case 1:
                        response = StreamingDrmSessionManager.this.callback.executeKeyRequest(StreamingDrmSessionManager.this.uuid, (KeyRequest) msg.obj);
                        break;
                    default:
                        throw new RuntimeException();
                }
            } catch (Exception e) {
                response = e;
            }
            StreamingDrmSessionManager.this.postResponseHandler.obtainMessage(msg.what, response).sendToTarget();
        }
    }

    @SuppressLint({"HandlerLeak"})
    private class PostResponseHandler extends Handler {
        public PostResponseHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    StreamingDrmSessionManager.this.onProvisionResponse(msg.obj);
                    return;
                case 1:
                    StreamingDrmSessionManager.this.onKeyResponse(msg.obj);
                    return;
                default:
                    return;
            }
        }
    }

    private class MediaDrmEventListener implements OnEventListener<T> {
        private MediaDrmEventListener() {
        }

        public void onEvent(ExoMediaDrm<? extends T> exoMediaDrm, byte[] sessionId, int event, int extra, byte[] data) {
            StreamingDrmSessionManager.this.mediaDrmHandler.sendEmptyMessage(event);
        }
    }

    public static StreamingDrmSessionManager<FrameworkMediaCrypto> newWidevineInstance(MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, EventListener eventListener) throws UnsupportedDrmException {
        return newFrameworkInstance(C.WIDEVINE_UUID, callback, optionalKeyRequestParameters, eventHandler, eventListener);
    }

    public static StreamingDrmSessionManager<FrameworkMediaCrypto> newPlayReadyInstance(MediaDrmCallback callback, String customData, Handler eventHandler, EventListener eventListener) throws UnsupportedDrmException {
        HashMap<String, String> optionalKeyRequestParameters;
        if (TextUtils.isEmpty(customData)) {
            optionalKeyRequestParameters = null;
        } else {
            optionalKeyRequestParameters = new HashMap();
            optionalKeyRequestParameters.put(PLAYREADY_CUSTOM_DATA_KEY, customData);
        }
        return newFrameworkInstance(C.PLAYREADY_UUID, callback, optionalKeyRequestParameters, eventHandler, eventListener);
    }

    public static StreamingDrmSessionManager<FrameworkMediaCrypto> newFrameworkInstance(UUID uuid, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, EventListener eventListener) throws UnsupportedDrmException {
        return new StreamingDrmSessionManager(uuid, FrameworkMediaDrm.newInstance(uuid), callback, optionalKeyRequestParameters, eventHandler, eventListener);
    }

    public StreamingDrmSessionManager(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, EventListener eventListener) {
        this.uuid = uuid;
        this.mediaDrm = mediaDrm;
        this.callback = callback;
        this.optionalKeyRequestParameters = optionalKeyRequestParameters;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        mediaDrm.setOnEventListener(new MediaDrmEventListener());
    }

    public final String getPropertyString(String key) {
        return this.mediaDrm.getPropertyString(key);
    }

    public final void setPropertyString(String key, String value) {
        this.mediaDrm.setPropertyString(key, value);
    }

    public final byte[] getPropertyByteArray(String key) {
        return this.mediaDrm.getPropertyByteArray(key);
    }

    public final void setPropertyByteArray(String key, byte[] value) {
        this.mediaDrm.setPropertyByteArray(key, value);
    }

    public DrmSession<T> acquireSession(Looper playbackLooper, DrmInitData drmInitData) {
        boolean z = this.playbackLooper == null || this.playbackLooper == playbackLooper;
        Assertions.checkState(z);
        int i = this.openCount + 1;
        this.openCount = i;
        if (i == 1) {
            if (this.playbackLooper == null) {
                this.playbackLooper = playbackLooper;
                this.mediaDrmHandler = new MediaDrmHandler(playbackLooper);
                this.postResponseHandler = new PostResponseHandler(playbackLooper);
            }
            this.requestHandlerThread = new HandlerThread("DrmRequestHandler");
            this.requestHandlerThread.start();
            this.postRequestHandler = new PostRequestHandler(this.requestHandlerThread.getLooper());
            this.schemeData = drmInitData.get(this.uuid);
            if (this.schemeData == null) {
                onError(new IllegalStateException("Media does not support uuid: " + this.uuid));
            } else {
                if (Util.SDK_INT < 21) {
                    byte[] psshData = PsshAtomUtil.parseSchemeSpecificData(this.schemeData.data, C.WIDEVINE_UUID);
                    if (psshData != null) {
                        this.schemeData = new SchemeData(C.WIDEVINE_UUID, this.schemeData.mimeType, psshData);
                    }
                }
                this.state = 2;
                openInternal(true);
            }
        }
        return this;
    }

    public void releaseSession(DrmSession<T> drmSession) {
        int i = this.openCount - 1;
        this.openCount = i;
        if (i == 0) {
            this.state = 1;
            this.provisioningInProgress = false;
            this.mediaDrmHandler.removeCallbacksAndMessages(null);
            this.postResponseHandler.removeCallbacksAndMessages(null);
            this.postRequestHandler.removeCallbacksAndMessages(null);
            this.postRequestHandler = null;
            this.requestHandlerThread.quit();
            this.requestHandlerThread = null;
            this.schemeData = null;
            this.mediaCrypto = null;
            this.lastException = null;
            if (this.sessionId != null) {
                this.mediaDrm.closeSession(this.sessionId);
                this.sessionId = null;
            }
        }
    }

    public final int getState() {
        return this.state;
    }

    public final T getMediaCrypto() {
        if (this.state == 3 || this.state == 4) {
            return this.mediaCrypto;
        }
        throw new IllegalStateException();
    }

    public boolean requiresSecureDecoderComponent(String mimeType) {
        if (this.state == 3 || this.state == 4) {
            return this.mediaCrypto.requiresSecureDecoderComponent(mimeType);
        }
        throw new IllegalStateException();
    }

    public final Exception getError() {
        return this.state == 0 ? this.lastException : null;
    }

    private void openInternal(boolean allowProvisioning) {
        try {
            this.sessionId = this.mediaDrm.openSession();
            this.mediaCrypto = this.mediaDrm.createMediaCrypto(this.uuid, this.sessionId);
            this.state = 3;
            postKeyRequest();
        } catch (NotProvisionedException e) {
            if (allowProvisioning) {
                postProvisionRequest();
            } else {
                onError(e);
            }
        } catch (Exception e2) {
            onError(e2);
        }
    }

    private void postProvisionRequest() {
        if (!this.provisioningInProgress) {
            this.provisioningInProgress = true;
            this.postRequestHandler.obtainMessage(0, this.mediaDrm.getProvisionRequest()).sendToTarget();
        }
    }

    private void onProvisionResponse(Object response) {
        this.provisioningInProgress = false;
        if (this.state != 2 && this.state != 3 && this.state != 4) {
            return;
        }
        if (response instanceof Exception) {
            onError((Exception) response);
            return;
        }
        try {
            this.mediaDrm.provideProvisionResponse((byte[]) response);
            if (this.state == 2) {
                openInternal(false);
            } else {
                postKeyRequest();
            }
        } catch (DeniedByServerException e) {
            onError(e);
        }
    }

    private void postKeyRequest() {
        try {
            this.postRequestHandler.obtainMessage(1, this.mediaDrm.getKeyRequest(this.sessionId, this.schemeData.data, this.schemeData.mimeType, 1, this.optionalKeyRequestParameters)).sendToTarget();
        } catch (NotProvisionedException e) {
            onKeysError(e);
        }
    }

    private void onKeyResponse(Object response) {
        if (this.state != 3 && this.state != 4) {
            return;
        }
        if (response instanceof Exception) {
            onKeysError((Exception) response);
            return;
        }
        try {
            this.mediaDrm.provideKeyResponse(this.sessionId, (byte[]) response);
            this.state = 4;
            if (this.eventHandler != null && this.eventListener != null) {
                this.eventHandler.post(new Runnable() {
                    public void run() {
                        StreamingDrmSessionManager.this.eventListener.onDrmKeysLoaded();
                    }
                });
            }
        } catch (Exception e) {
            onKeysError(e);
        }
    }

    private void onKeysError(Exception e) {
        if (e instanceof NotProvisionedException) {
            postProvisionRequest();
        } else {
            onError(e);
        }
    }

    private void onError(final Exception e) {
        this.lastException = e;
        if (!(this.eventHandler == null || this.eventListener == null)) {
            this.eventHandler.post(new Runnable() {
                public void run() {
                    StreamingDrmSessionManager.this.eventListener.onDrmSessionManagerError(e);
                }
            });
        }
        if (this.state != 4) {
            this.state = 0;
        }
    }
}
