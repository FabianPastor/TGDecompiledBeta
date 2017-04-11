package org.telegram.messenger.exoplayer2.source.dash;

import android.net.Uri;
import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ChunkIndex;
import org.telegram.messenger.exoplayer2.extractor.mkv.MatroskaExtractor;
import org.telegram.messenger.exoplayer2.extractor.mp4.FragmentedMp4Extractor;
import org.telegram.messenger.exoplayer2.source.chunk.ChunkExtractorWrapper;
import org.telegram.messenger.exoplayer2.source.chunk.InitializationChunk;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifest;
import org.telegram.messenger.exoplayer2.source.dash.manifest.DashManifestParser;
import org.telegram.messenger.exoplayer2.source.dash.manifest.RangedUri;
import org.telegram.messenger.exoplayer2.source.dash.manifest.Representation;
import org.telegram.messenger.exoplayer2.upstream.DataSource;
import org.telegram.messenger.exoplayer2.upstream.DataSourceInputStream;
import org.telegram.messenger.exoplayer2.upstream.DataSpec;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class DashUtil {
    public static DashManifest loadManifest(DataSource dataSource, String manifestUriString) throws IOException {
        InputStream inputStream = new DataSourceInputStream(dataSource, new DataSpec(Uri.parse(manifestUriString), 2));
        try {
            inputStream.open();
            DashManifest parse = new DashManifestParser().parse(dataSource.getUri(), inputStream);
            return parse;
        } finally {
            inputStream.close();
        }
    }

    public static Format loadSampleFormat(DataSource dataSource, Representation representation) throws IOException, InterruptedException {
        ChunkExtractorWrapper extractorWrapper = loadInitializationData(dataSource, representation, false);
        return extractorWrapper == null ? null : extractorWrapper.getSampleFormats()[0];
    }

    public static ChunkIndex loadChunkIndex(DataSource dataSource, Representation representation) throws IOException, InterruptedException {
        ChunkExtractorWrapper extractorWrapper = loadInitializationData(dataSource, representation, true);
        return extractorWrapper == null ? null : (ChunkIndex) extractorWrapper.getSeekMap();
    }

    private static ChunkExtractorWrapper loadInitializationData(DataSource dataSource, Representation representation, boolean loadIndex) throws IOException, InterruptedException {
        RangedUri initializationUri = representation.getInitializationUri();
        if (initializationUri == null) {
            return null;
        }
        RangedUri requestUri;
        ChunkExtractorWrapper extractorWrapper = newWrappedExtractor(representation.format);
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

    private static ChunkExtractorWrapper newWrappedExtractor(Format format) {
        String mimeType = format.containerMimeType;
        boolean isWebm = mimeType.startsWith(MimeTypes.VIDEO_WEBM) || mimeType.startsWith(MimeTypes.AUDIO_WEBM);
        return new ChunkExtractorWrapper(isWebm ? new MatroskaExtractor() : new FragmentedMp4Extractor(), format);
    }

    private DashUtil() {
    }
}
