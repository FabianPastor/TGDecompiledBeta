package org.telegram.messenger.exoplayer2.upstream.cache;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.telegram.messenger.exoplayer2.upstream.DataSink;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.cache.Cache.CacheException;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ReusableBufferedOutputStream;
import org.telegram.messenger.exoplayer2.util.Util;

public final class CacheDataSink implements DataSink {
    public static final int DEFAULT_BUFFER_SIZE = 20480;
    private final int bufferSize;
    private ReusableBufferedOutputStream bufferedOutputStream;
    private final Cache cache;
    private DataSpec dataSpec;
    private long dataSpecBytesWritten;
    private File file;
    private final long maxCacheFileSize;
    private OutputStream outputStream;
    private long outputStreamBytesWritten;
    private FileOutputStream underlyingFileOutputStream;

    public static class CacheDataSinkException extends CacheException {
        public CacheDataSinkException(IOException iOException) {
            super((Throwable) iOException);
        }
    }

    public CacheDataSink(Cache cache, long j) {
        this(cache, j, DEFAULT_BUFFER_SIZE);
    }

    public CacheDataSink(Cache cache, long j, int i) {
        this.cache = (Cache) Assertions.checkNotNull(cache);
        this.maxCacheFileSize = j;
        this.bufferSize = i;
    }

    public void open(DataSpec dataSpec) throws CacheDataSinkException {
        if (dataSpec.length != -1 || dataSpec.isFlagSet(2)) {
            this.dataSpec = dataSpec;
            this.dataSpecBytesWritten = 0;
            try {
                openNextOutputStream();
                return;
            } catch (DataSpec dataSpec2) {
                throw new CacheDataSinkException(dataSpec2);
            }
        }
        this.dataSpec = null;
    }

    public void write(byte[] bArr, int i, int i2) throws CacheDataSinkException {
        if (this.dataSpec != null) {
            int i3 = 0;
            while (i3 < i2) {
                try {
                    if (this.outputStreamBytesWritten == this.maxCacheFileSize) {
                        closeCurrentOutputStream();
                        openNextOutputStream();
                    }
                    int min = (int) Math.min((long) (i2 - i3), this.maxCacheFileSize - this.outputStreamBytesWritten);
                    this.outputStream.write(bArr, i + i3, min);
                    i3 += min;
                    long j = (long) min;
                    this.outputStreamBytesWritten += j;
                    this.dataSpecBytesWritten += j;
                } catch (byte[] bArr2) {
                    throw new CacheDataSinkException(bArr2);
                }
            }
        }
    }

    public void close() throws CacheDataSinkException {
        if (this.dataSpec != null) {
            try {
                closeCurrentOutputStream();
            } catch (IOException e) {
                throw new CacheDataSinkException(e);
            }
        }
    }

    private void openNextOutputStream() throws IOException {
        long j;
        if (this.dataSpec.length == -1) {
            j = this.maxCacheFileSize;
        } else {
            j = Math.min(this.dataSpec.length - this.dataSpecBytesWritten, this.maxCacheFileSize);
        }
        this.file = this.cache.startFile(this.dataSpec.key, this.dataSpec.absoluteStreamPosition + this.dataSpecBytesWritten, j);
        this.underlyingFileOutputStream = new FileOutputStream(this.file);
        if (this.bufferSize > 0) {
            if (this.bufferedOutputStream == null) {
                this.bufferedOutputStream = new ReusableBufferedOutputStream(this.underlyingFileOutputStream, this.bufferSize);
            } else {
                this.bufferedOutputStream.reset(this.underlyingFileOutputStream);
            }
            this.outputStream = this.bufferedOutputStream;
        } else {
            this.outputStream = this.underlyingFileOutputStream;
        }
        this.outputStreamBytesWritten = 0;
    }

    private void closeCurrentOutputStream() throws IOException {
        if (this.outputStream != null) {
            try {
                this.outputStream.flush();
                this.underlyingFileOutputStream.getFD().sync();
                Util.closeQuietly(this.outputStream);
                this.outputStream = null;
                File file = this.file;
                this.file = null;
                this.cache.commitFile(file);
            } catch (Throwable th) {
                Util.closeQuietly(this.outputStream);
                this.outputStream = null;
                File file2 = this.file;
                this.file = null;
                file2.delete();
            }
        }
    }
}
