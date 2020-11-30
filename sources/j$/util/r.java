package j$.util;

import j$.CLASSNAMEc;
import java.util.NoSuchElementException;

public final class r {
    private static final r c = new r();
    private final boolean a;
    private final long b;

    private r() {
        this.a = false;
        this.b = 0;
    }

    private r(long j) {
        this.a = true;
        this.b = j;
    }

    public static r a() {
        return c;
    }

    public static r d(long j) {
        return new r(j);
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
        if (!(obj instanceof r)) {
            return false;
        }
        r rVar = (r) obj;
        boolean z = this.a;
        if (!z || !rVar.a) {
            if (z == rVar.a) {
                return true;
            }
        } else if (this.b == rVar.b) {
            return true;
        }
        return false;
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
