package org.telegram.messenger.exoplayer2.extractor;

import java.io.IOException;
import java.util.Arrays;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public interface TrackOutput {

    public static final class CryptoData {
        public final int clearBlocks;
        public final int cryptoMode;
        public final int encryptedBlocks;
        public final byte[] encryptionKey;

        public CryptoData(int cryptoMode, byte[] encryptionKey, int encryptedBlocks, int clearBlocks) {
            this.cryptoMode = cryptoMode;
            this.encryptionKey = encryptionKey;
            this.encryptedBlocks = encryptedBlocks;
            this.clearBlocks = clearBlocks;
        }

        public boolean equals(Object obj) {
            boolean z = true;
            if (this == obj) {
                return true;
            }
            if (obj != null) {
                if (getClass() == obj.getClass()) {
                    CryptoData other = (CryptoData) obj;
                    if (this.cryptoMode != other.cryptoMode || this.encryptedBlocks != other.encryptedBlocks || this.clearBlocks != other.clearBlocks || !Arrays.equals(this.encryptionKey, other.encryptionKey)) {
                        z = false;
                    }
                    return z;
                }
            }
            return false;
        }

        public int hashCode() {
            return (31 * ((31 * ((31 * this.cryptoMode) + Arrays.hashCode(this.encryptionKey))) + this.encryptedBlocks)) + this.clearBlocks;
        }
    }

    void format(Format format);

    int sampleData(ExtractorInput extractorInput, int i, boolean z) throws IOException, InterruptedException;

    void sampleData(ParsableByteArray parsableByteArray, int i);

    void sampleMetadata(long j, int i, int i2, int i3, CryptoData cryptoData);
}
