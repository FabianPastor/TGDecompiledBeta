package org.telegram.messenger.exoplayer2.drm;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.drm.DefaultDrmSession.ProvisioningManager;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
import org.telegram.messenger.exoplayer2.drm.DrmSession.DrmSessionException;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.OnEventListener;
import org.telegram.messenger.exoplayer2.extractor.mp4.PsshAtomUtil;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.Util;

@TargetApi(18)
public class DefaultDrmSessionManager<T extends ExoMediaCrypto> implements ProvisioningManager<T>, DrmSessionManager<T> {
    private static final String CENC_SCHEME_MIME_TYPE = "cenc";
    public static final int INITIAL_DRM_REQUEST_RETRY_COUNT = 3;
    public static final int MODE_DOWNLOAD = 2;
    public static final int MODE_PLAYBACK = 0;
    public static final int MODE_QUERY = 1;
    public static final int MODE_RELEASE = 3;
    public static final String PLAYREADY_CUSTOM_DATA_KEY = "PRCustomData";
    private static final String TAG = "DefaultDrmSessionMgr";
    private final MediaDrmCallback callback;
    private final Handler eventHandler;
    private final EventListener eventListener;
    private final int initialDrmRequestRetryCount;
    private final ExoMediaDrm<T> mediaDrm;
    volatile MediaDrmHandler mediaDrmHandler;
    private int mode;
    private final boolean multiSession;
    private byte[] offlineLicenseKeySetId;
    private final HashMap<String, String> optionalKeyRequestParameters;
    private Looper playbackLooper;
    private final List<DefaultDrmSession<T>> provisioningSessions;
    private final List<DefaultDrmSession<T>> sessions;
    private final UUID uuid;

    public interface EventListener {
        void onDrmKeysLoaded();

        void onDrmKeysRemoved();

        void onDrmKeysRestored();

        void onDrmSessionManagerError(Exception exception);
    }

