package org.telegram.messenger.exoplayer2.source.hls.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.DownloaderConstructorHelper;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloader;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.util.UriUtil;

public final class HlsDownloader extends SegmentDownloader<HlsMasterPlaylist, String> {
    public HlsDownloader(Uri uri, DownloaderConstructorHelper downloaderConstructorHelper) {
        super(uri, downloaderConstructorHelper);
    }

    protected HlsMasterPlaylist getManifest(DataSource dataSource, Uri uri) throws IOException {
        dataSource = loadManifest(dataSource, uri);
        if ((dataSource instanceof HlsMasterPlaylist) != null) {
            return (HlsMasterPlaylist) dataSource;
        }
        return HlsMasterPlaylist.createSingleVariantMasterPlaylist(dataSource.baseUri);
    }

    protected List<Segment> getAllSegments(DataSource dataSource, HlsMasterPlaylist hlsMasterPlaylist, boolean z) throws InterruptedException, IOException {
        ArrayList arrayList = new ArrayList();
        extractUrls(hlsMasterPlaylist.variants, arrayList);
        extractUrls(hlsMasterPlaylist.audios, arrayList);
        extractUrls(hlsMasterPlaylist.subtitles, arrayList);
        return getSegments(dataSource, hlsMasterPlaylist, (String[]) arrayList.toArray(new String[arrayList.size()]), z);
    }

    protected List<Segment> getSegments(DataSource dataSource, HlsMasterPlaylist hlsMasterPlaylist, String[] strArr, boolean z) throws InterruptedException, IOException {
        HashSet hashSet = new HashSet();
        List arrayList = new ArrayList();
        for (String resolveToUri : strArr) {
            HlsMediaPlaylist hlsMediaPlaylist = null;
            Uri resolveToUri2 = UriUtil.resolveToUri(hlsMasterPlaylist.baseUri, resolveToUri);
            try {
                hlsMediaPlaylist = (HlsMediaPlaylist) loadManifest(dataSource, resolveToUri2);
            } catch (IOException e) {
                if (!z) {
                    throw e;
                }
            }
            arrayList.add(new Segment(hlsMediaPlaylist != null ? hlsMediaPlaylist.startTimeUs : Long.MIN_VALUE, new DataSpec(resolveToUri2)));
            if (hlsMediaPlaylist != null) {
                Segment segment = hlsMediaPlaylist.initializationSegment;
                if (segment != null) {
                    addSegment(arrayList, hlsMediaPlaylist, segment, hashSet);
                }
                List list = hlsMediaPlaylist.segments;
                for (int i = 0; i < list.size(); i++) {
                    addSegment(arrayList, hlsMediaPlaylist, (Segment) list.get(i), hashSet);
                }
            }
        }
        return arrayList;
    }

    private HlsPlaylist loadManifest(DataSource dataSource, Uri uri) throws IOException {
        uri = new ParsingLoadable(dataSource, new DataSpec(uri, 3), 4, new HlsPlaylistParser());
        uri.load();
        return (HlsPlaylist) uri.getResult();
    }

    private static void addSegment(ArrayList<Segment> arrayList, HlsMediaPlaylist hlsMediaPlaylist, Segment segment, HashSet<Uri> hashSet) throws IOException, InterruptedException {
        ArrayList<Segment> arrayList2 = arrayList;
        HlsMediaPlaylist hlsMediaPlaylist2 = hlsMediaPlaylist;
        Segment segment2 = segment;
        long j = hlsMediaPlaylist2.startTimeUs + segment2.relativeStartTimeUs;
        if (segment2.fullSegmentEncryptionKeyUri != null) {
            Uri resolveToUri = UriUtil.resolveToUri(hlsMediaPlaylist2.baseUri, segment2.fullSegmentEncryptionKeyUri);
            if (hashSet.add(resolveToUri)) {
                arrayList2.add(new Segment(j, new DataSpec(resolveToUri)));
            }
        }
        arrayList2.add(new Segment(j, new DataSpec(UriUtil.resolveToUri(hlsMediaPlaylist2.baseUri, segment2.url), segment2.byterangeOffset, segment2.byterangeLength, null)));
    }

    private static void extractUrls(List<HlsUrl> list, ArrayList<String> arrayList) {
        for (int i = 0; i < list.size(); i++) {
            arrayList.add(((HlsUrl) list.get(i)).url);
        }
    }
}
