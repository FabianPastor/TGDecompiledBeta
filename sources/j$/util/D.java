package j$.util;

import j$.CLASSNAMEc;
import java.util.NoSuchElementException;

public final class D {
    private static final D c = new D();
    private final boolean a;
    private final long b;

    private D() {
        this.a = false;
        this.b = 0;
    }

    public static D a() {
        return c;
    }

    private D(long value) {
        this.a = true;
        this.b = value;
    }

    public static D d(long value) {
        return new D(value);
    }

    public long b() {
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
        if (!(obj instanceof D)) {
            return false;
        }
        D other = (D) obj;
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
            return CLASSNAMEc.a(this.b);
        }
        return 0;
    }

    public String toString() {
        if (!this.a) {
            return "OptionalLong.empty";
        }
        return String.format("OptionalLong[%s]", new Object[]{Long.valueOf(this.b)});
    }
}
