package org.telegram.messenger.exoplayer2.source.dash.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.offline.DownloadHelper;
import org.telegram.messenger.exoplayer2.offline.TrackKey;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifestParser;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RepresentationKey;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class DashDownloadHelper extends DownloadHelper {
    private DashManifest manifest;
    private final Factory manifestDataSourceFactory;
    private final Uri uri;

    public DashDownloadHelper(Uri uri, Factory manifestDataSourceFactory) {
        this.uri = uri;
        this.manifestDataSourceFactory = manifestDataSourceFactory;
    }

    protected void prepareInternal() throws IOException {
        this.manifest = (DashManifest) ParsingLoadable.load(this.manifestDataSourceFactory.createDataSource(), new DashManifestParser(), this.uri);
    }

    public DashManifest getManifest() {
        Assertions.checkNotNull(this.manifest);
        return this.manifest;
    }

    public int getPeriodCount() {
        Assertions.checkNotNull(this.manifest);
        return this.manifest.getPeriodCount();
    }

    public TrackGroupArray getTrackGroups(int periodIndex) {
        Assertions.checkNotNull(this.manifest);
        List<AdaptationSet> adaptationSets = this.manifest.getPeriod(periodIndex).adaptationSets;
        TrackGroup[] trackGroups = new TrackGroup[adaptationSets.size()];
        for (int i = 0; i < trackGroups.length; i++) {
            List<Representation> representations = ((AdaptationSet) adaptationSets.get(i)).representations;
            Format[] formats = new Format[representations.size()];
            int representationsCount = representations.size();
            for (int j = 0; j < representationsCount; j++) {
                formats[j] = ((Representation) representations.get(j)).format;
            }
            trackGroups[i] = new TrackGroup(formats);
        }
        return new TrackGroupArray(trackGroups);
    }

    public DashDownloadAction getDownloadAction(byte[] data, List<TrackKey> trackKeys) {
        return new DashDownloadAction(this.uri, false, data, toRepresentationKeys(trackKeys));
    }

    public DashDownloadAction getRemoveAction(byte[] data) {
        return new DashDownloadAction(this.uri, true, data, Collections.emptyList());
    }

    private static List<RepresentationKey> toRepresentationKeys(List<TrackKey> trackKeys) {
        List<RepresentationKey> representationKeys = new ArrayList(trackKeys.size());
        for (int i = 0; i < trackKeys.size(); i++) {
            TrackKey trackKey = (TrackKey) trackKeys.get(i);
            representationKeys.add(new RepresentationKey(trackKey.periodIndex, trackKey.groupIndex, trackKey.trackIndex));
        }
        return representationKeys;
    }
}
