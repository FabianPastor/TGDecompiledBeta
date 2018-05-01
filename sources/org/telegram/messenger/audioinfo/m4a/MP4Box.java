package org.telegram.messenger.audioinfo.m4a;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import org.telegram.messenger.audioinfo.util.PositionInputStream;

public class MP4Box<I extends PositionInputStream> {
    protected static final String ASCII = "ISO8859_1";
    private MP4Atom child;
    protected final DataInput data;
    private final I input;
    private final MP4Box<?> parent;
    private final String type;

    public MP4Box(I i, MP4Box<?> mP4Box, String str) {
        this.input = i;
        this.parent = mP4Box;
        this.type = str;
        this.data = new DataInputStream(i);
    }

    public String getType() {
        return this.type;
    }

    public MP4Box<?> getParent() {
        return this.parent;
    }

    public long getPosition() {
        return this.input.getPosition();
    }

    public I getInput() {
        return this.input;
    }

    protected MP4Atom getChild() {
        return this.child;
    }

    public org.telegram.messenger.audioinfo.m4a.MP4Atom nextChild() throws java.io.IOException {
        /* JADX: method processing error */
/*
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r1_4 org.telegram.messenger.audioinfo.util.RangeInputStream) in PHI: PHI: (r1_7 org.telegram.messenger.audioinfo.util.RangeInputStream) = (r1_4 org.telegram.messenger.audioinfo.util.RangeInputStream), (r1_6 org.telegram.messenger.audioinfo.util.RangeInputStream) binds: {(r1_4 org.telegram.messenger.audioinfo.util.RangeInputStream)=B:5:0x0023, (r1_6 org.telegram.messenger.audioinfo.util.RangeInputStream)=B:6:0x0039}
	at jadx.core.dex.instructions.PhiInsn.replaceArg(PhiInsn.java:79)
	at jadx.core.dex.visitors.ModVisitor.processInvoke(ModVisitor.java:222)
	at jadx.core.dex.visitors.ModVisitor.replaceStep(ModVisitor.java:83)
	at jadx.core.dex.visitors.ModVisitor.visit(ModVisitor.java:68)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:31)
	at jadx.core.dex.visitors.DepthTraversal.visit(DepthTraversal.java:17)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:282)
	at jadx.api.JavaClass.decompile(JavaClass.java:62)
	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:200)
*/
        /*
        r20 = this;
        r0 = r20;
        r1 = r0.child;
        if (r1 == 0) goto L_0x000b;
    L_0x0006:
        r1 = r0.child;
        r1.skip();
    L_0x000b:
        r1 = r0.data;
        r1 = r1.readInt();
        r2 = 4;
        r2 = new byte[r2];
        r3 = r0.data;
        r3.readFully(r2);
        r3 = new java.lang.String;
        r4 = "ISO8859_1";
        r3.<init>(r2, r4);
        r2 = 1;
        if (r1 != r2) goto L_0x0039;
    L_0x0023:
        r1 = new org.telegram.messenger.audioinfo.util.RangeInputStream;
        r5 = r0.input;
        r6 = 16;
        r2 = r0.data;
        r8 = r2.readLong();
        r10 = 16;
        r12 = r8 - r10;
        r4 = r1;
        r8 = r12;
        r4.<init>(r5, r6, r8);
        goto L_0x0049;
    L_0x0039:
        r2 = new org.telegram.messenger.audioinfo.util.RangeInputStream;
        r15 = r0.input;
        r16 = 8;
        r1 = r1 + -8;
        r4 = (long) r1;
        r14 = r2;
        r18 = r4;
        r14.<init>(r15, r16, r18);
        r1 = r2;
    L_0x0049:
        r2 = new org.telegram.messenger.audioinfo.m4a.MP4Atom;
        r2.<init>(r1, r0, r3);
        r0.child = r2;
        return r2;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.m4a.MP4Box.nextChild():org.telegram.messenger.audioinfo.m4a.MP4Atom");
    }

    public MP4Atom nextChild(String str) throws IOException {
        MP4Atom nextChild = nextChild();
        if (nextChild.getType().matches(str)) {
            return nextChild;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("atom type mismatch, expected ");
        stringBuilder.append(str);
        stringBuilder.append(", got ");
        stringBuilder.append(nextChild.getType());
        throw new IOException(stringBuilder.toString());
    }
}
