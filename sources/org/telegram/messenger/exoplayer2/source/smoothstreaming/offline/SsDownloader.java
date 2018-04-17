package org.telegram.messenger.exoplayer2.source.smoothstreaming.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.DownloaderConstructorHelper;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloader;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifest.StreamElement;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsManifestParser;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.TrackKey;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;

public final class SsDownloader extends SegmentDownloader<SsManifest, TrackKey> {
    public SsDownloader(Uri manifestUri, DownloaderConstructorHelper constructorHelper) {
        super(manifestUri, constructorHelper);
    }

    public SsManifest getManifest(DataSource dataSource, Uri uri) throws IOException {
        ParsingLoadable<SsManifest> loadable = new ParsingLoadable(dataSource, new DataSpec(uri, 3), 4, new SsManifestParser());
        loadable.load();
        return (SsManifest) loadable.getResult();
    }

    protected List<Segment> getAllSegments(DataSource dataSource, SsManifest manifest, boolean allowIndexLoadErrors) throws InterruptedException, IOException {
        ArrayList<Segment> segments = new ArrayList();
        for (StreamElement streamElement : manifest.streamElements) {
            for (int j = 0; j < streamElement.formats.length; j++) {
                segments.addAll(getSegments(dataSource, manifest, new TrackKey[]{new TrackKey(i, j)}, allowIndexLoadErrors));
            }
        }
        return segments;
    }

    protected List<Segment> getSegments(DataSource dataSource, SsManifest manifest, TrackKey[] keys, boolean allowIndexLoadErrors) throws InterruptedException, IOException {
        TrackKey[] trackKeyArr = keys;
        ArrayList<Segment> segments = new ArrayList();
        for (TrackKey key : trackKeyArr) {
            StreamElement streamElement = manifest.streamElements[key.streamElementIndex];
            for (int i = 0; i < streamElement.chunkCount; i++) {
                segments.add(new Segment(streamElement.getStartTimeUs(i), new DataSpec(streamElement.buildRequestUri(key.trackIndex, i))));
            }
        }
        SsManifest ssManifest = manifest;
        return segments;
    }
}
