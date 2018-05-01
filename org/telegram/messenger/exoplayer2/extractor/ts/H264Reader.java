package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ExtractorOutput;
import org.telegram.messenger.exoplayer2.extractor.TrackOutput;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil.PpsData;
import org.telegram.messenger.exoplayer2.util.NalUnitUtil.SpsData;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.ParsableNalUnitBitArray;

public final class H264Reader
  implements ElementaryStreamReader
{
  private static final int NAL_UNIT_TYPE_PPS = 8;
  private static final int NAL_UNIT_TYPE_SEI = 6;
  private static final int NAL_UNIT_TYPE_SPS = 7;
  private final boolean allowNonIdrKeyframes;
  private final boolean detectAccessUnits;
  private String formatId;
  private boolean hasOutputFormat;
  private TrackOutput output;
  private long pesTimeUs;
  private final NalUnitTargetBuffer pps;
  private final boolean[] prefixFlags;
  private SampleReader sampleReader;
  private final NalUnitTargetBuffer sei;
  private final SeiReader seiReader;
  private final ParsableByteArray seiWrapper;
  private final NalUnitTargetBuffer sps;
  private long totalBytesWritten;
  
  public H264Reader(SeiReader paramSeiReader, boolean paramBoolean1, boolean paramBoolean2)
  {
    this.seiReader = paramSeiReader;
    this.allowNonIdrKeyframes = paramBoolean1;
    this.detectAccessUnits = paramBoolean2;
    this.prefixFlags = new boolean[3];
    this.sps = new NalUnitTargetBuffer(7, 128);
    this.pps = new NalUnitTargetBuffer(8, 128);
    this.sei = new NalUnitTargetBuffer(6, 128);
    this.seiWrapper = new ParsableByteArray();
  }
  
  private void endNalUnit(long paramLong1, int paramInt1, int paramInt2, long paramLong2)
  {
    Object localObject;
    if ((!this.hasOutputFormat) || (this.sampleReader.needsSpsPps()))
    {
      this.sps.endNalUnit(paramInt2);
      this.pps.endNalUnit(paramInt2);
      if (this.hasOutputFormat) {
        break label320;
      }
      if ((this.sps.isCompleted()) && (this.pps.isCompleted()))
      {
        localObject = new ArrayList();
        ((List)localObject).add(Arrays.copyOf(this.sps.nalData, this.sps.nalLength));
        ((List)localObject).add(Arrays.copyOf(this.pps.nalData, this.pps.nalLength));
        NalUnitUtil.SpsData localSpsData = NalUnitUtil.parseSpsNalUnit(this.sps.nalData, 3, this.sps.nalLength);
        NalUnitUtil.PpsData localPpsData = NalUnitUtil.parsePpsNalUnit(this.pps.nalData, 3, this.pps.nalLength);
        this.output.format(Format.createVideoSampleFormat(this.formatId, "video/avc", null, -1, -1, localSpsData.width, localSpsData.height, -1.0F, (List)localObject, -1, localSpsData.pixelWidthAspectRatio, null));
        this.hasOutputFormat = true;
        this.sampleReader.putSps(localSpsData);
        this.sampleReader.putPps(localPpsData);
        this.sps.reset();
        this.pps.reset();
      }
    }
    for (;;)
    {
      if (this.sei.endNalUnit(paramInt2))
      {
        paramInt2 = NalUnitUtil.unescapeStream(this.sei.nalData, this.sei.nalLength);
        this.seiWrapper.reset(this.sei.nalData, paramInt2);
        this.seiWrapper.setPosition(4);
        this.seiReader.consume(paramLong2, this.seiWrapper);
      }
      this.sampleReader.endNalUnit(paramLong1, paramInt1);
      return;
      label320:
      if (this.sps.isCompleted())
      {
        localObject = NalUnitUtil.parseSpsNalUnit(this.sps.nalData, 3, this.sps.nalLength);
        this.sampleReader.putSps((NalUnitUtil.SpsData)localObject);
        this.sps.reset();
      }
      else if (this.pps.isCompleted())
      {
        localObject = NalUnitUtil.parsePpsNalUnit(this.pps.nalData, 3, this.pps.nalLength);
        this.sampleReader.putPps((NalUnitUtil.PpsData)localObject);
        this.pps.reset();
      }
    }
  }
  
  private void nalUnitData(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    if ((!this.hasOutputFormat) || (this.sampleReader.needsSpsPps()))
    {
      this.sps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
      this.pps.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
    }
    this.sei.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
    this.sampleReader.appendToNalUnit(paramArrayOfByte, paramInt1, paramInt2);
  }
  
  private void startNalUnit(long paramLong1, int paramInt, long paramLong2)
  {
    if ((!this.hasOutputFormat) || (this.sampleReader.needsSpsPps()))
    {
      this.sps.startNalUnit(paramInt);
      this.pps.startNalUnit(paramInt);
    }
    this.sei.startNalUnit(paramInt);
    this.sampleReader.startNalUnit(paramLong1, paramInt, paramLong2);
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
      nalUnitData(arrayOfByte, i, j);
      return;
    }
    int m = NalUnitUtil.getNalUnitType(arrayOfByte, k);
    int n = k - i;
    if (n > 0) {
      nalUnitData(arrayOfByte, i, k);
    }
    int i1 = j - k;
    long l = this.totalBytesWritten - i1;
    if (n < 0) {}
    for (i = -n;; i = 0)
    {
      endNalUnit(l, i1, i, this.pesTimeUs);
      startNalUnit(l, m, this.pesTimeUs);
      i = k + 3;
      break;
    }
  }
  
  public void createTracks(ExtractorOutput paramExtractorOutput, TsPayloadReader.TrackIdGenerator paramTrackIdGenerator)
  {
    paramTrackIdGenerator.generateNewId();
    this.formatId = paramTrackIdGenerator.getFormatId();
    this.output = paramExtractorOutput.track(paramTrackIdGenerator.getTrackId(), 2);
    this.sampleReader = new SampleReader(this.output, this.allowNonIdrKeyframes, this.detectAccessUnits);
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
    this.sps.reset();
    this.pps.reset();
    this.sei.reset();
    this.sampleReader.reset();
    this.totalBytesWritten = 0L;
  }
  
  private static final class SampleReader
  {
    private static final int DEFAULT_BUFFER_SIZE = 128;
    private static final int NAL_UNIT_TYPE_AUD = 9;
    private static final int NAL_UNIT_TYPE_IDR = 5;
    private static final int NAL_UNIT_TYPE_NON_IDR = 1;
    private static final int NAL_UNIT_TYPE_PARTITION_A = 2;
    private final boolean allowNonIdrKeyframes;
    private final ParsableNalUnitBitArray bitArray;
    private byte[] buffer;
    private int bufferLength;
    private final boolean detectAccessUnits;
    private boolean isFilling;
    private long nalUnitStartPosition;
    private long nalUnitTimeUs;
    private int nalUnitType;
    private final TrackOutput output;
    private final SparseArray<NalUnitUtil.PpsData> pps;
    private SliceHeaderData previousSliceHeader;
    private boolean readingSample;
    private boolean sampleIsKeyframe;
    private long samplePosition;
    private long sampleTimeUs;
    private SliceHeaderData sliceHeader;
    private final SparseArray<NalUnitUtil.SpsData> sps;
    
    public SampleReader(TrackOutput paramTrackOutput, boolean paramBoolean1, boolean paramBoolean2)
    {
      this.output = paramTrackOutput;
      this.allowNonIdrKeyframes = paramBoolean1;
      this.detectAccessUnits = paramBoolean2;
      this.sps = new SparseArray();
      this.pps = new SparseArray();
      this.previousSliceHeader = new SliceHeaderData(null);
      this.sliceHeader = new SliceHeaderData(null);
      this.buffer = new byte[''];
      this.bitArray = new ParsableNalUnitBitArray(this.buffer, 0, 0);
      reset();
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
    
    public void appendToNalUnit(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
      if (!this.isFilling) {}
      int i;
      int j;
      int k;
      NalUnitUtil.PpsData localPpsData;
      boolean bool1;
      int m;
      boolean bool4;
      boolean bool5;
      boolean bool6;
      label404:
      int n;
      int i1;
      int i2;
      int i3;
      do
      {
        do
        {
          do
          {
            do
            {
              boolean bool2;
              boolean bool3;
              do
              {
                do
                {
                  do
                  {
                    for (;;)
                    {
                      return;
                      paramInt2 -= paramInt1;
                      if (this.buffer.length < this.bufferLength + paramInt2) {
                        this.buffer = Arrays.copyOf(this.buffer, (this.bufferLength + paramInt2) * 2);
                      }
                      System.arraycopy(paramArrayOfByte, paramInt1, this.buffer, this.bufferLength, paramInt2);
                      this.bufferLength += paramInt2;
                      this.bitArray.reset(this.buffer, 0, this.bufferLength);
                      if (this.bitArray.canReadBits(8))
                      {
                        this.bitArray.skipBit();
                        i = this.bitArray.readBits(2);
                        this.bitArray.skipBits(5);
                        if (this.bitArray.canReadExpGolombCodedNum())
                        {
                          this.bitArray.readUnsignedExpGolombCodedInt();
                          if (this.bitArray.canReadExpGolombCodedNum())
                          {
                            j = this.bitArray.readUnsignedExpGolombCodedInt();
                            if (!this.detectAccessUnits)
                            {
                              this.isFilling = false;
                              this.sliceHeader.setSliceType(j);
                            }
                            else if (this.bitArray.canReadExpGolombCodedNum())
                            {
                              k = this.bitArray.readUnsignedExpGolombCodedInt();
                              if (this.pps.indexOfKey(k) >= 0) {
                                break;
                              }
                              this.isFilling = false;
                            }
                          }
                        }
                      }
                    }
                    localPpsData = (NalUnitUtil.PpsData)this.pps.get(k);
                    paramArrayOfByte = (NalUnitUtil.SpsData)this.sps.get(localPpsData.seqParameterSetId);
                    if (!paramArrayOfByte.separateColorPlaneFlag) {
                      break;
                    }
                  } while (!this.bitArray.canReadBits(2));
                  this.bitArray.skipBits(2);
                } while (!this.bitArray.canReadBits(paramArrayOfByte.frameNumLength));
                bool1 = false;
                bool2 = false;
                bool3 = false;
                m = this.bitArray.readBits(paramArrayOfByte.frameNumLength);
                bool4 = bool2;
                bool5 = bool3;
                if (paramArrayOfByte.frameMbsOnlyFlag) {
                  break;
                }
              } while (!this.bitArray.canReadBits(1));
              bool6 = this.bitArray.readBit();
              bool1 = bool6;
              bool4 = bool2;
              bool5 = bool3;
              if (!bool6) {
                break;
              }
            } while (!this.bitArray.canReadBits(1));
            bool5 = this.bitArray.readBit();
            bool4 = true;
            bool1 = bool6;
            if (this.nalUnitType != 5) {
              break label588;
            }
            bool6 = true;
            paramInt2 = 0;
            if (!bool6) {
              break;
            }
          } while (!this.bitArray.canReadExpGolombCodedNum());
          paramInt2 = this.bitArray.readUnsignedExpGolombCodedInt();
          n = 0;
          i1 = 0;
          i2 = 0;
          i3 = 0;
          if (paramArrayOfByte.picOrderCountType != 0) {
            break label594;
          }
        } while (!this.bitArray.canReadBits(paramArrayOfByte.picOrderCntLsbLength));
        n = this.bitArray.readBits(paramArrayOfByte.picOrderCntLsbLength);
        i4 = n;
        i5 = i1;
        paramInt1 = i2;
        i6 = i3;
        if (!localPpsData.bottomFieldPicOrderInFramePresentFlag) {
          break;
        }
        i4 = n;
        i5 = i1;
        paramInt1 = i2;
        i6 = i3;
        if (bool1) {
          break;
        }
      } while (!this.bitArray.canReadExpGolombCodedNum());
      int i5 = this.bitArray.readSignedExpGolombCodedInt();
      int i6 = i3;
      paramInt1 = i2;
      int i4 = n;
      for (;;)
      {
        this.sliceHeader.setAll(paramArrayOfByte, i, j, m, k, bool1, bool4, bool5, bool6, paramInt2, i4, i5, paramInt1, i6);
        this.isFilling = false;
        break;
        label588:
        bool6 = false;
        break label404;
        label594:
        i4 = n;
        i5 = i1;
        paramInt1 = i2;
        i6 = i3;
        if (paramArrayOfByte.picOrderCountType == 1)
        {
          i4 = n;
          i5 = i1;
          paramInt1 = i2;
          i6 = i3;
          if (!paramArrayOfByte.deltaPicOrderAlwaysZeroFlag)
          {
            if (!this.bitArray.canReadExpGolombCodedNum()) {
              break;
            }
            i2 = this.bitArray.readSignedExpGolombCodedInt();
            i4 = n;
            i5 = i1;
            paramInt1 = i2;
            i6 = i3;
            if (localPpsData.bottomFieldPicOrderInFramePresentFlag)
            {
              i4 = n;
              i5 = i1;
              paramInt1 = i2;
              i6 = i3;
              if (!bool1)
              {
                if (!this.bitArray.canReadExpGolombCodedNum()) {
                  break;
                }
                i6 = this.bitArray.readSignedExpGolombCodedInt();
                i4 = n;
                i5 = i1;
                paramInt1 = i2;
              }
            }
          }
        }
      }
    }
    
    public void endNalUnit(long paramLong, int paramInt)
    {
      int i = 0;
      if ((this.nalUnitType == 9) || ((this.detectAccessUnits) && (this.sliceHeader.isFirstVclNalUnitOfPicture(this.previousSliceHeader))))
      {
        if (this.readingSample) {
          outputSample(paramInt + (int)(paramLong - this.nalUnitStartPosition));
        }
        this.samplePosition = this.nalUnitStartPosition;
        this.sampleTimeUs = this.nalUnitTimeUs;
        this.sampleIsKeyframe = false;
        this.readingSample = true;
      }
      int j = this.sampleIsKeyframe;
      if (this.nalUnitType != 5)
      {
        paramInt = i;
        if (this.allowNonIdrKeyframes)
        {
          paramInt = i;
          if (this.nalUnitType == 1)
          {
            paramInt = i;
            if (!this.sliceHeader.isISlice()) {}
          }
        }
      }
      else
      {
        paramInt = 1;
      }
      this.sampleIsKeyframe = (paramInt | j);
    }
    
    public boolean needsSpsPps()
    {
      return this.detectAccessUnits;
    }
    
    public void putPps(NalUnitUtil.PpsData paramPpsData)
    {
      this.pps.append(paramPpsData.picParameterSetId, paramPpsData);
    }
    
    public void putSps(NalUnitUtil.SpsData paramSpsData)
    {
      this.sps.append(paramSpsData.seqParameterSetId, paramSpsData);
    }
    
    public void reset()
    {
      this.isFilling = false;
      this.readingSample = false;
      this.sliceHeader.clear();
    }
    
    public void startNalUnit(long paramLong1, int paramInt, long paramLong2)
    {
      this.nalUnitType = paramInt;
      this.nalUnitTimeUs = paramLong2;
      this.nalUnitStartPosition = paramLong1;
      if (((this.allowNonIdrKeyframes) && (this.nalUnitType == 1)) || ((this.detectAccessUnits) && ((this.nalUnitType == 5) || (this.nalUnitType == 1) || (this.nalUnitType == 2))))
      {
        SliceHeaderData localSliceHeaderData = this.previousSliceHeader;
        this.previousSliceHeader = this.sliceHeader;
        this.sliceHeader = localSliceHeaderData;
        this.sliceHeader.clear();
        this.bufferLength = 0;
        this.isFilling = true;
      }
    }
    
    private static final class SliceHeaderData
    {
      private static final int SLICE_TYPE_ALL_I = 7;
      private static final int SLICE_TYPE_I = 2;
      private boolean bottomFieldFlag;
      private boolean bottomFieldFlagPresent;
      private int deltaPicOrderCnt0;
      private int deltaPicOrderCnt1;
      private int deltaPicOrderCntBottom;
      private boolean fieldPicFlag;
      private int frameNum;
      private boolean hasSliceType;
      private boolean idrPicFlag;
      private int idrPicId;
      private boolean isComplete;
      private int nalRefIdc;
      private int picOrderCntLsb;
      private int picParameterSetId;
      private int sliceType;
      private NalUnitUtil.SpsData spsData;
      
      private boolean isFirstVclNalUnitOfPicture(SliceHeaderData paramSliceHeaderData)
      {
        boolean bool1 = true;
        if (this.isComplete)
        {
          bool2 = bool1;
          if (paramSliceHeaderData.isComplete)
          {
            bool2 = bool1;
            if (this.frameNum == paramSliceHeaderData.frameNum)
            {
              bool2 = bool1;
              if (this.picParameterSetId == paramSliceHeaderData.picParameterSetId)
              {
                bool2 = bool1;
                if (this.fieldPicFlag == paramSliceHeaderData.fieldPicFlag) {
                  if ((this.bottomFieldFlagPresent) && (paramSliceHeaderData.bottomFieldFlagPresent))
                  {
                    bool2 = bool1;
                    if (this.bottomFieldFlag != paramSliceHeaderData.bottomFieldFlag) {}
                  }
                  else if (this.nalRefIdc != paramSliceHeaderData.nalRefIdc)
                  {
                    bool2 = bool1;
                    if (this.nalRefIdc != 0)
                    {
                      bool2 = bool1;
                      if (paramSliceHeaderData.nalRefIdc == 0) {}
                    }
                  }
                  else if ((this.spsData.picOrderCountType == 0) && (paramSliceHeaderData.spsData.picOrderCountType == 0))
                  {
                    bool2 = bool1;
                    if (this.picOrderCntLsb == paramSliceHeaderData.picOrderCntLsb)
                    {
                      bool2 = bool1;
                      if (this.deltaPicOrderCntBottom != paramSliceHeaderData.deltaPicOrderCntBottom) {}
                    }
                  }
                  else if ((this.spsData.picOrderCountType == 1) && (paramSliceHeaderData.spsData.picOrderCountType == 1))
                  {
                    bool2 = bool1;
                    if (this.deltaPicOrderCnt0 == paramSliceHeaderData.deltaPicOrderCnt0)
                    {
                      bool2 = bool1;
                      if (this.deltaPicOrderCnt1 != paramSliceHeaderData.deltaPicOrderCnt1) {}
                    }
                  }
                  else
                  {
                    bool2 = bool1;
                    if (this.idrPicFlag == paramSliceHeaderData.idrPicFlag) {
                      if ((!this.idrPicFlag) || (!paramSliceHeaderData.idrPicFlag) || (this.idrPicId == paramSliceHeaderData.idrPicId)) {
                        break label249;
                      }
                    }
                  }
                }
              }
            }
          }
        }
        label249:
        for (boolean bool2 = bool1;; bool2 = false) {
          return bool2;
        }
      }
      
      public void clear()
      {
        this.hasSliceType = false;
        this.isComplete = false;
      }
      
      public boolean isISlice()
      {
        if ((this.hasSliceType) && ((this.sliceType == 7) || (this.sliceType == 2))) {}
        for (boolean bool = true;; bool = false) {
          return bool;
        }
      }
      
      public void setAll(NalUnitUtil.SpsData paramSpsData, int paramInt1, int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean1, boolean paramBoolean2, boolean paramBoolean3, boolean paramBoolean4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, int paramInt9)
      {
        this.spsData = paramSpsData;
        this.nalRefIdc = paramInt1;
        this.sliceType = paramInt2;
        this.frameNum = paramInt3;
        this.picParameterSetId = paramInt4;
        this.fieldPicFlag = paramBoolean1;
        this.bottomFieldFlagPresent = paramBoolean2;
        this.bottomFieldFlag = paramBoolean3;
        this.idrPicFlag = paramBoolean4;
        this.idrPicId = paramInt5;
        this.picOrderCntLsb = paramInt6;
        this.deltaPicOrderCntBottom = paramInt7;
        this.deltaPicOrderCnt0 = paramInt8;
        this.deltaPicOrderCnt1 = paramInt9;
        this.isComplete = true;
        this.hasSliceType = true;
      }
      
      public void setSliceType(int paramInt)
      {
        this.sliceType = paramInt;
        this.hasSliceType = true;
      }
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/extractor/ts/H264Reader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */