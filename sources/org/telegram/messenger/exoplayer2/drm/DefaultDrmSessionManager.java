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
import org.telegram.messenger.exoplayer2.C0539C;
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
            byte[] sessionId = msg.obj;
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
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Media does not support uuid: ");
            stringBuilder.append(uuid);
            super(stringBuilder.toString());
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
        return newFrameworkInstance(C0539C.WIDEVINE_UUID, callback, optionalKeyRequestParameters, eventHandler, eventListener);
    }

    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newPlayReadyInstance(MediaDrmCallback callback, String customData, Handler eventHandler, EventListener eventListener) throws UnsupportedDrmException {
        HashMap<String, String> optionalKeyRequestParameters;
        if (TextUtils.isEmpty(customData)) {
            optionalKeyRequestParameters = null;
        } else {
            optionalKeyRequestParameters = new HashMap();
            optionalKeyRequestParameters.put(PLAYREADY_CUSTOM_DATA_KEY, customData);
        }
        return newFrameworkInstance(C0539C.PLAYREADY_UUID, callback, optionalKeyRequestParameters, eventHandler, eventListener);
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
        Assertions.checkArgument(C0539C.COMMON_PSSH_UUID.equals(uuid) ^ 1, "Use C.CLEARKEY_UUID instead");
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
        boolean z = true;
        if (this.offlineLicenseKeySetId != null) {
            return true;
        }
        String str;
        if (getSchemeData(drmInitData, this.uuid, true) == null) {
            if (drmInitData.schemeDataCount != 1 || !drmInitData.get(0).matches(C0539C.COMMON_PSSH_UUID)) {
                return false;
            }
            str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DrmInitData only contains common PSSH SchemeData. Assuming support for: ");
            stringBuilder.append(this.uuid);
            Log.w(str, stringBuilder.toString());
        }
        str = drmInitData.schemeType;
        if (str != null) {
            if (!"cenc".equals(str)) {
                if (!(C0539C.CENC_TYPE_cbc1.equals(str) || C0539C.CENC_TYPE_cbcs.equals(str))) {
                    if (!C0539C.CENC_TYPE_cens.equals(str)) {
                        return true;
                    }
                }
                if (Util.SDK_INT < 24) {
                    z = false;
                }
                return z;
            }
        }
        return true;
    }

    public DrmSession<T> acquireSession(Looper playbackLooper, DrmInitData drmInitData) {
        boolean z;
        byte[] initData;
        String mimeType;
        C05631 c05631;
        SchemeData data;
        final MissingSchemeDataException error;
        byte[] initData2;
        String mimeType2;
        DefaultDrmSession<T> session;
        DefaultDrmSession<T> session2;
        Looper looper = playbackLooper;
        if (this.playbackLooper != null) {
            if (r14.playbackLooper != looper) {
                z = false;
                Assertions.checkState(z);
                if (r14.sessions.isEmpty()) {
                    r14.playbackLooper = looper;
                    if (r14.mediaDrmHandler == null) {
                        r14.mediaDrmHandler = new MediaDrmHandler(looper);
                    }
                }
                initData = null;
                mimeType = null;
                c05631 = null;
                if (r14.offlineLicenseKeySetId != null) {
                    data = getSchemeData(drmInitData, r14.uuid, false);
                    if (data != null) {
                        error = new MissingSchemeDataException(r14.uuid);
                        if (!(r14.eventHandler == null || r14.eventListener == null)) {
                            r14.eventHandler.post(new Runnable() {
                                public void run() {
                                    DefaultDrmSessionManager.this.eventListener.onDrmSessionManagerError(error);
                                }
                            });
                        }
                        return new ErrorStateDrmSession(new DrmSessionException(error));
                    }
                    initData = getSchemeInitData(data, r14.uuid);
                    mimeType = getSchemeMimeType(data, r14.uuid);
                } else {
                    DrmInitData drmInitData2 = drmInitData;
                }
                initData2 = initData;
                mimeType2 = mimeType;
                if (r14.multiSession) {
                    if (r14.sessions.isEmpty()) {
                        c05631 = (DefaultDrmSession) r14.sessions.get(0);
                    }
                    session = c05631;
                } else {
                    session = null;
                    for (DefaultDrmSession<T> existingSession : r14.sessions) {
                        if (existingSession.hasInitData(initData2)) {
                            session = existingSession;
                            break;
                        }
                    }
                }
                session2 = session;
                if (session2 != null) {
                    UUID uuid = r14.uuid;
                    ExoMediaDrm exoMediaDrm = r14.mediaDrm;
                    int i = r14.mode;
                    byte[] bArr = r14.offlineLicenseKeySetId;
                    HashMap hashMap = r14.optionalKeyRequestParameters;
                    MediaDrmCallback mediaDrmCallback = r14.callback;
                    Handler handler = r14.eventHandler;
                    EventListener eventListener = r14.eventListener;
                    EventListener eventListener2 = eventListener;
                    session = new DefaultDrmSession(uuid, exoMediaDrm, r14, initData2, mimeType2, i, bArr, hashMap, mediaDrmCallback, looper, handler, eventListener2, r14.initialDrmRequestRetryCount);
                    r14.sessions.add(session);
                } else {
                    session = session2;
                }
                session.acquire();
                return session;
            }
        }
        z = true;
        Assertions.checkState(z);
        if (r14.sessions.isEmpty()) {
            r14.playbackLooper = looper;
            if (r14.mediaDrmHandler == null) {
                r14.mediaDrmHandler = new MediaDrmHandler(looper);
            }
        }
        initData = null;
        mimeType = null;
        c05631 = null;
        if (r14.offlineLicenseKeySetId != null) {
            DrmInitData drmInitData22 = drmInitData;
        } else {
            data = getSchemeData(drmInitData, r14.uuid, false);
            if (data != null) {
                initData = getSchemeInitData(data, r14.uuid);
                mimeType = getSchemeMimeType(data, r14.uuid);
            } else {
                error = new MissingSchemeDataException(r14.uuid);
                r14.eventHandler.post(/* anonymous class already generated */);
                return new ErrorStateDrmSession(new DrmSessionException(error));
            }
        }
        initData2 = initData;
        mimeType2 = mimeType;
        if (r14.multiSession) {
            session = null;
            for (DefaultDrmSession<T> existingSession2 : r14.sessions) {
                if (existingSession2.hasInitData(initData2)) {
                    session = existingSession2;
                    break;
                }
            }
        } else {
            if (r14.sessions.isEmpty()) {
                c05631 = (DefaultDrmSession) r14.sessions.get(0);
            }
            session = c05631;
        }
        session2 = session;
        if (session2 != null) {
            session = session2;
        } else {
            UUID uuid2 = r14.uuid;
            ExoMediaDrm exoMediaDrm2 = r14.mediaDrm;
            int i2 = r14.mode;
            byte[] bArr2 = r14.offlineLicenseKeySetId;
            HashMap hashMap2 = r14.optionalKeyRequestParameters;
            MediaDrmCallback mediaDrmCallback2 = r14.callback;
            Handler handler2 = r14.eventHandler;
            EventListener eventListener3 = r14.eventListener;
            EventListener eventListener22 = eventListener3;
            session = new DefaultDrmSession(uuid2, exoMediaDrm2, r14, initData2, mimeType2, i2, bArr2, hashMap2, mediaDrmCallback2, looper, handler2, eventListener22, r14.initialDrmRequestRetryCount);
            r14.sessions.add(session);
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
        List<SchemeData> matchingSchemeDatas = new ArrayList(drmInitData.schemeDataCount);
        int i = 0;
        while (true) {
            boolean uuidMatches = true;
            if (i >= drmInitData.schemeDataCount) {
                break;
            }
            SchemeData schemeData = drmInitData.get(i);
            if (!schemeData.matches(uuid)) {
                if (!C0539C.CLEARKEY_UUID.equals(uuid) || !schemeData.matches(C0539C.COMMON_PSSH_UUID)) {
                    uuidMatches = false;
                }
            }
            if (uuidMatches && (schemeData.data != null || allowMissingData)) {
                matchingSchemeDatas.add(schemeData);
            }
            i++;
        }
        if (matchingSchemeDatas.isEmpty()) {
            return null;
        }
        if (C0539C.WIDEVINE_UUID.equals(uuid)) {
            for (i = 0; i < matchingSchemeDatas.size(); i++) {
                schemeData = (SchemeData) matchingSchemeDatas.get(i);
                int version = schemeData.hasData() ? PsshAtomUtil.parseVersion(schemeData.data) : -1;
                if (Util.SDK_INT < 23 && version == 0) {
                    return schemeData;
                }
                if (Util.SDK_INT >= 23 && version == 1) {
                    return schemeData;
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
        if (Util.SDK_INT >= 26 || !C0539C.CLEARKEY_UUID.equals(uuid)) {
            return schemeMimeType;
        }
        if (MimeTypes.VIDEO_MP4.equals(schemeMimeType) || MimeTypes.AUDIO_MP4.equals(schemeMimeType)) {
            return "cenc";
        }
        return schemeMimeType;
    }
}
