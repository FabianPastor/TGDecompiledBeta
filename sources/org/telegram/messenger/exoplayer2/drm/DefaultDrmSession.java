package org.telegram.messenger.exoplayer2.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.NotProvisionedException;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C0539C;
import org.telegram.messenger.exoplayer2.DefaultLoadControl;
import org.telegram.messenger.exoplayer2.drm.DefaultDrmSessionManager.EventListener;
import org.telegram.messenger.exoplayer2.drm.DrmSession.DrmSessionException;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.DefaultKeyRequest;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.KeyRequest;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.ProvisionRequest;

@TargetApi(18)
class DefaultDrmSession<T extends ExoMediaCrypto> implements DrmSession<T> {
    private static final int MAX_LICENSE_DURATION_TO_RENEW = 60;
    private static final int MSG_KEYS = 1;
    private static final int MSG_PROVISION = 0;
    private static final String TAG = "DefaultDrmSession";
    final MediaDrmCallback callback;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final byte[] initData;
    private final int initialDrmRequestRetryCount;
    private DrmSessionException lastException;
    private T mediaCrypto;
    private final ExoMediaDrm<T> mediaDrm;
    private final String mimeType;
    private final int mode;
    private byte[] offlineLicenseKeySetId;
    private int openCount;
    private final HashMap<String, String> optionalKeyRequestParameters;
    private PostRequestHandler postRequestHandler;
    final PostResponseHandler postResponseHandler;
    private final ProvisioningManager<T> provisioningManager;
    private HandlerThread requestHandlerThread;
    private byte[] sessionId;
    private int state = 2;
    final UUID uuid;

    /* renamed from: org.telegram.messenger.exoplayer2.drm.DefaultDrmSession$1 */
    class C05591 implements Runnable {
        C05591() {
        }