    @SuppressLint({"HandlerLeak"})
    private class MediaDrmHandler extends Handler {
        public MediaDrmHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            byte[] sessionId = (byte[]) msg.obj;
            for (DefaultDrmSession<T> session : DefaultDrmSessionManager.this.sessions) {
                if (session.hasSessionId(sessionId)) {
                    session.onMediaDrmEvent(msg.what);
                    return;
                }
            }
        }
    }

    public static final class MissingSchemeDataException extends Exception {
        private MissingSchemeDataException(UUID uuid) {
            super("Media does not support uuid: " + uuid);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
    }

    private class MediaDrmEventListener implements OnEventListener<T> {
        private MediaDrmEventListener() {
        }

        public void onEvent(ExoMediaDrm<? extends T> exoMediaDrm, byte[] sessionId, int event, int extra, byte[] data) {
            if (DefaultDrmSessionManager.this.mode == 0) {
                DefaultDrmSessionManager.this.mediaDrmHandler.obtainMessage(event, sessionId).sendToTarget();
            }
        }
    }

    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newWidevineInstance(MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, EventListener eventListener) throws UnsupportedDrmException {
        return newFrameworkInstance(C.WIDEVINE_UUID, callback, optionalKeyRequestParameters, eventHandler, eventListener);
    }

    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newPlayReadyInstance(MediaDrmCallback callback, String customData, Handler eventHandler, EventListener eventListener) throws UnsupportedDrmException {
        HashMap<String, String> optionalKeyRequestParameters;
        if (TextUtils.isEmpty(customData)) {
            optionalKeyRequestParameters = null;
        } else {
            optionalKeyRequestParameters = new HashMap();
            optionalKeyRequestParameters.put(PLAYREADY_CUSTOM_DATA_KEY, customData);
        }
        return newFrameworkInstance(C.PLAYREADY_UUID, callback, optionalKeyRequestParameters, eventHandler, eventListener);
    }

    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newFrameworkInstance(UUID uuid, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, EventListener eventListener) throws UnsupportedDrmException {
        return new DefaultDrmSessionManager(uuid, FrameworkMediaDrm.newInstance(uuid), callback, optionalKeyRequestParameters, eventHandler, eventListener, false, 3);
    }

    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, EventListener eventListener) {
        this(uuid, mediaDrm, callback, optionalKeyRequestParameters, eventHandler, eventListener, false, 3);
    }

    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, EventListener eventListener, boolean multiSession) {
        this(uuid, mediaDrm, callback, optionalKeyRequestParameters, eventHandler, eventListener, multiSession, 3);
    }

    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> mediaDrm, MediaDrmCallback callback, HashMap<String, String> optionalKeyRequestParameters, Handler eventHandler, EventListener eventListener, boolean multiSession, int initialDrmRequestRetryCount) {
        Assertions.checkNotNull(uuid);
        Assertions.checkNotNull(mediaDrm);
        Assertions.checkArgument(!C.COMMON_PSSH_UUID.equals(uuid), "Use C.CLEARKEY_UUID instead");
        this.uuid = uuid;
        this.mediaDrm = mediaDrm;
        this.callback = callback;
        this.optionalKeyRequestParameters = optionalKeyRequestParameters;
        this.eventHandler = eventHandler;
        this.eventListener = eventListener;
        this.multiSession = multiSession;
        this.initialDrmRequestRetryCount = initialDrmRequestRetryCount;
        this.mode = 0;
        this.sessions = new ArrayList();
        this.provisioningSessions = new ArrayList();
        if (multiSession) {
            mediaDrm.setPropertyString("sessionSharing", "enable");
        }
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

    public void setMode(int mode, byte[] offlineLicenseKeySetId) {
        Assertions.checkState(this.sessions.isEmpty());
        if (mode == 1 || mode == 3) {
            Assertions.checkNotNull(offlineLicenseKeySetId);
        }
        this.mode = mode;
        this.offlineLicenseKeySetId = offlineLicenseKeySetId;
    }

    public boolean canAcquireSession(DrmInitData drmInitData) {
        if (this.offlineLicenseKeySetId != null) {
            return true;
        }
        if (getSchemeData(drmInitData, this.uuid, true) == null) {
            if (drmInitData.schemeDataCount != 1 || !drmInitData.get(0).matches(C.COMMON_PSSH_UUID)) {
                return false;
            }
            Log.w(TAG, "DrmInitData only contains common PSSH SchemeData. Assuming support for: " + this.uuid);
        }
        String schemeType = drmInitData.schemeType;
        if (schemeType == null || "cenc".equals(schemeType)) {
            return true;
        }
        if ((C.CENC_TYPE_cbc1.equals(schemeType) || C.CENC_TYPE_cbcs.equals(schemeType) || C.CENC_TYPE_cens.equals(schemeType)) && Util.SDK_INT < 24) {
            return false;
        }
        return true;
    }

    public DrmSession<T> acquireSession(Looper playbackLooper, DrmInitData drmInitData) {
        DrmSession<T> session;
        boolean z = this.playbackLooper == null || this.playbackLooper == playbackLooper;
        Assertions.checkState(z);
        if (this.sessions.isEmpty()) {
            this.playbackLooper = playbackLooper;
            if (this.mediaDrmHandler == null) {
                this.mediaDrmHandler = new MediaDrmHandler(playbackLooper);
            }
        }
        byte[] initData = null;
        String mimeType = null;
        if (this.offlineLicenseKeySetId == null) {
            SchemeData data = getSchemeData(drmInitData, this.uuid, false);
            if (data == null) {
                Throwable missingSchemeDataException = new MissingSchemeDataException(this.uuid);
                if (!(this.eventHandler == null || this.eventListener == null)) {
                    final Throwable th = missingSchemeDataException;
                    this.eventHandler.post(new Runnable() {
                        public void run() {
                            DefaultDrmSessionManager.this.eventListener.onDrmSessionManagerError(th);
                        }
                    });
                }
                return new ErrorStateDrmSession(new DrmSessionException(missingSchemeDataException));
            }
            initData = getSchemeInitData(data, this.uuid);
            mimeType = getSchemeMimeType(data, this.uuid);
        }
        Object session2;
        if (this.multiSession) {
            session = null;
            for (DefaultDrmSession<T> existingSession : this.sessions) {
                if (existingSession.hasInitData(initData)) {
                    session2 = existingSession;
                    break;
                }
            }
        } else if (this.sessions.isEmpty()) {
            session = null;
        } else {
            session2 = (DefaultDrmSession) this.sessions.get(0);
        }
        if (session == null) {
            session = new DefaultDrmSession(this.uuid, this.mediaDrm, this, initData, mimeType, this.mode, this.offlineLicenseKeySetId, this.optionalKeyRequestParameters, this.callback, playbackLooper, this.eventHandler, this.eventListener, this.initialDrmRequestRetryCount);
            this.sessions.add(session);
        }
        session.acquire();
        return session;
    }

    public void releaseSession(DrmSession<T> session) {
        if (!(session instanceof ErrorStateDrmSession)) {
            DefaultDrmSession<T> drmSession = (DefaultDrmSession) session;
            if (drmSession.release()) {
                this.sessions.remove(drmSession);
                if (this.provisioningSessions.size() > 1 && this.provisioningSessions.get(0) == drmSession) {
                    ((DefaultDrmSession) this.provisioningSessions.get(1)).provision();
                }
                this.provisioningSessions.remove(drmSession);
            }
        }
    }

    public void provisionRequired(DefaultDrmSession<T> session) {
        this.provisioningSessions.add(session);
        if (this.provisioningSessions.size() == 1) {
            session.provision();
        }
    }

    public void onProvisionCompleted() {
        for (DefaultDrmSession<T> session : this.provisioningSessions) {
            session.onProvisionCompleted();
        }
        this.provisioningSessions.clear();
    }

    public void onProvisionError(Exception error) {
        for (DefaultDrmSession<T> session : this.provisioningSessions) {
            session.onProvisionError(error);
        }
        this.provisioningSessions.clear();
    }

    private static SchemeData getSchemeData(DrmInitData drmInitData, UUID uuid, boolean allowMissingData) {
        int i;
        List<SchemeData> matchingSchemeDatas = new ArrayList(drmInitData.schemeDataCount);
        for (i = 0; i < drmInitData.schemeDataCount; i++) {
            boolean uuidMatches;
            SchemeData schemeData = drmInitData.get(i);
            if (schemeData.matches(uuid) || (C.CLEARKEY_UUID.equals(uuid) && schemeData.matches(C.COMMON_PSSH_UUID))) {
                uuidMatches = true;
            } else {
                uuidMatches = false;
            }
            if (uuidMatches && (schemeData.data != null || allowMissingData)) {
                matchingSchemeDatas.add(schemeData);
            }
        }
        if (matchingSchemeDatas.isEmpty()) {
            return null;
        }
        if (C.WIDEVINE_UUID.equals(uuid)) {
            for (i = 0; i < matchingSchemeDatas.size(); i++) {
                SchemeData matchingSchemeData = (SchemeData) matchingSchemeDatas.get(i);
                int version = matchingSchemeData.hasData() ? PsshAtomUtil.parseVersion(matchingSchemeData.data) : -1;
                if (Util.SDK_INT < 23 && version == 0) {
                    return matchingSchemeData;
                }
                if (Util.SDK_INT >= 23 && version == 1) {
                    return matchingSchemeData;
                }
            }
        }
        return (SchemeData) matchingSchemeDatas.get(0);
    }

    private static byte[] getSchemeInitData(SchemeData data, UUID uuid) {
        byte[] schemeInitData = data.data;
        if (Util.SDK_INT >= 21) {
            return schemeInitData;
        }
        byte[] psshData = PsshAtomUtil.parseSchemeSpecificData(schemeInitData, uuid);
        if (psshData == null) {
            return schemeInitData;
        }
        return psshData;
    }

    private static String getSchemeMimeType(SchemeData data, UUID uuid) {
        String schemeMimeType = data.mimeType;
        if (Util.SDK_INT >= 26 || !C.CLEARKEY_UUID.equals(uuid)) {
            return schemeMimeType;
        }
        if (MimeTypes.VIDEO_MP4.equals(schemeMimeType) || MimeTypes.AUDIO_MP4.equals(schemeMimeType)) {
            return "cenc";
        }
        return schemeMimeType;
    }
}
