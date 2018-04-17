package org.telegram.messenger.audioinfo.mp3;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.exoplayer2.RendererCapabilities;
import org.telegram.messenger.exoplayer2.metadata.id3.ApicFrame;
import org.telegram.messenger.exoplayer2.metadata.id3.CommentFrame;
import org.telegram.messenger.support.widget.helper.ItemTouchHelper.Callback;

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

        public AttachedPicture(byte type, String description, String imageType, byte[] imageData) {
            this.type = type;
            this.description = description;
            this.imageType = imageType;
            this.imageData = imageData;
        }
    }

    static class CommentOrUnsynchronizedLyrics {
        final String description;
        final String language;
        final String text;

        public CommentOrUnsynchronizedLyrics(String language, String description, String text) {
            this.language = language;
            this.description = description;
            this.text = text;
        }
    }

    public static boolean isID3v2StartPosition(InputStream input) throws IOException {
        input.mark(3);
        try {
            boolean z = input.read() == 73 && input.read() == 68 && input.read() == 51;
            input.reset();
            return z;
        } catch (Throwable th) {
            input.reset();
        }
    }

    public ID3v2Info(InputStream input) throws IOException, ID3v2Exception {
        this(input, Level.FINEST);
    }

    public ID3v2Info(InputStream input, Level debugLevel) throws IOException, ID3v2Exception {
        this.debugLevel = debugLevel;
        if (isID3v2StartPosition(input)) {
            ID3v2TagHeader tagHeader = new ID3v2TagHeader(input);
            this.brand = "ID3";
            this.version = String.format("2.%d.%d", new Object[]{Integer.valueOf(tagHeader.getVersion()), Integer.valueOf(tagHeader.getRevision())});
            ID3v2TagBody tagBody = tagHeader.tagBody(input);
            while (tagBody.getRemainingLength() > 10) {
                ID3v2FrameHeader frameHeader = new ID3v2FrameHeader(tagBody);
                if (frameHeader.isPadding()) {
                    break;
                } else if (((long) frameHeader.getBodySize()) > tagBody.getRemainingLength()) {
                    if (LOGGER.isLoggable(debugLevel)) {
                        LOGGER.log(debugLevel, "ID3 frame claims to extend frames area");
                    }
                } else if (!frameHeader.isValid() || frameHeader.isEncryption()) {
                    tagBody.getData().skipFully((long) frameHeader.getBodySize());
                } else {
                    ID3v2DataInput e;
                    long remainingLength;
                    ID3v2FrameBody frameBody = tagBody.frameBody(frameHeader);
                    try {
                        parseFrame(frameBody);
                    } catch (ID3v2Exception e2) {
                        if (LOGGER.isLoggable(debugLevel)) {
                            LOGGER.log(debugLevel, String.format("ID3 exception occured in frame %s: %s", new Object[]{frameHeader.getFrameId(), e2.getMessage()}));
                        }
                        e = frameBody.getData();
                        remainingLength = frameBody.getRemainingLength();
                    }
                    try {
                        e = frameBody.getData();
                        remainingLength = frameBody.getRemainingLength();
                        e.skipFully(remainingLength);
                    } catch (ID3v2Exception e3) {
                        if (LOGGER.isLoggable(debugLevel)) {
                            Logger logger = LOGGER;
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("ID3 exception occured: ");
                            stringBuilder.append(e3.getMessage());
                            logger.log(debugLevel, stringBuilder.toString());
                        }
                    } catch (Throwable th) {
                        frameBody.getData().skipFully(frameBody.getRemainingLength());
                    }
                }
            }
            tagBody.getData().skipFully(tagBody.getRemainingLength());
            if (tagHeader.getFooterSize() > 0) {
                input.skip((long) tagHeader.getFooterSize());
            }
        }
    }

    void parseFrame(ID3v2FrameBody frame) throws IOException, ID3v2Exception {
        Logger logger;
        Level level;
        StringBuilder stringBuilder;
        Logger logger2;
        Level level2;
        StringBuilder stringBuilder2;
        if (LOGGER.isLoggable(this.debugLevel)) {
            Logger logger3 = LOGGER;
            Level level3 = this.debugLevel;
            StringBuilder stringBuilder3 = new StringBuilder();
            stringBuilder3.append("Parsing frame: ");
            stringBuilder3.append(frame.getFrameHeader().getFrameId());
            logger3.log(level3, stringBuilder3.toString());
        }
        String frameId = frame.getFrameHeader().getFrameId();
        boolean z = true;
        switch (frameId.hashCode()) {
            case 66913:
                if (frameId.equals("COM")) {
                    z = true;
                    break;
                }
                break;
            case 79210:
                if (frameId.equals("PIC")) {
                    z = false;
                    break;
                }
                break;
            case 82815:
                if (frameId.equals("TAL")) {
                    z = true;
                    break;
                }
                break;
            case 82878:
                if (frameId.equals("TCM")) {
                    z = true;
                    break;
                }
                break;
            case 82880:
                if (frameId.equals("TCO")) {
                    z = true;
                    break;
                }
                break;
            case 82881:
                if (frameId.equals("TCP")) {
                    z = true;
                    break;
                }
                break;
            case 82883:
                if (frameId.equals("TCR")) {
                    z = true;
                    break;
                }
                break;
            case 83149:
                if (frameId.equals("TLE")) {
                    z = true;
                    break;
                }
                break;
            case 83253:
                if (frameId.equals("TP1")) {
                    z = true;
                    break;
                }
                break;
            case 83254:
                if (frameId.equals("TP2")) {
                    z = true;
                    break;
                }
                break;
            case 83269:
                if (frameId.equals("TPA")) {
                    z = true;
                    break;
                }
                break;
            case 83341:
                if (frameId.equals("TRK")) {
                    z = true;
                    break;
                }
                break;
            case 83377:
                if (frameId.equals("TT1")) {
                    z = true;
                    break;
                }
                break;
            case 83378:
                if (frameId.equals("TT2")) {
                    z = true;
                    break;
                }
                break;
            case 83552:
                if (frameId.equals("TYE")) {
                    z = true;
                    break;
                }
                break;
            case 84125:
                if (frameId.equals("ULT")) {
                    z = true;
                    break;
                }
                break;
            case 2015625:
                if (frameId.equals(ApicFrame.ID)) {
                    z = true;
                    break;
                }
                break;
            case 2074380:
                if (frameId.equals(CommentFrame.ID)) {
                    z = true;
                    break;
                }
                break;
            case 2567331:
                if (frameId.equals("TALB")) {
                    z = true;
                    break;
                }
                break;
            case 2569298:
                if (frameId.equals("TCMP")) {
                    z = true;
                    break;
                }
                break;
            case 2569357:
                if (frameId.equals("TCOM")) {
                    z = true;
                    break;
                }
                break;
            case 2569358:
                if (frameId.equals("TCON")) {
                    z = true;
                    break;
                }
                break;
            case 2569360:
                if (frameId.equals("TCOP")) {
                    z = true;
                    break;
                }
                break;
            case 2570401:
                if (frameId.equals("TDRC")) {
                    z = true;
                    break;
                }
                break;
            case 2575250:
                if (frameId.equals("TIT1")) {
                    z = true;
                    break;
                }
                break;
            case 2575251:
                if (frameId.equals("TIT2")) {
                    z = true;
                    break;
                }
                break;
            case 2577697:
                if (frameId.equals("TLEN")) {
                    z = true;
                    break;
                }
                break;
            case 2581512:
                if (frameId.equals("TPE1")) {
                    z = true;
                    break;
                }
                break;
            case 2581513:
                if (frameId.equals("TPE2")) {
                    z = true;
                    break;
                }
                break;
            case 2581856:
                if (frameId.equals("TPOS")) {
                    z = true;
                    break;
                }
                break;
            case 2583398:
                if (frameId.equals("TRCK")) {
                    z = true;
                    break;
                }
                break;
            case 2590194:
                if (frameId.equals("TYER")) {
                    z = true;
                    break;
                }
                break;
            case 2614438:
                if (frameId.equals("USLT")) {
                    z = true;
                    break;
                }
                break;
            default:
                break;
        }
        String tpos;
        int index;
        switch (z) {
            case false:
            case true:
                if (this.cover == null || this.coverPictureType != (byte) 3) {
                    AttachedPicture picture = parseAttachedPictureFrame(frame);
                    if (this.cover == null || picture.type == (byte) 3 || picture.type == (byte) 0) {
                        try {
                            byte[] bytes = picture.imageData;
                            Options opts = new Options();
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
                    }
                    return;
                }
                return;
            case true:
            case true:
                CommentOrUnsynchronizedLyrics comm = parseCommentOrUnsynchronizedLyricsFrame(frame);
                if (this.comment == null || comm.description == null || TtmlNode.ANONYMOUS_REGION_ID.equals(comm.description)) {
                    this.comment = comm.text;
                    return;
                }
                return;
            case true:
            case true:
                this.album = parseTextFrame(frame);
                return;
            case true:
            case true:
                this.compilation = "1".equals(parseTextFrame(frame));
                return;
            case true:
            case true:
                this.composer = parseTextFrame(frame);
                return;
            case true:
            case true:
                frameId = parseTextFrame(frame);
                if (frameId.length() > 0) {
                    this.genre = frameId;
                    ID3v1Genre id3v1Genre = null;
                    try {
                        if (frameId.charAt(0) == '(') {
                            int pos = frameId.indexOf(41);
                            if (pos > 1) {
                                id3v1Genre = ID3v1Genre.getGenre(Integer.parseInt(frameId.substring(1, pos)));
                                if (id3v1Genre == null && frameId.length() > pos + 1) {
                                    this.genre = frameId.substring(pos + 1);
                                }
                            }
                        } else {
                            id3v1Genre = ID3v1Genre.getGenre(Integer.parseInt(frameId));
                        }
                        if (id3v1Genre != null) {
                            this.genre = id3v1Genre.getDescription();
                        }
                    } catch (NumberFormatException e2) {
                    }
                    return;
                }
                return;
            case true:
            case true:
                this.copyright = parseTextFrame(frame);
                return;
            case true:
                frameId = parseTextFrame(frame);
                if (frameId.length() >= 4) {
                    try {
                        this.year = Short.valueOf(frameId.substring(0, 4)).shortValue();
                    } catch (NumberFormatException e3) {
                        if (LOGGER.isLoggable(this.debugLevel)) {
                            logger = LOGGER;
                            level = this.debugLevel;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Could not parse year from: ");
                            stringBuilder.append(frameId);
                            logger.log(level, stringBuilder.toString());
                        }
                    }
                    return;
                }
                return;
            case true:
            case true:
                frameId = parseTextFrame(frame);
                try {
                    this.duration = Long.valueOf(frameId).longValue();
                    return;
                } catch (NumberFormatException e4) {
                    if (LOGGER.isLoggable(this.debugLevel)) {
                        logger = LOGGER;
                        level = this.debugLevel;
                        stringBuilder = new StringBuilder();
                        stringBuilder.append("Could not parse track duration: ");
                        stringBuilder.append(frameId);
                        logger.log(level, stringBuilder.toString());
                    }
                    return;
                }
            case true:
            case true:
                this.artist = parseTextFrame(frame);
                return;
            case true:
            case true:
                this.albumArtist = parseTextFrame(frame);
                return;
            case true:
            case true:
                tpos = parseTextFrame(frame);
                if (tpos.length() > 0) {
                    index = tpos.indexOf(47);
                    if (index < 0) {
                        try {
                            this.disc = Short.valueOf(tpos).shortValue();
                        } catch (NumberFormatException e5) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                logger2 = LOGGER;
                                level2 = this.debugLevel;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Could not parse disc number: ");
                                stringBuilder2.append(tpos);
                                logger2.log(level2, stringBuilder2.toString());
                            }
                        }
                    } else {
                        try {
                            this.disc = Short.valueOf(tpos.substring(0, index)).shortValue();
                        } catch (NumberFormatException e6) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                logger2 = LOGGER;
                                level2 = this.debugLevel;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Could not parse disc number: ");
                                stringBuilder2.append(tpos);
                                logger2.log(level2, stringBuilder2.toString());
                            }
                        }
                        try {
                            this.discs = Short.valueOf(tpos.substring(index + 1)).shortValue();
                        } catch (NumberFormatException e7) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                logger2 = LOGGER;
                                level2 = this.debugLevel;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Could not parse number of discs: ");
                                stringBuilder2.append(tpos);
                                logger2.log(level2, stringBuilder2.toString());
                            }
                        }
                    }
                    return;
                }
                return;
            case true:
            case RendererCapabilities.ADAPTIVE_SUPPORT_MASK /*24*/:
                tpos = parseTextFrame(frame);
                if (tpos.length() > 0) {
                    index = tpos.indexOf(47);
                    if (index < 0) {
                        try {
                            this.track = Short.valueOf(tpos).shortValue();
                        } catch (NumberFormatException e8) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                logger2 = LOGGER;
                                level2 = this.debugLevel;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Could not parse track number: ");
                                stringBuilder2.append(tpos);
                                logger2.log(level2, stringBuilder2.toString());
                            }
                        }
                    } else {
                        try {
                            this.track = Short.valueOf(tpos.substring(0, index)).shortValue();
                        } catch (NumberFormatException e9) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                logger2 = LOGGER;
                                level2 = this.debugLevel;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Could not parse track number: ");
                                stringBuilder2.append(tpos);
                                logger2.log(level2, stringBuilder2.toString());
                            }
                        }
                        try {
                            this.tracks = Short.valueOf(tpos.substring(index + 1)).shortValue();
                        } catch (NumberFormatException e10) {
                            if (LOGGER.isLoggable(this.debugLevel)) {
                                logger2 = LOGGER;
                                level2 = this.debugLevel;
                                stringBuilder2 = new StringBuilder();
                                stringBuilder2.append("Could not parse number of tracks: ");
                                stringBuilder2.append(tpos);
                                logger2.log(level2, stringBuilder2.toString());
                            }
                        }
                    }
                    return;
                }
                return;
            case true:
            case true:
                this.grouping = parseTextFrame(frame);
                return;
            case true:
            case true:
                this.title = parseTextFrame(frame);
                return;
            case true:
            case true:
                frameId = parseTextFrame(frame);
                if (frameId.length() > 0) {
                    try {
                        this.year = Short.valueOf(frameId).shortValue();
                    } catch (NumberFormatException e11) {
                        if (LOGGER.isLoggable(this.debugLevel)) {
                            logger = LOGGER;
                            level = this.debugLevel;
                            stringBuilder = new StringBuilder();
                            stringBuilder.append("Could not parse year: ");
                            stringBuilder.append(frameId);
                            logger.log(level, stringBuilder.toString());
                        }
                    }
                    return;
                }
                return;
            case true:
            case true:
                if (this.lyrics == null) {
                    this.lyrics = parseCommentOrUnsynchronizedLyricsFrame(frame).text;
                    return;
                }
                return;
            default:
                return;
        }
    }

    String parseTextFrame(ID3v2FrameBody frame) throws IOException, ID3v2Exception {
        return frame.readFixedLengthString((int) frame.getRemainingLength(), frame.readEncoding());
    }

    CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame(ID3v2FrameBody data) throws IOException, ID3v2Exception {
        ID3v2Encoding encoding = data.readEncoding();
        return new CommentOrUnsynchronizedLyrics(data.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1), data.readZeroTerminatedString(Callback.DEFAULT_DRAG_ANIMATION_DURATION, encoding), data.readFixedLengthString((int) data.getRemainingLength(), encoding));
    }

    AttachedPicture parseAttachedPictureFrame(ID3v2FrameBody data) throws IOException, ID3v2Exception {
        String imageType;
        ID3v2Encoding encoding = data.readEncoding();
        if (data.getTagHeader().getVersion() == 2) {
            String toUpperCase = data.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1).toUpperCase();
            Object obj = -1;
            int hashCode = toUpperCase.hashCode();
            if (hashCode != 73665) {
                if (hashCode == 79369) {
                    if (toUpperCase.equals("PNG")) {
                        obj = null;
                    }
                }
            } else if (toUpperCase.equals("JPG")) {
                obj = 1;
            }
            switch (obj) {
                case null:
                    toUpperCase = "image/png";
                    break;
                case 1:
                    toUpperCase = "image/jpeg";
                    break;
                default:
                    toUpperCase = "image/unknown";
                    break;
            }
            imageType = toUpperCase;
        } else {
            imageType = data.readZeroTerminatedString(20, ID3v2Encoding.ISO_8859_1);
        }
        return new AttachedPicture(data.getData().readByte(), data.readZeroTerminatedString(Callback.DEFAULT_DRAG_ANIMATION_DURATION, encoding), imageType, data.getData().readFully((int) data.getRemainingLength()));
    }
}
