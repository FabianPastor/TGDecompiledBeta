package j$.util;

import java.util.NoSuchElementException;

public final class o {
    private static final o a = new o();
    private final boolean b;
    private final double c;

    private o() {
        this.b = false;
        this.c = Double.NaN;
    }

    private o(double d) {
        this.b = true;
        this.c = d;
    }

    public static o a() {
        return a;
    }

    public static o d(double d) {
        return new o(d);
    }

    public double b() {
        if (this.b) {
            return this.c;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean c() {
        return this.b;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof o)) {
            return false;
        }
        o oVar = (o) obj;
        boolean z = this.b;
        if (!z || !oVar.b) {
            if (z == oVar.b) {
                return true;
            }
        } else if (Double.compare(this.c, oVar.c) == 0) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (!this.b) {
            return 0;
        }
        long doubleToLongBits = Double.doubleToLongBits(this.c);
        return (int) (doubleToLongBits ^ (doubleToLongBits >>> 32));
    }

    public String toString() {
        if (!this.b) {
            return "OptionalDouble.empty";
        }
        return String.format("OptionalDouble[%s]", new Object[]{Double.valueOf(this.c)});
    }
}
