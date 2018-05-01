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

public final class Format implements Parcelable {
    public static final Creator<Format> CREATOR = new C05451();
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

    /* renamed from: org.telegram.messenger.exoplayer2.Format$1 */
    static class C05451 implements Creator<Format> {
        C05451() {
        }

        public Format createFromParcel(Parcel parcel) {
            return new Format(parcel);
        }

        public Format[] newArray(int i) {
            return new Format[i];
        }
    }

    public int describeContents() {
        return 0;
    }

    public static Format createVideoContainerFormat(String str, String str2, String str3, String str4, int i, int i2, int i3, float f, List<byte[]> list, int i4) {
        return new Format(str, str2, str3, str4, i, -1, i2, i3, f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, i4, null, -1, Long.MAX_VALUE, list, null, null);
    }

    public static Format createVideoSampleFormat(String str, String str2, String str3, int i, int i2, int i3, int i4, float f, List<byte[]> list, DrmInitData drmInitData) {
        return createVideoSampleFormat(str, str2, str3, i, i2, i3, i4, f, list, -1, -1.0f, drmInitData);
    }

    public static Format createVideoSampleFormat(String str, String str2, String str3, int i, int i2, int i3, int i4, float f, List<byte[]> list, int i5, float f2, DrmInitData drmInitData) {
        return createVideoSampleFormat(str, str2, str3, i, i2, i3, i4, f, list, i5, f2, null, -1, null, drmInitData);
    }

    public static Format createVideoSampleFormat(String str, String str2, String str3, int i, int i2, int i3, int i4, float f, List<byte[]> list, int i5, float f2, byte[] bArr, int i6, ColorInfo colorInfo, DrmInitData drmInitData) {
        return new Format(str, null, str2, str3, i, i2, i3, i4, f, i5, f2, bArr, i6, colorInfo, -1, -1, -1, -1, -1, 0, null, -1, Long.MAX_VALUE, list, drmInitData, null);
    }

    public static Format createAudioContainerFormat(String str, String str2, String str3, String str4, int i, int i2, int i3, List<byte[]> list, int i4, String str5) {
        return new Format(str, str2, str3, str4, i, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, i2, i3, -1, -1, -1, i4, str5, -1, Long.MAX_VALUE, list, null, null);
    }

    public static Format createAudioSampleFormat(String str, String str2, String str3, int i, int i2, int i3, int i4, List<byte[]> list, DrmInitData drmInitData, int i5, String str4) {
        return createAudioSampleFormat(str, str2, str3, i, i2, i3, i4, -1, list, drmInitData, i5, str4);
    }

    public static Format createAudioSampleFormat(String str, String str2, String str3, int i, int i2, int i3, int i4, int i5, List<byte[]> list, DrmInitData drmInitData, int i6, String str4) {
        return createAudioSampleFormat(str, str2, str3, i, i2, i3, i4, i5, -1, -1, list, drmInitData, i6, str4, null);
    }

    public static Format createAudioSampleFormat(String str, String str2, String str3, int i, int i2, int i3, int i4, int i5, int i6, int i7, List<byte[]> list, DrmInitData drmInitData, int i8, String str4, Metadata metadata) {
        return new Format(str, null, str2, str3, i, i2, -1, -1, -1.0f, -1, -1.0f, null, -1, null, i3, i4, i5, i6, i7, i8, str4, -1, Long.MAX_VALUE, list, drmInitData, metadata);
    }

    public static Format createTextContainerFormat(String str, String str2, String str3, String str4, int i, int i2, String str5) {
        return createTextContainerFormat(str, str2, str3, str4, i, i2, str5, -1);
    }

    public static Format createTextContainerFormat(String str, String str2, String str3, String str4, int i, int i2, String str5, int i3) {
        return new Format(str, str2, str3, str4, i, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, i2, str5, i3, Long.MAX_VALUE, null, null, null);
    }

