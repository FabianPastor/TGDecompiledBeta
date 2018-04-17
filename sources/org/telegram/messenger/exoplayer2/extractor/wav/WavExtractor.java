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
    public static final ExtractorsFactory FACTORY = new C18411();
    private static final int MAX_INPUT_SIZE = 32768;
    private int bytesPerFrame;
    private ExtractorOutput extractorOutput;
    private int pendingBytes;
    private TrackOutput trackOutput;
    private WavHeader wavHeader;

    /* renamed from: org.telegram.messenger.exoplayer2.extractor.wav.WavExtractor$1 */
    static class C18411 implements ExtractorsFactory {
        C18411() {
        }

        public Extractor[] createExtractors() {
            return new Extractor[]{new WavExtractor()};
        }
    }

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return WavHeaderReader.peek(input) != null;
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = output.track(0, 1);
        this.wavHeader = null;
        output.endTracks();
    }

    public void seek(long position, long timeUs) {
        this.pendingBytes = 0;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        ExtractorInput extractorInput = input;
        if (this.wavHeader == null) {
            r0.wavHeader = WavHeaderReader.peek(input);
            if (r0.wavHeader == null) {
                throw new ParserException("Unsupported or unrecognized wav header.");
            }
            r0.trackOutput.format(Format.createAudioSampleFormat(null, MimeTypes.AUDIO_RAW, null, r0.wavHeader.getBitrate(), 32768, r0.wavHeader.getNumChannels(), r0.wavHeader.getSampleRateHz(), r0.wavHeader.getEncoding(), null, null, 0, null));
            r0.bytesPerFrame = r0.wavHeader.getBytesPerFrame();
        }
        if (!r0.wavHeader.hasDataBounds()) {
            WavHeaderReader.skipToData(extractorInput, r0.wavHeader);
            r0.extractorOutput.seekMap(r0.wavHeader);
        }
        int bytesAppended = r0.trackOutput.sampleData(extractorInput, 32768 - r0.pendingBytes, true);
        if (bytesAppended != -1) {
            r0.pendingBytes += bytesAppended;
        }
        int pendingFrames = r0.pendingBytes / r0.bytesPerFrame;
        if (pendingFrames > 0) {
            long timeUs = r0.wavHeader.getTimeUs(input.getPosition() - ((long) r0.pendingBytes));
            int size = r0.bytesPerFrame * pendingFrames;
            r0.pendingBytes -= size;
            r0.trackOutput.sampleMetadata(timeUs, 1, size, r0.pendingBytes, null);
        }
        if (bytesAppended == -1) {
            return -1;
        }
        return 0;
    }
}
