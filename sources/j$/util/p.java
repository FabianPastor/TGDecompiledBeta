package j$.util;

import java.util.NoSuchElementException;

public final class p {
    private static final p c = new p();
    private final boolean a;
    private final double b;

    private p() {
        this.a = false;
        this.b = Double.NaN;
    }

    private p(double d) {
        this.a = true;
        this.b = d;
    }

    public static p a() {
        return c;
    }

    public static p d(double d) {
        return new p(d);
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
        if (!(obj instanceof p)) {
            return false;
        }
        p pVar = (p) obj;
        boolean z = this.a;
        if (!z || !pVar.a) {
            if (z == pVar.a) {
                return true;
            }
        } else if (Double.compare(this.b, pVar.b) == 0) {
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
