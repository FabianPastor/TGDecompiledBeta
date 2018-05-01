package org.telegram.messenger.exoplayer2.drm;

import java.util.Map;
import org.telegram.messenger.exoplayer2.drm.DrmSession.DrmSessionException;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class ErrorStateDrmSession<T extends ExoMediaCrypto> implements DrmSession<T> {
    private final DrmSessionException error;

    public T getMediaCrypto() {
        return null;
    }

    public byte[] getOfflineLicenseKeySetId() {
        return null;
    }

    public int getState() {
        return 1;
    }

    public Map<String, String> queryKeyStatus() {
        return null;
    }

    public ErrorStateDrmSession(DrmSessionException drmSessionException) {
        this.error = (DrmSessionException) Assertions.checkNotNull(drmSessionException);
    }

    public DrmSessionException getError() {
        return this.error;
    }
}
