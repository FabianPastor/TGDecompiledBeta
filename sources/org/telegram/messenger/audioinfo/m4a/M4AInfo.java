package org.telegram.messenger.audioinfo.m4a;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.audioinfo.mp3.ID3v1Genre;

public class M4AInfo extends AudioInfo {
    static final Logger LOGGER = Logger.getLogger(M4AInfo.class.getName());
    private final Level debugLevel;

    public M4AInfo(InputStream inputStream) throws IOException {
        this(inputStream, Level.FINEST);
    }

    public M4AInfo(InputStream inputStream, Level level) throws IOException {
        this.debugLevel = level;
        MP4Input mP4Input = new MP4Input(inputStream);
        if (LOGGER.isLoggable(level)) {
            LOGGER.log(level, mP4Input.toString());
        }
        ftyp(mP4Input.nextChild("ftyp"));
        moov(mP4Input.nextChildUpTo("moov"));
    }

    /* access modifiers changed from: package-private */
    public void ftyp(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        String trim = mP4Atom.readString(4, "ISO8859_1").trim();
        this.brand = trim;
        if (trim.matches("M4V|MP4|mp42|isom")) {
            Logger logger = LOGGER;
            logger.warning(mP4Atom.getPath() + ": brand=" + this.brand + " (experimental)");
        } else if (!this.brand.matches("M4A|M4P")) {
            Logger logger2 = LOGGER;
            logger2.warning(mP4Atom.getPath() + ": brand=" + this.brand + " (expected M4A or M4P)");
        }
        String.valueOf(mP4Atom.readInt());
    }

