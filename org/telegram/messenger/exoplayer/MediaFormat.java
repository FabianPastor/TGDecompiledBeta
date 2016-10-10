package org.telegram.messenger.exoplayer;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

public final class MediaFormat
  implements Parcelable
{
  public static final Parcelable.Creator<MediaFormat> CREATOR = new Parcelable.Creator()
  {
    public MediaFormat createFromParcel(Parcel paramAnonymousParcel)
    {
      return new MediaFormat(paramAnonymousParcel);
    }
    
    public MediaFormat[] newArray(int paramAnonymousInt)
    {
      return new MediaFormat[paramAnonymousInt];
    }
  };
  public static final int NO_VALUE = -1;
  public static final long OFFSET_SAMPLE_RELATIVE = Long.MAX_VALUE;
  public final boolean adaptive;
  public final int bitrate;
  public final int channelCount;
  public final long durationUs;
  public final int encoderDelay;
  public final int encoderPadding;
  private android.media.MediaFormat frameworkMediaFormat;
  private int hashCode;
  public final int height;
  public final List<byte[]> initializationData;
  public final String language;
  public final int maxHeight;
  public final int maxInputSize;
  public final int maxWidth;
  public final String mimeType;
  public final int pcmEncoding;
  public final float pixelWidthHeightRatio;
  public final int rotationDegrees;
  public final int sampleRate;
  public final long subsampleOffsetUs;
  public final String trackId;
  public final int width;
  
  MediaFormat(Parcel paramParcel)
  {
    this.trackId = paramParcel.readString();
    this.mimeType = paramParcel.readString();
    this.bitrate = paramParcel.readInt();
    this.maxInputSize = paramParcel.readInt();
    this.durationUs = paramParcel.readLong();
    this.width = paramParcel.readInt();
    this.height = paramParcel.readInt();
    this.rotationDegrees = paramParcel.readInt();
    this.pixelWidthHeightRatio = paramParcel.readFloat();
    this.channelCount = paramParcel.readInt();
    this.sampleRate = paramParcel.readInt();
    this.language = paramParcel.readString();
    this.subsampleOffsetUs = paramParcel.readLong();
    this.initializationData = new ArrayList();
    paramParcel.readList(this.initializationData, null);
    if (paramParcel.readInt() == 1) {}
    for (;;)
    {
      this.adaptive = bool;
      this.maxWidth = paramParcel.readInt();
      this.maxHeight = paramParcel.readInt();
      this.pcmEncoding = paramParcel.readInt();
      this.encoderDelay = paramParcel.readInt();
      this.encoderPadding = paramParcel.readInt();
      return;
      bool = false;
    }
  }
  
  MediaFormat(String paramString1, String paramString2, int paramInt1, int paramInt2, long paramLong1, int paramInt3, int paramInt4, int paramInt5, float paramFloat, int paramInt6, int paramInt7, String paramString3, long paramLong2, List<byte[]> paramList, boolean paramBoolean, int paramInt8, int paramInt9, int paramInt10, int paramInt11, int paramInt12)
  {
    this.trackId = paramString1;
    this.mimeType = Assertions.checkNotEmpty(paramString2);
    this.bitrate = paramInt1;
    this.maxInputSize = paramInt2;
    this.durationUs = paramLong1;
    this.width = paramInt3;
    this.height = paramInt4;
    this.rotationDegrees = paramInt5;
    this.pixelWidthHeightRatio = paramFloat;
    this.channelCount = paramInt6;
    this.sampleRate = paramInt7;
    this.language = paramString3;
    this.subsampleOffsetUs = paramLong2;
    paramString1 = paramList;
    if (paramList == null) {
      paramString1 = Collections.emptyList();
    }
    this.initializationData = paramString1;
    this.adaptive = paramBoolean;
    this.maxWidth = paramInt8;
    this.maxHeight = paramInt9;
    this.pcmEncoding = paramInt10;
    this.encoderDelay = paramInt11;
    this.encoderPadding = paramInt12;
  }
  
  public static MediaFormat createAudioFormat(String paramString1, String paramString2, int paramInt1, int paramInt2, long paramLong, int paramInt3, int paramInt4, List<byte[]> paramList, String paramString3)
  {
    return createAudioFormat(paramString1, paramString2, paramInt1, paramInt2, paramLong, paramInt3, paramInt4, paramList, paramString3, -1);
  }
  
  public static MediaFormat createAudioFormat(String paramString1, String paramString2, int paramInt1, int paramInt2, long paramLong, int paramInt3, int paramInt4, List<byte[]> paramList, String paramString3, int paramInt5)
  {
    return new MediaFormat(paramString1, paramString2, paramInt1, paramInt2, paramLong, -1, -1, -1, -1.0F, paramInt3, paramInt4, paramString3, Long.MAX_VALUE, paramList, false, -1, -1, paramInt5, -1, -1);
  }
  
  public static MediaFormat createFormatForMimeType(String paramString1, String paramString2, int paramInt, long paramLong)
  {
    return new MediaFormat(paramString1, paramString2, paramInt, -1, paramLong, -1, -1, -1, -1.0F, -1, -1, null, Long.MAX_VALUE, null, false, -1, -1, -1, -1, -1);
  }
  
  public static MediaFormat createId3Format()
  {
    return createFormatForMimeType(null, "application/id3", -1, -1L);
  }
  
  public static MediaFormat createImageFormat(String paramString1, String paramString2, int paramInt, long paramLong, List<byte[]> paramList, String paramString3)
  {
    return new MediaFormat(paramString1, paramString2, paramInt, -1, paramLong, -1, -1, -1, -1.0F, -1, -1, paramString3, Long.MAX_VALUE, paramList, false, -1, -1, -1, -1, -1);
  }
  
  public static MediaFormat createTextFormat(String paramString1, String paramString2, int paramInt, long paramLong, String paramString3)
  {
    return createTextFormat(paramString1, paramString2, paramInt, paramLong, paramString3, Long.MAX_VALUE);
  }
  
  public static MediaFormat createTextFormat(String paramString1, String paramString2, int paramInt, long paramLong1, String paramString3, long paramLong2)
  {
    return new MediaFormat(paramString1, paramString2, paramInt, -1, paramLong1, -1, -1, -1, -1.0F, -1, -1, paramString3, paramLong2, null, false, -1, -1, -1, -1, -1);
  }
  
  public static MediaFormat createVideoFormat(String paramString1, String paramString2, int paramInt1, int paramInt2, long paramLong, int paramInt3, int paramInt4, List<byte[]> paramList)
  {
    return createVideoFormat(paramString1, paramString2, paramInt1, paramInt2, paramLong, paramInt3, paramInt4, paramList, -1, -1.0F);
  }
  
  public static MediaFormat createVideoFormat(String paramString1, String paramString2, int paramInt1, int paramInt2, long paramLong, int paramInt3, int paramInt4, List<byte[]> paramList, int paramInt5, float paramFloat)
  {
    return new MediaFormat(paramString1, paramString2, paramInt1, paramInt2, paramLong, paramInt3, paramInt4, paramInt5, paramFloat, -1, -1, null, Long.MAX_VALUE, paramList, false, -1, -1, -1, -1, -1);
  }
  
  @TargetApi(16)
  private static final void maybeSetIntegerV16(android.media.MediaFormat paramMediaFormat, String paramString, int paramInt)
  {
    if (paramInt != -1) {
      paramMediaFormat.setInteger(paramString, paramInt);
    }
  }
  
  @TargetApi(16)
  private static final void maybeSetStringV16(android.media.MediaFormat paramMediaFormat, String paramString1, String paramString2)
  {
    if (paramString2 != null) {
      paramMediaFormat.setString(paramString1, paramString2);
    }
  }
  
  public MediaFormat copyAsAdaptive(String paramString)
  {
    return new MediaFormat(paramString, this.mimeType, -1, -1, this.durationUs, -1, -1, -1, -1.0F, -1, -1, null, Long.MAX_VALUE, null, true, this.maxWidth, this.maxHeight, -1, -1, -1);
  }
  
  public MediaFormat copyWithDurationUs(long paramLong)
  {
    return new MediaFormat(this.trackId, this.mimeType, this.bitrate, this.maxInputSize, paramLong, this.width, this.height, this.rotationDegrees, this.pixelWidthHeightRatio, this.channelCount, this.sampleRate, this.language, this.subsampleOffsetUs, this.initializationData, this.adaptive, this.maxWidth, this.maxHeight, this.pcmEncoding, this.encoderDelay, this.encoderPadding);
  }
  
  public MediaFormat copyWithFixedTrackInfo(String paramString1, int paramInt1, int paramInt2, int paramInt3, String paramString2)
  {
    return new MediaFormat(paramString1, this.mimeType, paramInt1, this.maxInputSize, this.durationUs, paramInt2, paramInt3, this.rotationDegrees, this.pixelWidthHeightRatio, this.channelCount, this.sampleRate, paramString2, this.subsampleOffsetUs, this.initializationData, this.adaptive, -1, -1, this.pcmEncoding, this.encoderDelay, this.encoderPadding);
  }
  
  public MediaFormat copyWithGaplessInfo(int paramInt1, int paramInt2)
  {
    return new MediaFormat(this.trackId, this.mimeType, this.bitrate, this.maxInputSize, this.durationUs, this.width, this.height, this.rotationDegrees, this.pixelWidthHeightRatio, this.channelCount, this.sampleRate, this.language, this.subsampleOffsetUs, this.initializationData, this.adaptive, this.maxWidth, this.maxHeight, this.pcmEncoding, paramInt1, paramInt2);
  }
  
  public MediaFormat copyWithLanguage(String paramString)
  {
    return new MediaFormat(this.trackId, this.mimeType, this.bitrate, this.maxInputSize, this.durationUs, this.width, this.height, this.rotationDegrees, this.pixelWidthHeightRatio, this.channelCount, this.sampleRate, paramString, this.subsampleOffsetUs, this.initializationData, this.adaptive, this.maxWidth, this.maxHeight, this.pcmEncoding, this.encoderDelay, this.encoderPadding);
  }
  
  public MediaFormat copyWithMaxInputSize(int paramInt)
  {
    return new MediaFormat(this.trackId, this.mimeType, this.bitrate, paramInt, this.durationUs, this.width, this.height, this.rotationDegrees, this.pixelWidthHeightRatio, this.channelCount, this.sampleRate, this.language, this.subsampleOffsetUs, this.initializationData, this.adaptive, this.maxWidth, this.maxHeight, this.pcmEncoding, this.encoderDelay, this.encoderPadding);
  }
  
  public MediaFormat copyWithMaxVideoDimensions(int paramInt1, int paramInt2)
  {
    return new MediaFormat(this.trackId, this.mimeType, this.bitrate, this.maxInputSize, this.durationUs, this.width, this.height, this.rotationDegrees, this.pixelWidthHeightRatio, this.channelCount, this.sampleRate, this.language, this.subsampleOffsetUs, this.initializationData, this.adaptive, paramInt1, paramInt2, this.pcmEncoding, this.encoderDelay, this.encoderPadding);
  }
  
  public MediaFormat copyWithSubsampleOffsetUs(long paramLong)
  {
    return new MediaFormat(this.trackId, this.mimeType, this.bitrate, this.maxInputSize, this.durationUs, this.width, this.height, this.rotationDegrees, this.pixelWidthHeightRatio, this.channelCount, this.sampleRate, this.language, paramLong, this.initializationData, this.adaptive, this.maxWidth, this.maxHeight, this.pcmEncoding, this.encoderDelay, this.encoderPadding);
  }
  
  public int describeContents()
  {
    return 0;
  }
  
  public boolean equals(Object paramObject)
  {
    if (this == paramObject) {
      return true;
    }
    if ((paramObject == null) || (getClass() != paramObject.getClass())) {
      return false;
    }
    paramObject = (MediaFormat)paramObject;
    if ((this.adaptive != ((MediaFormat)paramObject).adaptive) || (this.bitrate != ((MediaFormat)paramObject).bitrate) || (this.maxInputSize != ((MediaFormat)paramObject).maxInputSize) || (this.durationUs != ((MediaFormat)paramObject).durationUs) || (this.width != ((MediaFormat)paramObject).width) || (this.height != ((MediaFormat)paramObject).height) || (this.rotationDegrees != ((MediaFormat)paramObject).rotationDegrees) || (this.pixelWidthHeightRatio != ((MediaFormat)paramObject).pixelWidthHeightRatio) || (this.maxWidth != ((MediaFormat)paramObject).maxWidth) || (this.maxHeight != ((MediaFormat)paramObject).maxHeight) || (this.channelCount != ((MediaFormat)paramObject).channelCount) || (this.sampleRate != ((MediaFormat)paramObject).sampleRate) || (this.pcmEncoding != ((MediaFormat)paramObject).pcmEncoding) || (this.encoderDelay != ((MediaFormat)paramObject).encoderDelay) || (this.encoderPadding != ((MediaFormat)paramObject).encoderPadding) || (this.subsampleOffsetUs != ((MediaFormat)paramObject).subsampleOffsetUs) || (!Util.areEqual(this.trackId, ((MediaFormat)paramObject).trackId)) || (!Util.areEqual(this.language, ((MediaFormat)paramObject).language)) || (!Util.areEqual(this.mimeType, ((MediaFormat)paramObject).mimeType)) || (this.initializationData.size() != ((MediaFormat)paramObject).initializationData.size())) {
      return false;
    }
    int i = 0;
    while (i < this.initializationData.size())
    {
      if (!Arrays.equals((byte[])this.initializationData.get(i), (byte[])((MediaFormat)paramObject).initializationData.get(i))) {
        return false;
      }
      i += 1;
    }
    return true;
  }
  
  @SuppressLint({"InlinedApi"})
  @TargetApi(16)
  public final android.media.MediaFormat getFrameworkMediaFormatV16()
  {
    if (this.frameworkMediaFormat == null)
    {
      android.media.MediaFormat localMediaFormat = new android.media.MediaFormat();
      localMediaFormat.setString("mime", this.mimeType);
      maybeSetStringV16(localMediaFormat, "language", this.language);
      maybeSetIntegerV16(localMediaFormat, "max-input-size", this.maxInputSize);
      maybeSetIntegerV16(localMediaFormat, "width", this.width);
      maybeSetIntegerV16(localMediaFormat, "height", this.height);
      maybeSetIntegerV16(localMediaFormat, "rotation-degrees", this.rotationDegrees);
      maybeSetIntegerV16(localMediaFormat, "max-width", this.maxWidth);
      maybeSetIntegerV16(localMediaFormat, "max-height", this.maxHeight);
      maybeSetIntegerV16(localMediaFormat, "channel-count", this.channelCount);
      maybeSetIntegerV16(localMediaFormat, "sample-rate", this.sampleRate);
      maybeSetIntegerV16(localMediaFormat, "encoder-delay", this.encoderDelay);
      maybeSetIntegerV16(localMediaFormat, "encoder-padding", this.encoderPadding);
      int i = 0;
      while (i < this.initializationData.size())
      {
        localMediaFormat.setByteBuffer("csd-" + i, ByteBuffer.wrap((byte[])this.initializationData.get(i)));
        i += 1;
      }
      if (this.durationUs != -1L) {
        localMediaFormat.setLong("durationUs", this.durationUs);
      }
      this.frameworkMediaFormat = localMediaFormat;
    }
    return this.frameworkMediaFormat;
  }
  
  public int hashCode()
  {
    int m = 0;
    if (this.hashCode == 0)
    {
      int i;
      int j;
      label28:
      int n;
      int i1;
      int i2;
      int i3;
      int i4;
      int i5;
      int i6;
      int k;
      label85:
      int i7;
      int i8;
      int i9;
      int i10;
      int i11;
      int i12;
      int i13;
      if (this.trackId == null)
      {
        i = 0;
        if (this.mimeType != null) {
          break label304;
        }
        j = 0;
        n = this.bitrate;
        i1 = this.maxInputSize;
        i2 = this.width;
        i3 = this.height;
        i4 = this.rotationDegrees;
        i5 = Float.floatToRawIntBits(this.pixelWidthHeightRatio);
        i6 = (int)this.durationUs;
        if (!this.adaptive) {
          break label315;
        }
        k = 1231;
        i7 = this.maxWidth;
        i8 = this.maxHeight;
        i9 = this.channelCount;
        i10 = this.sampleRate;
        i11 = this.pcmEncoding;
        i12 = this.encoderDelay;
        i13 = this.encoderPadding;
        if (this.language != null) {
          break label322;
        }
      }
      for (;;)
      {
        j = ((((((((((((((((((i + 527) * 31 + j) * 31 + n) * 31 + i1) * 31 + i2) * 31 + i3) * 31 + i4) * 31 + i5) * 31 + i6) * 31 + k) * 31 + i7) * 31 + i8) * 31 + i9) * 31 + i10) * 31 + i11) * 31 + i12) * 31 + i13) * 31 + m) * 31 + (int)this.subsampleOffsetUs;
        i = 0;
        while (i < this.initializationData.size())
        {
          j = j * 31 + Arrays.hashCode((byte[])this.initializationData.get(i));
          i += 1;
        }
        i = this.trackId.hashCode();
        break;
        label304:
        j = this.mimeType.hashCode();
        break label28;
        label315:
        k = 1237;
        break label85;
        label322:
        m = this.language.hashCode();
      }
      this.hashCode = j;
    }
    return this.hashCode;
  }
  
  @Deprecated
  @TargetApi(16)
  final void setFrameworkFormatV16(android.media.MediaFormat paramMediaFormat)
  {
    this.frameworkMediaFormat = paramMediaFormat;
  }
  
  public String toString()
  {
    return "MediaFormat(" + this.trackId + ", " + this.mimeType + ", " + this.bitrate + ", " + this.maxInputSize + ", " + this.width + ", " + this.height + ", " + this.rotationDegrees + ", " + this.pixelWidthHeightRatio + ", " + this.channelCount + ", " + this.sampleRate + ", " + this.language + ", " + this.durationUs + ", " + this.adaptive + ", " + this.maxWidth + ", " + this.maxHeight + ", " + this.pcmEncoding + ", " + this.encoderDelay + ", " + this.encoderPadding + ")";
  }
  
  public void writeToParcel(Parcel paramParcel, int paramInt)
  {
    paramParcel.writeString(this.trackId);
    paramParcel.writeString(this.mimeType);
    paramParcel.writeInt(this.bitrate);
    paramParcel.writeInt(this.maxInputSize);
    paramParcel.writeLong(this.durationUs);
    paramParcel.writeInt(this.width);
    paramParcel.writeInt(this.height);
    paramParcel.writeInt(this.rotationDegrees);
    paramParcel.writeFloat(this.pixelWidthHeightRatio);
    paramParcel.writeInt(this.channelCount);
    paramParcel.writeInt(this.sampleRate);
    paramParcel.writeString(this.language);
    paramParcel.writeLong(this.subsampleOffsetUs);
    paramParcel.writeList(this.initializationData);
    if (this.adaptive) {}
    for (paramInt = 1;; paramInt = 0)
    {
      paramParcel.writeInt(paramInt);
      paramParcel.writeInt(this.maxWidth);
      paramParcel.writeInt(this.maxHeight);
      paramParcel.writeInt(this.pcmEncoding);
      paramParcel.writeInt(this.encoderDelay);
      paramParcel.writeInt(this.encoderPadding);
      return;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/exoplayer/MediaFormat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */