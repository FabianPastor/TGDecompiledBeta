package org.telegram.messenger.exoplayer2.source.dash;

import android.net.Uri;
import java.io.IOException;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor;
import org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper;
import org.telegram.messenger.exoplayer2.source.chunk.InitializationChunk;
import org.telegram.messenger.exoplayer2.source.dash.manifest.AdaptationSet;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifestParser;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Period;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.upstream.ParsingLoadable;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class DashUtil {
    public static DashManifest loadManifest(DataSource dataSource, Uri uri) throws IOException {
        ParsingLoadable<DashManifest> loadable = new ParsingLoadable(dataSource, new DataSpec(uri, 3), 4, new DashManifestParser());
        loadable.load();
        return (DashManifest) loadable.getResult();
    }

    public static DrmInitData loadDrmInitData(DataSource dataSource, Period period) throws IOException, InterruptedException {
        DrmInitData drmInitData;
        int primaryTrackType = 2;
        Representation representation = getFirstRepresentation(period, 2);
        if (representation == null) {
            primaryTrackType = 1;
            representation = getFirstRepresentation(period, 1);
            if (representation == null) {
                return null;
            }
        }
        Format manifestFormat = representation.format;
        Format sampleFormat = loadSampleFormat(dataSource, primaryTrackType, representation);
        if (sampleFormat == null) {
            drmInitData = manifestFormat.drmInitData;
        } else {
            drmInitData = sampleFormat.copyWithManifestFormatInfo(manifestFormat).drmInitData;
        }
        return drmInitData;
    }

    public static Format loadSampleFormat(DataSource dataSource, int trackType, Representation representation) throws IOException, InterruptedException {
        ChunkExtractorWrapper extractorWrapper = loadInitializationData(dataSource, trackType, representation, false);
        return extractorWrapper == null ? null : extractorWrapper.getSampleFormats()[0];
    }

    public static ChunkIndex loadChunkIndex(DataSource dataSource, int trackType, Representation representation) throws IOException, InterruptedException {
        ChunkExtractorWrapper extractorWrapper = loadInitializationData(dataSource, trackType, representation, true);
        return extractorWrapper == null ? null : (ChunkIndex) extractorWrapper.getSeekMap();
    }

    private static ChunkExtractorWrapper loadInitializationData(DataSource dataSource, int trackType, Representation representation, boolean loadIndex) throws IOException, InterruptedException {
        RangedUri initializationUri = representation.getInitializationUri();
        if (initializationUri == null) {
            return null;
        }
        RangedUri requestUri;
        ChunkExtractorWrapper extractorWrapper = newWrappedExtractor(trackType, representation.format);
        if (loadIndex) {
            RangedUri indexUri = representation.getIndexUri();
            if (indexUri == null) {
                return null;
            }
            requestUri = initializationUri.attemptMerge(indexUri, representation.baseUrl);
            if (requestUri == null) {
                loadInitializationData(dataSource, representation, extractorWrapper, initializationUri);
                requestUri = indexUri;
            }
        } else {
            requestUri = initializationUri;
        }
        loadInitializationData(dataSource, representation, extractorWrapper, requestUri);
        return extractorWrapper;
    }

    private static void loadInitializationData(DataSource dataSource, Representation representation, ChunkExtractorWrapper extractorWrapper, RangedUri requestUri) throws IOException, InterruptedException {
        new InitializationChunk(dataSource, new DataSpec(requestUri.resolveUri(representation.baseUrl), requestUri.start, requestUri.length, representation.getCacheKey()), representation.format, 0, null, extractorWrapper).load();
    }

    private static ChunkExtractorWrapper newWrappedExtractor(int trackType, Format format) {
        boolean isWebm;
        String mimeType = format.containerMimeType;
        if (!mimeType.startsWith(MimeTypes.VIDEO_WEBM)) {
            if (!mimeType.startsWith(MimeTypes.AUDIO_WEBM)) {
                isWebm = false;
                return new ChunkExtractorWrapper(isWebm ? new MatroskaExtractor() : new FragmentedMp4Extractor(), trackType, format);
            }
        }
        isWebm = true;
        if (isWebm) {
        }
        return new ChunkExtractorWrapper(isWebm ? new MatroskaExtractor() : new FragmentedMp4Extractor(), trackType, format);
    }

    private static Representation getFirstRepresentation(Period period, int type) {
        int index = period.getAdaptationSetIndex(type);
        Representation representation = null;
        if (index == -1) {
            return null;
        }
        List<Representation> representations = ((AdaptationSet) period.adaptationSets.get(index)).representations;
        if (!representations.isEmpty()) {
            representation = (Representation) representations.get(0);
        }
        return representation;
    }

    private DashUtil() {
    }
}
