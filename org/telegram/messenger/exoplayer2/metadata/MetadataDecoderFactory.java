package org.telegram.messenger.exoplayer2.metadata;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public interface MetadataDecoderFactory {
    public static final MetadataDecoderFactory DEFAULT = new MetadataDecoderFactory() {
        public boolean supportsFormat(Format format) {
            return getDecoderClass(format.sampleMimeType) != null;
        }

        public MetadataDecoder createDecoder(Format format) {
            try {
                Class<?> clazz = getDecoderClass(format.sampleMimeType);
                if (clazz != null) {
                    return (MetadataDecoder) clazz.asSubclass(MetadataDecoder.class).getConstructor(new Class[0]).newInstance(new Object[0]);
                }
                throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
            } catch (Exception e) {
                throw new IllegalStateException("Unexpected error instantiating decoder", e);
            }
        }

        private Class<?> getDecoderClass(String mimeType) {
            if (mimeType == null) {
                return null;
            }
            Object obj = -1;
            try {
                switch (mimeType.hashCode()) {
                    case -1248341703:
                        if (mimeType.equals(MimeTypes.APPLICATION_ID3)) {
                            obj = null;
                            break;
                        }
                        break;
                    case 1154383568:
                        if (mimeType.equals(MimeTypes.APPLICATION_EMSG)) {
                            obj = 1;
                            break;
                        }
                        break;
                    case 1652648887:
                        if (mimeType.equals(MimeTypes.APPLICATION_SCTE35)) {
                            obj = 2;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case null:
                        return Class.forName("org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder");
                    case 1:
                        return Class.forName("org.telegram.messenger.exoplayer2.metadata.emsg.EventMessageDecoder");
                    case 2:
                        return Class.forName("org.telegram.messenger.exoplayer2.metadata.scte35.SpliceInfoDecoder");
                    default:
                        return null;
                }
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    };

    MetadataDecoder createDecoder(Format format);

    boolean supportsFormat(Format format);
}
