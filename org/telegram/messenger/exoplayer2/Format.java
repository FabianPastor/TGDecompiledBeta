package org.telegram.messenger.exoplayer2;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.media.MediaFormat;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.drm.DrmInitData;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.util.Util;
import org.telegram.messenger.exoplayer2.video.ColorInfo;

public final class Format
  implements Parcelable
{
  public static final Parcelable.Creator<Format> CREATOR = new Parcelable.Creator()
  {
    public Format createFromParcel(Parcel paramAnonymousParcel)
    {
      return new Format(paramAnonymousParcel);
    }
    
    public Format[] newArray(int paramAnonymousInt)
    {
      return new Format[paramAnonymousInt];
    }
  };
  public static final int NO_VALUE = -1;
  public static final long OFFSET_SAMPLE_RELATIVE = Long.MAX_VALUE;
  public final int accessibilityChannel;
  public final int bitrate;
  public final int channelCount;
  public final String codecs;
  public final ColorInfo colorInfo;
  public final String containerMimeType;
  public final DrmInitData drmInitData;
  public final int encoderDelay;
  public final int encoderPadding;
  public final float frameRate;
  private int hashCode;
  public final int height;
  public final String id;
  public final List<byte[]> initializationData;
  public final String language;
  public final int maxInputSize;
  public final Metadata metadata;
  public final int pcmEncoding;
  public final float pixelWidthHeightRatio;
  public final byte[] projectionData;
  public final int rotationDegrees;
  public final String sampleMimeType;
  public final int sampleRate;
  public final int selectionFlags;
  public final int stereoMode;
  public final long subsampleOffsetUs;
  public final int width;
  
  Format(Parcel paramParcel)
  {
    this.id = paramParcel.readString();
    this.containerMimeType = paramParcel.readString();
    this.sampleMimeType = paramParcel.readString();
    this.codecs = paramParcel.readString();
    this.bitrate = paramParcel.readInt();
    this.maxInputSize = paramParcel.readInt();
    this.width = paramParcel.readInt();
    this.height = paramParcel.readInt();
    this.frameRate = paramParcel.readFloat();
    this.rotationDegrees = paramParcel.readInt();
    this.pixelWidthHeightRatio = paramParcel.readFloat();
    int i;
    if (paramParcel.readInt() != 0)
    {
      i = 1;
      if (i == 0) {
        break label263;
      }
    }
    label263:
    for (byte[] arrayOfByte = paramParcel.createByteArray();; arrayOfByte = null)
    {
      this.projectionData = arrayOfByte;
      this.stereoMode = paramParcel.readInt();
      this.colorInfo = ((ColorInfo)paramParcel.readParcelable(ColorInfo.class.getClassLoader()));
      this.channelCount = paramParcel.readInt();
      this.sampleRate = paramParcel.readInt();
      this.pcmEncoding = paramParcel.readInt();
      this.encoderDelay = paramParcel.readInt();
      this.encoderPadding = paramParcel.readInt();
      this.selectionFlags = paramParcel.readInt();
      this.language = paramParcel.readString();
      this.accessibilityChannel = paramParcel.readInt();
      this.subsampleOffsetUs = paramParcel.readLong();
      int j = paramParcel.readInt();
      this.initializationData = new ArrayList(j);
      for (i = 0; i < j; i++) {
        this.initializationData.add(paramParcel.createByteArray());
      }
      i = 0;
      break;
    }
    this.drmInitData = ((DrmInitData)paramParcel.readParcelable(DrmInitData.class.getClassLoader()));
    this.metadata = ((Metadata)paramParcel.readParcelable(Metadata.class.getClassLoader()));
  }
  
  Format(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, int paramInt5, float paramFloat2, byte[] paramArrayOfByte, int paramInt6, ColorInfo paramColorInfo, int paramInt7, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12, String paramString5, int paramInt13, long paramLong, List<byte[]> paramList, DrmInitData paramDrmInitData, Metadata paramMetadata)
  {
    this.id = paramString1;
    this.containerMimeType = paramString2;
    this.sampleMimeType = paramString3;
    this.codecs = paramString4;
    this.bitrate = paramInt1;
    this.maxInputSize = paramInt2;
    this.width = paramInt3;
    this.height = paramInt4;
    this.frameRate = paramFloat1;
    this.rotationDegrees = paramInt5;
    this.pixelWidthHeightRatio = paramFloat2;
    this.projectionData = paramArrayOfByte;
    this.stereoMode = paramInt6;
    this.colorInfo = paramColorInfo;
    this.channelCount = paramInt7;
    this.sampleRate = paramInt8;
    this.pcmEncoding = paramInt9;
    this.encoderDelay = paramInt10;
    this.encoderPadding = paramInt11;
    this.selectionFlags = paramInt12;
    this.language = paramString5;
    this.accessibilityChannel = paramInt13;
    this.subsampleOffsetUs = paramLong;
    paramString1 = paramList;
    if (paramList == null) {
      paramString1 = Collections.emptyList();
    }
    this.initializationData = paramString1;
    this.drmInitData = paramDrmInitData;
    this.metadata = paramMetadata;
  }
  
  public static Format createAudioContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, List<byte[]> paramList, int paramInt4, String paramString5)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, paramInt2, paramInt3, -1, -1, -1, paramInt4, paramString5, -1, Long.MAX_VALUE, paramList, null, null);
  }
  
  public static Format createAudioSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, List<byte[]> paramList, DrmInitData paramDrmInitData, int paramInt8, String paramString4, Metadata paramMetadata)
  {
    return new Format(paramString1, null, paramString2, paramString3, paramInt1, paramInt2, -1, -1, -1.0F, -1, -1.0F, null, -1, null, paramInt3, paramInt4, paramInt5, paramInt6, paramInt7, paramInt8, paramString4, -1, Long.MAX_VALUE, paramList, paramDrmInitData, paramMetadata);
  }
  
  public static Format createAudioSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, List<byte[]> paramList, DrmInitData paramDrmInitData, int paramInt6, String paramString4)
  {
    return createAudioSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, -1, -1, paramList, paramDrmInitData, paramInt6, paramString4, null);
  }
  
  public static Format createAudioSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, List<byte[]> paramList, DrmInitData paramDrmInitData, int paramInt5, String paramString4)
  {
    return createAudioSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, -1, paramList, paramDrmInitData, paramInt5, paramString4);
  }
  
  public static Format createContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, String paramString5)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, paramInt2, paramString5, -1, Long.MAX_VALUE, null, null, null);
  }
  
  public static Format createImageSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, List<byte[]> paramList, String paramString4, DrmInitData paramDrmInitData)
  {
    return new Format(paramString1, null, paramString2, paramString3, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, paramInt2, paramString4, -1, Long.MAX_VALUE, paramList, paramDrmInitData, null);
  }
  
  public static Format createSampleFormat(String paramString1, String paramString2, long paramLong)
  {
    return new Format(paramString1, null, paramString2, null, -1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, 0, null, -1, paramLong, null, null, null);
  }
  
  public static Format createSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt, DrmInitData paramDrmInitData)
  {
    return new Format(paramString1, null, paramString2, paramString3, paramInt, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, 0, null, -1, Long.MAX_VALUE, null, paramDrmInitData, null);
  }
  
  public static Format createTextContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, String paramString5)
  {
    return createTextContainerFormat(paramString1, paramString2, paramString3, paramString4, paramInt1, paramInt2, paramString5, -1);
  }
  
  public static Format createTextContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, String paramString5, int paramInt3)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, paramInt2, paramString5, paramInt3, Long.MAX_VALUE, null, null, null);
  }
  
  public static Format createTextSampleFormat(String paramString1, String paramString2, int paramInt, String paramString3)
  {
    return createTextSampleFormat(paramString1, paramString2, paramInt, paramString3, null);
  }
  
  public static Format createTextSampleFormat(String paramString1, String paramString2, int paramInt, String paramString3, DrmInitData paramDrmInitData)
  {
    return createTextSampleFormat(paramString1, paramString2, null, -1, paramInt, paramString3, -1, paramDrmInitData, Long.MAX_VALUE, Collections.emptyList());
  }
  
  public static Format createTextSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4, int paramInt3, DrmInitData paramDrmInitData)
  {
    return createTextSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramString4, paramInt3, paramDrmInitData, Long.MAX_VALUE, Collections.emptyList());
  }
  
  public static Format createTextSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4, int paramInt3, DrmInitData paramDrmInitData, long paramLong, List<byte[]> paramList)
  {
    return new Format(paramString1, null, paramString2, paramString3, paramInt1, -1, -1, -1, -1.0F, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, paramInt2, paramString4, paramInt3, paramLong, paramList, paramDrmInitData, null);
  }
  
  public static Format createTextSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, String paramString4, DrmInitData paramDrmInitData, long paramLong)
  {
    return createTextSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramString4, -1, paramDrmInitData, paramLong, Collections.emptyList());
  }
  
  public static Format createVideoContainerFormat(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, float paramFloat, List<byte[]> paramList, int paramInt4)
  {
    return new Format(paramString1, paramString2, paramString3, paramString4, paramInt1, -1, paramInt2, paramInt3, paramFloat, -1, -1.0F, null, -1, null, -1, -1, -1, -1, -1, paramInt4, null, -1, Long.MAX_VALUE, paramList, null, null);
  }
  
  public static Format createVideoSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, List<byte[]> paramList, int paramInt5, float paramFloat2, DrmInitData paramDrmInitData)
  {
    return createVideoSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramFloat1, paramList, paramInt5, paramFloat2, null, -1, null, paramDrmInitData);
  }
  
  public static Format createVideoSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat1, List<byte[]> paramList, int paramInt5, float paramFloat2, byte[] paramArrayOfByte, int paramInt6, ColorInfo paramColorInfo, DrmInitData paramDrmInitData)
  {
    return new Format(paramString1, null, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramFloat1, paramInt5, paramFloat2, paramArrayOfByte, paramInt6, paramColorInfo, -1, -1, -1, -1, -1, 0, null, -1, Long.MAX_VALUE, paramList, paramDrmInitData, null);
  }
  
  public static Format createVideoSampleFormat(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, float paramFloat, List<byte[]> paramList, DrmInitData paramDrmInitData)
  {
    return createVideoSampleFormat(paramString1, paramString2, paramString3, paramInt1, paramInt2, paramInt3, paramInt4, paramFloat, paramList, -1, -1.0F, paramDrmInitData);
  }
  
  @TargetApi(16)
  private static void maybeSetByteBufferV16(MediaFormat paramMediaFormat, String paramString, byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte != null) {
      paramMediaFormat.setByteBuffer(paramString, ByteBuffer.wrap(paramArrayOfByte));
    }
  }
  
  @TargetApi(24)
  private static void maybeSetColorInfoV24(MediaFormat paramMediaFormat, ColorInfo paramColorInfo)
  {
    if (paramColorInfo == null) {}
    for (;;)
    {
      return;
      maybeSetIntegerV16(paramMediaFormat, "color-transfer", paramColorInfo.colorTransfer);
      maybeSetIntegerV16(paramMediaFormat, "color-standard", paramColorInfo.colorSpace);
      maybeSetIntegerV16(paramMediaFormat, "color-range", paramColorInfo.colorRange);
      maybeSetByteBufferV16(paramMediaFormat, "hdr-static-info", paramColorInfo.hdrStaticInfo);
    }
  }
  
  @TargetApi(16)
  private static void maybeSetFloatV16(MediaFormat paramMediaFormat, String paramString, float paramFloat)
  {
    if (paramFloat != -1.0F) {
      paramMediaFormat.setFloat(paramString, paramFloat);
    }
  }
  
  @TargetApi(16)
  private static void maybeSetIntegerV16(MediaFormat paramMediaFormat, String paramString, int paramInt)
  {
    if (paramInt != -1) {
      paramMediaFormat.setInteger(paramString, paramInt);
    }
  }
  
  @TargetApi(16)
  private static void maybeSetStringV16(MediaFormat paramMediaFormat, String paramString1, String paramString2)
  {
    if (paramString2 != null) {
      paramMediaFormat.setString(paramString1, paramString2);
    }
  }
  
  public static String toLogString(Format paramFormat)
  {
    if (paramFormat == null) {}
    StringBuilder localStringBuilder;
    for (paramFormat = "null";; paramFormat = localStringBuilder.toString())
    {
      return paramFormat;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("id=").append(paramFormat.id).append(", mimeType=").append(paramFormat.sampleMimeType);
      if (paramFormat.bitrate != -1) {
        localStringBuilder.append(", bitrate=").append(paramFormat.bitrate);
      }
      if ((paramFormat.width != -1) && (paramFormat.height != -1)) {
        localStringBuilder.append(", res=").append(paramFormat.width).append("x").append(paramFormat.height);
      }
      if (paramFormat.frameRate != -1.0F) {
        localStringBuilder.append(", fps=").append(paramFormat.frameRate);
      }
      if (paramFormat.channelCount != -1) {
        localStringBuilder.append(", channels=").append(paramFormat.channelCount);
      }
      if (paramFormat.sampleRate != -1) {
        localStringBuilder.append(", sample_rate=").append(paramFormat.sampleRate);
      }
      if (paramFormat.language != null) {
        localStringBuilder.append(", language=").append(paramFormat.language);
      }
    }
  }
  
  public Format copyWithContainerInfo(String paramString1, String paramString2, String paramString3, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString4)
  {
    return new Format(paramString1, this.containerMimeType, paramString2, paramString3, paramInt1, this.maxInputSize, paramInt2, paramInt3, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, paramInt4, paramString4, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
  }
  
  public Format copyWithDrmInitData(DrmInitData paramDrmInitData)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, paramDrmInitData, this.metadata);
  }
  
  public Format copyWithGaplessInfo(int paramInt1, int paramInt2)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, paramInt1, paramInt2, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
  }
  
  public Format copyWithManifestFormatInfo(Format paramFormat)
  {
    if (this == paramFormat)
    {
      paramFormat = this;
      return paramFormat;
    }
    String str1 = paramFormat.id;
    String str2;
    label26:
    int i;
    label40:
    float f;
    label56:
    int j;
    int k;
    if (this.codecs == null)
    {
      str2 = paramFormat.codecs;
      if (this.bitrate != -1) {
        break label202;
      }
      i = paramFormat.bitrate;
      if (this.frameRate != -1.0F) {
        break label211;
      }
      f = paramFormat.frameRate;
      j = this.selectionFlags;
      k = paramFormat.selectionFlags;
      if (this.language != null) {
        break label220;
      }
    }
    label202:
    label211:
    label220:
    for (String str3 = paramFormat.language;; str3 = this.language)
    {
      paramFormat = DrmInitData.createSessionCreationData(paramFormat.drmInitData, this.drmInitData);
      paramFormat = new Format(str1, this.containerMimeType, this.sampleMimeType, str2, i, this.maxInputSize, this.width, this.height, f, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, j | k, str3, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, paramFormat, this.metadata);
      break;
      str2 = this.codecs;
      break label26;
      i = this.bitrate;
      break label40;
      f = this.frameRate;
      break label56;
    }
  }
  
  public Format copyWithMaxInputSize(int paramInt)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, paramInt, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
  }
  
  public Format copyWithMetadata(Metadata paramMetadata)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, paramMetadata);
  }
  
  public Format copyWithRotationDegrees(int paramInt)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, paramInt, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, this.subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
  }
  
  public Format copyWithSubsampleOffsetUs(long paramLong)
  {
    return new Format(this.id, this.containerMimeType, this.sampleMimeType, this.codecs, this.bitrate, this.maxInputSize, this.width, this.height, this.frameRate, this.rotationDegrees, this.pixelWidthHeightRatio, this.projectionData, this.stereoMode, this.colorInfo, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, paramLong, this.initializationData, this.drmInitData, this.metadata);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    boolean bool;
    if (this == paramObject) {
      bool = true;
    }
    for (;;)
    {
      return bool;
      if ((paramObject == null) || (getClass() != paramObject.getClass()))
      {
        bool = false;
      }
      else
      {
        paramObject = (Format)paramObject;
        if ((this.bitrate != ((Format)paramObject).bitrate) || (this.maxInputSize != ((Format)paramObject).maxInputSize) || (this.width != ((Format)paramObject).width) || (this.height != ((Format)paramObject).height) || (this.frameRate != ((Format)paramObject).frameRate) || (this.rotationDegrees != ((Format)paramObject).rotationDegrees) || (this.pixelWidthHeightRatio != ((Format)paramObject).pixelWidthHeightRatio) || (this.stereoMode != ((Format)paramObject).stereoMode) || (this.channelCount != ((Format)paramObject).channelCount) || (this.sampleRate != ((Format)paramObject).sampleRate) || (this.pcmEncoding != ((Format)paramObject).pcmEncoding) || (this.encoderDelay != ((Format)paramObject).encoderDelay) || (this.encoderPadding != ((Format)paramObject).encoderPadding) || (this.subsampleOffsetUs != ((Format)paramObject).subsampleOffsetUs) || (this.selectionFlags != ((Format)paramObject).selectionFlags) || (!Util.areEqual(this.id, ((Format)paramObject).id)) || (!Util.areEqual(this.language, ((Format)paramObject).language)) || (this.accessibilityChannel != ((Format)paramObject).accessibilityChannel) || (!Util.areEqual(this.containerMimeType, ((Format)paramObject).containerMimeType)) || (!Util.areEqual(this.sampleMimeType, ((Format)paramObject).sampleMimeType)) || (!Util.areEqual(this.codecs, ((Format)paramObject).codecs)) || (!Util.areEqual(this.drmInitData, ((Format)paramObject).drmInitData)) || (!Util.areEqual(this.metadata, ((Format)paramObject).metadata)) || (!Util.areEqual(this.colorInfo, ((Format)paramObject).colorInfo)) || (!Arrays.equals(this.projectionData, ((Format)paramObject).projectionData)) || (this.initializationData.size() != ((Format)paramObject).initializationData.size()))
        {
          bool = false;
        }
        else
        {
          for (int i = 0;; i++)
          {
            if (i >= this.initializationData.size()) {
              break label423;
            }
            if (!Arrays.equals((byte[])this.initializationData.get(i), (byte[])((Format)paramObject).initializationData.get(i)))
            {
              bool = false;
              break;
            }
          }
          label423:
          bool = true;
        }
      }
    }
  }
  
  @SuppressLint({"InlinedApi"})
  @TargetApi(16)
  public final MediaFormat getFrameworkMediaFormatV16()
  {
    MediaFormat localMediaFormat = new MediaFormat();
    localMediaFormat.setString("mime", this.sampleMimeType);
    maybeSetStringV16(localMediaFormat, "language", this.language);
    maybeSetIntegerV16(localMediaFormat, "max-input-size", this.maxInputSize);
    maybeSetIntegerV16(localMediaFormat, "width", this.width);
    maybeSetIntegerV16(localMediaFormat, "height", this.height);
    maybeSetFloatV16(localMediaFormat, "frame-rate", this.frameRate);
    maybeSetIntegerV16(localMediaFormat, "rotation-degrees", this.rotationDegrees);
    maybeSetIntegerV16(localMediaFormat, "channel-count", this.channelCount);
    maybeSetIntegerV16(localMediaFormat, "sample-rate", this.sampleRate);
    for (int i = 0; i < this.initializationData.size(); i++) {
      localMediaFormat.setByteBuffer("csd-" + i, ByteBuffer.wrap((byte[])this.initializationData.get(i)));
    }
    maybeSetColorInfoV24(localMediaFormat, this.colorInfo);
    return localMediaFormat;
  }
  
  public int getPixelCount()
  {
    int i = -1;
    int j = i;
    if (this.width != -1) {
      if (this.height != -1) {
        break label24;
      }
    }
    label24:
    for (j = i;; j = this.width * this.height) {
      return j;
    }
  }
  
  public int hashCode()
  {
    int i = 0;
    int j;
    int k;
    label27:
    int m;
    label37:
    int n;
    label47:
    int i1;
    int i2;
    int i3;
    int i4;
    int i5;
    int i6;
    label87:
    int i7;
    int i8;
    if (this.hashCode == 0)
    {
      if (this.id != null) {
        break label194;
      }
      j = 0;
      if (this.containerMimeType != null) {
        break label205;
      }
      k = 0;
      if (this.sampleMimeType != null) {
        break label216;
      }
      m = 0;
      if (this.codecs != null) {
        break label228;
      }
      n = 0;
      i1 = this.bitrate;
      i2 = this.width;
      i3 = this.height;
      i4 = this.channelCount;
      i5 = this.sampleRate;
      if (this.language != null) {
        break label240;
      }
      i6 = 0;
      i7 = this.accessibilityChannel;
      if (this.drmInitData != null) {
        break label252;
      }
      i8 = 0;
      label103:
      if (this.metadata != null) {
        break label264;
      }
    }
    for (;;)
    {
      this.hashCode = (((((((((((((j + 527) * 31 + k) * 31 + m) * 31 + n) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + i5) * 31 + i6) * 31 + i7) * 31 + i8) * 31 + i);
      return this.hashCode;
      label194:
      j = this.id.hashCode();
      break;
      label205:
      k = this.containerMimeType.hashCode();
      break label27;
      label216:
      m = this.sampleMimeType.hashCode();
      break label37;
      label228:
      n = this.codecs.hashCode();
      break label47;
      label240:
      i6 = this.language.hashCode();
      break label87;
      label252:
      i8 = this.drmInitData.hashCode();
      break label103;
      label264:
      i = this.metadata.hashCode();
    }
  }
  
  public String toString()
  {
    return "Format(" + this.id + ", " + this.containerMimeType + ", " + this.sampleMimeType + ", " + this.bitrate + ", " + this.language + ", [" + this.width + ", " + this.height + ", " + this.frameRate + "], [" + this.channelCount + ", " + this.sampleRate + "])";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.id);
    paramParcel.writeString(this.containerMimeType);
    paramParcel.writeString(this.sampleMimeType);
    paramParcel.writeString(this.codecs);
    paramParcel.writeInt(this.bitrate);
    paramParcel.writeInt(this.maxInputSize);
    paramParcel.writeInt(this.width);
    paramParcel.writeInt(this.height);
    paramParcel.writeFloat(this.frameRate);
    paramParcel.writeInt(this.rotationDegrees);
    paramParcel.writeFloat(this.pixelWidthHeightRatio);
    if (this.projectionData != null) {}
    for (int i = 1;; i = 0)
    {
      paramParcel.writeInt(i);
      if (this.projectionData != null) {
        paramParcel.writeByteArray(this.projectionData);
      }
      paramParcel.writeInt(this.stereoMode);
      paramParcel.writeParcelable(this.colorInfo, paramInt);
      paramParcel.writeInt(this.channelCount);
      paramParcel.writeInt(this.sampleRate);
      paramParcel.writeInt(this.pcmEncoding);
      paramParcel.writeInt(this.encoderDelay);
      paramParcel.writeInt(this.encoderPadding);
      paramParcel.writeInt(this.selectionFlags);
      paramParcel.writeString(this.language);
      paramParcel.writeInt(this.accessibilityChannel);
      paramParcel.writeLong(this.subsampleOffsetUs);
      i = this.initializationData.size();
      paramParcel.writeInt(i);
      for (paramInt = 0; paramInt < i; paramInt++) {
        paramParcel.writeByteArray((byte[])this.initializationData.get(paramInt));
      }
    }
    paramParcel.writeParcelable(this.drmInitData, 0);
    paramParcel.writeParcelable(this.metadata, 0);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer2/Format.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */