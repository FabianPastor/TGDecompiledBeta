package org.telegram.messenger.exoplayer2.offline;

import java.io.IOException;

public interface Downloader {
    void cancel();

    void download() throws InterruptedException, IOException;

    float getDownloadPercentage();

    long getDownloadedBytes();

    void remove() throws InterruptedException;
}
