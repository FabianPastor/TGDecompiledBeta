package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.EsInfo;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.Factory;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class DefaultTsPayloadReaderFactory implements Factory {
    private static final int DESCRIPTOR_TAG_CAPTION_SERVICE = 134;
    public static final int FLAG_ALLOW_NON_IDR_KEYFRAMES = 1;
    public static final int FLAG_DETECT_ACCESS_UNITS = 8;
    public static final int FLAG_IGNORE_AAC_STREAM = 2;
    public static final int FLAG_IGNORE_H264_STREAM = 4;
    public static final int FLAG_IGNORE_SPLICE_INFO_STREAM = 16;
    public static final int FLAG_OVERRIDE_CAPTION_DESCRIPTORS = 32;
    private final List<Format> closedCaptionFormats;
    private final int flags;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    public DefaultTsPayloadReaderFactory() {
        this(0);
    }

    public DefaultTsPayloadReaderFactory(int i) {
        this(i, Collections.emptyList());
    }

    public DefaultTsPayloadReaderFactory(int i, List<Format> list) {
        this.flags = i;
        if (isSet(32) == 0 && list.isEmpty() != 0) {
            list = Collections.singletonList(Format.createTextSampleFormat(null, MimeTypes.APPLICATION_CEA608, null, null));
        }
        this.closedCaptionFormats = list;
    }

    public SparseArray<TsPayloadReader> createInitialPayloadReaders() {
        return new SparseArray();
    }

    public TsPayloadReader createPayloadReader(int i, EsInfo esInfo) {
        TsPayloadReader tsPayloadReader = null;
        switch (i) {
            case 2:
                return new PesReader(new H262Reader());
            case 3:
            case 4:
                return new PesReader(new MpegAudioReader(esInfo.language));
            case 15:
                if (isSet(2) == 0) {
                    tsPayloadReader = new PesReader(new AdtsReader(false, esInfo.language));
                }
                return tsPayloadReader;
            case 17:
                if (isSet(2) == 0) {
                    tsPayloadReader = new PesReader(new LatmReader(esInfo.language));
                }
                return tsPayloadReader;
            case 21:
                return new PesReader(new Id3Reader());
            case 27:
                if (isSet(4) == 0) {
                    tsPayloadReader = new PesReader(new H264Reader(buildSeiReader(esInfo), isSet(1), isSet(8)));
                }
                return tsPayloadReader;
            case TsExtractor.TS_STREAM_TYPE_H265 /*36*/:
                return new PesReader(new H265Reader(buildSeiReader(esInfo)));
            case TsExtractor.TS_STREAM_TYPE_DVBSUBS /*89*/:
                return new PesReader(new DvbSubtitleReader(esInfo.dvbSubtitleInfos));
            case TsExtractor.TS_STREAM_TYPE_AC3 /*129*/:
            case TsExtractor.TS_STREAM_TYPE_E_AC3 /*135*/:
                return new PesReader(new Ac3Reader(esInfo.language));
            case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
            case TsExtractor.TS_STREAM_TYPE_DTS /*138*/:
                return new PesReader(new DtsReader(esInfo.language));
            case 134:
                if (isSet(16) == 0) {
                    tsPayloadReader = new SectionReader(new SpliceInfoSectionReader());
                }
                return tsPayloadReader;
            default:
                return null;
        }
    }

    private SeiReader buildSeiReader(EsInfo esInfo) {
        if (isSet(32)) {
            return new SeiReader(this.closedCaptionFormats);
        }
        ParsableByteArray parsableByteArray = new ParsableByteArray(esInfo.descriptorBytes);
        esInfo = this.closedCaptionFormats;
        while (parsableByteArray.bytesLeft() > 0) {
            int position = parsableByteArray.getPosition() + parsableByteArray.readUnsignedByte();
            if (parsableByteArray.readUnsignedByte() == 134) {
                esInfo = new ArrayList();
                int readUnsignedByte = parsableByteArray.readUnsignedByte() & 31;
                for (int i = 0; i < readUnsignedByte; i++) {
                    int i2;
                    String str;
                    String readString = parsableByteArray.readString(3);
                    int readUnsignedByte2 = parsableByteArray.readUnsignedByte();
                    if (((readUnsignedByte2 & 128) != 0 ? 1 : null) != null) {
                        i2 = readUnsignedByte2 & 63;
                        str = MimeTypes.APPLICATION_CEA708;
                    } else {
                        i2 = 1;
                        str = MimeTypes.APPLICATION_CEA608;
                    }
                    esInfo.add(Format.createTextSampleFormat(null, str, null, -1, 0, readString, i2, null));
                    parsableByteArray.skipBytes(2);
                }
            }
            parsableByteArray.setPosition(position);
        }
        return new SeiReader(esInfo);
    }

    private boolean isSet(int i) {
        return (i & this.flags) != 0;
    }
}
