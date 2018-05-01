package org.telegram.messenger.exoplayer2.drm;

import android.os.ConditionVariable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Pair;
import java.util.HashMap;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.drm.DefaultDrmSessionManager.EventListener;
import org.telegram.messenger.exoplayer2.drm.DrmSession.DrmSessionException;
import org.telegram.messenger.exoplayer2.upstream.HttpDataSource.Factory;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class OfflineLicenseHelper<T extends ExoMediaCrypto> {
    private final ConditionVariable conditionVariable;
    private final DefaultDrmSessionManager<T> drmSessionManager;
    private final HandlerThread handlerThread = new HandlerThread("OfflineLicenseHelper");

    /* renamed from: org.telegram.messenger.exoplayer2.drm.OfflineLicenseHelper$1 */
    class C18341 implements EventListener {
        C18341() {
        }

        public void onDrmKeysLoaded() {
            OfflineLicenseHelper.this.conditionVariable.open();
        }

        public void onDrmSessionManagerError(Exception exception) {
            OfflineLicenseHelper.this.conditionVariable.open();
        }

        public void onDrmKeysRestored() {
            OfflineLicenseHelper.this.conditionVariable.open();
        }

        public void onDrmKeysRemoved() {
            OfflineLicenseHelper.this.conditionVariable.open();
        }
    }

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String str, Factory factory) throws UnsupportedDrmException {
        return newWidevineInstance(str, false, factory, null);
    }

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String str, boolean z, Factory factory) throws UnsupportedDrmException {
        return newWidevineInstance(str, z, factory, null);
    }

    public static OfflineLicenseHelper<FrameworkMediaCrypto> newWidevineInstance(String str, boolean z, Factory factory, HashMap<String, String> hashMap) throws UnsupportedDrmException {
        return new OfflineLicenseHelper(C0542C.WIDEVINE_UUID, FrameworkMediaDrm.newInstance(C0542C.WIDEVINE_UUID), new HttpMediaDrmCallback(str, z, factory), hashMap);
    }

    public OfflineLicenseHelper(UUID uuid, ExoMediaDrm<T> exoMediaDrm, MediaDrmCallback mediaDrmCallback, HashMap<String, String> hashMap) {
        this.handlerThread.start();
        this.conditionVariable = new ConditionVariable();
        UUID uuid2 = uuid;
        ExoMediaDrm<T> exoMediaDrm2 = exoMediaDrm;
        MediaDrmCallback mediaDrmCallback2 = mediaDrmCallback;
        HashMap<String, String> hashMap2 = hashMap;
        this.drmSessionManager = new DefaultDrmSessionManager(uuid2, exoMediaDrm2, mediaDrmCallback2, hashMap2, new Handler(this.handlerThread.getLooper()), new C18341());
    }

    public synchronized byte[] getPropertyByteArray(String str) {
        return this.drmSessionManager.getPropertyByteArray(str);
    }

    public synchronized void setPropertyByteArray(String str, byte[] bArr) {
        this.drmSessionManager.setPropertyByteArray(str, bArr);
    }

    public synchronized String getPropertyString(String str) {
        return this.drmSessionManager.getPropertyString(str);
    }

    public synchronized void setPropertyString(String str, String str2) {
        this.drmSessionManager.setPropertyString(str, str2);
    }

    public synchronized byte[] downloadLicense(DrmInitData drmInitData) throws DrmSessionException {
        Assertions.checkArgument(drmInitData != null);
        return blockingKeyRequest(2, null, drmInitData);
    }

    public synchronized byte[] renewLicense(byte[] bArr) throws DrmSessionException {
        Assertions.checkNotNull(bArr);
        return blockingKeyRequest(2, bArr, null);
    }

    public synchronized void releaseLicense(byte[] bArr) throws DrmSessionException {
        Assertions.checkNotNull(bArr);
        blockingKeyRequest(3, bArr, null);
    }

    public synchronized Pair<Long, Long> getLicenseDurationRemainingSec(byte[] bArr) throws DrmSessionException {
        Assertions.checkNotNull(bArr);
        bArr = openBlockingKeyRequest(1, bArr, null);
        DrmSessionException error = bArr.getError();
        Pair<Long, Long> licenseDurationRemainingSec = WidevineUtil.getLicenseDurationRemainingSec(bArr);
        this.drmSessionManager.releaseSession(bArr);
        if (error == null) {
            return licenseDurationRemainingSec;
        }
        if ((error.getCause() instanceof KeysExpiredException) != null) {
            return Pair.create(Long.valueOf(0), Long.valueOf(0));
        }
        throw error;
    }

    public void release() {
        this.handlerThread.quit();
    }

    private byte[] blockingKeyRequest(int i, byte[] bArr, DrmInitData drmInitData) throws DrmSessionException {
        i = openBlockingKeyRequest(i, bArr, drmInitData);
        bArr = i.getError();
        drmInitData = i.getOfflineLicenseKeySetId();
        this.drmSessionManager.releaseSession(i);
        if (bArr == null) {
            return drmInitData;
        }
        throw bArr;
    }

    private DrmSession<T> openBlockingKeyRequest(int i, byte[] bArr, DrmInitData drmInitData) {
        this.drmSessionManager.setMode(i, bArr);
        this.conditionVariable.close();
        i = this.drmSessionManager.acquireSession(this.handlerThread.getLooper(), drmInitData);
        this.conditionVariable.block();
        return i;
    }
}
