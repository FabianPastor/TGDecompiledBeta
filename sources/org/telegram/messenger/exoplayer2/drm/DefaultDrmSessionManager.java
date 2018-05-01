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
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.drm.DefaultDrmSession.ProvisioningManager;
import org.telegram.messenger.exoplayer2.drm.DrmInitData.SchemeData;
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

        public void handleMessage(Message message) {
            byte[] bArr = (byte[]) message.obj;
            for (DefaultDrmSession defaultDrmSession : DefaultDrmSessionManager.this.sessions) {
                if (defaultDrmSession.hasSessionId(bArr)) {
                    defaultDrmSession.onMediaDrmEvent(message.what);
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

        public void onEvent(ExoMediaDrm<? extends T> exoMediaDrm, byte[] bArr, int i, int i2, byte[] bArr2) {
            if (DefaultDrmSessionManager.this.mode == null) {
                DefaultDrmSessionManager.this.mediaDrmHandler.obtainMessage(i, bArr).sendToTarget();
            }
        }
    }

    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newWidevineInstance(MediaDrmCallback mediaDrmCallback, HashMap<String, String> hashMap, Handler handler, EventListener eventListener) throws UnsupportedDrmException {
        return newFrameworkInstance(C0542C.WIDEVINE_UUID, mediaDrmCallback, hashMap, handler, eventListener);
    }

    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newPlayReadyInstance(MediaDrmCallback mediaDrmCallback, String str, Handler handler, EventListener eventListener) throws UnsupportedDrmException {
        HashMap hashMap;
        if (TextUtils.isEmpty(str)) {
            hashMap = null;
        } else {
            hashMap = new HashMap();
            hashMap.put(PLAYREADY_CUSTOM_DATA_KEY, str);
        }
        return newFrameworkInstance(C0542C.PLAYREADY_UUID, mediaDrmCallback, hashMap, handler, eventListener);
    }

    public static DefaultDrmSessionManager<FrameworkMediaCrypto> newFrameworkInstance(UUID uuid, MediaDrmCallback mediaDrmCallback, HashMap<String, String> hashMap, Handler handler, EventListener eventListener) throws UnsupportedDrmException {
        return new DefaultDrmSessionManager(uuid, FrameworkMediaDrm.newInstance(uuid), mediaDrmCallback, hashMap, handler, eventListener, false, 3);
    }

    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> exoMediaDrm, MediaDrmCallback mediaDrmCallback, HashMap<String, String> hashMap, Handler handler, EventListener eventListener) {
        this(uuid, exoMediaDrm, mediaDrmCallback, hashMap, handler, eventListener, false, 3);
    }

    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> exoMediaDrm, MediaDrmCallback mediaDrmCallback, HashMap<String, String> hashMap, Handler handler, EventListener eventListener, boolean z) {
        this(uuid, exoMediaDrm, mediaDrmCallback, hashMap, handler, eventListener, z, 3);
    }

    public DefaultDrmSessionManager(UUID uuid, ExoMediaDrm<T> exoMediaDrm, MediaDrmCallback mediaDrmCallback, HashMap<String, String> hashMap, Handler handler, EventListener eventListener, boolean z, int i) {
        Assertions.checkNotNull(uuid);
        Assertions.checkNotNull(exoMediaDrm);
        Assertions.checkArgument(C0542C.COMMON_PSSH_UUID.equals(uuid) ^ 1, "Use C.CLEARKEY_UUID instead");
        this.uuid = uuid;
        this.mediaDrm = exoMediaDrm;
        this.callback = mediaDrmCallback;
        this.optionalKeyRequestParameters = hashMap;
        this.eventHandler = handler;
        this.eventListener = eventListener;
        this.multiSession = z;
        this.initialDrmRequestRetryCount = i;
        this.mode = null;
        this.sessions = new ArrayList();
        this.provisioningSessions = new ArrayList();
        if (z) {
            exoMediaDrm.setPropertyString("sessionSharing", "enable");
        }
        exoMediaDrm.setOnEventListener(new MediaDrmEventListener());
    }

    public final String getPropertyString(String str) {
        return this.mediaDrm.getPropertyString(str);
    }

    public final void setPropertyString(String str, String str2) {
        this.mediaDrm.setPropertyString(str, str2);
    }

    public final byte[] getPropertyByteArray(String str) {
        return this.mediaDrm.getPropertyByteArray(str);
    }

    public final void setPropertyByteArray(String str, byte[] bArr) {
        this.mediaDrm.setPropertyByteArray(str, bArr);
    }

    public void setMode(int i, byte[] bArr) {
        Assertions.checkState(this.sessions.isEmpty());
        if (i == 1 || i == 3) {
            Assertions.checkNotNull(bArr);
        }
        this.mode = i;
        this.offlineLicenseKeySetId = bArr;
    }

    public boolean canAcquireSession(DrmInitData drmInitData) {
        boolean z = true;
        if (this.offlineLicenseKeySetId != null) {
            return true;
        }
        if (getSchemeData(drmInitData, this.uuid, true) == null) {
            if (drmInitData.schemeDataCount != 1 || !drmInitData.get(0).matches(C0542C.COMMON_PSSH_UUID)) {
                return false;
            }
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("DrmInitData only contains common PSSH SchemeData. Assuming support for: ");
            stringBuilder.append(this.uuid);
            Log.w(str, stringBuilder.toString());
        }
        drmInitData = drmInitData.schemeType;
        if (drmInitData != null) {
            if (!"cenc".equals(drmInitData)) {
                if (!(C0542C.CENC_TYPE_cbc1.equals(drmInitData) || C0542C.CENC_TYPE_cbcs.equals(drmInitData))) {
                    if (C0542C.CENC_TYPE_cens.equals(drmInitData) == null) {
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

    public org.telegram.messenger.exoplayer2.drm.DrmSession<T> acquireSession(android.os.Looper r17, org.telegram.messenger.exoplayer2.drm.DrmInitData r18) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r15_0 org.telegram.messenger.exoplayer2.drm.DrmSession) in PHI: PHI: (r15_2 org.telegram.messenger.exoplayer2.drm.DrmSession) = (r15_0 org.telegram.messenger.exoplayer2.drm.DrmSession), (r15_1 org.telegram.messenger.exoplayer2.drm.DrmSession) binds: {(r15_0 org.telegram.messenger.exoplayer2.drm.DrmSession)=B:38:0x009f, (r15_1 org.telegram.messenger.exoplayer2.drm.DrmSession)=B:39:0x00be}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:60)
	at jadx.core.ProcessClass.process(ProcessClass.java:39)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r16 = this;
        r14 = r16;
        r10 = r17;
        r0 = r14.playbackLooper;
        r1 = 0;
        if (r0 == 0) goto L_0x0010;
    L_0x0009:
        r0 = r14.playbackLooper;
        if (r0 != r10) goto L_0x000e;
    L_0x000d:
        goto L_0x0010;
    L_0x000e:
        r0 = r1;
        goto L_0x0011;
    L_0x0010:
        r0 = 1;
    L_0x0011:
        org.telegram.messenger.exoplayer2.util.Assertions.checkState(r0);
        r0 = r14.sessions;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x0029;
    L_0x001c:
        r14.playbackLooper = r10;
        r0 = r14.mediaDrmHandler;
        if (r0 != 0) goto L_0x0029;
    L_0x0022:
        r0 = new org.telegram.messenger.exoplayer2.drm.DefaultDrmSessionManager$MediaDrmHandler;
        r0.<init>(r10);
        r14.mediaDrmHandler = r0;
    L_0x0029:
        r0 = r14.offlineLicenseKeySetId;
        r2 = 0;
        if (r0 != 0) goto L_0x006b;
    L_0x002e:
        r0 = r14.uuid;
        r3 = r18;
        r0 = getSchemeData(r3, r0, r1);
        if (r0 != 0) goto L_0x005c;
    L_0x0038:
        r0 = new org.telegram.messenger.exoplayer2.drm.DefaultDrmSessionManager$MissingSchemeDataException;
        r1 = r14.uuid;
        r0.<init>(r1);
        r1 = r14.eventHandler;
        if (r1 == 0) goto L_0x0051;
    L_0x0043:
        r1 = r14.eventListener;
        if (r1 == 0) goto L_0x0051;
    L_0x0047:
        r1 = r14.eventHandler;
        r2 = new org.telegram.messenger.exoplayer2.drm.DefaultDrmSessionManager$1;
        r2.<init>(r0);
        r1.post(r2);
    L_0x0051:
        r1 = new org.telegram.messenger.exoplayer2.drm.ErrorStateDrmSession;
        r2 = new org.telegram.messenger.exoplayer2.drm.DrmSession$DrmSessionException;
        r2.<init>(r0);
        r1.<init>(r2);
        return r1;
    L_0x005c:
        r3 = r14.uuid;
        r3 = getSchemeInitData(r0, r3);
        r4 = r14.uuid;
        r0 = getSchemeMimeType(r0, r4);
        r5 = r0;
        r4 = r3;
        goto L_0x006d;
    L_0x006b:
        r4 = r2;
        r5 = r4;
    L_0x006d:
        r0 = r14.multiSession;
        if (r0 != 0) goto L_0x0084;
    L_0x0071:
        r0 = r14.sessions;
        r0 = r0.isEmpty();
        if (r0 == 0) goto L_0x007a;
    L_0x0079:
        goto L_0x009d;
    L_0x007a:
        r0 = r14.sessions;
        r0 = r0.get(r1);
        r2 = r0;
        r2 = (org.telegram.messenger.exoplayer2.drm.DefaultDrmSession) r2;
        goto L_0x009d;
    L_0x0084:
        r0 = r14.sessions;
        r0 = r0.iterator();
    L_0x008a:
        r1 = r0.hasNext();
        if (r1 == 0) goto L_0x009d;
    L_0x0090:
        r1 = r0.next();
        r1 = (org.telegram.messenger.exoplayer2.drm.DefaultDrmSession) r1;
        r3 = r1.hasInitData(r4);
        if (r3 == 0) goto L_0x008a;
    L_0x009c:
        r2 = r1;
    L_0x009d:
        if (r2 != 0) goto L_0x00be;
    L_0x009f:
        r15 = new org.telegram.messenger.exoplayer2.drm.DefaultDrmSession;
        r1 = r14.uuid;
        r2 = r14.mediaDrm;
        r6 = r14.mode;
        r7 = r14.offlineLicenseKeySetId;
        r8 = r14.optionalKeyRequestParameters;
        r9 = r14.callback;
        r11 = r14.eventHandler;
        r12 = r14.eventListener;
        r13 = r14.initialDrmRequestRetryCount;
        r0 = r15;
        r3 = r14;
        r0.<init>(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13);
        r0 = r14.sessions;
        r0.add(r15);
        goto L_0x00bf;
    L_0x00be:
        r15 = r2;
    L_0x00bf:
        r15.acquire();
        return r15;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.drm.DefaultDrmSessionManager.acquireSession(android.os.Looper, org.telegram.messenger.exoplayer2.drm.DrmInitData):org.telegram.messenger.exoplayer2.drm.DrmSession<T>");
    }

    public void releaseSession(DrmSession<T> drmSession) {
        if (!(drmSession instanceof ErrorStateDrmSession)) {
            DefaultDrmSession defaultDrmSession = (DefaultDrmSession) drmSession;
            if (defaultDrmSession.release()) {
                this.sessions.remove(defaultDrmSession);
                if (this.provisioningSessions.size() > 1 && this.provisioningSessions.get(0) == defaultDrmSession) {
                    ((DefaultDrmSession) this.provisioningSessions.get(1)).provision();
                }
                this.provisioningSessions.remove(defaultDrmSession);
            }
        }
    }

    public void provisionRequired(DefaultDrmSession<T> defaultDrmSession) {
        this.provisioningSessions.add(defaultDrmSession);
        if (this.provisioningSessions.size() == 1) {
            defaultDrmSession.provision();
        }
    }

    public void onProvisionCompleted() {
        for (DefaultDrmSession onProvisionCompleted : this.provisioningSessions) {
            onProvisionCompleted.onProvisionCompleted();
        }
        this.provisioningSessions.clear();
    }

    public void onProvisionError(Exception exception) {
        for (DefaultDrmSession onProvisionError : this.provisioningSessions) {
            onProvisionError.onProvisionError(exception);
        }
        this.provisioningSessions.clear();
    }

    private static SchemeData getSchemeData(DrmInitData drmInitData, UUID uuid, boolean z) {
        List arrayList = new ArrayList(drmInitData.schemeDataCount);
        int i = 0;
        while (true) {
            int i2 = 1;
            if (i >= drmInitData.schemeDataCount) {
                break;
            }
            SchemeData schemeData = drmInitData.get(i);
            if (!schemeData.matches(uuid)) {
                if (!C0542C.CLEARKEY_UUID.equals(uuid) || !schemeData.matches(C0542C.COMMON_PSSH_UUID)) {
                    i2 = 0;
                }
            }
            if (i2 != 0 && (schemeData.data != null || z)) {
                arrayList.add(schemeData);
            }
            i++;
        }
        if (arrayList.isEmpty() != null) {
            return null;
        }
        if (C0542C.WIDEVINE_UUID.equals(uuid) != null) {
            for (drmInitData = null; drmInitData < arrayList.size(); drmInitData++) {
                SchemeData schemeData2 = (SchemeData) arrayList.get(drmInitData);
                z = schemeData2.hasData() ? PsshAtomUtil.parseVersion(schemeData2.data) : true;
                if (Util.SDK_INT < 23 && !z) {
                    return schemeData2;
                }
                if (Util.SDK_INT >= 23 && z) {
                    return schemeData2;
                }
            }
        }
        return (SchemeData) arrayList.get(0);
    }

    private static byte[] getSchemeInitData(SchemeData schemeData, UUID uuid) {
        schemeData = schemeData.data;
        if (Util.SDK_INT >= 21) {
            return schemeData;
        }
        uuid = PsshAtomUtil.parseSchemeSpecificData(schemeData, uuid);
        return uuid == null ? schemeData : uuid;
    }

    private static String getSchemeMimeType(SchemeData schemeData, UUID uuid) {
        schemeData = schemeData.mimeType;
        if (Util.SDK_INT >= 26 || C0542C.CLEARKEY_UUID.equals(uuid) == null) {
            return schemeData;
        }
        return (MimeTypes.VIDEO_MP4.equals(schemeData) == null && MimeTypes.AUDIO_MP4.equals(schemeData) == null) ? schemeData : "cenc";
    }
}
