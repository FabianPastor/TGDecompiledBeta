package org.telegram.messenger.exoplayer2.text;

import org.telegram.messenger.exoplayer2.Format;
import org.telegram.messenger.exoplayer2.text.cea.Cea608Decoder;
import org.telegram.messenger.exoplayer2.text.cea.Cea708Decoder;
import org.telegram.messenger.exoplayer2.text.dvb.DvbDecoder;
import org.telegram.messenger.exoplayer2.text.pgs.PgsDecoder;
import org.telegram.messenger.exoplayer2.text.ssa.SsaDecoder;
import org.telegram.messenger.exoplayer2.text.subrip.SubripDecoder;
import org.telegram.messenger.exoplayer2.text.ttml.TtmlDecoder;
import org.telegram.messenger.exoplayer2.text.tx3g.Tx3gDecoder;
import org.telegram.messenger.exoplayer2.text.webvtt.Mp4WebvttDecoder;
import org.telegram.messenger.exoplayer2.text.webvtt.WebvttDecoder;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public interface SubtitleDecoderFactory {
    public static final SubtitleDecoderFactory DEFAULT = new SubtitleDecoderFactory() {
        public boolean supportsFormat(Format format) {
            String mimeType = format.sampleMimeType;
            if (MimeTypes.TEXT_VTT.equals(mimeType) || MimeTypes.TEXT_SSA.equals(mimeType) || MimeTypes.APPLICATION_TTML.equals(mimeType) || MimeTypes.APPLICATION_MP4VTT.equals(mimeType) || MimeTypes.APPLICATION_SUBRIP.equals(mimeType) || MimeTypes.APPLICATION_TX3G.equals(mimeType) || MimeTypes.APPLICATION_CEA608.equals(mimeType) || MimeTypes.APPLICATION_MP4CEA608.equals(mimeType) || MimeTypes.APPLICATION_CEA708.equals(mimeType) || MimeTypes.APPLICATION_DVBSUBS.equals(mimeType) || MimeTypes.APPLICATION_PGS.equals(mimeType)) {
                return true;
            }
            return false;
        }

        public SubtitleDecoder createDecoder(Format format) {
            String str = format.sampleMimeType;
            Object obj = -1;
            switch (str.hashCode()) {
                case -1351681404:
                    if (str.equals(MimeTypes.APPLICATION_DVBSUBS)) {
                        obj = 9;
                        break;
                    }
                    break;
                case -1248334819:
                    if (str.equals(MimeTypes.APPLICATION_PGS)) {
                        obj = 10;
                        break;
                    }
                    break;
                case -1026075066:
                    if (str.equals(MimeTypes.APPLICATION_MP4VTT)) {
                        obj = 2;
                        break;
                    }
                    break;
                case -1004728940:
                    if (str.equals(MimeTypes.TEXT_VTT)) {
                        obj = null;
                        break;
                    }
                    break;
                case 691401887:
                    if (str.equals(MimeTypes.APPLICATION_TX3G)) {
                        obj = 5;
                        break;
                    }
                    break;
                case 822864842:
                    if (str.equals(MimeTypes.TEXT_SSA)) {
                        obj = 1;
                        break;
                    }
                    break;
                case 930165504:
                    if (str.equals(MimeTypes.APPLICATION_MP4CEA608)) {
                        obj = 7;
                        break;
                    }
                    break;
                case 1566015601:
                    if (str.equals(MimeTypes.APPLICATION_CEA608)) {
                        obj = 6;
                        break;
                    }
                    break;
                case 1566016562:
                    if (str.equals(MimeTypes.APPLICATION_CEA708)) {
                        obj = 8;
                        break;
                    }
                    break;
                case 1668750253:
                    if (str.equals(MimeTypes.APPLICATION_SUBRIP)) {
                        obj = 4;
                        break;
                    }
                    break;
                case 1693976202:
                    if (str.equals(MimeTypes.APPLICATION_TTML)) {
                        obj = 3;
                        break;
                    }
                    break;
            }
            switch (obj) {
                case null:
                    return new WebvttDecoder();
                case 1:
                    return new SsaDecoder(format.initializationData);
                case 2:
                    return new Mp4WebvttDecoder();
                case 3:
                    return new TtmlDecoder();
                case 4:
                    return new SubripDecoder();
                case 5:
                    return new Tx3gDecoder(format.initializationData);
                case 6:
                case 7:
                    return new Cea608Decoder(format.sampleMimeType, format.accessibilityChannel);
                case 8:
                    return new Cea708Decoder(format.accessibilityChannel);
                case 9:
                    return new DvbDecoder(format.initializationData);
                case 10:
                    return new PgsDecoder();
                default:
                    throw new IllegalArgumentException("Attempted to create decoder for unsupported format");
            }
        }
    };

    SubtitleDecoder createDecoder(Format format);

    boolean supportsFormat(Format format);
}
