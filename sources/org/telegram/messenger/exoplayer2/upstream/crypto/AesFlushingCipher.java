package org.telegram.messenger.exoplayer2.upstream.crypto;

import java.nio.ByteBuffer;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class AesFlushingCipher {
    private final int blockSize;
    private final Cipher cipher;
    private final byte[] flushedBlock;
    private int pendingXorBytes;
    private final byte[] zerosBlock;

    public AesFlushingCipher(int i, byte[] bArr, long j, long j2) {
        try {
            this.cipher = Cipher.getInstance("AES/CTR/NoPadding");
            this.blockSize = this.cipher.getBlockSize();
            this.zerosBlock = new byte[this.blockSize];
            this.flushedBlock = new byte[this.blockSize];
            long j3 = j2 / ((long) this.blockSize);
            j2 = (int) (j2 % ((long) this.blockSize));
            this.cipher.init(i, new SecretKeySpec(bArr, this.cipher.getAlgorithm().split("/")[0]), new IvParameterSpec(getInitializationVector(j, j3)));
            if (j2 != null) {
                updateInPlace(new byte[j2], 0, j2);
            }
        } catch (int i2) {
            throw new RuntimeException(i2);
        }
    }

    public void updateInPlace(byte[] bArr, int i, int i2) {
        update(bArr, i, i2, bArr, i);
    }

    public void update(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        AesFlushingCipher aesFlushingCipher = this;
        int i4 = i;
        int i5 = i2;
        int i6 = i3;
        do {
            boolean z = true;
            if (aesFlushingCipher.pendingXorBytes > 0) {
                bArr2[i6] = (byte) (bArr[i4] ^ aesFlushingCipher.flushedBlock[aesFlushingCipher.blockSize - aesFlushingCipher.pendingXorBytes]);
                i6++;
                i4++;
                aesFlushingCipher.pendingXorBytes--;
                i5--;
            } else {
                int nonFlushingUpdate = nonFlushingUpdate(bArr, i4, i5, bArr2, i6);
                if (i5 != nonFlushingUpdate) {
                    i5 -= nonFlushingUpdate;
                    int i7 = 0;
                    Assertions.checkState(i5 < aesFlushingCipher.blockSize);
                    i6 += nonFlushingUpdate;
                    aesFlushingCipher.pendingXorBytes = aesFlushingCipher.blockSize - i5;
                    if (nonFlushingUpdate(aesFlushingCipher.zerosBlock, 0, aesFlushingCipher.pendingXorBytes, aesFlushingCipher.flushedBlock, 0) != aesFlushingCipher.blockSize) {
                        z = false;
                    }
                    Assertions.checkState(z);
                    while (i7 < i5) {
                        nonFlushingUpdate = i6 + 1;
                        bArr2[i6] = aesFlushingCipher.flushedBlock[i7];
                        i7++;
                        i6 = nonFlushingUpdate;
                    }
                    return;
                }
                return;
            }
        } while (i5 != 0);
    }

    private int nonFlushingUpdate(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        try {
            return this.cipher.update(bArr, i, i2, bArr2, i3);
        } catch (byte[] bArr3) {
            throw new RuntimeException(bArr3);
        }
    }

    private byte[] getInitializationVector(long j, long j2) {
        return ByteBuffer.allocate(16).putLong(j).putLong(j2).array();
    }
}
