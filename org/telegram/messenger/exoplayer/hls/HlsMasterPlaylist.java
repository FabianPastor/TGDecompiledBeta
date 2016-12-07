package org.telegram.messenger.exoplayer.hls;

import java.util.Collections;
import java.util.List;

public final class HlsMasterPlaylist extends HlsPlaylist {
    public final List<Variant> audios;
    public final String muxedAudioLanguage;
    public final String muxedCaptionLanguage;
    public final List<Variant> subtitles;
    public final List<Variant> variants;

    public HlsMasterPlaylist(String baseUri, List<Variant> variants, List<Variant> audios, List<Variant> subtitles, String muxedAudioLanguage, String muxedCaptionLanguage) {
        super(baseUri, 0);
        this.variants = Collections.unmodifiableList(variants);
        this.audios = Collections.unmodifiableList(audios);
        this.subtitles = Collections.unmodifiableList(subtitles);
        this.muxedAudioLanguage = muxedAudioLanguage;
        this.muxedCaptionLanguage = muxedCaptionLanguage;
    }
}
