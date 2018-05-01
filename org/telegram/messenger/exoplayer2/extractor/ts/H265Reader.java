package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.Log;
import java.util.Collections;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.ParsableNalUnitBitArray;

public final class H265Reader
  implements ElementaryStreamReader
{
  private static final int BLA_W_LP = 16;
  private static final int CRA_NUT = 21;
  private static final int PPS_NUT = 34;
  private static final int PREFIX_SEI_NUT = 39;
  private static final int RASL_R = 9;
  private static final int SPS_NUT = 33;
  private static final int SUFFIX_SEI_NUT = 40;
  private static final String TAG = "H265Reader";
  private static final int VPS_NUT = 32;
  private String formatId;
  private boolean hasOutputFormat;
  private TrackOutput output;
  private long pesTimeUs;
  private final NalUnitTargetBuffer pps;
  private final boolean[] prefixFlags;
  private final NalUnitTargetBuffer prefixSei;
  private SampleReader sampleReader;
  private final SeiReader seiReader;
  private final ParsableByteArray seiWrapper;
  private final NalUnitTargetBuffer sps;
  private final NalUnitTargetBuffer suffixSei;
  private long totalBytesWritten;
  private final NalUnitTargetBuffer vps;
  
  public H265Reader(SeiReader paramSeiReader)
  {
    this.seiReader = paramSeiReader;
    this.prefixFlags = new boolean[3];
    this.vps = new NalUnitTargetBuffer(32, 128);
    this.sps = new NalUnitTargetBuffer(33, 128);
    this.pps = new NalUnitTargetBuffer(34, 128);
    this.prefixSei = new NalUnitTargetBuffer(39, 128);
    this.suffixSei = new NalUnitTargetBuffer(40, 128);
    this.seiWrapper = new ParsableByteArray();
  }
  
  private void endNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
  {
    if (this.hasOutputFormat) {
      this.sampleReader.endNalUnit(paramLong1, paramInt1);
    }
    for (;;)
    {
      if (this.prefixSei.endNalUnit(paramInt2))
      {
        paramInt1 = NalUnitUtil.unescapeStream(this.prefixSei.nalData, this.prefixSei.nalLength);
        this.seiWrapper.reset(this.prefixSei.nalData, paramInt1);
        this.seiWrapper.skipBytes(5);
        this.seiReader.consume(paramLong2, this.seiWrapper);
      }
      if (this.suffixSei.endNalUnit(paramInt2))
      {
        paramInt1 = NalUnitUtil.unescapeStream(this.suffixSei.nalData, this.suffixSei.nalLength);
        this.seiWrapper.reset(this.suffixSei.nalData, paramInt1);
        this.seiWrapper.skipBytes(5);
        this.seiReader.consume(paramLong2, this.seiWrapper);
      }
      return;
      this.vps.endNalUnit(paramInt2);
      this.sps.endNalUnit(paramInt2);
      this.pps.endNalUnit(paramInt2);
      if ((this.vps.isCompleted()) && (this.sps.isCompleted()) && (this.pps.isCompleted()))
      {
        this.output.format(parseMediaFormat(this.formatId, this.vps, this.sps, this.pps));
        this.hasOutputFormat = true;
      }
    }
  }
  
  private void nalUnitData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if (this.hasOutputFormat) {
      this.sampleReader.readNalUnitData(paramArrayOfByte, paramInt1, paramInt2);
    }
    for (;;)
    {
      this.prefixSei.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      this.suffixSei.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      return;
      this.vps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      this.sps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      this.pps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
    }
  }
  
  private static Format parseMediaFormat(String paramString, NalUnitTargetBuffer paramNalUnitTargetBuffer1, NalUnitTargetBuffer paramNalUnitTargetBuffer2, NalUnitTargetBuffer paramNalUnitTargetBuffer3)
  {
    byte[] arrayOfByte = new byte[paramNalUnitTargetBuffer1.nalLength + paramNalUnitTargetBuffer2.nalLength + paramNalUnitTargetBuffer3.nalLength];
    System.arraycopy(paramNalUnitTargetBuffer1.nalData, 0, arrayOfByte, 0, paramNalUnitTargetBuffer1.nalLength);
    System.arraycopy(paramNalUnitTargetBuffer2.nalData, 0, arrayOfByte, paramNalUnitTargetBuffer1.nalLength, paramNalUnitTargetBuffer2.nalLength);
    System.arraycopy(paramNalUnitTargetBuffer3.nalData, 0, arrayOfByte, paramNalUnitTargetBuffer1.nalLength + paramNalUnitTargetBuffer2.nalLength, paramNalUnitTargetBuffer3.nalLength);
    paramNalUnitTargetBuffer1 = new ParsableNalUnitBitArray(paramNalUnitTargetBuffer2.nalData, 0, paramNalUnitTargetBuffer2.nalLength);
    paramNalUnitTargetBuffer1.skipBits(44);
    int i = paramNalUnitTargetBuffer1.readBits(3);
    paramNalUnitTargetBuffer1.skipBit();
    paramNalUnitTargetBuffer1.skipBits(88);
    paramNalUnitTargetBuffer1.skipBits(8);
    int j = 0;
    for (int k = 0; k < i; k++)
    {
      m = j;
      if (paramNalUnitTargetBuffer1.readBit()) {
        m = j + 89;
      }
      j = m;
      if (paramNalUnitTargetBuffer1.readBit()) {
        j = m + 8;
      }
    }
    paramNalUnitTargetBuffer1.skipBits(j);
    if (i > 0) {
      paramNalUnitTargetBuffer1.skipBits((8 - i) * 2);
    }
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    int n = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    if (n == 3) {
      paramNalUnitTargetBuffer1.skipBit();
    }
    int i1 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    int i2 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    k = i1;
    int m = i2;
    if (paramNalUnitTargetBuffer1.readBit())
    {
      k = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      int i3 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      int i4 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      int i5 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      if ((n == 1) || (n == 2))
      {
        j = 2;
        if (n != 1) {
          break label379;
        }
        m = 2;
        label293:
        k = i1 - (k + i3) * j;
        m = i2 - (i4 + i5) * m;
      }
    }
    else
    {
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      i2 = paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      if (!paramNalUnitTargetBuffer1.readBit()) {
        break label385;
      }
      j = 0;
    }
    for (;;)
    {
      if (j > i) {
        break label392;
      }
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      j++;
      continue;
      j = 1;
      break;
      label379:
      m = 1;
      break label293;
      label385:
      j = i;
    }
    label392:
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
    if ((paramNalUnitTargetBuffer1.readBit()) && (paramNalUnitTargetBuffer1.readBit())) {
      skipScalingList(paramNalUnitTargetBuffer1);
    }
    paramNalUnitTargetBuffer1.skipBits(2);
    if (paramNalUnitTargetBuffer1.readBit())
    {
      paramNalUnitTargetBuffer1.skipBits(8);
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt();
      paramNalUnitTargetBuffer1.skipBit();
    }
    skipShortTermRefPicSets(paramNalUnitTargetBuffer1);
    if (paramNalUnitTargetBuffer1.readBit()) {
      for (j = 0; j < paramNalUnitTargetBuffer1.readUnsignedExpGolombCodedInt(); j++) {
        paramNalUnitTargetBuffer1.skipBits(i2 + 4 + 1);
      }
    }
    paramNalUnitTargetBuffer1.skipBits(2);
    float f1 = 1.0F;
    float f2 = f1;
    if (paramNalUnitTargetBuffer1.readBit())
    {
      f2 = f1;
      if (paramNalUnitTargetBuffer1.readBit())
      {
        j = paramNalUnitTargetBuffer1.readBits(8);
        if (j != 255) {
          break label625;
        }
        i = paramNalUnitTargetBuffer1.readBits(16);
        j = paramNalUnitTargetBuffer1.readBits(16);
        f2 = f1;
        if (i != 0)
        {
          f2 = f1;
          if (j != 0) {
            f2 = i / j;
          }
        }
      }
    }
    for (;;)
    {
      return Format.createVideoSampleFormat(paramString, "video/hevc", null, -1, -1, k, m, -1.0F, Collections.singletonList(arrayOfByte), -1, f2, null);
      label625:
      if (j < NalUnitUtil.ASPECT_RATIO_IDC_VALUES.length)
      {
        f2 = NalUnitUtil.ASPECT_RATIO_IDC_VALUES[j];
      }
      else
      {
        Log.w("H265Reader", "Unexpected aspect_ratio_idc value: " + j);
        f2 = f1;
      }
    }
  }
  
  private static void skipScalingList(ParsableNalUnitBitArray paramParsableNalUnitBitArray)
  {
    for (int i = 0; i < 4; i++)
    {
      int j = 0;
      if (j < 6)
      {
        if (!paramParsableNalUnitBitArray.readBit())
        {
          paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
          label27:
          if (i != 3) {
            break label84;
          }
        }
        label84:
        for (int k = 3;; k = 1)
        {
          j += k;
          break;
          int m = Math.min(64, 1 << (i << 1) + 4);
          if (i > 1) {
            paramParsableNalUnitBitArray.readSignedExpGolombCodedInt();
          }
          for (k = 0; k < m; k++) {
            paramParsableNalUnitBitArray.readSignedExpGolombCodedInt();
          }
          break label27;
        }
      }
    }
  }
  
  private static void skipShortTermRefPicSets(ParsableNalUnitBitArray paramParsableNalUnitBitArray)
  {
    int i = paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
    boolean bool = false;
    int j = 0;
    int k = 0;
    while (k < i)
    {
      if (k != 0) {
        bool = paramParsableNalUnitBitArray.readBit();
      }
      if (bool)
      {
        paramParsableNalUnitBitArray.skipBit();
        paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        for (m = 0;; m++)
        {
          n = j;
          if (m > j) {
            break;
          }
          if (paramParsableNalUnitBitArray.readBit()) {
            paramParsableNalUnitBitArray.skipBit();
          }
        }
      }
      j = paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
      int i1 = paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
      int m = j + i1;
      for (int n = 0; n < j; n++)
      {
        paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        paramParsableNalUnitBitArray.skipBit();
      }
      for (j = 0;; j++)
      {
        n = m;
        if (j >= i1) {
          break;
        }
        paramParsableNalUnitBitArray.readUnsignedExpGolombCodedInt();
        paramParsableNalUnitBitArray.skipBit();
      }
      k++;
      j = n;
    }
  }
  
  private void startNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
  {
    if (this.hasOutputFormat) {
      this.sampleReader.startNalUnit(paramLong1, paramInt1, paramInt2, paramLong2);
    }
    for (;;)
    {
      this.prefixSei.startNalUnit(paramInt2);
      this.suffixSei.startNalUnit(paramInt2);
      return;
      this.vps.startNalUnit(paramInt2);
      this.sps.startNalUnit(paramInt2);
      this.pps.startNalUnit(paramInt2);
    }
  }
  
  public void consume(ParsableByteArray paramParsableByteArray)
  {
    int j;
    byte[] arrayOfByte;
    int k;
    while (paramParsableByteArray.bytesLeft() > 0)
    {
      i = paramParsableByteArray.getPosition();
      j = paramParsableByteArray.limit();
      arrayOfByte = paramParsableByteArray.data;
      this.totalBytesWritten += paramParsableByteArray.bytesLeft();
      this.output.sampleData(paramParsableByteArray, paramParsableByteArray.bytesLeft());
      if (i < j)
      {
        k = NalUnitUtil.findNalUnit(arrayOfByte, i, j, this.prefixFlags);
        if (k != j) {
          break label84;
        }
        nalUnitData(arrayOfByte, i, j);
      }
    }
    return;
    label84:
    int m = NalUnitUtil.getH265NalUnitType(arrayOfByte, k);
    int n = k - i;
    if (n > 0) {
      nalUnitData(arrayOfByte, i, k);
    }
    int i1 = j - k;
    long l = this.totalBytesWritten - i1;
    if (n < 0) {}
    for (int i = -n;; i = 0)
    {
      endNalUnit(l, i1, i, this.pesTimeUs);
      startNalUnit(l, i1, m, this.pesTimeUs);
      i = k + 3;
      break;
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    paramTrackIdGenerator.generateNewId();
    this.formatId = paramTrackIdGenerator.getFormatId();
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 2);
    this.sampleReader = new SampleReader(this.output);
    this.seiReader.createTracks(paramExtractorOutput, paramTrackIdGenerator);
  }
  
  public void packetFinished() {}
  
  public void packetStarted(long paramLong, boolean paramBoolean)
  {
    this.pesTimeUs = paramLong;
  }
  
  public void seek()
  {
    NalUnitUtil.clearPrefixFlags(this.prefixFlags);
    this.vps.reset();
    this.sps.reset();
    this.pps.reset();
    this.prefixSei.reset();
    this.suffixSei.reset();
    this.sampleReader.reset();
    this.totalBytesWritten = 0L;
  }
  
  private static final class SampleReader
  {
    private static final int FIRST_SLICE_FLAG_OFFSET = 2;
    private boolean isFirstParameterSet;
    private boolean isFirstSlice;
    private boolean lookingForFirstSliceFlag;
    private int nalUnitBytesRead;
    private boolean nalUnitHasKeyframeData;
    private long nalUnitStartPosition;
    private long nalUnitTimeUs;
    private final TrackOutput output;
    private boolean readingSample;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private boolean writingParameterSets;
    
    public SampleReader(TrackOutput paramTrackOutput)
    {
      this.output = paramTrackOutput;
    }
    
    private void outputSample(int paramInt)
    {
      if (this.sampleIsKeyframe) {}
      for (int i = 1;; i = 0)
      {
        int j = (int)(this.nalUnitStartPosition - this.samplePosition);
        this.output.sampleMetadata(this.sampleTimeUs, i, j, paramInt, null);
        return;
      }
    }
    
    public void endNalUnit(long paramLong, int paramInt)
    {
      if ((this.writingParameterSets) && (this.isFirstSlice))
      {
        this.sampleIsKeyframe = this.nalUnitHasKeyframeData;
        this.writingParameterSets = false;
      }
      for (;;)
      {
        return;
        if ((this.isFirstParameterSet) || (this.isFirstSlice))
        {
          if (this.readingSample) {
            outputSample(paramInt + (int)(paramLong - this.nalUnitStartPosition));
          }
          this.samplePosition = this.nalUnitStartPosition;
          this.sampleTimeUs = this.nalUnitTimeUs;
          this.readingSample = true;
          this.sampleIsKeyframe = this.nalUnitHasKeyframeData;
        }
      }
    }
    
    public void readNalUnitData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      boolean bool;
      if (this.lookingForFirstSliceFlag)
      {
        int i = paramInt1 + 2 - this.nalUnitBytesRead;
        if (i >= paramInt2) {
          break label55;
        }
        if ((paramArrayOfByte[i] & 0x80) == 0) {
          break label49;
        }
        bool = true;
        this.isFirstSlice = bool;
        this.lookingForFirstSliceFlag = false;
      }
      for (;;)
      {
        return;
        label49:
        bool = false;
        break;
        label55:
        this.nalUnitBytesRead += paramInt2 - paramInt1;
      }
    }
    
    public void reset()
    {
      this.lookingForFirstSliceFlag = false;
      this.isFirstSlice = false;
      this.isFirstParameterSet = false;
      this.readingSample = false;
      this.writingParameterSets = false;
    }
    
    public void startNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
    {
      boolean bool1 = false;
      this.isFirstSlice = false;
      this.isFirstParameterSet = false;
      this.nalUnitTimeUs = paramLong2;
      this.nalUnitBytesRead = 0;
      this.nalUnitStartPosition = paramLong1;
      if (paramInt2 >= 32)
      {
        if ((!this.writingParameterSets) && (this.readingSample))
        {
          outputSample(paramInt1);
          this.readingSample = false;
        }
        if (paramInt2 <= 34)
        {
          if (this.writingParameterSets) {
            break label139;
          }
          bool2 = true;
          this.isFirstParameterSet = bool2;
          this.writingParameterSets = true;
        }
      }
      if ((paramInt2 >= 16) && (paramInt2 <= 21)) {}
      for (boolean bool2 = true;; bool2 = false)
      {
        this.nalUnitHasKeyframeData = bool2;
        if (!this.nalUnitHasKeyframeData)
        {
          bool2 = bool1;
          if (paramInt2 > 9) {}
        }
        else
        {
          bool2 = true;
        }
        this.lookingForFirstSliceFlag = bool2;
        return;
        label139:
        bool2 = false;
        break;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/H265Reader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */