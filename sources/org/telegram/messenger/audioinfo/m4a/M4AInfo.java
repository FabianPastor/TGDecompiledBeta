package org.telegram.messenger.audioinfo.m4a;

import com.coremedia.iso.boxes.FileTypeBox;
import com.coremedia.iso.boxes.MediaBox;
import com.coremedia.iso.boxes.MediaHeaderBox;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.MovieHeaderBox;
import com.coremedia.iso.boxes.TrackBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
import org.telegram.messenger.exoplayer2.upstream.DataSchemeDataSource;

public class M4AInfo extends AudioInfo {
    private static final String ASCII = "ISO8859_1";
    static final Logger LOGGER = Logger.getLogger(M4AInfo.class.getName());
    private static final String UTF_8 = "UTF-8";
    private final Level debugLevel;
    private byte rating;
    private BigDecimal speed;
    private short tempo;
    private BigDecimal volume;

    public M4AInfo(InputStream inputStream) throws IOException {
        this(inputStream, Level.FINEST);
    }

    public M4AInfo(InputStream inputStream, Level level) throws IOException {
        this.debugLevel = level;
        MP4Input mP4Input = new MP4Input(inputStream);
        if (LOGGER.isLoggable(level) != null) {
            LOGGER.log(level, mP4Input.toString());
        }
        ftyp(mP4Input.nextChild(FileTypeBox.TYPE));
        moov(mP4Input.nextChildUpTo(MovieBox.TYPE));
    }

    void ftyp(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        this.brand = mP4Atom.readString(4, ASCII).trim();
        Logger logger;
        StringBuilder stringBuilder;
        if (this.brand.matches("M4V|MP4|mp42|isom")) {
            logger = LOGGER;
            stringBuilder = new StringBuilder();
            stringBuilder.append(mP4Atom.getPath());
            stringBuilder.append(": brand=");
            stringBuilder.append(this.brand);
            stringBuilder.append(" (experimental)");
            logger.warning(stringBuilder.toString());
        } else if (!this.brand.matches("M4A|M4P")) {
            logger = LOGGER;
            stringBuilder = new StringBuilder();
            stringBuilder.append(mP4Atom.getPath());
            stringBuilder.append(": brand=");
            stringBuilder.append(this.brand);
            stringBuilder.append(" (expected M4A or M4P)");
            logger.warning(stringBuilder.toString());
        }
        this.version = String.valueOf(mP4Atom.readInt());
    }

    void moov(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        while (mP4Atom.hasMoreChildren()) {
            MP4Atom nextChild = mP4Atom.nextChild();
            String type = nextChild.getType();
            Object obj = -1;
            int hashCode = type.hashCode();
            if (hashCode != 3363941) {
                if (hashCode != 3568424) {
                    if (hashCode == 3585340) {
                        if (type.equals(UserDataBox.TYPE)) {
                            obj = 2;
                        }
                    }
                } else if (type.equals(TrackBox.TYPE)) {
                    obj = 1;
                }
            } else if (type.equals(MovieHeaderBox.TYPE)) {
                obj = null;
            }
            switch (obj) {
                case null:
                    mvhd(nextChild);
                    break;
                case 1:
                    trak(nextChild);
                    break;
                case 2:
                    udta(nextChild);
                    break;
                default:
                    break;
            }
        }
    }

    void mvhd(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        byte readByte = mP4Atom.readByte();
        mP4Atom.skip(3);
        mP4Atom.skip(readByte == (byte) 1 ? 16 : 8);
        int readInt = mP4Atom.readInt();
        long readLong = readByte == (byte) 1 ? mP4Atom.readLong() : (long) mP4Atom.readInt();
        if (this.duration == 0) {
            this.duration = (1000 * readLong) / ((long) readInt);
        } else if (LOGGER.isLoggable(this.debugLevel)) {
            long j = (1000 * readLong) / ((long) readInt);
            if (Math.abs(this.duration - j) > 2) {
                Logger logger = LOGGER;
                Level level = this.debugLevel;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("mvhd: duration ");
                stringBuilder.append(this.duration);
                stringBuilder.append(" -> ");
                stringBuilder.append(j);
                logger.log(level, stringBuilder.toString());
            }
        }
        this.speed = mP4Atom.readIntegerFixedPoint();
        this.volume = mP4Atom.readShortFixedPoint();
    }

