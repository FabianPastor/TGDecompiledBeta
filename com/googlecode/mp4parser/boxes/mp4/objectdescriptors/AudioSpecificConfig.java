package com.googlecode.mp4parser.boxes.mp4.objectdescriptors;

import com.coremedia.iso.Hex;
import com.coremedia.iso.IsoTypeWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Descriptor(objectTypeIndication=64, tags={5})
public class AudioSpecificConfig
  extends BaseDescriptor
{
  public static Map<Integer, String> audioObjectTypeMap;
  public static Map<Integer, Integer> samplingFrequencyIndexMap = new HashMap();
  public boolean aacScalefactorDataResilienceFlag;
  public boolean aacSectionDataResilienceFlag;
  public boolean aacSpectralDataResilienceFlag;
  public int audioObjectType;
  public int channelConfiguration;
  byte[] configBytes;
  public int coreCoderDelay;
  public int dependsOnCoreCoder;
  public int directMapping;
  public ELDSpecificConfig eldSpecificConfig;
  public int epConfig;
  public int erHvxcExtensionFlag;
  public int extensionAudioObjectType;
  public int extensionChannelConfiguration;
  public int extensionFlag;
  public int extensionFlag3;
  public int extensionSamplingFrequency;
  public int extensionSamplingFrequencyIndex;
  public int fillBits;
  public int frameLengthFlag;
  public boolean gaSpecificConfig;
  public int hilnContMode;
  public int hilnEnhaLayer;
  public int hilnEnhaQuantMode;
  public int hilnFrameLength;
  public int hilnMaxNumLine;
  public int hilnQuantMode;
  public int hilnSampleRateCode;
  public int hvxcRateMode;
  public int hvxcVarMode;
  public int isBaseLayer;
  public int layerNr;
  public int layer_length;
  public int numOfSubFrame;
  public int paraExtensionFlag;
  public int paraMode;
  public boolean parametricSpecificConfig;
  public boolean psPresentFlag;
  public int sacPayloadEmbedding;
  public int samplingFrequency;
  public int samplingFrequencyIndex;
  public boolean sbrPresentFlag;
  public int syncExtensionType;
  public int var_ScalableFlag;
  
  static
  {
    audioObjectTypeMap = new HashMap();
    samplingFrequencyIndexMap.put(Integer.valueOf(0), Integer.valueOf(96000));
    samplingFrequencyIndexMap.put(Integer.valueOf(1), Integer.valueOf(88200));
    samplingFrequencyIndexMap.put(Integer.valueOf(2), Integer.valueOf(64000));
    samplingFrequencyIndexMap.put(Integer.valueOf(3), Integer.valueOf(48000));
    samplingFrequencyIndexMap.put(Integer.valueOf(4), Integer.valueOf(44100));
    samplingFrequencyIndexMap.put(Integer.valueOf(5), Integer.valueOf(32000));
    samplingFrequencyIndexMap.put(Integer.valueOf(6), Integer.valueOf(24000));
    samplingFrequencyIndexMap.put(Integer.valueOf(7), Integer.valueOf(22050));
    samplingFrequencyIndexMap.put(Integer.valueOf(8), Integer.valueOf(16000));
    samplingFrequencyIndexMap.put(Integer.valueOf(9), Integer.valueOf(12000));
    samplingFrequencyIndexMap.put(Integer.valueOf(10), Integer.valueOf(11025));
    samplingFrequencyIndexMap.put(Integer.valueOf(11), Integer.valueOf(8000));
    audioObjectTypeMap.put(Integer.valueOf(1), "AAC main");
    audioObjectTypeMap.put(Integer.valueOf(2), "AAC LC");
    audioObjectTypeMap.put(Integer.valueOf(3), "AAC SSR");
    audioObjectTypeMap.put(Integer.valueOf(4), "AAC LTP");
    audioObjectTypeMap.put(Integer.valueOf(5), "SBR");
    audioObjectTypeMap.put(Integer.valueOf(6), "AAC Scalable");
    audioObjectTypeMap.put(Integer.valueOf(7), "TwinVQ");
    audioObjectTypeMap.put(Integer.valueOf(8), "CELP");
    audioObjectTypeMap.put(Integer.valueOf(9), "HVXC");
    audioObjectTypeMap.put(Integer.valueOf(10), "(reserved)");
    audioObjectTypeMap.put(Integer.valueOf(11), "(reserved)");
    audioObjectTypeMap.put(Integer.valueOf(12), "TTSI");
    audioObjectTypeMap.put(Integer.valueOf(13), "Main synthetic");
    audioObjectTypeMap.put(Integer.valueOf(14), "Wavetable synthesis");
    audioObjectTypeMap.put(Integer.valueOf(15), "General MIDI");
    audioObjectTypeMap.put(Integer.valueOf(16), "Algorithmic Synthesis and Audio FX");
    audioObjectTypeMap.put(Integer.valueOf(17), "ER AAC LC");
    audioObjectTypeMap.put(Integer.valueOf(18), "(reserved)");
    audioObjectTypeMap.put(Integer.valueOf(19), "ER AAC LTP");
    audioObjectTypeMap.put(Integer.valueOf(20), "ER AAC Scalable");
    audioObjectTypeMap.put(Integer.valueOf(21), "ER TwinVQ");
    audioObjectTypeMap.put(Integer.valueOf(22), "ER BSAC");
    audioObjectTypeMap.put(Integer.valueOf(23), "ER AAC LD");
    audioObjectTypeMap.put(Integer.valueOf(24), "ER CELP");
    audioObjectTypeMap.put(Integer.valueOf(25), "ER HVXC");
    audioObjectTypeMap.put(Integer.valueOf(26), "ER HILN");
    audioObjectTypeMap.put(Integer.valueOf(27), "ER Parametric");
    audioObjectTypeMap.put(Integer.valueOf(28), "SSC");
    audioObjectTypeMap.put(Integer.valueOf(29), "PS");
    audioObjectTypeMap.put(Integer.valueOf(30), "MPEG Surround");
    audioObjectTypeMap.put(Integer.valueOf(31), "(escape)");
    audioObjectTypeMap.put(Integer.valueOf(32), "Layer-1");
    audioObjectTypeMap.put(Integer.valueOf(33), "Layer-2");
    audioObjectTypeMap.put(Integer.valueOf(34), "Layer-3");
    audioObjectTypeMap.put(Integer.valueOf(35), "DST");
    audioObjectTypeMap.put(Integer.valueOf(36), "ALS");
    audioObjectTypeMap.put(Integer.valueOf(37), "SLS");
    audioObjectTypeMap.put(Integer.valueOf(38), "SLS non-core");
    audioObjectTypeMap.put(Integer.valueOf(39), "ER AAC ELD");
    audioObjectTypeMap.put(Integer.valueOf(40), "SMR Simple");
    audioObjectTypeMap.put(Integer.valueOf(41), "SMR Main");
  }
  
  private int gaSpecificConfigSize()
  {
    return 0;
  }
  
  private int getAudioObjectType(BitReaderBuffer paramBitReaderBuffer)
    throws IOException
  {
    int i = paramBitReaderBuffer.readBits(5);
    int j = i;
    if (i == 31) {
      j = paramBitReaderBuffer.readBits(6) + 32;
    }
    return j;
  }
  
  private void parseErHvxcConfig(int paramInt1, int paramInt2, int paramInt3, BitReaderBuffer paramBitReaderBuffer)
    throws IOException
  {
    this.hvxcVarMode = paramBitReaderBuffer.readBits(1);
    this.hvxcRateMode = paramBitReaderBuffer.readBits(2);
    this.erHvxcExtensionFlag = paramBitReaderBuffer.readBits(1);
    if (this.erHvxcExtensionFlag == 1) {
      this.var_ScalableFlag = paramBitReaderBuffer.readBits(1);
    }
  }
  
  private void parseGaSpecificConfig(int paramInt1, int paramInt2, int paramInt3, BitReaderBuffer paramBitReaderBuffer)
    throws IOException
  {
    this.frameLengthFlag = paramBitReaderBuffer.readBits(1);
    this.dependsOnCoreCoder = paramBitReaderBuffer.readBits(1);
    if (this.dependsOnCoreCoder == 1) {
      this.coreCoderDelay = paramBitReaderBuffer.readBits(14);
    }
    this.extensionFlag = paramBitReaderBuffer.readBits(1);
    if (paramInt2 == 0) {
      throw new UnsupportedOperationException("can't parse program_config_element yet");
    }
    if ((paramInt3 == 6) || (paramInt3 == 20)) {
      this.layerNr = paramBitReaderBuffer.readBits(3);
    }
    if (this.extensionFlag == 1)
    {
      if (paramInt3 == 22)
      {
        this.numOfSubFrame = paramBitReaderBuffer.readBits(5);
        this.layer_length = paramBitReaderBuffer.readBits(11);
      }
      if ((paramInt3 == 17) || (paramInt3 == 19) || (paramInt3 == 20) || (paramInt3 == 23))
      {
        this.aacSectionDataResilienceFlag = paramBitReaderBuffer.readBool();
        this.aacScalefactorDataResilienceFlag = paramBitReaderBuffer.readBool();
        this.aacSpectralDataResilienceFlag = paramBitReaderBuffer.readBool();
      }
      this.extensionFlag3 = paramBitReaderBuffer.readBits(1);
    }
    this.gaSpecificConfig = true;
  }
  
  private void parseHilnConfig(int paramInt1, int paramInt2, int paramInt3, BitReaderBuffer paramBitReaderBuffer)
    throws IOException
  {
    this.hilnQuantMode = paramBitReaderBuffer.readBits(1);
    this.hilnMaxNumLine = paramBitReaderBuffer.readBits(8);
    this.hilnSampleRateCode = paramBitReaderBuffer.readBits(4);
    this.hilnFrameLength = paramBitReaderBuffer.readBits(12);
    this.hilnContMode = paramBitReaderBuffer.readBits(2);
  }
  
  private void parseHilnEnexConfig(int paramInt1, int paramInt2, int paramInt3, BitReaderBuffer paramBitReaderBuffer)
    throws IOException
  {
    this.hilnEnhaLayer = paramBitReaderBuffer.readBits(1);
    if (this.hilnEnhaLayer == 1) {
      this.hilnEnhaQuantMode = paramBitReaderBuffer.readBits(2);
    }
  }
  
  private void parseParaConfig(int paramInt1, int paramInt2, int paramInt3, BitReaderBuffer paramBitReaderBuffer)
    throws IOException
  {
    this.paraMode = paramBitReaderBuffer.readBits(2);
    if (this.paraMode != 1) {
      parseErHvxcConfig(paramInt1, paramInt2, paramInt3, paramBitReaderBuffer);
    }
    if (this.paraMode != 0) {
      parseHilnConfig(paramInt1, paramInt2, paramInt3, paramBitReaderBuffer);
    }
    this.paraExtensionFlag = paramBitReaderBuffer.readBits(1);
    this.parametricSpecificConfig = true;
  }
  
  private void parseParametricSpecificConfig(int paramInt1, int paramInt2, int paramInt3, BitReaderBuffer paramBitReaderBuffer)
    throws IOException
  {
    this.isBaseLayer = paramBitReaderBuffer.readBits(1);
    if (this.isBaseLayer == 1) {
      parseParaConfig(paramInt1, paramInt2, paramInt3, paramBitReaderBuffer);
    }
    for (;;)
    {
      return;
      parseHilnEnexConfig(paramInt1, paramInt2, paramInt3, paramBitReaderBuffer);
    }
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool = true;
    if (this == paramObject) {}
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (AudioSpecificConfig)paramObject;
        if (this.aacScalefactorDataResilienceFlag != ((AudioSpecificConfig)paramObject).aacScalefactorDataResilienceFlag) {
          bool = false;
        } else if (this.aacSectionDataResilienceFlag != ((AudioSpecificConfig)paramObject).aacSectionDataResilienceFlag) {
          bool = false;
        } else if (this.aacSpectralDataResilienceFlag != ((AudioSpecificConfig)paramObject).aacSpectralDataResilienceFlag) {
          bool = false;
        } else if (this.audioObjectType != ((AudioSpecificConfig)paramObject).audioObjectType) {
          bool = false;
        } else if (this.channelConfiguration != ((AudioSpecificConfig)paramObject).channelConfiguration) {
          bool = false;
        } else if (this.coreCoderDelay != ((AudioSpecificConfig)paramObject).coreCoderDelay) {
          bool = false;
        } else if (this.dependsOnCoreCoder != ((AudioSpecificConfig)paramObject).dependsOnCoreCoder) {
          bool = false;
        } else if (this.directMapping != ((AudioSpecificConfig)paramObject).directMapping) {
          bool = false;
        } else if (this.epConfig != ((AudioSpecificConfig)paramObject).epConfig) {
          bool = false;
        } else if (this.erHvxcExtensionFlag != ((AudioSpecificConfig)paramObject).erHvxcExtensionFlag) {
          bool = false;
        } else if (this.extensionAudioObjectType != ((AudioSpecificConfig)paramObject).extensionAudioObjectType) {
          bool = false;
        } else if (this.extensionChannelConfiguration != ((AudioSpecificConfig)paramObject).extensionChannelConfiguration) {
          bool = false;
        } else if (this.extensionFlag != ((AudioSpecificConfig)paramObject).extensionFlag) {
          bool = false;
        } else if (this.extensionFlag3 != ((AudioSpecificConfig)paramObject).extensionFlag3) {
          bool = false;
        } else if (this.extensionSamplingFrequency != ((AudioSpecificConfig)paramObject).extensionSamplingFrequency) {
          bool = false;
        } else if (this.extensionSamplingFrequencyIndex != ((AudioSpecificConfig)paramObject).extensionSamplingFrequencyIndex) {
          bool = false;
        } else if (this.fillBits != ((AudioSpecificConfig)paramObject).fillBits) {
          bool = false;
        } else if (this.frameLengthFlag != ((AudioSpecificConfig)paramObject).frameLengthFlag) {
          bool = false;
        } else if (this.gaSpecificConfig != ((AudioSpecificConfig)paramObject).gaSpecificConfig) {
          bool = false;
        } else if (this.hilnContMode != ((AudioSpecificConfig)paramObject).hilnContMode) {
          bool = false;
        } else if (this.hilnEnhaLayer != ((AudioSpecificConfig)paramObject).hilnEnhaLayer) {
          bool = false;
        } else if (this.hilnEnhaQuantMode != ((AudioSpecificConfig)paramObject).hilnEnhaQuantMode) {
          bool = false;
        } else if (this.hilnFrameLength != ((AudioSpecificConfig)paramObject).hilnFrameLength) {
          bool = false;
        } else if (this.hilnMaxNumLine != ((AudioSpecificConfig)paramObject).hilnMaxNumLine) {
          bool = false;
        } else if (this.hilnQuantMode != ((AudioSpecificConfig)paramObject).hilnQuantMode) {
          bool = false;
        } else if (this.hilnSampleRateCode != ((AudioSpecificConfig)paramObject).hilnSampleRateCode) {
          bool = false;
        } else if (this.hvxcRateMode != ((AudioSpecificConfig)paramObject).hvxcRateMode) {
          bool = false;
        } else if (this.hvxcVarMode != ((AudioSpecificConfig)paramObject).hvxcVarMode) {
          bool = false;
        } else if (this.isBaseLayer != ((AudioSpecificConfig)paramObject).isBaseLayer) {
          bool = false;
        } else if (this.layerNr != ((AudioSpecificConfig)paramObject).layerNr) {
          bool = false;
        } else if (this.layer_length != ((AudioSpecificConfig)paramObject).layer_length) {
          bool = false;
        } else if (this.numOfSubFrame != ((AudioSpecificConfig)paramObject).numOfSubFrame) {
          bool = false;
        } else if (this.paraExtensionFlag != ((AudioSpecificConfig)paramObject).paraExtensionFlag) {
          bool = false;
        } else if (this.paraMode != ((AudioSpecificConfig)paramObject).paraMode) {
          bool = false;
        } else if (this.parametricSpecificConfig != ((AudioSpecificConfig)paramObject).parametricSpecificConfig) {
          bool = false;
        } else if (this.psPresentFlag != ((AudioSpecificConfig)paramObject).psPresentFlag) {
          bool = false;
        } else if (this.sacPayloadEmbedding != ((AudioSpecificConfig)paramObject).sacPayloadEmbedding) {
          bool = false;
        } else if (this.samplingFrequency != ((AudioSpecificConfig)paramObject).samplingFrequency) {
          bool = false;
        } else if (this.samplingFrequencyIndex != ((AudioSpecificConfig)paramObject).samplingFrequencyIndex) {
          bool = false;
        } else if (this.sbrPresentFlag != ((AudioSpecificConfig)paramObject).sbrPresentFlag) {
          bool = false;
        } else if (this.syncExtensionType != ((AudioSpecificConfig)paramObject).syncExtensionType) {
          bool = false;
        } else if (this.var_ScalableFlag != ((AudioSpecificConfig)paramObject).var_ScalableFlag) {
          bool = false;
        } else if (!Arrays.equals(this.configBytes, ((AudioSpecificConfig)paramObject).configBytes)) {
          bool = false;
        }
      }
    }
  }
  
  public int hashCode()
  {
    int i = 1;
    int j;
    int k;
    int m;
    int n;
    int i1;
    int i2;
    int i3;
    label56:
    int i4;
    label66:
    int i5;
    int i6;
    int i7;
    int i8;
    int i9;
    int i10;
    int i11;
    int i12;
    int i13;
    int i14;
    int i15;
    int i16;
    int i17;
    int i18;
    int i19;
    int i20;
    label166:
    int i21;
    label176:
    int i22;
    label186:
    int i23;
    int i24;
    label202:
    int i25;
    int i26;
    int i27;
    int i28;
    int i29;
    int i30;
    int i31;
    int i32;
    int i33;
    int i34;
    int i35;
    int i36;
    int i37;
    int i38;
    if (this.configBytes != null)
    {
      j = Arrays.hashCode(this.configBytes);
      k = this.audioObjectType;
      m = this.samplingFrequencyIndex;
      n = this.samplingFrequency;
      i1 = this.channelConfiguration;
      i2 = this.extensionAudioObjectType;
      if (!this.sbrPresentFlag) {
        break label550;
      }
      i3 = 1;
      if (!this.psPresentFlag) {
        break label556;
      }
      i4 = 1;
      i5 = this.extensionSamplingFrequencyIndex;
      i6 = this.extensionSamplingFrequency;
      i7 = this.extensionChannelConfiguration;
      i8 = this.sacPayloadEmbedding;
      i9 = this.fillBits;
      i10 = this.epConfig;
      i11 = this.directMapping;
      i12 = this.syncExtensionType;
      i13 = this.frameLengthFlag;
      i14 = this.dependsOnCoreCoder;
      i15 = this.coreCoderDelay;
      i16 = this.extensionFlag;
      i17 = this.layerNr;
      i18 = this.numOfSubFrame;
      i19 = this.layer_length;
      if (!this.aacSectionDataResilienceFlag) {
        break label562;
      }
      i20 = 1;
      if (!this.aacScalefactorDataResilienceFlag) {
        break label568;
      }
      i21 = 1;
      if (!this.aacSpectralDataResilienceFlag) {
        break label574;
      }
      i22 = 1;
      i23 = this.extensionFlag3;
      if (!this.gaSpecificConfig) {
        break label580;
      }
      i24 = 1;
      i25 = this.isBaseLayer;
      i26 = this.paraMode;
      i27 = this.paraExtensionFlag;
      i28 = this.hvxcVarMode;
      i29 = this.hvxcRateMode;
      i30 = this.erHvxcExtensionFlag;
      i31 = this.var_ScalableFlag;
      i32 = this.hilnQuantMode;
      i33 = this.hilnMaxNumLine;
      i34 = this.hilnSampleRateCode;
      i35 = this.hilnFrameLength;
      i36 = this.hilnContMode;
      i37 = this.hilnEnhaLayer;
      i38 = this.hilnEnhaQuantMode;
      if (!this.parametricSpecificConfig) {
        break label586;
      }
    }
    for (;;)
    {
      return (((((((((((((((((((((((((((((((((((((((((j * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + i5) * 31 + i6) * 31 + i7) * 31 + i8) * 31 + i9) * 31 + i10) * 31 + i11) * 31 + i12) * 31 + i13) * 31 + i14) * 31 + i15) * 31 + i16) * 31 + i17) * 31 + i18) * 31 + i19) * 31 + i20) * 31 + i21) * 31 + i22) * 31 + i23) * 31 + i24) * 31 + i25) * 31 + i26) * 31 + i27) * 31 + i28) * 31 + i29) * 31 + i30) * 31 + i31) * 31 + i32) * 31 + i33) * 31 + i34) * 31 + i35) * 31 + i36) * 31 + i37) * 31 + i38) * 31 + i;
      j = 0;
      break;
      label550:
      i3 = 0;
      break label56;
      label556:
      i4 = 0;
      break label66;
      label562:
      i20 = 0;
      break label166;
      label568:
      i21 = 0;
      break label176;
      label574:
      i22 = 0;
      break label186;
      label580:
      i24 = 0;
      break label202;
      label586:
      i = 0;
    }
  }
  
  public void parseDetail(ByteBuffer paramByteBuffer)
    throws IOException
  {
    ByteBuffer localByteBuffer = paramByteBuffer.slice();
    localByteBuffer.limit(this.sizeOfInstance);
    paramByteBuffer.position(paramByteBuffer.position() + this.sizeOfInstance);
    this.configBytes = new byte[this.sizeOfInstance];
    localByteBuffer.get(this.configBytes);
    localByteBuffer.rewind();
    paramByteBuffer = new BitReaderBuffer(localByteBuffer);
    this.audioObjectType = getAudioObjectType(paramByteBuffer);
    this.samplingFrequencyIndex = paramByteBuffer.readBits(4);
    if (this.samplingFrequencyIndex == 15) {
      this.samplingFrequency = paramByteBuffer.readBits(24);
    }
    this.channelConfiguration = paramByteBuffer.readBits(4);
    if ((this.audioObjectType == 5) || (this.audioObjectType == 29))
    {
      this.extensionAudioObjectType = 5;
      this.sbrPresentFlag = true;
      if (this.audioObjectType == 29) {
        this.psPresentFlag = true;
      }
      this.extensionSamplingFrequencyIndex = paramByteBuffer.readBits(4);
      if (this.extensionSamplingFrequencyIndex == 15) {
        this.extensionSamplingFrequency = paramByteBuffer.readBits(24);
      }
      this.audioObjectType = getAudioObjectType(paramByteBuffer);
      if (this.audioObjectType == 22) {
        this.extensionChannelConfiguration = paramByteBuffer.readBits(4);
      }
      switch (this.audioObjectType)
      {
      case 5: 
      case 10: 
      case 11: 
      case 18: 
      case 29: 
      case 31: 
      default: 
        label384:
        switch (this.audioObjectType)
        {
        }
        break;
      }
    }
    do
    {
      do
      {
        if ((this.extensionAudioObjectType != 5) && (paramByteBuffer.remainingBits() >= 16))
        {
          this.syncExtensionType = paramByteBuffer.readBits(11);
          if (this.syncExtensionType == 695)
          {
            this.extensionAudioObjectType = getAudioObjectType(paramByteBuffer);
            if (this.extensionAudioObjectType == 5)
            {
              this.sbrPresentFlag = paramByteBuffer.readBool();
              if (this.sbrPresentFlag)
              {
                this.extensionSamplingFrequencyIndex = paramByteBuffer.readBits(4);
                if (this.extensionSamplingFrequencyIndex == 15) {
                  this.extensionSamplingFrequency = paramByteBuffer.readBits(24);
                }
                if (paramByteBuffer.remainingBits() >= 12)
                {
                  this.syncExtensionType = paramByteBuffer.readBits(11);
                  if (this.syncExtensionType == 1352) {
                    this.psPresentFlag = paramByteBuffer.readBool();
                  }
                }
              }
            }
            if (this.extensionAudioObjectType == 22)
            {
              this.sbrPresentFlag = paramByteBuffer.readBool();
              if (this.sbrPresentFlag)
              {
                this.extensionSamplingFrequencyIndex = paramByteBuffer.readBits(4);
                if (this.extensionSamplingFrequencyIndex == 15) {
                  this.extensionSamplingFrequency = paramByteBuffer.readBits(24);
                }
              }
              this.extensionChannelConfiguration = paramByteBuffer.readBits(4);
            }
          }
        }
        return;
        this.extensionAudioObjectType = 0;
        break;
        parseGaSpecificConfig(this.samplingFrequencyIndex, this.channelConfiguration, this.audioObjectType, paramByteBuffer);
        break label384;
        throw new UnsupportedOperationException("can't parse CelpSpecificConfig yet");
        throw new UnsupportedOperationException("can't parse HvxcSpecificConfig yet");
        throw new UnsupportedOperationException("can't parse TTSSpecificConfig yet");
        throw new UnsupportedOperationException("can't parse StructuredAudioSpecificConfig yet");
        throw new UnsupportedOperationException("can't parse ErrorResilientCelpSpecificConfig yet");
        throw new UnsupportedOperationException("can't parse ErrorResilientHvxcSpecificConfig yet");
        parseParametricSpecificConfig(this.samplingFrequencyIndex, this.channelConfiguration, this.audioObjectType, paramByteBuffer);
        break label384;
        throw new UnsupportedOperationException("can't parse SSCSpecificConfig yet");
        this.sacPayloadEmbedding = paramByteBuffer.readBits(1);
        throw new UnsupportedOperationException("can't parse SpatialSpecificConfig yet");
        throw new UnsupportedOperationException("can't parse MPEG_1_2_SpecificConfig yet");
        throw new UnsupportedOperationException("can't parse DSTSpecificConfig yet");
        this.fillBits = paramByteBuffer.readBits(5);
        throw new UnsupportedOperationException("can't parse ALSSpecificConfig yet");
        throw new UnsupportedOperationException("can't parse SLSSpecificConfig yet");
        this.eldSpecificConfig = new ELDSpecificConfig(this.channelConfiguration, paramByteBuffer);
        break label384;
        throw new UnsupportedOperationException("can't parse SymbolicMusicSpecificConfig yet");
        this.epConfig = paramByteBuffer.readBits(2);
        if ((this.epConfig == 2) || (this.epConfig == 3)) {
          throw new UnsupportedOperationException("can't parse ErrorProtectionSpecificConfig yet");
        }
      } while (this.epConfig != 3);
      this.directMapping = paramByteBuffer.readBits(1);
    } while (this.directMapping != 0);
    throw new RuntimeException("not implemented");
  }
  
  public ByteBuffer serialize()
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(serializedSize());
    IsoTypeWriter.writeUInt8(localByteBuffer, 5);
    IsoTypeWriter.writeUInt8(localByteBuffer, serializedSize() - 2);
    BitWriterBuffer localBitWriterBuffer = new BitWriterBuffer(localByteBuffer);
    localBitWriterBuffer.writeBits(this.audioObjectType, 5);
    localBitWriterBuffer.writeBits(this.samplingFrequencyIndex, 4);
    if (this.samplingFrequencyIndex == 15) {
      throw new UnsupportedOperationException("can't serialize that yet");
    }
    localBitWriterBuffer.writeBits(this.channelConfiguration, 4);
    return localByteBuffer;
  }
  
  public int serializedSize()
  {
    if (this.audioObjectType == 2) {
      return 4 + gaSpecificConfigSize();
    }
    throw new UnsupportedOperationException("can't serialize that yet");
  }
  
  public void setAudioObjectType(int paramInt)
  {
    this.audioObjectType = paramInt;
  }
  
  public void setChannelConfiguration(int paramInt)
  {
    this.channelConfiguration = paramInt;
  }
  
  public void setSamplingFrequencyIndex(int paramInt)
  {
    this.samplingFrequencyIndex = paramInt;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("AudioSpecificConfig");
    localStringBuilder.append("{configBytes=").append(Hex.encodeHex(this.configBytes));
    localStringBuilder.append(", audioObjectType=").append(this.audioObjectType).append(" (").append((String)audioObjectTypeMap.get(Integer.valueOf(this.audioObjectType))).append(")");
    localStringBuilder.append(", samplingFrequencyIndex=").append(this.samplingFrequencyIndex).append(" (").append(samplingFrequencyIndexMap.get(Integer.valueOf(this.samplingFrequencyIndex))).append(")");
    localStringBuilder.append(", samplingFrequency=").append(this.samplingFrequency);
    localStringBuilder.append(", channelConfiguration=").append(this.channelConfiguration);
    if (this.extensionAudioObjectType > 0)
    {
      localStringBuilder.append(", extensionAudioObjectType=").append(this.extensionAudioObjectType).append(" (").append((String)audioObjectTypeMap.get(Integer.valueOf(this.extensionAudioObjectType))).append(")");
      localStringBuilder.append(", sbrPresentFlag=").append(this.sbrPresentFlag);
      localStringBuilder.append(", psPresentFlag=").append(this.psPresentFlag);
      localStringBuilder.append(", extensionSamplingFrequencyIndex=").append(this.extensionSamplingFrequencyIndex).append(" (").append(samplingFrequencyIndexMap.get(Integer.valueOf(this.extensionSamplingFrequencyIndex))).append(")");
      localStringBuilder.append(", extensionSamplingFrequency=").append(this.extensionSamplingFrequency);
      localStringBuilder.append(", extensionChannelConfiguration=").append(this.extensionChannelConfiguration);
    }
    localStringBuilder.append(", syncExtensionType=").append(this.syncExtensionType);
    if (this.gaSpecificConfig)
    {
      localStringBuilder.append(", frameLengthFlag=").append(this.frameLengthFlag);
      localStringBuilder.append(", dependsOnCoreCoder=").append(this.dependsOnCoreCoder);
      localStringBuilder.append(", coreCoderDelay=").append(this.coreCoderDelay);
      localStringBuilder.append(", extensionFlag=").append(this.extensionFlag);
      localStringBuilder.append(", layerNr=").append(this.layerNr);
      localStringBuilder.append(", numOfSubFrame=").append(this.numOfSubFrame);
      localStringBuilder.append(", layer_length=").append(this.layer_length);
      localStringBuilder.append(", aacSectionDataResilienceFlag=").append(this.aacSectionDataResilienceFlag);
      localStringBuilder.append(", aacScalefactorDataResilienceFlag=").append(this.aacScalefactorDataResilienceFlag);
      localStringBuilder.append(", aacSpectralDataResilienceFlag=").append(this.aacSpectralDataResilienceFlag);
      localStringBuilder.append(", extensionFlag3=").append(this.extensionFlag3);
    }
    if (this.parametricSpecificConfig)
    {
      localStringBuilder.append(", isBaseLayer=").append(this.isBaseLayer);
      localStringBuilder.append(", paraMode=").append(this.paraMode);
      localStringBuilder.append(", paraExtensionFlag=").append(this.paraExtensionFlag);
      localStringBuilder.append(", hvxcVarMode=").append(this.hvxcVarMode);
      localStringBuilder.append(", hvxcRateMode=").append(this.hvxcRateMode);
      localStringBuilder.append(", erHvxcExtensionFlag=").append(this.erHvxcExtensionFlag);
      localStringBuilder.append(", var_ScalableFlag=").append(this.var_ScalableFlag);
      localStringBuilder.append(", hilnQuantMode=").append(this.hilnQuantMode);
      localStringBuilder.append(", hilnMaxNumLine=").append(this.hilnMaxNumLine);
      localStringBuilder.append(", hilnSampleRateCode=").append(this.hilnSampleRateCode);
      localStringBuilder.append(", hilnFrameLength=").append(this.hilnFrameLength);
      localStringBuilder.append(", hilnContMode=").append(this.hilnContMode);
      localStringBuilder.append(", hilnEnhaLayer=").append(this.hilnEnhaLayer);
      localStringBuilder.append(", hilnEnhaQuantMode=").append(this.hilnEnhaQuantMode);
    }
    localStringBuilder.append('}');
    return localStringBuilder.toString();
  }
  
  public class ELDSpecificConfig
  {
    public boolean aacScalefactorDataResilienceFlag;
    public boolean aacSectionDataResilienceFlag;
    public boolean aacSpectralDataResilienceFlag;
    public boolean frameLengthFlag;
    public boolean ldSbrCrcFlag;
    public boolean ldSbrPresentFlag;
    public boolean ldSbrSamplingRate;
    
    public ELDSpecificConfig(int paramInt, BitReaderBuffer paramBitReaderBuffer)
    {
      this.frameLengthFlag = paramBitReaderBuffer.readBool();
      this.aacSectionDataResilienceFlag = paramBitReaderBuffer.readBool();
      this.aacScalefactorDataResilienceFlag = paramBitReaderBuffer.readBool();
      this.aacSpectralDataResilienceFlag = paramBitReaderBuffer.readBool();
      this.ldSbrPresentFlag = paramBitReaderBuffer.readBool();
      if (this.ldSbrPresentFlag)
      {
        this.ldSbrSamplingRate = paramBitReaderBuffer.readBool();
        this.ldSbrCrcFlag = paramBitReaderBuffer.readBool();
        ld_sbr_header(paramInt, paramBitReaderBuffer);
      }
      for (;;)
      {
        if (paramBitReaderBuffer.readBits(4) == 0) {
          return;
        }
        int i = paramBitReaderBuffer.readBits(4);
        int j = i;
        int k = 0;
        paramInt = j;
        if (i == 15)
        {
          k = paramBitReaderBuffer.readBits(8);
          paramInt = j + k;
        }
        j = paramInt;
        if (k == 255) {
          j = paramInt + paramBitReaderBuffer.readBits(16);
        }
        for (paramInt = 0; paramInt < j; paramInt++) {
          paramBitReaderBuffer.readBits(8);
        }
      }
    }
    
    public void ld_sbr_header(int paramInt, BitReaderBuffer paramBitReaderBuffer)
    {
      switch (paramInt)
      {
      default: 
        paramInt = 0;
      }
      for (int i = 0;; i++)
      {
        if (i >= paramInt)
        {
          return;
          paramInt = 1;
          break;
          paramInt = 2;
          break;
          paramInt = 3;
          break;
          paramInt = 4;
          break;
        }
        new AudioSpecificConfig.sbr_header(AudioSpecificConfig.this, paramBitReaderBuffer);
      }
    }
  }
  
  public class sbr_header
  {
    public boolean bs_alter_scale;
    public boolean bs_amp_res;
    public int bs_freq_scale;
    public boolean bs_header_extra_1;
    public boolean bs_header_extra_2;
    public boolean bs_interpol_freq;
    public int bs_limiter_bands;
    public int bs_limiter_gains;
    public int bs_noise_bands;
    public int bs_reserved;
    public boolean bs_smoothing_mode;
    public int bs_start_freq;
    public int bs_stop_freq;
    public int bs_xover_band;
    
    public sbr_header(BitReaderBuffer paramBitReaderBuffer)
    {
      this.bs_amp_res = paramBitReaderBuffer.readBool();
      this.bs_start_freq = paramBitReaderBuffer.readBits(4);
      this.bs_stop_freq = paramBitReaderBuffer.readBits(4);
      this.bs_xover_band = paramBitReaderBuffer.readBits(3);
      this.bs_reserved = paramBitReaderBuffer.readBits(2);
      this.bs_header_extra_1 = paramBitReaderBuffer.readBool();
      this.bs_header_extra_2 = paramBitReaderBuffer.readBool();
      if (this.bs_header_extra_1)
      {
        this.bs_freq_scale = paramBitReaderBuffer.readBits(2);
        this.bs_alter_scale = paramBitReaderBuffer.readBool();
        this.bs_noise_bands = paramBitReaderBuffer.readBits(2);
      }
      if (this.bs_header_extra_2)
      {
        this.bs_limiter_bands = paramBitReaderBuffer.readBits(2);
        this.bs_limiter_gains = paramBitReaderBuffer.readBits(2);
        this.bs_interpol_freq = paramBitReaderBuffer.readBool();
      }
      this.bs_smoothing_mode = paramBitReaderBuffer.readBool();
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/com/googlecode/mp4parser/boxes/mp4/objectdescriptors/AudioSpecificConfig.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */