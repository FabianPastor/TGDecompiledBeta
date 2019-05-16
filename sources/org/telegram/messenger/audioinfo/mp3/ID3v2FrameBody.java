package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class ID3v2FrameBody {
    static final ThreadLocal<Buffer> textBuffer = new ThreadLocal<Buffer>() {
        /* Access modifiers changed, original: protected */
        public Buffer initialValue() {
            return new Buffer(4096);
        }
    };
    private final ID3v2DataInput data = new ID3v2DataInput(this.input);
    private final ID3v2FrameHeader frameHeader;
    private final RangeInputStream input;
    private final ID3v2TagHeader tagHeader;

    static final class Buffer {
        byte[] bytes;

        Buffer(int i) {
            this.bytes = new byte[i];
        }

        /* Access modifiers changed, original: 0000 */
        public byte[] bytes(int i) {
            byte[] bArr = this.bytes;
            if (i > bArr.length) {
                int length = bArr.length;
                while (true) {
                    length *= 2;
                    if (i <= length) {
                        break;
                    }
                }
                this.bytes = new byte[length];
            }
            return this.bytes;
        }
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:17:0x0053 in {8, 12, 13, 14, 16} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public java.lang.String readZeroTerminatedString(int r8, org.telegram.messenger.audioinfo.mp3.ID3v2Encoding r9) throws java.io.IOException, org.telegram.messenger.audioinfo.mp3.ID3v2Exception {
        /*
        r7 = this;
        r0 = r7.getRemainingLength();
        r1 = (int) r0;
        r8 = java.lang.Math.min(r8, r1);
        r0 = textBuffer;
        r0 = r0.get();
        r0 = (org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody.Buffer) r0;
        r2 = r0.bytes(r8);
        r0 = 0;
        r1 = 0;
        r3 = 0;
        if (r1 >= r8) goto L_0x004b;
        r4 = r7.data;
        r4 = r4.readByte();
        r2[r1] = r4;
        if (r4 != 0) goto L_0x0047;
        r4 = org.telegram.messenger.audioinfo.mp3.ID3v2Encoding.UTF_16;
        if (r9 != r4) goto L_0x002e;
        if (r3 != 0) goto L_0x002e;
        r4 = r1 % 2;
        if (r4 != 0) goto L_0x0047;
        r3 = r3 + 1;
        r4 = r9.getZeroBytes();
        if (r3 != r4) goto L_0x0048;
        r3 = 0;
        r1 = r1 + 1;
        r8 = r9.getZeroBytes();
        r4 = r1 - r8;
        r6 = 0;
        r1 = r7;
        r5 = r9;
        r8 = r1.extractString(r2, r3, r4, r5, r6);
        return r8;
        r3 = 0;
        r1 = r1 + 1;
        goto L_0x0018;
        r8 = new org.telegram.messenger.audioinfo.mp3.ID3v2Exception;
        r9 = "Could not read zero-termiated string";
        r8.<init>(r9);
        throw r8;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody.readZeroTerminatedString(int, org.telegram.messenger.audioinfo.mp3.ID3v2Encoding):java.lang.String");
    }

    ID3v2FrameBody(InputStream inputStream, long j, int i, ID3v2TagHeader iD3v2TagHeader, ID3v2FrameHeader iD3v2FrameHeader) throws IOException {
        this.input = new RangeInputStream(inputStream, j, (long) i);
        this.tagHeader = iD3v2TagHeader;
        this.frameHeader = iD3v2FrameHeader;
    }

    public ID3v2DataInput getData() {
        return this.data;
    }

    public long getPosition() {
        return this.input.getPosition();
    }

    public long getRemainingLength() {
        return this.input.getRemainingLength();
    }

    public ID3v2TagHeader getTagHeader() {
        return this.tagHeader;
    }

    public ID3v2FrameHeader getFrameHeader() {
        return this.frameHeader;
    }

    private String extractString(byte[] bArr, int i, int i2, ID3v2Encoding iD3v2Encoding, boolean z) {
        if (z) {
            int i3 = 0;
            for (int i4 = 0; i4 < i2; i4++) {
                int i5 = i + i4;
                if (bArr[i5] != (byte) 0 || (iD3v2Encoding == ID3v2Encoding.UTF_16 && i3 == 0 && i5 % 2 != 0)) {
                    i3 = 0;
                } else {
                    i3++;
                    if (i3 == iD3v2Encoding.getZeroBytes()) {
                        i2 = (i4 + 1) - iD3v2Encoding.getZeroBytes();
                        break;
                    }
                }
            }
        }
        try {
            String str = new String(bArr, i, i2, iD3v2Encoding.getCharset().name());
            if (str.length() > 0 && str.charAt(0) == 65279) {
                str = str.substring(1);
            }
            return str;
        } catch (Exception unused) {
            return "";
        }
    }

    public String readFixedLengthString(int i, ID3v2Encoding iD3v2Encoding) throws IOException, ID3v2Exception {
        if (((long) i) <= getRemainingLength()) {
            byte[] bytes = ((Buffer) textBuffer.get()).bytes(i);
            this.data.readFully(bytes, 0, i);
            return extractString(bytes, 0, i, iD3v2Encoding, true);
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Could not read fixed-length string of length: ");
        stringBuilder.append(i);
        throw new ID3v2Exception(stringBuilder.toString());
    }

    public ID3v2Encoding readEncoding() throws IOException, ID3v2Exception {
        byte readByte = this.data.readByte();
        if (readByte == (byte) 0) {
            return ID3v2Encoding.ISO_8859_1;
        }
        if (readByte == (byte) 1) {
            return ID3v2Encoding.UTF_16;
        }
        if (readByte == (byte) 2) {
            return ID3v2Encoding.UTF_16BE;
        }
        if (readByte == (byte) 3) {
            return ID3v2Encoding.UTF_8;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid encoding: ");
        stringBuilder.append(readByte);
        throw new ID3v2Exception(stringBuilder.toString());
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id3v2frame[pos=");
        stringBuilder.append(getPosition());
        stringBuilder.append(", ");
        stringBuilder.append(getRemainingLength());
        stringBuilder.append(" left]");
        return stringBuilder.toString();
    }
}
