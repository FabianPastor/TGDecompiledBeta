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

        public Format createFromParcel(Parcel in) {
            return new Format(in);
        }

        public Format[] newArray(int size) {
            return new Format[size];
        }
    }

    public static Format createVideoContainerFormat(String id, String containerMimeType, String sampleMimeType, String codecs, int bitrate, int width, int height, float frameRate, List<byte[]> initializationData, int selectionFlags) {
        return new Format(id, containerMimeType, sampleMimeType, codecs, bitrate, -1, width, height, frameRate, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, selectionFlags, null, -1, Long.MAX_VALUE, initializationData, null, null);
    }

    public static Format createVideoSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, int maxInputSize, int width, int height, float frameRate, List<byte[]> initializationData, DrmInitData drmInitData) {
        return createVideoSampleFormat(id, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, initializationData, -1, -1.0f, drmInitData);
    }

    public static Format createVideoSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, int maxInputSize, int width, int height, float frameRate, List<byte[]> initializationData, int rotationDegrees, float pixelWidthHeightRatio, DrmInitData drmInitData) {
        return createVideoSampleFormat(id, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, initializationData, rotationDegrees, pixelWidthHeightRatio, null, -1, null, drmInitData);
    }

    public static Format createVideoSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, int maxInputSize, int width, int height, float frameRate, List<byte[]> initializationData, int rotationDegrees, float pixelWidthHeightRatio, byte[] projectionData, int stereoMode, ColorInfo colorInfo, DrmInitData drmInitData) {
        return new Format(id, null, sampleMimeType, codecs, bitrate, maxInputSize, width, height, frameRate, rotationDegrees, pixelWidthHeightRatio, projectionData, stereoMode, colorInfo, -1, -1, -1, -1, -1, 0, null, -1, Long.MAX_VALUE, initializationData, drmInitData, null);
    }

    public static Format createAudioContainerFormat(String id, String containerMimeType, String sampleMimeType, String codecs, int bitrate, int channelCount, int sampleRate, List<byte[]> initializationData, int selectionFlags, String language) {
        return new Format(id, containerMimeType, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, channelCount, sampleRate, -1, -1, -1, selectionFlags, language, -1, Long.MAX_VALUE, initializationData, null, null);
    }

    public static Format createAudioSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, int maxInputSize, int channelCount, int sampleRate, List<byte[]> initializationData, DrmInitData drmInitData, int selectionFlags, String language) {
        return createAudioSampleFormat(id, sampleMimeType, codecs, bitrate, maxInputSize, channelCount, sampleRate, -1, initializationData, drmInitData, selectionFlags, language);
    }

    public static Format createAudioSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, int maxInputSize, int channelCount, int sampleRate, int pcmEncoding, List<byte[]> initializationData, DrmInitData drmInitData, int selectionFlags, String language) {
        return createAudioSampleFormat(id, sampleMimeType, codecs, bitrate, maxInputSize, channelCount, sampleRate, pcmEncoding, -1, -1, initializationData, drmInitData, selectionFlags, language, null);
    }

    public static Format createAudioSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, int maxInputSize, int channelCount, int sampleRate, int pcmEncoding, int encoderDelay, int encoderPadding, List<byte[]> initializationData, DrmInitData drmInitData, int selectionFlags, String language, Metadata metadata) {
        return new Format(id, null, sampleMimeType, codecs, bitrate, maxInputSize, -1, -1, -1.0f, -1, -1.0f, null, -1, null, channelCount, sampleRate, pcmEncoding, encoderDelay, encoderPadding, selectionFlags, language, -1, Long.MAX_VALUE, initializationData, drmInitData, metadata);
    }

    public static Format createTextContainerFormat(String id, String containerMimeType, String sampleMimeType, String codecs, int bitrate, int selectionFlags, String language) {
        return createTextContainerFormat(id, containerMimeType, sampleMimeType, codecs, bitrate, selectionFlags, language, -1);
    }

    public static Format createTextContainerFormat(String id, String containerMimeType, String sampleMimeType, String codecs, int bitrate, int selectionFlags, String language, int accessibilityChannel) {
        return new Format(id, containerMimeType, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, selectionFlags, language, accessibilityChannel, Long.MAX_VALUE, null, null, null);
    }

    public static Format createTextSampleFormat(String id, String sampleMimeType, int selectionFlags, String language) {
        return createTextSampleFormat(id, sampleMimeType, selectionFlags, language, null);
    }

    public static Format createTextSampleFormat(String id, String sampleMimeType, int selectionFlags, String language, DrmInitData drmInitData) {
        return createTextSampleFormat(id, sampleMimeType, null, -1, selectionFlags, language, -1, drmInitData, Long.MAX_VALUE, Collections.emptyList());
    }

    public static Format createTextSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, int selectionFlags, String language, int accessibilityChannel, DrmInitData drmInitData) {
        return createTextSampleFormat(id, sampleMimeType, codecs, bitrate, selectionFlags, language, accessibilityChannel, drmInitData, Long.MAX_VALUE, Collections.emptyList());
    }

    public static Format createTextSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, int selectionFlags, String language, DrmInitData drmInitData, long subsampleOffsetUs) {
        return createTextSampleFormat(id, sampleMimeType, codecs, bitrate, selectionFlags, language, -1, drmInitData, subsampleOffsetUs, Collections.emptyList());
    }

    public static Format createTextSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, int selectionFlags, String language, int accessibilityChannel, DrmInitData drmInitData, long subsampleOffsetUs, List<byte[]> initializationData) {
        return new Format(id, null, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, selectionFlags, language, accessibilityChannel, subsampleOffsetUs, initializationData, drmInitData, null);
    }

    public static Format createImageSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, int selectionFlags, List<byte[]> initializationData, String language, DrmInitData drmInitData) {
        return new Format(id, null, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, selectionFlags, language, -1, Long.MAX_VALUE, initializationData, drmInitData, null);
    }

    public static Format createContainerFormat(String id, String containerMimeType, String sampleMimeType, String codecs, int bitrate, int selectionFlags, String language) {
        return new Format(id, containerMimeType, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, selectionFlags, language, -1, Long.MAX_VALUE, null, null, null);
    }

    public static Format createSampleFormat(String id, String sampleMimeType, long subsampleOffsetUs) {
        return new Format(id, null, sampleMimeType, null, -1, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, 0, null, -1, subsampleOffsetUs, null, null, null);
    }

    public static Format createSampleFormat(String id, String sampleMimeType, String codecs, int bitrate, DrmInitData drmInitData) {
        return new Format(id, null, sampleMimeType, codecs, bitrate, -1, -1, -1, -1.0f, -1, -1.0f, null, -1, null, -1, -1, -1, -1, -1, 0, null, -1, Long.MAX_VALUE, null, drmInitData, null);
    }

    Format(String id, String containerMimeType, String sampleMimeType, String codecs, int bitrate, int maxInputSize, int width, int height, float frameRate, int rotationDegrees, float pixelWidthHeightRatio, byte[] projectionData, int stereoMode, ColorInfo colorInfo, int channelCount, int sampleRate, int pcmEncoding, int encoderDelay, int encoderPadding, int selectionFlags, String language, int accessibilityChannel, long subsampleOffsetUs, List<byte[]> initializationData, DrmInitData drmInitData, Metadata metadata) {
        this.id = id;
        this.containerMimeType = containerMimeType;
        this.sampleMimeType = sampleMimeType;
        this.codecs = codecs;
        this.bitrate = bitrate;
        this.maxInputSize = maxInputSize;
        this.width = width;
        this.height = height;
        this.frameRate = frameRate;
        this.rotationDegrees = rotationDegrees;
        this.pixelWidthHeightRatio = pixelWidthHeightRatio;
        this.projectionData = projectionData;
        this.stereoMode = stereoMode;
        this.colorInfo = colorInfo;
        this.channelCount = channelCount;
        this.sampleRate = sampleRate;
        this.pcmEncoding = pcmEncoding;
        this.encoderDelay = encoderDelay;
        this.encoderPadding = encoderPadding;
        this.selectionFlags = selectionFlags;
        this.language = language;
        this.accessibilityChannel = accessibilityChannel;
        this.subsampleOffsetUs = subsampleOffsetUs;
        r0.initializationData = initializationData == null ? Collections.emptyList() : initializationData;
        r0.drmInitData = drmInitData;
        r0.metadata = metadata;
    }

    Format(Parcel in) {
        this.id = in.readString();
        this.containerMimeType = in.readString();
        this.sampleMimeType = in.readString();
        this.codecs = in.readString();
        this.bitrate = in.readInt();
        this.maxInputSize = in.readInt();
        this.width = in.readInt();
        this.height = in.readInt();
        this.frameRate = in.readFloat();
        this.rotationDegrees = in.readInt();
        this.pixelWidthHeightRatio = in.readFloat();
        int i = 0;
        this.projectionData = in.readInt() != 0 ? in.createByteArray() : null;
        this.stereoMode = in.readInt();
        this.colorInfo = (ColorInfo) in.readParcelable(ColorInfo.class.getClassLoader());
        this.channelCount = in.readInt();
        this.sampleRate = in.readInt();
        this.pcmEncoding = in.readInt();
        this.encoderDelay = in.readInt();
        this.encoderPadding = in.readInt();
        this.selectionFlags = in.readInt();
        this.language = in.readString();
        this.accessibilityChannel = in.readInt();
        this.subsampleOffsetUs = in.readLong();
        int initializationDataSize = in.readInt();
        this.initializationData = new ArrayList(initializationDataSize);
        while (i < initializationDataSize) {
            this.initializationData.add(in.createByteArray());
            i++;
        }
        this.drmInitData = (DrmInitData) in.readParcelable(DrmInitData.class.getClassLoader());
        this.metadata = (Metadata) in.readParcelable(Metadata.class.getClassLoader());
    }

    public Format copyWithMaxInputSize(int maxInputSize) {
        String str = this.id;
        String str2 = this.containerMimeType;
        String str3 = this.sampleMimeType;
        String str4 = this.codecs;
        int i = this.bitrate;
        int i2 = this.width;
        int i3 = this.height;
        float f = this.frameRate;
        int i4 = this.rotationDegrees;
        float f2 = this.pixelWidthHeightRatio;
        byte[] bArr = this.projectionData;
        int i5 = this.stereoMode;
        ColorInfo colorInfo = this.colorInfo;
        int i6 = this.channelCount;
        int i7 = this.sampleRate;
        ColorInfo colorInfo2 = colorInfo;
        int i8 = this.pcmEncoding;
        int i9 = this.encoderDelay;
        int i10 = this.encoderPadding;
        int i11 = this.selectionFlags;
        String str5 = this.language;
        int i12 = i5;
        int i13 = this.accessibilityChannel;
        long j = this.subsampleOffsetUs;
        List list = this.initializationData;
        List list2 = list;
        int i14 = i6;
        return new Format(str, str2, str3, str4, i, maxInputSize, i2, i3, f, i4, f2, bArr, i12, colorInfo2, i14, i7, i8, i9, i10, i11, str5, i13, j, list2, this.drmInitData, this.metadata);
    }

    public Format copyWithSubsampleOffsetUs(long subsampleOffsetUs) {
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
        return new Format(str, str2, str3, str4, i, i2, i3, i4, f, i5, f2, bArr, i6, colorInfo2, this.channelCount, this.sampleRate, this.pcmEncoding, this.encoderDelay, this.encoderPadding, this.selectionFlags, this.language, this.accessibilityChannel, subsampleOffsetUs, this.initializationData, this.drmInitData, this.metadata);
    }

    public Format copyWithContainerInfo(String id, String sampleMimeType, String codecs, int bitrate, int width, int height, int selectionFlags, String language) {
        String str = this.containerMimeType;
        int i = this.maxInputSize;
        float f = this.frameRate;
        int i2 = this.rotationDegrees;
        float f2 = this.pixelWidthHeightRatio;
        byte[] bArr = this.projectionData;
        int i3 = this.stereoMode;
        ColorInfo colorInfo = this.colorInfo;
        int i4 = this.channelCount;
        int i5 = this.sampleRate;
        int i6 = this.pcmEncoding;
        int i7 = this.encoderDelay;
        int i8 = this.encoderPadding;
        int i9 = this.accessibilityChannel;
        int i10 = i3;
        ColorInfo colorInfo2 = colorInfo;
        long j = this.subsampleOffsetUs;
        List list = this.initializationData;
        long j2 = j;
        DrmInitData drmInitData = this.drmInitData;
        int i11 = i9;
        int i12 = i8;
        int i13 = i7;
        int i14 = i6;
        int i15 = i5;
        int i16 = i4;
        DrmInitData drmInitData2 = drmInitData;
        return new Format(id, str, sampleMimeType, codecs, bitrate, i, width, height, f, i2, f2, bArr, i10, colorInfo2, i16, i15, i14, i13, i12, selectionFlags, language, i11, j2, list, drmInitData2, this.metadata);
    }

    public Format copyWithManifestFormatInfo(Format manifestFormat) {
        Format format = manifestFormat;
        if (this == format) {
            return r0;
        }
        String id = format.id;
        return new Format(id, r0.containerMimeType, r0.sampleMimeType, r0.codecs == null ? format.codecs : r0.codecs, r0.bitrate == -1 ? format.bitrate : r0.bitrate, r0.maxInputSize, r0.width, r0.height, r0.frameRate == -1.0f ? format.frameRate : r0.frameRate, r0.rotationDegrees, r0.pixelWidthHeightRatio, r0.projectionData, r0.stereoMode, r0.colorInfo, r0.channelCount, r0.sampleRate, r0.pcmEncoding, r0.encoderDelay, r0.encoderPadding, r0.selectionFlags | format.selectionFlags, r0.language == null ? format.language : r0.language, r0.accessibilityChannel, r0.subsampleOffsetUs, r0.initializationData, DrmInitData.createSessionCreationData(format.drmInitData, r0.drmInitData), r0.metadata);
    }

    public Format copyWithGaplessInfo(int encoderDelay, int encoderPadding) {
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
        int i10 = this.selectionFlags;
        String str5 = this.language;
        int i11 = i6;
        int i12 = this.accessibilityChannel;
        long j = this.subsampleOffsetUs;
        List list = this.initializationData;
        List list2 = list;
        return new Format(str, str2, str3, str4, i, i2, i3, i4, f, i5, f2, bArr, i11, colorInfo2, i7, i8, i9, encoderDelay, encoderPadding, i10, str5, i12, j, list2, this.drmInitData, this.metadata);
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

    public Format copyWithRotationDegrees(int rotationDegrees) {
        String str = this.id;
        String str2 = this.containerMimeType;
        String str3 = this.sampleMimeType;
        String str4 = this.codecs;
        int i = this.bitrate;
        int i2 = this.maxInputSize;
        int i3 = this.width;
        int i4 = this.height;
        float f = this.frameRate;
        float f2 = this.pixelWidthHeightRatio;
        byte[] bArr = this.projectionData;
        int i5 = this.stereoMode;
        ColorInfo colorInfo = this.colorInfo;
        int i6 = this.channelCount;
        int i7 = this.sampleRate;
        ColorInfo colorInfo2 = colorInfo;
        int i8 = this.pcmEncoding;
        int i9 = this.encoderDelay;
        int i10 = this.encoderPadding;
        int i11 = this.selectionFlags;
        String str5 = this.language;
        int i12 = i5;
        int i13 = this.accessibilityChannel;
        long j = this.subsampleOffsetUs;
        List list = this.initializationData;
        List list2 = list;
        int i14 = i6;
        return new Format(str, str2, str3, str4, i, i2, i3, i4, f, rotationDegrees, f2, bArr, i12, colorInfo2, i14, i7, i8, i9, i10, i11, str5, i13, j, list2, this.drmInitData, this.metadata);
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
        MediaFormat format = new MediaFormat();
        format.setString("mime", this.sampleMimeType);
        maybeSetStringV16(format, "language", this.language);
        maybeSetIntegerV16(format, "max-input-size", this.maxInputSize);
        maybeSetIntegerV16(format, "width", this.width);
        maybeSetIntegerV16(format, "height", this.height);
        maybeSetFloatV16(format, "frame-rate", this.frameRate);
        maybeSetIntegerV16(format, "rotation-degrees", this.rotationDegrees);
        maybeSetIntegerV16(format, "channel-count", this.channelCount);
        maybeSetIntegerV16(format, "sample-rate", this.sampleRate);
        for (int i = 0; i < this.initializationData.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("csd-");
            stringBuilder.append(i);
            format.setByteBuffer(stringBuilder.toString(), ByteBuffer.wrap((byte[]) this.initializationData.get(i)));
        }
        maybeSetColorInfoV24(format, this.colorInfo);
        return format;
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
            int result = 31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * ((31 * 17) + (this.id == null ? 0 : this.id.hashCode()))) + (this.containerMimeType == null ? 0 : this.containerMimeType.hashCode()))) + (this.sampleMimeType == null ? 0 : this.sampleMimeType.hashCode()))) + (this.codecs == null ? 0 : this.codecs.hashCode()))) + this.bitrate)) + this.width)) + this.height)) + this.channelCount)) + this.sampleRate)) + (this.language == null ? 0 : this.language.hashCode()))) + this.accessibilityChannel)) + (this.drmInitData == null ? 0 : this.drmInitData.hashCode()));
            if (this.metadata != null) {
                i = this.metadata.hashCode();
            }
            this.hashCode = result + i;
        }
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                Format other = (Format) obj;
                if (this.bitrate == other.bitrate && this.maxInputSize == other.maxInputSize && this.width == other.width && this.height == other.height && this.frameRate == other.frameRate && this.rotationDegrees == other.rotationDegrees && this.pixelWidthHeightRatio == other.pixelWidthHeightRatio && this.stereoMode == other.stereoMode && this.channelCount == other.channelCount && this.sampleRate == other.sampleRate && this.pcmEncoding == other.pcmEncoding && this.encoderDelay == other.encoderDelay && this.encoderPadding == other.encoderPadding && this.subsampleOffsetUs == other.subsampleOffsetUs && this.selectionFlags == other.selectionFlags && Util.areEqual(this.id, other.id) && Util.areEqual(this.language, other.language) && this.accessibilityChannel == other.accessibilityChannel && Util.areEqual(this.containerMimeType, other.containerMimeType) && Util.areEqual(this.sampleMimeType, other.sampleMimeType) && Util.areEqual(this.codecs, other.codecs) && Util.areEqual(this.drmInitData, other.drmInitData) && Util.areEqual(this.metadata, other.metadata) && Util.areEqual(this.colorInfo, other.colorInfo) && Arrays.equals(this.projectionData, other.projectionData)) {
                    if (this.initializationData.size() == other.initializationData.size()) {
                        for (int i = 0; i < this.initializationData.size(); i++) {
                            if (!Arrays.equals((byte[]) this.initializationData.get(i), (byte[]) other.initializationData.get(i))) {
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
    private static void maybeSetColorInfoV24(MediaFormat format, ColorInfo colorInfo) {
        if (colorInfo != null) {
            maybeSetIntegerV16(format, "color-transfer", colorInfo.colorTransfer);
            maybeSetIntegerV16(format, "color-standard", colorInfo.colorSpace);
            maybeSetIntegerV16(format, "color-range", colorInfo.colorRange);
            maybeSetByteBufferV16(format, "hdr-static-info", colorInfo.hdrStaticInfo);
        }
    }

    @TargetApi(16)
    private static void maybeSetStringV16(MediaFormat format, String key, String value) {
        if (value != null) {
            format.setString(key, value);
        }
    }

    @TargetApi(16)
    private static void maybeSetIntegerV16(MediaFormat format, String key, int value) {
        if (value != -1) {
            format.setInteger(key, value);
        }
    }

    @TargetApi(16)
    private static void maybeSetFloatV16(MediaFormat format, String key, float value) {
        if (value != -1.0f) {
            format.setFloat(key, value);
        }
    }

    @TargetApi(16)
    private static void maybeSetByteBufferV16(MediaFormat format, String key, byte[] value) {
        if (value != null) {
            format.setByteBuffer(key, ByteBuffer.wrap(value));
        }
    }

    public static String toLogString(Format format) {
        if (format == null) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        builder.append("id=");
        builder.append(format.id);
        builder.append(", mimeType=");
        builder.append(format.sampleMimeType);
        if (format.bitrate != -1) {
            builder.append(", bitrate=");
            builder.append(format.bitrate);
        }
        if (!(format.width == -1 || format.height == -1)) {
            builder.append(", res=");
            builder.append(format.width);
            builder.append("x");
            builder.append(format.height);
        }
        if (format.frameRate != -1.0f) {
            builder.append(", fps=");
            builder.append(format.frameRate);
        }
        if (format.channelCount != -1) {
            builder.append(", channels=");
            builder.append(format.channelCount);
        }
        if (format.sampleRate != -1) {
            builder.append(", sample_rate=");
            builder.append(format.sampleRate);
        }
        if (format.language != null) {
            builder.append(", language=");
            builder.append(format.language);
        }
        return builder.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.containerMimeType);
        dest.writeString(this.sampleMimeType);
        dest.writeString(this.codecs);
        dest.writeInt(this.bitrate);
        dest.writeInt(this.maxInputSize);
        dest.writeInt(this.width);
        dest.writeInt(this.height);
        dest.writeFloat(this.frameRate);
        dest.writeInt(this.rotationDegrees);
        dest.writeFloat(this.pixelWidthHeightRatio);
        dest.writeInt(this.projectionData != null ? 1 : 0);
        if (this.projectionData != null) {
            dest.writeByteArray(this.projectionData);
        }
        dest.writeInt(this.stereoMode);
        dest.writeParcelable(this.colorInfo, flags);
        dest.writeInt(this.channelCount);
        dest.writeInt(this.sampleRate);
        dest.writeInt(this.pcmEncoding);
        dest.writeInt(this.encoderDelay);
        dest.writeInt(this.encoderPadding);
        dest.writeInt(this.selectionFlags);
        dest.writeString(this.language);
        dest.writeInt(this.accessibilityChannel);
        dest.writeLong(this.subsampleOffsetUs);
        int initializationDataSize = this.initializationData.size();
        dest.writeInt(initializationDataSize);
        for (int i = 0; i < initializationDataSize; i++) {
            dest.writeByteArray((byte[]) this.initializationData.get(i));
        }
        dest.writeParcelable(this.drmInitData, 0);
        dest.writeParcelable(this.metadata, 0);
    }
}
