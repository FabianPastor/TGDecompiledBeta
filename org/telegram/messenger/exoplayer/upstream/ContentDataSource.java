package org.telegram.messenger.exoplayer.upstream;

import android.content.ContentResolver;
import android.content.Context;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public final class ContentDataSource implements UriDataSource {
    private long bytesRemaining;
    private InputStream inputStream;
    private final TransferListener listener;
    private boolean opened;
    private final ContentResolver resolver;
    private String uriString;

    public static class ContentDataSourceException extends IOException {
        public ContentDataSourceException(IOException cause) {
            super(cause);
        }
    }

    public ContentDataSource(Context context) {
        this(context, null);
    }

    public ContentDataSource(Context context, TransferListener listener) {
        this.resolver = context.getContentResolver();
        this.listener = listener;
    }

    public long open(DataSpec dataSpec) throws ContentDataSourceException {
        try {
            this.uriString = dataSpec.uri.toString();
            this.inputStream = new FileInputStream(this.resolver.openAssetFileDescriptor(dataSpec.uri, "r").getFileDescriptor());
            if (this.inputStream.skip(dataSpec.position) < dataSpec.position) {
                throw new EOFException();
            }
            if (dataSpec.length != -1) {
                this.bytesRemaining = dataSpec.length;
            } else {
                this.bytesRemaining = (long) this.inputStream.available();
                if (this.bytesRemaining == 0) {
                    this.bytesRemaining = -1;
                }
            }
            this.opened = true;
            if (this.listener != null) {
                this.listener.onTransferStart();
            }
            return this.bytesRemaining;
        } catch (IOException e) {
            throw new ContentDataSourceException(e);
        }
    }

    public int read(byte[] buffer, int offset, int readLength) throws ContentDataSourceException {
        if (this.bytesRemaining == 0) {
            return -1;
        }
        try {
            int bytesToRead;
            if (this.bytesRemaining == -1) {
                bytesToRead = readLength;
            } else {
                bytesToRead = (int) Math.min(this.bytesRemaining, (long) readLength);
            }
            int bytesRead = this.inputStream.read(buffer, offset, bytesToRead);
            if (bytesRead <= 0) {
                return bytesRead;
            }
            if (this.bytesRemaining != -1) {
                this.bytesRemaining -= (long) bytesRead;
            }
            if (this.listener == null) {
                return bytesRead;
            }
            this.listener.onBytesTransferred(bytesRead);
            return bytesRead;
        } catch (IOException e) {
            throw new ContentDataSourceException(e);
        }
    }

    public String getUri() {
        return this.uriString;
    }

    public void close() throws ContentDataSourceException {
        this.uriString = null;
        if (this.inputStream != null) {
            try {
                this.inputStream.close();
                this.inputStream = null;
                if (this.opened) {
                    this.opened = false;
                    if (this.listener != null) {
                        this.listener.onTransferEnd();
                    }
                }
            } catch (IOException e) {
                throw new ContentDataSourceException(e);
            } catch (Throwable th) {
                this.inputStream = null;
                if (this.opened) {
                    this.opened = false;
                    if (this.listener != null) {
                        this.listener.onTransferEnd();
                    }
                }
            }
        }
    }
}
