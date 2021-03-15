package j$.util;

import java.util.NoSuchElementException;

public final class p {
    private static final p c = new p();

    /* renamed from: a  reason: collision with root package name */
    private final boolean var_a;
    private final double b;

    private p() {
        this.var_a = false;
        this.b = Double.NaN;
    }

    private p(double d) {
        this.var_a = true;
        this.b = d;
    }

    public static p a() {
        return c;
    }

    public static p d(double d) {
        return new p(d);
    }

    public double b() {
        if (this.var_a) {
            return this.b;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean c() {
        return this.var_a;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof p)) {
            return false;
        }
        p pVar = (p) obj;
        boolean z = this.var_a;
        if (!z || !pVar.var_a) {
            if (z == pVar.var_a) {
                return true;
            }
        } else if (Double.compare(this.b, pVar.b) == 0) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.var_a) {
            return Double.doubleToLongBits(this.b);
        }
        return 0;
    }

    public String toString() {
        if (!this.var_a) {
            return "OptionalDouble.empty";
        }
        return String.format("OptionalDouble[%s]", new Object[]{Double.valueOf(this.b)});
    }
}