    void trak(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        mdia(mP4Atom.nextChildUpTo(MediaBox.TYPE));
    }

    void mdia(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        mdhd(mP4Atom.nextChild(MediaHeaderBox.TYPE));
    }

    void mdhd(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        byte readByte = mP4Atom.readByte();
        mP4Atom.skip(3);
        mP4Atom.skip(readByte == (byte) 1 ? 16 : 8);
        int readInt = mP4Atom.readInt();
        long readLong = readByte == (byte) 1 ? mP4Atom.readLong() : (long) mP4Atom.readInt();
        if (this.duration == 0) {
            this.duration = (1000 * readLong) / ((long) readInt);
        } else if (LOGGER.isLoggable(this.debugLevel) != null) {
            long j = (1000 * readLong) / ((long) readInt);
            if (Math.abs(this.duration - j) > 2) {
                mP4Atom = LOGGER;
                Level level = this.debugLevel;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("mdhd: duration ");
                stringBuilder.append(this.duration);
                stringBuilder.append(" -> ");
                stringBuilder.append(j);
                mP4Atom.log(level, stringBuilder.toString());
            }
        }
    }

    void udta(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        while (mP4Atom.hasMoreChildren()) {
            MP4Atom nextChild = mP4Atom.nextChild();
            if (MetaBox.TYPE.equals(nextChild.getType())) {
                meta(nextChild);
                return;
            }
        }
    }

    void meta(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        mP4Atom.skip(4);
        while (mP4Atom.hasMoreChildren()) {
            MP4Atom nextChild = mP4Atom.nextChild();
            if (AppleItemListBox.TYPE.equals(nextChild.getType())) {
                ilst(nextChild);
                return;
            }
        }
    }

