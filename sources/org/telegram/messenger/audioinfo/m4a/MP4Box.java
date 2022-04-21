package org.telegram.messenger.audioinfo.m4a;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import org.telegram.messenger.audioinfo.util.PositionInputStream;
import org.telegram.messenger.audioinfo.util.RangeInputStream;

public class MP4Box<I extends PositionInputStream> {
    protected static final String ASCII = "ISO8859_1";
    private MP4Atom child;
    protected final DataInput data;
    private final I input;
    private final MP4Box<?> parent;
    private final String type;

    public MP4Box(I input2, MP4Box<?> parent2, String type2) {
        this.input = input2;
        this.parent = parent2;
        this.type = type2;
        this.data = new DataInputStream(input2);
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

    /* access modifiers changed from: protected */
    public MP4Atom getChild() {
        return this.child;
    }

    public MP4Atom nextChild() throws IOException {
        RangeInputStream atomInput;
        MP4Atom mP4Atom = this.child;
        if (mP4Atom != null) {
            mP4Atom.skip();
        }
        int atomLength = this.data.readInt();
        byte[] typeBytes = new byte[4];
        this.data.readFully(typeBytes);
        String atomType = new String(typeBytes, "ISO8859_1");
        if (atomLength == 1) {
            atomInput = new RangeInputStream(this.input, 16, this.data.readLong() - 16);
        } else {
            atomInput = new RangeInputStream(this.input, 8, (long) (atomLength - 8));
        }
        MP4Atom mP4Atom2 = new MP4Atom(atomInput, this, atomType);
        this.child = mP4Atom2;
        return mP4Atom2;
    }

    public MP4Atom nextChild(String expectedTypeExpression) throws IOException {
        MP4Atom atom = nextChild();
        if (atom.getType().matches(expectedTypeExpression)) {
            return atom;
        }
        throw new IOException("atom type mismatch, expected " + expectedTypeExpression + ", got " + atom.getType());
    }
}
