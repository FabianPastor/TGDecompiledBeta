package org.telegram.messenger.exoplayer2.metadata.id3;

import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.exoplayer2.C;
import org.telegram.messenger.exoplayer2.metadata.Metadata;
import org.telegram.messenger.exoplayer2.metadata.MetadataDecoder;
import org.telegram.messenger.exoplayer2.metadata.MetadataInputBuffer;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;
import org.telegram.messenger.exoplayer2.util.Util;

public final class Id3Decoder implements MetadataDecoder {
    private static final int FRAME_FLAG_V3_HAS_GROUP_IDENTIFIER = 32;
    private static final int FRAME_FLAG_V3_IS_COMPRESSED = 128;
    private static final int FRAME_FLAG_V3_IS_ENCRYPTED = 64;
    private static final int FRAME_FLAG_V4_HAS_DATA_LENGTH = 1;
    private static final int FRAME_FLAG_V4_HAS_GROUP_IDENTIFIER = 64;
    private static final int FRAME_FLAG_V4_IS_COMPRESSED = 8;
    private static final int FRAME_FLAG_V4_IS_ENCRYPTED = 4;
    private static final int FRAME_FLAG_V4_IS_UNSYNCHRONIZED = 2;
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
Error: jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:192:? in {2, 7, 10, 18, 21, 22, 23, 35, 38, 41, 42, 46, 47, 48, 53, 56, 59, 62, 65, 66, 67, 68, 69, 70, 71, 73, 75, 77, 87, 89, 91, 96, 106, 107, 110, 119, 129, 130, 138, 139, 147, 157, 158, 167, 176, 178, 183, 185, 186, 187, 188, 189, 190, 191, 193} preds:[]
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
        if (r4 != r2) goto L_0x0189;
    L_0x013c:
        r2 = 88;
        if (r5 != r2) goto L_0x0189;
    L_0x0140:
        r2 = 88;
        if (r6 != r2) goto L_0x0189;
    L_0x0144:
        r2 = 2;
        r0 = r24;
        if (r0 == r2) goto L_0x014d;
    L_0x0149:
        r2 = 88;
        if (r7 != r2) goto L_0x0189;
    L_0x014d:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodeTxxxFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0153:
        if (r16 != 0) goto L_0x0180;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0155:
        r2 = "Id3Decoder";	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r3 = new java.lang.StringBuilder;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r3.<init>();	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r8 = "Failed to decode frame: id=";	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r3 = r3.append(r8);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r8 = getFrameId(r0, r4, r5, r6, r7);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r3 = r3.append(r8);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r8 = ", frameSize=";	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r3 = r3.append(r8);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r3 = r3.append(r9);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r3 = r3.toString();	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        android.util.Log.w(r2, r3);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0180:
        r0 = r25;
        r1 = r23;
        r0.setPosition(r1);
        goto L_0x0058;
    L_0x0189:
        r2 = 84;
        if (r4 != r2) goto L_0x019c;
    L_0x018d:
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r19 = getFrameId(r0, r4, r5, r6, r7);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r1 = r19;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodeTextInformationFrame(r0, r9, r1);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x019c:
        r2 = 87;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r4 != r2) goto L_0x01b8;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01a0:
        r2 = 88;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r5 != r2) goto L_0x01b8;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01a4:
        r2 = 88;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r6 != r2) goto L_0x01b8;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01a8:
        r2 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r0 == r2) goto L_0x01b1;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01ad:
        r2 = 88;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r7 != r2) goto L_0x01b8;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01b1:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodeWxxxFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01b8:
        r2 = 87;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r4 != r2) goto L_0x01cb;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01bc:
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r19 = getFrameId(r0, r4, r5, r6, r7);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r1 = r19;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodeUrlLinkFrame(r0, r9, r1);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01cb:
        r2 = 80;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r4 != r2) goto L_0x01e3;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01cf:
        r2 = 82;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r5 != r2) goto L_0x01e3;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01d3:
        r2 = 73;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r6 != r2) goto L_0x01e3;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01d7:
        r2 = 86;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r7 != r2) goto L_0x01e3;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01db:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodePrivFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01e3:
        r2 = 71;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r4 != r2) goto L_0x0200;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01e7:
        r2 = 69;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r5 != r2) goto L_0x0200;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01eb:
        r2 = 79;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r6 != r2) goto L_0x0200;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01ef:
        r2 = 66;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r7 == r2) goto L_0x01f8;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01f3:
        r2 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r0 != r2) goto L_0x0200;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x01f8:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodeGeobFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0200:
        r2 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r0 != r2) goto L_0x021b;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0205:
        r2 = 80;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r4 != r2) goto L_0x022b;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0209:
        r2 = 73;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r5 != r2) goto L_0x022b;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x020d:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r6 != r2) goto L_0x022b;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0211:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r1 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodeApicFrame(r0, r9, r1);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x021b:
        r2 = 65;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r4 != r2) goto L_0x022b;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x021f:
        r2 = 80;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r5 != r2) goto L_0x022b;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0223:
        r2 = 73;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r6 != r2) goto L_0x022b;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0227:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r7 == r2) goto L_0x0211;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x022b:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r4 != r2) goto L_0x0248;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x022f:
        r2 = 79;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r5 != r2) goto L_0x0248;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0233:
        r2 = 77;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r6 != r2) goto L_0x0248;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0237:
        r2 = 77;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r7 == r2) goto L_0x0240;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x023b:
        r2 = 2;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r0 != r2) goto L_0x0248;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0240:
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodeCommentFrame(r0, r9);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0248:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r4 != r2) goto L_0x0268;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x024c:
        r2 = 72;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r5 != r2) goto L_0x0268;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0250:
        r2 = 65;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r6 != r2) goto L_0x0268;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0254:
        r2 = 80;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r7 != r2) goto L_0x0268;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0258:
        r8 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r10 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r11 = r26;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r12 = r27;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r13 = r28;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodeChapterFrame(r8, r9, r10, r11, r12, r13);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0268:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r4 != r2) goto L_0x0288;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x026c:
        r2 = 84;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r5 != r2) goto L_0x0288;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0270:
        r2 = 79;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r6 != r2) goto L_0x0288;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0274:
        r2 = 67;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        if (r7 != r2) goto L_0x0288;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0278:
        r8 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r10 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r11 = r26;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r12 = r27;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r13 = r28;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodeChapterTOCFrame(r8, r9, r10, r11, r12, r13);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        goto L_0x0153;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
    L_0x0288:
        r0 = r24;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r19 = getFrameId(r0, r4, r5, r6, r7);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r0 = r25;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r1 = r19;	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = decodeBinaryFrame(r0, r9, r1);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        goto L_0x0153;
    L_0x0298:
        r14 = move-exception;
        r2 = "Id3Decoder";	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r3 = "Unsupported character encoding";	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        android.util.Log.w(r2, r3);	 Catch:{ UnsupportedEncodingException -> 0x0298, all -> 0x02ad }
        r16 = 0;
        r0 = r25;
        r1 = r23;
        r0.setPosition(r1);
        goto L_0x0058;
    L_0x02ad:
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
        int frameHeaderSize = id3Header.majorVersion == 2 ? 6 : 10;
        int framesSize = id3Header.framesSize;
        if (id3Header.isUnsynchronized) {
            framesSize = removeUnsynchronization(id3Data, id3Header.framesSize);
        }
        id3Data.setLimit(startPosition + framesSize);
        boolean unsignedIntFrameSizeHack = false;
        if (!validateFrames(id3Data, id3Header.majorVersion, frameHeaderSize, false)) {
            if (id3Header.majorVersion == 4 && validateFrames(id3Data, 4, frameHeaderSize, true)) {
                unsignedIntFrameSizeHack = true;
            } else {
                Log.w(TAG, "Failed to validate ID3 tag with majorVersion=" + id3Header.majorVersion);
                return null;
            }
        }
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

    private static boolean validateFrames(ParsableByteArray id3Data, int majorVersion, int frameHeaderSize, boolean unsignedIntFrameSizeHack) {
        int startPosition = id3Data.getPosition();
        while (id3Data.bytesLeft() >= frameHeaderSize) {
            int id;
            long frameSize;
            int flags;
            if (majorVersion >= 3) {
                id = id3Data.readInt();
                frameSize = id3Data.readUnsignedInt();
                flags = id3Data.readUnsignedShort();
            } else {
                id = id3Data.readUnsignedInt24();
                frameSize = (long) id3Data.readUnsignedInt24();
                flags = 0;
            }
            if (id == 0 && frameSize == 0 && flags == 0) {
                id3Data.setPosition(startPosition);
                return true;
            }
            if (majorVersion == 4 && !unsignedIntFrameSizeHack) {
                if ((8421504 & frameSize) != 0) {
                    id3Data.setPosition(startPosition);
                    return false;
                }
                frameSize = (((255 & frameSize) | (((frameSize >> 8) & 255) << 7)) | (((frameSize >> 16) & 255) << 14)) | (((frameSize >> 24) & 255) << 21);
            }
            boolean hasGroupIdentifier = false;
            boolean hasDataLength = false;
            if (majorVersion == 4) {
                hasGroupIdentifier = (flags & 64) != 0;
                hasDataLength = (flags & 1) != 0;
            } else if (majorVersion == 3) {
                hasGroupIdentifier = (flags & 32) != 0;
                hasDataLength = (flags & 128) != 0;
            }
            int minimumFrameSize = 0;
            if (hasGroupIdentifier) {
                minimumFrameSize = 0 + 1;
            }
            if (hasDataLength) {
                minimumFrameSize += 4;
            }
            if (frameSize < ((long) minimumFrameSize)) {
                id3Data.setPosition(startPosition);
                return false;
            }
            try {
                if (((long) id3Data.bytesLeft()) < frameSize) {
                    return false;
                }
                id3Data.skipBytes((int) frameSize);
            } finally {
                id3Data.setPosition(startPosition);
            }
        }
        id3Data.setPosition(startPosition);
        return true;
    }

    private static TextInformationFrame decodeTxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
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
        if (frameSize < 1) {
            return null;
        }
        int encoding = id3Data.readUnsignedByte();
        String charset = getCharsetName(encoding);
        byte[] data = new byte[(frameSize - 1)];
        id3Data.readBytes(data, 0, frameSize - 1);
        return new TextInformationFrame(id, null, new String(data, 0, indexOfEos(data, 0, encoding), charset));
    }

    private static UrlLinkFrame decodeWxxxFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        if (frameSize < 1) {
            return null;
        }
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
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        return new UrlLinkFrame(id, null, new String(data, 0, indexOfZeroByte(data, 0), "ISO-8859-1"));
    }

    private static PrivFrame decodePrivFrame(ParsableByteArray id3Data, int frameSize) throws UnsupportedEncodingException {
        byte[] privateData;
        byte[] data = new byte[frameSize];
        id3Data.readBytes(data, 0, frameSize);
        int ownerEndIndex = indexOfZeroByte(data, 0);
        String owner = new String(data, 0, ownerEndIndex, "ISO-8859-1");
        int privateDataStartIndex = ownerEndIndex + 1;
        if (privateDataStartIndex < data.length) {
            privateData = Arrays.copyOfRange(data, privateDataStartIndex, data.length);
        } else {
            privateData = new byte[0];
        }
        return new PrivFrame(owner, privateData);
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
        if (frameSize < 4) {
            return null;
        }
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
                return C.UTF16_NAME;
            case 2:
                return "UTF-16BE";
            case 3:
                return "UTF-8";
            default:
                return "ISO-8859-1";
        }
    }

    private static String getFrameId(int majorVersion, int frameId0, int frameId1, int frameId2, int frameId3) {
        if (majorVersion == 2) {
            return String.format(Locale.US, "%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2)});
        }
        return String.format(Locale.US, "%c%c%c%c", new Object[]{Integer.valueOf(frameId0), Integer.valueOf(frameId1), Integer.valueOf(frameId2), Integer.valueOf(frameId3)});
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
