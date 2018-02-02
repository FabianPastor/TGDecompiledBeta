package org.telegram.messenger.exoplayer2.source.dash.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.offline.DownloadException;
import org.telegram.messenger.exoplayer2.offline.DownloaderConstructorHelper;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloader;
import org.telegram.messenger.exoplayer2.source.dash.DashSegmentIndex;
import org.telegram.messenger.exoplayer2.source.dash.DashUtil;
import org.telegram.messenger.exoplayer2.source.dash.DashWrappingSegmentIndex;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Period;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RepresentationKey;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;

public final class DashDownloader extends SegmentDownloader<DashManifest, RepresentationKey> {
    public DashDownloader(Uri manifestUri, DownloaderConstructorHelper constructorHelper) {
        super(manifestUri, constructorHelper);
    }

    public DashManifest getManifest(DataSource dataSource, Uri uri) throws IOException {
        return DashUtil.loadManifest(dataSource, uri);
    }

    protected List<Segment> getAllSegments(DataSource dataSource, DashManifest manifest, boolean allowIndexLoadErrors) throws InterruptedException, IOException {
        ArrayList<Segment> segments = new ArrayList();
        for (int periodIndex = 0; periodIndex < manifest.getPeriodCount(); periodIndex++) {
            List<AdaptationSet> adaptationSets = manifest.getPeriod(periodIndex).adaptationSets;
            for (int adaptationIndex = 0; adaptationIndex < adaptationSets.size(); adaptationIndex++) {
                RepresentationKey[] keys = new RepresentationKey[((AdaptationSet) adaptationSets.get(adaptationIndex)).representations.size()];
                for (int i = 0; i < keys.length; i++) {
                    keys[i] = new RepresentationKey(periodIndex, adaptationIndex, i);
                }
                segments.addAll(getSegments(dataSource, manifest, keys, allowIndexLoadErrors));
            }
        }
        return segments;
    }

    protected List<Segment> getSegments(DataSource dataSource, DashManifest manifest, RepresentationKey[] keys, boolean allowIndexLoadErrors) throws InterruptedException, IOException {
        ArrayList<Segment> segments = new ArrayList();
        int length = keys.length;
        int i = 0;
        while (i < length) {
            RepresentationKey key = keys[i];
            try {
                DashSegmentIndex index = getSegmentIndex(dataSource, manifest, key);
                if (index == null) {
                    throw new DownloadException("No index for representation: " + key);
                }
                int segmentCount = index.getSegmentCount(C.TIME_UNSET);
                if (segmentCount == -1) {
                    throw new DownloadException("Unbounded index for representation: " + key);
                }
                Period period = manifest.getPeriod(key.periodIndex);
                Representation representation = (Representation) ((AdaptationSet) period.adaptationSets.get(key.adaptationSetIndex)).representations.get(key.representationIndex);
                long startUs = C.msToUs(period.startMs);
                String baseUrl = representation.baseUrl;
                RangedUri initializationUri = representation.getInitializationUri();
                if (initializationUri != null) {
                    addSegment(segments, startUs, baseUrl, initializationUri);
                }
                RangedUri indexUri = representation.getIndexUri();
                if (indexUri != null) {
                    addSegment(segments, startUs, baseUrl, indexUri);
                }
                int firstSegmentNum = index.getFirstSegmentNum();
                int lastSegmentNum = (firstSegmentNum + segmentCount) - 1;
                for (int j = firstSegmentNum; j <= lastSegmentNum; j++) {
                    addSegment(segments, index.getTimeUs(j) + startUs, baseUrl, index.getSegmentUrl(j));
                }
                i++;
            } catch (IOException e) {
                if (!allowIndexLoadErrors) {
                    throw e;
                }
            }
        }
        return segments;
    }

    private DashSegmentIndex getSegmentIndex(DataSource dataSource, DashManifest manifest, RepresentationKey key) throws IOException, InterruptedException {
        AdaptationSet adaptationSet = (AdaptationSet) manifest.getPeriod(key.periodIndex).adaptationSets.get(key.adaptationSetIndex);
        Representation representation = (Representation) adaptationSet.representations.get(key.representationIndex);
        DashSegmentIndex index = representation.getIndex();
        if (index != null) {
            return index;
        }
        ChunkIndex seekMap = DashUtil.loadChunkIndex(dataSource, adaptationSet.type, representation);
        return seekMap == null ? null : new DashWrappingSegmentIndex(seekMap);
    }

    private static void addSegment(ArrayList<Segment> segments, long startTimeUs, String baseUrl, RangedUri rangedUri) {
        segments.add(new Segment(startTimeUs, new DataSpec(rangedUri.resolveUri(baseUrl), rangedUri.start, rangedUri.length, null)));
    }
}
