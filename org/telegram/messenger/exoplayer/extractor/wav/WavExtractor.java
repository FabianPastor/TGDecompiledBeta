package org.telegram.messenger.exoplayer.extractor.wav;

import java.io.IOException;
import org.telegram.messenger.exoplayer.MediaFormat;
import org.telegram.messenger.exoplayer.ParserException;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.extractor.TrackOutput;
import org.telegram.messenger.exoplayer.util.MimeTypes;

public final class WavExtractor implements Extractor, SeekMap {
    private static final int MAX_INPUT_SIZE = 32768;
    private int bytesPerFrame;
    private ExtractorOutput extractorOutput;
    private int pendingBytes;
    private TrackOutput trackOutput;
    private WavHeader wavHeader;

    public boolean sniff(ExtractorInput input) throws IOException, InterruptedException {
        return WavHeaderReader.peek(input) != null;
    }

    public void init(ExtractorOutput output) {
        this.extractorOutput = output;
        this.trackOutput = output.track(0);
        this.wavHeader = null;
        output.endTracks();
    }

    public void seek() {
        this.pendingBytes = 0;
    }

    public void release() {
    }

    public int read(ExtractorInput input, PositionHolder seekPosition) throws IOException, InterruptedException {
        if (this.wavHeader == null) {
            this.wavHeader = WavHeaderReader.peek(input);
            if (this.wavHeader == null) {
                throw new ParserException("Error initializing WavHeader. Did you sniff first?");
            }
            this.bytesPerFrame = this.wavHeader.getBytesPerFrame();
        }
        if (!this.wavHeader.hasDataBounds()) {
            WavHeaderReader.skipToData(input, this.wavHeader);
            this.trackOutput.format(MediaFormat.createAudioFormat(null, MimeTypes.AUDIO_RAW, this.wavHeader.getBitrate(), 32768, this.wavHeader.getDurationUs(), this.wavHeader.getNumChannels(), this.wavHeader.getSampleRateHz(), null, null, this.wavHeader.getEncoding()));
            this.extractorOutput.seekMap(this);
        }
        int bytesAppended = this.trackOutput.sampleData(input, 32768 - this.pendingBytes, true);
        if (bytesAppended != -1) {
            this.pendingBytes += bytesAppended;
        }
        int frameBytes = (this.pendingBytes / this.bytesPerFrame) * this.bytesPerFrame;
        if (frameBytes > 0) {
            long sampleStartPosition = input.getPosition() - ((long) this.pendingBytes);
            this.pendingBytes -= frameBytes;
            this.trackOutput.sampleMetadata(this.wavHeader.getTimeUs(sampleStartPosition), 1, frameBytes, this.pendingBytes, null);
        }
        if (bytesAppended == -1) {
            return -1;
        }
        return 0;
    }

    public boolean isSeekable() {
        return true;
    }

    public long getPosition(long timeUs) {
        return this.wavHeader.getPosition(timeUs);
    }
}