    /* access modifiers changed from: package-private */
    public void moov(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        while (mP4Atom.hasMoreChildren()) {
            MP4Atom nextChild = mP4Atom.nextChild();
            String type = nextChild.getType();
            char c = 65535;
            int hashCode = type.hashCode();
            if (hashCode != 3363941) {
                if (hashCode != 3568424) {
                    if (hashCode == 3585340 && type.equals("udta")) {
                        c = 2;
                    }
                } else if (type.equals("trak")) {
                    c = 1;
                }
            } else if (type.equals("mvhd")) {
                c = 0;
            }
            if (c == 0) {
                mvhd(nextChild);
            } else if (c == 1) {
                trak(nextChild);
            } else if (c == 2) {
                udta(nextChild);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void mvhd(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        byte readByte = mP4Atom.readByte();
        mP4Atom.skip(3);
        mP4Atom.skip(readByte == 1 ? 16 : 8);
        int readInt = mP4Atom.readInt();
        long readLong = readByte == 1 ? mP4Atom.readLong() : (long) mP4Atom.readInt();
        if (this.duration == 0) {
            this.duration = (readLong * 1000) / ((long) readInt);
        } else if (LOGGER.isLoggable(this.debugLevel)) {
            long j = (readLong * 1000) / ((long) readInt);
            if (Math.abs(this.duration - j) > 2) {
                Logger logger = LOGGER;
                Level level = this.debugLevel;
                logger.log(level, "mvhd: duration " + this.duration + " -> " + j);
            }
        }
        mP4Atom.readIntegerFixedPoint();
        mP4Atom.readShortFixedPoint();
    }

    /* access modifiers changed from: package-private */
    public void trak(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        mdia(mP4Atom.nextChildUpTo("mdia"));
    }

    /* access modifiers changed from: package-private */
    public void mdia(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        mdhd(mP4Atom.nextChild("mdhd"));
    }

    /* access modifiers changed from: package-private */
    public void mdhd(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        byte readByte = mP4Atom.readByte();
        mP4Atom.skip(3);
        mP4Atom.skip(readByte == 1 ? 16 : 8);
        int readInt = mP4Atom.readInt();
        long readLong = readByte == 1 ? mP4Atom.readLong() : (long) mP4Atom.readInt();
        if (this.duration == 0) {
            this.duration = (readLong * 1000) / ((long) readInt);
        } else if (LOGGER.isLoggable(this.debugLevel)) {
            long j = (readLong * 1000) / ((long) readInt);
            if (Math.abs(this.duration - j) > 2) {
                Logger logger = LOGGER;
                Level level = this.debugLevel;
                logger.log(level, "mdhd: duration " + this.duration + " -> " + j);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void udta(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        while (mP4Atom.hasMoreChildren()) {
            MP4Atom nextChild = mP4Atom.nextChild();
            if ("meta".equals(nextChild.getType())) {
                meta(nextChild);
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void meta(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        mP4Atom.skip(4);
        while (mP4Atom.hasMoreChildren()) {
            MP4Atom nextChild = mP4Atom.nextChild();
            if ("ilst".equals(nextChild.getType())) {
                ilst(nextChild);
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void ilst(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        while (mP4Atom.hasMoreChildren()) {
            MP4Atom nextChild = mP4Atom.nextChild();
            if (LOGGER.isLoggable(this.debugLevel)) {
                LOGGER.log(this.debugLevel, nextChild.toString());
            }
            if (nextChild.getRemaining() != 0) {
                data(nextChild.nextChildUpTo("data"));
            } else if (LOGGER.isLoggable(this.debugLevel)) {
                Logger logger = LOGGER;
                Level level = this.debugLevel;
                logger.log(level, nextChild.getPath() + ": contains no value");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void data(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        mP4Atom.skip(4);
        mP4Atom.skip(4);
        String type = mP4Atom.getParent().getType();
        char c = 65535;
        switch (type.hashCode()) {
            case 2954818:
                if (type.equals("aART")) {
                    c = 1;
                    break;
                }
                break;
            case 3059752:
                if (type.equals("covr")) {
                    c = 6;
                    break;
                }
                break;
            case 3060304:
                if (type.equals("cpil")) {
                    c = 7;
                    break;
                }
                break;
            case 3060591:
                if (type.equals("cprt")) {
                    c = 8;
                    break;
                }
                break;
            case 3083677:
                if (type.equals("disk")) {
                    c = 11;
                    break;
                }
                break;
            case 3177818:
                if (type.equals("gnre")) {
                    c = 12;
                    break;
                }
                break;
            case 3511163:
                if (type.equals("rtng")) {
                    c = 17;
                    break;
                }
                break;
            case 3564088:
                if (type.equals("tmpo")) {
                    c = 18;
                    break;
                }
                break;
            case 3568737:
                if (type.equals("trkn")) {
                    c = 19;
                    break;
                }
                break;
            case 5099770:
                if (type.equals("©ART")) {
                    c = 2;
                    break;
                }
                break;
            case 5131342:
                if (type.equals("©alb")) {
                    c = 0;
                    break;
                }
                break;
            case 5133313:
                if (type.equals("©cmt")) {
                    c = 3;
                    break;
                }
                break;
            case 5133368:
                if (type.equals("©com")) {
                    c = 4;
                    break;
                }
                break;
            case 5133411:
                if (type.equals("©cpy")) {
                    c = 9;
                    break;
                }
                break;
            case 5133907:
                if (type.equals("©day")) {
                    c = 10;
                    break;
                }
                break;
            case 5136903:
                if (type.equals("©gen")) {
                    c = 13;
                    break;
                }
                break;
            case 5137308:
                if (type.equals("©grp")) {
                    c = 14;
                    break;
                }
                break;
            case 5142332:
                if (type.equals("©lyr")) {
                    c = 15;
                    break;
                }
                break;
            case 5143505:
                if (type.equals("©nam")) {
                    c = 16;
                    break;
                }
                break;
            case 5152688:
                if (type.equals("©wrt")) {
                    c = 5;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                this.album = mP4Atom.readString("UTF-8");
                return;
            case 1:
                this.albumArtist = mP4Atom.readString("UTF-8");
                return;
            case 2:
                this.artist = mP4Atom.readString("UTF-8");
                return;
            case 3:
                this.comment = mP4Atom.readString("UTF-8");
                return;
            case 4:
            case 5:
                String str = this.composer;
                if (str == null || str.trim().length() == 0) {
                    this.composer = mP4Atom.readString("UTF-8");
                    return;
                }
                return;
            case 6:
                try {
                    byte[] readBytes = mP4Atom.readBytes();
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    options.inSampleSize = 1;
                    BitmapFactory.decodeByteArray(readBytes, 0, readBytes.length, options);
                    if (options.outWidth > 800 || options.outHeight > 800) {
                        for (int max = Math.max(options.outWidth, options.outHeight); max > 800; max /= 2) {
                            options.inSampleSize *= 2;
                        }
                    }
                    options.inJustDecodeBounds = false;
                    Bitmap decodeByteArray = BitmapFactory.decodeByteArray(readBytes, 0, readBytes.length, options);
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
                            return;
                        }
                        return;
                    }
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            case 7:
                this.compilation = mP4Atom.readBoolean();
                return;
            case 8:
            case 9:
                String str2 = this.copyright;
                if (str2 == null || str2.trim().length() == 0) {
                    this.copyright = mP4Atom.readString("UTF-8");
                    return;
                }
                return;
            case 10:
                String trim = mP4Atom.readString("UTF-8").trim();
                if (trim.length() >= 4) {
                    try {
                        this.year = Short.valueOf(trim.substring(0, 4)).shortValue();
                        return;
                    } catch (NumberFormatException unused) {
                        return;
                    }
                } else {
                    return;
                }
            case 11:
                mP4Atom.skip(2);
                this.disc = mP4Atom.readShort();
                this.discs = mP4Atom.readShort();
                return;
            case 12:
                String str3 = this.genre;
                if (str3 != null && str3.trim().length() != 0) {
                    return;
                }
                if (mP4Atom.getRemaining() == 2) {
                    ID3v1Genre genre = ID3v1Genre.getGenre(mP4Atom.readShort() - 1);
                    if (genre != null) {
                        this.genre = genre.getDescription();
                        return;
                    }
                    return;
                }
                this.genre = mP4Atom.readString("UTF-8");
                return;
            case 13:
                String str4 = this.genre;
                if (str4 == null || str4.trim().length() == 0) {
                    this.genre = mP4Atom.readString("UTF-8");
                    return;
                }
                return;
            case 14:
                this.grouping = mP4Atom.readString("UTF-8");
                return;
            case 15:
                this.lyrics = mP4Atom.readString("UTF-8");
                return;
            case 16:
                this.title = mP4Atom.readString("UTF-8");
                return;
            case 17:
                mP4Atom.readByte();
                return;
            case 18:
                mP4Atom.readShort();
                return;
            case 19:
                mP4Atom.skip(2);
                this.track = mP4Atom.readShort();
                this.tracks = mP4Atom.readShort();
                return;
            default:
                return;
        }
    }
}
