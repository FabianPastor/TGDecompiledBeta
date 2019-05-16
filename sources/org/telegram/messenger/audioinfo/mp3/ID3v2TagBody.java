package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class ID3v2TagBody {
    private final ID3v2DataInput data = new ID3v2DataInput(this.input);
    private final RangeInputStream input;
    private final ID3v2TagHeader tagHeader;

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:23:0x0068 in {6, 7, 9, 10, 11, 12, 17, 18, 20, 22} preds:[]
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
    public org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody frameBody(org.telegram.messenger.audioinfo.mp3.ID3v2FrameHeader r11) throws java.io.IOException, org.telegram.messenger.audioinfo.mp3.ID3v2Exception {
        /*
        r10 = this;
        r0 = r11.getBodySize();
        r1 = r10.input;
        r2 = r11.isUnsynchronization();
        if (r2 == 0) goto L_0x0037;
        r0 = r10.data;
        r1 = r11.getBodySize();
        r0 = r0.readFully(r1);
        r1 = -1;
        r2 = r0.length;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        if (r4 >= r2) goto L_0x0031;
        r7 = r0[r4];
        if (r6 == 0) goto L_0x0024;
        if (r7 == 0) goto L_0x0029;
        r6 = r5 + 1;
        r0[r5] = r7;
        r5 = r6;
        if (r7 != r1) goto L_0x002d;
        r6 = 1;
        goto L_0x002e;
        r6 = 0;
        r4 = r4 + 1;
        goto L_0x001c;
        r1 = new java.io.ByteArrayInputStream;
        r1.<init>(r0, r3, r5);
        r0 = r5;
        r2 = r11.isEncryption();
        if (r2 != 0) goto L_0x0060;
        r2 = r11.isCompression();
        if (r2 == 0) goto L_0x004f;
        r0 = r11.getDataLengthIndicator();
        r2 = new java.util.zip.InflaterInputStream;
        r2.<init>(r1);
        r7 = r0;
        r4 = r2;
        goto L_0x0051;
        r7 = r0;
        r4 = r1;
        r0 = new org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody;
        r1 = r11.getHeaderSize();
        r5 = (long) r1;
        r8 = r10.tagHeader;
        r3 = r0;
        r9 = r11;
        r3.<init>(r4, r5, r7, r8, r9);
        return r0;
        r11 = new org.telegram.messenger.audioinfo.mp3.ID3v2Exception;
        r0 = "Frame encryption is not supported";
        r11.<init>(r0);
        throw r11;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.ID3v2TagBody.frameBody(org.telegram.messenger.audioinfo.mp3.ID3v2FrameHeader):org.telegram.messenger.audioinfo.mp3.ID3v2FrameBody");
    }

    ID3v2TagBody(InputStream inputStream, long j, int i, ID3v2TagHeader iD3v2TagHeader) throws IOException {
        this.input = new RangeInputStream(inputStream, j, (long) i);
        this.tagHeader = iD3v2TagHeader;
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

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("id3v2tag[pos=");
        stringBuilder.append(getPosition());
        stringBuilder.append(", ");
        stringBuilder.append(getRemainingLength());
        stringBuilder.append(" left]");
        return stringBuilder.toString();
    }
}
