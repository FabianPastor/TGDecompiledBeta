package j$.util;

import java.util.NoSuchElementException;

public final class C {
    private static final C c = new C();
    private final boolean a;
    private final int b;

    private C() {
        this.a = false;
        this.b = 0;
    }

    public static C a() {
        return c;
    }

    private C(int value) {
        this.a = true;
        this.b = value;
    }

    public static C d(int value) {
        return new C(value);
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
        if (!(obj instanceof C)) {
            return false;
        }
        C other = (C) obj;
        if (!this.a || !other.a) {
            if (this.a == other.a) {
                return true;
            }
            return false;
        } else if (this.b == other.b) {
            return true;
        } else {
            return false;
        }
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
