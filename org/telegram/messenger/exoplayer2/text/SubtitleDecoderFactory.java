package org.telegram.messenger.exoplayer2.text;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.text.cea.Cea608Decoder;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public interface SubtitleDecoderFactory {
    public static final SubtitleDecoderFactory DEFAULT = new SubtitleDecoderFactory() {
        public boolean supportsFormat(Format format) {
            return getDecoderClass(format.sampleMimeType) != null;
        }

        public SubtitleDecoder createDecoder(Format format) {
            try {
                Class<?> clazz = getDecoderClass(format.sampleMimeType);
                if (clazz == null) {
                    throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
                } else if (clazz != Cea608Decoder.class) {
                    return (SubtitleDecoder) clazz.asSubclass(SubtitleDecoder.class).getConstructor(new Class[0]).newInstance(new Object[0]);
                } else {
                    return (SubtitleDecoder) clazz.asSubclass(SubtitleDecoder.class).getConstructor(new Class[]{Integer.TYPE}).newInstance(new Object[]{Integer.valueOf(format.accessibilityChannel)});
                }
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
                    case -1004728940:
                        if (mimeType.equals(MimeTypes.TEXT_VTT)) {
                            obj = null;
                            break;
                        }
                        break;
                    case 691401887:
                        if (mimeType.equals(MimeTypes.APPLICATION_TX3G)) {
                            obj = 4;
                            break;
                        }
                        break;
                    case 1490991545:
                        if (mimeType.equals(MimeTypes.APPLICATION_MP4VTT)) {
                            obj = 2;
                            break;
                        }
                        break;
                    case 1566015601:
                        if (mimeType.equals(MimeTypes.APPLICATION_CEA608)) {
                            obj = 5;
                            break;
                        }
                        break;
                    case 1668750253:
                        if (mimeType.equals(MimeTypes.APPLICATION_SUBRIP)) {
                            obj = 3;
                            break;
                        }
                        break;
                    case 1693976202:
                        if (mimeType.equals(MimeTypes.APPLICATION_TTML)) {
                            obj = 1;
                            break;
                        }
                        break;
                }
                switch (obj) {
                    case null:
                        return Class.forName("org.telegram.messenger.exoplayer2.text.webvtt.WebvttDecoder");
                    case 1:
                        return Class.forName("org.telegram.messenger.exoplayer2.text.ttml.TtmlDecoder");
                    case 2:
                        return Class.forName("org.telegram.messenger.exoplayer2.text.webvtt.Mp4WebvttDecoder");
                    case 3:
                        return Class.forName("org.telegram.messenger.exoplayer2.text.subrip.SubripDecoder");
                    case 4:
                        return Class.forName("org.telegram.messenger.exoplayer2.text.tx3g.Tx3gDecoder");
                    case 5:
                        return Class.forName("org.telegram.messenger.exoplayer2.text.cea.Cea608Decoder");
                    default:
                        return null;
                }
            } catch (ClassNotFoundException e) {
                return null;
            }
        }
    };

    SubtitleDecoder createDecoder(Format format);

    boolean supportsFormat(Format format);
}
