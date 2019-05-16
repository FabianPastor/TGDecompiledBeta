package org.telegram.messenger.audioinfo.mp3;

import java.io.IOException;
import java.io.InputStream;
import org.telegram.messenger.audioinfo.util.PositionInputStream;

public class ID3v2TagHeader {
    private boolean compression;
    private int footerSize;
    private int headerSize;
    private int paddingSize;
    private int revision;
    private int totalTagSize;
    private boolean unsynchronization;
    private int version;

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:22:0x0061 in {10, 11, 13, 14, 15, 17, 19, 21} preds:[]
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.computeDominators(BlockProcessor.java:242)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.processBlocksTree(BlockProcessor.java:52)
        	at jadx.core.dex.visitors.blocksmaker.BlockProcessor.visit(BlockProcessor.java:42)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:27)
        	at jadx.core.dex.visitors.DepthTraversal.lambda$visit$1(DepthTraversal.java:14)
        	at java.util.ArrayList.forEach(ArrayList.java:1257)
        	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:14)
        	at jadx.core.ProcessClass.process(ProcessClass.java:32)
        	at jadx.core.ProcessClass.lambda$processDependencies$0(ProcessClass.java:51)
        	at java.lang.Iterable.forEach(Iterable.java:75)
        	at jadx.core.ProcessClass.processDependencies(ProcessClass.java:51)
        	at jadx.core.ProcessClass.process(ProcessClass.java:37)
        	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:292)
        	at jadx.api.JavaClass.decompile(JavaClass.java:62)
        	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
        */
    public org.telegram.messenger.audioinfo.mp3.ID3v2TagBody tagBody(java.io.InputStream r9) throws java.io.IOException, org.telegram.messenger.audioinfo.mp3.ID3v2Exception {
        /*
        r8 = this;
        r0 = r8.compression;
        if (r0 != 0) goto L_0x0059;
        r0 = r8.version;
        r1 = 4;
        if (r0 >= r1) goto L_0x0047;
        r0 = r8.unsynchronization;
        if (r0 == 0) goto L_0x0047;
        r0 = new org.telegram.messenger.audioinfo.mp3.ID3v2DataInput;
        r0.<init>(r9);
        r1 = r8.totalTagSize;
        r2 = r8.headerSize;
        r1 = r1 - r2;
        r0 = r0.readFully(r1);
        r1 = -1;
        r2 = r0.length;
        r3 = 0;
        r4 = 0;
        r5 = 0;
        r6 = 0;
        if (r4 >= r2) goto L_0x0036;
        r7 = r0[r4];
        if (r6 == 0) goto L_0x0029;
        if (r7 == 0) goto L_0x002e;
        r6 = r5 + 1;
        r0[r5] = r7;
        r5 = r6;
        if (r7 != r1) goto L_0x0032;
        r6 = 1;
        goto L_0x0033;
        r6 = 0;
        r4 = r4 + 1;
        goto L_0x0021;
        r6 = new org.telegram.messenger.audioinfo.mp3.ID3v2TagBody;
        r1 = new java.io.ByteArrayInputStream;
        r1.<init>(r0, r3, r5);
        r0 = r8.headerSize;
        r2 = (long) r0;
        r0 = r6;
        r4 = r5;
        r5 = r8;
        r0.<init>(r1, r2, r4, r5);
        return r6;
        r6 = new org.telegram.messenger.audioinfo.mp3.ID3v2TagBody;
        r0 = r8.headerSize;
        r2 = (long) r0;
        r4 = r8.totalTagSize;
        r4 = r4 - r0;
        r0 = r8.footerSize;
        r4 = r4 - r0;
        r0 = r6;
        r1 = r9;
        r5 = r8;
        r0.<init>(r1, r2, r4, r5);
        return r6;
        r0 = new org.telegram.messenger.audioinfo.mp3.ID3v2Exception;
        r1 = "Tag compression is not supported";
        r0.<init>(r1);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.mp3.ID3v2TagHeader.tagBody(java.io.InputStream):org.telegram.messenger.audioinfo.mp3.ID3v2TagBody");
    }

    public ID3v2TagHeader(InputStream inputStream) throws IOException, ID3v2Exception {
        this(new PositionInputStream(inputStream));
    }

    ID3v2TagHeader(PositionInputStream positionInputStream) throws IOException, ID3v2Exception {
        boolean z = false;
        this.version = 0;
        this.revision = 0;
        this.headerSize = 0;
        this.totalTagSize = 0;
        this.paddingSize = 0;
        this.footerSize = 0;
        long position = positionInputStream.getPosition();
        ID3v2DataInput iD3v2DataInput = new ID3v2DataInput(positionInputStream);
        String str = new String(iD3v2DataInput.readFully(3), "ISO-8859-1");
        StringBuilder stringBuilder;
        if ("ID3".equals(str)) {
            this.version = iD3v2DataInput.readByte();
            int i = this.version;
            if (i == 2 || i == 3 || i == 4) {
                this.revision = iD3v2DataInput.readByte();
                byte readByte = iD3v2DataInput.readByte();
                this.totalTagSize = iD3v2DataInput.readSyncsafeInt() + 10;
                if (this.version == 2) {
                    this.unsynchronization = (readByte & 128) != 0;
                    if ((readByte & 64) != 0) {
                        z = true;
                    }
                    this.compression = z;
                } else {
                    if ((readByte & 128) != 0) {
                        z = true;
                    }
                    this.unsynchronization = z;
                    if ((readByte & 64) != 0) {
                        if (this.version == 3) {
                            int readInt = iD3v2DataInput.readInt();
                            iD3v2DataInput.readByte();
                            iD3v2DataInput.readByte();
                            this.paddingSize = iD3v2DataInput.readInt();
                            iD3v2DataInput.skipFully((long) (readInt - 6));
                        } else {
                            iD3v2DataInput.skipFully((long) (iD3v2DataInput.readSyncsafeInt() - 4));
                        }
                    }
                    if (this.version >= 4 && (readByte & 16) != 0) {
                        this.footerSize = 10;
                        this.totalTagSize += 10;
                    }
                }
                this.headerSize = (int) (positionInputStream.getPosition() - position);
                return;
            }
            stringBuilder = new StringBuilder();
            stringBuilder.append("Unsupported ID3v2 version: ");
            stringBuilder.append(this.version);
            throw new ID3v2Exception(stringBuilder.toString());
        }
        stringBuilder = new StringBuilder();
        stringBuilder.append("Invalid ID3 identifier: ");
        stringBuilder.append(str);
        throw new ID3v2Exception(stringBuilder.toString());
    }

    public int getVersion() {
        return this.version;
    }

    public int getRevision() {
        return this.revision;
    }

    public int getTotalTagSize() {
        return this.totalTagSize;
    }

    public boolean isUnsynchronization() {
        return this.unsynchronization;
    }

    public boolean isCompression() {
        return this.compression;
    }

    public int getHeaderSize() {
        return this.headerSize;
    }

    public int getFooterSize() {
        return this.footerSize;
    }

    public int getPaddingSize() {
        return this.paddingSize;
    }

    public String toString() {
        return String.format("%s[version=%s, totalTagSize=%d]", new Object[]{ID3v2TagHeader.class.getSimpleName(), Integer.valueOf(this.version), Integer.valueOf(this.totalTagSize)});
    }
}
