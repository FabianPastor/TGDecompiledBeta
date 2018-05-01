package org.telegram.messenger.exoplayer2.extractor.ts;

import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.TrackIdGenerator;
import org.telegram.messenger.exoplayer2.text.cea.CeaUtil;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

final class SeiReader {
    private final List<Format> closedCaptionFormats;
    private final TrackOutput[] outputs;

    public SeiReader(List<Format> list) {
        this.closedCaptionFormats = list;
        this.outputs = new TrackOutput[list.size()];
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator trackIdGenerator) {
        for (int i = 0; i < this.outputs.length; i++) {
            boolean z;
            StringBuilder stringBuilder;
            trackIdGenerator.generateNewId();
            TrackOutput track = extractorOutput.track(trackIdGenerator.getTrackId(), 3);
            Format format = (Format) this.closedCaptionFormats.get(i);
            String str = format.sampleMimeType;
            if (!MimeTypes.APPLICATION_CEA608.equals(str)) {
                if (!MimeTypes.APPLICATION_CEA708.equals(str)) {
                    z = false;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Invalid closed caption mime type provided: ");
                    stringBuilder.append(str);
                    Assertions.checkArgument(z, stringBuilder.toString());
                    track.format(Format.createTextSampleFormat(format.id == null ? format.id : trackIdGenerator.getFormatId(), str, null, -1, format.selectionFlags, format.language, format.accessibilityChannel, null));
                    this.outputs[i] = track;
                }
            }
            z = true;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid closed caption mime type provided: ");
            stringBuilder.append(str);
            Assertions.checkArgument(z, stringBuilder.toString());
            if (format.id == null) {
            }
            track.format(Format.createTextSampleFormat(format.id == null ? format.id : trackIdGenerator.getFormatId(), str, null, -1, format.selectionFlags, format.language, format.accessibilityChannel, null));
            this.outputs[i] = track;
        }
    }

    public void consume(long j, ParsableByteArray parsableByteArray) {
        CeaUtil.consume(j, parsableByteArray, this.outputs);
    }
}
