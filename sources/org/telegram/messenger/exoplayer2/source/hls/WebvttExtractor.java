package org.telegram.messenger.exoplayer2.source.hls;

import android.text.TextUtils;
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.exoplayer2.C0542C;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.text.webvtt.WebvttParserUtil;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

final class WebvttExtractor implements Extractor {
    private static final Pattern LOCAL_TIMESTAMP = Pattern.compile("LOCAL:([^,]+)");
    private static final Pattern MEDIA_TIMESTAMP = Pattern.compile("MPEGTS:(\\d+)");
    private final String language;
    private ExtractorOutput output;
    private byte[] sampleData = new byte[1024];
    private final ParsableByteArray sampleDataWrapper = new ParsableByteArray();
    private int sampleSize;
    private final TimestampAdjuster timestampAdjuster;

    public void release() {
    }

    public WebvttExtractor(String str, TimestampAdjuster timestampAdjuster) {
        this.language = str;
        this.timestampAdjuster = timestampAdjuster;
    }

    public boolean sniff(ExtractorInput extractorInput) throws IOException, InterruptedException {
        throw new IllegalStateException();
    }

    public void init(ExtractorOutput extractorOutput) {
        this.output = extractorOutput;
        extractorOutput.seekMap(new Unseekable(C0542C.TIME_UNSET));
    }

    public void seek(long j, long j2) {
        throw new IllegalStateException();
    }

    public int read(ExtractorInput extractorInput, PositionHolder positionHolder) throws IOException, InterruptedException {
        Object length = (int) extractorInput.getLength();
        if (this.sampleSize == this.sampleData.length) {
            this.sampleData = Arrays.copyOf(this.sampleData, ((length != -1 ? length : this.sampleData.length) * 3) / 2);
        }
        extractorInput = extractorInput.read(this.sampleData, this.sampleSize, this.sampleData.length - this.sampleSize);
        if (extractorInput != -1) {
            this.sampleSize += extractorInput;
            if (length == -1 || this.sampleSize != length) {
                return null;
            }
        }
        processSample();
        return -1;
    }

    private void processSample() throws ParserException {
        ParsableByteArray parsableByteArray = new ParsableByteArray(this.sampleData);
        try {
            WebvttParserUtil.validateWebvttHeaderLine(parsableByteArray);
            long j = 0;
            long j2 = j;
            while (true) {
                String readLine = parsableByteArray.readLine();
                if (TextUtils.isEmpty(readLine)) {
                    break;
                } else if (readLine.startsWith("X-TIMESTAMP-MAP")) {
                    Matcher matcher = LOCAL_TIMESTAMP.matcher(readLine);
                    StringBuilder stringBuilder;
                    if (matcher.find()) {
                        Matcher matcher2 = MEDIA_TIMESTAMP.matcher(readLine);
                        if (matcher2.find()) {
                            j2 = WebvttParserUtil.parseTimestampUs(matcher.group(1));
                            j = TimestampAdjuster.ptsToUs(Long.parseLong(matcher2.group(1)));
                        } else {
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("X-TIMESTAMP-MAP doesn't contain media timestamp: ");
                            stringBuilder.append(readLine);
                            throw new ParserException(stringBuilder.toString());
                        }
                    }
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("X-TIMESTAMP-MAP doesn't contain local timestamp: ");
                    stringBuilder.append(readLine);
                    throw new ParserException(stringBuilder.toString());
                }
            }
            Matcher findNextCueHeader = WebvttParserUtil.findNextCueHeader(parsableByteArray);
            if (findNextCueHeader == null) {
                buildTrackOutput(0);
                return;
            }
            long parseTimestampUs = WebvttParserUtil.parseTimestampUs(findNextCueHeader.group(1));
            long adjustTsTimestamp = this.timestampAdjuster.adjustTsTimestamp(TimestampAdjuster.usToPts((parseTimestampUs + j) - j2));
            TrackOutput buildTrackOutput = buildTrackOutput(adjustTsTimestamp - parseTimestampUs);
            this.sampleDataWrapper.reset(this.sampleData, this.sampleSize);
            buildTrackOutput.sampleData(this.sampleDataWrapper, this.sampleSize);
            buildTrackOutput.sampleMetadata(adjustTsTimestamp, 1, this.sampleSize, 0, null);
        } catch (Throwable e) {
            throw new ParserException(e);
        }
    }

    private TrackOutput buildTrackOutput(long j) {
        TrackOutput track = this.output.track(0, 3);
        track.format(Format.createTextSampleFormat(null, MimeTypes.TEXT_VTT, null, -1, 0, this.language, null, j));
        this.output.endTracks();
        return track;
    }
}
