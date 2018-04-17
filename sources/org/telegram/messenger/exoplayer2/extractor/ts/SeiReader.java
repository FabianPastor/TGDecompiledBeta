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

    public SeiReader(List<Format> closedCaptionFormats) {
        this.closedCaptionFormats = closedCaptionFormats;
        this.outputs = new TrackOutput[closedCaptionFormats.size()];
    }

    public void createTracks(ExtractorOutput extractorOutput, TrackIdGenerator idGenerator) {
        for (int i = 0; i < this.outputs.length; i++) {
            boolean z;
            StringBuilder stringBuilder;
            idGenerator.generateNewId();
            TrackOutput output = extractorOutput.track(idGenerator.getTrackId(), 3);
            Format channelFormat = (Format) this.closedCaptionFormats.get(i);
            String channelMimeType = channelFormat.sampleMimeType;
            if (!MimeTypes.APPLICATION_CEA608.equals(channelMimeType)) {
                if (!MimeTypes.APPLICATION_CEA708.equals(channelMimeType)) {
                    z = false;
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("Invalid closed caption mime type provided: ");
                    stringBuilder.append(channelMimeType);
                    Assertions.checkArgument(z, stringBuilder.toString());
                    output.format(Format.createTextSampleFormat(channelFormat.id == null ? channelFormat.id : idGenerator.getFormatId(), channelMimeType, null, -1, channelFormat.selectionFlags, channelFormat.language, channelFormat.accessibilityChannel, null));
                    this.outputs[i] = output;
                }
            }
            z = true;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid closed caption mime type provided: ");
            stringBuilder.append(channelMimeType);
            Assertions.checkArgument(z, stringBuilder.toString());
            if (channelFormat.id == null) {
            }
            output.format(Format.createTextSampleFormat(channelFormat.id == null ? channelFormat.id : idGenerator.getFormatId(), channelMimeType, null, -1, channelFormat.selectionFlags, channelFormat.language, channelFormat.accessibilityChannel, null));
            this.outputs[i] = output;
        }
    }

    public void consume(long pesTimeUs, ParsableByteArray seiBuffer) {
        CeaUtil.consume(pesTimeUs, seiBuffer, this.outputs);
    }
}
