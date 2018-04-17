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
    public HlsDownloader(Uri manifestUri, DownloaderConstructorHelper constructorHelper) {
        super(manifestUri, constructorHelper);
    }

    protected HlsMasterPlaylist getManifest(DataSource dataSource, Uri uri) throws IOException {
        HlsPlaylist hlsPlaylist = loadManifest(dataSource, uri);
        if (hlsPlaylist instanceof HlsMasterPlaylist) {
            return (HlsMasterPlaylist) hlsPlaylist;
        }
        return HlsMasterPlaylist.createSingleVariantMasterPlaylist(hlsPlaylist.baseUri);
    }

    protected List<Segment> getAllSegments(DataSource dataSource, HlsMasterPlaylist manifest, boolean allowIndexLoadErrors) throws InterruptedException, IOException {
        ArrayList<String> urls = new ArrayList();
        extractUrls(manifest.variants, urls);
        extractUrls(manifest.audios, urls);
        extractUrls(manifest.subtitles, urls);
        return getSegments(dataSource, manifest, (String[]) urls.toArray(new String[urls.size()]), allowIndexLoadErrors);
    }

    protected List<Segment> getSegments(DataSource dataSource, HlsMasterPlaylist manifest, String[] keys, boolean allowIndexLoadErrors) throws InterruptedException, IOException {
        String[] strArr = keys;
        HashSet<Uri> encryptionKeyUris = new HashSet();
        ArrayList<Segment> segments = new ArrayList();
        int length = strArr.length;
        int i = 0;
        while (i < length) {
            int i2;
            long j;
            HlsMediaPlaylist mediaPlaylist = null;
            Uri uri = UriUtil.resolveToUri(manifest.baseUri, strArr[i]);
            try {
                mediaPlaylist = (HlsMediaPlaylist) loadManifest(dataSource, uri);
            } catch (IOException e) {
                IOException e2 = e;
                if (!allowIndexLoadErrors) {
                    throw e2;
                }
            }
            if (mediaPlaylist != null) {
                i2 = length;
                j = mediaPlaylist.startTimeUs;
            } else {
                i2 = length;
                j = Long.MIN_VALUE;
            }
            segments.add(new Segment(j, new DataSpec(uri)));
            if (mediaPlaylist != null) {
                Segment initSegment = mediaPlaylist.initializationSegment;
                if (initSegment != null) {
                    addSegment(segments, mediaPlaylist, initSegment, encryptionKeyUris);
                }
                List<Segment> hlsSegments = mediaPlaylist.segments;
                for (int i3 = 0; i3 < hlsSegments.size(); i3++) {
                    addSegment(segments, mediaPlaylist, (Segment) hlsSegments.get(i3), encryptionKeyUris);
                }
            }
            i++;
            length = i2;
        }
        HlsDownloader hlsDownloader = this;
        DataSource dataSource2 = dataSource;
        HlsMasterPlaylist hlsMasterPlaylist = manifest;
        return segments;
    }

    private HlsPlaylist loadManifest(DataSource dataSource, Uri uri) throws IOException {
        ParsingLoadable<HlsPlaylist> loadable = new ParsingLoadable(dataSource, new DataSpec(uri, 3), 4, new HlsPlaylistParser());
        loadable.load();
        return (HlsPlaylist) loadable.getResult();
    }

    private static void addSegment(ArrayList<Segment> segments, HlsMediaPlaylist mediaPlaylist, Segment hlsSegment, HashSet<Uri> encryptionKeyUris) throws IOException, InterruptedException {
        ArrayList<Segment> arrayList = segments;
        HlsMediaPlaylist hlsMediaPlaylist = mediaPlaylist;
        Segment segment = hlsSegment;
        long startTimeUs = hlsMediaPlaylist.startTimeUs + segment.relativeStartTimeUs;
        if (segment.fullSegmentEncryptionKeyUri != null) {
            Uri keyUri = UriUtil.resolveToUri(hlsMediaPlaylist.baseUri, segment.fullSegmentEncryptionKeyUri);
            if (encryptionKeyUris.add(keyUri)) {
                arrayList.add(new Segment(startTimeUs, new DataSpec(keyUri)));
            }
        } else {
            HashSet<Uri> hashSet = encryptionKeyUris;
        }
        arrayList.add(new Segment(startTimeUs, new DataSpec(UriUtil.resolveToUri(hlsMediaPlaylist.baseUri, segment.url), segment.byterangeOffset, segment.byterangeLength, null)));
    }

    private static void extractUrls(List<HlsUrl> hlsUrls, ArrayList<String> urls) {
        for (int i = 0; i < hlsUrls.size(); i++) {
            urls.add(((HlsUrl) hlsUrls.get(i)).url);
        }
    }
}
