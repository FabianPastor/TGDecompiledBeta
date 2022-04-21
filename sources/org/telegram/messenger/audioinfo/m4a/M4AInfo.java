package org.telegram.messenger.audioinfo.m4a;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.audioinfo.mp3.ID3v1Genre;

public class M4AInfo extends AudioInfo {
    private static final String ASCII = "ISO8859_1";
    static final Logger LOGGER = Logger.getLogger(M4AInfo.class.getName());
    private static final String UTF_8 = "UTF-8";
    private final Level debugLevel;
    private byte rating;
    private BigDecimal speed;
    private short tempo;
    private BigDecimal volume;

    public M4AInfo(InputStream input) throws IOException {
        this(input, Level.FINEST);
    }

    public M4AInfo(InputStream input, Level debugLevel2) throws IOException {
        this.debugLevel = debugLevel2;
        MP4Input mp4 = new MP4Input(input);
        Logger logger = LOGGER;
        if (logger.isLoggable(debugLevel2)) {
            logger.log(debugLevel2, mp4.toString());
        }
        ftyp(mp4.nextChild("ftyp"));
        moov(mp4.nextChildUpTo("moov"));
    }

    /* access modifiers changed from: package-private */
    public void ftyp(MP4Atom atom) throws IOException {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, atom.toString());
        }
        this.brand = atom.readString(4, "ISO8859_1").trim();
        if (this.brand.matches("M4V|MP4|mp42|isom")) {
            logger.warning(atom.getPath() + ": brand=" + this.brand + " (experimental)");
        } else if (!this.brand.matches("M4A|M4P")) {
            logger.warning(atom.getPath() + ": brand=" + this.brand + " (expected M4A or M4P)");
        }
        this.version = String.valueOf(atom.readInt());
    }

    /* access modifiers changed from: package-private */
    public void moov(MP4Atom atom) throws IOException {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, atom.toString());
        }
        while (atom.hasMoreChildren()) {
            MP4Atom child = atom.nextChild();
            String type = child.getType();
            char c = 65535;
            switch (type.hashCode()) {
                case 3363941:
                    if (type.equals("mvhd")) {
                        c = 0;
                        break;
                    }
                    break;
                case 3568424:
                    if (type.equals("trak")) {
                        c = 1;
                        break;
                    }
                    break;
                case 3585340:
                    if (type.equals("udta")) {
                        c = 2;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                    mvhd(child);
                    break;
                case 1:
                    trak(child);
                    break;
                case 2:
                    udta(child);
                    break;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void mvhd(MP4Atom atom) throws IOException {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, atom.toString());
        }
        byte version = atom.readByte();
        atom.skip(3);
        atom.skip(version == 1 ? 16 : 8);
        int scale = atom.readInt();
        long units = version == 1 ? atom.readLong() : (long) atom.readInt();
        if (this.duration == 0) {
            this.duration = (1000 * units) / ((long) scale);
        } else if (logger.isLoggable(this.debugLevel) && Math.abs(this.duration - ((units * 1000) / ((long) scale))) > 2) {
            Level level = this.debugLevel;
            logger.log(level, "mvhd: duration " + this.duration + " -> " + ((1000 * units) / ((long) scale)));
        }
        this.speed = atom.readIntegerFixedPoint();
        this.volume = atom.readShortFixedPoint();
    }

    /* access modifiers changed from: package-private */
    public void trak(MP4Atom atom) throws IOException {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, atom.toString());
        }
        mdia(atom.nextChildUpTo("mdia"));
    }

    /* access modifiers changed from: package-private */
    public void mdia(MP4Atom atom) throws IOException {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, atom.toString());
        }
        mdhd(atom.nextChild("mdhd"));
    }

    /* access modifiers changed from: package-private */
    public void mdhd(MP4Atom atom) throws IOException {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, atom.toString());
        }
        byte version = atom.readByte();
        atom.skip(3);
        atom.skip(version == 1 ? 16 : 8);
        int sampleRate = atom.readInt();
        long samples = version == 1 ? atom.readLong() : (long) atom.readInt();
        if (this.duration == 0) {
            this.duration = (1000 * samples) / ((long) sampleRate);
        } else if (logger.isLoggable(this.debugLevel) && Math.abs(this.duration - ((samples * 1000) / ((long) sampleRate))) > 2) {
            Level level = this.debugLevel;
            logger.log(level, "mdhd: duration " + this.duration + " -> " + ((1000 * samples) / ((long) sampleRate)));
        }
    }

    /* access modifiers changed from: package-private */
    public void udta(MP4Atom atom) throws IOException {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, atom.toString());
        }
        while (atom.hasMoreChildren()) {
            MP4Atom child = atom.nextChild();
            if ("meta".equals(child.getType())) {
                meta(child);
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void meta(MP4Atom atom) throws IOException {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, atom.toString());
        }
        atom.skip(4);
        while (atom.hasMoreChildren()) {
            MP4Atom child = atom.nextChild();
            if ("ilst".equals(child.getType())) {
                ilst(child);
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void ilst(MP4Atom atom) throws IOException {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, atom.toString());
        }
        while (atom.hasMoreChildren()) {
            MP4Atom child = atom.nextChild();
            Logger logger2 = LOGGER;
            if (logger2.isLoggable(this.debugLevel)) {
                logger2.log(this.debugLevel, child.toString());
            }
            if (child.getRemaining() != 0) {
                data(child.nextChildUpTo("data"));
            } else if (logger2.isLoggable(this.debugLevel)) {
                Level level = this.debugLevel;
                logger2.log(level, child.getPath() + ": contains no value");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void data(MP4Atom atom) throws IOException {
        Logger logger = LOGGER;
        if (logger.isLoggable(this.debugLevel)) {
            logger.log(this.debugLevel, atom.toString());
        }
        atom.skip(4);
        atom.skip(4);
        String type = atom.getParent().getType();
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
                this.album = atom.readString("UTF-8");
                return;
            case 1:
                this.albumArtist = atom.readString("UTF-8");
                return;
            case 2:
                this.artist = atom.readString("UTF-8");
                return;
            case 3:
                this.comment = atom.readString("UTF-8");
                return;
            case 4:
            case 5:
                if (this.composer == null || this.composer.trim().length() == 0) {
                    this.composer = atom.readString("UTF-8");
                    return;
                }
                return;
            case 6:
                try {
                    byte[] bytes = atom.readBytes();
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
                this.compilation = atom.readBoolean();
                return;
            case 8:
            case 9:
                if (this.copyright == null || this.copyright.trim().length() == 0) {
                    this.copyright = atom.readString("UTF-8");
                    return;
                }
                return;
            case 10:
                String day = atom.readString("UTF-8").trim();
                if (day.length() >= 4) {
                    try {
                        this.year = Short.valueOf(day.substring(0, 4)).shortValue();
                        return;
                    } catch (NumberFormatException e2) {
                        return;
                    }
                } else {
                    return;
                }
            case 11:
                atom.skip(2);
                this.disc = atom.readShort();
                this.discs = atom.readShort();
                return;
            case 12:
                if (this.genre != null && this.genre.trim().length() != 0) {
                    return;
                }
                if (atom.getRemaining() == 2) {
                    ID3v1Genre id3v1Genre = ID3v1Genre.getGenre(atom.readShort() - 1);
                    if (id3v1Genre != null) {
                        this.genre = id3v1Genre.getDescription();
                        return;
                    }
                    return;
                }
                this.genre = atom.readString("UTF-8");
                return;
            case 13:
                if (this.genre == null || this.genre.trim().length() == 0) {
                    this.genre = atom.readString("UTF-8");
                    return;
                }
                return;
            case 14:
                this.grouping = atom.readString("UTF-8");
                return;
            case 15:
                this.lyrics = atom.readString("UTF-8");
                return;
            case 16:
                this.title = atom.readString("UTF-8");
                return;
            case 17:
                this.rating = atom.readByte();
                return;
            case 18:
                this.tempo = atom.readShort();
                return;
            case 19:
                atom.skip(2);
                this.track = atom.readShort();
                this.tracks = atom.readShort();
                return;
            default:
                return;
        }
    }

    public short getTempo() {
        return this.tempo;
    }

    public byte getRating() {
        return this.rating;
    }

    public BigDecimal getSpeed() {
        return this.speed;
    }

    public BigDecimal getVolume() {
        return this.volume;
    }
}
