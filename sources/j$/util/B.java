package j$.util;

import java.util.NoSuchElementException;

public final class B {
    private static final B c = new B();
    private final boolean a;
    private final double b;

    private B() {
        this.a = false;
        this.b = Double.NaN;
    }

    public static B a() {
        return c;
    }

    private B(double value) {
        this.a = true;
        this.b = value;
    }

    public static B d(double value) {
        return new B(value);
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
        if (!(obj instanceof B)) {
            return false;
        }
        B other = (B) obj;
        if (!this.a || !other.a) {
            if (this.a == other.a) {
                return true;
            }
            return false;
        } else if (Double.compare(this.b, other.b) == 0) {
            return true;
        } else {
            return false;
        }
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
