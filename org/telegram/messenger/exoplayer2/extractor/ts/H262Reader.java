package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.Pair;
import java.util.Arrays;
import java.util.Collections;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class H262Reader
  implements ElementaryStreamReader
{
  private static final double[] FRAME_RATE_VALUES = { 23.976023976023978D, 24.0D, 25.0D, 29.97002997002997D, 30.0D, 50.0D, 59.94005994005994D, 60.0D };
  private static final int START_EXTENSION = 181;
  private static final int START_GROUP = 184;
  private static final int START_PICTURE = 0;
  private static final int START_SEQUENCE_HEADER = 179;
  private final CsdBuffer csdBuffer = new CsdBuffer(128);
  private String formatId;
  private long frameDurationUs;
  private boolean hasOutputFormat;
  private TrackOutput output;
  private long pesTimeUs;
  private final boolean[] prefixFlags = new boolean[4];
  private boolean sampleHasPicture;
  private boolean sampleIsKeyframe;
  private long samplePosition;
  private long sampleTimeUs;
  private boolean startedFirstSample;
  private long totalBytesWritten;
  
  private static Pair<Format, Long> parseCsdBuffer(CsdBuffer paramCsdBuffer, String paramString)
  {
    byte[] arrayOfByte = Arrays.copyOf(paramCsdBuffer.data, paramCsdBuffer.length);
    int i = arrayOfByte[4];
    int j = arrayOfByte[5] & 0xFF;
    int k = arrayOfByte[6];
    i = (i & 0xFF) << 4 | j >> 4;
    k = (j & 0xF) << 8 | k & 0xFF;
    float f = 1.0F;
    switch ((arrayOfByte[7] & 0xF0) >> 4)
    {
    }
    for (;;)
    {
      paramString = Format.createVideoSampleFormat(paramString, "video/mpeg2", null, -1, -1, i, k, -1.0F, Collections.singletonList(arrayOfByte), -1, f, null);
      long l1 = 0L;
      k = (arrayOfByte[7] & 0xF) - 1;
      long l2 = l1;
      if (k >= 0)
      {
        l2 = l1;
        if (k < FRAME_RATE_VALUES.length)
        {
          double d1 = FRAME_RATE_VALUES[k];
          j = paramCsdBuffer.sequenceExtensionPosition;
          k = (arrayOfByte[(j + 9)] & 0x60) >> 5;
          j = arrayOfByte[(j + 9)] & 0x1F;
          double d2 = d1;
          if (k != j) {
            d2 = d1 * ((k + 1.0D) / (j + 1));
          }
          l2 = (1000000.0D / d2);
        }
      }
      return Pair.create(paramString, Long.valueOf(l2));
      f = k * 4 / (i * 3);
      continue;
      f = k * 16 / (i * 9);
      continue;
      f = k * 121 / (i * 100);
    }
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    int i = paramParsableByteArray.getPosition();
    int j = paramParsableByteArray.limit();
    byte[] arrayOfByte = paramParsableByteArray.data;
    this.totalBytesWritten += paramParsableByteArray.bytesLeft();
    this.output.sampleData(paramParsableByteArray, paramParsableByteArray.bytesLeft());
    int k = NalUnitUtil.findNalUnit(arrayOfByte, i, j, this.prefixFlags);
    if (k == j)
    {
      if (!this.hasOutputFormat) {
        this.csdBuffer.onData(arrayOfByte, i, j);
      }
      return;
    }
    int m = paramParsableByteArray.data[(k + 3)] & 0xFF;
    int n;
    if (!this.hasOutputFormat)
    {
      n = k - i;
      if (n > 0) {
        this.csdBuffer.onData(arrayOfByte, i, k);
      }
      if (n >= 0) {
        break label370;
      }
      i = -n;
      label136:
      if (this.csdBuffer.onStartCode(m, i))
      {
        Pair localPair = parseCsdBuffer(this.csdBuffer, this.formatId);
        this.output.format((Format)localPair.first);
        this.frameDurationUs = ((Long)localPair.second).longValue();
        this.hasOutputFormat = true;
      }
    }
    label248:
    long l;
    label325:
    boolean bool;
    if ((m == 0) || (m == 179))
    {
      n = j - k;
      if ((this.startedFirstSample) && (this.sampleHasPicture) && (this.hasOutputFormat))
      {
        if (this.sampleIsKeyframe)
        {
          i = 1;
          int i1 = (int)(this.totalBytesWritten - this.samplePosition);
          this.output.sampleMetadata(this.sampleTimeUs, i, i1 - n, n, null);
        }
      }
      else
      {
        if ((!this.startedFirstSample) || (this.sampleHasPicture))
        {
          this.samplePosition = (this.totalBytesWritten - n);
          if (this.pesTimeUs == -9223372036854775807L) {
            break label380;
          }
          l = this.pesTimeUs;
          this.sampleTimeUs = l;
          this.sampleIsKeyframe = false;
          this.pesTimeUs = -9223372036854775807L;
          this.startedFirstSample = true;
        }
        if (m != 0) {
          break label407;
        }
        bool = true;
        label356:
        this.sampleHasPicture = bool;
      }
    }
    for (;;)
    {
      i = k + 3;
      break;
      label370:
      i = 0;
      break label136;
      i = 0;
      break label248;
      label380:
      if (this.startedFirstSample)
      {
        l = this.sampleTimeUs + this.frameDurationUs;
        break label325;
      }
      l = 0L;
      break label325;
      label407:
      bool = false;
      break label356;
      if (m == 184) {
        this.sampleIsKeyframe = true;
      }
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    paramTrackIdGenerator.generateNewId();
    this.formatId = paramTrackIdGenerator.getFormatId();
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 2);
  }
  
  public void packetFinished() {}
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    this.pesTimeUs = paramLong;
  }
  
  public void seek()
  {
    NalUnitUtil.clearPrefixFlags(this.prefixFlags);
    this.csdBuffer.reset();
    this.totalBytesWritten = 0L;
    this.startedFirstSample = false;
  }
  
  private static final class CsdBuffer
  {
    private static final byte[] START_CODE = { 0, 0, 1 };
    public byte[] data;
    private boolean isFilling;
    public int length;
    public int sequenceExtensionPosition;
    
    public CsdBuffer(int paramInt)
    {
      this.data = new byte[paramInt];
    }
    
    public void onData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (!this.isFilling) {}
      for (;;)
      {
        return;
        paramInt2 -= paramInt1;
        if (this.data.length < this.length + paramInt2) {
          this.data = Arrays.copyOf(this.data, (this.length + paramInt2) * 2);
        }
        System.arraycopy(paramArrayOfByte, paramInt1, this.data, this.length, paramInt2);
        this.length += paramInt2;
      }
    }
    
    public boolean onStartCode(int paramInt1, int paramInt2)
    {
      boolean bool = true;
      if (this.isFilling)
      {
        this.length -= paramInt2;
        if ((this.sequenceExtensionPosition == 0) && (paramInt1 == 181)) {
          this.sequenceExtensionPosition = this.length;
        }
      }
      for (;;)
      {
        onData(START_CODE, 0, START_CODE.length);
        bool = false;
        for (;;)
        {
          return bool;
          this.isFilling = false;
        }
        if (paramInt1 == 179) {
          this.isFilling = true;
        }
      }
    }
    
    public void reset()
    {
      this.isFilling = false;
      this.length = 0;
      this.sequenceExtensionPosition = 0;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/H262Reader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */