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
        public CacheDataSinkException(IOException cause) {
            super((Throwable) cause);
        }
    }

    public CacheDataSink(Cache cache, long maxCacheFileSize) {
        this(cache, maxCacheFileSize, DEFAULT_BUFFER_SIZE);
    }

    public CacheDataSink(Cache cache, long maxCacheFileSize, int bufferSize) {
        this.cache = (Cache) Assertions.checkNotNull(cache);
        this.maxCacheFileSize = maxCacheFileSize;
        this.bufferSize = bufferSize;
    }

    public void open(DataSpec dataSpec) throws CacheDataSinkException {
        if (dataSpec.length != -1 || dataSpec.isFlagSet(2)) {
            this.dataSpec = dataSpec;
            this.dataSpecBytesWritten = 0;
            try {
                openNextOutputStream();
                return;
            } catch (IOException e) {
                throw new CacheDataSinkException(e);
            }
        }
        this.dataSpec = null;
    }

    public void write(byte[] buffer, int offset, int length) throws CacheDataSinkException {
        if (this.dataSpec != null) {
            int bytesWritten = 0;
            while (bytesWritten < length) {
                try {
                    if (this.outputStreamBytesWritten == this.maxCacheFileSize) {
                        closeCurrentOutputStream();
                        openNextOutputStream();
                    }
                    int bytesToWrite = (int) Math.min((long) (length - bytesWritten), this.maxCacheFileSize - this.outputStreamBytesWritten);
                    this.outputStream.write(buffer, offset + bytesWritten, bytesToWrite);
                    bytesWritten += bytesToWrite;
                    this.outputStreamBytesWritten += (long) bytesToWrite;
                    this.dataSpecBytesWritten += (long) bytesToWrite;
                } catch (IOException e) {
                    throw new CacheDataSinkException(e);
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
            boolean success = false;
            try {
                this.outputStream.flush();
                this.underlyingFileOutputStream.getFD().sync();
                success = true;
            } finally {
                Util.closeQuietly(this.outputStream);
                this.outputStream = null;
                File fileToCommit = this.file;
                this.file = null;
                if (success) {
                    this.cache.commitFile(fileToCommit);
                } else {
                    fileToCommit.delete();
                }
            }
        }
    }
}
