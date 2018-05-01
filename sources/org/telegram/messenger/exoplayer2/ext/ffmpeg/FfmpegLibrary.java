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

    public static boolean supportsFormat(String str) {
        str = getCodecName(str);
        return (str == null || ffmpegHasDecoder(str) == null) ? null : true;
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static String getCodecName(String str) {
        switch (str.hashCode()) {
            case -1606874997:
                if (str.equals(MimeTypes.AUDIO_AMR_WB) != null) {
                    str = 12;
                    break;
                }
            case -1095064472:
                if (str.equals(MimeTypes.AUDIO_DTS) != null) {
                    str = 7;
                    break;
                }
            case -1003765268:
                if (str.equals(MimeTypes.AUDIO_VORBIS) != null) {
                    str = 9;
                    break;
                }
            case -432837260:
                if (str.equals(MimeTypes.AUDIO_MPEG_L1) != null) {
                    str = 2;
                    break;
                }
            case -432837259:
                if (str.equals(MimeTypes.AUDIO_MPEG_L2) != null) {
                    str = 3;
                    break;
                }
            case -53558318:
                if (str.equals(MimeTypes.AUDIO_AAC) != null) {
                    str = null;
                    break;
                }
            case 187078296:
                if (str.equals(MimeTypes.AUDIO_AC3) != null) {
                    str = 4;
                    break;
                }
            case 1503095341:
                if (str.equals(MimeTypes.AUDIO_AMR_NB) != null) {
                    str = 11;
                    break;
                }
            case 1504470054:
                if (str.equals(MimeTypes.AUDIO_ALAC) != null) {
                    str = 14;
                    break;
                }
            case 1504578661:
                if (str.equals(MimeTypes.AUDIO_E_AC3) != null) {
                    str = 5;
                    break;
                }
            case 1504619009:
                if (str.equals(MimeTypes.AUDIO_FLAC) != null) {
                    str = 13;
                    break;
                }
            case 1504831518:
                if (str.equals(MimeTypes.AUDIO_MPEG) != null) {
                    str = true;
                    break;
                }
            case 1504891608:
                if (str.equals(MimeTypes.AUDIO_OPUS) != null) {
                    str = 10;
                    break;
                }
            case 1505942594:
                if (str.equals(MimeTypes.AUDIO_DTS_HD) != null) {
                    str = 8;
                    break;
                }
            case 1556697186:
                if (str.equals(MimeTypes.AUDIO_TRUEHD) != null) {
                    str = 6;
                    break;
                }
            default:
        }
        str = -1;
        switch (str) {
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
