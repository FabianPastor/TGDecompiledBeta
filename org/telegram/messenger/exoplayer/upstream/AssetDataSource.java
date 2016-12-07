package org.telegram.messenger.exoplayer.upstream;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public final class AssetDataSource implements UriDataSource {
    private final AssetManager assetManager;
    private long bytesRemaining;
    private InputStream inputStream;
    private final TransferListener listener;
    private boolean opened;
    private String uriString;

    public static final class AssetDataSourceException extends IOException {
        public AssetDataSourceException(IOException cause) {
            super(cause);
        }
    }

    public AssetDataSource(Context context) {
        this(context, null);
    }

    public AssetDataSource(Context context, TransferListener listener) {
        this.assetManager = context.getAssets();
        this.listener = listener;
    }

    public long open(DataSpec dataSpec) throws AssetDataSourceException {
        try {
            this.uriString = dataSpec.uri.toString();
            String path = dataSpec.uri.getPath();
            if (path.startsWith("/android_asset/")) {
                path = path.substring(15);
            } else if (path.startsWith("/")) {
                path = path.substring(1);
            }
            this.uriString = dataSpec.uri.toString();
            this.inputStream = this.assetManager.open(path, 1);
            if (this.inputStream.skip(dataSpec.position) < dataSpec.position) {
                throw new EOFException();
            }
            if (dataSpec.length != -1) {
                this.bytesRemaining = dataSpec.length;
            } else {
                this.bytesRemaining = (long) this.inputStream.available();
                if (this.bytesRemaining == 2147483647L) {
                    this.bytesRemaining = -1;
                }
            }
            this.opened = true;
            if (this.listener != null) {
                this.listener.onTransferStart();
            }
            return this.bytesRemaining;
        } catch (IOException e) {
            throw new AssetDataSourceException(e);
        }
    }

    public int read(byte[] buffer, int offset, int readLength) throws AssetDataSourceException {
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
            throw new AssetDataSourceException(e);
        }
    }

    public String getUri() {
        return this.uriString;
    }

    public void close() throws AssetDataSourceException {
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
                throw new AssetDataSourceException(e);
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
