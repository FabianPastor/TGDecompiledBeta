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
            format = format.sampleMimeType;
            if (!(MimeTypes.APPLICATION_ID3.equals(format) || MimeTypes.APPLICATION_EMSG.equals(format))) {
                if (MimeTypes.APPLICATION_SCTE35.equals(format) == null) {
                    return null;
                }
            }
            return true;
        }

        public MetadataDecoder createDecoder(Format format) {
            format = format.sampleMimeType;
            int hashCode = format.hashCode();
            if (hashCode != -NUM) {
                if (hashCode != NUM) {
                    if (hashCode == NUM) {
                        if (format.equals(MimeTypes.APPLICATION_SCTE35) != null) {
                            format = 2;
                            switch (format) {
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
                } else if (format.equals(MimeTypes.APPLICATION_EMSG) != null) {
                    format = true;
                    switch (format) {
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
            } else if (format.equals(MimeTypes.APPLICATION_ID3) != null) {
                format = null;
                switch (format) {
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
            format = -1;
            switch (format) {
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