    public static Format createTextSampleFormat(String str, String str2, int i, String str3) {
        return createTextSampleFormat(str, str2, i, str3, null);
    }

    public static Format createTextSampleFormat(String str, String str2, int i, String str3, DrmInitData drmInitData) {
        return createTextSampleFormat(str, str2, null, -1, i, str3, -1, drmInitData, Long.MAX_VALUE, Collections.emptyList());
    }

    public static Format createTextSampleFormat(String str, String str2, String str3, int i, int i2, String str4, int i3, DrmInitData drmInitData) {
        return createTextSampleFormat(str, str2, str3, i, i2, str4, i3, drmInitData, Long.MAX_VALUE, Collections.emptyList());
    }

    public static Format createTextSampleFormat(String str, String str2, String str3, int i, int i2, String str4, DrmInitData drmInitData, long j) {
        return createTextSampleFormat(str, str2, str3, i, i2, str4, -1, drmInitData, j, Collections.emptyList());
    }

    public static Format createTextSampleFormat(String str, String str2, String str3, int i, int i2, String str4, int i3, DrmInitData drmInitData, long j, List<byte[]> list) {
        return new Format(str, null, str2, str3, i, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, i2, str4, i3, j, list, drmInitData, null);
    }

    public static Format createImageSampleFormat(String str, String str2, String str3, int i, int i2, List<byte[]> list, String str4, DrmInitData drmInitData) {
        return new Format(str, null, str2, str3, i, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, i2, str4, -1, Long.MAX_VALUE, list, drmInitData, null);
    }

    public static Format createContainerFormat(String str, String str2, String str3, String str4, int i, int i2, String str5) {
        return new Format(str, str2, str3, str4, i, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, i2, str5, -1, Long.MAX_VALUE, null, null, null);
    }

    public static Format createSampleFormat(String str, String str2, long j) {
        return new Format(str, null, str2, null, -1, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, 0, null, -1, j, null, null, null);
    }

    public static Format createSampleFormat(String str, String str2, String str3, int i, DrmInitData drmInitData) {
        return new Format(str, null, str2, str3, i, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, 0, null, -1, Long.MAX_VALUE, null, drmInitData, null);
    }

    Format(String str, String str2, String str3, String str4, int i, int i2, int i3, int i4, float f, int i5, float f2, byte[] bArr, int i6, ColorInfo colorInfo, int i7, int i8, int i9, int i10, int i11, int i12, String str5, int i13, long j, List<byte[]> list, DrmInitData drmInitData, Metadata metadata) {
        this.id = str;
        this.containerMimeType = str2;
        this.sampleMimeType = str3;
        this.codecs = str4;
        this.bitrate = i;
        this.maxInputSize = i2;
        this.width = i3;
        this.height = i4;
        this.frameRate = f;
        this.rotationDegrees = i5;
        this.pixelWidthHeightRatio = f2;
        this.projectionData = bArr;
        this.stereoMode = i6;
        this.colorInfo = colorInfo;
        this.channelCount = i7;
        this.sampleRate = i8;
        this.pcmEncoding = i9;
        this.encoderDelay = i10;
        this.encoderPadding = i11;
        this.selectionFlags = i12;
        this.language = str5;
        this.accessibilityChannel = i13;
        this.subsampleOffsetUs = j;
        r0.initializationData = list == null ? Collections.emptyList() : list;
        r0.drmInitData = drmInitData;
        r0.metadata = metadata;
    }

