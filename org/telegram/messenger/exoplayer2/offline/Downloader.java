package org.telegram.messenger.exoplayer2.offline;

import java.io.IOException;

public interface Downloader {

    public interface ProgressListener {
        void onDownloadProgress(Downloader downloader, float f, long j);
    }

    void download(ProgressListener progressListener) throws InterruptedException, IOException;

    float getDownloadPercentage();

    long getDownloadedBytes();

    void init() throws InterruptedException, IOException;

    void remove() throws InterruptedException;
}
