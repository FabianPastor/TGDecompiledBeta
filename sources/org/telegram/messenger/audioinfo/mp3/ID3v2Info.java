package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.telegram.messenger.audioinfo.AudioInfo;
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
                    } catch (ID3v2Exception e) {
                        if (LOGGER.isLoggable(level)) {
                            LOGGER.log(level, String.format("ID3 exception occured in frame %s: %s", new Object[]{iD3v2FrameHeader.getFrameId(), e.getMessage()}));
                        }
                        data = frameBody.getData();
                        remainingLength = frameBody.getRemainingLength();
                    }
                    try {
                        data = frameBody.getData();
                        remainingLength = frameBody.getRemainingLength();
                        data.skipFully(remainingLength);
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
            }
            tagBody.getData().skipFully(tagBody.getRemainingLength());
            if (iD3v2TagHeader.getFooterSize() > null) {
                inputStream.skip((long) iD3v2TagHeader.getFooterSize());
            }
        }
    }

    void parseFrame(org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody r9) throws java.io.IOException, org.telegram.messenger.audioinfo.mp3.ID3v2Exception {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
*/
        /*
        r8 = this;
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r0 = r0.isLoggable(r1);
        if (r0 == 0) goto L_0x002a;
    L_0x000a:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Parsing frame: ";
        r2.append(r3);
        r3 = r9.getFrameHeader();
        r3 = r3.getFrameId();
        r2.append(r3);
        r2 = r2.toString();
        r0.log(r1, r2);
    L_0x002a:
        r0 = r9.getFrameHeader();
        r0 = r0.getFrameId();
        r1 = -1;
        r2 = r0.hashCode();
        r3 = 2;
        r4 = 4;
        r5 = 3;
        r6 = 0;
        r7 = 1;
        switch(r2) {
            case 66913: goto L_0x01b2;
            case 79210: goto L_0x01a8;
            case 82815: goto L_0x019e;
            case 82878: goto L_0x0193;
            case 82880: goto L_0x0188;
            case 82881: goto L_0x017e;
            case 82883: goto L_0x0173;
            case 83149: goto L_0x0168;
            case 83253: goto L_0x015d;
            case 83254: goto L_0x0151;
            case 83269: goto L_0x0145;
            case 83341: goto L_0x0139;
            case 83377: goto L_0x012d;
            case 83378: goto L_0x0121;
            case 83552: goto L_0x0115;
            case 84125: goto L_0x0109;
            case 2015625: goto L_0x00fe;
            case 2074380: goto L_0x00f3;
            case 2567331: goto L_0x00e8;
            case 2569298: goto L_0x00dd;
            case 2569357: goto L_0x00d1;
            case 2569358: goto L_0x00c5;
            case 2569360: goto L_0x00b9;
            case 2570401: goto L_0x00ad;
            case 2575250: goto L_0x00a1;
            case 2575251: goto L_0x0095;
            case 2577697: goto L_0x0089;
            case 2581512: goto L_0x007d;
            case 2581513: goto L_0x0071;
            case 2581856: goto L_0x0065;
            case 2583398: goto L_0x0059;
            case 2590194: goto L_0x004d;
            case 2614438: goto L_0x0041;
            default: goto L_0x003f;
        };
    L_0x003f:
        goto L_0x01bb;
    L_0x0041:
        r2 = "USLT";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0049:
        r1 = 32;
        goto L_0x01bb;
    L_0x004d:
        r2 = "TYER";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0055:
        r1 = 30;
        goto L_0x01bb;
    L_0x0059:
        r2 = "TRCK";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0061:
        r1 = 24;
        goto L_0x01bb;
    L_0x0065:
        r2 = "TPOS";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x006d:
        r1 = 22;
        goto L_0x01bb;
    L_0x0071:
        r2 = "TPE2";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0079:
        r1 = 20;
        goto L_0x01bb;
    L_0x007d:
        r2 = "TPE1";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0085:
        r1 = 18;
        goto L_0x01bb;
    L_0x0089:
        r2 = "TLEN";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0091:
        r1 = 16;
        goto L_0x01bb;
    L_0x0095:
        r2 = "TIT2";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x009d:
        r1 = 28;
        goto L_0x01bb;
    L_0x00a1:
        r2 = "TIT1";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x00a9:
        r1 = 26;
        goto L_0x01bb;
    L_0x00ad:
        r2 = "TDRC";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x00b5:
        r1 = 14;
        goto L_0x01bb;
    L_0x00b9:
        r2 = "TCOP";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x00c1:
        r1 = 13;
        goto L_0x01bb;
    L_0x00c5:
        r2 = "TCON";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x00cd:
        r1 = 11;
        goto L_0x01bb;
    L_0x00d1:
        r2 = "TCOM";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x00d9:
        r1 = 9;
        goto L_0x01bb;
    L_0x00dd:
        r2 = "TCMP";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x00e5:
        r1 = 7;
        goto L_0x01bb;
    L_0x00e8:
        r2 = "TALB";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x00f0:
        r1 = 5;
        goto L_0x01bb;
    L_0x00f3:
        r2 = "COMM";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x00fb:
        r1 = r5;
        goto L_0x01bb;
    L_0x00fe:
        r2 = "APIC";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0106:
        r1 = r7;
        goto L_0x01bb;
    L_0x0109:
        r2 = "ULT";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0111:
        r1 = 31;
        goto L_0x01bb;
    L_0x0115:
        r2 = "TYE";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x011d:
        r1 = 29;
        goto L_0x01bb;
    L_0x0121:
        r2 = "TT2";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0129:
        r1 = 27;
        goto L_0x01bb;
    L_0x012d:
        r2 = "TT1";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0135:
        r1 = 25;
        goto L_0x01bb;
    L_0x0139:
        r2 = "TRK";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0141:
        r1 = 23;
        goto L_0x01bb;
    L_0x0145:
        r2 = "TPA";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x014d:
        r1 = 21;
        goto L_0x01bb;
    L_0x0151:
        r2 = "TP2";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0159:
        r1 = 19;
        goto L_0x01bb;
    L_0x015d:
        r2 = "TP1";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0165:
        r1 = 17;
        goto L_0x01bb;
    L_0x0168:
        r2 = "TLE";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0170:
        r1 = 15;
        goto L_0x01bb;
    L_0x0173:
        r2 = "TCR";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x017b:
        r1 = 12;
        goto L_0x01bb;
    L_0x017e:
        r2 = "TCP";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0186:
        r1 = 6;
        goto L_0x01bb;
    L_0x0188:
        r2 = "TCO";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x0190:
        r1 = 10;
        goto L_0x01bb;
    L_0x0193:
        r2 = "TCM";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x019b:
        r1 = 8;
        goto L_0x01bb;
    L_0x019e:
        r2 = "TAL";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x01a6:
        r1 = r4;
        goto L_0x01bb;
    L_0x01a8:
        r2 = "PIC";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x01b0:
        r1 = r6;
        goto L_0x01bb;
    L_0x01b2:
        r2 = "COM";
        r0 = r0.equals(r2);
        if (r0 == 0) goto L_0x01bb;
    L_0x01ba:
        r1 = r3;
    L_0x01bb:
        r0 = 47;
        switch(r1) {
            case 0: goto L_0x0475;
            case 1: goto L_0x0475;
            case 2: goto L_0x0459;
            case 3: goto L_0x0459;
            case 4: goto L_0x0451;
            case 5: goto L_0x0451;
            case 6: goto L_0x0443;
            case 7: goto L_0x0443;
            case 8: goto L_0x043b;
            case 9: goto L_0x043b;
            case 10: goto L_0x03f0;
            case 11: goto L_0x03f0;
            case 12: goto L_0x03e8;
            case 13: goto L_0x03e8;
            case 14: goto L_0x03aa;
            case 15: goto L_0x0376;
            case 16: goto L_0x0376;
            case 17: goto L_0x036e;
            case 18: goto L_0x036e;
            case 19: goto L_0x0366;
            case 20: goto L_0x0366;
            case 21: goto L_0x02c0;
            case 22: goto L_0x02c0;
            case 23: goto L_0x021a;
            case 24: goto L_0x021a;
            case 25: goto L_0x0212;
            case 26: goto L_0x0212;
            case 27: goto L_0x020a;
            case 28: goto L_0x020a;
            case 29: goto L_0x01d0;
            case 30: goto L_0x01d0;
            case 31: goto L_0x01c2;
            case 32: goto L_0x01c2;
            default: goto L_0x01c0;
        };
    L_0x01c0:
        goto L_0x050e;
    L_0x01c2:
        r0 = r8.lyrics;
        if (r0 != 0) goto L_0x050e;
    L_0x01c6:
        r9 = r8.parseCommentOrUnsynchronizedLyricsFrame(r9);
        r9 = r9.text;
        r8.lyrics = r9;
        goto L_0x050e;
    L_0x01d0:
        r9 = r8.parseTextFrame(r9);
        r0 = r9.length();
        if (r0 <= 0) goto L_0x050e;
    L_0x01da:
        r0 = java.lang.Short.valueOf(r9);	 Catch:{ NumberFormatException -> 0x01e6 }
        r0 = r0.shortValue();	 Catch:{ NumberFormatException -> 0x01e6 }
        r8.year = r0;	 Catch:{ NumberFormatException -> 0x01e6 }
        goto L_0x050e;
    L_0x01e6:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r0 = r0.isLoggable(r1);
        if (r0 == 0) goto L_0x050e;
    L_0x01f0:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Could not parse year: ";
        r2.append(r3);
        r2.append(r9);
        r9 = r2.toString();
        r0.log(r1, r9);
        goto L_0x050e;
    L_0x020a:
        r9 = r8.parseTextFrame(r9);
        r8.title = r9;
        goto L_0x050e;
    L_0x0212:
        r9 = r8.parseTextFrame(r9);
        r8.grouping = r9;
        goto L_0x050e;
    L_0x021a:
        r9 = r8.parseTextFrame(r9);
        r1 = r9.length();
        if (r1 <= 0) goto L_0x050e;
    L_0x0224:
        r0 = r9.indexOf(r0);
        if (r0 >= 0) goto L_0x025a;
    L_0x022a:
        r0 = java.lang.Short.valueOf(r9);	 Catch:{ NumberFormatException -> 0x0236 }
        r0 = r0.shortValue();	 Catch:{ NumberFormatException -> 0x0236 }
        r8.track = r0;	 Catch:{ NumberFormatException -> 0x0236 }
        goto L_0x050e;
    L_0x0236:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r0 = r0.isLoggable(r1);
        if (r0 == 0) goto L_0x050e;
    L_0x0240:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Could not parse track number: ";
        r2.append(r3);
        r2.append(r9);
        r9 = r2.toString();
        r0.log(r1, r9);
        goto L_0x050e;
    L_0x025a:
        r1 = r9.substring(r6, r0);	 Catch:{ NumberFormatException -> 0x0269 }
        r1 = java.lang.Short.valueOf(r1);	 Catch:{ NumberFormatException -> 0x0269 }
        r1 = r1.shortValue();	 Catch:{ NumberFormatException -> 0x0269 }
        r8.track = r1;	 Catch:{ NumberFormatException -> 0x0269 }
        goto L_0x028b;
    L_0x0269:
        r1 = LOGGER;
        r2 = r8.debugLevel;
        r1 = r1.isLoggable(r2);
        if (r1 == 0) goto L_0x028b;
    L_0x0273:
        r1 = LOGGER;
        r2 = r8.debugLevel;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Could not parse track number: ";
        r3.append(r4);
        r3.append(r9);
        r3 = r3.toString();
        r1.log(r2, r3);
    L_0x028b:
        r0 = r0 + r7;
        r0 = r9.substring(r0);	 Catch:{ NumberFormatException -> 0x029c }
        r0 = java.lang.Short.valueOf(r0);	 Catch:{ NumberFormatException -> 0x029c }
        r0 = r0.shortValue();	 Catch:{ NumberFormatException -> 0x029c }
        r8.tracks = r0;	 Catch:{ NumberFormatException -> 0x029c }
        goto L_0x050e;
    L_0x029c:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r0 = r0.isLoggable(r1);
        if (r0 == 0) goto L_0x050e;
    L_0x02a6:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Could not parse number of tracks: ";
        r2.append(r3);
        r2.append(r9);
        r9 = r2.toString();
        r0.log(r1, r9);
        goto L_0x050e;
    L_0x02c0:
        r9 = r8.parseTextFrame(r9);
        r1 = r9.length();
        if (r1 <= 0) goto L_0x050e;
    L_0x02ca:
        r0 = r9.indexOf(r0);
        if (r0 >= 0) goto L_0x0300;
    L_0x02d0:
        r0 = java.lang.Short.valueOf(r9);	 Catch:{ NumberFormatException -> 0x02dc }
        r0 = r0.shortValue();	 Catch:{ NumberFormatException -> 0x02dc }
        r8.disc = r0;	 Catch:{ NumberFormatException -> 0x02dc }
        goto L_0x050e;
    L_0x02dc:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r0 = r0.isLoggable(r1);
        if (r0 == 0) goto L_0x050e;
    L_0x02e6:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Could not parse disc number: ";
        r2.append(r3);
        r2.append(r9);
        r9 = r2.toString();
        r0.log(r1, r9);
        goto L_0x050e;
    L_0x0300:
        r1 = r9.substring(r6, r0);	 Catch:{ NumberFormatException -> 0x030f }
        r1 = java.lang.Short.valueOf(r1);	 Catch:{ NumberFormatException -> 0x030f }
        r1 = r1.shortValue();	 Catch:{ NumberFormatException -> 0x030f }
        r8.disc = r1;	 Catch:{ NumberFormatException -> 0x030f }
        goto L_0x0331;
    L_0x030f:
        r1 = LOGGER;
        r2 = r8.debugLevel;
        r1 = r1.isLoggable(r2);
        if (r1 == 0) goto L_0x0331;
    L_0x0319:
        r1 = LOGGER;
        r2 = r8.debugLevel;
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "Could not parse disc number: ";
        r3.append(r4);
        r3.append(r9);
        r3 = r3.toString();
        r1.log(r2, r3);
    L_0x0331:
        r0 = r0 + r7;
        r0 = r9.substring(r0);	 Catch:{ NumberFormatException -> 0x0342 }
        r0 = java.lang.Short.valueOf(r0);	 Catch:{ NumberFormatException -> 0x0342 }
        r0 = r0.shortValue();	 Catch:{ NumberFormatException -> 0x0342 }
        r8.discs = r0;	 Catch:{ NumberFormatException -> 0x0342 }
        goto L_0x050e;
    L_0x0342:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r0 = r0.isLoggable(r1);
        if (r0 == 0) goto L_0x050e;
    L_0x034c:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Could not parse number of discs: ";
        r2.append(r3);
        r2.append(r9);
        r9 = r2.toString();
        r0.log(r1, r9);
        goto L_0x050e;
    L_0x0366:
        r9 = r8.parseTextFrame(r9);
        r8.albumArtist = r9;
        goto L_0x050e;
    L_0x036e:
        r9 = r8.parseTextFrame(r9);
        r8.artist = r9;
        goto L_0x050e;
    L_0x0376:
        r9 = r8.parseTextFrame(r9);
        r0 = java.lang.Long.valueOf(r9);	 Catch:{ NumberFormatException -> 0x0386 }
        r0 = r0.longValue();	 Catch:{ NumberFormatException -> 0x0386 }
        r8.duration = r0;	 Catch:{ NumberFormatException -> 0x0386 }
        goto L_0x050e;
    L_0x0386:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r0 = r0.isLoggable(r1);
        if (r0 == 0) goto L_0x050e;
    L_0x0390:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Could not parse track duration: ";
        r2.append(r3);
        r2.append(r9);
        r9 = r2.toString();
        r0.log(r1, r9);
        goto L_0x050e;
    L_0x03aa:
        r9 = r8.parseTextFrame(r9);
        r0 = r9.length();
        if (r0 < r4) goto L_0x050e;
    L_0x03b4:
        r0 = r9.substring(r6, r4);	 Catch:{ NumberFormatException -> 0x03c4 }
        r0 = java.lang.Short.valueOf(r0);	 Catch:{ NumberFormatException -> 0x03c4 }
        r0 = r0.shortValue();	 Catch:{ NumberFormatException -> 0x03c4 }
        r8.year = r0;	 Catch:{ NumberFormatException -> 0x03c4 }
        goto L_0x050e;
    L_0x03c4:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r0 = r0.isLoggable(r1);
        if (r0 == 0) goto L_0x050e;
    L_0x03ce:
        r0 = LOGGER;
        r1 = r8.debugLevel;
        r2 = new java.lang.StringBuilder;
        r2.<init>();
        r3 = "Could not parse year from: ";
        r2.append(r3);
        r2.append(r9);
        r9 = r2.toString();
        r0.log(r1, r9);
        goto L_0x050e;
    L_0x03e8:
        r9 = r8.parseTextFrame(r9);
        r8.copyright = r9;
        goto L_0x050e;
    L_0x03f0:
        r9 = r8.parseTextFrame(r9);
        r0 = r9.length();
        if (r0 <= 0) goto L_0x050e;
    L_0x03fa:
        r8.genre = r9;
        r0 = 0;
        r1 = r9.charAt(r6);	 Catch:{ NumberFormatException -> 0x050e }
        r2 = 40;	 Catch:{ NumberFormatException -> 0x050e }
        if (r1 != r2) goto L_0x0429;	 Catch:{ NumberFormatException -> 0x050e }
    L_0x0405:
        r1 = 41;	 Catch:{ NumberFormatException -> 0x050e }
        r1 = r9.indexOf(r1);	 Catch:{ NumberFormatException -> 0x050e }
        if (r1 <= r7) goto L_0x0431;	 Catch:{ NumberFormatException -> 0x050e }
    L_0x040d:
        r0 = r9.substring(r7, r1);	 Catch:{ NumberFormatException -> 0x050e }
        r0 = java.lang.Integer.parseInt(r0);	 Catch:{ NumberFormatException -> 0x050e }
        r0 = org.telegram.messenger.audioinfo.mp3.ID3v1Genre.getGenre(r0);	 Catch:{ NumberFormatException -> 0x050e }
        if (r0 != 0) goto L_0x0431;	 Catch:{ NumberFormatException -> 0x050e }
    L_0x041b:
        r2 = r9.length();	 Catch:{ NumberFormatException -> 0x050e }
        r1 = r1 + r7;	 Catch:{ NumberFormatException -> 0x050e }
        if (r2 <= r1) goto L_0x0431;	 Catch:{ NumberFormatException -> 0x050e }
    L_0x0422:
        r9 = r9.substring(r1);	 Catch:{ NumberFormatException -> 0x050e }
        r8.genre = r9;	 Catch:{ NumberFormatException -> 0x050e }
        goto L_0x0431;	 Catch:{ NumberFormatException -> 0x050e }
    L_0x0429:
        r9 = java.lang.Integer.parseInt(r9);	 Catch:{ NumberFormatException -> 0x050e }
        r0 = org.telegram.messenger.audioinfo.mp3.ID3v1Genre.getGenre(r9);	 Catch:{ NumberFormatException -> 0x050e }
    L_0x0431:
        if (r0 == 0) goto L_0x050e;	 Catch:{ NumberFormatException -> 0x050e }
    L_0x0433:
        r9 = r0.getDescription();	 Catch:{ NumberFormatException -> 0x050e }
        r8.genre = r9;	 Catch:{ NumberFormatException -> 0x050e }
        goto L_0x050e;
    L_0x043b:
        r9 = r8.parseTextFrame(r9);
        r8.composer = r9;
        goto L_0x050e;
    L_0x0443:
        r0 = "1";
        r9 = r8.parseTextFrame(r9);
        r9 = r0.equals(r9);
        r8.compilation = r9;
        goto L_0x050e;
    L_0x0451:
        r9 = r8.parseTextFrame(r9);
        r8.album = r9;
        goto L_0x050e;
    L_0x0459:
        r9 = r8.parseCommentOrUnsynchronizedLyricsFrame(r9);
        r0 = r8.comment;
        if (r0 == 0) goto L_0x046f;
    L_0x0461:
        r0 = r9.description;
        if (r0 == 0) goto L_0x046f;
    L_0x0465:
        r0 = "";
        r1 = r9.description;
        r0 = r0.equals(r1);
        if (r0 == 0) goto L_0x050e;
    L_0x046f:
        r9 = r9.text;
        r8.comment = r9;
        goto L_0x050e;
    L_0x0475:
        r0 = r8.cover;
        if (r0 == 0) goto L_0x047d;
    L_0x0479:
        r0 = r8.coverPictureType;
        if (r0 == r5) goto L_0x050e;
    L_0x047d:
        r9 = r8.parseAttachedPictureFrame(r9);
        r0 = r8.cover;
        if (r0 == 0) goto L_0x048d;
    L_0x0485:
        r0 = r9.type;
        if (r0 == r5) goto L_0x048d;
    L_0x0489:
        r0 = r9.type;
        if (r0 != 0) goto L_0x050e;
    L_0x048d:
        r0 = r9.imageData;	 Catch:{ Throwable -> 0x0506 }
        r1 = new android.graphics.BitmapFactory$Options;	 Catch:{ Throwable -> 0x0506 }
        r1.<init>();	 Catch:{ Throwable -> 0x0506 }
        r1.inJustDecodeBounds = r7;	 Catch:{ Throwable -> 0x0506 }
        r1.inSampleSize = r7;	 Catch:{ Throwable -> 0x0506 }
        r2 = r0.length;	 Catch:{ Throwable -> 0x0506 }
        android.graphics.BitmapFactory.decodeByteArray(r0, r6, r2, r1);	 Catch:{ Throwable -> 0x0506 }
        r2 = r1.outWidth;	 Catch:{ Throwable -> 0x0506 }
        r4 = 800; // 0x320 float:1.121E-42 double:3.953E-321;	 Catch:{ Throwable -> 0x0506 }
        if (r2 > r4) goto L_0x04a6;	 Catch:{ Throwable -> 0x0506 }
    L_0x04a2:
        r2 = r1.outHeight;	 Catch:{ Throwable -> 0x0506 }
        if (r2 <= r4) goto L_0x04b8;	 Catch:{ Throwable -> 0x0506 }
    L_0x04a6:
        r2 = r1.outWidth;	 Catch:{ Throwable -> 0x0506 }
        r5 = r1.outHeight;	 Catch:{ Throwable -> 0x0506 }
        r2 = java.lang.Math.max(r2, r5);	 Catch:{ Throwable -> 0x0506 }
    L_0x04ae:
        if (r2 <= r4) goto L_0x04b8;	 Catch:{ Throwable -> 0x0506 }
    L_0x04b0:
        r5 = r1.inSampleSize;	 Catch:{ Throwable -> 0x0506 }
        r5 = r5 * r3;	 Catch:{ Throwable -> 0x0506 }
        r1.inSampleSize = r5;	 Catch:{ Throwable -> 0x0506 }
        r2 = r2 / 2;	 Catch:{ Throwable -> 0x0506 }
        goto L_0x04ae;	 Catch:{ Throwable -> 0x0506 }
    L_0x04b8:
        r1.inJustDecodeBounds = r6;	 Catch:{ Throwable -> 0x0506 }
        r2 = r0.length;	 Catch:{ Throwable -> 0x0506 }
        r0 = android.graphics.BitmapFactory.decodeByteArray(r0, r6, r2, r1);	 Catch:{ Throwable -> 0x0506 }
        r8.cover = r0;	 Catch:{ Throwable -> 0x0506 }
        r0 = r8.cover;	 Catch:{ Throwable -> 0x0506 }
        if (r0 == 0) goto L_0x050a;	 Catch:{ Throwable -> 0x0506 }
    L_0x04c5:
        r0 = r8.cover;	 Catch:{ Throwable -> 0x0506 }
        r0 = r0.getWidth();	 Catch:{ Throwable -> 0x0506 }
        r1 = r8.cover;	 Catch:{ Throwable -> 0x0506 }
        r1 = r1.getHeight();	 Catch:{ Throwable -> 0x0506 }
        r0 = java.lang.Math.max(r0, r1);	 Catch:{ Throwable -> 0x0506 }
        r0 = (float) r0;	 Catch:{ Throwable -> 0x0506 }
        r1 = NUM; // 0x42f00000 float:120.0 double:5.548480205E-315;	 Catch:{ Throwable -> 0x0506 }
        r0 = r0 / r1;	 Catch:{ Throwable -> 0x0506 }
        r1 = 0;	 Catch:{ Throwable -> 0x0506 }
        r1 = (r0 > r1 ? 1 : (r0 == r1 ? 0 : -1));	 Catch:{ Throwable -> 0x0506 }
        if (r1 <= 0) goto L_0x04f9;	 Catch:{ Throwable -> 0x0506 }
    L_0x04de:
        r1 = r8.cover;	 Catch:{ Throwable -> 0x0506 }
        r2 = r8.cover;	 Catch:{ Throwable -> 0x0506 }
        r2 = r2.getWidth();	 Catch:{ Throwable -> 0x0506 }
        r2 = (float) r2;	 Catch:{ Throwable -> 0x0506 }
        r2 = r2 / r0;	 Catch:{ Throwable -> 0x0506 }
        r2 = (int) r2;	 Catch:{ Throwable -> 0x0506 }
        r3 = r8.cover;	 Catch:{ Throwable -> 0x0506 }
        r3 = r3.getHeight();	 Catch:{ Throwable -> 0x0506 }
        r3 = (float) r3;	 Catch:{ Throwable -> 0x0506 }
        r3 = r3 / r0;	 Catch:{ Throwable -> 0x0506 }
        r0 = (int) r3;	 Catch:{ Throwable -> 0x0506 }
        r0 = android.graphics.Bitmap.createScaledBitmap(r1, r2, r0, r7);	 Catch:{ Throwable -> 0x0506 }
        r8.smallCover = r0;	 Catch:{ Throwable -> 0x0506 }
        goto L_0x04fd;	 Catch:{ Throwable -> 0x0506 }
    L_0x04f9:
        r0 = r8.cover;	 Catch:{ Throwable -> 0x0506 }
        r8.smallCover = r0;	 Catch:{ Throwable -> 0x0506 }
    L_0x04fd:
        r0 = r8.smallCover;	 Catch:{ Throwable -> 0x0506 }
        if (r0 != 0) goto L_0x050a;	 Catch:{ Throwable -> 0x0506 }
    L_0x0501:
        r0 = r8.cover;	 Catch:{ Throwable -> 0x0506 }
        r8.smallCover = r0;	 Catch:{ Throwable -> 0x0506 }
        goto L_0x050a;
    L_0x0506:
        r0 = move-exception;
        r0.printStackTrace();
    L_0x050a:
        r9 = r9.type;
        r8.coverPictureType = r9;
    L_0x050e:
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.ID3v2Info.parseFrame(org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody):void");
    }

    String parseTextFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        return iD3v2FrameBody.readFixedLengthString((int) iD3v2FrameBody.getRemainingLength(), iD3v2FrameBody.readEncoding());
    }

    CommentOrUnsynchronizedLyrics parseCommentOrUnsynchronizedLyricsFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        ID3v2Encoding readEncoding = iD3v2FrameBody.readEncoding();
        return new CommentOrUnsynchronizedLyrics(iD3v2FrameBody.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1), iD3v2FrameBody.readZeroTerminatedString(Callback.DEFAULT_DRAG_ANIMATION_DURATION, readEncoding), iD3v2FrameBody.readFixedLengthString((int) iD3v2FrameBody.getRemainingLength(), readEncoding));
    }

    AttachedPicture parseAttachedPictureFrame(ID3v2FrameBody iD3v2FrameBody) throws IOException, ID3v2Exception {
        String toUpperCase;
        ID3v2Encoding readEncoding = iD3v2FrameBody.readEncoding();
        if (iD3v2FrameBody.getTagHeader().getVersion() == 2) {
            toUpperCase = iD3v2FrameBody.readFixedLengthString(3, ID3v2Encoding.ISO_8859_1).toUpperCase();
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
        }
        toUpperCase = iD3v2FrameBody.readZeroTerminatedString(20, ID3v2Encoding.ISO_8859_1);
        return new AttachedPicture(iD3v2FrameBody.getData().readByte(), iD3v2FrameBody.readZeroTerminatedString(Callback.DEFAULT_DRAG_ANIMATION_DURATION, readEncoding), toUpperCase, iD3v2FrameBody.getData().readFully((int) iD3v2FrameBody.getRemainingLength()));
    }
}