    void ilst(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        while (mP4Atom.hasMoreChildren()) {
            MP4Atom nextChild = mP4Atom.nextChild();
            if (LOGGER.isLoggable(this.debugLevel)) {
                LOGGER.log(this.debugLevel, nextChild.toString());
            }
            if (nextChild.getRemaining() != 0) {
                data(nextChild.nextChildUpTo(DataSchemeDataSource.SCHEME_DATA));
            } else if (LOGGER.isLoggable(this.debugLevel)) {
                Logger logger = LOGGER;
                Level level = this.debugLevel;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(nextChild.getPath());
                stringBuilder.append(": contains no value");
                logger.log(level, stringBuilder.toString());
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    void data(org.telegram.messenger.audioinfo.m4a.MP4Atom r8) throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r7 = this;
        r0 = LOGGER;
        r1 = r7.debugLevel;
        r0 = r0.isLoggable(r1);
        if (r0 == 0) goto L_0x0015;
    L_0x000a:
        r0 = LOGGER;
        r1 = r7.debugLevel;
        r2 = r8.toString();
        r0.log(r1, r2);
    L_0x0015:
        r0 = 4;
        r8.skip(r0);
        r8.skip(r0);
        r1 = r8.getParent();
        r1 = r1.getType();
        r2 = -1;
        r3 = r1.hashCode();
        r4 = 1;
        r5 = 2;
        r6 = 0;
        switch(r3) {
            case 2954818: goto L_0x0106;
            case 3059752: goto L_0x00fc;
            case 3060304: goto L_0x00f2;
            case 3060591: goto L_0x00e7;
            case 3083677: goto L_0x00dc;
            case 3177818: goto L_0x00d1;
            case 3511163: goto L_0x00c6;
            case 3564088: goto L_0x00bb;
            case 3568737: goto L_0x00b0;
            case 5099770: goto L_0x00a5;
            case 5131342: goto L_0x009a;
            case 5133313: goto L_0x008f;
            case 5133368: goto L_0x0084;
            case 5133411: goto L_0x0078;
            case 5133907: goto L_0x006c;
            case 5136903: goto L_0x0060;
            case 5137308: goto L_0x0054;
            case 5142332: goto L_0x0048;
            case 5143505: goto L_0x003c;
            case 5152688: goto L_0x0031;
            default: goto L_0x002f;
        };
    L_0x002f:
        goto L_0x0110;
    L_0x0031:
        r3 = "\u00a9wrt";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x0039:
        r1 = 5;
        goto L_0x0111;
    L_0x003c:
        r3 = "\u00a9nam";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x0044:
        r1 = 16;
        goto L_0x0111;
    L_0x0048:
        r3 = "\u00a9lyr";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x0050:
        r1 = 15;
        goto L_0x0111;
    L_0x0054:
        r3 = "\u00a9grp";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x005c:
        r1 = 14;
        goto L_0x0111;
    L_0x0060:
        r3 = "\u00a9gen";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x0068:
        r1 = 13;
        goto L_0x0111;
    L_0x006c:
        r3 = "\u00a9day";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x0074:
        r1 = 10;
        goto L_0x0111;
    L_0x0078:
        r3 = "\u00a9cpy";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x0080:
        r1 = 9;
        goto L_0x0111;
    L_0x0084:
        r3 = "\u00a9com";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x008c:
        r1 = r0;
        goto L_0x0111;
    L_0x008f:
        r3 = "\u00a9cmt";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x0097:
        r1 = 3;
        goto L_0x0111;
    L_0x009a:
        r3 = "\u00a9alb";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x00a2:
        r1 = r6;
        goto L_0x0111;
    L_0x00a5:
        r3 = "\u00a9ART";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x00ad:
        r1 = r5;
        goto L_0x0111;
    L_0x00b0:
        r3 = "trkn";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x00b8:
        r1 = 19;
        goto L_0x0111;
    L_0x00bb:
        r3 = "tmpo";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x00c3:
        r1 = 18;
        goto L_0x0111;
    L_0x00c6:
        r3 = "rtng";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x00ce:
        r1 = 17;
        goto L_0x0111;
    L_0x00d1:
        r3 = "gnre";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x00d9:
        r1 = 12;
        goto L_0x0111;
    L_0x00dc:
        r3 = "disk";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x00e4:
        r1 = 11;
        goto L_0x0111;
    L_0x00e7:
        r3 = "cprt";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x00ef:
        r1 = 8;
        goto L_0x0111;
    L_0x00f2:
        r3 = "cpil";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x00fa:
        r1 = 7;
        goto L_0x0111;
    L_0x00fc:
        r3 = "covr";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x0104:
        r1 = 6;
        goto L_0x0111;
    L_0x0106:
        r3 = "aART";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x0110;
    L_0x010e:
        r1 = r4;
        goto L_0x0111;
    L_0x0110:
        r1 = r2;
    L_0x0111:
        switch(r1) {
            case 0: goto L_0x02ad;
            case 1: goto L_0x02a4;
            case 2: goto L_0x029b;
            case 3: goto L_0x0292;
            case 4: goto L_0x0279;
            case 5: goto L_0x0279;
            case 6: goto L_0x01f9;
            case 7: goto L_0x01f1;
            case 8: goto L_0x01d7;
            case 9: goto L_0x01d7;
            case 10: goto L_0x01b7;
            case 11: goto L_0x01a6;
            case 12: goto L_0x016f;
            case 13: goto L_0x0155;
            case 14: goto L_0x014b;
            case 15: goto L_0x0141;
            case 16: goto L_0x0137;
            case 17: goto L_0x012f;
            case 18: goto L_0x0127;
            case 19: goto L_0x0116;
            default: goto L_0x0114;
        };
    L_0x0114:
        goto L_0x02b5;
    L_0x0116:
        r8.skip(r5);
        r0 = r8.readShort();
        r7.track = r0;
        r8 = r8.readShort();
        r7.tracks = r8;
        goto L_0x02b5;
    L_0x0127:
        r8 = r8.readShort();
        r7.tempo = r8;
        goto L_0x02b5;
    L_0x012f:
        r8 = r8.readByte();
        r7.rating = r8;
        goto L_0x02b5;
    L_0x0137:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.title = r8;
        goto L_0x02b5;
    L_0x0141:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.lyrics = r8;
        goto L_0x02b5;
    L_0x014b:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.grouping = r8;
        goto L_0x02b5;
    L_0x0155:
        r0 = r7.genre;
        if (r0 == 0) goto L_0x0165;
    L_0x0159:
        r0 = r7.genre;
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x02b5;
    L_0x0165:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.genre = r8;
        goto L_0x02b5;
    L_0x016f:
        r0 = r7.genre;
        if (r0 == 0) goto L_0x017f;
    L_0x0173:
        r0 = r7.genre;
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x02b5;
    L_0x017f:
        r0 = r8.getRemaining();
        r2 = 2;
        r5 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r5 != 0) goto L_0x019c;
    L_0x0189:
        r8 = r8.readShort();
        r8 = r8 - r4;
        r8 = org.telegram.messenger.audioinfo.mp3.ID3v1Genre.getGenre(r8);
        if (r8 == 0) goto L_0x02b5;
    L_0x0194:
        r8 = r8.getDescription();
        r7.genre = r8;
        goto L_0x02b5;
    L_0x019c:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.genre = r8;
        goto L_0x02b5;
    L_0x01a6:
        r8.skip(r5);
        r0 = r8.readShort();
        r7.disc = r0;
        r8 = r8.readShort();
        r7.discs = r8;
        goto L_0x02b5;
    L_0x01b7:
        r1 = "UTF-8";
        r8 = r8.readString(r1);
        r8 = r8.trim();
        r1 = r8.length();
        if (r1 < r0) goto L_0x02b5;
    L_0x01c7:
        r8 = r8.substring(r6, r0);	 Catch:{ NumberFormatException -> 0x02b5 }
        r8 = java.lang.Short.valueOf(r8);	 Catch:{ NumberFormatException -> 0x02b5 }
        r8 = r8.shortValue();	 Catch:{ NumberFormatException -> 0x02b5 }
        r7.year = r8;	 Catch:{ NumberFormatException -> 0x02b5 }
        goto L_0x02b5;
    L_0x01d7:
        r0 = r7.copyright;
        if (r0 == 0) goto L_0x01e7;
    L_0x01db:
        r0 = r7.copyright;
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x02b5;
    L_0x01e7:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.copyright = r8;
        goto L_0x02b5;
    L_0x01f1:
        r8 = r8.readBoolean();
        r7.compilation = r8;
        goto L_0x02b5;
    L_0x01f9:
        r8 = r8.readBytes();	 Catch:{ Exception -> 0x0274 }
        r0 = new android.graphics.BitmapFactory$Options;	 Catch:{ Exception -> 0x0274 }
        r0.<init>();	 Catch:{ Exception -> 0x0274 }
        r0.inJustDecodeBounds = r4;	 Catch:{ Exception -> 0x0274 }
        r0.inSampleSize = r4;	 Catch:{ Exception -> 0x0274 }
        r1 = r8.length;	 Catch:{ Exception -> 0x0274 }
        android.graphics.BitmapFactory.decodeByteArray(r8, r6, r1, r0);	 Catch:{ Exception -> 0x0274 }
        r1 = r0.outWidth;	 Catch:{ Exception -> 0x0274 }
        r2 = 800; // 0x320 float:1.121E-42 double:3.953E-321;	 Catch:{ Exception -> 0x0274 }
        if (r1 > r2) goto L_0x0214;	 Catch:{ Exception -> 0x0274 }
    L_0x0210:
        r1 = r0.outHeight;	 Catch:{ Exception -> 0x0274 }
        if (r1 <= r2) goto L_0x0226;	 Catch:{ Exception -> 0x0274 }
    L_0x0214:
        r1 = r0.outWidth;	 Catch:{ Exception -> 0x0274 }
        r3 = r0.outHeight;	 Catch:{ Exception -> 0x0274 }
        r1 = java.lang.Math.max(r1, r3);	 Catch:{ Exception -> 0x0274 }
    L_0x021c:
        if (r1 <= r2) goto L_0x0226;	 Catch:{ Exception -> 0x0274 }
    L_0x021e:
        r3 = r0.inSampleSize;	 Catch:{ Exception -> 0x0274 }
        r3 = r3 * r5;	 Catch:{ Exception -> 0x0274 }
        r0.inSampleSize = r3;	 Catch:{ Exception -> 0x0274 }
        r1 = r1 / 2;	 Catch:{ Exception -> 0x0274 }
        goto L_0x021c;	 Catch:{ Exception -> 0x0274 }
    L_0x0226:
        r0.inJustDecodeBounds = r6;	 Catch:{ Exception -> 0x0274 }
        r1 = r8.length;	 Catch:{ Exception -> 0x0274 }
        r8 = android.graphics.BitmapFactory.decodeByteArray(r8, r6, r1, r0);	 Catch:{ Exception -> 0x0274 }
        r7.cover = r8;	 Catch:{ Exception -> 0x0274 }
        r8 = r7.cover;	 Catch:{ Exception -> 0x0274 }
        if (r8 == 0) goto L_0x02b5;	 Catch:{ Exception -> 0x0274 }
    L_0x0233:
        r8 = r7.cover;	 Catch:{ Exception -> 0x0274 }
        r8 = r8.getWidth();	 Catch:{ Exception -> 0x0274 }
        r0 = r7.cover;	 Catch:{ Exception -> 0x0274 }
        r0 = r0.getHeight();	 Catch:{ Exception -> 0x0274 }
        r8 = java.lang.Math.max(r8, r0);	 Catch:{ Exception -> 0x0274 }
        r8 = (float) r8;	 Catch:{ Exception -> 0x0274 }
        r0 = NUM; // 0x42f00000 float:120.0 double:5.548480205E-315;	 Catch:{ Exception -> 0x0274 }
        r8 = r8 / r0;	 Catch:{ Exception -> 0x0274 }
        r0 = 0;	 Catch:{ Exception -> 0x0274 }
        r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));	 Catch:{ Exception -> 0x0274 }
        if (r0 <= 0) goto L_0x0267;	 Catch:{ Exception -> 0x0274 }
    L_0x024c:
        r0 = r7.cover;	 Catch:{ Exception -> 0x0274 }
        r1 = r7.cover;	 Catch:{ Exception -> 0x0274 }
        r1 = r1.getWidth();	 Catch:{ Exception -> 0x0274 }
        r1 = (float) r1;	 Catch:{ Exception -> 0x0274 }
        r1 = r1 / r8;	 Catch:{ Exception -> 0x0274 }
        r1 = (int) r1;	 Catch:{ Exception -> 0x0274 }
        r2 = r7.cover;	 Catch:{ Exception -> 0x0274 }
        r2 = r2.getHeight();	 Catch:{ Exception -> 0x0274 }
        r2 = (float) r2;	 Catch:{ Exception -> 0x0274 }
        r2 = r2 / r8;	 Catch:{ Exception -> 0x0274 }
        r8 = (int) r2;	 Catch:{ Exception -> 0x0274 }
        r8 = android.graphics.Bitmap.createScaledBitmap(r0, r1, r8, r4);	 Catch:{ Exception -> 0x0274 }
        r7.smallCover = r8;	 Catch:{ Exception -> 0x0274 }
        goto L_0x026b;	 Catch:{ Exception -> 0x0274 }
    L_0x0267:
        r8 = r7.cover;	 Catch:{ Exception -> 0x0274 }
        r7.smallCover = r8;	 Catch:{ Exception -> 0x0274 }
    L_0x026b:
        r8 = r7.smallCover;	 Catch:{ Exception -> 0x0274 }
        if (r8 != 0) goto L_0x02b5;	 Catch:{ Exception -> 0x0274 }
    L_0x026f:
        r8 = r7.cover;	 Catch:{ Exception -> 0x0274 }
        r7.smallCover = r8;	 Catch:{ Exception -> 0x0274 }
        goto L_0x02b5;
    L_0x0274:
        r8 = move-exception;
        r8.printStackTrace();
        goto L_0x02b5;
    L_0x0279:
        r0 = r7.composer;
        if (r0 == 0) goto L_0x0289;
    L_0x027d:
        r0 = r7.composer;
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x02b5;
    L_0x0289:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.composer = r8;
        goto L_0x02b5;
    L_0x0292:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.comment = r8;
        goto L_0x02b5;
    L_0x029b:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.artist = r8;
        goto L_0x02b5;
    L_0x02a4:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.albumArtist = r8;
        goto L_0x02b5;
    L_0x02ad:
        r0 = "UTF-8";
        r8 = r8.readString(r0);
        r7.album = r8;
    L_0x02b5:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.m4a.M4AInfo.data(org.telegram.messenger.audioinfo.m4a.MP4Atom):void");
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
