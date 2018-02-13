package org.telegram.messenger.exoplayer2.ext.ffmpeg;

import org.telegram.messenger.exoplayer2.ExoPlayerLibraryInfo;
import org.telegram.messenger.exoplayer2.util.MimeTypes;

public final class FfmpegLibrary {
    private static native String ffmpegGetVersion();

    private static native boolean ffmpegHasDecoder(String str);

    static {
        ExoPlayerLibraryInfo.registerModule("goog.exo.ffmpeg");
    }

    private FfmpegLibrary() {
    }

    public static String getVersion() {
        return ffmpegGetVersion();
    }

    public static boolean supportsFormat(String mimeType) {
        String codecName = getCodecName(mimeType);
        return codecName != null && ffmpegHasDecoder(codecName);
    }

    static String getCodecName(String mimeType) {
        Object obj = -1;
        switch (mimeType.hashCode()) {
            case -1606874997:
                if (mimeType.equals(MimeTypes.AUDIO_AMR_WB)) {
                    obj = 12;
                    break;
                }
                break;
            case -1095064472:
                if (mimeType.equals(MimeTypes.AUDIO_DTS)) {
                    obj = 7;
                    break;
                }
                break;
            case -1003765268:
                if (mimeType.equals(MimeTypes.AUDIO_VORBIS)) {
                    obj = 9;
                    break;
                }
                break;
            case -432837260:
                if (mimeType.equals(MimeTypes.AUDIO_MPEG_L1)) {
                    obj = 2;
                    break;
                }
                break;
            case -432837259:
                if (mimeType.equals(MimeTypes.AUDIO_MPEG_L2)) {
                    obj = 3;
                    break;
                }
                break;
            case -53558318:
                if (mimeType.equals(MimeTypes.AUDIO_AAC)) {
                    obj = null;
                    break;
                }
                break;
            case 187078296:
                if (mimeType.equals(MimeTypes.AUDIO_AC3)) {
                    obj = 4;
                    break;
                }
                break;
            case 1503095341:
                if (mimeType.equals(MimeTypes.AUDIO_AMR_NB)) {
                    obj = 11;
                    break;
                }
                break;
            case 1504470054:
                if (mimeType.equals(MimeTypes.AUDIO_ALAC)) {
                    obj = 14;
                    break;
                }
                break;
            case 1504578661:
                if (mimeType.equals(MimeTypes.AUDIO_E_AC3)) {
                    obj = 5;
                    break;
                }
                break;
            case 1504619009:
                if (mimeType.equals(MimeTypes.AUDIO_FLAC)) {
                    obj = 13;
                    break;
                }
                break;
            case 1504831518:
                if (mimeType.equals(MimeTypes.AUDIO_MPEG)) {
                    obj = 1;
                    break;
                }
                break;
            case 1504891608:
                if (mimeType.equals(MimeTypes.AUDIO_OPUS)) {
                    obj = 10;
                    break;
                }
                break;
            case 1505942594:
                if (mimeType.equals(MimeTypes.AUDIO_DTS_HD)) {
                    obj = 8;
                    break;
                }
                break;
            case 1556697186:
                if (mimeType.equals(MimeTypes.AUDIO_TRUEHD)) {
                    obj = 6;
                    break;
                }
                break;
        }
        switch (obj) {
            case null:
                return "aac";
            case 1:
            case 2:
            case 3:
                return "mp3";
            case 4:
                return "ac3";
            case 5:
                return "eac3";
            case 6:
                return "truehd";
            case 7:
            case 8:
                return "dca";
            case 9:
                return "vorbis";
            case 10:
                return "opus";
            case 11:
                return "amrnb";
            case 12:
                return "amrwb";
            case 13:
                return "flac";
            case 14:
                return "alac";
            default:
                return null;
        }
    }
}
