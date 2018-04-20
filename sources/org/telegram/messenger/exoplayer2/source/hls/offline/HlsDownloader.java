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
        HashSet<Uri> encryptionKeyUris = new HashSet();
        ArrayList<Segment> segments = new ArrayList();
        for (String playlistUrl : keys) {
            HlsMediaPlaylist mediaPlaylist = null;
            Uri uri = UriUtil.resolveToUri(manifest.baseUri, playlistUrl);
            try {
                mediaPlaylist = (HlsMediaPlaylist) loadManifest(dataSource, uri);
            } catch (IOException e) {
                if (!allowIndexLoadErrors) {
                    throw e;
                }
            }
            segments.add(new Segment(mediaPlaylist != null ? mediaPlaylist.startTimeUs : Long.MIN_VALUE, new DataSpec(uri)));
            if (mediaPlaylist != null) {
                Segment initSegment = mediaPlaylist.initializationSegment;
                if (initSegment != null) {
                    addSegment(segments, mediaPlaylist, initSegment, encryptionKeyUris);
                }
                List<Segment> hlsSegments = mediaPlaylist.segments;
                for (int i = 0; i < hlsSegments.size(); i++) {
                    addSegment(segments, mediaPlaylist, (Segment) hlsSegments.get(i), encryptionKeyUris);
                }
            }
        }
        return segments;
    }

    private HlsPlaylist loadManifest(DataSource dataSource, Uri uri) throws IOException {
        ParsingLoadable<HlsPlaylist> loadable = new ParsingLoadable(dataSource, new DataSpec(uri, 3), 4, new HlsPlaylistParser());
        loadable.load();
        return (HlsPlaylist) loadable.getResult();
    }

    private static void addSegment(ArrayList<Segment> segments, HlsMediaPlaylist mediaPlaylist, Segment hlsSegment, HashSet<Uri> encryptionKeyUris) throws IOException, InterruptedException {
        long startTimeUs = mediaPlaylist.startTimeUs + hlsSegment.relativeStartTimeUs;
        if (hlsSegment.fullSegmentEncryptionKeyUri != null) {
            Uri keyUri = UriUtil.resolveToUri(mediaPlaylist.baseUri, hlsSegment.fullSegmentEncryptionKeyUri);
            if (encryptionKeyUris.add(keyUri)) {
                segments.add(new Segment(startTimeUs, new DataSpec(keyUri)));
            }
        }
        segments.add(new Segment(startTimeUs, new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, hlsSegment.url), hlsSegment.byterangeOffset, hlsSegment.byterangeLength, null)));
    }

    private static void extractUrls(List<HlsUrl> hlsUrls, ArrayList<String> urls) {
        for (int i = 0; i < hlsUrls.size(); i++) {
            urls.add(((HlsUrl) hlsUrls.get(i)).url);
        }
    }
}
