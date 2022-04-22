package j$.util;

import java.util.NoSuchElementException;

/* renamed from: j$.util.l  reason: case insensitive filesystem */
public final class CLASSNAMEl {
    private static final CLASSNAMEl c = new CLASSNAMEl();
    private final boolean a;
    private final long b;

    private CLASSNAMEl() {
        this.a = false;
        this.b = 0;
    }

    private CLASSNAMEl(long j) {
        this.a = true;
        this.b = j;
    }

    public static CLASSNAMEl a() {
        return c;
    }

    public static CLASSNAMEl d(long j) {
        return new CLASSNAMEl(j);
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
        if (!(obj instanceof CLASSNAMEl)) {
            return false;
        }
        CLASSNAMEl lVar = (CLASSNAMEl) obj;
        boolean z = this.a;
        if (!z || !lVar.a) {
            if (z == lVar.a) {
                return true;
            }
        } else if (this.b == lVar.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (!this.a) {
            return 0;
        }
        long j = this.b;
        return (int) (j ^ (j >>> 32));
    }

    public String toString() {
        if (!this.a) {
            return "OptionalLong.empty";
        }
        return String.format("OptionalLong[%s]", new Object[]{Long.valueOf(this.b)});
    }
}
