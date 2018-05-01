package org.telegram.messenger.exoplayer2.drm;

import java.util.Map;
import org.telegram.messenger.exoplayer2.drm.DrmSession.DrmSessionException;
import org.telegram.messenger.exoplayer2.util.Assertions;

final class ErrorStateDrmSession<T extends ExoMediaCrypto> implements DrmSession<T> {
    private final DrmSessionException error;

    public ErrorStateDrmSession(DrmSessionException error) {
        this.error = (DrmSessionException) Assertions.checkNotNull(error);
    }

    public int getState() {
        return 1;
    }

    public DrmSessionException getError() {
        return this.error;
    }

    public T getMediaCrypto() {
        return null;
    }

    public Map<String, String> queryKeyStatus() {
        return null;
    }

    public byte[] getOfflineLicenseKeySetId() {
        return null;
    }
}
