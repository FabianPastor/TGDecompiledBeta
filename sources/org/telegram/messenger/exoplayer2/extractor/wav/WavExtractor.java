package org.telegram.messenger.exoplayer2.extractor.wav;

import java.io.IOException;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class WavExtractor implements Extractor {
    public static final ExtractorsFactory FACTORY = new C18471();
    private static final int MAX_INPUT_SIZE = 32768;
    private int bytesPerFrame;
    private ExtractorOutput extractorOutput;
    private int pendingBytes;
    private TrackOutput trackOutput;
    private WavHeader wavHeader;

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.wav.WavExtractor$1 */
    static class C18471 implements ExtractorsFactory {
        C18471() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new WavExtractor()};
        }
    }

    public void release() {
    }

    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        return WavHeaderReader.peek(extractorInput) != null ? true : null;
    }

    public void init(ExtractorOutput extractorOutput) {
        this.extractorOutput = extractorOutput;
        this.trackOutput = extractorOutput.track(0, 1);
        this.wavHeader = null;
        extractorOutput.endTracks();
    }

    public void seek(long j, long j2) {
        this.pendingBytes = 0;
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        ExtractorInput extractorInput2 = extractorInput;
        if (this.wavHeader == null) {
            r0.wavHeader = WavHeaderReader.peek(extractorInput);
            if (r0.wavHeader == null) {
                throw new ParserException("Unsupported or unrecognized wav header.");
            }
            r0.trackOutput.format(Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, r0.wavHeader.getBitrate(), 32768, r0.wavHeader.getNumChannels(), r0.wavHeader.getSampleRateHz(), r0.wavHeader.getEncoding(), null, null, 0, null));
            r0.bytesPerFrame = r0.wavHeader.getBytesPerFrame();
        }
        if (!r0.wavHeader.hasDataBounds()) {
            WavHeaderReader.skipToData(extractorInput2, r0.wavHeader);
            r0.extractorOutput.seekMap(r0.wavHeader);
        }
        int sampleData = r0.trackOutput.sampleData(extractorInput2, 32768 - r0.pendingBytes, true);
        if (sampleData != -1) {
            r0.pendingBytes += sampleData;
        }
        int i = r0.pendingBytes / r0.bytesPerFrame;
        if (i > 0) {
            long timeUs = r0.wavHeader.getTimeUs(extractorInput.getPosition() - ((long) r0.pendingBytes));
            int i2 = i * r0.bytesPerFrame;
            r0.pendingBytes -= i2;
            r0.trackOutput.sampleMetadata(timeUs, 1, i2, r0.pendingBytes, null);
        }
        if (sampleData == -1) {
            return -1;
        }
        return 0;
    }
}
