package j$.util;

import java.util.NoSuchElementException;

/* renamed from: j$.util.j  reason: case insensitive filesystem */
public final class CLASSNAMEj {
    private static final CLASSNAMEj c = new CLASSNAMEj();
    private final boolean a;
    private final double b;

    private CLASSNAMEj() {
        this.a = false;
        this.b = Double.NaN;
    }

    private CLASSNAMEj(double d) {
        this.a = true;
        this.b = d;
    }

    public static CLASSNAMEj a() {
        return c;
    }

    public static CLASSNAMEj d(double d) {
        return new CLASSNAMEj(d);
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
        if (!(obj instanceof CLASSNAMEj)) {
            return false;
        }
        CLASSNAMEj jVar = (CLASSNAMEj) obj;
        boolean z = this.a;
        if (!z || !jVar.a) {
            if (z == jVar.a) {
                return true;
            }
        } else if (Double.compare(this.b, jVar.b) == 0) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (!this.a) {
            return 0;
        }
        long doubleToLongBits = Double.doubleToLongBits(this.b);
        return (int) (doubleToLongBits ^ (doubleToLongBits >>> 32));
    }

    public String toString() {
        if (!this.a) {
            return "OptionalDouble.empty";
        }
        return String.format("OptionalDouble[%s]", new Object[]{Double.valueOf(this.b)});
    }
}
