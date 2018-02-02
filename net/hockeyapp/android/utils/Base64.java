package net.hockeyapp.android.utils;

import java.io.UnsupportedEncodingException;
import org.telegram.messenger.exoplayer2.C;

public class Base64 {

    static abstract class Coder {
        public int op;
        public byte[] output;

        Coder() {
        }
    }

    static class Encoder extends Coder {
        private static final byte[] ENCODE = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 43, (byte) 47};
        private static final byte[] ENCODE_WEBSAFE = new byte[]{(byte) 65, (byte) 66, (byte) 67, (byte) 68, (byte) 69, (byte) 70, (byte) 71, (byte) 72, (byte) 73, (byte) 74, (byte) 75, (byte) 76, (byte) 77, (byte) 78, (byte) 79, (byte) 80, (byte) 81, (byte) 82, (byte) 83, (byte) 84, (byte) 85, (byte) 86, (byte) 87, (byte) 88, (byte) 89, (byte) 90, (byte) 97, (byte) 98, (byte) 99, (byte) 100, (byte) 101, (byte) 102, (byte) 103, (byte) 104, (byte) 105, (byte) 106, (byte) 107, (byte) 108, (byte) 109, (byte) 110, (byte) 111, (byte) 112, (byte) 113, (byte) 114, (byte) 115, (byte) 116, (byte) 117, (byte) 118, (byte) 119, (byte) 120, (byte) 121, (byte) 122, (byte) 48, (byte) 49, (byte) 50, (byte) 51, (byte) 52, (byte) 53, (byte) 54, (byte) 55, (byte) 56, (byte) 57, (byte) 45, (byte) 95};
        private final byte[] alphabet;
        private int count;
        public final boolean do_cr;
        public final boolean do_newline;
        public final boolean do_padding;
        private final byte[] tail;
        int tailLen;

        public Encoder(int flags, byte[] output) {
            boolean z;
            boolean z2 = true;
            this.output = output;
            this.do_padding = (flags & 1) == 0;
            if ((flags & 2) == 0) {
                z = true;
            } else {
                z = false;
            }
            this.do_newline = z;
            if ((flags & 4) == 0) {
                z2 = false;
            }
            this.do_cr = z2;
            this.alphabet = (flags & 8) == 0 ? ENCODE : ENCODE_WEBSAFE;
            this.tail = new byte[2];
            this.tailLen = 0;
            this.count = this.do_newline ? 19 : -1;
        }

        public boolean process(byte[] r15, int r16, int r17, boolean r18) {
            /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxOverflowException: Regions stack size limit reached
	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:37)
	at jadx.core.utils.ErrorsCounter.methodError(ErrorsCounter.java:61)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:33)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
	at jadx.core.ProcessClass.process(ProcessClass.java:37)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:306)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:199)