    Format(Parcel parcel) {
        this.id = parcel.readString();
        this.containerMimeType = parcel.readString();
        this.sampleMimeType = parcel.readString();
        this.codecs = parcel.readString();
        this.bitrate = parcel.readInt();
        this.maxInputSize = parcel.readInt();
        this.width = parcel.readInt();
        this.height = parcel.readInt();
        this.frameRate = parcel.readFloat();
        this.rotationDegrees = parcel.readInt();
        this.pixelWidthHeightRatio = parcel.readFloat();
        int i = 0;
        this.projectionData = (parcel.readInt() != 0 ? 1 : 0) != 0 ? parcel.createByteArray() : null;
        this.stereoMode = parcel.readInt();
        this.colorInfo = (ColorInfo) parcel.readParcelable(ColorInfo.class.getClassLoader());
        this.channelCount = parcel.readInt();
        this.sampleRate = parcel.readInt();
        this.pcmEncoding = parcel.readInt();
        this.encoderDelay = parcel.readInt();
        this.encoderPadding = parcel.readInt();
        this.selectionFlags = parcel.readInt();
        this.language = parcel.readString();
        this.accessibilityChannel = parcel.readInt();
        this.subsampleOffsetUs = parcel.readLong();
        int readInt = parcel.readInt();
        this.initializationData = new ArrayList(readInt);
        while (i < readInt) {
            this.initializationData.add(parcel.createByteArray());
            i++;
        }
        this.drmInitData = (DrmInitData) parcel.readParcelable(DrmInitData.class.getClassLoader());
        this.metadata = (Metadata) parcel.readParcelable(Metadata.class.getClassLoader());
    }

    public Format copyWithMaxInputSize(int i) {
        String str = this.id;
        String str2 = this.containerMimeType;
        String str3 = this.sampleMimeType;
        String str4 = this.codecs;
        int i2 = this.bitrate;
        int i3 = this.width;
        int i4 = this.height;
        float f = this.frameRate;
        int i5 = this.rotationDegrees;
        float f2 = this.pixelWidthHeightRatio;
        byte[] bArr = this.projectionData;
        int i6 = this.stereoMode;
        ColorInfo colorInfo = this.colorInfo;
        int i7 = this.channelCount;
        int i8 = this.sampleRate;
        ColorInfo colorInfo2 = colorInfo;
        int i9 = this.pcmEncoding;
        int i10 = this.encoderDelay;
        int i11 = this.encoderPadding;
        int i12 = this.selectionFlags;
        String str5 = this.language;
        int i13 = i6;
        int i14 = this.accessibilityChannel;
        long j = this.subsampleOffsetUs;
        List list = this.initializationData;
        List list2 = list;
        int i15 = i7;
        return new Format(str, str2, str3, str4, i2, i, i3, i4, f, i5, f2, bArr, i13, colorInfo2, i15, i8, i9, i10, i11, i12, str5, i14, j, list2, this.drmInitData, this.metadata);
    }

