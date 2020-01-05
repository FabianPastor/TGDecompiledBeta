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
            case 2954818: goto L_0x0112;
            case 3059752: goto L_0x0108;
            case 3060304: goto L_0x00fe;
            case 3060591: goto L_0x00f3;
            case 3083677: goto L_0x00e8;
            case 3177818: goto L_0x00dd;
            case 3511163: goto L_0x00d2;
            case 3564088: goto L_0x00c6;
            case 3568737: goto L_0x00ba;
            case 5099770: goto L_0x00af;
            case 5131342: goto L_0x00a3;
            case 5133313: goto L_0x0097;
            case 5133368: goto L_0x008b;
            case 5133411: goto L_0x007e;
            case 5133907: goto L_0x0071;
            case 5136903: goto L_0x0064;
            case 5137308: goto L_0x0057;
            case 5142332: goto L_0x004a;
            case 5143505: goto L_0x003d;
            case 5152688: goto L_0x0031;
            default: goto L_0x002f;
        };
    L_0x002f:
        goto L_0x011c;
    L_0x0031:
        r3 = "©wrt";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x003a:
        r1 = 5;
        goto L_0x011d;
    L_0x003d:
        r3 = "©nam";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x0046:
        r1 = 16;
        goto L_0x011d;
    L_0x004a:
        r3 = "©lyr";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x0053:
        r1 = 15;
        goto L_0x011d;
    L_0x0057:
        r3 = "©grp";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x0060:
        r1 = 14;
        goto L_0x011d;
    L_0x0064:
        r3 = "©gen";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x006d:
        r1 = 13;
        goto L_0x011d;
    L_0x0071:
        r3 = "©day";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x007a:
        r1 = 10;
        goto L_0x011d;
    L_0x007e:
        r3 = "©cpy";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x0087:
        r1 = 9;
        goto L_0x011d;
    L_0x008b:
        r3 = "©com";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x0094:
        r1 = 4;
        goto L_0x011d;
    L_0x0097:
        r3 = "©cmt";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x00a0:
        r1 = 3;
        goto L_0x011d;
    L_0x00a3:
        r3 = "©alb";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x00ac:
        r1 = 0;
        goto L_0x011d;
    L_0x00af:
        r3 = "©ART";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x00b8:
        r1 = 2;
        goto L_0x011d;
    L_0x00ba:
        r3 = "trkn";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x00c3:
        r1 = 19;
        goto L_0x011d;
    L_0x00c6:
        r3 = "tmpo";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x00cf:
        r1 = 18;
        goto L_0x011d;
    L_0x00d2:
        r3 = "rtng";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x00da:
        r1 = 17;
        goto L_0x011d;
    L_0x00dd:
        r3 = "gnre";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x00e5:
        r1 = 12;
        goto L_0x011d;
    L_0x00e8:
        r3 = "disk";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x00f0:
        r1 = 11;
        goto L_0x011d;
    L_0x00f3:
        r3 = "cprt";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x00fb:
        r1 = 8;
        goto L_0x011d;
    L_0x00fe:
        r3 = "cpil";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x0106:
        r1 = 7;
        goto L_0x011d;
    L_0x0108:
        r3 = "covr";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x0110:
        r1 = 6;
        goto L_0x011d;
    L_0x0112:
        r3 = "aART";
        r1 = r1.equals(r3);
        if (r1 == 0) goto L_0x011c;
    L_0x011a:
        r1 = 1;
        goto L_0x011d;
    L_0x011c:
        r1 = -1;
    L_0x011d:
        r2 = "UTF-8";
        switch(r1) {
            case 0: goto L_0x029e;
            case 1: goto L_0x0297;
            case 2: goto L_0x0290;
            case 3: goto L_0x0289;
            case 4: goto L_0x0274;
            case 5: goto L_0x0274;
            case 6: goto L_0x01f3;
            case 7: goto L_0x01eb;
            case 8: goto L_0x01d5;
            case 9: goto L_0x01d5;
            case 10: goto L_0x01b7;
            case 11: goto L_0x01a6;
            case 12: goto L_0x0173;
            case 13: goto L_0x015d;
            case 14: goto L_0x0155;
            case 15: goto L_0x014d;
            case 16: goto L_0x0145;
            case 17: goto L_0x013d;
            case 18: goto L_0x0135;
            case 19: goto L_0x0124;
            default: goto L_0x0122;
        };
    L_0x0122:
        goto L_0x02a4;
    L_0x0124:
        r8.skip(r6);
        r0 = r8.readShort();
        r7.track = r0;
        r8 = r8.readShort();
        r7.tracks = r8;
        goto L_0x02a4;
    L_0x0135:
        r8 = r8.readShort();
        r7.tempo = r8;
        goto L_0x02a4;
    L_0x013d:
        r8 = r8.readByte();
        r7.rating = r8;
        goto L_0x02a4;
    L_0x0145:
        r8 = r8.readString(r2);
        r7.title = r8;
        goto L_0x02a4;
    L_0x014d:
        r8 = r8.readString(r2);
        r7.lyrics = r8;
        goto L_0x02a4;
    L_0x0155:
        r8 = r8.readString(r2);
        r7.grouping = r8;
        goto L_0x02a4;
    L_0x015d:
        r0 = r7.genre;
        if (r0 == 0) goto L_0x016b;
    L_0x0161:
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x02a4;
    L_0x016b:
        r8 = r8.readString(r2);
        r7.genre = r8;
        goto L_0x02a4;
    L_0x0173:
        r0 = r7.genre;
        if (r0 == 0) goto L_0x0181;
    L_0x0177:
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x02a4;
    L_0x0181:
        r0 = r8.getRemaining();
        r3 = 2;
        r6 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1));
        if (r6 != 0) goto L_0x019e;
    L_0x018b:
        r8 = r8.readShort();
        r8 = r8 - r5;
        r8 = org.telegram.messenger.audioinfo.mp3.ID3v1Genre.getGenre(r8);
        if (r8 == 0) goto L_0x02a4;
    L_0x0196:
        r8 = r8.getDescription();
        r7.genre = r8;
        goto L_0x02a4;
    L_0x019e:
        r8 = r8.readString(r2);
        r7.genre = r8;
        goto L_0x02a4;
    L_0x01a6:
        r8.skip(r6);
        r0 = r8.readShort();
        r7.disc = r0;
        r8 = r8.readShort();
        r7.discs = r8;
        goto L_0x02a4;
    L_0x01b7:
        r8 = r8.readString(r2);
        r8 = r8.trim();
        r1 = r8.length();
        if (r1 < r0) goto L_0x02a4;
    L_0x01c5:
        r8 = r8.substring(r4, r0);	 Catch:{ NumberFormatException -> 0x02a4 }
        r8 = java.lang.Short.valueOf(r8);	 Catch:{ NumberFormatException -> 0x02a4 }
        r8 = r8.shortValue();	 Catch:{ NumberFormatException -> 0x02a4 }
        r7.year = r8;	 Catch:{ NumberFormatException -> 0x02a4 }
        goto L_0x02a4;
    L_0x01d5:
        r0 = r7.copyright;
        if (r0 == 0) goto L_0x01e3;
    L_0x01d9:
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x02a4;
    L_0x01e3:
        r8 = r8.readString(r2);
        r7.copyright = r8;
        goto L_0x02a4;
    L_0x01eb:
        r8 = r8.readBoolean();
        r7.compilation = r8;
        goto L_0x02a4;
    L_0x01f3:
        r8 = r8.readBytes();	 Catch:{ Exception -> 0x026f }
        r0 = new android.graphics.BitmapFactory$Options;	 Catch:{ Exception -> 0x026f }
        r0.<init>();	 Catch:{ Exception -> 0x026f }
        r0.inJustDecodeBounds = r5;	 Catch:{ Exception -> 0x026f }
        r0.inSampleSize = r5;	 Catch:{ Exception -> 0x026f }
        r1 = r8.length;	 Catch:{ Exception -> 0x026f }
        android.graphics.BitmapFactory.decodeByteArray(r8, r4, r1, r0);	 Catch:{ Exception -> 0x026f }
        r1 = r0.outWidth;	 Catch:{ Exception -> 0x026f }
        r2 = 800; // 0x320 float:1.121E-42 double:3.953E-321;
        if (r1 > r2) goto L_0x020e;
    L_0x020a:
        r1 = r0.outHeight;	 Catch:{ Exception -> 0x026f }
        if (r1 <= r2) goto L_0x0221;
    L_0x020e:
        r1 = r0.outWidth;	 Catch:{ Exception -> 0x026f }
        r3 = r0.outHeight;	 Catch:{ Exception -> 0x026f }
        r1 = java.lang.Math.max(r1, r3);	 Catch:{ Exception -> 0x026f }
    L_0x0216:
        if (r1 <= r2) goto L_0x0221;
    L_0x0218:
        r3 = r0.inSampleSize;	 Catch:{ Exception -> 0x026f }
        r3 = r3 * 2;
        r0.inSampleSize = r3;	 Catch:{ Exception -> 0x026f }
        r1 = r1 / 2;
        goto L_0x0216;
    L_0x0221:
        r0.inJustDecodeBounds = r4;	 Catch:{ Exception -> 0x026f }
        r1 = r8.length;	 Catch:{ Exception -> 0x026f }
        r8 = android.graphics.BitmapFactory.decodeByteArray(r8, r4, r1, r0);	 Catch:{ Exception -> 0x026f }
        r7.cover = r8;	 Catch:{ Exception -> 0x026f }
        r8 = r7.cover;	 Catch:{ Exception -> 0x026f }
        if (r8 == 0) goto L_0x02a4;
    L_0x022e:
        r8 = r7.cover;	 Catch:{ Exception -> 0x026f }
        r8 = r8.getWidth();	 Catch:{ Exception -> 0x026f }
        r0 = r7.cover;	 Catch:{ Exception -> 0x026f }
        r0 = r0.getHeight();	 Catch:{ Exception -> 0x026f }
        r8 = java.lang.Math.max(r8, r0);	 Catch:{ Exception -> 0x026f }
        r8 = (float) r8;	 Catch:{ Exception -> 0x026f }
        r0 = NUM; // 0x42var_ float:120.0 double:5.548480205E-315;
        r8 = r8 / r0;
        r0 = 0;
        r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1));
        if (r0 <= 0) goto L_0x0262;
    L_0x0247:
        r0 = r7.cover;	 Catch:{ Exception -> 0x026f }
        r1 = r7.cover;	 Catch:{ Exception -> 0x026f }
        r1 = r1.getWidth();	 Catch:{ Exception -> 0x026f }
        r1 = (float) r1;	 Catch:{ Exception -> 0x026f }
        r1 = r1 / r8;
        r1 = (int) r1;	 Catch:{ Exception -> 0x026f }
        r2 = r7.cover;	 Catch:{ Exception -> 0x026f }
        r2 = r2.getHeight();	 Catch:{ Exception -> 0x026f }
        r2 = (float) r2;	 Catch:{ Exception -> 0x026f }
        r2 = r2 / r8;
        r8 = (int) r2;	 Catch:{ Exception -> 0x026f }
        r8 = android.graphics.Bitmap.createScaledBitmap(r0, r1, r8, r5);	 Catch:{ Exception -> 0x026f }
        r7.smallCover = r8;	 Catch:{ Exception -> 0x026f }
        goto L_0x0266;
    L_0x0262:
        r8 = r7.cover;	 Catch:{ Exception -> 0x026f }
        r7.smallCover = r8;	 Catch:{ Exception -> 0x026f }
    L_0x0266:
        r8 = r7.smallCover;	 Catch:{ Exception -> 0x026f }
        if (r8 != 0) goto L_0x02a4;
    L_0x026a:
        r8 = r7.cover;	 Catch:{ Exception -> 0x026f }
        r7.smallCover = r8;	 Catch:{ Exception -> 0x026f }
        goto L_0x02a4;
    L_0x026f:
        r8 = move-exception;
        r8.printStackTrace();
        goto L_0x02a4;
    L_0x0274:
        r0 = r7.composer;
        if (r0 == 0) goto L_0x0282;
    L_0x0278:
        r0 = r0.trim();
        r0 = r0.length();
        if (r0 != 0) goto L_0x02a4;
    L_0x0282:
        r8 = r8.readString(r2);
        r7.composer = r8;
        goto L_0x02a4;
    L_0x0289:
        r8 = r8.readString(r2);
        r7.comment = r8;
        goto L_0x02a4;
    L_0x0290:
        r8 = r8.readString(r2);
        r7.artist = r8;
        goto L_0x02a4;
    L_0x0297:
        r8 = r8.readString(r2);
        r7.albumArtist = r8;
        goto L_0x02a4;
    L_0x029e:
        r8 = r8.readString(r2);
        r7.album = r8;
    L_0x02a4:
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
