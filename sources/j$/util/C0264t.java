package j$.util;

import java.util.NoSuchElementException;

/* renamed from: j$.util.t  reason: case insensitive filesystem */
public final class CLASSNAMEt {
    private static final CLASSNAMEt c = new CLASSNAMEt();
    private final boolean a;
    private final double b;

    private CLASSNAMEt() {
        this.a = false;
        this.b = Double.NaN;
    }

    private CLASSNAMEt(double d) {
        this.a = true;
        this.b = d;
    }

    public static CLASSNAMEt a() {
        return c;
    }

    public static CLASSNAMEt d(double d) {
        return new CLASSNAMEt(d);
    }

    public double b() {
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
        if (!(obj instanceof CLASSNAMEt)) {
            return false;
        }
        CLASSNAMEt tVar = (CLASSNAMEt) obj;
        boolean z = this.a;
        if (!z || !tVar.a) {
            if (z == tVar.a) {
                return true;
            }
        } else if (Double.compare(this.b, tVar.b) == 0) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.a) {
            return Double.doubleToLongBits(this.b);
        }
        return 0;
    }

    public String toString() {
        if (!this.a) {
            return "OptionalDouble.empty";
        }
        return String.format("OptionalDouble[%s]", new Object[]{Double.valueOf(this.b)});
    }
}