    public Format copyWithSubsampleOffsetUs(long j) {
        String str = this.id;
        String str2 = this.containerMimeType;
        String str3 = this.sampleMimeType;
        String str4 = this.codecs;
        int i = this.bitrate;
        int i2 = this.maxInputSize;
        int i3 = this.width;
        int i4 = this.height;
        float f = this.frameRate;
        int i5 = this.rotationDegrees;
        float f2 = this.pixelWidthHeightRatio;
        byte[] bArr = this.projectionData;
        int i6 = this.stereoMode;
        ColorInfo colorInfo = this.colorInfo;
        ColorInfo colorInfo2 = colorInfo;
        return new Format(str, str2, str3, str4, i, i2, i3, i4, f, i5, f2, bArr, i6, colorInfo2, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, j, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithContainerInfo(String str, String str2, String str3, int i, int i2, int i3, int i4, String str4) {
        String str5 = this.containerMimeType;
        int i5 = this.maxInputSize;
        float f = this.frameRate;
        int i6 = this.rotationDegrees;
        float f2 = this.pixelWidthHeightRatio;
        byte[] bArr = this.projectionData;
        int i7 = this.stereoMode;
        ColorInfo colorInfo = this.colorInfo;
        int i8 = this.channelCount;
        int i9 = this.sampleRate;
        int i10 = this.pcmEncoding;
        int i11 = this.encoderDelay;
        int i12 = this.encoderPadding;
        int i13 = this.accessibilityChannel;
        int i14 = i7;
        ColorInfo colorInfo2 = colorInfo;
        long j = this.subsampleOffsetUs;
        List list = this.initializationData;
        long j2 = j;
        DrmInitData drmInitData = this.drmInitData;
        int i15 = i13;
        int i16 = i12;
        int i17 = i11;
        int i18 = i10;
        int i19 = i9;
        int i20 = i8;
        DrmInitData drmInitData2 = drmInitData;
        return new Format(str, str5, str2, str3, i, i5, i2, i3, f, i6, f2, bArr, i14, colorInfo2, i20, i19, i18, i17, i16, i4, str4, i15, j2, list, drmInitData2, this.metadata);
    }

    public Format copyWithManifestFormatInfo(Format format) {
        Format format2 = format;
        if (this == format2) {
            return r0;
        }
        return new Format(format2.id, r0.containerMimeType, r0.sampleMimeType, r0.codecs == null ? format2.codecs : r0.codecs, r0.bitrate == -1 ? format2.bitrate : r0.bitrate, r0.maxInputSize, r0.width, r0.height, r0.frameRate == -1.0f ? format2.frameRate : r0.frameRate, r0.rotationDegrees, r0.pixelWidthHeightRatio, r0.projectionData, r0.stereoMode, r0.colorInfo, r0.channelCount, r0.sampleRate, r0.pcmEncoding, r0.encoderDelay, r0.encoderPadding, r0.selectionFlags | format2.selectionFlags, r0.language == null ? format2.language : r0.language, r0.accessibilityChannel, r0.subsampleOffsetUs, r0.initializationData, DrmInitData.createSessionCreationData(format2.drmInitData, r0.drmInitData), r0.metadata);
    }

    public Format copyWithGaplessInfo(int i, int i2) {
        String str = this.id;
        String str2 = this.containerMimeType;
        String str3 = this.sampleMimeType;
        String str4 = this.codecs;
        int i3 = this.bitrate;
        int i4 = this.maxInputSize;
        int i5 = this.width;
        int i6 = this.height;
        float f = this.frameRate;
        int i7 = this.rotationDegrees;
        float f2 = this.pixelWidthHeightRatio;
        byte[] bArr = this.projectionData;
        int i8 = this.stereoMode;
        ColorInfo colorInfo = this.colorInfo;
        int i9 = this.channelCount;
        ColorInfo colorInfo2 = colorInfo;
        int i10 = this.sampleRate;
        int i11 = this.pcmEncoding;
        int i12 = this.selectionFlags;
        String str5 = this.language;
        int i13 = i8;
        int i14 = this.accessibilityChannel;
        long j = this.subsampleOffsetUs;
        List list = this.initializationData;
        List list2 = list;
        return new Format(str, str2, str3, str4, i3, i4, i5, i6, f, i7, f2, bArr, i13, colorInfo2, i9, i10, i11, i, i2, i12, str5, i14, j, list2, this.drmInitData, this.metadata);
    }

    public Format copyWithDrmInitData(DrmInitData drmInitData) {
        String str = this.id;
        String str2 = this.containerMimeType;
        String str3 = this.sampleMimeType;
        String str4 = this.codecs;
        int i = this.bitrate;
        int i2 = this.maxInputSize;
        int i3 = this.width;
        int i4 = this.height;
        float f = this.frameRate;
        int i5 = this.rotationDegrees;
        float f2 = this.pixelWidthHeightRatio;
        byte[] bArr = this.projectionData;
        int i6 = this.stereoMode;
        ColorInfo colorInfo = this.colorInfo;
        int i7 = this.channelCount;
        ColorInfo colorInfo2 = colorInfo;
        int i8 = this.sampleRate;
        int i9 = this.pcmEncoding;
        int i10 = this.encoderDelay;
        int i11 = this.encoderPadding;
        int i12 = this.selectionFlags;
        String str5 = this.language;
        int i13 = i6;
        int i14 = this.accessibilityChannel;
        long j = this.subsampleOffsetUs;
        List list = this.initializationData;
        List list2 = list;
        return new Format(str, str2, str3, str4, i, i2, i3, i4, f, i5, f2, bArr, i13, colorInfo2, i7, i8, i9, i10, i11, i12, str5, i14, j, list2, drmInitData, this.metadata);
    }

    public Format copyWithMetadata(Metadata metadata) {
        String str = this.id;
        String str2 = this.containerMimeType;
        String str3 = this.sampleMimeType;
        String str4 = this.codecs;
        int i = this.bitrate;
        int i2 = this.maxInputSize;
        int i3 = this.width;
        int i4 = this.height;
        float f = this.frameRate;
        int i5 = this.rotationDegrees;
        float f2 = this.pixelWidthHeightRatio;
        byte[] bArr = this.projectionData;
        int i6 = this.stereoMode;
        ColorInfo colorInfo = this.colorInfo;
        int i7 = this.channelCount;
        ColorInfo colorInfo2 = colorInfo;
        int i8 = this.sampleRate;
        int i9 = this.pcmEncoding;
        int i10 = this.encoderDelay;
        int i11 = this.encoderPadding;
        int i12 = this.selectionFlags;
        String str5 = this.language;
        int i13 = i6;
        int i14 = this.accessibilityChannel;
        long j = this.subsampleOffsetUs;
        List list = this.initializationData;
        List list2 = list;
        return new Format(str, str2, str3, str4, i, i2, i3, i4, f, i5, f2, bArr, i13, colorInfo2, i7, i8, i9, i10, i11, i12, str5, i14, j, list2, this.drmInitData, metadata);
    }

    public Format copyWithRotationDegrees(int i) {
        String str = this.id;
        String str2 = this.containerMimeType;
        String str3 = this.sampleMimeType;
        String str4 = this.codecs;
        int i2 = this.bitrate;
        int i3 = this.maxInputSize;
        int i4 = this.width;
        int i5 = this.height;
        float f = this.frameRate;
        float f2 = this.pixelWidthHeightRatio;
        byte[] bArr = this.projectionData;
        int i6 = this.stereoMode;
        ColorInfo colorInfo = this.colorInfo;
        int i7 = this.channelCount;
        int i8 = this.sampleRate;
        ColorInfo colorInfo2 = colorInfo;
        int i9 = this.pcmEncoding;
        int i10 = this.encoderDelay;
        int i11 = this.encoderPadding;
        int i12 = this.selectionFlags;
        String str5 = this.language;
        int i13 = i6;
        int i14 = this.accessibilityChannel;
        long j = this.subsampleOffsetUs;
        List list = this.initializationData;
        List list2 = list;
        int i15 = i7;
        return new Format(str, str2, str3, str4, i2, i3, i4, i5, f, i, f2, bArr, i13, colorInfo2, i15, i8, i9, i10, i11, i12, str5, i14, j, list2, this.drmInitData, this.metadata);
    }

    public int getPixelCount() {
        if (this.width == -1) {
            return -1;
        }
        if (this.height == -1) {
            return -1;
        }
        return this.height * this.width;
    }

    @SuppressLint({"InlinedApi"})
    @TargetApi(16)
    public final MediaFormat getFrameworkMediaFormatV16() {
        MediaFormat mediaFormat = new MediaFormat();
        mediaFormat.setString("mime", this.sampleMimeType);
        maybeSetStringV16(mediaFormat, "language", this.language);
        maybeSetIntegerV16(mediaFormat, "max-input-size", this.maxInputSize);
        maybeSetIntegerV16(mediaFormat, "width", this.width);
        maybeSetIntegerV16(mediaFormat, "height", this.height);
        maybeSetFloatV16(mediaFormat, "frame-rate", this.frameRate);
        maybeSetIntegerV16(mediaFormat, "rotation-degrees", this.rotationDegrees);
        maybeSetIntegerV16(mediaFormat, "channel-count", this.channelCount);
        maybeSetIntegerV16(mediaFormat, "sample-rate", this.sampleRate);
        for (int i = 0; i < this.initializationData.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("csd-");
            stringBuilder.append(i);
            mediaFormat.setByteBuffer(stringBuilder.toString(), ByteBuffer.wrap((byte[]) this.initializationData.get(i)));
        }
        maybeSetColorInfoV24(mediaFormat, this.colorInfo);
        return mediaFormat;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Format(");
        stringBuilder.append(this.id);
        stringBuilder.append(", ");
        stringBuilder.append(this.containerMimeType);
        stringBuilder.append(", ");
        stringBuilder.append(this.sampleMimeType);
        stringBuilder.append(", ");
        stringBuilder.append(this.bitrate);
        stringBuilder.append(", ");
        stringBuilder.append(this.language);
        stringBuilder.append(", [");
        stringBuilder.append(this.width);
        stringBuilder.append(", ");
        stringBuilder.append(this.height);
        stringBuilder.append(", ");
        stringBuilder.append(this.frameRate);
        stringBuilder.append("], [");
        stringBuilder.append(this.channelCount);
        stringBuilder.append(", ");
        stringBuilder.append(this.sampleRate);
        stringBuilder.append("])");
        return stringBuilder.toString();
    }

    public int hashCode() {
        if (this.hashCode == 0) {
            int i = 0;
            int hashCode = 31 * (((((((((((((((((((((((527 + (this.id == null ? 0 : this.id.hashCode())) * 31) + (this.containerMimeType == null ? 0 : this.containerMimeType.hashCode())) * 31) + (this.sampleMimeType == null ? 0 : this.sampleMimeType.hashCode())) * 31) + (this.codecs == null ? 0 : this.codecs.hashCode())) * 31) + this.bitrate) * 31) + this.width) * 31) + this.height) * 31) + this.channelCount) * 31) + this.sampleRate) * 31) + (this.language == null ? 0 : this.language.hashCode())) * 31) + this.accessibilityChannel) * 31) + (this.drmInitData == null ? 0 : this.drmInitData.hashCode()));
            if (this.metadata != null) {
                i = this.metadata.hashCode();
            }
            this.hashCode = hashCode + i;
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                Format format = (Format) obj;
                if (this.bitrate == format.bitrate && this.maxInputSize == format.maxInputSize && this.width == format.width && this.height == format.height && this.frameRate == format.frameRate && this.rotationDegrees == format.rotationDegrees && this.pixelWidthHeightRatio == format.pixelWidthHeightRatio && this.stereoMode == format.stereoMode && this.channelCount == format.channelCount && this.sampleRate == format.sampleRate && this.pcmEncoding == format.pcmEncoding && this.encoderDelay == format.encoderDelay && this.encoderPadding == format.encoderPadding && this.subsampleOffsetUs == format.subsampleOffsetUs && this.selectionFlags == format.selectionFlags && Util.areEqual(this.id, format.id) && Util.areEqual(this.language, format.language) && this.accessibilityChannel == format.accessibilityChannel && Util.areEqual(this.containerMimeType, format.containerMimeType) && Util.areEqual(this.sampleMimeType, format.sampleMimeType) && Util.areEqual(this.codecs, format.codecs) && Util.areEqual(this.drmInitData, format.drmInitData) && Util.areEqual(this.metadata, format.metadata) && Util.areEqual(this.colorInfo, format.colorInfo) && Arrays.equals(this.projectionData, format.projectionData)) {
                    if (this.initializationData.size() == format.initializationData.size()) {
                        for (int i = 0; i < this.initializationData.size(); i++) {
                            if (!Arrays.equals((byte[]) this.initializationData.get(i), (byte[]) format.initializationData.get(i))) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        }
        return false;
    }

    @TargetApi(24)
    private static void maybeSetColorInfoV24(MediaFormat mediaFormat, ColorInfo colorInfo) {
        if (colorInfo != null) {
            maybeSetIntegerV16(mediaFormat, "color-transfer", colorInfo.colorTransfer);
            maybeSetIntegerV16(mediaFormat, "color-standard", colorInfo.colorSpace);
            maybeSetIntegerV16(mediaFormat, "color-range", colorInfo.colorRange);
            maybeSetByteBufferV16(mediaFormat, "hdr-static-info", colorInfo.hdrStaticInfo);
        }
    }

    @TargetApi(16)
    private static void maybeSetStringV16(MediaFormat mediaFormat, String str, String str2) {
        if (str2 != null) {
            mediaFormat.setString(str, str2);
        }
    }

    @TargetApi(16)
    private static void maybeSetIntegerV16(MediaFormat mediaFormat, String str, int i) {
        if (i != -1) {
            mediaFormat.setInteger(str, i);
        }
    }

    @TargetApi(16)
    private static void maybeSetFloatV16(MediaFormat mediaFormat, String str, float f) {
        if (f != -1.0f) {
            mediaFormat.setFloat(str, f);
        }
    }

    @TargetApi(16)
    private static void maybeSetByteBufferV16(MediaFormat mediaFormat, String str, byte[] bArr) {
        if (bArr != null) {
            mediaFormat.setByteBuffer(str, ByteBuffer.wrap(bArr));
        }
    }

    public static String toLogString(Format format) {
        if (format == null) {
            return "null";
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id=");
        stringBuilder.append(format.id);
        stringBuilder.append(", mimeType=");
        stringBuilder.append(format.sampleMimeType);
        if (format.bitrate != -1) {
            stringBuilder.append(", bitrate=");
            stringBuilder.append(format.bitrate);
        }
        if (!(format.width == -1 || format.height == -1)) {
            stringBuilder.append(", res=");
            stringBuilder.append(format.width);
            stringBuilder.append("x");
            stringBuilder.append(format.height);
        }
        if (format.frameRate != -1.0f) {
            stringBuilder.append(", fps=");
            stringBuilder.append(format.frameRate);
        }
        if (format.channelCount != -1) {
            stringBuilder.append(", channels=");
            stringBuilder.append(format.channelCount);
        }
        if (format.sampleRate != -1) {
            stringBuilder.append(", sample_rate=");
            stringBuilder.append(format.sampleRate);
        }
        if (format.language != null) {
            stringBuilder.append(", language=");
            stringBuilder.append(format.language);
        }
        return stringBuilder.toString();
    }

    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.containerMimeType);
        parcel.writeString(this.sampleMimeType);
        parcel.writeString(this.codecs);
        parcel.writeInt(this.bitrate);
        parcel.writeInt(this.maxInputSize);
        parcel.writeInt(this.width);
        parcel.writeInt(this.height);
        parcel.writeFloat(this.frameRate);
        parcel.writeInt(this.rotationDegrees);
        parcel.writeFloat(this.pixelWidthHeightRatio);
        parcel.writeInt(this.projectionData != null ? 1 : 0);
        if (this.projectionData != null) {
            parcel.writeByteArray(this.projectionData);
        }
        parcel.writeInt(this.stereoMode);
        parcel.writeParcelable(this.colorInfo, i);
        parcel.writeInt(this.channelCount);
        parcel.writeInt(this.sampleRate);
        parcel.writeInt(this.pcmEncoding);
        parcel.writeInt(this.encoderDelay);
        parcel.writeInt(this.encoderPadding);
        parcel.writeInt(this.selectionFlags);
        parcel.writeString(this.language);
        parcel.writeInt(this.accessibilityChannel);
        parcel.writeLong(this.subsampleOffsetUs);
        i = this.initializationData.size();
        parcel.writeInt(i);
        for (int i2 = 0; i2 < i; i2++) {
            parcel.writeByteArray((byte[]) this.initializationData.get(i2));
        }
        parcel.writeParcelable(this.drmInitData, 0);
        parcel.writeParcelable(this.metadata, 0);
    }
}
