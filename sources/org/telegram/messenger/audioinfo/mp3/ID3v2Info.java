package org.telegram.messenger.audioinfo.mp3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
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
        static final byte TYPE_COVER_FRONT = (byte) 3;
        static final byte TYPE_OTHER = (byte) 0;
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
            boolean z = inputStream.read() == 73 && inputStream.read() == 68 && inputStream.read() == 51;
            inputStream.reset();
            return z;
        } catch (Throwable th) {
            inputStream.reset();
        }
    }

    public ID3v2Info(InputStream inputStream) throws IOException, ID3v2Exception {
        this(inputStream, Level.FINEST);
    }

    public ID3v2Info(InputStream inputStream, Level level) throws IOException, ID3v2Exception {
        this.debugLevel = level;
        if (isID3v2StartPosition(inputStream)) {
            ID3v2TagHeader iD3v2TagHeader = new ID3v2TagHeader(inputStream);
            this.brand = "ID3";
            this.version = String.format("2.%d.%d", new Object[]{Integer.valueOf(iD3v2TagHeader.getVersion()), Integer.valueOf(iD3v2TagHeader.getRevision())});
            ID3v2TagBody tagBody = iD3v2TagHeader.tagBody(inputStream);
            while (tagBody.getRemainingLength() > 10) {
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
                    ID3v2DataInput data;
                    long remainingLength;
                    ID3v2FrameBody frameBody = tagBody.frameBody(iD3v2FrameHeader);
                    try {
                        parseFrame(frameBody);
                        data = frameBody.getData();
                        remainingLength = frameBody.getRemainingLength();
                    } catch (ID3v2Exception e) {
                        try {
                            if (LOGGER.isLoggable(level)) {
                                LOGGER.log(level, String.format("ID3 exception occured in frame %s: %s", new Object[]{iD3v2FrameHeader.getFrameId(), e.getMessage()}));
                            }
                            data = frameBody.getData();
                            remainingLength = frameBody.getRemainingLength();
                        } catch (ID3v2Exception e2) {
                            if (LOGGER.isLoggable(level)) {
                                Logger logger = LOGGER;
                                StringBuilder stringBuilder = new StringBuilder();
                                stringBuilder.append("ID3 exception occured: ");
                                stringBuilder.append(e2.getMessage());
                                logger.log(level, stringBuilder.toString());
                            }
                        } catch (Throwable th) {
                            frameBody.getData().skipFully(frameBody.getRemainingLength());
                        }
                    }
                    data.skipFully(remainingLength);
                }
            }
            tagBody.getData().skipFully(tagBody.getRemainingLength());
            if (iD3v2TagHeader.getFooterSize() > 0) {
                inputStream.skip((long) iD3v2TagHeader.getFooterSize());
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void parseFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        Logger logger;
        Level level;
        StringBuilder stringBuilder;
        String str;
        Level level2;
        StringBuilder stringBuilder2;
        Logger logger2;
        Level level3;
        StringBuilder stringBuilder3;
        if (LOGGER.isLoggable(this.debugLevel)) {
            logger = LOGGER;
            level = this.debugLevel;
            stringBuilder = new StringBuilder();
            stringBuilder.append("Parsing frame: ");
            stringBuilder.append(iD3v2FrameBody.getFrameHeader().getFrameId());
            logger.log(level, stringBuilder.toString());
        }
        String frameId = iD3v2FrameBody.getFrameHeader().getFrameId();
        Object obj = -1;
        switch (frameId.hashCode()) {
            case 66913:
                if (frameId.equals("COM")) {
                    obj = 2;
                    break;
                }
                break;
            case 79210:
                if (frameId.equals("PIC")) {
                    obj = null;
                    break;
                }
                break;
            case 82815:
                if (frameId.equals("TAL")) {
                    obj = 4;
                    break;
                }
                break;
            case 82878:
                if (frameId.equals("TCM")) {
                    obj = 8;
                    break;
                }
                break;
            case 82880:
                if (frameId.equals("TCO")) {
                    obj = 10;
                    break;
                }
                break;
            case 82881:
                if (frameId.equals("TCP")) {
                    obj = 6;
                    break;
                }
                break;
            case 82883:
                if (frameId.equals("TCR")) {
                    obj = 12;
                    break;
                }
                break;
            case 83149:
                if (frameId.equals("TLE")) {
                    obj = 15;
                    break;
                }
                break;
            case 83253:
                if (frameId.equals("TP1")) {
                    obj = 17;
                    break;
                }
                break;
            case 83254:
                if (frameId.equals("TP2")) {
                    obj = 19;
                    break;
                }
                break;
            case 83269:
                if (frameId.equals("TPA")) {
                    obj = 21;
                    break;
                }
                break;
            case 83341:
                if (frameId.equals("TRK")) {
                    obj = 23;
                    break;
                }
                break;
            case 83377:
                if (frameId.equals("TT1")) {
                    obj = 25;
                    break;
                }
                break;
            case 83378:
                if (frameId.equals("TT2")) {
                    obj = 27;
                    break;
                }
                break;
            case 83552:
                if (frameId.equals("TYE")) {
                    obj = 29;
                    break;
                }
                break;
            case 84125:
                if (frameId.equals("ULT")) {
                    obj = 31;
                    break;
                }
                break;
            case 2015625:
                if (frameId.equals("APIC")) {
                    obj = 1;
                    break;
                }
                break;
            case 2074380:
                if (frameId.equals("COMM")) {
                    obj = 3;
                    break;
                }
                break;
            case 2567331:
                if (frameId.equals("TALB")) {
                    obj = 5;
                    break;
                }
                break;
            case 2569298:
                if (frameId.equals("TCMP")) {
                    obj = 7;
                    break;
                }
                break;
            case 2569357:
                if (frameId.equals("TCOM")) {
                    obj = 9;
                    break;
                }
                break;
            case 2569358:
                if (frameId.equals("TCON")) {
                    obj = 11;
                    break;
                }
                break;
            case 2569360:
                if (frameId.equals("TCOP")) {
                    obj = 13;
                    break;
                }
                break;
            case 2570401:
                if (frameId.equals("TDRC")) {
                    obj = 14;
                    break;
                }
                break;
            case 2575250:
                if (frameId.equals("TIT1")) {
                    obj = 26;
                    break;
                }
                break;
            case 2575251:
                if (frameId.equals("TIT2")) {
                    obj = 28;
                    break;
                }
                break;
            case 2577697:
                if (frameId.equals("TLEN")) {
                    obj = 16;
                    break;
                }
                break;
            case 2581512:
                if (frameId.equals("TPE1")) {
                    obj = 18;
                    break;
                }
                break;
            case 2581513:
                if (frameId.equals("TPE2")) {
                    obj = 20;
                    break;
                }
                break;
            case 2581856:
                if (frameId.equals("TPOS")) {
                    obj = 22;
                    break;
                }
                break;
            case 2583398:
                if (frameId.equals("TRCK")) {
                    obj = 24;
                    break;
                }
                break;
            case 2590194:
                if (frameId.equals("TYER")) {
                    obj = 30;
                    break;
                }
                break;
            case 2614438:
                if (frameId.equals("USLT")) {
                    obj = 32;
                    break;
                }
                break;
        }
        String parseTextFrame;
        int indexOf;
        switch (obj) {
            case null:
            case 1:
                if (this.cover == null || this.coverPictureType != (byte) 3) {
                    AttachedPicture parseAttachedPictureFrame = parseAttachedPictureFrame(iD3v2FrameBody);
                    if (this.cover != null) {
                        byte b = parseAttachedPictureFrame.type;
                        if (!(b == (byte) 3 || b == (byte) 0)) {
                            return;
                        }
                    }
                    try {
                        byte[] bArr = parseAttachedPictureFrame.imageData;
                        Options options = new Options();
                        options.inJustDecodeBounds = true;
                        options.inSampleSize = 1;
                        BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
                        if (options.outWidth > 800 || options.outHeight > 800) {
                            for (int max = Math.max(options.outWidth, options.outHeight); max > 800; max /= 2) {
                                options.inSampleSize *= 2;
                            }
                        }
                        options.inJustDecodeBounds = false;
                        this.cover = BitmapFactory.decodeByteArray(bArr, 0, bArr.length, options);
                        if (this.cover != null) {
                            float max2 = ((float) Math.max(this.cover.getWidth(), this.cover.getHeight())) / 120.0f;
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
            case 2:
            case 3:
                CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame = parseCommentOrUnsynchronizedLyricsFrame(iD3v2FrameBody);
                if (this.comment != null) {
                    frameId = parseCommentOrUnsynchronizedLyricsFrame.description;
                    if (!(frameId == null || "".equals(frameId))) {
                        return;
                    }
                }
                this.comment = parseCommentOrUnsynchronizedLyricsFrame.text;
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
                parseTextFrame = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame.length() > 0) {
                    this.genre = parseTextFrame;
                    ID3v1Genre iD3v1Genre = null;
                    try {
                        if (parseTextFrame.charAt(0) == '(') {
                            int indexOf2 = parseTextFrame.indexOf(41);
                            if (indexOf2 > 1) {
                                iD3v1Genre = ID3v1Genre.getGenre(Integer.parseInt(parseTextFrame.substring(1, indexOf2)));
                                if (iD3v1Genre == null) {
                                    indexOf2++;
                                    if (parseTextFrame.length() > indexOf2) {
                                        this.genre = parseTextFrame.substring(indexOf2);
                                    }
                                }
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
                }
                return;
            case 12:
            case 13:
                this.copyright = parseTextFrame(iD3v2FrameBody);
                return;
            case 14:
                parseTextFrame = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame.length() >= 4) {
                    try {
                        this.year = Short.valueOf(parseTextFrame.substring(0, 4)).shortValue();
                        return;
                    } catch (NumberFormatException unused2) {
                        if (LOGGER.isLoggable(this.debugLevel)) {
                            logger = LOGGER;
                            level = this.debugLevel;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Could not parse year from: ");
                            stringBuilder.append(parseTextFrame);
                            logger.log(level, stringBuilder.toString());
                            return;
                        }
                        return;
                    }
                }
                return;
            case 15:
            case 16:
                parseTextFrame = parseTextFrame(iD3v2FrameBody);
                try {
                    this.duration = Long.valueOf(parseTextFrame).longValue();
                    return;
                } catch (NumberFormatException unused3) {
                    if (LOGGER.isLoggable(this.debugLevel)) {
                        logger = LOGGER;
                        level = this.debugLevel;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Could not parse track duration: ");
                        stringBuilder.append(parseTextFrame);
                        logger.log(level, stringBuilder.toString());
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
                parseTextFrame = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame.length() > 0) {
                    indexOf = parseTextFrame.indexOf(47);
                    str = "Could not parse disc number: ";
                    if (indexOf < 0) {
                        try {
                            this.disc = Short.valueOf(parseTextFrame).shortValue();
                            return;
                        } catch (NumberFormatException unused4) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                logger = LOGGER;
                                level2 = this.debugLevel;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(str);
                                stringBuilder2.append(parseTextFrame);
                                logger.log(level2, stringBuilder2.toString());
                                return;
                            }
                            return;
                        }
                    }
                    try {
                        this.disc = Short.valueOf(parseTextFrame.substring(0, indexOf)).shortValue();
                    } catch (NumberFormatException unused5) {
                        if (LOGGER.isLoggable(this.debugLevel)) {
                            logger2 = LOGGER;
                            level3 = this.debugLevel;
                            stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(str);
                            stringBuilder3.append(parseTextFrame);
                            logger2.log(level3, stringBuilder3.toString());
                        }
                    }
                    try {
                        this.discs = Short.valueOf(parseTextFrame.substring(indexOf + 1)).shortValue();
                        return;
                    } catch (NumberFormatException unused6) {
                        if (LOGGER.isLoggable(this.debugLevel)) {
                            logger = LOGGER;
                            level = this.debugLevel;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Could not parse number of discs: ");
                            stringBuilder.append(parseTextFrame);
                            logger.log(level, stringBuilder.toString());
                            return;
                        }
                        return;
                    }
                }
                return;
            case 23:
            case 24:
                parseTextFrame = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame.length() > 0) {
                    indexOf = parseTextFrame.indexOf(47);
                    str = "Could not parse track number: ";
                    if (indexOf < 0) {
                        try {
                            this.track = Short.valueOf(parseTextFrame).shortValue();
                            return;
                        } catch (NumberFormatException unused7) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                logger = LOGGER;
                                level2 = this.debugLevel;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append(str);
                                stringBuilder2.append(parseTextFrame);
                                logger.log(level2, stringBuilder2.toString());
                                return;
                            }
                            return;
                        }
                    }
                    try {
                        this.track = Short.valueOf(parseTextFrame.substring(0, indexOf)).shortValue();
                    } catch (NumberFormatException unused8) {
                        if (LOGGER.isLoggable(this.debugLevel)) {
                            logger2 = LOGGER;
                            level3 = this.debugLevel;
                            stringBuilder3 = new StringBuilder();
                            stringBuilder3.append(str);
                            stringBuilder3.append(parseTextFrame);
                            logger2.log(level3, stringBuilder3.toString());
                        }
                    }
                    try {
                        this.tracks = Short.valueOf(parseTextFrame.substring(indexOf + 1)).shortValue();
                        return;
                    } catch (NumberFormatException unused9) {
                        if (LOGGER.isLoggable(this.debugLevel)) {
                            logger = LOGGER;
                            level = this.debugLevel;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Could not parse number of tracks: ");
                            stringBuilder.append(parseTextFrame);
                            logger.log(level, stringBuilder.toString());
                            return;
                        }
                        return;
                    }
                }
                return;
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
                parseTextFrame = parseTextFrame(iD3v2FrameBody);
                if (parseTextFrame.length() > 0) {
                    try {
                        this.year = Short.valueOf(parseTextFrame).shortValue();
                        return;
                    } catch (NumberFormatException unused10) {
                        if (LOGGER.isLoggable(this.debugLevel)) {
                            logger = LOGGER;
                            level = this.debugLevel;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Could not parse year: ");
                            stringBuilder.append(parseTextFrame);
                            logger.log(level, stringBuilder.toString());
                            return;
                        }
                        return;
                    }
                }
                return;
            case 31:
            case 32:
                if (this.lyrics == null) {
                    this.lyrics = parseCommentOrUnsynchronizedLyricsFrame(iD3v2FrameBody).text;
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* Access modifiers changed, original: 0000 */
    public String parseTextFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        return iD3v2FrameBody.readFixedLengthString((int) iD3v2FrameBody.getRemainingLength(), iD3v2FrameBody.readEncoding());
    }

    /* Access modifiers changed, original: 0000 */
    public CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        ID3v2Encoding readEncoding = iD3v2FrameBody.readEncoding();
        return new CommentOrUnsynchronizedLyrics(iD3v2FrameBody.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1), iD3v2FrameBody.readZeroTerminatedString(200, readEncoding), iD3v2FrameBody.readFixedLengthString((int) iD3v2FrameBody.getRemainingLength(), readEncoding));
    }

    /* Access modifiers changed, original: 0000 */
    public AttachedPicture parseAttachedPictureFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        String toUpperCase;
        ID3v2Encoding readEncoding = iD3v2FrameBody.readEncoding();
        if (iD3v2FrameBody.getTagHeader().getVersion() == 2) {
            toUpperCase = iD3v2FrameBody.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1).toUpperCase();
            int i = -1;
            int hashCode = toUpperCase.hashCode();
            if (hashCode != 73665) {
                if (hashCode == 79369 && toUpperCase.equals("PNG")) {
                    i = 0;
                }
            } else if (toUpperCase.equals("JPG")) {
                i = 1;
            }
            toUpperCase = i != 0 ? i != 1 ? "image/unknown" : "image/jpeg" : "image/png";
        } else {
            toUpperCase = iD3v2FrameBody.readZeroTerminatedString(20, ID3v2Encoding.ISO_8859_1);
        }
        return new AttachedPicture(iD3v2FrameBody.getData().readByte(), iD3v2FrameBody.readZeroTerminatedString(200, readEncoding), toUpperCase, iD3v2FrameBody.getData().readFully((int) iD3v2FrameBody.getRemainingLength()));
    }
}
