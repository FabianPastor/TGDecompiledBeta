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

    public MP4Box(I input, MP4Box<?> parent, String type) {
        this.input = input;
        this.parent = parent;
        this.type = type;
        this.data = new DataInputStream(input);
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
Error: jadx.core.utils.exceptions.JadxRuntimeException: Unknown predecessor block by arg (r3_2 'atomInput' org.telegram.messenger.audioinfo.util.RangeInputStream) in PHI: PHI: (r3_4 'atomInput' org.telegram.messenger.audioinfo.util.RangeInputStream) = (r3_2 'atomInput' org.telegram.messenger.audioinfo.util.RangeInputStream), (r3_3 'atomInput' org.telegram.messenger.audioinfo.util.RangeInputStream) binds: {(r3_2 'atomInput' org.telegram.messenger.audioinfo.util.RangeInputStream)=B:5:0x0021, (r3_3 'atomInput' org.telegram.messenger.audioinfo.util.RangeInputStream)=B:6:0x0037}
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
        r14 = this;
        r0 = r14.child;
        if (r0 == 0) goto L_0x0009;
    L_0x0004:
        r0 = r14.child;
        r0.skip();
    L_0x0009:
        r0 = r14.data;
        r0 = r0.readInt();
        r1 = 4;
        r1 = new byte[r1];
        r2 = r14.data;
        r2.readFully(r1);
        r2 = new java.lang.String;
        r3 = "ISO8859_1";
        r2.<init>(r1, r3);
        r3 = 1;
        if (r0 != r3) goto L_0x0037;
    L_0x0021:
        r3 = new org.telegram.messenger.audioinfo.util.RangeInputStream;
        r5 = r14.input;
        r6 = 16;
        r4 = r14.data;
        r8 = r4.readLong();
        r10 = 16;
        r12 = r8 - r10;
        r4 = r3;
        r8 = r12;
        r4.<init>(r5, r6, r8);
        goto L_0x0044;
    L_0x0037:
        r3 = new org.telegram.messenger.audioinfo.util.RangeInputStream;
        r5 = r14.input;
        r6 = 8;
        r4 = r0 + -8;
        r8 = (long) r4;
        r4 = r3;
        r4.<init>(r5, r6, r8);
    L_0x0044:
        r4 = new org.telegram.messenger.audioinfo.m4a.MP4Atom;
        r4.<init>(r3, r14, r2);
        r14.child = r4;
        return r4;
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.audioinfo.m4a.MP4Box.nextChild():org.telegram.messenger.audioinfo.m4a.MP4Atom");
    }

    public MP4Atom nextChild(String expectedTypeExpression) throws IOException {
        MP4Atom atom = nextChild();
        if (atom.getType().matches(expectedTypeExpression)) {
            return atom;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("atom type mismatch, expected ");
        stringBuilder.append(expectedTypeExpression);
        stringBuilder.append(", got ");
        stringBuilder.append(atom.getType());
        throw new IOException(stringBuilder.toString());
    }
}
