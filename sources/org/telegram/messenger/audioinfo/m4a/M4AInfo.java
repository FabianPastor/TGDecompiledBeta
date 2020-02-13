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

    /* access modifiers changed from: package-private */
    public void ftyp(MP4Atom mP4Atom) throws IOException {
        if (LOGGER.isLoggable(this.debugLevel)) {
            LOGGER.log(this.debugLevel, mP4Atom.toString());
        }
        this.brand = mP4Atom.readString(4, "ISO8859_1").trim();
        if (this.brand.matches("M4V|MP4|mp42|isom")) {
            Logger logger = LOGGER;
            logger.warning(mP4Atom.getPath() + ": brand=" + this.brand + " (experimental)");
        } else if (!this.brand.matches("M4A|M4P")) {
            Logger logger2 = LOGGER;
            logger2.warning(mP4Atom.getPath() + ": brand=" + this.brand + " (expected M4A or M4P)");
        }
        this.version = String.valueOf(mP4Atom.readInt());
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
        this.speed = mP4Atom.readIntegerFixedPoint();
        this.volume = mP4Atom.readShortFixedPoint();
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
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void data(org.telegram.messenger.audioinfo.m4a.MP4Atom r8) throws java.io.IOException {
        /*
            r7 = this;
            java.util.logging.Logger r0 = LOGGER
            java.util.logging.Level r1 = r7.debugLevel
            boolean r0 = r0.isLoggable(r1)
            if (r0 == 0) goto L_0x0015
            java.util.logging.Logger r0 = LOGGER
            java.util.logging.Level r1 = r7.debugLevel
            java.lang.String r2 = r8.toString()
            r0.log(r1, r2)
        L_0x0015:
            r0 = 4
            r8.skip(r0)
            r8.skip(r0)
            org.telegram.messenger.audioinfo.m4a.MP4Box r1 = r8.getParent()
            java.lang.String r1 = r1.getType()
            r2 = -1
            int r3 = r1.hashCode()
            r4 = 0
            r5 = 1
            r6 = 2
            switch(r3) {
                case 2954818: goto L_0x0111;
                case 3059752: goto L_0x0107;
                case 3060304: goto L_0x00fd;
                case 3060591: goto L_0x00f2;
                case 3083677: goto L_0x00e7;
                case 3177818: goto L_0x00dc;
                case 3511163: goto L_0x00d1;
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
            }
        L_0x002f:
            goto L_0x011b
        L_0x0031:
            java.lang.String r3 = "©wrt"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 5
            goto L_0x011c
        L_0x003d:
            java.lang.String r3 = "©nam"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 16
            goto L_0x011c
        L_0x004a:
            java.lang.String r3 = "©lyr"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 15
            goto L_0x011c
        L_0x0057:
            java.lang.String r3 = "©grp"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 14
            goto L_0x011c
        L_0x0064:
            java.lang.String r3 = "©gen"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 13
            goto L_0x011c
        L_0x0071:
            java.lang.String r3 = "©day"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 10
            goto L_0x011c
        L_0x007e:
            java.lang.String r3 = "©cpy"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 9
            goto L_0x011c
        L_0x008b:
            java.lang.String r3 = "©com"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 4
            goto L_0x011c
        L_0x0097:
            java.lang.String r3 = "©cmt"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 3
            goto L_0x011c
        L_0x00a3:
            java.lang.String r3 = "©alb"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 0
            goto L_0x011c
        L_0x00af:
            java.lang.String r3 = "©ART"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 2
            goto L_0x011c
        L_0x00ba:
            java.lang.String r3 = "trkn"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 19
            goto L_0x011c
        L_0x00c6:
            java.lang.String r3 = "tmpo"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 18
            goto L_0x011c
        L_0x00d1:
            java.lang.String r3 = "rtng"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 17
            goto L_0x011c
        L_0x00dc:
            java.lang.String r3 = "gnre"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 12
            goto L_0x011c
        L_0x00e7:
            java.lang.String r3 = "disk"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 11
            goto L_0x011c
        L_0x00f2:
            java.lang.String r3 = "cprt"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 8
            goto L_0x011c
        L_0x00fd:
            java.lang.String r3 = "cpil"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 7
            goto L_0x011c
        L_0x0107:
            java.lang.String r3 = "covr"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 6
            goto L_0x011c
        L_0x0111:
            java.lang.String r3 = "aART"
            boolean r1 = r1.equals(r3)
            if (r1 == 0) goto L_0x011b
            r1 = 1
            goto L_0x011c
        L_0x011b:
            r1 = -1
        L_0x011c:
            java.lang.String r2 = "UTF-8"
            switch(r1) {
                case 0: goto L_0x029d;
                case 1: goto L_0x0296;
                case 2: goto L_0x028f;
                case 3: goto L_0x0288;
                case 4: goto L_0x0273;
                case 5: goto L_0x0273;
                case 6: goto L_0x01f2;
                case 7: goto L_0x01ea;
                case 8: goto L_0x01d4;
                case 9: goto L_0x01d4;
                case 10: goto L_0x01b6;
                case 11: goto L_0x01a5;
                case 12: goto L_0x0172;
                case 13: goto L_0x015c;
                case 14: goto L_0x0154;
                case 15: goto L_0x014c;
                case 16: goto L_0x0144;
                case 17: goto L_0x013c;
                case 18: goto L_0x0134;
                case 19: goto L_0x0123;
                default: goto L_0x0121;
            }
        L_0x0121:
            goto L_0x02a3
        L_0x0123:
            r8.skip(r6)
            short r0 = r8.readShort()
            r7.track = r0
            short r8 = r8.readShort()
            r7.tracks = r8
            goto L_0x02a3
        L_0x0134:
            short r8 = r8.readShort()
            r7.tempo = r8
            goto L_0x02a3
        L_0x013c:
            byte r8 = r8.readByte()
            r7.rating = r8
            goto L_0x02a3
        L_0x0144:
            java.lang.String r8 = r8.readString(r2)
            r7.title = r8
            goto L_0x02a3
        L_0x014c:
            java.lang.String r8 = r8.readString(r2)
            r7.lyrics = r8
            goto L_0x02a3
        L_0x0154:
            java.lang.String r8 = r8.readString(r2)
            r7.grouping = r8
            goto L_0x02a3
        L_0x015c:
            java.lang.String r0 = r7.genre
            if (r0 == 0) goto L_0x016a
            java.lang.String r0 = r0.trim()
            int r0 = r0.length()
            if (r0 != 0) goto L_0x02a3
        L_0x016a:
            java.lang.String r8 = r8.readString(r2)
            r7.genre = r8
            goto L_0x02a3
        L_0x0172:
            java.lang.String r0 = r7.genre
            if (r0 == 0) goto L_0x0180
            java.lang.String r0 = r0.trim()
            int r0 = r0.length()
            if (r0 != 0) goto L_0x02a3
        L_0x0180:
            long r0 = r8.getRemaining()
            r3 = 2
            int r6 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r6 != 0) goto L_0x019d
            short r8 = r8.readShort()
            int r8 = r8 - r5
            org.telegram.messenger.audioinfo.mp3.ID3v1Genre r8 = org.telegram.messenger.audioinfo.mp3.ID3v1Genre.getGenre(r8)
            if (r8 == 0) goto L_0x02a3
            java.lang.String r8 = r8.getDescription()
            r7.genre = r8
            goto L_0x02a3
        L_0x019d:
            java.lang.String r8 = r8.readString(r2)
            r7.genre = r8
            goto L_0x02a3
        L_0x01a5:
            r8.skip(r6)
            short r0 = r8.readShort()
            r7.disc = r0
            short r8 = r8.readShort()
            r7.discs = r8
            goto L_0x02a3
        L_0x01b6:
            java.lang.String r8 = r8.readString(r2)
            java.lang.String r8 = r8.trim()
            int r1 = r8.length()
            if (r1 < r0) goto L_0x02a3
            java.lang.String r8 = r8.substring(r4, r0)     // Catch:{ NumberFormatException -> 0x02a3 }
            java.lang.Short r8 = java.lang.Short.valueOf(r8)     // Catch:{ NumberFormatException -> 0x02a3 }
            short r8 = r8.shortValue()     // Catch:{ NumberFormatException -> 0x02a3 }
            r7.year = r8     // Catch:{ NumberFormatException -> 0x02a3 }
            goto L_0x02a3
        L_0x01d4:
            java.lang.String r0 = r7.copyright
            if (r0 == 0) goto L_0x01e2
            java.lang.String r0 = r0.trim()
            int r0 = r0.length()
            if (r0 != 0) goto L_0x02a3
        L_0x01e2:
            java.lang.String r8 = r8.readString(r2)
            r7.copyright = r8
            goto L_0x02a3
        L_0x01ea:
            boolean r8 = r8.readBoolean()
            r7.compilation = r8
            goto L_0x02a3
        L_0x01f2:
            byte[] r8 = r8.readBytes()     // Catch:{ Exception -> 0x026e }
            android.graphics.BitmapFactory$Options r0 = new android.graphics.BitmapFactory$Options     // Catch:{ Exception -> 0x026e }
            r0.<init>()     // Catch:{ Exception -> 0x026e }
            r0.inJustDecodeBounds = r5     // Catch:{ Exception -> 0x026e }
            r0.inSampleSize = r5     // Catch:{ Exception -> 0x026e }
            int r1 = r8.length     // Catch:{ Exception -> 0x026e }
            android.graphics.BitmapFactory.decodeByteArray(r8, r4, r1, r0)     // Catch:{ Exception -> 0x026e }
            int r1 = r0.outWidth     // Catch:{ Exception -> 0x026e }
            r2 = 800(0x320, float:1.121E-42)
            if (r1 > r2) goto L_0x020d
            int r1 = r0.outHeight     // Catch:{ Exception -> 0x026e }
            if (r1 <= r2) goto L_0x0220
        L_0x020d:
            int r1 = r0.outWidth     // Catch:{ Exception -> 0x026e }
            int r3 = r0.outHeight     // Catch:{ Exception -> 0x026e }
            int r1 = java.lang.Math.max(r1, r3)     // Catch:{ Exception -> 0x026e }
        L_0x0215:
            if (r1 <= r2) goto L_0x0220
            int r3 = r0.inSampleSize     // Catch:{ Exception -> 0x026e }
            int r3 = r3 * 2
            r0.inSampleSize = r3     // Catch:{ Exception -> 0x026e }
            int r1 = r1 / 2
            goto L_0x0215
        L_0x0220:
            r0.inJustDecodeBounds = r4     // Catch:{ Exception -> 0x026e }
            int r1 = r8.length     // Catch:{ Exception -> 0x026e }
            android.graphics.Bitmap r8 = android.graphics.BitmapFactory.decodeByteArray(r8, r4, r1, r0)     // Catch:{ Exception -> 0x026e }
            r7.cover = r8     // Catch:{ Exception -> 0x026e }
            android.graphics.Bitmap r8 = r7.cover     // Catch:{ Exception -> 0x026e }
            if (r8 == 0) goto L_0x02a3
            android.graphics.Bitmap r8 = r7.cover     // Catch:{ Exception -> 0x026e }
            int r8 = r8.getWidth()     // Catch:{ Exception -> 0x026e }
            android.graphics.Bitmap r0 = r7.cover     // Catch:{ Exception -> 0x026e }
            int r0 = r0.getHeight()     // Catch:{ Exception -> 0x026e }
            int r8 = java.lang.Math.max(r8, r0)     // Catch:{ Exception -> 0x026e }
            float r8 = (float) r8     // Catch:{ Exception -> 0x026e }
            r0 = 1123024896(0x42var_, float:120.0)
            float r8 = r8 / r0
            r0 = 0
            int r0 = (r8 > r0 ? 1 : (r8 == r0 ? 0 : -1))
            if (r0 <= 0) goto L_0x0261
            android.graphics.Bitmap r0 = r7.cover     // Catch:{ Exception -> 0x026e }
            android.graphics.Bitmap r1 = r7.cover     // Catch:{ Exception -> 0x026e }
            int r1 = r1.getWidth()     // Catch:{ Exception -> 0x026e }
            float r1 = (float) r1     // Catch:{ Exception -> 0x026e }
            float r1 = r1 / r8
            int r1 = (int) r1     // Catch:{ Exception -> 0x026e }
            android.graphics.Bitmap r2 = r7.cover     // Catch:{ Exception -> 0x026e }
            int r2 = r2.getHeight()     // Catch:{ Exception -> 0x026e }
            float r2 = (float) r2     // Catch:{ Exception -> 0x026e }
            float r2 = r2 / r8
            int r8 = (int) r2     // Catch:{ Exception -> 0x026e }
            android.graphics.Bitmap r8 = android.graphics.Bitmap.createScaledBitmap(r0, r1, r8, r5)     // Catch:{ Exception -> 0x026e }
            r7.smallCover = r8     // Catch:{ Exception -> 0x026e }
            goto L_0x0265
        L_0x0261:
            android.graphics.Bitmap r8 = r7.cover     // Catch:{ Exception -> 0x026e }
            r7.smallCover = r8     // Catch:{ Exception -> 0x026e }
        L_0x0265:
            android.graphics.Bitmap r8 = r7.smallCover     // Catch:{ Exception -> 0x026e }
            if (r8 != 0) goto L_0x02a3
            android.graphics.Bitmap r8 = r7.cover     // Catch:{ Exception -> 0x026e }
            r7.smallCover = r8     // Catch:{ Exception -> 0x026e }
            goto L_0x02a3
        L_0x026e:
            r8 = move-exception
            r8.printStackTrace()
            goto L_0x02a3
        L_0x0273:
            java.lang.String r0 = r7.composer
            if (r0 == 0) goto L_0x0281
            java.lang.String r0 = r0.trim()
            int r0 = r0.length()
            if (r0 != 0) goto L_0x02a3
        L_0x0281:
            java.lang.String r8 = r8.readString(r2)
            r7.composer = r8
            goto L_0x02a3
        L_0x0288:
            java.lang.String r8 = r8.readString(r2)
            r7.comment = r8
            goto L_0x02a3
        L_0x028f:
            java.lang.String r8 = r8.readString(r2)
            r7.artist = r8
            goto L_0x02a3
        L_0x0296:
            java.lang.String r8 = r8.readString(r2)
            r7.albumArtist = r8
            goto L_0x02a3
        L_0x029d:
            java.lang.String r8 = r8.readString(r2)
            r7.album = r8
        L_0x02a3:
            return
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
