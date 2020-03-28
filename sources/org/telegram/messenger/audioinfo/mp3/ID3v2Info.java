package org.telegram.messenger.audioinfo.mp3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;

public class ID3v2Info extends AudioInfo {
    static final Logger LOGGER = Logger.getLogger(ID3v2Info.class.getName());
    private byte coverPictureType;
    private final Level debugLevel;

    static class AttachedPicture {
        final byte[] imageData;
        final byte type;

        public AttachedPicture(byte b, String str, String str2, byte[] bArr) {
            this.type = b;
            this.imageData = bArr;
        }
    }

    static class CommentOrUnsynchronizedLyrics {
        final String description;
        final String text;

        public CommentOrUnsynchronizedLyrics(String str, String str2, String str3) {
            this.description = str2;
            this.text = str3;
        }
    }

    public static boolean isID3v2StartPosition(InputStream inputStream) throws IOException {
        inputStream.mark(3);
        try {
            return inputStream.read() == 73 && inputStream.read() == 68 && inputStream.read() == 51;
        } finally {
            inputStream.reset();
        }
    }

    public ID3v2Info(InputStream inputStream, Level level) throws IOException, ID3v2Exception {
        ID3v2FrameBody frameBody;
        ID3v2DataInput data;
        long remainingLength;
        this.debugLevel = level;
        if (isID3v2StartPosition(inputStream)) {
            ID3v2TagHeader iD3v2TagHeader = new ID3v2TagHeader(inputStream);
            this.brand = "ID3";
            String.format("2.%d.%d", new Object[]{Integer.valueOf(iD3v2TagHeader.getVersion()), Integer.valueOf(iD3v2TagHeader.getRevision())});
            ID3v2TagBody tagBody = iD3v2TagHeader.tagBody(inputStream);
            while (true) {
                try {
                    if (tagBody.getRemainingLength() <= 10) {
                        break;
                    }
                    ID3v2FrameHeader iD3v2FrameHeader = new ID3v2FrameHeader(tagBody);
                    if (iD3v2FrameHeader.isPadding()) {
                        break;
                    } else if (((long) iD3v2FrameHeader.getBodySize()) > tagBody.getRemainingLength()) {
                        if (LOGGER.isLoggable(level)) {
                            LOGGER.log(level, "ID3 frame claims to extend frames area");
                        }
                    } else if (!iD3v2FrameHeader.isValid() || iD3v2FrameHeader.isEncryption()) {
                        tagBody.getData().skipFully((long) iD3v2FrameHeader.getBodySize());
                    } else {
                        frameBody = tagBody.frameBody(iD3v2FrameHeader);
                        try {
                            parseFrame(frameBody);
                            data = frameBody.getData();
                            remainingLength = frameBody.getRemainingLength();
                        } catch (ID3v2Exception e) {
                            if (LOGGER.isLoggable(level)) {
                                LOGGER.log(level, String.format("ID3 exception occured in frame %s: %s", new Object[]{iD3v2FrameHeader.getFrameId(), e.getMessage()}));
                            }
                            data = frameBody.getData();
                            remainingLength = frameBody.getRemainingLength();
                        }
                        data.skipFully(remainingLength);
                    }
                } catch (ID3v2Exception e2) {
                    if (LOGGER.isLoggable(level)) {
                        Logger logger = LOGGER;
                        logger.log(level, "ID3 exception occured: " + e2.getMessage());
                    }
                } catch (Throwable th) {
                    frameBody.getData().skipFully(frameBody.getRemainingLength());
                    throw th;
                }
            }
            tagBody.getData().skipFully(tagBody.getRemainingLength());
            if (iD3v2TagHeader.getFooterSize() > 0) {
                inputStream.skip((long) iD3v2TagHeader.getFooterSize());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void parseFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        byte b;
        String str;
        int i;
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, "Parsing frame: " + iD3v2FrameBody.getFrameHeader().getFrameId());
        }
        String frameId = iD3v2FrameBody.getFrameHeader().getFrameId();
        char c = 65535;
        switch (frameId.hashCode()) {
            case 66913:
                if (frameId.equals("COM")) {
                    c = 2;
                    break;
                }
                break;
            case 79210:
                if (frameId.equals("PIC")) {
                    c = 0;
                    break;
                }
                break;
            case 82815:
                if (frameId.equals("TAL")) {
                    c = 4;
                    break;
                }
                break;
            case 82878:
                if (frameId.equals("TCM")) {
                    c = 8;
                    break;
                }
                break;
            case 82880:
                if (frameId.equals("TCO")) {
                    c = 10;
                    break;
                }
                break;
            case 82881:
                if (frameId.equals("TCP")) {
                    c = 6;
                    break;
                }
                break;
            case 82883:
                if (frameId.equals("TCR")) {
                    c = 12;
                    break;
                }
                break;
            case 83149:
                if (frameId.equals("TLE")) {
                    c = 15;
                    break;
                }
                break;
            case 83253:
                if (frameId.equals("TP1")) {
                    c = 17;
                    break;
                }
                break;
            case 83254:
                if (frameId.equals("TP2")) {
                    c = 19;
                    break;
                }
                break;
            case 83269:
                if (frameId.equals("TPA")) {
                    c = 21;
                    break;
                }
                break;
            case 83341:
                if (frameId.equals("TRK")) {
                    c = 23;
                    break;
                }
                break;
            case 83377:
                if (frameId.equals("TT1")) {
                    c = 25;
                    break;
                }
                break;
            case 83378:
                if (frameId.equals("TT2")) {
                    c = 27;
                    break;
                }
                break;
            case 83552:
                if (frameId.equals("TYE")) {
                    c = 29;
                    break;
                }
                break;
            case 84125:
                if (frameId.equals("ULT")) {
                    c = 31;
                    break;
                }
                break;
            case 2015625:
                if (frameId.equals("APIC")) {
                    c = 1;
                    break;
                }
                break;
            case 2074380:
                if (frameId.equals("COMM")) {
                    c = 3;
                    break;
                }
                break;
            case 2567331:
                if (frameId.equals("TALB")) {
                    c = 5;
                    break;
                }
                break;
            case 2569298:
                if (frameId.equals("TCMP")) {
                    c = 7;
                    break;
                }
                break;
            case 2569357:
                if (frameId.equals("TCOM")) {
                    c = 9;
                    break;
                }
                break;
            case 2569358:
                if (frameId.equals("TCON")) {
                    c = 11;
                    break;
                }
                break;
            case 2569360:
                if (frameId.equals("TCOP")) {
                    c = 13;
                    break;
                }
                break;
            case 2570401:
                if (frameId.equals("TDRC")) {
                    c = 14;
                    break;
                }
                break;
            case 2575250:
                if (frameId.equals("TIT1")) {
                    c = 26;
                    break;
                }
                break;
            case 2575251:
                if (frameId.equals("TIT2")) {
                    c = 28;
                    break;
                }
                break;
            case 2577697:
                if (frameId.equals("TLEN")) {
                    c = 16;
                    break;
                }
                break;
            case 2581512:
                if (frameId.equals("TPE1")) {
                    c = 18;
                    break;
                }
                break;
            case 2581513:
                if (frameId.equals("TPE2")) {
                    c = 20;
                    break;
                }
                break;
            case 2581856:
                if (frameId.equals("TPOS")) {
                    c = 22;
                    break;
                }
                break;
            case 2583398:
                if (frameId.equals("TRCK")) {
                    c = 24;
                    break;
                }
                break;
            case 2590194:
                if (frameId.equals("TYER")) {
                    c = 30;
                    break;
                }
                break;
            case 2614438:
                if (frameId.equals("USLT")) {
                    c = ' ';
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
            case 1:
                if (this.cover == null || this.coverPictureType != 3) {
                    AttachedPicture parseAttachedPictureFrame = parseAttachedPictureFrame(iD3v2FrameBody);
                    if (this.cover == null || (b = parseAttachedPictureFrame.type) == 3 || b == 0) {
                        try {
                            byte[] bArr = parseAttachedPictureFrame.imageData;
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            options.inSampleSize = 1;
                            BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
                            if (options.outWidth > 800 || options.outHeight > 800) {
                                for (int max = Math.max(options.outWidth, options.outHeight); max > 800; max /= 2) {
                                    options.inSampleSize *= 2;
                                }
                            }
                            options.inJustDecodeBounds = false;
                            Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
                            this.cover = decodeByteArray;
                            if (decodeByteArray != null) {
                                float max2 = ((float) Math.max(decodeByteArray.getWidth(), this.cover.getHeight())) / 120.0f;
                                if (max2 > 0.0f) {
                                    this.smallCover = Bitmap.createScaledBitmap(this.cover, (int) (((float) this.cover.getWidth()) / max2), (int) (((float) this.cover.getHeight()) / max2), true);
                                } else {
                                    this.smallCover = this.cover;
                                }
                                if (this.smallCover == null) {
                                    this.smallCover = this.cover;
                                }
                            }
                        } catch (Throwable th) {
                            th.printStackTrace();
                        }
                        this.coverPictureType = parseAttachedPictureFrame.type;
                        return;
                    }
                    return;
                }
                return;
            case 2:
            case 3:
                CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame = parseCommentOrUnsynchronizedLyricsFrame(iD3v2FrameBody);
                if (this.comment == null || (str = parseCommentOrUnsynchronizedLyricsFrame.description) == null || "".equals(str)) {
                    this.comment = parseCommentOrUnsynchronizedLyricsFrame.text;
                    return;
                }
                return;
            case 4:
            case 5:
                this.album = parseTextFrame(iD3v2FrameBody);
                return;
            case 6:
            case 7:
                this.compilation = "1".equals(parseTextFrame(iD3v2FrameBody));
                return;
            case 8:
            case 9:
                this.composer = parseTextFrame(iD3v2FrameBody);
                return;
            case 10:
            case 11:
                String parseTextFrame = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame.length() > 0) {
                    this.genre = parseTextFrame;
                    ID3v1Genre iD3v1Genre = null;
                    try {
                        if (parseTextFrame.charAt(0) == '(') {
                            int indexOf = parseTextFrame.indexOf(41);
                            if (indexOf > 1 && (iD3v1Genre = ID3v1Genre.getGenre(Integer.parseInt(parseTextFrame.substring(1, indexOf)))) == null && parseTextFrame.length() > (i = indexOf + 1)) {
                                this.genre = parseTextFrame.substring(i);
                            }
                        } else {
                            iD3v1Genre = ID3v1Genre.getGenre(Integer.parseInt(parseTextFrame));
                        }
                        if (iD3v1Genre != null) {
                            this.genre = iD3v1Genre.getDescription();
                            return;
                        }
                        return;
                    } catch (NumberFormatException unused) {
                        return;
                    }
                } else {
                    return;
                }
            case 12:
            case 13:
                this.copyright = parseTextFrame(iD3v2FrameBody);
                return;
            case 14:
                String parseTextFrame2 = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame2.length() >= 4) {
                    try {
                        this.year = Short.valueOf(parseTextFrame2.substring(0, 4)).shortValue();
                        return;
                    } catch (NumberFormatException unused2) {
                        if (LOGGER.isLoggable(this.debugLevel)) {
                            LOGGER.log(this.debugLevel, "Could not parse year from: " + parseTextFrame2);
                            return;
                        }
                        return;
                    }
                } else {
                    return;
                }
            case 15:
            case 16:
                String parseTextFrame3 = parseTextFrame(iD3v2FrameBody);
                try {
                    this.duration = Long.valueOf(parseTextFrame3).longValue();
                    return;
                } catch (NumberFormatException unused3) {
                    if (LOGGER.isLoggable(this.debugLevel)) {
                        LOGGER.log(this.debugLevel, "Could not parse track duration: " + parseTextFrame3);
                        return;
                    }
                    return;
                }
            case 17:
            case 18:
                this.artist = parseTextFrame(iD3v2FrameBody);
                return;
            case 19:
            case 20:
                this.albumArtist = parseTextFrame(iD3v2FrameBody);
                return;
            case 21:
            case 22:
                String parseTextFrame4 = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame4.length() > 0) {
                    int indexOf2 = parseTextFrame4.indexOf(47);
                    if (indexOf2 < 0) {
                        try {
                            this.disc = Short.valueOf(parseTextFrame4).shortValue();
                            return;
                        } catch (NumberFormatException unused4) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                LOGGER.log(this.debugLevel, "Could not parse disc number: " + parseTextFrame4);
                                return;
                            }
                            return;
                        }
                    } else {
                        try {
                            this.disc = Short.valueOf(parseTextFrame4.substring(0, indexOf2)).shortValue();
                        } catch (NumberFormatException unused5) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                LOGGER.log(this.debugLevel, "Could not parse disc number: " + parseTextFrame4);
                            }
                        }
                        try {
                            this.discs = Short.valueOf(parseTextFrame4.substring(indexOf2 + 1)).shortValue();
                            return;
                        } catch (NumberFormatException unused6) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                LOGGER.log(this.debugLevel, "Could not parse number of discs: " + parseTextFrame4);
                                return;
                            }
                            return;
                        }
                    }
                } else {
                    return;
                }
            case 23:
            case 24:
                String parseTextFrame5 = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame5.length() > 0) {
                    int indexOf3 = parseTextFrame5.indexOf(47);
                    if (indexOf3 < 0) {
                        try {
                            this.track = Short.valueOf(parseTextFrame5).shortValue();
                            return;
                        } catch (NumberFormatException unused7) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                LOGGER.log(this.debugLevel, "Could not parse track number: " + parseTextFrame5);
                                return;
                            }
                            return;
                        }
                    } else {
                        try {
                            this.track = Short.valueOf(parseTextFrame5.substring(0, indexOf3)).shortValue();
                        } catch (NumberFormatException unused8) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                LOGGER.log(this.debugLevel, "Could not parse track number: " + parseTextFrame5);
                            }
                        }
                        try {
                            this.tracks = Short.valueOf(parseTextFrame5.substring(indexOf3 + 1)).shortValue();
                            return;
                        } catch (NumberFormatException unused9) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                LOGGER.log(this.debugLevel, "Could not parse number of tracks: " + parseTextFrame5);
                                return;
                            }
                            return;
                        }
                    }
                } else {
                    return;
                }
            case 25:
            case 26:
                this.grouping = parseTextFrame(iD3v2FrameBody);
                return;
            case 27:
            case 28:
                this.title = parseTextFrame(iD3v2FrameBody);
                return;
            case 29:
            case 30:
                String parseTextFrame6 = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame6.length() > 0) {
                    try {
                        this.year = Short.valueOf(parseTextFrame6).shortValue();
                        return;
                    } catch (NumberFormatException unused10) {
                        if (LOGGER.isLoggable(this.debugLevel)) {
                            LOGGER.log(this.debugLevel, "Could not parse year: " + parseTextFrame6);
                            return;
                        }
                        return;
                    }
                } else {
                    return;
                }
            case 31:
            case ' ':
                if (this.lyrics == null) {
                    this.lyrics = parseCommentOrUnsynchronizedLyricsFrame(iD3v2FrameBody).text;
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: package-private */
    public String parseTextFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        return iD3v2FrameBody.readFixedLengthString((int) iD3v2FrameBody.getRemainingLength(), iD3v2FrameBody.readEncoding());
    }

    /* access modifiers changed from: package-private */
    public CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        ID3v2Encoding readEncoding = iD3v2FrameBody.readEncoding();
        return new CommentOrUnsynchronizedLyrics(iD3v2FrameBody.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1), iD3v2FrameBody.readZeroTerminatedString(200, readEncoding), iD3v2FrameBody.readFixedLengthString((int) iD3v2FrameBody.getRemainingLength(), readEncoding));
    }

    /* access modifiers changed from: package-private */
    public AttachedPicture parseAttachedPictureFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        String str;
        ID3v2Encoding readEncoding = iD3v2FrameBody.readEncoding();
        if (iD3v2FrameBody.getTagHeader().getVersion() == 2) {
            String upperCase = iD3v2FrameBody.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1).toUpperCase();
            char c = 65535;
            int hashCode = upperCase.hashCode();
            if (hashCode != 73665) {
                if (hashCode == 79369 && upperCase.equals("PNG")) {
                    c = 0;
                }
            } else if (upperCase.equals("JPG")) {
                c = 1;
            }
            str = c != 0 ? c != 1 ? "image/unknown" : "image/jpeg" : "image/png";
        } else {
            str = iD3v2FrameBody.readZeroTerminatedString(20, ID3v2Encoding.ISO_8859_1);
        }
        return new AttachedPicture(iD3v2FrameBody.getData().readByte(), iD3v2FrameBody.readZeroTerminatedString(200, readEncoding), str, iD3v2FrameBody.getData().readFully((int) iD3v2FrameBody.getRemainingLength()));
    }
}
