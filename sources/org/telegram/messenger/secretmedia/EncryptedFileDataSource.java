package org.telegram.messenger.secretmedia;

import android.net.Uri;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.TransferListener;

public final class EncryptedFileDataSource implements DataSource {
    private long bytesRemaining;
    private RandomAccessFile file;
    private int fileOffset;
    private byte[] iv;
    private byte[] key;
    private final TransferListener<? super EncryptedFileDataSource> listener;
    private boolean opened;
    private Uri uri;

    public static class EncryptedFileDataSourceException extends IOException {
        public EncryptedFileDataSourceException(IOException iOException) {
            super(iOException);
        }
    }

    public EncryptedFileDataSource() {
        this(null);
    }

    public EncryptedFileDataSource(TransferListener<? super EncryptedFileDataSource> transferListener) {
        this.key = new byte[32];
        this.iv = new byte[16];
        this.listener = transferListener;
    }

    public long open(DataSpec dataSpec) throws EncryptedFileDataSourceException {
        try {
            this.uri = dataSpec.uri;
            File file = new File(dataSpec.uri.getPath());
            String name = file.getName();
            File internalCacheDir = FileLoader.getInternalCacheDir();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name);
            stringBuilder.append(".key");
            RandomAccessFile randomAccessFile = new RandomAccessFile(new File(internalCacheDir, stringBuilder.toString()), "r");
            randomAccessFile.read(this.key);
            randomAccessFile.read(this.iv);
            randomAccessFile.close();
            this.file = new RandomAccessFile(file, "r");
            this.file.seek(dataSpec.position);
            this.fileOffset = (int) dataSpec.position;
            this.bytesRemaining = dataSpec.length == -1 ? this.file.length() - dataSpec.position : dataSpec.length;
            if (this.bytesRemaining < 0) {
                throw new EOFException();
            }
            this.opened = true;
            if (this.listener != null) {
                this.listener.onTransferStart(this, dataSpec);
            }
            return this.bytesRemaining;
        } catch (DataSpec dataSpec2) {
            throw new EncryptedFileDataSourceException(dataSpec2);
        }
    }

    public int read(byte[] bArr, int i, int i2) throws EncryptedFileDataSourceException {
        if (i2 == 0) {
            return null;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            i2 = this.file.read(bArr, i, (int) Math.min(this.bytesRemaining, (long) i2));
            Utilities.aesCtrDecryptionByteArray(bArr, this.key, this.iv, i, i2, this.fileOffset);
            this.fileOffset += i2;
            if (i2 > 0) {
                this.bytesRemaining -= (long) i2;
                if (this.listener != null) {
                    this.listener.onBytesTransferred(this, i2);
                }
            }
            return i2;
        } catch (byte[] bArr2) {
            throw new EncryptedFileDataSourceException(bArr2);
        }
    }

    public Uri getUri() {
        return this.uri;
    }

    public void close() throws EncryptedFileDataSourceException {
        this.uri = null;
        this.fileOffset = 0;
        try {
            if (this.file != null) {
                this.file.close();
            }
            this.file = null;
            if (this.opened) {
                this.opened = false;
                if (this.listener != null) {
                    this.listener.onTransferEnd(this);
                }
            }
        } catch (IOException e) {
            throw new EncryptedFileDataSourceException(e);
        } catch (Throwable th) {
            this.file = null;
            if (this.opened) {
                this.opened = false;
                if (this.listener != null) {
                    this.listener.onTransferEnd(this);
                }
            }
        }
    }
}
