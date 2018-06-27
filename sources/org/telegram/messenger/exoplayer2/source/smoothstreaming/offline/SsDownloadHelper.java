package org.telegram.messenger.exoplayer2.source.smoothstreaming.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.DownloadHelper;
import org.telegram.messenger.exoplayer2.offline.TrackKey;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.StreamKey;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class SsDownloadHelper extends DownloadHelper {
    private SsManifest manifest;
    private final Factory manifestDataSourceFactory;
    private final Uri uri;

    public SsDownloadHelper(Uri uri, Factory manifestDataSourceFactory) {
        this.uri = uri;
        this.manifestDataSourceFactory = manifestDataSourceFactory;
    }

    protected void prepareInternal() throws IOException {
        this.manifest = (SsManifest) ParsingLoadable.load(this.manifestDataSourceFactory.createDataSource(), new SsManifestParser(), this.uri);
    }

    public SsManifest getManifest() {
        Assertions.checkNotNull(this.manifest);
        return this.manifest;
    }

    public int getPeriodCount() {
        Assertions.checkNotNull(this.manifest);
        return 1;
    }

    public TrackGroupArray getTrackGroups(int periodIndex) {
        Assertions.checkNotNull(this.manifest);
        StreamElement[] streamElements = this.manifest.streamElements;
        TrackGroup[] trackGroups = new TrackGroup[streamElements.length];
        for (int i = 0; i < streamElements.length; i++) {
            trackGroups[i] = new TrackGroup(streamElements[i].formats);
        }
        return new TrackGroupArray(trackGroups);
    }

    public SsDownloadAction getDownloadAction(byte[] data, List<TrackKey> trackKeys) {
        return new SsDownloadAction(this.uri, false, data, toStreamKeys(trackKeys));
    }

    public SsDownloadAction getRemoveAction(byte[] data) {
        return new SsDownloadAction(this.uri, true, data, Collections.emptyList());
    }

    private static List<StreamKey> toStreamKeys(List<TrackKey> trackKeys) {
        List<StreamKey> representationKeys = new ArrayList(trackKeys.size());
        for (int i = 0; i < trackKeys.size(); i++) {
            TrackKey trackKey = (TrackKey) trackKeys.get(i);
            representationKeys.add(new StreamKey(trackKey.groupIndex, trackKey.trackIndex));
        }
        return representationKeys;
    }
}
