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
    private int currentMode;
    private int fileOffset;
    private byte[] iv;
    private byte[] key;

    public EncryptedFileInputStream(File file, File keyFile) throws Exception {
        super(file);
        this.key = new byte[32];
        this.iv = new byte[16];
        this.currentMode = 0;
        RandomAccessFile randomAccessFile = new RandomAccessFile(keyFile, "r");
        randomAccessFile.read(this.key, 0, 32);
        randomAccessFile.read(this.iv, 0, 16);
        randomAccessFile.close();
    }

    public EncryptedFileInputStream(File file, SecureDocumentKey secureDocumentKey) throws Exception {
        super(file);
        this.key = new byte[32];
        this.iv = new byte[16];
        this.currentMode = 1;
        System.arraycopy(secureDocumentKey.file_key, 0, this.key, 0, this.key.length);
        System.arraycopy(secureDocumentKey.file_iv, 0, this.iv, 0, this.iv.length);
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
        if (this.currentMode == 1) {
            Utilities.aesCbcEncryptionByteArraySafe(b, this.key, this.iv, off, len, this.fileOffset, 0);
        } else if (this.currentMode == 0) {
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
        byte[] key = new byte[32];
        byte[] iv = new byte[16];
        RandomAccessFile randomAccessFile = new RandomAccessFile(keyFile, "r");
        randomAccessFile.read(key, 0, 32);
        randomAccessFile.read(iv, 0, 16);
        randomAccessFile.close();
        Utilities.aesCtrDecryptionByteArray(bytes, key, iv, offset, length, 0);
    }
}