*/
            /*
            r14 = this;
            r1 = r14.alphabet;
            r5 = r14.output;
            r3 = 0;
            r2 = r14.count;
            r6 = r16;
            r17 = r17 + r16;
            r10 = -1;
            r11 = r14.tailLen;
            switch(r11) {
                case 0: goto L_0x0011;
                case 1: goto L_0x00b0;
                case 2: goto L_0x00d5;
                default: goto L_0x0011;
            };
        L_0x0011:
            r11 = -1;
            if (r10 == r11) goto L_0x0249;
        L_0x0014:
            r4 = r3 + 1;
            r11 = r10 >> 18;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r3] = r11;
            r3 = r4 + 1;
            r11 = r10 >> 12;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r4 = r3 + 1;
            r11 = r10 >> 6;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r3] = r11;
            r3 = r4 + 1;
            r11 = r10 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r2 = r2 + -1;
            if (r2 != 0) goto L_0x0249;
        L_0x003e:
            r11 = r14.do_cr;
            if (r11 == 0) goto L_0x0049;
        L_0x0042:
            r4 = r3 + 1;
            r11 = 13;
            r5[r3] = r11;
            r3 = r4;
        L_0x0049:
            r4 = r3 + 1;
            r11 = 10;
            r5[r3] = r11;
            r2 = 19;
            r7 = r6;
        L_0x0052:
            r11 = r7 + 3;
            r0 = r17;
            if (r11 > r0) goto L_0x00fc;
        L_0x0058:
            r11 = r15[r7];
            r11 = r11 & 255;
            r11 = r11 << 16;
            r12 = r7 + 1;
            r12 = r15[r12];
            r12 = r12 & 255;
            r12 = r12 << 8;
            r11 = r11 | r12;
            r12 = r7 + 2;
            r12 = r15[r12];
            r12 = r12 & 255;
            r10 = r11 | r12;
            r11 = r10 >> 18;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r11 = r4 + 1;
            r12 = r10 >> 12;
            r12 = r12 & 63;
            r12 = r1[r12];
            r5[r11] = r12;
            r11 = r4 + 2;
            r12 = r10 >> 6;
            r12 = r12 & 63;
            r12 = r1[r12];
            r5[r11] = r12;
            r11 = r4 + 3;
            r12 = r10 & 63;
            r12 = r1[r12];
            r5[r11] = r12;
            r6 = r7 + 3;
            r3 = r4 + 4;
            r2 = r2 + -1;
            if (r2 != 0) goto L_0x0249;
        L_0x009b:
            r11 = r14.do_cr;
            if (r11 == 0) goto L_0x00a6;
        L_0x009f:
            r4 = r3 + 1;
            r11 = 13;
            r5[r3] = r11;
            r3 = r4;
        L_0x00a6:
            r4 = r3 + 1;
            r11 = 10;
            r5[r3] = r11;
            r2 = 19;
            r7 = r6;
            goto L_0x0052;
        L_0x00b0:
            r11 = r6 + 2;
            r0 = r17;
            if (r11 > r0) goto L_0x0011;
        L_0x00b6:
            r11 = r14.tail;
            r12 = 0;
            r11 = r11[r12];
            r11 = r11 & 255;
            r11 = r11 << 16;
            r7 = r6 + 1;
            r12 = r15[r6];
            r12 = r12 & 255;
            r12 = r12 << 8;
            r11 = r11 | r12;
            r6 = r7 + 1;
            r12 = r15[r7];
            r12 = r12 & 255;
            r10 = r11 | r12;
            r11 = 0;
            r14.tailLen = r11;
            goto L_0x0011;
        L_0x00d5:
            r11 = r6 + 1;
            r0 = r17;
            if (r11 > r0) goto L_0x0011;
        L_0x00db:
            r11 = r14.tail;
            r12 = 0;
            r11 = r11[r12];
            r11 = r11 & 255;
            r11 = r11 << 16;
            r12 = r14.tail;
            r13 = 1;
            r12 = r12[r13];
            r12 = r12 & 255;
            r12 = r12 << 8;
            r11 = r11 | r12;
            r7 = r6 + 1;
            r12 = r15[r6];
            r12 = r12 & 255;
            r10 = r11 | r12;
            r11 = 0;
            r14.tailLen = r11;
            r6 = r7;
            goto L_0x0011;
        L_0x00fc:
            if (r18 == 0) goto L_0x0211;
        L_0x00fe:
            r11 = r14.tailLen;
            r11 = r7 - r11;
            r12 = r17 + -1;
            if (r11 != r12) goto L_0x017a;
        L_0x0106:
            r8 = 0;
            r11 = r14.tailLen;
            if (r11 <= 0) goto L_0x0175;
        L_0x010b:
            r11 = r14.tail;
            r9 = r8 + 1;
            r11 = r11[r8];
            r8 = r9;
            r6 = r7;
        L_0x0113:
            r11 = r11 & 255;
            r10 = r11 << 4;
            r11 = r14.tailLen;
            r11 = r11 - r8;
            r14.tailLen = r11;
            r3 = r4 + 1;
            r11 = r10 >> 6;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r4 = r3 + 1;
            r11 = r10 & 63;
            r11 = r1[r11];
            r5[r3] = r11;
            r11 = r14.do_padding;
            if (r11 == 0) goto L_0x013e;
        L_0x0132:
            r3 = r4 + 1;
            r11 = 61;
            r5[r4] = r11;
            r4 = r3 + 1;
            r11 = 61;
            r5[r3] = r11;
        L_0x013e:
            r3 = r4;
            r11 = r14.do_newline;
            if (r11 == 0) goto L_0x0155;
        L_0x0143:
            r11 = r14.do_cr;
            if (r11 == 0) goto L_0x014e;
        L_0x0147:
            r4 = r3 + 1;
            r11 = 13;
            r5[r3] = r11;
            r3 = r4;
        L_0x014e:
            r4 = r3 + 1;
            r11 = 10;
            r5[r3] = r11;
        L_0x0154:
            r3 = r4;
        L_0x0155:
            r11 = r14.tailLen;
            if (r11 == 0) goto L_0x0162;
        L_0x0159:
            r11 = "BASE64";
            r12 = "Error during encoding";
            net.hockeyapp.android.utils.HockeyLog.error(r11, r12);
        L_0x0162:
            r0 = r17;
            if (r6 == r0) goto L_0x016f;
        L_0x0166:
            r11 = "BASE64";
            r12 = "Error during encoding";
            net.hockeyapp.android.utils.HockeyLog.error(r11, r12);
        L_0x016f:
            r14.op = r3;
            r14.count = r2;
            r11 = 1;
            return r11;
        L_0x0175:
            r6 = r7 + 1;
            r11 = r15[r7];
            goto L_0x0113;
        L_0x017a:
            r11 = r14.tailLen;
            r11 = r7 - r11;
            r12 = r17 + -2;
            if (r11 != r12) goto L_0x01f3;
        L_0x0182:
            r8 = 0;
            r11 = r14.tailLen;
            r12 = 1;
            if (r11 <= r12) goto L_0x01e8;
        L_0x0188:
            r11 = r14.tail;
            r9 = r8 + 1;
            r11 = r11[r8];
            r8 = r9;
            r6 = r7;
        L_0x0190:
            r11 = r11 & 255;
            r12 = r11 << 10;
            r11 = r14.tailLen;
            if (r11 <= 0) goto L_0x01ed;
        L_0x0198:
            r11 = r14.tail;
            r9 = r8 + 1;
            r11 = r11[r8];
            r8 = r9;
        L_0x019f:
            r11 = r11 & 255;
            r11 = r11 << 2;
            r10 = r12 | r11;
            r11 = r14.tailLen;
            r11 = r11 - r8;
            r14.tailLen = r11;
            r3 = r4 + 1;
            r11 = r10 >> 12;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r4 = r3 + 1;
            r11 = r10 >> 6;
            r11 = r11 & 63;
            r11 = r1[r11];
            r5[r3] = r11;
            r3 = r4 + 1;
            r11 = r10 & 63;
            r11 = r1[r11];
            r5[r4] = r11;
            r11 = r14.do_padding;
            if (r11 == 0) goto L_0x01d1;
        L_0x01ca:
            r4 = r3 + 1;
            r11 = 61;
            r5[r3] = r11;
            r3 = r4;
        L_0x01d1:
            r11 = r14.do_newline;
            if (r11 == 0) goto L_0x0155;
        L_0x01d5:
            r11 = r14.do_cr;
            if (r11 == 0) goto L_0x01e0;
        L_0x01d9:
            r4 = r3 + 1;
            r11 = 13;
            r5[r3] = r11;
            r3 = r4;
        L_0x01e0:
            r4 = r3 + 1;
            r11 = 10;
            r5[r3] = r11;
            goto L_0x0154;
        L_0x01e8:
            r6 = r7 + 1;
            r11 = r15[r7];
            goto L_0x0190;
        L_0x01ed:
            r7 = r6 + 1;
            r11 = r15[r6];
            r6 = r7;
            goto L_0x019f;
        L_0x01f3:
            r11 = r14.do_newline;
            if (r11 == 0) goto L_0x020d;
        L_0x01f7:
            if (r4 <= 0) goto L_0x020d;
        L_0x01f9:
            r11 = 19;
            if (r2 == r11) goto L_0x020d;
        L_0x01fd:
            r11 = r14.do_cr;
            if (r11 == 0) goto L_0x0247;
        L_0x0201:
            r3 = r4 + 1;
            r11 = 13;
            r5[r4] = r11;
        L_0x0207:
            r4 = r3 + 1;
            r11 = 10;
            r5[r3] = r11;
        L_0x020d:
            r6 = r7;
            r3 = r4;
            goto L_0x0155;
        L_0x0211:
            r11 = r17 + -1;
            if (r7 != r11) goto L_0x0225;
        L_0x0215:
            r11 = r14.tail;
            r12 = r14.tailLen;
            r13 = r12 + 1;
            r14.tailLen = r13;
            r13 = r15[r7];
            r11[r12] = r13;
            r6 = r7;
            r3 = r4;
            goto L_0x016f;
        L_0x0225:
            r11 = r17 + -2;
            if (r7 != r11) goto L_0x0243;
        L_0x0229:
            r11 = r14.tail;
            r12 = r14.tailLen;
            r13 = r12 + 1;
            r14.tailLen = r13;
            r13 = r15[r7];
            r11[r12] = r13;
            r11 = r14.tail;
            r12 = r14.tailLen;
            r13 = r12 + 1;
            r14.tailLen = r13;
            r13 = r7 + 1;
            r13 = r15[r13];
            r11[r12] = r13;
        L_0x0243:
            r6 = r7;
            r3 = r4;
            goto L_0x016f;
        L_0x0247:
            r3 = r4;
            goto L_0x0207;
        L_0x0249:
            r7 = r6;
            r4 = r3;
            goto L_0x0052;
            */
            throw new UnsupportedOperationException("Method not decompiled: net.hockeyapp.android.utils.Base64.Encoder.process(byte[], int, int, boolean):boolean");
        }
    }

    public static String encodeToString(byte[] input, int flags) {
        try {
            return new String(encode(input, flags), C.ASCII_NAME);
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError(e);
        }
    }

    public static byte[] encode(byte[] input, int flags) {
        return encode(input, 0, input.length, flags);
    }

    public static byte[] encode(byte[] input, int offset, int len, int flags) {
        Encoder encoder = new Encoder(flags, null);
        int output_len = (len / 3) * 4;
        if (!encoder.do_padding) {
            switch (len % 3) {
                case 0:
                    break;
                case 1:
                    output_len += 2;
                    break;
                case 2:
                    output_len += 3;
                    break;
                default:
                    break;
            }
        } else if (len % 3 > 0) {
            output_len += 4;
        }
        if (encoder.do_newline && len > 0) {
            int i;
            int i2 = ((len - 1) / 57) + 1;
            if (encoder.do_cr) {
                i = 2;
            } else {
                i = 1;
            }
            output_len += i * i2;
        }
        encoder.output = new byte[output_len];
        encoder.process(input, offset, len, true);
        if (encoder.op == output_len) {
            return encoder.output;
        }
        throw new AssertionError();
    }
}
