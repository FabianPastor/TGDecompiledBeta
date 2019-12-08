package org.telegram.messenger.audioinfo.m4a;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;

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
        if (LOGGER.isLoggable(level)) {
            LOGGER.log(level, mP4Input.toString());
        }
        ftyp(mP4Input.nextChild("ftyp"));
        moov(mP4Input.nextChildUpTo("moov"));
    }

    /* Access modifiers changed, original: 0000 */
    public void ftyp(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        this.brand = mP4Atom.readString(4, "ISO8859_1").trim();
        String str = ": brand=";
        Logger logger;
        StringBuilder stringBuilder;
        if (this.brand.matches("M4V|MP4|mp42|isom")) {
            logger = LOGGER;
            stringBuilder = new StringBuilder();
            stringBuilder.append(mP4Atom.getPath());
            stringBuilder.append(str);
            stringBuilder.append(this.brand);
            stringBuilder.append(" (experimental)");
            logger.warning(stringBuilder.toString());
        } else if (!this.brand.matches("M4A|M4P")) {
            logger = LOGGER;
            stringBuilder = new StringBuilder();
            stringBuilder.append(mP4Atom.getPath());
            stringBuilder.append(str);
            stringBuilder.append(this.brand);
            stringBuilder.append(" (expected M4A or M4P)");
            logger.warning(stringBuilder.toString());
        }
        this.version = String.valueOf(mP4Atom.readInt());
    }

    /* Access modifiers changed, original: 0000 */
    public void moov(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        while (mP4Atom.hasMoreChildren()) {
            MP4Atom nextChild = mP4Atom.nextChild();
            String type = nextChild.getType();
            int i = -1;
            int hashCode = type.hashCode();
            if (hashCode != 3363941) {
                if (hashCode != 3568424) {
                    if (hashCode == 3585340 && type.equals("udta")) {
                        i = 2;
                    }
                } else if (type.equals("trak")) {
                    i = 1;
                }
            } else if (type.equals("mvhd")) {
                i = 0;
            }
            if (i == 0) {
                mvhd(nextChild);
            } else if (i == 1) {
                trak(nextChild);
            } else if (i == 2) {
                udta(nextChild);
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void mvhd(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        byte readByte = mP4Atom.readByte();
        mP4Atom.skip(3);
        mP4Atom.skip(readByte == (byte) 1 ? 16 : 8);
        int readInt = mP4Atom.readInt();
        long readLong = readByte == (byte) 1 ? mP4Atom.readLong() : (long) mP4Atom.readInt();
        if (this.duration == 0) {
            this.duration = (readLong * 1000) / ((long) readInt);
        } else if (LOGGER.isLoggable(this.debugLevel)) {
            readLong = (readLong * 1000) / ((long) readInt);
            if (Math.abs(this.duration - readLong) > 2) {
                Logger logger = LOGGER;
                Level level = this.debugLevel;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("mvhd: duration ");
                stringBuilder.append(this.duration);
                stringBuilder.append(" -> ");
                stringBuilder.append(readLong);
                logger.log(level, stringBuilder.toString());
            }
        }
        this.speed = mP4Atom.readIntegerFixedPoint();
        this.volume = mP4Atom.readShortFixedPoint();
    }

    /* Access modifiers changed, original: 0000 */
    public void trak(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        mdia(mP4Atom.nextChildUpTo("mdia"));
    }

    /* Access modifiers changed, original: 0000 */
    public void mdia(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        mdhd(mP4Atom.nextChild("mdhd"));
    }

    /* Access modifiers changed, original: 0000 */
    public void mdhd(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        byte readByte = mP4Atom.readByte();
        mP4Atom.skip(3);
        mP4Atom.skip(readByte == (byte) 1 ? 16 : 8);
        int readInt = mP4Atom.readInt();
        long readLong = readByte == (byte) 1 ? mP4Atom.readLong() : (long) mP4Atom.readInt();
        if (this.duration == 0) {
            this.duration = (readLong * 1000) / ((long) readInt);
        } else if (LOGGER.isLoggable(this.debugLevel)) {
            readLong = (readLong * 1000) / ((long) readInt);
            if (Math.abs(this.duration - readLong) > 2) {
                Logger logger = LOGGER;
                Level level = this.debugLevel;
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("mdhd: duration ");
                stringBuilder.append(this.duration);
                stringBuilder.append(" -> ");
                stringBuilder.append(readLong);
                logger.log(level, stringBuilder.toString());
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
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

    /* Access modifiers changed, original: 0000 */
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

    /* Access modifiers changed, original: 0000 */
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
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(nextChild.getPath());
                stringBuilder.append(": contains no value");
                logger.log(level, stringBuilder.toString());
            }
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void data(org.telegram.messenger.audioinfo.m4a.MP4Atom r8) throws java.io.IOException {
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
        r4 = 0;
        r5 = 1;
        r6 = 2;
        switch(r3) {
            case 2954818: goto L_0x0105;
            case 3059752: goto L_0x00fb;
            case 3060304: goto L_0x00f1;
            case 3060591: goto L_0x00e6;
            case 3083677: goto L_0x00db;
            case 3177818: goto L_0x00d0;
            case 3511163: goto L_0x00c5;
            case 3564088: goto L_0x00ba;
            case 3568737: goto L_0x00af;
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
        goto L_0x010f;
    L_0x0031:
        r3 = "©wrt";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x0039:
        r1 = 5;
        goto L_0x0110;
    L_0x003c:
        r3 = "©nam";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x0044:
        r1 = 16;
        goto L_0x0110;
    L_0x0048:
        r3 = "©lyr";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x0050:
        r1 = 15;
        goto L_0x0110;
    L_0x0054:
        r3 = "©grp";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x005c:
        r1 = 14;
        goto L_0x0110;
    L_0x0060:
        r3 = "©gen";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x0068:
        r1 = 13;
        goto L_0x0110;
    L_0x006c:
        r3 = "©day";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x0074:
        r1 = 10;
        goto L_0x0110;
    L_0x0078:
        r3 = "©cpy";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x0080:
        r1 = 9;
        goto L_0x0110;
    L_0x0084:
        r3 = "©com";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x008c:
        r1 = 4;
        goto L_0x0110;
    L_0x008f:
        r3 = "©cmt";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x0097:
        r1 = 3;
        goto L_0x0110;
    L_0x009a:
        r3 = "©alb";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x00a2:
        r1 = 0;
        goto L_0x0110;
    L_0x00a5:
        r3 = "©ART";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x00ad:
        r1 = 2;
        goto L_0x0110;
    L_0x00af:
        r3 = "trkn";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x00b7:
        r1 = 19;
        goto L_0x0110;
    L_0x00ba:
        r3 = "tmpo";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x00c2:
        r1 = 18;
        goto L_0x0110;
    L_0x00c5:
        r3 = "rtng";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x00cd:
        r1 = 17;
        goto L_0x0110;
    L_0x00d0:
        r3 = "gnre";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x00d8:
        r1 = 12;
        goto L_0x0110;
    L_0x00db:
        r3 = "disk";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x00e3:
        r1 = 11;
        goto L_0x0110;
    L_0x00e6:
        r3 = "cprt";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x00ee:
        r1 = 8;
        goto L_0x0110;
    L_0x00f1:
        r3 = "cpil";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x00f9:
        r1 = 7;
        goto L_0x0110;
    L_0x00fb:
        r3 = "covr";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x0103:
        r1 = 6;
        goto L_0x0110;
    L_0x0105:
        r3 = "aART";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x010f;
    L_0x010d:
        r1 = 1;
        goto L_0x0110;
    L_0x010f:
        r1 = -1;
    L_0x0110:
        r2 = "UTF-8";
        switch(r1) {
            case 0: goto L_0x0291;
            case 1: goto L_0x028a;
            case 2: goto L_0x0283;
            case 3: goto L_0x027c;
            case 4: goto L_0x0267;
            case 5: goto L_0x0267;
            case 6: goto L_0x01e6;
            case 7: goto L_0x01de;
            case 8: goto L_0x01c8;
            case 9: goto L_0x01c8;
            case 10: goto L_0x01aa;
            case 11: goto L_0x0199;
            case 12: goto L_0x0166;
            case 13: goto L_0x0150;
            case 14: goto L_0x0148;
            case 15: goto L_0x0140;
            case 16: goto L_0x0138;
            case 17: goto L_0x0130;
            case 18: goto L_0x0128;
            case 19: goto L_0x0117;
            default: goto L_0x0115;
        };
    L_0x0115:
        goto L_0x0297;
    L_0x0117:
        r8.skip(r6);
        r0 = r8.readShort();
        r7.track = r0;
        r8 = r8.readShort();
        r7.tracks = r8;
        goto L_0x0297;
    L_0x0128:
        r8 = r8.readShort();
        r7.tempo = r8;
        goto L_0x0297;
    L_0x0130:
        r8 = r8.readByte();
        r7.rating = r8;
        goto L_0x0297;
    L_0x0138:
        r8 = r8.readString(r2);
        r7.title = r8;
        goto L_0x0297;
    L_0x0140:
        r8 = r8.readString(r2);
        r7.lyrics = r8;
        goto L_0x0297;
    L_0x0148:
        r8 = r8.readString(r2);
        r7.grouping = r8;
        goto L_0x0297;
    L_0x0150:
        r0 = r7.genre;
        if (r0 == 0) goto L_0x015e;
    L_0x0154:
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x0297;
    L_0x015e:
        r8 = r8.readString(r2);
        r7.genre = r8;
        goto L_0x0297;
    L_0x0166:
        r0 = r7.genre;
        if (r0 == 0) goto L_0x0174;
    L_0x016a:
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x0297;
    L_0x0174:
        r0 = r8.getRemaining();
        r3 = 2;
        r6 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r6 != 0) goto L_0x0191;
    L_0x017e:
        r8 = r8.readShort();
        r8 = r8 - r5;
        r8 = org.telegram.messenger.audioinfo.mp3.ID3v1Genre.getGenre(r8);
        if (r8 == 0) goto L_0x0297;
    L_0x0189:
        r8 = r8.getDescription();
        r7.genre = r8;
        goto L_0x0297;
    L_0x0191:
        r8 = r8.readString(r2);
        r7.genre = r8;
        goto L_0x0297;
    L_0x0199:
        r8.skip(r6);
        r0 = r8.readShort();
        r7.disc = r0;
        r8 = r8.readShort();
        r7.discs = r8;
        goto L_0x0297;
    L_0x01aa:
        r8 = r8.readString(r2);
        r8 = r8.trim();
        r1 = r8.length();
        if (r1 < r0) goto L_0x0297;
    L_0x01b8:
        r8 = r8.substring(r4, r0);	 Catch:{ NumberFormatException -> 0x0297 }
        r8 = java.lang.Short.valueOf(r8);	 Catch:{ NumberFormatException -> 0x0297 }
        r8 = r8.shortValue();	 Catch:{ NumberFormatException -> 0x0297 }
        r7.year = r8;	 Catch:{ NumberFormatException -> 0x0297 }
        goto L_0x0297;
    L_0x01c8:
        r0 = r7.copyright;
        if (r0 == 0) goto L_0x01d6;
    L_0x01cc:
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x0297;
    L_0x01d6:
        r8 = r8.readString(r2);
        r7.copyright = r8;
        goto L_0x0297;
    L_0x01de:
        r8 = r8.readBoolean();
        r7.compilation = r8;
        goto L_0x0297;
    L_0x01e6:
        r8 = r8.readBytes();	 Catch:{ Exception -> 0x0262 }
        r0 = new android.graphics.BitmapFactory$Options;	 Catch:{ Exception -> 0x0262 }
        r0.<init>();	 Catch:{ Exception -> 0x0262 }
        r0.inJustDecodeBounds = r5;	 Catch:{ Exception -> 0x0262 }
        r0.inSampleSize = r5;	 Catch:{ Exception -> 0x0262 }
        r1 = r8.length;	 Catch:{ Exception -> 0x0262 }
        android.graphics.BitmapFactory.decodeByteArray(r8, r4, r1, r0);	 Catch:{ Exception -> 0x0262 }
        r1 = r0.outWidth;	 Catch:{ Exception -> 0x0262 }
        r2 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r1 > r2) goto L_0x0201;
    L_0x01fd:
        r1 = r0.outHeight;	 Catch:{ Exception -> 0x0262 }
        if (r1 <= r2) goto L_0x0214;
    L_0x0201:
        r1 = r0.outWidth;	 Catch:{ Exception -> 0x0262 }
        r3 = r0.outHeight;	 Catch:{ Exception -> 0x0262 }
        r1 = java.lang.Math.max(r1, r3);	 Catch:{ Exception -> 0x0262 }
    L_0x0209:
        if (r1 <= r2) goto L_0x0214;
    L_0x020b:
        r3 = r0.inSampleSize;	 Catch:{ Exception -> 0x0262 }
        r3 = r3 * 2;
        r0.inSampleSize = r3;	 Catch:{ Exception -> 0x0262 }
        r1 = r1 / 2;
        goto L_0x0209;
    L_0x0214:
        r0.inJustDecodeBounds = r4;	 Catch:{ Exception -> 0x0262 }
        r1 = r8.length;	 Catch:{ Exception -> 0x0262 }
        r8 = android.graphics.BitmapFactory.decodeByteArray(r8, r4, r1, r0);	 Catch:{ Exception -> 0x0262 }
        r7.cover = r8;	 Catch:{ Exception -> 0x0262 }
        r8 = r7.cover;	 Catch:{ Exception -> 0x0262 }
        if (r8 == 0) goto L_0x0297;
    L_0x0221:
        r8 = r7.cover;	 Catch:{ Exception -> 0x0262 }
        r8 = r8.getWidth();	 Catch:{ Exception -> 0x0262 }
        r0 = r7.cover;	 Catch:{ Exception -> 0x0262 }
        r0 = r0.getHeight();	 Catch:{ Exception -> 0x0262 }
        r8 = java.lang.Math.max(r8, r0);	 Catch:{ Exception -> 0x0262 }
        r8 = (float) r8;	 Catch:{ Exception -> 0x0262 }
        r0 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r8 = r8 / r0;
        r0 = 0;
        r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r0 <= 0) goto L_0x0255;
    L_0x023a:
        r0 = r7.cover;	 Catch:{ Exception -> 0x0262 }
        r1 = r7.cover;	 Catch:{ Exception -> 0x0262 }
        r1 = r1.getWidth();	 Catch:{ Exception -> 0x0262 }
        r1 = (float) r1;	 Catch:{ Exception -> 0x0262 }
        r1 = r1 / r8;
        r1 = (int) r1;	 Catch:{ Exception -> 0x0262 }
        r2 = r7.cover;	 Catch:{ Exception -> 0x0262 }
        r2 = r2.getHeight();	 Catch:{ Exception -> 0x0262 }
        r2 = (float) r2;	 Catch:{ Exception -> 0x0262 }
        r2 = r2 / r8;
        r8 = (int) r2;	 Catch:{ Exception -> 0x0262 }
        r8 = android.graphics.Bitmap.createScaledBitmap(r0, r1, r8, r5);	 Catch:{ Exception -> 0x0262 }
        r7.smallCover = r8;	 Catch:{ Exception -> 0x0262 }
        goto L_0x0259;
    L_0x0255:
        r8 = r7.cover;	 Catch:{ Exception -> 0x0262 }
        r7.smallCover = r8;	 Catch:{ Exception -> 0x0262 }
    L_0x0259:
        r8 = r7.smallCover;	 Catch:{ Exception -> 0x0262 }
        if (r8 != 0) goto L_0x0297;
    L_0x025d:
        r8 = r7.cover;	 Catch:{ Exception -> 0x0262 }
        r7.smallCover = r8;	 Catch:{ Exception -> 0x0262 }
        goto L_0x0297;
    L_0x0262:
        r8 = move-exception;
        r8.printStackTrace();
        goto L_0x0297;
    L_0x0267:
        r0 = r7.composer;
        if (r0 == 0) goto L_0x0275;
    L_0x026b:
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x0297;
    L_0x0275:
        r8 = r8.readString(r2);
        r7.composer = r8;
        goto L_0x0297;
    L_0x027c:
        r8 = r8.readString(r2);
        r7.comment = r8;
        goto L_0x0297;
    L_0x0283:
        r8 = r8.readString(r2);
        r7.artist = r8;
        goto L_0x0297;
    L_0x028a:
        r8 = r8.readString(r2);
        r7.albumArtist = r8;
        goto L_0x0297;
    L_0x0291:
        r8 = r8.readString(r2);
        r7.album = r8;
    L_0x0297:
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
