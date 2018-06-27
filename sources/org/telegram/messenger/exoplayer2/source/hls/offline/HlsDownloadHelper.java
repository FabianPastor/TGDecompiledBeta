package org.telegram.messenger.exoplayer2.source.hls.offline;

import android.net.Uri;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.offline.DownloadHelper;
import org.telegram.messenger.exoplayer2.offline.TrackKey;
import org.telegram.messenger.exoplayer2.source.TrackGroup;
import org.telegram.messenger.exoplayer2.source.TrackGroupArray;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMasterPlaylist.HlsUrl;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsMediaPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylist;
import org.telegram.messenger.exoplayer2.source.hls.playlist.HlsPlaylistParser;
import org.telegram.messenger.exoplayer2.source.hls.playlist.RenditionKey;
import org.telegram.messenger.exoplayer2.upstream.DataSource.Factory;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.util.Assertions;

public final class HlsDownloadHelper extends DownloadHelper {
    private final Factory manifestDataSourceFactory;
    private HlsPlaylist playlist;
    private int[] renditionTypes;
    private final Uri uri;

    public HlsDownloadHelper(Uri uri, Factory manifestDataSourceFactory) {
        this.uri = uri;
        this.manifestDataSourceFactory = manifestDataSourceFactory;
    }

    protected void prepareInternal() throws IOException {
        this.playlist = (HlsPlaylist) ParsingLoadable.load(this.manifestDataSourceFactory.createDataSource(), new HlsPlaylistParser(), this.uri);
    }

    public HlsPlaylist getPlaylist() {
        Assertions.checkNotNull(this.playlist);
        return this.playlist;
    }

    public int getPeriodCount() {
        Assertions.checkNotNull(this.playlist);
        return 1;
    }

    public TrackGroupArray getTrackGroups(int periodIndex) {
        Assertions.checkNotNull(this.playlist);
        if (this.playlist instanceof HlsMediaPlaylist) {
            return TrackGroupArray.EMPTY;
        }
        HlsMasterPlaylist masterPlaylist = this.playlist;
        TrackGroup[] trackGroups = new TrackGroup[3];
        this.renditionTypes = new int[3];
        int i = 0;
        if (!masterPlaylist.variants.isEmpty()) {
            this.renditionTypes[0] = 0;
            int trackGroupIndex = 0 + 1;
            trackGroups[0] = new TrackGroup(toFormats(masterPlaylist.variants));
            i = trackGroupIndex;
        }
        if (!masterPlaylist.audios.isEmpty()) {
            this.renditionTypes[i] = 1;
            trackGroupIndex = i + 1;
            trackGroups[i] = new TrackGroup(toFormats(masterPlaylist.audios));
            i = trackGroupIndex;
        }
        if (!masterPlaylist.subtitles.isEmpty()) {
            this.renditionTypes[i] = 2;
            trackGroupIndex = i + 1;
            trackGroups[i] = new TrackGroup(toFormats(masterPlaylist.subtitles));
            i = trackGroupIndex;
        }
        return new TrackGroupArray((TrackGroup[]) Arrays.copyOf(trackGroups, i));
    }

    public HlsDownloadAction getDownloadAction(byte[] data, List<TrackKey> trackKeys) {
        Assertions.checkNotNull(this.renditionTypes);
        return new HlsDownloadAction(this.uri, false, data, toRenditionKeys(trackKeys, this.renditionTypes));
    }

    public HlsDownloadAction getRemoveAction(byte[] data) {
        return new HlsDownloadAction(this.uri, true, data, Collections.emptyList());
    }

    private static Format[] toFormats(List<HlsUrl> hlsUrls) {
        Format[] formats = new Format[hlsUrls.size()];
        for (int i = 0; i < hlsUrls.size(); i++) {
            formats[i] = ((HlsUrl) hlsUrls.get(i)).format;
        }
        return formats;
    }

    private static List<RenditionKey> toRenditionKeys(List<TrackKey> trackKeys, int[] groups) {
        List<RenditionKey> representationKeys = new ArrayList(trackKeys.size());
        for (int i = 0; i < trackKeys.size(); i++) {
            TrackKey trackKey = (TrackKey) trackKeys.get(i);
            representationKeys.add(new RenditionKey(groups[trackKey.groupIndex], trackKey.trackIndex));
        }
        return representationKeys;
    }
}
