package org.telegram.messenger.exoplayer2.offline;

import android.net.Uri;
import java.util.List;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;

public final class ProgressiveDownloadHelper extends DownloadHelper {
    private final String customCacheKey;
    private final Uri uri;

    public ProgressiveDownloadHelper(Uri uri) {
        this(uri, null);
    }

    public ProgressiveDownloadHelper(Uri uri, String customCacheKey) {
        this.uri = uri;
        this.customCacheKey = customCacheKey;
    }

    protected void prepareInternal() {
    }

    public int getPeriodCount() {
        return 1;
    }

    public TrackGroupArray getTrackGroups(int periodIndex) {
        return TrackGroupArray.EMPTY;
    }

    public DownloadAction getDownloadAction(byte[] data, List<TrackKey> list) {
        return new ProgressiveDownloadAction(this.uri, false, data, this.customCacheKey);
    }

    public DownloadAction getRemoveAction(byte[] data) {
        return new ProgressiveDownloadAction(this.uri, true, data, this.customCacheKey);
    }
}
