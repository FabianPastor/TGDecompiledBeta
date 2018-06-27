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
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.SsUtil;
import org.telegram.messenger.exoplayer2.source.smoothstreaming.manifest.StreamKey;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;

public final class SsDownloader extends SegmentDownloader<SsManifest, StreamKey> {
    public SsDownloader(Uri manifestUri, List<StreamKey> streamKeys, DownloaderConstructorHelper constructorHelper) {
        super(SsUtil.fixManifestUri(manifestUri), streamKeys, constructorHelper);
    }

    protected SsManifest getManifest(DataSource dataSource, Uri uri) throws IOException {
        ParsingLoadable<SsManifest> loadable = new ParsingLoadable(dataSource, uri, 4, new SsManifestParser());
        loadable.load();
        return (SsManifest) loadable.getResult();
    }

    protected List<Segment> getSegments(DataSource dataSource, SsManifest manifest, boolean allowIncompleteList) {
        ArrayList<Segment> segments = new ArrayList();
        for (StreamElement streamElement : manifest.streamElements) {
            for (int i = 0; i < streamElement.formats.length; i++) {
                for (int j = 0; j < streamElement.chunkCount; j++) {
                    segments.add(new Segment(streamElement.getStartTimeUs(j), new DataSpec(streamElement.buildRequestUri(i, j))));
                }
            }
        }
        return segments;
    }
}
