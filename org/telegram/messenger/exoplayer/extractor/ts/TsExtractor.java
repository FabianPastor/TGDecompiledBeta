package org.telegram.messenger.exoplayer.extractor.ts;

import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import java.io.IOException;
import org.telegram.messenger.exoplayer.extractor.DummyTrackOutput;
import org.telegram.messenger.exoplayer.extractor.Extractor;
import org.telegram.messenger.exoplayer.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer.extractor.PositionHolder;
import org.telegram.messenger.exoplayer.extractor.SeekMap;
import org.telegram.messenger.exoplayer.util.ParsableBitArray;
import org.telegram.messenger.exoplayer.util.ParsableByteArray;
import org.telegram.messenger.exoplayer.util.Util;

public final class TsExtractor
  implements Extractor
{
  private static final long AC3_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("AC-3");
  private static final long E_AC3_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("EAC3");
  private static final long HEVC_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("HEVC");
  private static final String TAG = "TsExtractor";
  private static final int TS_PACKET_SIZE = 188;
  private static final int TS_PAT_PID = 0;
  private static final int TS_STREAM_TYPE_AAC = 15;
  private static final int TS_STREAM_TYPE_AC3 = 129;
  private static final int TS_STREAM_TYPE_DTS = 138;
  private static final int TS_STREAM_TYPE_EIA608 = 256;
  private static final int TS_STREAM_TYPE_E_AC3 = 135;
  private static final int TS_STREAM_TYPE_H262 = 2;
  private static final int TS_STREAM_TYPE_H264 = 27;
  private static final int TS_STREAM_TYPE_H265 = 36;
  private static final int TS_STREAM_TYPE_HDMV_DTS = 130;
  private static final int TS_STREAM_TYPE_ID3 = 21;
  private static final int TS_STREAM_TYPE_MPA = 3;
  private static final int TS_STREAM_TYPE_MPA_LSF = 4;
  private static final int TS_SYNC_BYTE = 71;
  public static final int WORKAROUND_ALLOW_NON_IDR_KEYFRAMES = 1;
  public static final int WORKAROUND_DETECT_ACCESS_UNITS = 8;
  public static final int WORKAROUND_IGNORE_AAC_STREAM = 2;
  public static final int WORKAROUND_IGNORE_H264_STREAM = 4;
  Id3Reader id3Reader;
  private ExtractorOutput output;
  private final PtsTimestampAdjuster ptsTimestampAdjuster;
  final SparseBooleanArray streamTypes;
  private final ParsableByteArray tsPacketBuffer;
  final SparseArray<TsPayloadReader> tsPayloadReaders;
  private final ParsableBitArray tsScratch;
  private final int workaroundFlags;
  
  public TsExtractor()
  {
    this(new PtsTimestampAdjuster(0L));
  }
  
  public TsExtractor(PtsTimestampAdjuster paramPtsTimestampAdjuster)
  {
    this(paramPtsTimestampAdjuster, 0);
  }
  
  public TsExtractor(PtsTimestampAdjuster paramPtsTimestampAdjuster, int paramInt)
  {
    this.ptsTimestampAdjuster = paramPtsTimestampAdjuster;
    this.workaroundFlags = paramInt;
    this.tsPacketBuffer = new ParsableByteArray(188);
    this.tsScratch = new ParsableBitArray(new byte[3]);
    this.tsPayloadReaders = new SparseArray();
    this.tsPayloadReaders.put(0, new PatReader());
    this.streamTypes = new SparseBooleanArray();
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.output = paramExtractorOutput;
    paramExtractorOutput.seekMap(SeekMap.UNSEEKABLE);
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    int j = 0;
    int i;
    if (!paramExtractorInput.readFully(this.tsPacketBuffer.data, 0, 188, true)) {
      i = -1;
    }
    boolean bool1;
    do
    {
      int k;
      boolean bool3;
      do
      {
        do
        {
          return i;
          this.tsPacketBuffer.setPosition(0);
          this.tsPacketBuffer.setLimit(188);
          i = j;
        } while (this.tsPacketBuffer.readUnsignedByte() != 71);
        this.tsPacketBuffer.readBytes(this.tsScratch, 3);
        this.tsScratch.skipBits(1);
        bool1 = this.tsScratch.readBit();
        this.tsScratch.skipBits(1);
        k = this.tsScratch.readBits(13);
        this.tsScratch.skipBits(2);
        boolean bool2 = this.tsScratch.readBit();
        bool3 = this.tsScratch.readBit();
        if (bool2)
        {
          i = this.tsPacketBuffer.readUnsignedByte();
          this.tsPacketBuffer.skipBytes(i);
        }
        i = j;
      } while (!bool3);
      paramExtractorInput = (TsPayloadReader)this.tsPayloadReaders.get(k);
      i = j;
    } while (paramExtractorInput == null);
    paramExtractorInput.consume(this.tsPacketBuffer, bool1, this.output);
    return 0;
  }
  
  public void release() {}
  
  public void seek()
  {
    this.ptsTimestampAdjuster.reset();
    int i = 0;
    while (i < this.tsPayloadReaders.size())
    {
      ((TsPayloadReader)this.tsPayloadReaders.valueAt(i)).seek();
      i += 1;
    }
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    byte[] arrayOfByte = new byte[1];
    int i = 0;
    while (i < 5)
    {
      paramExtractorInput.peekFully(arrayOfByte, 0, 1);
      if ((arrayOfByte[0] & 0xFF) != 71) {
        return false;
      }
      paramExtractorInput.advancePeekPosition(187);
      i += 1;
    }
    return true;
  }
  
  private class PatReader
    extends TsExtractor.TsPayloadReader
  {
    private final ParsableBitArray patScratch = new ParsableBitArray(new byte[4]);
    
    public PatReader()
    {
      super();
    }
    
    public void consume(ParsableByteArray paramParsableByteArray, boolean paramBoolean, ExtractorOutput paramExtractorOutput)
    {
      if (paramBoolean) {
        paramParsableByteArray.skipBytes(paramParsableByteArray.readUnsignedByte());
      }
      paramParsableByteArray.readBytes(this.patScratch, 3);
      this.patScratch.skipBits(12);
      int i = this.patScratch.readBits(12);
      paramParsableByteArray.skipBytes(5);
      int j = (i - 9) / 4;
      i = 0;
      if (i < j)
      {
        paramParsableByteArray.readBytes(this.patScratch, 4);
        int k = this.patScratch.readBits(16);
        this.patScratch.skipBits(3);
        if (k == 0) {
          this.patScratch.skipBits(13);
        }
        for (;;)
        {
          i += 1;
          break;
          k = this.patScratch.readBits(13);
          TsExtractor.this.tsPayloadReaders.put(k, new TsExtractor.PmtReader(TsExtractor.this));
        }
      }
    }
    
    public void seek() {}
  }
  
  private static final class PesReader
    extends TsExtractor.TsPayloadReader
  {
    private static final int HEADER_SIZE = 9;
    private static final int MAX_HEADER_EXTENSION_SIZE = 10;
    private static final int PES_SCRATCH_SIZE = 10;
    private static final int STATE_FINDING_HEADER = 0;
    private static final int STATE_READING_BODY = 3;
    private static final int STATE_READING_HEADER = 1;
    private static final int STATE_READING_HEADER_EXTENSION = 2;
    private int bytesRead;
    private boolean dataAlignmentIndicator;
    private boolean dtsFlag;
    private int extendedHeaderLength;
    private int payloadSize;
    private final ElementaryStreamReader pesPayloadReader;
    private final ParsableBitArray pesScratch;
    private boolean ptsFlag;
    private final PtsTimestampAdjuster ptsTimestampAdjuster;
    private boolean seenFirstDts;
    private int state;
    private long timeUs;
    
    public PesReader(ElementaryStreamReader paramElementaryStreamReader, PtsTimestampAdjuster paramPtsTimestampAdjuster)
    {
      super();
      this.pesPayloadReader = paramElementaryStreamReader;
      this.ptsTimestampAdjuster = paramPtsTimestampAdjuster;
      this.pesScratch = new ParsableBitArray(new byte[10]);
      this.state = 0;
    }
    
    private boolean continueRead(ParsableByteArray paramParsableByteArray, byte[] paramArrayOfByte, int paramInt)
    {
      int i = Math.min(paramParsableByteArray.bytesLeft(), paramInt - this.bytesRead);
      if (i <= 0) {
        return true;
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
        return false;
        paramParsableByteArray.readBytes(paramArrayOfByte, this.bytesRead, i);
      }
    }
    
    private boolean parseHeader()
    {
      this.pesScratch.setPosition(0);
      int i = this.pesScratch.readBits(24);
      if (i != 1)
      {
        Log.w("TsExtractor", "Unexpected start code prefix: " + i);
        this.payloadSize = -1;
        return false;
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
      for (this.payloadSize = -1;; this.payloadSize = (i + 6 - 9 - this.extendedHeaderLength)) {
        return true;
      }
    }
    
    private void parseHeaderExtension()
    {
      this.pesScratch.setPosition(0);
      this.timeUs = -1L;
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
          this.ptsTimestampAdjuster.adjustTimestamp(l4 << 30 | l5 | l6);
          this.seenFirstDts = true;
        }
        this.timeUs = this.ptsTimestampAdjuster.adjustTimestamp(l1 << 30 | l2 | l3);
      }
    }
    
    private void setState(int paramInt)
    {
      this.state = paramInt;
      this.bytesRead = 0;
    }
    
    public void consume(ParsableByteArray paramParsableByteArray, boolean paramBoolean, ExtractorOutput paramExtractorOutput)
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
            Log.w("TsExtractor", "Unexpected start indicator reading extended header");
            break;
            if (this.payloadSize != -1) {
              Log.w("TsExtractor", "Unexpected start indicator: expected " + this.payloadSize + " more bytes");
            }
            this.pesPayloadReader.packetFinished();
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
              this.pesPayloadReader.packetStarted(this.timeUs, this.dataAlignmentIndicator);
              setState(3);
            }
            break;
          case 3: 
            int k = paramParsableByteArray.bytesLeft();
            if (this.payloadSize == -1) {}
            for (i = 0;; i = k - this.payloadSize)
            {
              int j = k;
              if (i > 0)
              {
                j = k - i;
                paramParsableByteArray.setLimit(paramParsableByteArray.getPosition() + j);
              }
              this.pesPayloadReader.consume(paramParsableByteArray);
              if (this.payloadSize == -1) {
                break;
              }
              this.payloadSize -= j;
              if (this.payloadSize != 0) {
                break;
              }
              this.pesPayloadReader.packetFinished();
              setState(1);
              break;
            }
          }
        }
      }
    }
    
    public void seek()
    {
      this.state = 0;
      this.bytesRead = 0;
      this.seenFirstDts = false;
      this.pesPayloadReader.seek();
    }
  }
  
  private class PmtReader
    extends TsExtractor.TsPayloadReader
  {
    private final ParsableBitArray pmtScratch = new ParsableBitArray(new byte[5]);
    private int sectionBytesRead;
    private final ParsableByteArray sectionData = new ParsableByteArray();
    private int sectionLength;
    
    public PmtReader()
    {
      super();
    }
    
    private int readPrivateDataStreamType(ParsableByteArray paramParsableByteArray, int paramInt)
    {
      int i = -1;
      int j = paramParsableByteArray.getPosition() + paramInt;
      paramInt = i;
      int m;
      int k;
      long l;
      if (paramParsableByteArray.getPosition() < j)
      {
        m = paramParsableByteArray.readUnsignedByte();
        k = paramParsableByteArray.readUnsignedByte();
        if (m != 5) {
          break label99;
        }
        l = paramParsableByteArray.readUnsignedInt();
        if (l != TsExtractor.AC3_FORMAT_IDENTIFIER) {
          break label66;
        }
        paramInt = 129;
      }
      for (;;)
      {
        paramParsableByteArray.setPosition(j);
        return paramInt;
        label66:
        if (l == TsExtractor.E_AC3_FORMAT_IDENTIFIER)
        {
          paramInt = 135;
        }
        else
        {
          paramInt = i;
          if (l == TsExtractor.HEVC_FORMAT_IDENTIFIER) {
            paramInt = 36;
          }
        }
      }
      label99:
      if (m == 106) {
        paramInt = 129;
      }
      for (;;)
      {
        paramParsableByteArray.skipBytes(k);
        i = paramInt;
        break;
        if (m == 122)
        {
          paramInt = 135;
        }
        else
        {
          paramInt = i;
          if (m == 123) {
            paramInt = 138;
          }
        }
      }
    }
    
    public void consume(ParsableByteArray paramParsableByteArray, boolean paramBoolean, ExtractorOutput paramExtractorOutput)
    {
      if (paramBoolean)
      {
        paramParsableByteArray.skipBytes(paramParsableByteArray.readUnsignedByte());
        paramParsableByteArray.readBytes(this.pmtScratch, 3);
        this.pmtScratch.skipBits(12);
        this.sectionLength = this.pmtScratch.readBits(12);
        if (this.sectionData.capacity() >= this.sectionLength) {
          break label132;
        }
        this.sectionData.reset(new byte[this.sectionLength], this.sectionLength);
      }
      for (;;)
      {
        i = Math.min(paramParsableByteArray.bytesLeft(), this.sectionLength - this.sectionBytesRead);
        paramParsableByteArray.readBytes(this.sectionData.data, this.sectionBytesRead, i);
        this.sectionBytesRead += i;
        if (this.sectionBytesRead >= this.sectionLength) {
          break;
        }
        return;
        label132:
        this.sectionData.reset();
        this.sectionData.setLimit(this.sectionLength);
      }
      this.sectionData.skipBytes(7);
      this.sectionData.readBytes(this.pmtScratch, 2);
      this.pmtScratch.skipBits(4);
      int i = this.pmtScratch.readBits(12);
      this.sectionData.skipBytes(i);
      if (TsExtractor.this.id3Reader == null) {
        TsExtractor.this.id3Reader = new Id3Reader(paramExtractorOutput.track(21));
      }
      i = this.sectionLength - 9 - i - 4;
      while (i > 0)
      {
        this.sectionData.readBytes(this.pmtScratch, 5);
        int j = this.pmtScratch.readBits(8);
        this.pmtScratch.skipBits(3);
        int m = this.pmtScratch.readBits(13);
        this.pmtScratch.skipBits(4);
        int k = this.pmtScratch.readBits(12);
        if (j == 6)
        {
          j = readPrivateDataStreamType(this.sectionData, k);
          label333:
          k = i - (k + 5);
          i = k;
          if (TsExtractor.this.streamTypes.get(j)) {
            continue;
          }
          switch (j)
          {
          default: 
            paramParsableByteArray = null;
          }
        }
        for (;;)
        {
          i = k;
          if (paramParsableByteArray == null) {
            break;
          }
          TsExtractor.this.streamTypes.put(j, true);
          TsExtractor.this.tsPayloadReaders.put(m, new TsExtractor.PesReader(paramParsableByteArray, TsExtractor.this.ptsTimestampAdjuster));
          i = k;
          break;
          this.sectionData.skipBytes(k);
          break label333;
          paramParsableByteArray = new MpegAudioReader(paramExtractorOutput.track(3));
          continue;
          paramParsableByteArray = new MpegAudioReader(paramExtractorOutput.track(4));
          continue;
          if ((TsExtractor.this.workaroundFlags & 0x2) != 0) {}
          for (paramParsableByteArray = null;; paramParsableByteArray = new AdtsReader(paramExtractorOutput.track(15), new DummyTrackOutput())) {
            break;
          }
          paramParsableByteArray = new Ac3Reader(paramExtractorOutput.track(129), false);
          continue;
          paramParsableByteArray = new Ac3Reader(paramExtractorOutput.track(135), true);
          continue;
          paramParsableByteArray = new DtsReader(paramExtractorOutput.track(138));
          continue;
          paramParsableByteArray = new H262Reader(paramExtractorOutput.track(2));
          continue;
          if ((TsExtractor.this.workaroundFlags & 0x4) != 0)
          {
            paramParsableByteArray = null;
          }
          else
          {
            paramParsableByteArray = paramExtractorOutput.track(27);
            SeiReader localSeiReader = new SeiReader(paramExtractorOutput.track(256));
            if ((TsExtractor.this.workaroundFlags & 0x1) != 0)
            {
              paramBoolean = true;
              label746:
              if ((TsExtractor.this.workaroundFlags & 0x8) == 0) {
                break label784;
              }
            }
            label784:
            for (boolean bool = true;; bool = false)
            {
              paramParsableByteArray = new H264Reader(paramParsableByteArray, localSeiReader, paramBoolean, bool);
              break;
              paramBoolean = false;
              break label746;
            }
            paramParsableByteArray = new H265Reader(paramExtractorOutput.track(36), new SeiReader(paramExtractorOutput.track(256)));
            continue;
            paramParsableByteArray = TsExtractor.this.id3Reader;
          }
        }
      }
      paramExtractorOutput.endTracks();
    }
    
    public void seek() {}
  }
  
  private static abstract class TsPayloadReader
  {
    public abstract void consume(ParsableByteArray paramParsableByteArray, boolean paramBoolean, ExtractorOutput paramExtractorOutput);
    
    public abstract void seek();
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/extractor/ts/TsExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */