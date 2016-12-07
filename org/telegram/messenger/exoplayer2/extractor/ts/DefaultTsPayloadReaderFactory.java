package org.telegram.messenger.exoplayer2.extractor.ts;

import android.util.SparseArray;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.EsInfo;
import org.telegram.messenger.exoplayer2.extractor.ts.TsPayloadReader.Factory;

public final class DefaultTsPayloadReaderFactory implements Factory {
    public static final int FLAG_ALLOW_NON_IDR_KEYFRAMES = 1;
    public static final int FLAG_DETECT_ACCESS_UNITS = 8;
    public static final int FLAG_IGNORE_AAC_STREAM = 2;
    public static final int FLAG_IGNORE_H264_STREAM = 4;
    private final int flags;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Flags {
    }

    public DefaultTsPayloadReaderFactory() {
        this(0);
    }

    public DefaultTsPayloadReaderFactory(int flags) {
        this.flags = flags;
    }

    public SparseArray<TsPayloadReader> createInitialPayloadReaders() {
        return new SparseArray();
    }

    public TsPayloadReader createPayloadReader(int streamType, EsInfo esInfo) {
        boolean z = true;
        switch (streamType) {
            case 2:
                return new PesReader(new H262Reader());
            case 3:
            case 4:
                return new PesReader(new MpegAudioReader(esInfo.language));
            case 15:
                if ((this.flags & 2) == 0) {
                    return new PesReader(new AdtsReader(false, esInfo.language));
                }
                return null;
            case 21:
                return new PesReader(new Id3Reader());
            case 27:
                if ((this.flags & 4) != 0) {
                    return null;
                }
                boolean z2 = (this.flags & 1) != 0;
                if ((this.flags & 8) == 0) {
                    z = false;
                }
                return new PesReader(new H264Reader(z2, z));
            case 36:
                return new PesReader(new H265Reader());
            case TsExtractor.TS_STREAM_TYPE_AC3 /*129*/:
            case TsExtractor.TS_STREAM_TYPE_E_AC3 /*135*/:
                return new PesReader(new Ac3Reader(esInfo.language));
            case TsExtractor.TS_STREAM_TYPE_HDMV_DTS /*130*/:
            case TsExtractor.TS_STREAM_TYPE_DTS /*138*/:
                return new PesReader(new DtsReader(esInfo.language));
            case TsExtractor.TS_STREAM_TYPE_SPLICE_INFO /*134*/:
                return new SectionReader(new SpliceInfoSectionReader());
            default:
                return null;
        }
    }
}
