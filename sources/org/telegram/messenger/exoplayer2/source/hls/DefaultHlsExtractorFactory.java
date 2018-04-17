package org.telegram.messenger.exoplayer2.source.hls;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Pair;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.mp3.Mp3Extractor;
import org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer2.extractor.ts.Ac3Extractor;
import org.telegram.messenger.exoplayer2.extractor.ts.AdtsExtractor;
import org.telegram.messenger.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory;
import org.telegram.messenger.exoplayer2.extractor.ts.TsExtractor;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class DefaultHlsExtractorFactory implements HlsExtractorFactory {
    public static final String AAC_FILE_EXTENSION = ".aac";
    public static final String AC3_FILE_EXTENSION = ".ac3";
    public static final String EC3_FILE_EXTENSION = ".ec3";
    public static final String M4_FILE_EXTENSION_PREFIX = ".m4";
    public static final String MP3_FILE_EXTENSION = ".mp3";
    public static final String MP4_FILE_EXTENSION = ".mp4";
    public static final String MP4_FILE_EXTENSION_PREFIX = ".mp4";
    public static final String VTT_FILE_EXTENSION = ".vtt";
    public static final String WEBVTT_FILE_EXTENSION = ".webvtt";

    public Pair<Extractor, Boolean> createExtractor(Extractor previousExtractor, Uri uri, Format format, List<Format> muxedCaptionFormats, DrmInitData drmInitData, TimestampAdjuster timestampAdjuster) {
        Extractor extractor;
        Format format2 = format;
        TimestampAdjuster timestampAdjuster2 = timestampAdjuster;
        String lastPathSegment = uri.getLastPathSegment();
        boolean isPackedAudioExtractor = false;
        if (!(MimeTypes.TEXT_VTT.equals(format2.sampleMimeType) || lastPathSegment.endsWith(WEBVTT_FILE_EXTENSION))) {
            if (!lastPathSegment.endsWith(VTT_FILE_EXTENSION)) {
                if (lastPathSegment.endsWith(AAC_FILE_EXTENSION)) {
                    isPackedAudioExtractor = true;
                    extractor = new AdtsExtractor();
                } else {
                    if (!lastPathSegment.endsWith(AC3_FILE_EXTENSION)) {
                        if (!lastPathSegment.endsWith(EC3_FILE_EXTENSION)) {
                            if (lastPathSegment.endsWith(MP3_FILE_EXTENSION)) {
                                isPackedAudioExtractor = true;
                                extractor = new Mp3Extractor(0, 0);
                            } else if (previousExtractor != null) {
                                extractor = previousExtractor;
                            } else {
                                List list;
                                if (!(lastPathSegment.endsWith(".mp4") || lastPathSegment.startsWith(M4_FILE_EXTENSION_PREFIX, lastPathSegment.length() - 4))) {
                                    if (!lastPathSegment.startsWith(".mp4", lastPathSegment.length() - 5)) {
                                        List<Format> muxedCaptionFormats2;
                                        int esReaderFactoryFlags = 16;
                                        if (muxedCaptionFormats != null) {
                                            esReaderFactoryFlags = 16 | 32;
                                            muxedCaptionFormats2 = muxedCaptionFormats;
                                        } else {
                                            muxedCaptionFormats2 = Collections.emptyList();
                                        }
                                        String codecs = format2.codecs;
                                        if (!TextUtils.isEmpty(codecs)) {
                                            if (!MimeTypes.AUDIO_AAC.equals(MimeTypes.getAudioMediaMimeType(codecs))) {
                                                esReaderFactoryFlags |= 2;
                                            }
                                            if (!"video/avc".equals(MimeTypes.getVideoMediaMimeType(codecs))) {
                                                esReaderFactoryFlags |= 4;
                                            }
                                        }
                                        extractor = new TsExtractor(2, timestampAdjuster2, new DefaultTsPayloadReaderFactory(esReaderFactoryFlags, muxedCaptionFormats2));
                                        return Pair.create(extractor, Boolean.valueOf(isPackedAudioExtractor));
                                    }
                                }
                                if (muxedCaptionFormats != null) {
                                    list = muxedCaptionFormats;
                                } else {
                                    list = Collections.emptyList();
                                }
                                extractor = new FragmentedMp4Extractor(0, timestampAdjuster2, null, drmInitData, list);
                            }
                        }
                    }
                    isPackedAudioExtractor = true;
                    extractor = new Ac3Extractor();
                }
                return Pair.create(extractor, Boolean.valueOf(isPackedAudioExtractor));
            }
        }
        extractor = new WebvttExtractor(format2.language, timestampAdjuster2);
        return Pair.create(extractor, Boolean.valueOf(isPackedAudioExtractor));
    }
}
