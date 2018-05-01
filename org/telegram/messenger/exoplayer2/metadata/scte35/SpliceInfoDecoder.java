package org.telegram.messenger.exoplayer2.metadata.scte35;

import java.nio.ByteBuffer;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.Metadata.Entry;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoderException;
import org.telegram.messenger.exoplayer2.metadata.MetadataInputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class SpliceInfoDecoder
  implements MetadataDecoder
{
  private static final int TYPE_PRIVATE_COMMAND = 255;
  private static final int TYPE_SPLICE_INSERT = 5;
  private static final int TYPE_SPLICE_NULL = 0;
  private static final int TYPE_SPLICE_SCHEDULE = 4;
  private static final int TYPE_TIME_SIGNAL = 6;
  private final ParsableByteArray sectionData = new ParsableByteArray();
  private final ParsableBitArray sectionHeader = new ParsableBitArray();
  private TimestampAdjuster timestampAdjuster;
  
  public Metadata decode(MetadataInputBuffer paramMetadataInputBuffer)
    throws MetadataDecoderException
  {
    if ((this.timestampAdjuster == null) || (paramMetadataInputBuffer.subsampleOffsetUs != this.timestampAdjuster.getTimestampOffsetUs()))
    {
      this.timestampAdjuster = new TimestampAdjuster(paramMetadataInputBuffer.timeUs);
      this.timestampAdjuster.adjustSampleTimestamp(paramMetadataInputBuffer.timeUs - paramMetadataInputBuffer.subsampleOffsetUs);
    }
    paramMetadataInputBuffer = paramMetadataInputBuffer.data;
    byte[] arrayOfByte = paramMetadataInputBuffer.array();
    int i = paramMetadataInputBuffer.limit();
    this.sectionData.reset(arrayOfByte, i);
    this.sectionHeader.reset(arrayOfByte, i);
    this.sectionHeader.skipBits(39);
    long l = this.sectionHeader.readBits(1) << 32 | this.sectionHeader.readBits(32);
    this.sectionHeader.skipBits(20);
    i = this.sectionHeader.readBits(12);
    int j = this.sectionHeader.readBits(8);
    paramMetadataInputBuffer = null;
    this.sectionData.skipBytes(14);
    switch (j)
    {
    default: 
      if (paramMetadataInputBuffer != null) {
        break;
      }
    }
    for (paramMetadataInputBuffer = new Metadata(new Metadata.Entry[0]);; paramMetadataInputBuffer = new Metadata(new Metadata.Entry[] { paramMetadataInputBuffer }))
    {
      return paramMetadataInputBuffer;
      paramMetadataInputBuffer = new SpliceNullCommand();
      break;
      paramMetadataInputBuffer = SpliceScheduleCommand.parseFromSection(this.sectionData);
      break;
      paramMetadataInputBuffer = SpliceInsertCommand.parseFromSection(this.sectionData, l, this.timestampAdjuster);
      break;
      paramMetadataInputBuffer = TimeSignalCommand.parseFromSection(this.sectionData, l, this.timestampAdjuster);
      break;
      paramMetadataInputBuffer = PrivateCommand.parseFromSection(this.sectionData, i, l);
      break;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/metadata/scte35/SpliceInfoDecoder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */