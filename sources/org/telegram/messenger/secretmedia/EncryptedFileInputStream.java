package org.telegram.messenger.secretmedia;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.telegram.messenger.SecureDocumentKey;
import org.telegram.messenger.Utilities;

public class EncryptedFileInputStream extends FileInputStream {
    private static final int MODE_CBC = 1;
    private static final int MODE_CTR = 0;
    private int currentMode = 0;
    private int fileOffset;
    private byte[] iv = new byte[16];
    private byte[] key = new byte[32];

    public EncryptedFileInputStream(File file, File keyFile) throws Exception {
        super(file);
        RandomAccessFile randomAccessFile = new RandomAccessFile(keyFile, "r");
        randomAccessFile.read(this.key, 0, 32);
        randomAccessFile.read(this.iv, 0, 16);
        randomAccessFile.close();
    }

    public EncryptedFileInputStream(File file, SecureDocumentKey secureDocumentKey) throws Exception {
        super(file);
        byte[] bArr = secureDocumentKey.file_key;
        byte[] bArr2 = this.key;
        System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
        byte[] bArr3 = secureDocumentKey.file_iv;
        byte[] bArr4 = this.iv;
        System.arraycopy(bArr3, 0, bArr4, 0, bArr4.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        if (this.currentMode == 1 && this.fileOffset == 0) {
            byte[] temp = new byte[32];
            super.read(temp, 0, 32);
            Utilities.aesCbcEncryptionByteArraySafe(b, this.key, this.iv, off, len, this.fileOffset, 0);
            this.fileOffset += 32;
            skip((long) ((temp[0] & 255) - 32));
        }
        int result = super.read(b, off, len);
        int i = this.currentMode;
        if (i == 1) {
            Utilities.aesCbcEncryptionByteArraySafe(b, this.key, this.iv, off, len, this.fileOffset, 0);
        } else if (i == 0) {
            Utilities.aesCtrDecryptionByteArray(b, this.key, this.iv, off, len, this.fileOffset);
        }
        this.fileOffset += len;
        return result;
    }

    public long skip(long n) throws IOException {
        this.fileOffset = (int) (((long) this.fileOffset) + n);
        return super.skip(n);
    }

    public static void decryptBytesWithKeyFile(byte[] bytes, int offset, int length, SecureDocumentKey secureDocumentKey) {
        Utilities.aesCbcEncryptionByteArraySafe(bytes, secureDocumentKey.file_key, secureDocumentKey.file_iv, offset, length, 0, 0);
    }

    public static void decryptBytesWithKeyFile(byte[] bytes, int offset, int length, File keyFile) throws Exception {
        byte[] key2 = new byte[32];
        byte[] iv2 = new byte[16];
        RandomAccessFile randomAccessFile = new RandomAccessFile(keyFile, "r");
        randomAccessFile.read(key2, 0, 32);
        randomAccessFile.read(iv2, 0, 16);
        randomAccessFile.close();
        Utilities.aesCtrDecryptionByteArray(bytes, key2, iv2, offset, length, 0);
    }
}
