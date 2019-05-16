package org.telegram.messenger.audioinfo.m4a;

import java.io.EOFException;
import java.io.IOException;
import java.math.BigDecimal;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class MP4Atom extends MP4Box<RangeInputStream> {
    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find immediate dominator for block B:7:0x0030 in {4, 6} preds:[]
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
    public org.telegram.messenger.audioinfo.m4a.MP4Atom nextChildUpTo(java.lang.String r6) throws java.io.IOException {
        /*
        r5 = this;
        r0 = r5.getRemaining();
        r2 = 0;
        r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1));
        if (r4 <= 0) goto L_0x0019;
        r0 = r5.nextChild();
        r1 = r0.getType();
        r1 = r1.matches(r6);
        if (r1 == 0) goto L_0x0000;
        return r0;
        r0 = new java.io.IOException;
        r1 = new java.lang.StringBuilder;
        r1.<init>();
        r2 = "atom type mismatch, not found: ";
        r1.append(r2);
        r1.append(r6);
        r6 = r1.toString();
        r0.<init>(r6);
        throw r0;
        return;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.m4a.MP4Atom.nextChildUpTo(java.lang.String):org.telegram.messenger.audioinfo.m4a.MP4Atom");
    }

    public MP4Atom(RangeInputStream rangeInputStream, MP4Box<?> mP4Box, String str) {
        super(rangeInputStream, mP4Box, str);
    }

    public long getLength() {
        return ((RangeInputStream) getInput()).getPosition() + ((RangeInputStream) getInput()).getRemainingLength();
    }

    public long getOffset() {
        return getParent().getPosition() - getPosition();
    }

    public long getRemaining() {
        return ((RangeInputStream) getInput()).getRemainingLength();
    }

    public boolean hasMoreChildren() {
        return (getChild() != null ? getChild().getRemaining() : 0) < getRemaining();
    }

    public boolean readBoolean() throws IOException {
        return this.data.readBoolean();
    }

    public byte readByte() throws IOException {
        return this.data.readByte();
    }

    public short readShort() throws IOException {
        return this.data.readShort();
    }

    public int readInt() throws IOException {
        return this.data.readInt();
    }

    public long readLong() throws IOException {
        return this.data.readLong();
    }

    public byte[] readBytes(int i) throws IOException {
        byte[] bArr = new byte[i];
        this.data.readFully(bArr);
        return bArr;
    }

    public byte[] readBytes() throws IOException {
        return readBytes((int) getRemaining());
    }

    public BigDecimal readShortFixedPoint() throws IOException {
        byte readByte = this.data.readByte();
        int readUnsignedByte = this.data.readUnsignedByte();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(readByte));
        stringBuilder.append("");
        stringBuilder.append(String.valueOf(readUnsignedByte));
        return new BigDecimal(stringBuilder.toString());
    }

    public BigDecimal readIntegerFixedPoint() throws IOException {
        short readShort = this.data.readShort();
        int readUnsignedShort = this.data.readUnsignedShort();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.valueOf(readShort));
        stringBuilder.append("");
        stringBuilder.append(String.valueOf(readUnsignedShort));
        return new BigDecimal(stringBuilder.toString());
    }

    public String readString(int i, String str) throws IOException {
        String str2 = new String(readBytes(i), str);
        int indexOf = str2.indexOf(0);
        return indexOf < 0 ? str2 : str2.substring(0, indexOf);
    }

    public String readString(String str) throws IOException {
        return readString((int) getRemaining(), str);
    }

    public void skip(int i) throws IOException {
        int i2 = 0;
        while (i2 < i) {
            int skipBytes = this.data.skipBytes(i - i2);
            if (skipBytes > 0) {
                i2 += skipBytes;
            } else {
                throw new EOFException();
            }
        }
    }

    public void skip() throws IOException {
        while (getRemaining() > 0) {
            if (((RangeInputStream) getInput()).skip(getRemaining()) == 0) {
                throw new EOFException("Cannot skip atom");
            }
        }
    }

    private StringBuffer appendPath(StringBuffer stringBuffer, MP4Box<?> mP4Box) {
        if (mP4Box.getParent() != null) {
            appendPath(stringBuffer, mP4Box.getParent());
            stringBuffer.append("/");
        }
        stringBuffer.append(mP4Box.getType());
        return stringBuffer;
    }

    public String getPath() {
        StringBuffer stringBuffer = new StringBuffer();
        appendPath(stringBuffer, this);
        return stringBuffer.toString();
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        appendPath(stringBuffer, this);
        stringBuffer.append("[off=");
        stringBuffer.append(getOffset());
        stringBuffer.append(",pos=");
        stringBuffer.append(getPosition());
        stringBuffer.append(",len=");
        stringBuffer.append(getLength());
        stringBuffer.append("]");
        return stringBuffer.toString();
    }
}
