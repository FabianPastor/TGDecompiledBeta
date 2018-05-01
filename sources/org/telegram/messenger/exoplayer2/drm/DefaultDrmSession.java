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
import org.telegram.messenger.exoplayer2.C0542C;
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
    class C05621 implements Runnable {
        C05621() {
        }

        public void run() {
            DefaultDrmSession.this.eventListener.onDrmKeysRestored();
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.drm.DefaultDrmSession$2 */
    class C05632 implements Runnable {
        C05632() {
        }

        public void run() {
            DefaultDrmSession.this.eventListener.onDrmKeysRemoved();
        }
    }

    /* renamed from: org.telegram.messenger.exoplayer2.drm.DefaultDrmSession$3 */
    class C05643 implements Runnable {
        C05643() {
        }

        public void run() {
            DefaultDrmSession.this.eventListener.onDrmKeysLoaded();
        }
    }

    @SuppressLint({"HandlerLeak"})
    private class PostRequestHandler extends Handler {
        public PostRequestHandler(Looper looper) {
            super(looper);
        }

        Message obtainMessage(int i, Object obj, boolean z) {
            return obtainMessage(i, z, 0, obj);
        }

        public void handleMessage(Message message) {
            Object executeProvisionRequest;
            try {
                switch (message.what) {
                    case 0:
                        executeProvisionRequest = DefaultDrmSession.this.callback.executeProvisionRequest(DefaultDrmSession.this.uuid, (ProvisionRequest) message.obj);
                        break;
                    case 1:
                        executeProvisionRequest = DefaultDrmSession.this.callback.executeKeyRequest(DefaultDrmSession.this.uuid, (KeyRequest) message.obj);
                        break;
                    default:
                        throw new RuntimeException();
                }
            } catch (Exception e) {
                executeProvisionRequest = e;
                if (maybeRetryRequest(message)) {
                    return;
                }
            }
            DefaultDrmSession.this.postResponseHandler.obtainMessage(message.what, executeProvisionRequest).sendToTarget();
        }

        private boolean maybeRetryRequest(Message message) {
            if (!(message.arg1 == 1)) {
                return false;
            }
            int i = message.arg2 + 1;
            if (i > DefaultDrmSession.this.initialDrmRequestRetryCount) {
                return false;
            }
            message = Message.obtain(message);
            message.arg2 = i;
            sendMessageDelayed(message, getRetryDelayMillis(i));
            return true;
        }

        private long getRetryDelayMillis(int i) {
            return (long) Math.min((i - 1) * 1000, DefaultLoadControl.DEFAULT_BUFFER_FOR_PLAYBACK_AFTER_REBUFFER_MS);
        }
    }

    @SuppressLint({"HandlerLeak"})
    private class PostResponseHandler extends Handler {
        public PostResponseHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    DefaultDrmSession.this.onProvisionResponse(message.obj);
                    return;
                case 1:
                    DefaultDrmSession.this.onKeyResponse(message.obj);
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

    public DefaultDrmSession(UUID uuid, ExoMediaDrm<T> exoMediaDrm, ProvisioningManager<T> provisioningManager, byte[] bArr, String str, int i, byte[] bArr2, HashMap<String, String> hashMap, MediaDrmCallback mediaDrmCallback, Looper looper, Handler handler, EventListener eventListener, int i2) {
        this.uuid = uuid;
        this.provisioningManager = provisioningManager;
        this.mediaDrm = exoMediaDrm;
        this.mode = i;
        this.offlineLicenseKeySetId = bArr2;
        this.optionalKeyRequestParameters = hashMap;
        this.callback = mediaDrmCallback;
        this.initialDrmRequestRetryCount = i2;
        this.eventHandler = handler;
        this.eventListener = eventListener;
        this.postResponseHandler = new PostResponseHandler(looper);
        this.requestHandlerThread = new HandlerThread("DrmRequestHandler");
        this.requestHandlerThread.start();
        this.postRequestHandler = new PostRequestHandler(this.requestHandlerThread.getLooper());
        if (bArr2 == null) {
            this.initData = bArr;
            this.mimeType = str;
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

    public boolean hasInitData(byte[] bArr) {
        return Arrays.equals(this.initData, bArr);
    }

    public boolean hasSessionId(byte[] bArr) {
        return Arrays.equals(this.sessionId, bArr);
    }

    public void provision() {
        this.postRequestHandler.obtainMessage(0, this.mediaDrm.getProvisionRequest(), true).sendToTarget();
    }

    public void onProvisionCompleted() {
        if (openInternal(false)) {
            doLicense(true);
        }
    }

    public void onProvisionError(Exception exception) {
        onError(exception);
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

    private boolean openInternal(boolean z) {
        if (isOpen()) {
            return true;
        }
        try {
            this.sessionId = this.mediaDrm.openSession();
            this.mediaCrypto = this.mediaDrm.createMediaCrypto(this.sessionId);
            this.state = 3;
            return true;
        } catch (Exception e) {
            if (z) {
                this.provisioningManager.provisionRequired(this);
            } else {
                onError(e);
            }
            return false;
        } catch (boolean z2) {
            onError(z2);
            return false;
        }
    }

    private void onProvisionResponse(Object obj) {
        if (this.state != 2 && !isOpen()) {
            return;
        }
        if (obj instanceof Exception) {
            this.provisioningManager.onProvisionError((Exception) obj);
            return;
        }
        try {
            this.mediaDrm.provideProvisionResponse((byte[]) obj);
            this.provisioningManager.onProvisionCompleted();
        } catch (Object obj2) {
            this.provisioningManager.onProvisionError(obj2);
        }
    }

    private void doLicense(boolean z) {
        switch (this.mode) {
            case 0:
            case 1:
                if (this.offlineLicenseKeySetId == null) {
                    postKeyRequest(1, z);
                    return;
                } else if (this.state == 4 || restoreKeys()) {
                    long licenseDurationRemainingSec = getLicenseDurationRemainingSec();
                    if (this.mode == 0 && licenseDurationRemainingSec <= 60) {
                        String str = TAG;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("Offline license has expired or will expire soon. Remaining seconds: ");
                        stringBuilder.append(licenseDurationRemainingSec);
                        Log.d(str, stringBuilder.toString());
                        postKeyRequest(2, z);
                        return;
                    } else if (licenseDurationRemainingSec <= 0) {
                        onError(new KeysExpiredException());
                        return;
                    } else {
                        this.state = 4;
                        if (this.eventHandler && this.eventListener) {
                            this.eventHandler.post(new C05621());
                            return;
                        }
                        return;
                    }
                } else {
                    return;
                }
            case 2:
                if (this.offlineLicenseKeySetId == null) {
                    postKeyRequest(2, z);
                    return;
                } else if (restoreKeys()) {
                    postKeyRequest(2, z);
                    return;
                } else {
                    return;
                }
            case 3:
                if (restoreKeys()) {
                    postKeyRequest(3, z);
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
        } catch (Throwable e) {
            Log.e(TAG, "Error trying to restore Widevine keys.", e);
            onError(e);
            return false;
        }
    }

    private long getLicenseDurationRemainingSec() {
        if (!C0542C.WIDEVINE_UUID.equals(this.uuid)) {
            return Long.MAX_VALUE;
        }
        Pair licenseDurationRemainingSec = WidevineUtil.getLicenseDurationRemainingSec(this);
        return Math.min(((Long) licenseDurationRemainingSec.first).longValue(), ((Long) licenseDurationRemainingSec.second).longValue());
    }

    private void postKeyRequest(int i, boolean z) {
        try {
            i = this.mediaDrm.getKeyRequest(i == 3 ? this.offlineLicenseKeySetId : this.sessionId, this.initData, this.mimeType, i, this.optionalKeyRequestParameters);
            if (C0542C.CLEARKEY_UUID.equals(this.uuid)) {
                i = new DefaultKeyRequest(ClearKeyUtil.adjustRequestData(i.getData()), i.getDefaultUrl());
            }
            this.postRequestHandler.obtainMessage(1, i, z).sendToTarget();
        } catch (int i2) {
            onKeysError(i2);
        }
    }

    private void onKeyResponse(Object obj) {
        if (!isOpen()) {
            return;
        }
        if (obj instanceof Exception) {
            onKeysError((Exception) obj);
            return;
        }
        try {
            obj = (byte[]) obj;
            if (C0542C.CLEARKEY_UUID.equals(this.uuid)) {
                obj = ClearKeyUtil.adjustResponseData(obj);
            }
            if (this.mode == 3) {
                this.mediaDrm.provideKeyResponse(this.offlineLicenseKeySetId, obj);
                if (!(this.eventHandler == null || this.eventListener == null)) {
                    this.eventHandler.post(new C05632());
                }
            } else {
                obj = this.mediaDrm.provideKeyResponse(this.sessionId, obj);
                if (!((this.mode != 2 && (this.mode != 0 || this.offlineLicenseKeySetId == null)) || obj == null || obj.length == 0)) {
                    this.offlineLicenseKeySetId = obj;
                }
                this.state = 4;
                if (!(this.eventHandler == null || this.eventListener == null)) {
                    this.eventHandler.post(new C05643());
                }
            }
        } catch (Object obj2) {
            onKeysError(obj2);
        }
    }

    private void onKeysExpired() {
        if (this.state == 4) {
            this.state = 3;
            onError(new KeysExpiredException());
        }
    }

    private void onKeysError(Exception exception) {
        if (exception instanceof NotProvisionedException) {
            this.provisioningManager.provisionRequired(this);
        } else {
            onError(exception);
        }
    }

    private void onError(final Exception exception) {
        this.lastException = new DrmSessionException(exception);
        if (!(this.eventHandler == null || this.eventListener == null)) {
            this.eventHandler.post(new Runnable() {
                public void run() {
                    DefaultDrmSession.this.eventListener.onDrmSessionManagerError(exception);
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

    public void onMediaDrmEvent(int i) {
        if (isOpen()) {
            switch (i) {
                case 1:
                    this.state = 3;
                    this.provisioningManager.provisionRequired(this);
                    break;
                case 2:
                    doLicense(0);
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
