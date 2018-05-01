package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.ParserException;
import org.telegram.messenger.exoplayer2.extractor.Extractor;
import org.telegram.messenger.exoplayer2.extractor.ExtractorInput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.ExtractorsFactory;
import org.telegram.messenger.exoplayer2.extractor.PositionHolder;
import org.telegram.messenger.exoplayer2.extractor.SeekMap.Unseekable;
import org.telegram.messenger.exoplayer2.util.Assertions;
import org.telegram.messenger.exoplayer2.util.ParsableBitArray;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.TimestampAdjuster;
import org.telegram.messenger.exoplayer2.util.Util;

public final class TsExtractor
  implements Extractor
{
  private static final long AC3_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("AC-3");
  private static final int BUFFER_SIZE = 9400;
  private static final long E_AC3_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("EAC3");
  public static final ExtractorsFactory FACTORY = new ExtractorsFactory()
  {
    public Extractor[] createExtractors()
    {
      return new Extractor[] { new TsExtractor() };
    }
  };
  private static final long HEVC_FORMAT_IDENTIFIER = Util.getIntegerCodeForString("HEVC");
  private static final int MAX_PID_PLUS_ONE = 8192;
  public static final int MODE_HLS = 2;
  public static final int MODE_MULTI_PMT = 0;
  public static final int MODE_SINGLE_PMT = 1;
  private static final int SNIFF_TS_PACKET_COUNT = 5;
  private static final int TS_PACKET_SIZE = 188;
  private static final int TS_PAT_PID = 0;
  public static final int TS_STREAM_TYPE_AAC_ADTS = 15;
  public static final int TS_STREAM_TYPE_AAC_LATM = 17;
  public static final int TS_STREAM_TYPE_AC3 = 129;
  public static final int TS_STREAM_TYPE_DTS = 138;
  public static final int TS_STREAM_TYPE_DVBSUBS = 89;
  public static final int TS_STREAM_TYPE_E_AC3 = 135;
  public static final int TS_STREAM_TYPE_H262 = 2;
  public static final int TS_STREAM_TYPE_H264 = 27;
  public static final int TS_STREAM_TYPE_H265 = 36;
  public static final int TS_STREAM_TYPE_HDMV_DTS = 130;
  public static final int TS_STREAM_TYPE_ID3 = 21;
  public static final int TS_STREAM_TYPE_MPA = 3;
  public static final int TS_STREAM_TYPE_MPA_LSF = 4;
  public static final int TS_STREAM_TYPE_SPLICE_INFO = 134;
  private static final int TS_SYNC_BYTE = 71;
  private int bytesSinceLastSync;
  private final SparseIntArray continuityCounters;
  private TsPayloadReader id3Reader;
  private final int mode;
  private ExtractorOutput output;
  private final TsPayloadReader.Factory payloadReaderFactory;
  private int remainingPmts;
  private final List<TimestampAdjuster> timestampAdjusters;
  private final SparseBooleanArray trackIds;
  private boolean tracksEnded;
  private final ParsableByteArray tsPacketBuffer;
  private final SparseArray<TsPayloadReader> tsPayloadReaders;
  
  public TsExtractor()
  {
    this(0);
  }
  
  public TsExtractor(int paramInt)
  {
    this(1, paramInt);
  }
  
  public TsExtractor(int paramInt1, int paramInt2)
  {
    this(paramInt1, new TimestampAdjuster(0L), new DefaultTsPayloadReaderFactory(paramInt2));
  }
  
  public TsExtractor(int paramInt, TimestampAdjuster paramTimestampAdjuster, TsPayloadReader.Factory paramFactory)
  {
    this.payloadReaderFactory = ((TsPayloadReader.Factory)Assertions.checkNotNull(paramFactory));
    this.mode = paramInt;
    if ((paramInt == 1) || (paramInt == 2)) {
      this.timestampAdjusters = Collections.singletonList(paramTimestampAdjuster);
    }
    for (;;)
    {
      this.tsPacketBuffer = new ParsableByteArray(new byte['Ⓒ'], 0);
      this.trackIds = new SparseBooleanArray();
      this.tsPayloadReaders = new SparseArray();
      this.continuityCounters = new SparseIntArray();
      resetPayloadReaders();
      return;
      this.timestampAdjusters = new ArrayList();
      this.timestampAdjusters.add(paramTimestampAdjuster);
    }
  }
  
  private void resetPayloadReaders()
  {
    this.trackIds.clear();
    this.tsPayloadReaders.clear();
    SparseArray localSparseArray = this.payloadReaderFactory.createInitialPayloadReaders();
    int i = localSparseArray.size();
    for (int j = 0; j < i; j++) {
      this.tsPayloadReaders.put(localSparseArray.keyAt(j), localSparseArray.valueAt(j));
    }
    this.tsPayloadReaders.put(0, new SectionReader(new PatReader()));
    this.id3Reader = null;
  }
  
  public void init(ExtractorOutput paramExtractorOutput)
  {
    this.output = paramExtractorOutput;
    paramExtractorOutput.seekMap(new SeekMap.Unseekable(-9223372036854775807L));
  }
  
  public int read(ExtractorInput paramExtractorInput, PositionHolder paramPositionHolder)
    throws IOException, InterruptedException
  {
    paramPositionHolder = this.tsPacketBuffer.data;
    int i;
    if (9400 - this.tsPacketBuffer.getPosition() < 188)
    {
      i = this.tsPacketBuffer.bytesLeft();
      if (i > 0) {
        System.arraycopy(paramPositionHolder, this.tsPacketBuffer.getPosition(), paramPositionHolder, 0, i);
      }
      this.tsPacketBuffer.reset(paramPositionHolder, i);
    }
    int j;
    if (this.tsPacketBuffer.bytesLeft() < 188)
    {
      j = this.tsPacketBuffer.limit();
      i = paramExtractorInput.read(paramPositionHolder, j, 9400 - j);
      if (i == -1) {
        i = -1;
      }
    }
    for (;;)
    {
      return i;
      this.tsPacketBuffer.setLimit(j + i);
      break;
      int k = this.tsPacketBuffer.limit();
      j = this.tsPacketBuffer.getPosition();
      for (i = j; (i < k) && (paramPositionHolder[i] != 71); i++) {}
      this.tsPacketBuffer.setPosition(i);
      int m = i + 188;
      if (m > k)
      {
        this.bytesSinceLastSync += i - j;
        if ((this.mode == 2) && (this.bytesSinceLastSync > 376)) {
          throw new ParserException("Cannot find sync byte. Most likely not a Transport Stream.");
        }
        i = 0;
      }
      else
      {
        this.bytesSinceLastSync = 0;
        int n = this.tsPacketBuffer.readInt();
        if ((0x800000 & n) != 0)
        {
          this.tsPacketBuffer.setPosition(m);
          i = 0;
        }
        else
        {
          boolean bool;
          label280:
          int i1;
          if ((0x400000 & n) != 0)
          {
            bool = true;
            i1 = (0x1FFF00 & n) >> 8;
            if ((n & 0x20) == 0) {
              break label354;
            }
            i = 1;
            label301:
            if ((n & 0x10) == 0) {
              break label359;
            }
            j = 1;
            label312:
            if (j == 0) {
              break label365;
            }
          }
          label354:
          label359:
          label365:
          for (paramExtractorInput = (TsPayloadReader)this.tsPayloadReaders.get(i1);; paramExtractorInput = null)
          {
            if (paramExtractorInput != null) {
              break label370;
            }
            this.tsPacketBuffer.setPosition(m);
            i = 0;
            break;
            bool = false;
            break label280;
            i = 0;
            break label301;
            j = 0;
            break label312;
          }
          label370:
          if (this.mode != 2)
          {
            n &= 0xF;
            j = this.continuityCounters.get(i1, n - 1);
            this.continuityCounters.put(i1, n);
            if (j == n)
            {
              this.tsPacketBuffer.setPosition(m);
              i = 0;
              continue;
            }
            if (n != (j + 1 & 0xF)) {
              paramExtractorInput.seek();
            }
          }
          if (i != 0)
          {
            i = this.tsPacketBuffer.readUnsignedByte();
            this.tsPacketBuffer.skipBytes(i);
          }
          this.tsPacketBuffer.setLimit(m);
          paramExtractorInput.consume(this.tsPacketBuffer, bool);
          this.tsPacketBuffer.setLimit(k);
          this.tsPacketBuffer.setPosition(m);
          i = 0;
        }
      }
    }
  }
  
  public void release() {}
  
  public void seek(long paramLong1, long paramLong2)
  {
    int i = this.timestampAdjusters.size();
    for (int j = 0; j < i; j++) {
      ((TimestampAdjuster)this.timestampAdjusters.get(j)).reset();
    }
    this.tsPacketBuffer.reset();
    this.continuityCounters.clear();
    resetPayloadReaders();
    this.bytesSinceLastSync = 0;
  }
  
  public boolean sniff(ExtractorInput paramExtractorInput)
    throws IOException, InterruptedException
  {
    boolean bool1 = false;
    byte[] arrayOfByte = this.tsPacketBuffer.data;
    paramExtractorInput.peekFully(arrayOfByte, 0, 940);
    int i = 0;
    boolean bool2 = bool1;
    if (i < 188) {}
    for (int j = 0;; j++)
    {
      if (j == 5)
      {
        paramExtractorInput.skipFully(i);
        bool2 = true;
        return bool2;
      }
      if (arrayOfByte[(j * 188 + i)] != 71)
      {
        i++;
        break;
      }
    }
  }
  
  @Retention(RetentionPolicy.SOURCE)
  public static @interface Mode {}
  
  private class PatReader
    implements SectionPayloadReader
  {
    private final ParsableBitArray patScratch = new ParsableBitArray(new byte[4]);
    
    public PatReader() {}
    
    public void consume(ParsableByteArray paramParsableByteArray)
    {
      if (paramParsableByteArray.readUnsignedByte() != 0) {}
      for (;;)
      {
        return;
        paramParsableByteArray.skipBytes(7);
        int i = paramParsableByteArray.bytesLeft() / 4;
        int j = 0;
        if (j < i)
        {
          paramParsableByteArray.readBytes(this.patScratch, 4);
          int k = this.patScratch.readBits(16);
          this.patScratch.skipBits(3);
          if (k == 0) {
            this.patScratch.skipBits(13);
          }
          for (;;)
          {
            j++;
            break;
            k = this.patScratch.readBits(13);
            TsExtractor.this.tsPayloadReaders.put(k, new SectionReader(new TsExtractor.PmtReader(TsExtractor.this, k)));
            TsExtractor.access$108(TsExtractor.this);
          }
        }
        if (TsExtractor.this.mode != 2) {
          TsExtractor.this.tsPayloadReaders.remove(0);
        }
      }
    }
    
    public void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator) {}
  }
  
  private class PmtReader
    implements SectionPayloadReader
  {
    private static final int TS_PMT_DESC_AC3 = 106;
    private static final int TS_PMT_DESC_DTS = 123;
    private static final int TS_PMT_DESC_DVBSUBS = 89;
    private static final int TS_PMT_DESC_EAC3 = 122;
    private static final int TS_PMT_DESC_ISO639_LANG = 10;
    private static final int TS_PMT_DESC_REGISTRATION = 5;
    private final int pid;
    private final ParsableBitArray pmtScratch = new ParsableBitArray(new byte[5]);
    private final SparseIntArray trackIdToPidScratch = new SparseIntArray();
    private final SparseArray<TsPayloadReader> trackIdToReaderScratch = new SparseArray();
    
    public PmtReader(int paramInt)
    {
      this.pid = paramInt;
    }
    
    private TsPayloadReader.EsInfo readEsInfo(ParsableByteArray paramParsableByteArray, int paramInt)
    {
      int i = paramParsableByteArray.getPosition();
      int j = i + paramInt;
      paramInt = -1;
      Object localObject1 = null;
      Object localObject2 = null;
      if (paramParsableByteArray.getPosition() < j)
      {
        int k = paramParsableByteArray.readUnsignedByte();
        int m = paramParsableByteArray.readUnsignedByte();
        m = paramParsableByteArray.getPosition() + m;
        long l;
        Object localObject3;
        Object localObject4;
        if (k == 5)
        {
          l = paramParsableByteArray.readUnsignedInt();
          if (l == TsExtractor.AC3_FORMAT_IDENTIFIER)
          {
            paramInt = 129;
            localObject3 = localObject1;
            localObject4 = localObject2;
          }
        }
        label248:
        do
        {
          for (;;)
          {
            paramParsableByteArray.skipBytes(m - paramParsableByteArray.getPosition());
            localObject2 = localObject4;
            localObject1 = localObject3;
            break;
            if (l == TsExtractor.E_AC3_FORMAT_IDENTIFIER)
            {
              paramInt = 135;
              localObject4 = localObject2;
              localObject3 = localObject1;
            }
            else
            {
              localObject4 = localObject2;
              localObject3 = localObject1;
              if (l == TsExtractor.HEVC_FORMAT_IDENTIFIER)
              {
                paramInt = 36;
                localObject4 = localObject2;
                localObject3 = localObject1;
                continue;
                if (k == 106)
                {
                  paramInt = 129;
                  localObject4 = localObject2;
                  localObject3 = localObject1;
                }
                else if (k == 122)
                {
                  paramInt = 135;
                  localObject4 = localObject2;
                  localObject3 = localObject1;
                }
                else if (k == 123)
                {
                  paramInt = 138;
                  localObject4 = localObject2;
                  localObject3 = localObject1;
                }
                else
                {
                  if (k != 10) {
                    break label248;
                  }
                  localObject3 = paramParsableByteArray.readString(3).trim();
                  localObject4 = localObject2;
                }
              }
            }
          }
          localObject4 = localObject2;
          localObject3 = localObject1;
        } while (k != 89);
        k = 89;
        localObject2 = new ArrayList();
        for (;;)
        {
          localObject4 = localObject2;
          localObject3 = localObject1;
          paramInt = k;
          if (paramParsableByteArray.getPosition() >= m) {
            break;
          }
          localObject3 = paramParsableByteArray.readString(3).trim();
          paramInt = paramParsableByteArray.readUnsignedByte();
          localObject4 = new byte[4];
          paramParsableByteArray.readBytes((byte[])localObject4, 0, 4);
          ((List)localObject2).add(new TsPayloadReader.DvbSubtitleInfo((String)localObject3, paramInt, (byte[])localObject4));
        }
      }
      paramParsableByteArray.setPosition(j);
      return new TsPayloadReader.EsInfo(paramInt, (String)localObject1, (List)localObject2, Arrays.copyOfRange(paramParsableByteArray.data, i, j));
    }
    
    public void consume(ParsableByteArray paramParsableByteArray)
    {
      if (paramParsableByteArray.readUnsignedByte() != 2) {}
      for (;;)
      {
        return;
        TimestampAdjuster localTimestampAdjuster;
        int i;
        Object localObject;
        if ((TsExtractor.this.mode == 1) || (TsExtractor.this.mode == 2) || (TsExtractor.this.remainingPmts == 1))
        {
          localTimestampAdjuster = (TimestampAdjuster)TsExtractor.this.timestampAdjusters.get(0);
          paramParsableByteArray.skipBytes(2);
          i = paramParsableByteArray.readUnsignedShort();
          paramParsableByteArray.skipBytes(5);
          paramParsableByteArray.readBytes(this.pmtScratch, 2);
          this.pmtScratch.skipBits(4);
          paramParsableByteArray.skipBytes(this.pmtScratch.readBits(12));
          if ((TsExtractor.this.mode == 2) && (TsExtractor.this.id3Reader == null))
          {
            localObject = new TsPayloadReader.EsInfo(21, null, null, new byte[0]);
            TsExtractor.access$402(TsExtractor.this, TsExtractor.this.payloadReaderFactory.createPayloadReader(21, (TsPayloadReader.EsInfo)localObject));
            TsExtractor.this.id3Reader.init(localTimestampAdjuster, TsExtractor.this.output, new TsPayloadReader.TrackIdGenerator(i, 21, 8192));
          }
          this.trackIdToReaderScratch.clear();
          this.trackIdToPidScratch.clear();
          j = paramParsableByteArray.bytesLeft();
        }
        label218:
        int k;
        while (j > 0)
        {
          paramParsableByteArray.readBytes(this.pmtScratch, 5);
          k = this.pmtScratch.readBits(8);
          this.pmtScratch.skipBits(3);
          int m = this.pmtScratch.readBits(13);
          this.pmtScratch.skipBits(4);
          int n = this.pmtScratch.readBits(12);
          localObject = readEsInfo(paramParsableByteArray, n);
          i1 = k;
          if (k == 6) {
            i1 = ((TsPayloadReader.EsInfo)localObject).streamType;
          }
          n = j - (n + 5);
          if (TsExtractor.this.mode == 2)
          {
            k = i1;
            label332:
            j = n;
            if (TsExtractor.this.trackIds.get(k)) {
              continue;
            }
            if ((TsExtractor.this.mode != 2) || (i1 != 21)) {
              break label490;
            }
          }
          label490:
          for (localObject = TsExtractor.this.id3Reader;; localObject = TsExtractor.this.payloadReaderFactory.createPayloadReader(i1, (TsPayloadReader.EsInfo)localObject))
          {
            if (TsExtractor.this.mode == 2)
            {
              j = n;
              if (m >= this.trackIdToPidScratch.get(k, 8192)) {
                break label218;
              }
            }
            this.trackIdToPidScratch.put(k, m);
            this.trackIdToReaderScratch.put(k, localObject);
            j = n;
            break label218;
            localTimestampAdjuster = new TimestampAdjuster(((TimestampAdjuster)TsExtractor.this.timestampAdjusters.get(0)).getFirstSampleTimestampUs());
            TsExtractor.this.timestampAdjusters.add(localTimestampAdjuster);
            break;
            k = m;
            break label332;
          }
        }
        int j = this.trackIdToPidScratch.size();
        for (i1 = 0; i1 < j; i1++)
        {
          k = this.trackIdToPidScratch.keyAt(i1);
          TsExtractor.this.trackIds.put(k, true);
          paramParsableByteArray = (TsPayloadReader)this.trackIdToReaderScratch.valueAt(i1);
          if (paramParsableByteArray != null)
          {
            if (paramParsableByteArray != TsExtractor.this.id3Reader) {
              paramParsableByteArray.init(localTimestampAdjuster, TsExtractor.this.output, new TsPayloadReader.TrackIdGenerator(i, k, 8192));
            }
            TsExtractor.this.tsPayloadReaders.put(this.trackIdToPidScratch.valueAt(i1), paramParsableByteArray);
          }
        }
        if (TsExtractor.this.mode != 2) {
          break;
        }
        if (!TsExtractor.this.tracksEnded)
        {
          TsExtractor.this.output.endTracks();
          TsExtractor.access$102(TsExtractor.this, 0);
          TsExtractor.access$802(TsExtractor.this, true);
        }
      }
      TsExtractor.this.tsPayloadReaders.remove(this.pid);
      paramParsableByteArray = TsExtractor.this;
      if (TsExtractor.this.mode == 1) {}
      for (int i1 = 0;; i1 = TsExtractor.this.remainingPmts - 1)
      {
        TsExtractor.access$102(paramParsableByteArray, i1);
        if (TsExtractor.this.remainingPmts != 0) {
          break;
        }
        TsExtractor.this.output.endTracks();
        TsExtractor.access$802(TsExtractor.this, true);
        break;
      }
    }
    
    public void init(TimestampAdjuster paramTimestampAdjuster, ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator) {}
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/TsExtractor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */