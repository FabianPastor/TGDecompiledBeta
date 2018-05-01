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
    public SsDownloader(Uri uri, DownloaderConstructorHelper downloaderConstructorHelper) {
        super(uri, downloaderConstructorHelper);
    }

    public SsManifest getManifest(DataSource dataSource, Uri uri) throws IOException {
        uri = new ParsingLoadable(dataSource, new DataSpec(uri, 3), 4, new SsManifestParser());
        uri.load();
        return (SsManifest) uri.getResult();
    }

    protected List<Segment> getAllSegments(DataSource dataSource, SsManifest ssManifest, boolean z) throws InterruptedException, IOException {
        List arrayList = new ArrayList();
        for (StreamElement streamElement : ssManifest.streamElements) {
            for (int i = 0; i < streamElement.formats.length; i++) {
                arrayList.addAll(getSegments(dataSource, ssManifest, new TrackKey[]{new TrackKey(r2, i)}, z));
            }
        }
        return arrayList;
    }

    protected List<Segment> getSegments(DataSource dataSource, SsManifest ssManifest, TrackKey[] trackKeyArr, boolean z) throws InterruptedException, IOException {
        dataSource = new ArrayList();
        for (TrackKey trackKey : trackKeyArr) {
            StreamElement streamElement = ssManifest.streamElements[trackKey.streamElementIndex];
            for (int i = 0; i < streamElement.chunkCount; i++) {
                dataSource.add(new Segment(streamElement.getStartTimeUs(i), new DataSpec(streamElement.buildRequestUri(trackKey.trackIndex, i))));
            }
        }
        return dataSource;
    }
}
