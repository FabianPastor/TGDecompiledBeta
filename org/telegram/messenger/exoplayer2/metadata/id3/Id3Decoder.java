package org.telegram.messenger.exoplayer2.metadata.id3;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.telegram.messenger.exoplayer2.metadata.MetadataInputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Id3Decoder implements MetadataDecoder {
    public static final int ID3_HEADER_LENGTH = 10;
    public static final int ID3_TAG = Util.getIntegerCodeForString("ID3");
    private static final int ID3_TEXT_ENCODING_ISO_8859_1 = 0;
    private static final int ID3_TEXT_ENCODING_UTF_16 = 1;
    private static final int ID3_TEXT_ENCODING_UTF_16BE = 2;
    private static final int ID3_TEXT_ENCODING_UTF_8 = 3;
    private static final String TAG = "Id3Decoder";
    private final FramePredicate framePredicate;

    public interface FramePredicate {
        boolean evaluate(int i, int i2, int i3, int i4, int i5);
    }

    private static final class Id3Header {
        private final int framesSize;
        private final boolean isUnsynchronized;
        private final int majorVersion;

        public Id3Header(int majorVersion, boolean isUnsynchronized, int framesSize) {
            this.majorVersion = majorVersion;
            this.isUnsynchronized = isUnsynchronized;
            this.framesSize = framesSize;
        }
    }

    private static org.telegram.messenger.exoplayer2.metadata.id3.Id3Frame decodeFrame(int r24, org.telegram.messenger.exoplayer2.util.ParsableByteArray r25, boolean r26, int r27, org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder.FramePredicate r28) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:203:? in {2, 7, 10, 18, 21, 22, 23, 35, 38, 41, 42, 46, 47, 48, 53, 56, 59, 62, 65, 66, 67, 68, 69, 70, 71, 73, 75, 77, 87, 90, 97, 98, 99, 109, 110, 115, 116, 117, 126, 136, 137, 145, 146, 154, 164, 165, 174, 183, 186, 187, 189, 194, 196, 197, 198, 199, 200, 201, 202, 204} preds:[]
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:129)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:48)
	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.rerun(BlockProcessor.java:44)
	at jadx.core.dex.visitors.blocksmaker.BlockFinallyExtract.visit(BlockFinallyExtract.java:57)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:59)
	at jadx.core.ProcessClass.process(ProcessClass.java:42)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
        /*
        r4 = r25.readUnsignedByte();
        r5 = r25.readUnsignedByte();
        r6 = r25.readUnsignedByte();
        r2 = 3;
        r0 = r24;
        if (r0 < r2) goto L_0x0059;
    L_0x0011:
        r7 = r25.readUnsignedByte();
    L_0x0015:
        r2 = 4;
        r0 = r24;
        if (r0 != r2) goto L_0x005b;
    L_0x001a:
        r9 = r25.readUnsignedIntToInt();
        if (r26 != 0) goto L_0x0038;
    L_0x0020:
        r2 = r9 & 255;
        r3 = r9 >> 8;
        r3 = r3 & 255;
        r3 = r3 << 7;
        r2 = r2 | r3;
        r3 = r9 >> 16;
        r3 = r3 & 255;
        r3 = r3 << 14;
        r2 = r2 | r3;
        r3 = r9 >> 24;
        r3 = r3 & 255;
        r3 = r3 << 21;
        r9 = r2 | r3;
    L_0x0038:
        r2 = 3;
        r0 = r24;
        if (r0 < r2) goto L_0x006a;
    L_0x003d:
        r15 = r25.readUnsignedShort();
    L_0x0041:
        if (r4 != 0) goto L_0x006c;
    L_0x0043:
        if (r5 != 0) goto L_0x006c;
    L_0x0045:
        if (r6 != 0) goto L_0x006c;
    L_0x0047:
        if (r7 != 0) goto L_0x006c;
    L_0x0049:
        if (r9 != 0) goto L_0x006c;
    L_0x004b:
        if (r15 != 0) goto L_0x006c;
    L_0x004d:
        r2 = r25.limit();
        r0 = r25;
        r0.setPosition(r2);
        r16 = 0;
    L_0x0058:
        return r16;
    L_0x0059:
        r7 = 0;
        goto L_0x0015;
    L_0x005b:
        r2 = 3;
        r0 = r24;
        if (r0 != r2) goto L_0x0065;
    L_0x0060:
        r9 = r25.readUnsignedIntToInt();
        goto L_0x0038;
    L_0x0065:
        r9 = r25.readUnsignedInt24();
        goto L_0x0038;
    L_0x006a:
        r15 = 0;
        goto L_0x0041;
    L_0x006c:
        r2 = r25.getPosition();
        r23 = r2 + r9;
        r2 = r25.limit();
        r0 = r23;
        if (r0 <= r2) goto L_0x008f;
    L_0x007a:
        r2 = "Id3Decoder";
        r3 = "Frame size exceeds remaining tag data";
        android.util.Log.w(r2, r3);
        r2 = r25.limit();
        r0 = r25;
        r0.setPosition(r2);
        r16 = 0;
        goto L_0x0058;
    L_0x008f:
        if (r28 == 0) goto L_0x00a5;
    L_0x0091:
        r2 = r28;
        r3 = r24;
        r2 = r2.evaluate(r3, r4, r5, r6, r7);
        if (r2 != 0) goto L_0x00a5;
    L_0x009b:
        r0 = r25;
        r1 = r23;
        r0.setPosition(r1);
        r16 = 0;
        goto L_0x0058;
    L_0x00a5:
        r20 = 0;
        r21 = 0;
        r22 = 0;
        r17 = 0;
        r18 = 0;
        r2 = 3;
        r0 = r24;
        if (r0 != r2) goto L_0x00e9;
    L_0x00b4:
        r2 = r15 & 128;
        if (r2 == 0) goto L_0x00e0;
    L_0x00b8:
        r20 = 1;
    L_0x00ba:
        r2 = r15 & 64;
        if (r2 == 0) goto L_0x00e3;
    L_0x00be:
        r21 = 1;
    L_0x00c0:
        r2 = r15 & 32;
        if (r2 == 0) goto L_0x00e6;
    L_0x00c4:
        r18 = 1;
    L_0x00c6:
        r17 = r20;
    L_0x00c8:
        if (r20 != 0) goto L_0x00cc;
    L_0x00ca:
        if (r21 == 0) goto L_0x011c;
    L_0x00cc:
        r2 = "Id3Decoder";
        r3 = "Skipping unsupported compressed or encrypted frame";
        android.util.Log.w(r2, r3);
        r0 = r25;
        r1 = r23;
        r0.setPosition(r1);
        r16 = 0;
        goto L_0x0058;
    L_0x00e0:
        r20 = 0;
        goto L_0x00ba;
    L_0x00e3:
        r21 = 0;
        goto L_0x00c0;
    L_0x00e6:
        r18 = 0;
        goto L_0x00c6;
    L_0x00e9:
        r2 = 4;
        r0 = r24;
        if (r0 != r2) goto L_0x00c8;
    L_0x00ee:
        r2 = r15 & 64;
        if (r2 == 0) goto L_0x010d;
    L_0x00f2:
        r18 = 1;
    L_0x00f4:
        r2 = r15 & 8;
        if (r2 == 0) goto L_0x0110;
    L_0x00f8:
        r20 = 1;
    L_0x00fa:
        r2 = r15 & 4;
        if (r2 == 0) goto L_0x0113;
    L_0x00fe:
        r21 = 1;
    L_0x0100:
        r2 = r15 & 2;
        if (r2 == 0) goto L_0x0116;
    L_0x0104:
        r22 = 1;
    L_0x0106:
        r2 = r15 & 1;
        if (r2 == 0) goto L_0x0119;
    L_0x010a:
        r17 = 1;
    L_0x010c:
        goto L_0x00c8;
    L_0x010d:
        r18 = 0;
        goto L_0x00f4;
    L_0x0110:
        r20 = 0;
        goto L_0x00fa;
    L_0x0113:
        r21 = 0;
        goto L_0x0100;
    L_0x0116:
        r22 = 0;
        goto L_0x0106;
    L_0x0119:
        r17 = 0;
        goto L_0x010c;
    L_0x011c:
        if (r18 == 0) goto L_0x0126;
    L_0x011e:
        r9 = r9 + -1;
        r2 = 1;
        r0 = r25;
        r0.skipBytes(r2);
    L_0x0126:
        if (r17 == 0) goto L_0x0130;
    L_0x0128:
        r9 = r9 + -4;
        r2 = 4;
        r0 = r25;
        r0.skipBytes(r2);
    L_0x0130:
        if (r22 == 0) goto L_0x0138;
    L_0x0132:
        r0 = r25;
        r9 = removeUnsynchronization(r0, r9);
    L_0x0138:
        r2 = 84;
        if (r4 != r2) goto L_0x015c;
    L_0x013c:
        r2 = 88;
        if (r5 != r2) goto L_0x015c;
    L_0x0140:
        r2 = 88;
        if (r6 != r2) goto L_0x015c;
    L_0x0144:
        r2 = 2;
        r0 = r24;
        if (r0 == r2) goto L_0x014d;
    L_0x0149:
        r2 = 88;
        if (r7 != r2) goto L_0x015c;
    L_0x014d:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodeTxxxFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0153:
        r0 = r25;
        r1 = r23;
        r0.setPosition(r1);
        goto L_0x0058;
    L_0x015c:
        r2 = 84;
        if (r4 != r2) goto L_0x01b8;
    L_0x0160:
        r2 = 2;
        r0 = r24;
        if (r0 != r2) goto L_0x018f;
    L_0x0165:
        r2 = java.util.Locale.US;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r3 = "%c%c%c";	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = 3;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = new java.lang.Object[r8];	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r4);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 1;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r5);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r6);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r19 = java.lang.String.format(r2, r3, r8);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0186:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r1 = r19;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodeTextInformationFrame(r0, r9, r1);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x018f:
        r2 = java.util.Locale.US;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r3 = "%c%c%c%c";	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = 4;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = new java.lang.Object[r8];	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r4);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 1;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r5);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r6);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 3;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r7);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r19 = java.lang.String.format(r2, r3, r8);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0186;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x01b8:
        r2 = 87;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r4 != r2) goto L_0x01d4;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x01bc:
        r2 = 88;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r5 != r2) goto L_0x01d4;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x01c0:
        r2 = 88;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r6 != r2) goto L_0x01d4;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x01c4:
        r2 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r0 == r2) goto L_0x01cd;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x01c9:
        r2 = 88;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r7 != r2) goto L_0x01d4;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x01cd:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodeWxxxFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x01d4:
        r2 = 87;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r4 != r2) goto L_0x0231;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x01d8:
        r2 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r0 != r2) goto L_0x0208;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x01dd:
        r2 = java.util.Locale.US;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r3 = "%c%c%c";	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = 3;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = new java.lang.Object[r8];	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r4);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 1;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r5);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r6);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r19 = java.lang.String.format(r2, r3, r8);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x01fe:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r1 = r19;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodeUrlLinkFrame(r0, r9, r1);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0208:
        r2 = java.util.Locale.US;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r3 = "%c%c%c%c";	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = 4;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = new java.lang.Object[r8];	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r4);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 1;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r5);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r6);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 3;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r7);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r19 = java.lang.String.format(r2, r3, r8);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x01fe;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0231:
        r2 = 80;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r4 != r2) goto L_0x0249;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0235:
        r2 = 82;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r5 != r2) goto L_0x0249;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0239:
        r2 = 73;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r6 != r2) goto L_0x0249;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x023d:
        r2 = 86;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r7 != r2) goto L_0x0249;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0241:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodePrivFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0249:
        r2 = 71;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r4 != r2) goto L_0x0266;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x024d:
        r2 = 69;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r5 != r2) goto L_0x0266;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0251:
        r2 = 79;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r6 != r2) goto L_0x0266;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0255:
        r2 = 66;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r7 == r2) goto L_0x025e;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0259:
        r2 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r0 != r2) goto L_0x0266;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x025e:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodeGeobFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0266:
        r2 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r0 != r2) goto L_0x0281;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x026b:
        r2 = 80;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r4 != r2) goto L_0x0291;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x026f:
        r2 = 73;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r5 != r2) goto L_0x0291;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0273:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r6 != r2) goto L_0x0291;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0277:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r1 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodeApicFrame(r0, r9, r1);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0281:
        r2 = 65;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r4 != r2) goto L_0x0291;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0285:
        r2 = 80;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r5 != r2) goto L_0x0291;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0289:
        r2 = 73;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r6 != r2) goto L_0x0291;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x028d:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r7 == r2) goto L_0x0277;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0291:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r4 != r2) goto L_0x02ae;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0295:
        r2 = 79;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r5 != r2) goto L_0x02ae;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0299:
        r2 = 77;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r6 != r2) goto L_0x02ae;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x029d:
        r2 = 77;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r7 == r2) goto L_0x02a6;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02a1:
        r2 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r0 != r2) goto L_0x02ae;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02a6:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodeCommentFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02ae:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r4 != r2) goto L_0x02ce;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02b2:
        r2 = 72;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r5 != r2) goto L_0x02ce;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02b6:
        r2 = 65;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r6 != r2) goto L_0x02ce;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02ba:
        r2 = 80;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r7 != r2) goto L_0x02ce;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02be:
        r8 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = r26;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r12 = r27;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r13 = r28;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodeChapterFrame(r8, r9, r10, r11, r12, r13);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02ce:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r4 != r2) goto L_0x02ee;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02d2:
        r2 = 84;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r5 != r2) goto L_0x02ee;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02d6:
        r2 = 79;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r6 != r2) goto L_0x02ee;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02da:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r7 != r2) goto L_0x02ee;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02de:
        r8 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = r26;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r12 = r27;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r13 = r28;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodeChapterTOCFrame(r8, r9, r10, r11, r12, r13);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02ee:
        r2 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        if (r0 != r2) goto L_0x031e;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x02f3:
        r2 = java.util.Locale.US;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r3 = "%c%c%c";	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = 3;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = new java.lang.Object[r8];	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r4);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 1;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r5);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r6);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r19 = java.lang.String.format(r2, r3, r8);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x0314:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r1 = r19;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = decodeBinaryFrame(r0, r9, r1);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
    L_0x031e:
        r2 = java.util.Locale.US;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r3 = "%c%c%c%c";	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = 4;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8 = new java.lang.Object[r8];	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 0;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r4);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 1;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r5);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r6);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r10 = 3;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r11 = java.lang.Integer.valueOf(r7);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r8[r10] = r11;	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r19 = java.lang.String.format(r2, r3, r8);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        goto L_0x0314;
    L_0x0347:
        r14 = move-exception;
        r2 = "Id3Decoder";	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r3 = "Unsupported character encoding";	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        android.util.Log.w(r2, r3);	 Catch:{ UnsupportedEncodingException -> 0x0347, all -> 0x035c }
        r16 = 0;
        r0 = r25;
        r1 = r23;
        r0.setPosition(r1);
        goto L_0x0058;
    L_0x035c:
        r2 = move-exception;
        r0 = r25;
        r1 = r23;
        r0.setPosition(r1);
        throw r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder.decodeFrame(int, org.telegram.messenger.exoplayer2.util.ParsableByteArray, boolean, int, org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder$FramePredicate):org.telegram.messenger.exoplayer2.metadata.id3.Id3Frame");
    }

    public Id3Decoder() {
        this(null);
    }

    public Id3Decoder(FramePredicate framePredicate) {
        this.framePredicate = framePredicate;
    }

    public Metadata decode(MetadataInputBuffer inputBuffer) {
        ByteBuffer buffer = inputBuffer.data;
        return decode(buffer.array(), buffer.limit());
    }

    public Metadata decode(byte[] data, int size) {
        List id3Frames = new ArrayList();
        ParsableByteArray id3Data = new ParsableByteArray(data, size);
        Id3Header id3Header = decodeHeader(id3Data);
        if (id3Header == null) {
            return null;
        }
        int startPosition = id3Data.getPosition();
        int framesSize = id3Header.framesSize;
        if (id3Header.isUnsynchronized) {
            framesSize = removeUnsynchronization(id3Data, id3Header.framesSize);
        }
        id3Data.setLimit(startPosition + framesSize);
        boolean unsignedIntFrameSizeHack = false;
        if (id3Header.majorVersion == 4 && !validateV4Frames(id3Data, false)) {
            if (validateV4Frames(id3Data, true)) {
                unsignedIntFrameSizeHack = true;
            } else {
                Log.w(TAG, "Failed to validate V4 ID3 tag");
                return null;
            }
        }
        int frameHeaderSize = id3Header.majorVersion == 2 ? 6 : 10;
        while (id3Data.bytesLeft() >= frameHeaderSize) {
            Id3Frame frame = decodeFrame(id3Header.majorVersion, id3Data, unsignedIntFrameSizeHack, frameHeaderSize, this.framePredicate);
            if (frame != null) {
                id3Frames.add(frame);
            }
        }
        return new Metadata(id3Frames);
    }

    private static Id3Header decodeHeader(ParsableByteArray data) {
        boolean isUnsynchronized = true;
        if (data.bytesLeft() < 10) {
            Log.w(TAG, "Data too short to be an ID3 tag");
            return null;
        }
        int id = data.readUnsignedInt24();
        if (id != ID3_TAG) {
            Log.w(TAG, "Unexpected first three bytes of ID3 tag header: " + id);
            return null;
        }
        int majorVersion = data.readUnsignedByte();
        data.skipBytes(1);
        int flags = data.readUnsignedByte();
        int framesSize = data.readSynchSafeInt();
        if (majorVersion == 2) {
            boolean isCompressed;
            if ((flags & 64) != 0) {
                isCompressed = true;
            } else {
                isCompressed = false;
            }
            if (isCompressed) {
                Log.w(TAG, "Skipped ID3 tag with majorVersion=2 and undefined compression scheme");
                return null;
            }
        } else if (majorVersion == 3) {
            if ((flags & 64) != 0) {
                hasExtendedHeader = true;
            } else {
                hasExtendedHeader = false;
            }
            if (hasExtendedHeader) {
                extendedHeaderSize = data.readInt();
                data.skipBytes(extendedHeaderSize);
                framesSize -= extendedHeaderSize + 4;
            }
        } else if (majorVersion == 4) {
            boolean hasFooter;
            if ((flags & 64) != 0) {
                hasExtendedHeader = true;
            } else {
                hasExtendedHeader = false;
            }
            if (hasExtendedHeader) {
                extendedHeaderSize = data.readSynchSafeInt();
                data.skipBytes(extendedHeaderSize - 4);
                framesSize -= extendedHeaderSize;
            }
            if ((flags & 16) != 0) {
                hasFooter = true;
            } else {
                hasFooter = false;
            }
            if (hasFooter) {
                framesSize -= 10;
            }
        } else {
            Log.w(TAG, "Skipped ID3 tag with unsupported majorVersion=" + majorVersion);
            return null;
        }
        if (majorVersion >= 4 || (flags & 128) == 0) {
            isUnsynchronized = false;
        }
        return new Id3Header(majorVersion, isUnsynchronized, framesSize);
    }

    private static boolean validateV4Frames(ParsableByteArray id3Data, boolean unsignedIntFrameSizeHack) {
        int startPosition = id3Data.getPosition();
        while (id3Data.bytesLeft() >= 10) {
            int id = id3Data.readInt();
            int frameSize = id3Data.readUnsignedIntToInt();
            int flags = id3Data.readUnsignedShort();
            if (id == 0 && frameSize == 0 && flags == 0) {
                id3Data.setPosition(startPosition);
                return true;
            }
            if (!unsignedIntFrameSizeHack) {
                if ((((long) frameSize) & 8421504) != 0) {
                    id3Data.setPosition(startPosition);
                    return false;
                }
                frameSize = (((frameSize & 255) | (((frameSize >> 8) & 255) << 7)) | (((frameSize >> 16) & 255) << 14)) | (((frameSize >> 24) & 255) << 21);
            }
            int minimumFrameSize = 0;
            if ((flags & 64) != 0) {
                minimumFrameSize = 0 + 1;
            }
            if ((flags & 1) != 0) {
                minimumFrameSize += 4;
            }
            if (frameSize < minimumFrameSize) {
                id3Data.setPosition(startPosition);
                return false;
            }
            try {
                if (id3Data.bytesLeft() < frameSize) {
                    return false;
                }
                id3Data.skipBytes(frameSize);
            } finally {
                id3Data.setPosition(startPosition);
            }
        }
        id3Data.setPosition(startPosition);
        return true;
    }

    private static TextInformationFrame decodeTxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        String value;
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int valueStartIndex = descriptionEndIndex + delimiterLength(encoding);
        if (valueStartIndex < data.length) {
            value = new String(data, valueStartIndex, indexOfEos(data, valueStartIndex, encoding) - valueStartIndex, charset);
        } else {
            value = "";
        }
        return new TextInformationFrame("TXXX", description, value);
    }

    private static TextInformationFrame decodeTextInformationFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        if (frameSize <= 1) {
            return new TextInformationFrame(id, null, "");
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        return new TextInformationFrame(id, null, new String(data, 0, indexOfEos(data, 0, encoding), charset));
    }

    private static UrlLinkFrame decodeWxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        String url;
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int urlStartIndex = descriptionEndIndex + delimiterLength(encoding);
        if (urlStartIndex < data.length) {
            url = new String(data, urlStartIndex, indexOfZeroByte(data, urlStartIndex) - urlStartIndex, "ISO-8859-1");
        } else {
            url = "";
        }
        return new UrlLinkFrame("WXXX", description, url);
    }

    private static UrlLinkFrame decodeUrlLinkFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        if (frameSize == 0) {
            return new UrlLinkFrame(id, null, "");
        }
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        return new UrlLinkFrame(id, null, new String(data, 0, indexOfZeroByte(data, 0), "ISO-8859-1"));
    }

    private static PrivFrame decodePrivFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize == 0) {
            return new PrivFrame("", new byte[0]);
        }
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        int ownerEndIndex = indexOfZeroByte(data, 0);
        return new PrivFrame(new String(data, 0, ownerEndIndex, "ISO-8859-1"), Arrays.copyOfRange(data, ownerEndIndex + 1, data.length));
    }

    private static GeobFrame decodeGeobFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int mimeTypeEndIndex = indexOfZeroByte(data, 0);
        String mimeType = new String(data, 0, mimeTypeEndIndex, "ISO-8859-1");
        int filenameStartIndex = mimeTypeEndIndex + 1;
        int filenameEndIndex = indexOfEos(data, filenameStartIndex, encoding);
        String filename = new String(data, filenameStartIndex, filenameEndIndex - filenameStartIndex, charset);
        int descriptionStartIndex = filenameEndIndex + delimiterLength(encoding);
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new GeobFrame(mimeType, filename, new String(data, descriptionStartIndex, descriptionEndIndex - descriptionStartIndex, charset), Arrays.copyOfRange(data, descriptionEndIndex + delimiterLength(encoding), data.length));
    }

    private static ApicFrame decodeApicFrame(ParsableByteArray id3Data, int frameSize, int majorVersion) throws UnsupportedEncodingException {
        int mimeTypeEndIndex;
        String mimeType;
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        if (majorVersion == 2) {
            mimeTypeEndIndex = 2;
            mimeType = "image/" + Util.toLowerInvariant(new String(data, 0, 3, "ISO-8859-1"));
            if (mimeType.equals("image/jpg")) {
                mimeType = "image/jpeg";
            }
        } else {
            mimeTypeEndIndex = indexOfZeroByte(data, 0);
            mimeType = Util.toLowerInvariant(new String(data, 0, mimeTypeEndIndex, "ISO-8859-1"));
            if (mimeType.indexOf(47) == -1) {
                mimeType = "image/" + mimeType;
            }
        }
        int pictureType = data[mimeTypeEndIndex + 1] & 255;
        int descriptionStartIndex = mimeTypeEndIndex + 2;
        int descriptionEndIndex = indexOfEos(data, descriptionStartIndex, encoding);
        return new ApicFrame(mimeType, new String(data, descriptionStartIndex, descriptionEndIndex - descriptionStartIndex, charset), pictureType, Arrays.copyOfRange(data, descriptionEndIndex + delimiterLength(encoding), data.length));
    }

    private static CommentFrame decodeCommentFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        String text;
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[3];
        id3Data.readBytes(data, 0, 3);
        String language = new String(data, 0, 3);
        data = new byte[(frameSize - 4)];
        id3Data.readBytes(data, 0, frameSize - 4);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        String description = new String(data, 0, descriptionEndIndex, charset);
        int textStartIndex = descriptionEndIndex + delimiterLength(encoding);
        if (textStartIndex < data.length) {
            text = new String(data, textStartIndex, indexOfEos(data, textStartIndex, encoding) - textStartIndex, charset);
        } else {
            text = "";
        }
        return new CommentFrame(language, description, text);
    }

    private static ChapterFrame decodeChapterFrame(ParsableByteArray id3Data, int frameSize, int majorVersion, boolean unsignedIntFrameSizeHack, int frameHeaderSize, FramePredicate framePredicate) throws UnsupportedEncodingException {
        int framePosition = id3Data.getPosition();
        int chapterIdEndIndex = indexOfZeroByte(id3Data.data, framePosition);
        String chapterId = new String(id3Data.data, framePosition, chapterIdEndIndex - framePosition, "ISO-8859-1");
        id3Data.setPosition(chapterIdEndIndex + 1);
        int startTime = id3Data.readInt();
        int endTime = id3Data.readInt();
        long startOffset = id3Data.readUnsignedInt();
        if (startOffset == 4294967295L) {
            startOffset = -1;
        }
        long endOffset = id3Data.readUnsignedInt();
        if (endOffset == 4294967295L) {
            endOffset = -1;
        }
        ArrayList<Id3Frame> subFrames = new ArrayList();
        int limit = framePosition + frameSize;
        while (id3Data.getPosition() < limit) {
            Id3Frame frame = decodeFrame(majorVersion, id3Data, unsignedIntFrameSizeHack, frameHeaderSize, framePredicate);
            if (frame != null) {
                subFrames.add(frame);
            }
        }
        Id3Frame[] subFrameArray = new Id3Frame[subFrames.size()];
        subFrames.toArray(subFrameArray);
        return new ChapterFrame(chapterId, startTime, endTime, startOffset, endOffset, subFrameArray);
    }

    private static ChapterTocFrame decodeChapterTOCFrame(ParsableByteArray id3Data, int frameSize, int majorVersion, boolean unsignedIntFrameSizeHack, int frameHeaderSize, FramePredicate framePredicate) throws UnsupportedEncodingException {
        int framePosition = id3Data.getPosition();
        int elementIdEndIndex = indexOfZeroByte(id3Data.data, framePosition);
        String elementId = new String(id3Data.data, framePosition, elementIdEndIndex - framePosition, "ISO-8859-1");
        id3Data.setPosition(elementIdEndIndex + 1);
        int ctocFlags = id3Data.readUnsignedByte();
        boolean isRoot = (ctocFlags & 2) != 0;
        boolean isOrdered = (ctocFlags & 1) != 0;
        int childCount = id3Data.readUnsignedByte();
        String[] children = new String[childCount];
        for (int i = 0; i < childCount; i++) {
            int startIndex = id3Data.getPosition();
            int endIndex = indexOfZeroByte(id3Data.data, startIndex);
            children[i] = new String(id3Data.data, startIndex, endIndex - startIndex, "ISO-8859-1");
            id3Data.setPosition(endIndex + 1);
        }
        ArrayList<Id3Frame> subFrames = new ArrayList();
        int limit = framePosition + frameSize;
        while (id3Data.getPosition() < limit) {
            Id3Frame frame = decodeFrame(majorVersion, id3Data, unsignedIntFrameSizeHack, frameHeaderSize, framePredicate);
            if (frame != null) {
                subFrames.add(frame);
            }
        }
        Id3Frame[] subFrameArray = new Id3Frame[subFrames.size()];
        subFrames.toArray(subFrameArray);
        return new ChapterTocFrame(elementId, isRoot, isOrdered, children, subFrameArray);
    }

    private static BinaryFrame decodeBinaryFrame(ParsableByteArray id3Data, int frameSize, String id) {
        byte[] frame = new byte[frameSize];
        id3Data.readBytes(frame, 0, frameSize);
        return new BinaryFrame(id, frame);
    }

    private static int removeUnsynchronization(ParsableByteArray data, int length) {
        byte[] bytes = data.data;
        int i = data.getPosition();
        while (i + 1 < length) {
            if ((bytes[i] & 255) == 255 && bytes[i + 1] == (byte) 0) {
                System.arraycopy(bytes, i + 2, bytes, i + 1, (length - i) - 2);
                length--;
            }
            i++;
        }
        return length;
    }

    private static String getCharsetName(int encodingByte) {
        switch (encodingByte) {
            case 0:
                return "ISO-8859-1";
            case 1:
                return "UTF-16";
            case 2:
                return "UTF-16BE";
            case 3:
                return "UTF-8";
            default:
                return "ISO-8859-1";
        }
    }

    private static int indexOfEos(byte[] data, int fromIndex, int encoding) {
        int terminationPos = indexOfZeroByte(data, fromIndex);
        if (encoding == 0 || encoding == 3) {
            return terminationPos;
        }
        while (terminationPos < data.length - 1) {
            if (terminationPos % 2 == 0 && data[terminationPos + 1] == (byte) 0) {
                return terminationPos;
            }
            terminationPos = indexOfZeroByte(data, terminationPos + 1);
        }
        return data.length;
    }

    private static int indexOfZeroByte(byte[] data, int fromIndex) {
        for (int i = fromIndex; i < data.length; i++) {
            if (data[i] == (byte) 0) {
                return i;
            }
        }
        return data.length;
    }

    private static int delimiterLength(int encodingByte) {
        return (encodingByte == 0 || encodingByte == 3) ? 1 : 2;
    }
}
