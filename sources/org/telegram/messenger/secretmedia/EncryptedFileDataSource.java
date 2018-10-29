package org.telegram.messenger.secretmedia;

import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSource$$CC;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.TransferListener;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.Utilities;

public final class EncryptedFileDataSource implements DataSource {
    private long bytesRemaining;
    private DataSpec dataSpec;
    private RandomAccessFile file;
    private int fileOffset;
    private byte[] iv;
    private byte[] key;
    private final TransferListener listener;
    private boolean opened;
    private Uri uri;

    public static class EncryptedFileDataSourceException extends IOException {
        public EncryptedFileDataSourceException(IOException cause) {
            super(cause);
        }
    }

    public void addTransferListener(TransferListener transferListener) {
        DataSource$$CC.addTransferListener(this, transferListener);
    }

    public Map getResponseHeaders() {
        return DataSource$$CC.getResponseHeaders(this);
    }

    public EncryptedFileDataSource() {
        this(null);
    }

    public EncryptedFileDataSource(TransferListener listener) {
        this.key = new byte[32];
        this.iv = new byte[16];
        this.listener = listener;
    }

    public long open(DataSpec dataSpec) throws EncryptedFileDataSourceException {
        try {
            this.dataSpec = dataSpec;
            this.uri = dataSpec.uri;
            File path = new File(dataSpec.uri.getPath());
            RandomAccessFile keyFile = new RandomAccessFile(new File(FileLoader.getInternalCacheDir(), path.getName() + ".key"), "r");
            keyFile.read(this.key);
            keyFile.read(this.iv);
            keyFile.close();
            this.file = new RandomAccessFile(path, "r");
            this.file.seek(dataSpec.position);
            this.fileOffset = (int) dataSpec.position;
            this.bytesRemaining = dataSpec.length == -1 ? this.file.length() - dataSpec.position : dataSpec.length;
            if (this.bytesRemaining < 0) {
                throw new EOFException();
            }
            this.opened = true;
            if (this.listener != null) {
                this.listener.onTransferStart(this, dataSpec, false);
            }
            return this.bytesRemaining;
        } catch (IOException e) {
            throw new EncryptedFileDataSourceException(e);
        }
    }

    public int read(byte[] buffer, int offset, int readLength) throws EncryptedFileDataSourceException {
        if (readLength == 0) {
            return 0;
        }
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            int bytesRead = this.file.read(buffer, offset, (int) Math.min(this.bytesRemaining, (long) readLength));
            Utilities.aesCtrDecryptionByteArray(buffer, this.key, this.iv, offset, bytesRead, this.fileOffset);
            this.fileOffset += bytesRead;
            if (bytesRead <= 0) {
                return bytesRead;
            }
            this.bytesRemaining -= (long) bytesRead;
            if (this.listener == null) {
                return bytesRead;
            }
            this.listener.onBytesTransferred(this, this.dataSpec, false, bytesRead);
            return bytesRead;
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
                if (this.listener != null) {
                    this.listener.onTransferEnd(this, this.dataSpec, false);
                }
            }
        } catch (IOException e) {
            throw new EncryptedFileDataSourceException(e);
        } catch (Throwable th) {
            this.file = null;
            if (this.opened) {
                this.opened = false;
                if (this.listener != null) {
                    this.listener.onTransferEnd(this, this.dataSpec, false);
                }
            }
        }
    }
}
