package j$.util;

import java.util.NoSuchElementException;

/* renamed from: j$.util.u  reason: case insensitive filesystem */
public final class CLASSNAMEu {
    private static final CLASSNAMEu c = new CLASSNAMEu();
    private final boolean a;
    private final int b;

    private CLASSNAMEu() {
        this.a = false;
        this.b = 0;
    }

    private CLASSNAMEu(int i) {
        this.a = true;
        this.b = i;
    }

    public static CLASSNAMEu a() {
        return c;
    }

    public static CLASSNAMEu d(int i) {
        return new CLASSNAMEu(i);
    }

    public int b() {
        if (this.a) {
            return this.b;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean c() {
        return this.a;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CLASSNAMEu)) {
            return false;
        }
        CLASSNAMEu uVar = (CLASSNAMEu) obj;
        boolean z = this.a;
        if (!z || !uVar.a) {
            if (z == uVar.a) {
                return true;
            }
        } else if (this.b == uVar.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.a) {
            return this.b;
        }
        return 0;
    }

    public String toString() {
        if (!this.a) {
            return "OptionalInt.empty";
        }
        return String.format("OptionalInt[%s]", new Object[]{Integer.valueOf(this.b)});
    }
}
