package org.telegram.messenger.exoplayer2.drm;

import java.io.IOException;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.KeyRequest;
import org.telegram.messenger.exoplayer2.drm.ExoMediaDrm.ProvisionRequest;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class LocalMediaDrmCallback implements MediaDrmCallback {
    private final byte[] keyResponse;

    public LocalMediaDrmCallback(byte[] bArr) {
        this.keyResponse = (byte[]) Assertions.checkNotNull(bArr);
    }

    public byte[] executeProvisionRequest(UUID uuid, ProvisionRequest provisionRequest) throws IOException {
        throw new UnsupportedOperationException();
    }

    public byte[] executeKeyRequest(UUID uuid, KeyRequest keyRequest) throws Exception {
        return this.keyResponse;
    }
}
