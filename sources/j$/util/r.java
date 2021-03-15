package j$.util;

import a.CLASSNAMEc;
import java.util.NoSuchElementException;

public final class r {
    private static final r c = new r();

    /* renamed from: a  reason: collision with root package name */
    private final boolean var_a;
    private final long b;

    private r() {
        this.var_a = false;
        this.b = 0;
    }

    private r(long j) {
        this.var_a = true;
        this.b = j;
    }

    public static r a() {
        return c;
    }

    public static r d(long j) {
        return new r(j);
    }

    public long b() {
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
        if (!(obj instanceof r)) {
            return false;
        }
        r rVar = (r) obj;
        boolean z = this.var_a;
        if (!z || !rVar.var_a) {
            if (z == rVar.var_a) {
                return true;
            }
        } else if (this.b == rVar.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.var_a) {
            return CLASSNAMEc.a(this.b);
        }
        return 0;
    }

    public String toString() {
        if (!this.var_a) {
            return "OptionalLong.empty";
        }
        return String.format("OptionalLong[%s]", new Object[]{Long.valueOf(this.b)});
    }
}
