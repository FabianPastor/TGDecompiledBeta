package org.telegram.messenger.exoplayer2.extractor.mp4;

public final class TrackEncryptionBox {
    public final int initializationVectorSize;
    public final boolean isEncrypted;
    public final byte[] keyId;

    public TrackEncryptionBox(boolean isEncrypted, int initializationVectorSize, byte[] keyId) {
        this.isEncrypted = isEncrypted;
        this.initializationVectorSize = initializationVectorSize;
        this.keyId = keyId;
    }
}
