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
        final String description;
        final byte[] imageData;
        final String imageType;
        final byte type;

        public AttachedPicture(byte b, String str, String str2, byte[] bArr) {
            this.type = b;
            this.description = str;
            this.imageType = str2;
            this.imageData = bArr;
        }
    }

    static class CommentOrUnsynchronizedLyrics {
        final String description;
        final String language;
        final String text;

        public CommentOrUnsynchronizedLyrics(String str, String str2, String str3) {
            this.language = str;
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
            this.version = String.format("2.%d.%d", new Object[]{Integer.valueOf(iD3v2TagHeader.getVersion()), Integer.valueOf(iD3v2TagHeader.getRevision())});
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
                        Logger logger = LOGGER;
                        if (logger.isLoggable(level)) {
                            logger.log(level, "ID3 frame claims to extend frames area");
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
                    Logger logger2 = LOGGER;
                    if (logger2.isLoggable(level)) {
                        logger2.log(level, "ID3 exception occured: " + e2.getMessage());
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
        String str;
        byte b;
        int i;
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, "Parsing frame: " + iD3v2FrameBody.getFrameHeader().getFrameId());
        }
        String frameId = iD3v2FrameBody.getFrameHeader().getFrameId();
        frameId.hashCode();
        char c = 65535;
        switch (frameId.hashCode()) {
            case 66913:
                if (frameId.equals("COM")) {
                    c = 0;
                    break;
                }
                break;
            case 79210:
                if (frameId.equals("PIC")) {
                    c = 1;
                    break;
                }
                break;
            case 82815:
                if (frameId.equals("TAL")) {
                    c = 2;
                    break;
                }
                break;
            case 82878:
                if (frameId.equals("TCM")) {
                    c = 3;
                    break;
                }
                break;
            case 82880:
                if (frameId.equals("TCO")) {
                    c = 4;
                    break;
                }
                break;
            case 82881:
                if (frameId.equals("TCP")) {
                    c = 5;
                    break;
                }
                break;
            case 82883:
                if (frameId.equals("TCR")) {
                    c = 6;
                    break;
                }
                break;
            case 83149:
                if (frameId.equals("TLE")) {
                    c = 7;
                    break;
                }
                break;
            case 83253:
                if (frameId.equals("TP1")) {
                    c = 8;
                    break;
                }
                break;
            case 83254:
                if (frameId.equals("TP2")) {
                    c = 9;
                    break;
                }
                break;
            case 83269:
                if (frameId.equals("TPA")) {
                    c = 10;
                    break;
                }
                break;
            case 83341:
                if (frameId.equals("TRK")) {
                    c = 11;
                    break;
                }
                break;
            case 83377:
                if (frameId.equals("TT1")) {
                    c = 12;
                    break;
                }
                break;
            case 83378:
                if (frameId.equals("TT2")) {
                    c = 13;
                    break;
                }
                break;
            case 83552:
                if (frameId.equals("TYE")) {
                    c = 14;
                    break;
                }
                break;
            case 84125:
                if (frameId.equals("ULT")) {
                    c = 15;
                    break;
                }
                break;
            case 2015625:
                if (frameId.equals("APIC")) {
                    c = 16;
                    break;
                }
                break;
            case 2074380:
                if (frameId.equals("COMM")) {
                    c = 17;
                    break;
                }
                break;
            case 2567331:
                if (frameId.equals("TALB")) {
                    c = 18;
                    break;
                }
                break;
            case 2569298:
                if (frameId.equals("TCMP")) {
                    c = 19;
                    break;
                }
                break;
            case 2569357:
                if (frameId.equals("TCOM")) {
                    c = 20;
                    break;
                }
                break;
            case 2569358:
                if (frameId.equals("TCON")) {
                    c = 21;
                    break;
                }
                break;
            case 2569360:
                if (frameId.equals("TCOP")) {
                    c = 22;
                    break;
                }
                break;
            case 2570401:
                if (frameId.equals("TDRC")) {
                    c = 23;
                    break;
                }
                break;
            case 2575250:
                if (frameId.equals("TIT1")) {
                    c = 24;
                    break;
                }
                break;
            case 2575251:
                if (frameId.equals("TIT2")) {
                    c = 25;
                    break;
                }
                break;
            case 2577697:
                if (frameId.equals("TLEN")) {
                    c = 26;
                    break;
                }
                break;
            case 2581512:
                if (frameId.equals("TPE1")) {
                    c = 27;
                    break;
                }
                break;
            case 2581513:
                if (frameId.equals("TPE2")) {
                    c = 28;
                    break;
                }
                break;
            case 2581856:
                if (frameId.equals("TPOS")) {
                    c = 29;
                    break;
                }
                break;
            case 2583398:
                if (frameId.equals("TRCK")) {
                    c = 30;
                    break;
                }
                break;
            case 2590194:
                if (frameId.equals("TYER")) {
                    c = 31;
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
            case 17:
                CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame = parseCommentOrUnsynchronizedLyricsFrame(iD3v2FrameBody);
                if (this.comment == null || (str = parseCommentOrUnsynchronizedLyricsFrame.description) == null || "".equals(str)) {
                    this.comment = parseCommentOrUnsynchronizedLyricsFrame.text;
                    return;
                }
                return;
            case 1:
            case 16:
                if (this.cover == null || this.coverPictureType != 3) {
                    AttachedPicture parseAttachedPictureFrame = parseAttachedPictureFrame(iD3v2FrameBody);
                    if (this.cover == null || (b = parseAttachedPictureFrame.type) == 3 || b == 0) {
                        try {
                            byte[] bArr = parseAttachedPictureFrame.imageData;
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            options.inSampleSize = 1;
                            BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
                            int i2 = options.outWidth;
                            if (i2 > 800 || options.outHeight > 800) {
                                for (int max = Math.max(i2, options.outHeight); max > 800; max /= 2) {
                                    options.inSampleSize *= 2;
                                }
                            }
                            options.inJustDecodeBounds = false;
                            Bitmap decodeByteArray = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
                            this.cover = decodeByteArray;
                            if (decodeByteArray != null) {
                                float max2 = ((float) Math.max(decodeByteArray.getWidth(), this.cover.getHeight())) / 120.0f;
                                if (max2 > 0.0f) {
                                    Bitmap bitmap = this.cover;
                                    this.smallCover = Bitmap.createScaledBitmap(bitmap, (int) (((float) bitmap.getWidth()) / max2), (int) (((float) this.cover.getHeight()) / max2), true);
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
            case 18:
                this.album = parseTextFrame(iD3v2FrameBody);
                return;
            case 3:
            case 20:
                this.composer = parseTextFrame(iD3v2FrameBody);
                return;
            case 4:
            case 21:
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
            case 5:
            case 19:
                this.compilation = "1".equals(parseTextFrame(iD3v2FrameBody));
                return;
            case 6:
            case 22:
                this.copyright = parseTextFrame(iD3v2FrameBody);
                return;
            case 7:
            case 26:
                String parseTextFrame2 = parseTextFrame(iD3v2FrameBody);
                try {
                    this.duration = Long.valueOf(parseTextFrame2).longValue();
                    return;
                } catch (NumberFormatException unused2) {
                    Logger logger2 = LOGGER;
                    if (logger2.isLoggable(this.debugLevel)) {
                        logger2.log(this.debugLevel, "Could not parse track duration: " + parseTextFrame2);
                        return;
                    }
                    return;
                }
            case 8:
            case 27:
                this.artist = parseTextFrame(iD3v2FrameBody);
                return;
            case 9:
            case 28:
                this.albumArtist = parseTextFrame(iD3v2FrameBody);
                return;
            case 10:
            case 29:
                String parseTextFrame3 = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame3.length() > 0) {
                    int indexOf2 = parseTextFrame3.indexOf(47);
                    if (indexOf2 < 0) {
                        try {
                            this.disc = Short.valueOf(parseTextFrame3).shortValue();
                            return;
                        } catch (NumberFormatException unused3) {
                            Logger logger3 = LOGGER;
                            if (logger3.isLoggable(this.debugLevel)) {
                                logger3.log(this.debugLevel, "Could not parse disc number: " + parseTextFrame3);
                                return;
                            }
                            return;
                        }
                    } else {
                        try {
                            this.disc = Short.valueOf(parseTextFrame3.substring(0, indexOf2)).shortValue();
                        } catch (NumberFormatException unused4) {
                            Logger logger4 = LOGGER;
                            if (logger4.isLoggable(this.debugLevel)) {
                                logger4.log(this.debugLevel, "Could not parse disc number: " + parseTextFrame3);
                            }
                        }
                        try {
                            this.discs = Short.valueOf(parseTextFrame3.substring(indexOf2 + 1)).shortValue();
                            return;
                        } catch (NumberFormatException unused5) {
                            Logger logger5 = LOGGER;
                            if (logger5.isLoggable(this.debugLevel)) {
                                logger5.log(this.debugLevel, "Could not parse number of discs: " + parseTextFrame3);
                                return;
                            }
                            return;
                        }
                    }
                } else {
                    return;
                }
            case 11:
            case 30:
                String parseTextFrame4 = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame4.length() > 0) {
                    int indexOf3 = parseTextFrame4.indexOf(47);
                    if (indexOf3 < 0) {
                        try {
                            this.track = Short.valueOf(parseTextFrame4).shortValue();
                            return;
                        } catch (NumberFormatException unused6) {
                            Logger logger6 = LOGGER;
                            if (logger6.isLoggable(this.debugLevel)) {
                                logger6.log(this.debugLevel, "Could not parse track number: " + parseTextFrame4);
                                return;
                            }
                            return;
                        }
                    } else {
                        try {
                            this.track = Short.valueOf(parseTextFrame4.substring(0, indexOf3)).shortValue();
                        } catch (NumberFormatException unused7) {
                            Logger logger7 = LOGGER;
                            if (logger7.isLoggable(this.debugLevel)) {
                                logger7.log(this.debugLevel, "Could not parse track number: " + parseTextFrame4);
                            }
                        }
                        try {
                            this.tracks = Short.valueOf(parseTextFrame4.substring(indexOf3 + 1)).shortValue();
                            return;
                        } catch (NumberFormatException unused8) {
                            Logger logger8 = LOGGER;
                            if (logger8.isLoggable(this.debugLevel)) {
                                logger8.log(this.debugLevel, "Could not parse number of tracks: " + parseTextFrame4);
                                return;
                            }
                            return;
                        }
                    }
                } else {
                    return;
                }
            case 12:
            case 24:
                this.grouping = parseTextFrame(iD3v2FrameBody);
                return;
            case 13:
            case 25:
                this.title = parseTextFrame(iD3v2FrameBody);
                return;
            case 14:
            case 31:
                String parseTextFrame5 = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame5.length() > 0) {
                    try {
                        this.year = Short.valueOf(parseTextFrame5).shortValue();
                        return;
                    } catch (NumberFormatException unused9) {
                        Logger logger9 = LOGGER;
                        if (logger9.isLoggable(this.debugLevel)) {
                            logger9.log(this.debugLevel, "Could not parse year: " + parseTextFrame5);
                            return;
                        }
                        return;
                    }
                } else {
                    return;
                }
            case 15:
            case ' ':
                if (this.lyrics == null) {
                    this.lyrics = parseCommentOrUnsynchronizedLyricsFrame(iD3v2FrameBody).text;
                    return;
                }
                return;
            case 23:
                String parseTextFrame6 = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame6.length() >= 4) {
                    try {
                        this.year = Short.valueOf(parseTextFrame6.substring(0, 4)).shortValue();
                        return;
                    } catch (NumberFormatException unused10) {
                        Logger logger10 = LOGGER;
                        if (logger10.isLoggable(this.debugLevel)) {
                            logger10.log(this.debugLevel, "Could not parse year from: " + parseTextFrame6);
                            return;
                        }
                        return;
                    }
                } else {
                    return;
                }
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
            upperCase.hashCode();
            str = !upperCase.equals("JPG") ? !upperCase.equals("PNG") ? "image/unknown" : "image/png" : "image/jpeg";
        } else {
            str = iD3v2FrameBody.readZeroTerminatedString(20, ID3v2Encoding.ISO_8859_1);
        }
        return new AttachedPicture(iD3v2FrameBody.getData().readByte(), iD3v2FrameBody.readZeroTerminatedString(200, readEncoding), str, iD3v2FrameBody.getData().readFully((int) iD3v2FrameBody.getRemainingLength()));
    }
}
