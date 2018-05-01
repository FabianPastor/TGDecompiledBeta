package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.Log;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;

public final class PesReader
  implements TsPayloadReader
{
  private static final int HEADER_SIZE = 9;
  private static final int MAX_HEADER_EXTENSION_SIZE = 10;
  private static final int PES_SCRATCH_SIZE = 10;
  private static final int STATE_FINDING_HEADER = 0;
  private static final int STATE_READING_BODY = 3;
  private static final int STATE_READING_HEADER = 1;
  private static final int STATE_READING_HEADER_EXTENSION = 2;
  private static final String TAG = "PesReader";
  private int bytesRead;
  private boolean dataAlignmentIndicator;
  private boolean dtsFlag;
  private int extendedHeaderLength;
  private int payloadSize;
  private final ParsableBitArray pesScratch;
  private boolean ptsFlag;
  private final ElementaryStreamReader reader;
  private boolean seenFirstDts;
  private int state;
  private long timeUs;
  private TimestampAdjuster timestampAdjuster;
  
  public PesReader(ElementaryStreamReader paramElementaryStreamReader)
  {
    this.reader = paramElementaryStreamReader;
    this.pesScratch = new ParsableBitArray(new byte[10]);
    this.state = 0;
  }
  
  private boolean continueRead(ParsableByteArray paramParsableByteArray, byte[] paramArrayOfByte, int paramInt)
  {
    boolean bool = true;
    int i = Math.min(paramParsableByteArray.bytesLeft(), paramInt - this.bytesRead);
    if (i <= 0) {
      return bool;
    }
    if (paramArrayOfByte == null) {
      paramParsableByteArray.skipBytes(i);
    }
    for (;;)
    {
      this.bytesRead += i;
      if (this.bytesRead == paramInt) {
        break;
      }
      bool = false;
      break;
      paramParsableByteArray.readBytes(paramArrayOfByte, this.bytesRead, i);
    }
  }
  
  private boolean parseHeader()
  {
    boolean bool = false;
    this.pesScratch.setPosition(0);
    int i = this.pesScratch.readBits(24);
    if (i != 1)
    {
      Log.w("PesReader", "Unexpected start code prefix: " + i);
      this.payloadSize = -1;
      return bool;
    }
    this.pesScratch.skipBits(8);
    i = this.pesScratch.readBits(16);
    this.pesScratch.skipBits(5);
    this.dataAlignmentIndicator = this.pesScratch.readBit();
    this.pesScratch.skipBits(2);
    this.ptsFlag = this.pesScratch.readBit();
    this.dtsFlag = this.pesScratch.readBit();
    this.pesScratch.skipBits(6);
    this.extendedHeaderLength = this.pesScratch.readBits(8);
    if (i == 0) {}
    for (this.payloadSize = -1;; this.payloadSize = (i + 6 - 9 - this.extendedHeaderLength))
    {
      bool = true;
      break;
    }
  }
  
  private void parseHeaderExtension()
  {
    this.pesScratch.setPosition(0);
    this.timeUs = -9223372036854775807L;
    if (this.ptsFlag)
    {
      this.pesScratch.skipBits(4);
      long l1 = this.pesScratch.readBits(3);
      this.pesScratch.skipBits(1);
      long l2 = this.pesScratch.readBits(15) << 15;
      this.pesScratch.skipBits(1);
      long l3 = this.pesScratch.readBits(15);
      this.pesScratch.skipBits(1);
      if ((!this.seenFirstDts) && (this.dtsFlag))
      {
        this.pesScratch.skipBits(4);
        long l4 = this.pesScratch.readBits(3);
        this.pesScratch.skipBits(1);
        long l5 = this.pesScratch.readBits(15) << 15;
        this.pesScratch.skipBits(1);
        long l6 = this.pesScratch.readBits(15);
        this.pesScratch.skipBits(1);
        this.timestampAdjuster.adjustTsTimestamp(l4 << 30 | l5 | l6);
        this.seenFirstDts = true;
      }
      this.timeUs = this.timestampAdjuster.adjustTsTimestamp(l1 << 30 | l2 | l3);
    }
  }
  
  private void setState(int paramInt)
  {
    this.state = paramInt;
    this.bytesRead = 0;
  }
  
  public final void consume(ParsableByteArray paramParsableByteArray, boolean paramBoolean)
    throws ParserException
  {
    if (paramBoolean) {
      switch (this.state)
      {
      case 0: 
      case 1: 
      default: 
        setState(1);
      }
    }
    for (;;)
    {
      if (paramParsableByteArray.bytesLeft() > 0)
      {
        int i;
        switch (this.state)
        {
        default: 
          break;
        case 0: 
          paramParsableByteArray.skipBytes(paramParsableByteArray.bytesLeft());
          continue;
          Log.w("PesReader", "Unexpected start indicator reading extended header");
          break;
          if (this.payloadSize != -1) {
            Log.w("PesReader", "Unexpected start indicator: expected " + this.payloadSize + " more bytes");
          }
          this.reader.packetFinished();
          break;
        case 1: 
          if (continueRead(paramParsableByteArray, this.pesScratch.data, 9))
          {
            if (parseHeader()) {}
            for (i = 2;; i = 0)
            {
              setState(i);
              break;
            }
          }
          break;
        case 2: 
          i = Math.min(10, this.extendedHeaderLength);
          if ((continueRead(paramParsableByteArray, this.pesScratch.data, i)) && (continueRead(paramParsableByteArray, null, this.extendedHeaderLength)))
          {
            parseHeaderExtension();
            this.reader.packetStarted(this.timeUs, this.dataAlignmentIndicator);
            setState(3);
          }
          break;
        case 3: 
          int j = paramParsableByteArray.bytesLeft();
          if (this.payloadSize == -1) {}
          for (i = 0;; i = j - this.payloadSize)
          {
            int k = j;
            if (i > 0)
            {
              k = j - i;
              paramParsableByteArray.setLimit(paramParsableByteArray.getPosition() + k);
            }
            this.reader.consume(paramParsableByteArray);
            if (this.payloadSize == -1) {
              break;
            }
            this.payloadSize -= k;
            if (this.payloadSize != 0) {
              break;
            }
            this.reader.packetFinished();
            setState(1);
            break;
          }
        }
      }
    }
  }
  
  public void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    this.timestampAdjuster = paramTimestampAdjuster;
    this.reader.createTracks(paramExtractorOutput, paramTrackIdGenerator);
  }
  
  public final void seek()
  {
    this.state = 0;
    this.bytesRead = 0;
    this.seenFirstDts = false;
    this.reader.seek();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/PesReader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */