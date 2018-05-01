package org.telegram.messenger.exoplayer2.extractor.ts;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class SpliceInfoSectionReader
  implements SectionPayloadReader
{
  private boolean formatDeclared;
  private TrackOutput output;
  private TimestampAdjuster timestampAdjuster;
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    if (!this.formatDeclared) {
      if (this.timestampAdjuster.getTimestampOffsetUs() != -9223372036854775807L) {}
    }
    for (;;)
    {
      return;
      this.output.format(Format.createSampleFormat(null, "application/x-scte35", this.timestampAdjuster.getTimestampOffsetUs()));
      this.formatDeclared = true;
      int i = paramParsableByteArray.bytesLeft();
      this.output.sampleData(paramParsableByteArray, i);
      this.output.sampleMetadata(this.timestampAdjuster.getLastAdjustedTimestampUs(), 1, i, 0, null);
    }
  }
  
  public void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    this.timestampAdjuster = paramTimestampAdjuster;
    paramTrackIdGenerator.generateNewId();
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 4);
    this.output.format(Format.createSampleFormat(paramTrackIdGenerator.getFormatId(), "application/x-scte35", null, -1, null));
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/SpliceInfoSectionReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */