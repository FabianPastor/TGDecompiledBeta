package org.telegram.messenger.exoplayer2.metadata;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessageDecoder;
import org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder;
import org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInfoDecoder;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public interface MetadataDecoderFactory {
    public static final MetadataDecoderFactory DEFAULT = new C18491();

    /* renamed from: org.telegram.messenger.exoplayer2.metadata.MetadataDecoderFactory$1 */
    static class C18491 implements MetadataDecoderFactory {
        C18491() {
        }

        public boolean supportsFormat(Format format) {
            String mimeType = format.sampleMimeType;
            if (!(MimeTypes.APPLICATION_ID3.equals(mimeType) || MimeTypes.APPLICATION_EMSG.equals(mimeType))) {
                if (!MimeTypes.APPLICATION_SCTE35.equals(mimeType)) {
                    return false;
                }
            }
            return true;
        }

        public MetadataDecoder createDecoder(Format format) {
            Object obj;
            String str = format.sampleMimeType;
            int hashCode = str.hashCode();
            if (hashCode != -NUM) {
                if (hashCode != NUM) {
                    if (hashCode == NUM) {
                        if (str.equals(MimeTypes.APPLICATION_SCTE35)) {
                            obj = 2;
                            switch (obj) {
                                case null:
                                    return new Id3Decoder();
                                case 1:
                                    return new EventMessageDecoder();
                                case 2:
                                    return new SpliceInfoDecoder();
                                default:
                                    throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
                            }
                        }
                    }
                } else if (str.equals(MimeTypes.APPLICATION_EMSG)) {
                    obj = 1;
                    switch (obj) {
                        case null:
                            return new Id3Decoder();
                        case 1:
                            return new EventMessageDecoder();
                        case 2:
                            return new SpliceInfoDecoder();
                        default:
                            throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
                    }
                }
            } else if (str.equals(MimeTypes.APPLICATION_ID3)) {
                obj = null;
                switch (obj) {
                    case null:
                        return new Id3Decoder();
                    case 1:
                        return new EventMessageDecoder();
                    case 2:
                        return new SpliceInfoDecoder();
                    default:
                        throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
                }
            }
            obj = -1;
            switch (obj) {
                case null:
                    return new Id3Decoder();
                case 1:
                    return new EventMessageDecoder();
                case 2:
                    return new SpliceInfoDecoder();
                default:
                    throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
            }
        }
    }

    MetadataDecoder createDecoder(Format format);

    boolean supportsFormat(Format format);
}
