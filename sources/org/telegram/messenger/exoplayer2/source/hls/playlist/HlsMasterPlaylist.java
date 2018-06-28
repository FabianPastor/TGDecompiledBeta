package org.telegram.messenger.exoplayer2.source.hls.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class HlsMasterPlaylist extends HlsPlaylist {
    public final List<HlsUrl> audios;
    public final Format muxedAudioFormat;
    public final List<Format> muxedCaptionFormats;
    public final List<HlsUrl> subtitles;
    public final List<HlsUrl> variants;

    public static final class HlsUrl {
        public final Format format;
        public final String url;

        public static HlsUrl createMediaPlaylistHlsUrl(String url) {
            return new HlsUrl(url, Format.createContainerFormat("0", MimeTypes.APPLICATION_M3U8, null, null, -1, 0, null));
        }

        public HlsUrl(String url, Format format) {
            this.url = url;
            this.format = format;
        }
    }

    public HlsMasterPlaylist(String baseUri, List<String> tags, List<HlsUrl> variants, List<HlsUrl> audios, List<HlsUrl> subtitles, Format muxedAudioFormat, List<Format> muxedCaptionFormats) {
        super(baseUri, tags);
        this.variants = Collections.unmodifiableList(variants);
        this.audios = Collections.unmodifiableList(audios);
        this.subtitles = Collections.unmodifiableList(subtitles);
        this.muxedAudioFormat = muxedAudioFormat;
        this.muxedCaptionFormats = muxedCaptionFormats != null ? Collections.unmodifiableList(muxedCaptionFormats) : null;
    }

    public HlsMasterPlaylist copy(List<RenditionKey> renditionKeys) {
        return new HlsMasterPlaylist(this.baseUri, this.tags, copyRenditionsList(this.variants, 0, renditionKeys), copyRenditionsList(this.audios, 1, renditionKeys), copyRenditionsList(this.subtitles, 2, renditionKeys), this.muxedAudioFormat, this.muxedCaptionFormats);
    }

    public static HlsMasterPlaylist createSingleVariantMasterPlaylist(String variantUrl) {
        List<HlsUrl> variant = Collections.singletonList(HlsUrl.createMediaPlaylistHlsUrl(variantUrl));
        List<HlsUrl> emptyList = Collections.emptyList();
        return new HlsMasterPlaylist(null, Collections.emptyList(), variant, emptyList, emptyList, null, null);
    }

    private static List<HlsUrl> copyRenditionsList(List<HlsUrl> renditions, int renditionType, List<RenditionKey> renditionKeys) {
        List<HlsUrl> copiedRenditions = new ArrayList(renditionKeys.size());
        int i = 0;
        while (i < renditions.size()) {
            HlsUrl rendition = (HlsUrl) renditions.get(i);
            for (int j = 0; j < renditionKeys.size(); j++) {
                RenditionKey renditionKey = (RenditionKey) renditionKeys.get(j);
                if (renditionKey.type == renditionType && renditionKey.trackIndex == i) {
                    copiedRenditions.add(rendition);
                    break;
                }
            }
            i++;
        }
        return copiedRenditions;
    }
}
