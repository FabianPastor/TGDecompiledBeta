package org.telegram.messenger.exoplayer2.source.dash.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.exoplayer2.C0542C;
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
    public DashDownloader(Uri uri, DownloaderConstructorHelper downloaderConstructorHelper) {
        super(uri, downloaderConstructorHelper);
    }

    public DashManifest getManifest(DataSource dataSource, Uri uri) throws IOException {
        return DashUtil.loadManifest(dataSource, uri);
    }

    protected List<Segment> getAllSegments(DataSource dataSource, DashManifest dashManifest, boolean z) throws InterruptedException, IOException {
        List arrayList = new ArrayList();
        for (int i = 0; i < dashManifest.getPeriodCount(); i++) {
            List list = dashManifest.getPeriod(i).adaptationSets;
            for (int i2 = 0; i2 < list.size(); i2++) {
                RepresentationKey[] representationKeyArr = new RepresentationKey[((AdaptationSet) list.get(i2)).representations.size()];
                for (int i3 = 0; i3 < representationKeyArr.length; i3++) {
                    representationKeyArr[i3] = new RepresentationKey(i, i2, i3);
                }
                arrayList.addAll(getSegments(dataSource, dashManifest, representationKeyArr, z));
            }
        }
        return arrayList;
    }

    protected List<Segment> getSegments(DataSource dataSource, DashManifest dashManifest, RepresentationKey[] representationKeyArr, boolean z) throws InterruptedException, IOException {
        DashManifest dashManifest2 = dashManifest;
        RepresentationKey[] representationKeyArr2 = representationKeyArr;
        List arrayList = new ArrayList();
        int i = 0;
        int length = representationKeyArr2.length;
        while (i < length) {
            RepresentationKey representationKey = representationKeyArr2[i];
            try {
                DashSegmentIndex segmentIndex = getSegmentIndex(dataSource, dashManifest2, representationKey);
                if (segmentIndex == null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("No index for representation: ");
                    stringBuilder.append(representationKey);
                    throw new DownloadException(stringBuilder.toString());
                }
                int segmentCount = segmentIndex.getSegmentCount(C0542C.TIME_UNSET);
                if (segmentCount == -1) {
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("Unbounded index for representation: ");
                    stringBuilder2.append(representationKey);
                    throw new DownloadException(stringBuilder2.toString());
                }
                Period period = dashManifest2.getPeriod(representationKey.periodIndex);
                Representation representation = (Representation) ((AdaptationSet) period.adaptationSets.get(representationKey.adaptationSetIndex)).representations.get(representationKey.representationIndex);
                long msToUs = C0542C.msToUs(period.startMs);
                String str = representation.baseUrl;
                RangedUri initializationUri = representation.getInitializationUri();
                if (initializationUri != null) {
                    addSegment(arrayList, msToUs, str, initializationUri);
                }
                RangedUri indexUri = representation.getIndexUri();
                if (indexUri != null) {
                    addSegment(arrayList, msToUs, str, indexUri);
                }
                int firstSegmentNum = segmentIndex.getFirstSegmentNum();
                segmentCount = (segmentCount + firstSegmentNum) - 1;
                while (firstSegmentNum <= segmentCount) {
                    addSegment(arrayList, msToUs + segmentIndex.getTimeUs(firstSegmentNum), str, segmentIndex.getSegmentUrl(firstSegmentNum));
                    firstSegmentNum++;
                    dashManifest2 = dashManifest;
                    representationKeyArr2 = representationKeyArr;
                }
                i++;
                dashManifest2 = dashManifest;
                representationKeyArr2 = representationKeyArr;
            } catch (IOException e) {
                IOException iOException = e;
                if (!z) {
                    throw iOException;
                }
            }
        }
        DashDownloader dashDownloader = this;
        return arrayList;
    }

    private DashSegmentIndex getSegmentIndex(DataSource dataSource, DashManifest dashManifest, RepresentationKey representationKey) throws IOException, InterruptedException {
        AdaptationSet adaptationSet = (AdaptationSet) dashManifest.getPeriod(representationKey.periodIndex).adaptationSets.get(representationKey.adaptationSetIndex);
        Representation representation = (Representation) adaptationSet.representations.get(representationKey.representationIndex);
        DashSegmentIndex index = representation.getIndex();
        if (index != null) {
            return index;
        }
        dataSource = DashUtil.loadChunkIndex(dataSource, adaptationSet.type, representation);
        if (dataSource == null) {
            dataSource = null;
        } else {
            dataSource = new DashWrappingSegmentIndex(dataSource);
        }
        return dataSource;
    }

    private static void addSegment(ArrayList<Segment> arrayList, long j, String str, RangedUri rangedUri) {
        arrayList.add(new Segment(j, new DataSpec(rangedUri.resolveUri(str), rangedUri.start, rangedUri.length, null)));
    }
}
