package org.telegram.messenger.exoplayer2.metadata.id3;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.telegram.messenger.exoplayer2.util.MimeTypes;
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

    private static org.telegram.messenger.exoplayer2.metadata.id3.Id3Frame decodeFrame(int r22, org.telegram.messenger.exoplayer2.util.ParsableByteArray r23, boolean r24) {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:162:? in {2, 7, 10, 18, 21, 22, 23, 31, 34, 37, 38, 42, 43, 44, 49, 52, 55, 58, 61, 62, 63, 64, 65, 66, 67, 69, 71, 73, 83, 86, 97, 107, 108, 116, 117, 125, 130, 131, 132, 142, 143, 146, 147, 149, 154, 156, 157, 158, 159, 160, 161, 163} preds:[]
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
        r5 = r23.readUnsignedByte();
        r6 = r23.readUnsignedByte();
        r7 = r23.readUnsignedByte();
        r17 = 3;
        r0 = r22;
        r1 = r17;
        if (r0 < r1) goto L_0x0073;
    L_0x0014:
        r8 = r23.readUnsignedByte();
    L_0x0018:
        r17 = 4;
        r0 = r22;
        r1 = r17;
        if (r0 != r1) goto L_0x0075;
    L_0x0020:
        r9 = r23.readUnsignedIntToInt();
        if (r24 != 0) goto L_0x004e;
    L_0x0026:
        r0 = r9 & 255;
        r17 = r0;
        r18 = r9 >> 8;
        r0 = r18;
        r0 = r0 & 255;
        r18 = r0;
        r18 = r18 << 7;
        r17 = r17 | r18;
        r18 = r9 >> 16;
        r0 = r18;
        r0 = r0 & 255;
        r18 = r0;
        r18 = r18 << 14;
        r17 = r17 | r18;
        r18 = r9 >> 24;
        r0 = r18;
        r0 = r0 & 255;
        r18 = r0;
        r18 = r18 << 21;
        r9 = r17 | r18;
    L_0x004e:
        r17 = 3;
        r0 = r22;
        r1 = r17;
        if (r0 < r1) goto L_0x0087;
    L_0x0056:
        r3 = r23.readUnsignedShort();
    L_0x005a:
        if (r5 != 0) goto L_0x0089;
    L_0x005c:
        if (r6 != 0) goto L_0x0089;
    L_0x005e:
        if (r7 != 0) goto L_0x0089;
    L_0x0060:
        if (r8 != 0) goto L_0x0089;
    L_0x0062:
        if (r9 != 0) goto L_0x0089;
    L_0x0064:
        if (r3 != 0) goto L_0x0089;
    L_0x0066:
        r17 = r23.limit();
        r0 = r23;
        r1 = r17;
        r0.setPosition(r1);
        r4 = 0;
    L_0x0072:
        return r4;
    L_0x0073:
        r8 = 0;
        goto L_0x0018;
    L_0x0075:
        r17 = 3;
        r0 = r22;
        r1 = r17;
        if (r0 != r1) goto L_0x0082;
    L_0x007d:
        r9 = r23.readUnsignedIntToInt();
        goto L_0x004e;
    L_0x0082:
        r9 = r23.readUnsignedInt24();
        goto L_0x004e;
    L_0x0087:
        r3 = 0;
        goto L_0x005a;
    L_0x0089:
        r17 = r23.getPosition();
        r16 = r17 + r9;
        r17 = r23.limit();
        r0 = r16;
        r1 = r17;
        if (r0 <= r1) goto L_0x00af;
    L_0x0099:
        r17 = "Id3Decoder";
        r18 = "Frame size exceeds remaining tag data";
        android.util.Log.w(r17, r18);
        r17 = r23.limit();
        r0 = r23;
        r1 = r17;
        r0.setPosition(r1);
        r4 = 0;
        goto L_0x0072;
    L_0x00af:
        r13 = 0;
        r14 = 0;
        r15 = 0;
        r10 = 0;
        r11 = 0;
        r17 = 3;
        r0 = r22;
        r1 = r17;
        if (r0 != r1) goto L_0x00ea;
    L_0x00bc:
        r0 = r3 & 128;
        r17 = r0;
        if (r17 == 0) goto L_0x00e4;
    L_0x00c2:
        r13 = 1;
    L_0x00c3:
        r17 = r3 & 64;
        if (r17 == 0) goto L_0x00e6;
    L_0x00c7:
        r14 = 1;
    L_0x00c8:
        r17 = r3 & 32;
        if (r17 == 0) goto L_0x00e8;
    L_0x00cc:
        r11 = 1;
    L_0x00cd:
        r10 = r13;
    L_0x00ce:
        if (r13 != 0) goto L_0x00d2;
    L_0x00d0:
        if (r14 == 0) goto L_0x0116;
    L_0x00d2:
        r17 = "Id3Decoder";
        r18 = "Skipping unsupported compressed or encrypted frame";
        android.util.Log.w(r17, r18);
        r0 = r23;
        r1 = r16;
        r0.setPosition(r1);
        r4 = 0;
        goto L_0x0072;
    L_0x00e4:
        r13 = 0;
        goto L_0x00c3;
    L_0x00e6:
        r14 = 0;
        goto L_0x00c8;
    L_0x00e8:
        r11 = 0;
        goto L_0x00cd;
    L_0x00ea:
        r17 = 4;
        r0 = r22;
        r1 = r17;
        if (r0 != r1) goto L_0x00ce;
    L_0x00f2:
        r17 = r3 & 64;
        if (r17 == 0) goto L_0x010c;
    L_0x00f6:
        r11 = 1;
    L_0x00f7:
        r17 = r3 & 8;
        if (r17 == 0) goto L_0x010e;
    L_0x00fb:
        r13 = 1;
    L_0x00fc:
        r17 = r3 & 4;
        if (r17 == 0) goto L_0x0110;
    L_0x0100:
        r14 = 1;
    L_0x0101:
        r17 = r3 & 2;
        if (r17 == 0) goto L_0x0112;
    L_0x0105:
        r15 = 1;
    L_0x0106:
        r17 = r3 & 1;
        if (r17 == 0) goto L_0x0114;
    L_0x010a:
        r10 = 1;
    L_0x010b:
        goto L_0x00ce;
    L_0x010c:
        r11 = 0;
        goto L_0x00f7;
    L_0x010e:
        r13 = 0;
        goto L_0x00fc;
    L_0x0110:
        r14 = 0;
        goto L_0x0101;
    L_0x0112:
        r15 = 0;
        goto L_0x0106;
    L_0x0114:
        r10 = 0;
        goto L_0x010b;
    L_0x0116:
        if (r11 == 0) goto L_0x0123;
    L_0x0118:
        r9 = r9 + -1;
        r17 = 1;
        r0 = r23;
        r1 = r17;
        r0.skipBytes(r1);
    L_0x0123:
        if (r10 == 0) goto L_0x0130;
    L_0x0125:
        r9 = r9 + -4;
        r17 = 4;
        r0 = r23;
        r1 = r17;
        r0.skipBytes(r1);
    L_0x0130:
        if (r15 == 0) goto L_0x0138;
    L_0x0132:
        r0 = r23;
        r9 = removeUnsynchronization(r0, r9);
    L_0x0138:
        r17 = 84;
        r0 = r17;
        if (r5 != r0) goto L_0x0167;
    L_0x013e:
        r17 = 88;
        r0 = r17;
        if (r6 != r0) goto L_0x0167;
    L_0x0144:
        r17 = 88;
        r0 = r17;
        if (r7 != r0) goto L_0x0167;
    L_0x014a:
        r17 = 2;
        r0 = r22;
        r1 = r17;
        if (r0 == r1) goto L_0x0158;
    L_0x0152:
        r17 = 88;
        r0 = r17;
        if (r8 != r0) goto L_0x0167;
    L_0x0158:
        r0 = r23;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r4 = decodeTxxxFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x015e:
        r0 = r23;
        r1 = r16;
        r0.setPosition(r1);
        goto L_0x0072;
    L_0x0167:
        r17 = 80;
        r0 = r17;
        if (r5 != r0) goto L_0x0186;
    L_0x016d:
        r17 = 82;
        r0 = r17;
        if (r6 != r0) goto L_0x0186;
    L_0x0173:
        r17 = 73;
        r0 = r17;
        if (r7 != r0) goto L_0x0186;
    L_0x0179:
        r17 = 86;
        r0 = r17;
        if (r8 != r0) goto L_0x0186;
    L_0x017f:
        r0 = r23;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r4 = decodePrivFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        goto L_0x015e;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x0186:
        r17 = 71;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r5 != r0) goto L_0x01ad;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x018c:
        r17 = 69;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r6 != r0) goto L_0x01ad;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x0192:
        r17 = 79;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r7 != r0) goto L_0x01ad;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x0198:
        r17 = 66;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r8 == r0) goto L_0x01a6;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x019e:
        r17 = 2;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r22;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r1 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r0 != r1) goto L_0x01ad;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01a6:
        r0 = r23;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r4 = decodeGeobFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        goto L_0x015e;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01ad:
        r17 = 2;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r22;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r1 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r0 != r1) goto L_0x01d0;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01b5:
        r17 = 80;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r5 != r0) goto L_0x01e8;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01bb:
        r17 = 73;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r6 != r0) goto L_0x01e8;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01c1:
        r17 = 67;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r7 != r0) goto L_0x01e8;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01c7:
        r0 = r23;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r1 = r22;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r4 = decodeApicFrame(r0, r9, r1);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        goto L_0x015e;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01d0:
        r17 = 65;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r5 != r0) goto L_0x01e8;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01d6:
        r17 = 80;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r6 != r0) goto L_0x01e8;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01dc:
        r17 = 73;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r7 != r0) goto L_0x01e8;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01e2:
        r17 = 67;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r8 == r0) goto L_0x01c7;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01e8:
        r17 = 84;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r5 != r0) goto L_0x0259;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01ee:
        r17 = 2;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r22;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r1 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r0 != r1) goto L_0x0227;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x01f6:
        r17 = java.util.Locale.US;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r18 = "%c%c%c";	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19 = 3;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r19;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = new java.lang.Object[r0];	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19 = r0;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 0;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r5);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 1;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r6);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 2;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r7);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r12 = java.lang.String.format(r17, r18, r19);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x021f:
        r0 = r23;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r4 = decodeTextInformationFrame(r0, r9, r12);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        goto L_0x015e;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x0227:
        r17 = java.util.Locale.US;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r18 = "%c%c%c%c";	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19 = 4;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r19;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = new java.lang.Object[r0];	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19 = r0;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 0;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r5);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 1;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r6);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 2;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r7);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 3;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r8);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r12 = java.lang.String.format(r17, r18, r19);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        goto L_0x021f;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x0259:
        r17 = 67;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r5 != r0) goto L_0x0281;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x025f:
        r17 = 79;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r6 != r0) goto L_0x0281;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x0265:
        r17 = 77;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r7 != r0) goto L_0x0281;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x026b:
        r17 = 77;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r8 == r0) goto L_0x0279;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x0271:
        r17 = 2;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r22;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r1 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r0 != r1) goto L_0x0281;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x0279:
        r0 = r23;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r4 = decodeCommentFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        goto L_0x015e;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x0281:
        r17 = 2;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r22;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r1 = r17;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        if (r0 != r1) goto L_0x02ba;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x0289:
        r17 = java.util.Locale.US;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r18 = "%c%c%c";	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19 = 3;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r19;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = new java.lang.Object[r0];	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19 = r0;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 0;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r5);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 1;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r6);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 2;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r7);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r12 = java.lang.String.format(r17, r18, r19);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x02b2:
        r0 = r23;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r4 = decodeBinaryFrame(r0, r9, r12);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        goto L_0x015e;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
    L_0x02ba:
        r17 = java.util.Locale.US;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r18 = "%c%c%c%c";	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19 = 4;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = r19;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r0 = new java.lang.Object[r0];	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19 = r0;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 0;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r5);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 1;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r6);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 2;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r7);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r20 = 3;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r21 = java.lang.Integer.valueOf(r8);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r19[r20] = r21;	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r12 = java.lang.String.format(r17, r18, r19);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        goto L_0x02b2;
    L_0x02ec:
        r2 = move-exception;
        r17 = "Id3Decoder";	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r18 = "Unsupported character encoding";	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        android.util.Log.w(r17, r18);	 Catch:{ UnsupportedEncodingException -> 0x02ec, all -> 0x0300 }
        r4 = 0;
        r0 = r23;
        r1 = r16;
        r0.setPosition(r1);
        goto L_0x0072;
    L_0x0300:
        r17 = move-exception;
        r0 = r23;
        r1 = r16;
        r0.setPosition(r1);
        throw r17;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.exoplayer2.metadata.id3.Id3Decoder.decodeFrame(int, org.telegram.messenger.exoplayer2.util.ParsableByteArray, boolean):org.telegram.messenger.exoplayer2.metadata.id3.Id3Frame");
    }

    public boolean canDecode(String mimeType) {
        return mimeType.equals(MimeTypes.APPLICATION_ID3);
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
            Id3Frame frame = decodeFrame(id3Header.majorVersion, id3Data, unsignedIntFrameSizeHack);
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

    private static TxxxFrame decodeTxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        int valueStartIndex = descriptionEndIndex + delimiterLength(encoding);
        return new TxxxFrame(new String(data, 0, descriptionEndIndex, charset), new String(data, valueStartIndex, indexOfEos(data, valueStartIndex, encoding) - valueStartIndex, charset));
    }

    private static PrivFrame decodePrivFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
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
            mimeType = "image/" + new String(data, 0, 3, "ISO-8859-1").toLowerCase();
            if (mimeType.equals("image/jpg")) {
                mimeType = "image/jpeg";
            }
        } else {
            mimeTypeEndIndex = indexOfZeroByte(data, 0);
            mimeType = new String(data, 0, mimeTypeEndIndex, "ISO-8859-1").toLowerCase();
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
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[3];
        id3Data.readBytes(data, 0, 3);
        String language = new String(data, 0, 3);
        data = new byte[(frameSize - 4)];
        id3Data.readBytes(data, 0, frameSize - 4);
        int descriptionEndIndex = indexOfEos(data, 0, encoding);
        int textStartIndex = descriptionEndIndex + delimiterLength(encoding);
        return new CommentFrame(language, new String(data, 0, descriptionEndIndex, charset), new String(data, textStartIndex, indexOfEos(data, textStartIndex, encoding) - textStartIndex, charset));
    }

    private static TextInformationFrame decodeTextInformationFrame(ParsableByteArray id3Data, int frameSize, String id) throws UnsupportedEncodingException {
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        return new TextInformationFrame(id, new String(data, 0, indexOfEos(data, 0, encoding), charset));
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
