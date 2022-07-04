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
        static final byte TYPE_COVER_FRONT = 3;
        static final byte TYPE_OTHER = 0;
        final String description;
        final byte[] imageData;
        final String imageType;
        final byte type;

        public AttachedPicture(byte type2, String description2, String imageType2, byte[] imageData2) {
            this.type = type2;
            this.description = description2;
            this.imageType = imageType2;
            this.imageData = imageData2;
        }
    }

    static class CommentOrUnsynchronizedLyrics {
        final String description;
        final String language;
        final String text;

        public CommentOrUnsynchronizedLyrics(String language2, String description2, String text2) {
            this.language = language2;
            this.description = description2;
            this.text = text2;
        }
    }

    public static boolean isID3v2StartPosition(InputStream input) throws IOException {
        input.mark(3);
        try {
            return input.read() == 73 && input.read() == 68 && input.read() == 51;
        } finally {
            input.reset();
        }
    }

    public ID3v2Info(InputStream input) throws IOException, ID3v2Exception {
        this(input, Level.FINEST);
    }

    public ID3v2Info(InputStream input, Level debugLevel2) throws IOException, ID3v2Exception {
        ID3v2FrameBody frameBody;
        ID3v2DataInput data;
        long remainingLength;
        this.debugLevel = debugLevel2;
        if (isID3v2StartPosition(input)) {
            ID3v2TagHeader tagHeader = new ID3v2TagHeader(input);
            this.brand = "ID3";
            this.version = String.format("2.%d.%d", new Object[]{Integer.valueOf(tagHeader.getVersion()), Integer.valueOf(tagHeader.getRevision())});
            ID3v2TagBody tagBody = tagHeader.tagBody(input);
            while (true) {
                try {
                    if (tagBody.getRemainingLength() <= 10) {
                        break;
                    }
                    ID3v2FrameHeader frameHeader = new ID3v2FrameHeader(tagBody);
                    if (frameHeader.isPadding()) {
                        break;
                    } else if (((long) frameHeader.getBodySize()) > tagBody.getRemainingLength()) {
                        Logger logger = LOGGER;
                        if (logger.isLoggable(debugLevel2)) {
                            logger.log(debugLevel2, "ID3 frame claims to extend frames area");
                        }
                    } else if (!frameHeader.isValid() || frameHeader.isEncryption()) {
                        tagBody.getData().skipFully((long) frameHeader.getBodySize());
                    } else {
                        frameBody = tagBody.frameBody(frameHeader);
                        try {
                            parseFrame(frameBody);
                            data = frameBody.getData();
                            remainingLength = frameBody.getRemainingLength();
                        } catch (ID3v2Exception e) {
                            if (LOGGER.isLoggable(debugLevel2)) {
                                LOGGER.log(debugLevel2, String.format("ID3 exception occured in frame %s: %s", new Object[]{frameHeader.getFrameId(), e.getMessage()}));
                            }
                            data = frameBody.getData();
                            remainingLength = frameBody.getRemainingLength();
                        }
                        data.skipFully(remainingLength);
                    }
                } catch (ID3v2Exception e2) {
                    Logger logger2 = LOGGER;
                    if (logger2.isLoggable(debugLevel2)) {
                        logger2.log(debugLevel2, "ID3 exception occured: " + e2.getMessage());
                    }
                } catch (Throwable th) {
                    frameBody.getData().skipFully(frameBody.getRemainingLength());
                    throw th;
                }
            }
            tagBody.getData().skipFully(tagBody.getRemainingLength());
            if (tagHeader.getFooterSize() > 0) {
                input.skip((long) tagHeader.getFooterSize());
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void parseFrame(ID3v2FrameBody frame) throws IOException, ID3v2Exception {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, "Parsing frame: " + frame.getFrameHeader().getFrameId());
        }
        String frameId = frame.getFrameHeader().getFrameId();
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
                    AttachedPicture picture = parseAttachedPictureFrame(frame);
                    if (this.cover == null || picture.type == 3 || picture.type == 0) {
                        try {
                            byte[] bytes = picture.imageData;
                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            opts.inJustDecodeBounds = true;
                            opts.inSampleSize = 1;
                            BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                            if (opts.outWidth > 800 || opts.outHeight > 800) {
                                for (int size = Math.max(opts.outWidth, opts.outHeight); size > 800; size /= 2) {
                                    opts.inSampleSize *= 2;
                                }
                            }
                            opts.inJustDecodeBounds = false;
                            this.cover = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
                            if (this.cover != null) {
                                float scale = ((float) Math.max(this.cover.getWidth(), this.cover.getHeight())) / 120.0f;
                                if (scale > 0.0f) {
                                    this.smallCover = Bitmap.createScaledBitmap(this.cover, (int) (((float) this.cover.getWidth()) / scale), (int) (((float) this.cover.getHeight()) / scale), true);
                                } else {
                                    this.smallCover = this.cover;
                                }
                                if (this.smallCover == null) {
                                    this.smallCover = this.cover;
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        this.coverPictureType = picture.type;
                        return;
                    }
                    return;
                }
                return;
            case 2:
            case 3:
                CommentOrUnsynchronizedLyrics comm = parseCommentOrUnsynchronizedLyricsFrame(frame);
                if (this.comment == null || comm.description == null || "".equals(comm.description)) {
                    this.comment = comm.text;
                    return;
                }
                return;
            case 4:
            case 5:
                this.album = parseTextFrame(frame);
                return;
            case 6:
            case 7:
                this.compilation = "1".equals(parseTextFrame(frame));
                return;
            case 8:
            case 9:
                this.composer = parseTextFrame(frame);
                return;
            case 10:
            case 11:
                String tcon = parseTextFrame(frame);
                if (tcon.length() > 0) {
                    this.genre = tcon;
                    ID3v1Genre id3v1Genre = null;
                    try {
                        if (tcon.charAt(0) == '(') {
                            int pos = tcon.indexOf(41);
                            if (pos > 1 && (id3v1Genre = ID3v1Genre.getGenre(Integer.parseInt(tcon.substring(1, pos)))) == null && tcon.length() > pos + 1) {
                                this.genre = tcon.substring(pos + 1);
                            }
                        } else {
                            id3v1Genre = ID3v1Genre.getGenre(Integer.parseInt(tcon));
                        }
                        if (id3v1Genre != null) {
                            this.genre = id3v1Genre.getDescription();
                            return;
                        }
                        return;
                    } catch (NumberFormatException e2) {
                        return;
                    }
                } else {
                    return;
                }
            case 12:
            case 13:
                this.copyright = parseTextFrame(frame);
                return;
            case 14:
                String tdrc = parseTextFrame(frame);
                if (tdrc.length() >= 4) {
                    try {
                        this.year = Short.valueOf(tdrc.substring(0, 4)).shortValue();
                        return;
                    } catch (NumberFormatException e3) {
                        Logger logger2 = LOGGER;
                        if (logger2.isLoggable(this.debugLevel)) {
                            logger2.log(this.debugLevel, "Could not parse year from: " + tdrc);
                            return;
                        }
                        return;
                    }
                } else {
                    return;
                }
            case 15:
            case 16:
                String tlen = parseTextFrame(frame);
                try {
                    this.duration = Long.valueOf(tlen).longValue();
                    return;
                } catch (NumberFormatException e4) {
                    Logger logger3 = LOGGER;
                    if (logger3.isLoggable(this.debugLevel)) {
                        logger3.log(this.debugLevel, "Could not parse track duration: " + tlen);
                        return;
                    }
                    return;
                }
            case 17:
            case 18:
                this.artist = parseTextFrame(frame);
                return;
            case 19:
            case 20:
                this.albumArtist = parseTextFrame(frame);
                return;
            case 21:
            case 22:
                String trck = parseTextFrame(frame);
                if (trck.length() > 0) {
                    int index = trck.indexOf(47);
                    if (index < 0) {
                        try {
                            this.disc = Short.valueOf(trck).shortValue();
                            return;
                        } catch (NumberFormatException e5) {
                            Logger logger4 = LOGGER;
                            if (logger4.isLoggable(this.debugLevel)) {
                                logger4.log(this.debugLevel, "Could not parse disc number: " + trck);
                                return;
                            }
                            return;
                        }
                    } else {
                        try {
                            this.disc = Short.valueOf(trck.substring(0, index)).shortValue();
                        } catch (NumberFormatException e6) {
                            Logger logger5 = LOGGER;
                            if (logger5.isLoggable(this.debugLevel)) {
                                logger5.log(this.debugLevel, "Could not parse disc number: " + trck);
                            }
                        }
                        try {
                            this.discs = Short.valueOf(trck.substring(index + 1)).shortValue();
                            return;
                        } catch (NumberFormatException e7) {
                            Logger logger6 = LOGGER;
                            if (logger6.isLoggable(this.debugLevel)) {
                                logger6.log(this.debugLevel, "Could not parse number of discs: " + trck);
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
                String trck2 = parseTextFrame(frame);
                if (trck2.length() > 0) {
                    int index2 = trck2.indexOf(47);
                    if (index2 < 0) {
                        try {
                            this.track = Short.valueOf(trck2).shortValue();
                            return;
                        } catch (NumberFormatException e8) {
                            Logger logger7 = LOGGER;
                            if (logger7.isLoggable(this.debugLevel)) {
                                logger7.log(this.debugLevel, "Could not parse track number: " + trck2);
                                return;
                            }
                            return;
                        }
                    } else {
                        try {
                            this.track = Short.valueOf(trck2.substring(0, index2)).shortValue();
                        } catch (NumberFormatException e9) {
                            Logger logger8 = LOGGER;
                            if (logger8.isLoggable(this.debugLevel)) {
                                logger8.log(this.debugLevel, "Could not parse track number: " + trck2);
                            }
                        }
                        try {
                            this.tracks = Short.valueOf(trck2.substring(index2 + 1)).shortValue();
                            return;
                        } catch (NumberFormatException e10) {
                            Logger logger9 = LOGGER;
                            if (logger9.isLoggable(this.debugLevel)) {
                                logger9.log(this.debugLevel, "Could not parse number of tracks: " + trck2);
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
                this.grouping = parseTextFrame(frame);
                return;
            case 27:
            case 28:
                this.title = parseTextFrame(frame);
                return;
            case 29:
            case 30:
                String tyer = parseTextFrame(frame);
                if (tyer.length() > 0) {
                    try {
                        this.year = Short.valueOf(tyer).shortValue();
                        return;
                    } catch (NumberFormatException e11) {
                        Logger logger10 = LOGGER;
                        if (logger10.isLoggable(this.debugLevel)) {
                            logger10.log(this.debugLevel, "Could not parse year: " + tyer);
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
                    this.lyrics = parseCommentOrUnsynchronizedLyricsFrame(frame).text;
                    return;
                }
                return;
            default:
                return;
        }
    }

    /* access modifiers changed from: package-private */
    public String parseTextFrame(ID3v2FrameBody frame) throws IOException, ID3v2Exception {
        return frame.readFixedLengthString((int) frame.getRemainingLength(), frame.readEncoding());
    }

    /* access modifiers changed from: package-private */
    public CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame(ID3v2FrameBody data) throws IOException, ID3v2Exception {
        ID3v2Encoding encoding = data.readEncoding();
        return new CommentOrUnsynchronizedLyrics(data.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1), data.readZeroTerminatedString(200, encoding), data.readFixedLengthString((int) data.getRemainingLength(), encoding));
    }

    /* access modifiers changed from: package-private */
    public AttachedPicture parseAttachedPictureFrame(ID3v2FrameBody data) throws IOException, ID3v2Exception {
        String imageType;
        ID3v2Encoding encoding = data.readEncoding();
        if (data.getTagHeader().getVersion() == 2) {
            String upperCase = data.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1).toUpperCase();
            char c = 65535;
            switch (upperCase.hashCode()) {
                case 73665:
                    if (upperCase.equals("JPG")) {
                        c = 1;
                        break;
                    }
                    break;
                case 79369:
                    if (upperCase.equals("PNG")) {
                        c = 0;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    imageType = "image/png";
                    break;
                case 1:
                    imageType = "image/jpeg";
                    break;
                default:
                    imageType = "image/unknown";
                    break;
            }
        } else {
            imageType = data.readZeroTerminatedString(20, ID3v2Encoding.ISO_8859_1);
        }
        return new AttachedPicture(data.getData().readByte(), data.readZeroTerminatedString(200, encoding), imageType, data.getData().readFully((int) data.getRemainingLength()));
    }
}
