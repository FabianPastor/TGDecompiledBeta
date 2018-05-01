package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class ID3v2FrameBody {
    static final ThreadLocal<Buffer> textBuffer = new C05211();
    private final ID3v2DataInput data = new ID3v2DataInput(this.input);
    private final ID3v2FrameHeader frameHeader;
    private final RangeInputStream input;
    private final ID3v2TagHeader tagHeader;

    /* renamed from: org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody$1 */
    static class C05211 extends ThreadLocal<Buffer> {
        C05211() {
        }

        protected Buffer initialValue() {
            return new Buffer(4096);
        }
    }

    static final class Buffer {
        byte[] bytes;

        Buffer(int i) {
            this.bytes = new byte[i];
        }

        byte[] bytes(int i) {
            if (i > this.bytes.length) {
                int length = this.bytes.length * 2;
                while (i > length) {
                    length *= 2;
                }
                this.bytes = new byte[length];
            }
            return this.bytes;
        }
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

    private java.lang.String extractString(byte[] r6, int r7, int r8, org.telegram.messenger.audioinfo.mp3.ID3v2Encoding r9, boolean r10) {
        /* JADX: method processing error */
/*
Error: java.lang.NullPointerException
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.searchTryCatchDominators(ProcessTryCatchRegions.java:75)
	at jadx.core.dex.visitors.regions.ProcessTryCatchRegions.process(ProcessTryCatchRegions.java:45)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.postProcessRegions(RegionMakerVisitor.java:63)
	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:58)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r5 = this;
        r0 = 1;
        r1 = 0;
        if (r10 == 0) goto L_0x002c;
    L_0x0004:
        r10 = r1;
        r2 = r10;
    L_0x0006:
        if (r10 >= r8) goto L_0x002c;
    L_0x0008:
        r3 = r7 + r10;
        r4 = r6[r3];
        if (r4 != 0) goto L_0x0028;
    L_0x000e:
        r4 = org.telegram.messenger.audioinfo.mp3.ID3v2Encoding.UTF_16;
        if (r9 != r4) goto L_0x0018;
    L_0x0012:
        if (r2 != 0) goto L_0x0018;
    L_0x0014:
        r3 = r3 % 2;
        if (r3 != 0) goto L_0x0028;
    L_0x0018:
        r2 = r2 + 1;
        r3 = r9.getZeroBytes();
        if (r2 != r3) goto L_0x0029;
    L_0x0020:
        r10 = r10 + r0;
        r8 = r9.getZeroBytes();
        r8 = r10 - r8;
        goto L_0x002c;
    L_0x0028:
        r2 = r1;
    L_0x0029:
        r10 = r10 + 1;
        goto L_0x0006;
    L_0x002c:
        r10 = new java.lang.String;	 Catch:{ Exception -> 0x004d }
        r9 = r9.getCharset();	 Catch:{ Exception -> 0x004d }
        r9 = r9.name();	 Catch:{ Exception -> 0x004d }
        r10.<init>(r6, r7, r8, r9);	 Catch:{ Exception -> 0x004d }
        r6 = r10.length();	 Catch:{ Exception -> 0x004d }
        if (r6 <= 0) goto L_0x004c;	 Catch:{ Exception -> 0x004d }
    L_0x003f:
        r6 = r10.charAt(r1);	 Catch:{ Exception -> 0x004d }
        r7 = 65279; // 0xfeff float:9.1475E-41 double:3.2252E-319;	 Catch:{ Exception -> 0x004d }
        if (r6 != r7) goto L_0x004c;	 Catch:{ Exception -> 0x004d }
    L_0x0048:
        r10 = r10.substring(r0);	 Catch:{ Exception -> 0x004d }
    L_0x004c:
        return r10;
    L_0x004d:
        r6 = "";
        return r6;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody.extractString(byte[], int, int, org.telegram.messenger.audioinfo.mp3.ID3v2Encoding, boolean):java.lang.String");
    }

    public String readZeroTerminatedString(int i, ID3v2Encoding iD3v2Encoding) throws IOException, ID3v2Exception {
        i = Math.min(i, (int) getRemainingLength());
        byte[] bytes = ((Buffer) textBuffer.get()).bytes(i);
        int i2 = 0;
        int i3 = i2;
        while (i2 < i) {
            byte readByte = this.data.readByte();
            bytes[i2] = readByte;
            if (readByte != (byte) 0 || (iD3v2Encoding == ID3v2Encoding.UTF_16 && i3 == 0 && i2 % 2 != 0)) {
                i3 = 0;
            } else {
                i3++;
                if (i3 == iD3v2Encoding.getZeroBytes()) {
                    return extractString(bytes, 0, (i2 + 1) - iD3v2Encoding.getZeroBytes(), iD3v2Encoding, false);
                }
            }
            i2++;
        }
        throw new ID3v2Exception("Could not read zero-termiated string");
    }

    public String readFixedLengthString(int i, ID3v2Encoding iD3v2Encoding) throws IOException, ID3v2Exception {
        if (((long) i) > getRemainingLength()) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Could not read fixed-length string of length: ");
            stringBuilder.append(i);
            throw new ID3v2Exception(stringBuilder.toString());
        }
        byte[] bytes = ((Buffer) textBuffer.get()).bytes(i);
        this.data.readFully(bytes, 0, i);
        return extractString(bytes, 0, i, iD3v2Encoding, true);
    }

    public ID3v2Encoding readEncoding() throws IOException, ID3v2Exception {
        byte readByte = this.data.readByte();
        switch (readByte) {
            case (byte) 0:
                return ID3v2Encoding.ISO_8859_1;
            case (byte) 1:
                return ID3v2Encoding.UTF_16;
            case (byte) 2:
                return ID3v2Encoding.UTF_16BE;
            case (byte) 3:
                return ID3v2Encoding.UTF_8;
            default:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Invalid encoding: ");
                stringBuilder.append(readByte);
                throw new ID3v2Exception(stringBuilder.toString());
        }
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
