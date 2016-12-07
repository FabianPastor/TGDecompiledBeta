package org.telegram.messenger.exoplayer2.source.chunk;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.SeekMap;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class ChunkExtractorWrapper implements ExtractorOutput, TrackOutput {
    private final Extractor extractor;
    private boolean extractorInitialized;
    private final Format manifestFormat;
    private SingleTrackMetadataOutput metadataOutput;
    private final boolean preferManifestDrmInitData;
    private final boolean resendFormatOnInit;
    private boolean seenTrack;
    private int seenTrackId;
    private Format sentFormat;
    private TrackOutput trackOutput;

    public interface SingleTrackMetadataOutput {
        void seekMap(SeekMap seekMap);
    }

    public ChunkExtractorWrapper(Extractor extractor, Format manifestFormat, boolean preferManifestDrmInitData, boolean resendFormatOnInit) {
        this.extractor = extractor;
        this.manifestFormat = manifestFormat;
        this.preferManifestDrmInitData = preferManifestDrmInitData;
        this.resendFormatOnInit = resendFormatOnInit;
    }

    public void init(SingleTrackMetadataOutput metadataOutput, TrackOutput trackOutput) {
        this.metadataOutput = metadataOutput;
        this.trackOutput = trackOutput;
        if (this.extractorInitialized) {
            this.extractor.seek(0);
            if (this.resendFormatOnInit && this.sentFormat != null) {
                trackOutput.format(this.sentFormat);
                return;
            }
            return;
        }
        this.extractor.init(this);
        this.extractorInitialized = true;
    }

    public int read(ExtractorInput input) throws IOException, InterruptedException {
        boolean z = true;
        int result = this.extractor.read(input, null);
        if (result == 1) {
            z = false;
        }
        Assertions.checkState(z);
        return result;
    }

    public TrackOutput track(int id) {
        boolean z = !this.seenTrack || this.seenTrackId == id;
        Assertions.checkState(z);
        this.seenTrack = true;
        this.seenTrackId = id;
        return this;
    }

    public void endTracks() {
        Assertions.checkState(this.seenTrack);
    }

    public void seekMap(SeekMap seekMap) {
        this.metadataOutput.seekMap(seekMap);
    }

    public void format(Format format) {
        this.sentFormat = format.copyWithManifestFormatInfo(this.manifestFormat, this.preferManifestDrmInitData);
        this.trackOutput.format(this.sentFormat);
    }

    public int sampleData(ExtractorInput input, int length, boolean allowEndOfInput) throws IOException, InterruptedException {
        return this.trackOutput.sampleData(input, length, allowEndOfInput);
    }

    public void sampleData(ParsableByteArray data, int length) {
        this.trackOutput.sampleData(data, length);
    }

    public void sampleMetadata(long timeUs, int flags, int size, int offset, byte[] encryptionKey) {
        this.trackOutput.sampleMetadata(timeUs, flags, size, offset, encryptionKey);
    }
}
