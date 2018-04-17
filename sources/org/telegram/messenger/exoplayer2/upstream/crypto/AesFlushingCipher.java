package org.telegram.messenger.exoplayer2.upstream.crypto;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class AesFlushingCipher {
    private final int blockSize;
    private final Cipher cipher;
    private final byte[] flushedBlock;
    private int pendingXorBytes;
    private final byte[] zerosBlock;

    public AesFlushingCipher(int mode, byte[] secretKey, long nonce, long offset) {
        try {
            this.cipher = Cipher.getInstance("AES/CTR/NoPadding");
            this.blockSize = this.cipher.getBlockSize();
            this.zerosBlock = new byte[this.blockSize];
            this.flushedBlock = new byte[this.blockSize];
            int startPadding = (int) (offset % ((long) this.blockSize));
            this.cipher.init(mode, new SecretKeySpec(secretKey, this.cipher.getAlgorithm().split("/")[0]), new IvParameterSpec(getInitializationVector(nonce, offset / ((long) this.blockSize))));
            if (startPadding != 0) {
                updateInPlace(new byte[startPadding], 0, startPadding);
            }
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateInPlace(byte[] data, int offset, int length) {
        update(data, offset, length, data, offset);
    }

    public void update(byte[] in, int inOffset, int length, byte[] out, int outOffset) {
        AesFlushingCipher aesFlushingCipher = this;
        int inOffset2 = inOffset;
        int length2 = length;
        int outOffset2 = outOffset;
        do {
            boolean z = true;
            if (aesFlushingCipher.pendingXorBytes > 0) {
                out[outOffset2] = (byte) (in[inOffset2] ^ aesFlushingCipher.flushedBlock[aesFlushingCipher.blockSize - aesFlushingCipher.pendingXorBytes]);
                outOffset2++;
                inOffset2++;
                aesFlushingCipher.pendingXorBytes--;
                length2--;
            } else {
                int written = nonFlushingUpdate(in, inOffset2, length2, out, outOffset2);
                if (length2 != written) {
                    int bytesToFlush = length2 - written;
                    int i = 0;
                    Assertions.checkState(bytesToFlush < aesFlushingCipher.blockSize);
                    outOffset2 += written;
                    aesFlushingCipher.pendingXorBytes = aesFlushingCipher.blockSize - bytesToFlush;
                    if (nonFlushingUpdate(aesFlushingCipher.zerosBlock, 0, aesFlushingCipher.pendingXorBytes, aesFlushingCipher.flushedBlock, 0) != aesFlushingCipher.blockSize) {
                        z = false;
                    }
                    Assertions.checkState(z);
                    while (true) {
                        int i2 = i;
                        if (i2 < bytesToFlush) {
                            int outOffset3 = outOffset2 + 1;
                            out[outOffset2] = aesFlushingCipher.flushedBlock[i2];
                            i = i2 + 1;
                            outOffset2 = outOffset3;
                        } else {
                            return;
                        }
                    }
                }
                return;
            }
        } while (length2 != 0);
    }

    private int nonFlushingUpdate(byte[] in, int inOffset, int length, byte[] out, int outOffset) {
        try {
            return this.cipher.update(in, inOffset, length, out, outOffset);
        } catch (ShortBufferException e) {
            throw new RuntimeException(e);
        }
    }

    private byte[] getInitializationVector(long nonce, long counter) {
        return ByteBuffer.allocate(16).putLong(nonce).putLong(counter).array();
    }
}
