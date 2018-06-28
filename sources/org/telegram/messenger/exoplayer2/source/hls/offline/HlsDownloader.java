package org.telegram.messenger.exoplayer2.source.hls.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.exoplayer2.offline.DownloaderConstructorHelper;
import org.telegram.messenger.exoplayer2.offline.SegmentDownloader;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist.Segment;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import org.telegram.messenger.exoplayer2.source.hls.playlist.RenditionKey;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.util.UriUtil;

public final class HlsDownloader extends SegmentDownloader<HlsPlaylist, RenditionKey> {
    public HlsDownloader(Uri playlistUri, List<RenditionKey> renditionKeys, DownloaderConstructorHelper constructorHelper) {
        super(playlistUri, renditionKeys, constructorHelper);
    }

    protected HlsPlaylist getManifest(DataSource dataSource, Uri uri) throws IOException {
        return loadManifest(dataSource, uri);
    }

    protected List<Segment> getSegments(DataSource dataSource, HlsPlaylist playlist, boolean allowIncompleteList) throws IOException {
        ArrayList<Uri> mediaPlaylistUris = new ArrayList();
        if (playlist instanceof HlsMasterPlaylist) {
            HlsMasterPlaylist masterPlaylist = (HlsMasterPlaylist) playlist;
            addResolvedUris(masterPlaylist.baseUri, masterPlaylist.variants, mediaPlaylistUris);
            addResolvedUris(masterPlaylist.baseUri, masterPlaylist.audios, mediaPlaylistUris);
            addResolvedUris(masterPlaylist.baseUri, masterPlaylist.subtitles, mediaPlaylistUris);
        } else {
            mediaPlaylistUris.add(Uri.parse(playlist.baseUri));
        }
        ArrayList<Segment> segments = new ArrayList();
        HashSet<Uri> seenEncryptionKeyUris = new HashSet();
        Iterator it = mediaPlaylistUris.iterator();
        while (it.hasNext()) {
            Uri mediaPlaylistUri = (Uri) it.next();
            try {
                HlsMediaPlaylist mediaPlaylist = (HlsMediaPlaylist) loadManifest(dataSource, mediaPlaylistUri);
                segments.add(new Segment(mediaPlaylist.startTimeUs, new DataSpec(mediaPlaylistUri)));
                Segment lastInitSegment = null;
                List<Segment> hlsSegments = mediaPlaylist.segments;
                for (int i = 0; i < hlsSegments.size(); i++) {
                    Segment segment = (Segment) hlsSegments.get(i);
                    Segment initSegment = segment.initializationSegment;
                    if (!(initSegment == null || initSegment == lastInitSegment)) {
                        lastInitSegment = initSegment;
                        addSegment(segments, mediaPlaylist, initSegment, seenEncryptionKeyUris);
                    }
                    addSegment(segments, mediaPlaylist, segment, seenEncryptionKeyUris);
                }
            } catch (IOException e) {
                if (allowIncompleteList) {
                    segments.add(new Segment(0, new DataSpec(mediaPlaylistUri)));
                } else {
                    throw e;
                }
            }
        }
        return segments;
    }

    private static HlsPlaylist loadManifest(DataSource dataSource, Uri uri) throws IOException {
        ParsingLoadable<HlsPlaylist> loadable = new ParsingLoadable(dataSource, uri, 4, new HlsPlaylistParser());
        loadable.load();
        return (HlsPlaylist) loadable.getResult();
    }

    private static void addSegment(ArrayList<Segment> segments, HlsMediaPlaylist mediaPlaylist, Segment hlsSegment, HashSet<Uri> seenEncryptionKeyUris) {
        long startTimeUs = mediaPlaylist.startTimeUs + hlsSegment.relativeStartTimeUs;
        if (hlsSegment.fullSegmentEncryptionKeyUri != null) {
            Uri keyUri = UriUtil.resolveToUri(mediaPlaylist.baseUri, hlsSegment.fullSegmentEncryptionKeyUri);
            if (seenEncryptionKeyUris.add(keyUri)) {
                segments.add(new Segment(startTimeUs, new DataSpec(keyUri)));
            }
        }
        segments.add(new Segment(startTimeUs, new DataSpec(UriUtil.resolveToUri(mediaPlaylist.baseUri, hlsSegment.url), hlsSegment.byterangeOffset, hlsSegment.byterangeLength, null)));
    }

    private static void addResolvedUris(String baseUri, List<HlsUrl> urls, List<Uri> out) {
        for (int i = 0; i < urls.size(); i++) {
            out.add(UriUtil.resolveToUri(baseUri, ((HlsUrl) urls.get(i)).url));
        }
    }
}
