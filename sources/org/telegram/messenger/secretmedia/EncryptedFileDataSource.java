package org.telegram.messenger.secretmedia;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.BaseDataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.Utilities;

public final class EncryptedFileDataSource extends BaseDataSource {
    private long bytesRemaining;
    private RandomAccessFile file;
    private int fileOffset;
    private byte[] iv;
    private byte[] key;
    private boolean opened;
    private Uri uri;

    public static class EncryptedFileDataSourceException extends IOException {
        public EncryptedFileDataSourceException(IOException iOException) {
            super(iOException);
        }
    }

    public EncryptedFileDataSource() {
        super(false);
        this.key = new byte[32];
        this.iv = new byte[16];
    }

    @Deprecated
    public EncryptedFileDataSource(TransferListener transferListener) {
        this();
        if (transferListener != null) {
            addTransferListener(transferListener);
        }
    }

    public long open(DataSpec dataSpec) throws EncryptedFileDataSourceException {
        try {
            this.uri = dataSpec.uri;
            File file2 = new File(dataSpec.uri.getPath());
            String name = file2.getName();
            File internalCacheDir = FileLoader.getInternalCacheDir();
            RandomAccessFile randomAccessFile = new RandomAccessFile(new File(internalCacheDir, name + ".key"), "r");
            randomAccessFile.read(this.key);
            randomAccessFile.read(this.iv);
            randomAccessFile.close();
            this.file = new RandomAccessFile(file2, "r");
            this.file.seek(dataSpec.position);
            this.fileOffset = (int) dataSpec.position;
            this.bytesRemaining = dataSpec.length == -1 ? this.file.length() - dataSpec.position : dataSpec.length;
            if (this.bytesRemaining >= 0) {
                this.opened = true;
                transferStarted(dataSpec);
                return this.bytesRemaining;
            }
            throw new EOFException();
        } catch (IOException e) {
            throw new EncryptedFileDataSourceException(e);
        }
    }

    public int read(byte[] bArr, int i, int i2) throws EncryptedFileDataSourceException {
        if (i2 == 0) {
            return 0;
        }
        long j = this.bytesRemaining;
        if (j == 0) {
            return -1;
        }
        try {
            int read = this.file.read(bArr, i, (int) Math.min(j, (long) i2));
            Utilities.aesCtrDecryptionByteArray(bArr, this.key, this.iv, i, read, this.fileOffset);
            this.fileOffset += read;
            if (read > 0) {
                this.bytesRemaining -= (long) read;
                bytesTransferred(read);
            }
            return read;
        } catch (IOException e) {
            throw new EncryptedFileDataSourceException(e);
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
                transferEnded();
            }
        } catch (IOException e) {
            throw new EncryptedFileDataSourceException(e);
        } catch (Throwable th) {
            this.file = null;
            if (this.opened) {
                this.opened = false;
                transferEnded();
            }
            throw th;
        }
    }
}