        public void run() {
            DefaultDrmSession.this.eventListener.onDrmKeysRestored();
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.drm.DefaultDrmSession$2 */
    class C05602 implements Runnable {
        C05602() {
        }

        public void run() {
            DefaultDrmSession.this.eventListener.onDrmKeysRemoved();
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.drm.DefaultDrmSession$3 */
    class C05613 implements Runnable {
        C05613() {
        }

        public void run() {
            DefaultDrmSession.this.eventListener.onDrmKeysLoaded();
        }
    }

    @SuppressLint({"HandlerLeak"})
    private class PostRequestHandler extends Handler {
        public PostRequestHandler(Looper backgroundLooper) {
            super(backgroundLooper);
        }

        Message obtainMessage(int what, Object object, boolean allowRetry) {
            return obtainMessage(what, allowRetry, 0, object);
        }

        public void handleMessage(Message msg) {
            Object executeProvisionRequest;
            try {
                switch (msg.what) {
                    case 0:
                        executeProvisionRequest = DefaultDrmSession.this.callback.executeProvisionRequest(DefaultDrmSession.this.uuid, (ProvisionRequest) msg.obj);
                        break;
                    case 1:
                        executeProvisionRequest = DefaultDrmSession.this.callback.executeKeyRequest(DefaultDrmSession.this.uuid, (KeyRequest) msg.obj);
                        break;
                    default:
                        throw new RuntimeException();
                }
            } catch (Exception e) {
                executeProvisionRequest = e;
                if (maybeRetryRequest(msg)) {
                    return;
                }
            }
            DefaultDrmSession.this.postResponseHandler.obtainMessage(msg.what, executeProvisionRequest).sendToTarget();
        }

        private boolean maybeRetryRequest(Message originalMsg) {
            if (!(originalMsg.arg1 == 1)) {
                return false;
            }
            int errorCount = originalMsg.arg2 + 1;
            if (errorCount > DefaultDrmSession.this.initialDrmRequestRetryCount) {
                return false;
            }
            Message retryMsg = Message.obtain(originalMsg);
            retryMsg.arg2 = errorCount;
            sendMessageDelayed(retryMsg, getRetryDelayMillis(errorCount));
            return true;
        }

        private long getRetryDelayMillis(int errorCount) {
            return (long) Math.min((errorCount - 1) * 1000, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
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
                    DefaultDrmSession.this.onProvisionResponse(msg.obj);
                    return;
                case 1:
                    DefaultDrmSession.this.onKeyResponse(msg.obj);
                    return;
                default:
                    return;
            }
        }
    }

    public interface ProvisioningManager<T extends ExoMediaCrypto> {
        void onProvisionCompleted();

        void onProvisionError(Exception exception);

        void provisionRequired(DefaultDrmSession<T> defaultDrmSession);
    }

    public DefaultDrmSession(UUID uuid, ExoMediaDrm<T> mediaDrm, ProvisioningManager<T> provisioningManager, byte[] initData, String mimeType, int mode, byte[] offlineLicenseKeySetId, HashMap<String, String> optionalKeyRequestParameters, MediaDrmCallback callback, Looper playbackLooper, Handler eventHandler, EventListener eventListener, int initialDrmRequestRetryCount) {
        this.uuid = uuid;
        this.provisioningManager = provisioningManager;
        this.mediaDrm = mediaDrm;
        this.mode = mode;
        this.offlineLicenseKeySetId = offlineLicenseKeySetId;
        this.optionalKeyRequestParameters = optionalKeyRequestParameters;
        this.callback = callback;
        this.initialDrmRequestRetryCount = initialDrmRequestRetryCount;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.postResponseHandler = new PostResponseHandler(playbackLooper);
        this.requestHandlerThread = new HandlerThread("DrmRequestHandler");
        this.requestHandlerThread.start();
        this.postRequestHandler = new PostRequestHandler(this.requestHandlerThread.getLooper());
        if (offlineLicenseKeySetId == null) {
            this.initData = initData;
            this.mimeType = mimeType;
            return;
        }
        this.initData = null;
        this.mimeType = null;
    }

    public void acquire() {
        int i = this.openCount + 1;
        this.openCount = i;
        if (i == 1 && this.state != 1 && openInternal(true)) {
            doLicense(true);
        }
    }

    public boolean release() {
        int i = this.openCount - 1;
        this.openCount = i;
        if (i != 0) {
            return false;
        }
        this.state = 0;
        this.postResponseHandler.removeCallbacksAndMessages(null);
        this.postRequestHandler.removeCallbacksAndMessages(null);
        this.postRequestHandler = null;
        this.requestHandlerThread.quit();
        this.requestHandlerThread = null;
        this.mediaCrypto = null;
        this.lastException = null;
        if (this.sessionId != null) {
            this.mediaDrm.closeSession(this.sessionId);
            this.sessionId = null;
        }
        return true;
    }

    public boolean hasInitData(byte[] initData) {
        return Arrays.equals(this.initData, initData);
    }

    public boolean hasSessionId(byte[] sessionId) {
        return Arrays.equals(this.sessionId, sessionId);
    }

    public void provision() {
        this.postRequestHandler.obtainMessage(0, this.mediaDrm.getProvisionRequest(), true).sendToTarget();
    }

    public void onProvisionCompleted() {
        if (openInternal(false)) {
            doLicense(true);
        }
    }

    public void onProvisionError(Exception error) {
        onError(error);
    }

    public final int getState() {
        return this.state;
    }

    public final DrmSessionException getError() {
        return this.state == 1 ? this.lastException : null;
    }

    public final T getMediaCrypto() {
        return this.mediaCrypto;
    }

    public Map<String, String> queryKeyStatus() {
        return this.sessionId == null ? null : this.mediaDrm.queryKeyStatus(this.sessionId);
    }

    public byte[] getOfflineLicenseKeySetId() {
        return this.offlineLicenseKeySetId;
    }

    private boolean openInternal(boolean allowProvisioning) {
        if (isOpen()) {
            return true;
        }
        try {
            this.sessionId = this.mediaDrm.openSession();
            this.mediaCrypto = this.mediaDrm.createMediaCrypto(this.sessionId);
            this.state = 3;
            return true;
        } catch (NotProvisionedException e) {
            if (allowProvisioning) {
                this.provisioningManager.provisionRequired(this);
            } else {
                onError(e);
            }
            return false;
        } catch (Exception e2) {
            onError(e2);
            return false;
        }
    }

    private void onProvisionResponse(Object response) {
        if (this.state != 2 && !isOpen()) {
            return;
        }
        if (response instanceof Exception) {
            this.provisioningManager.onProvisionError((Exception) response);
            return;
        }
        try {
            this.mediaDrm.provideProvisionResponse((byte[]) response);
            this.provisioningManager.onProvisionCompleted();
        } catch (Exception e) {
            this.provisioningManager.onProvisionError(e);
        }
    }

    private void doLicense(boolean allowRetry) {
        switch (this.mode) {
            case 0:
            case 1:
                if (this.offlineLicenseKeySetId == null) {
                    postKeyRequest(1, allowRetry);
                    return;
                } else if (this.state == 4 || restoreKeys()) {
                    long licenseDurationRemainingSec = getLicenseDurationRemainingSec();
                    if (this.mode == 0 && licenseDurationRemainingSec <= 60) {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Offline license has expired or will expire soon. Remaining seconds: ");
                        stringBuilder.append(licenseDurationRemainingSec);
                        Log.d(str, stringBuilder.toString());
                        postKeyRequest(2, allowRetry);
                    } else if (licenseDurationRemainingSec <= 0) {
                        onError(new KeysExpiredException());
                    } else {
                        this.state = 4;
                        if (!(this.eventHandler == null || this.eventListener == null)) {
                            this.eventHandler.post(new C05591());
                        }
                    }
                    return;
                } else {
                    return;
                }
            case 2:
                if (this.offlineLicenseKeySetId == null) {
                    postKeyRequest(2, allowRetry);
                    return;
                } else if (restoreKeys()) {
                    postKeyRequest(2, allowRetry);
                    return;
                } else {
                    return;
                }
            case 3:
                if (restoreKeys()) {
                    postKeyRequest(3, allowRetry);
                    return;
                }
                return;
            default:
                return;
        }
    }

    private boolean restoreKeys() {
        try {
            this.mediaDrm.restoreKeys(this.sessionId, this.offlineLicenseKeySetId);
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error trying to restore Widevine keys.", e);
            onError(e);
            return false;
        }
    }

    private long getLicenseDurationRemainingSec() {
        if (!C0539C.WIDEVINE_UUID.equals(this.uuid)) {
            return Long.MAX_VALUE;
        }
        Pair<Long, Long> pair = WidevineUtil.getLicenseDurationRemainingSec(this);
        return Math.min(((Long) pair.first).longValue(), ((Long) pair.second).longValue());
    }

    private void postKeyRequest(int type, boolean allowRetry) {
        try {
            KeyRequest request = this.mediaDrm.getKeyRequest(type == 3 ? this.offlineLicenseKeySetId : this.sessionId, this.initData, this.mimeType, type, this.optionalKeyRequestParameters);
            if (C0539C.CLEARKEY_UUID.equals(this.uuid)) {
                request = new DefaultKeyRequest(ClearKeyUtil.adjustRequestData(request.getData()), request.getDefaultUrl());
            }
            this.postRequestHandler.obtainMessage(1, request, allowRetry).sendToTarget();
        } catch (Exception e) {
            onKeysError(e);
        }
    }

    private void onKeyResponse(Object response) {
        if (!isOpen()) {
            return;
        }
        if (response instanceof Exception) {
            onKeysError((Exception) response);
            return;
        }
        try {
            byte[] responseData = (byte[]) response;
            if (C0539C.CLEARKEY_UUID.equals(this.uuid)) {
                responseData = ClearKeyUtil.adjustResponseData(responseData);
            }
            if (this.mode == 3) {
                this.mediaDrm.provideKeyResponse(this.offlineLicenseKeySetId, responseData);
                if (!(this.eventHandler == null || this.eventListener == null)) {
                    this.eventHandler.post(new C05602());
                }
            } else {
                byte[] keySetId = this.mediaDrm.provideKeyResponse(this.sessionId, responseData);
                if (!((this.mode != 2 && (this.mode != 0 || this.offlineLicenseKeySetId == null)) || keySetId == null || keySetId.length == 0)) {
                    this.offlineLicenseKeySetId = keySetId;
                }
                this.state = 4;
                if (!(this.eventHandler == null || this.eventListener == null)) {
                    this.eventHandler.post(new C05613());
                }
            }
        } catch (Exception e) {
            onKeysError(e);
        }
    }

    private void onKeysExpired() {
        if (this.state == 4) {
            this.state = 3;
            onError(new KeysExpiredException());
        }
    }

    private void onKeysError(Exception e) {
        if (e instanceof NotProvisionedException) {
            this.provisioningManager.provisionRequired(this);
        } else {
            onError(e);
        }
    }

    private void onError(final Exception e) {
        this.lastException = new DrmSessionException(e);
        if (!(this.eventHandler == null || this.eventListener == null)) {
            this.eventHandler.post(new Runnable() {
                public void run() {
                    DefaultDrmSession.this.eventListener.onDrmSessionManagerError(e);
                }
            });
        }
        if (this.state != 4) {
            this.state = 1;
        }
    }

    private boolean isOpen() {
        if (this.state != 3) {
            if (this.state != 4) {
                return false;
            }
        }
        return true;
    }

    public void onMediaDrmEvent(int what) {
        if (isOpen()) {
            switch (what) {
                case 1:
                    this.state = 3;
                    this.provisioningManager.provisionRequired(this);
                    break;
                case 2:
                    doLicense(false);
                    break;
                case 3:
                    onKeysExpired();
                    break;
                default:
                    break;
            }
        }
    }
}
