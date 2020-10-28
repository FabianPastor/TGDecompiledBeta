package j$.util;

import j$.CLASSNAMEc;
import java.util.NoSuchElementException;

/* renamed from: j$.util.v  reason: case insensitive filesystem */
public final class CLASSNAMEv {
    private static final CLASSNAMEv c = new CLASSNAMEv();
    private final boolean a;
    private final long b;

    private CLASSNAMEv() {
        this.a = false;
        this.b = 0;
    }

    private CLASSNAMEv(long j) {
        this.a = true;
        this.b = j;
    }

    public static CLASSNAMEv a() {
        return c;
    }

    public static CLASSNAMEv d(long j) {
        return new CLASSNAMEv(j);
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
        if (!(obj instanceof CLASSNAMEv)) {
            return false;
        }
        CLASSNAMEv vVar = (CLASSNAMEv) obj;
        boolean z = this.a;
        if (!z || !vVar.a) {
            if (z == vVar.a) {
                return true;
            }
        } else if (this.b == vVar.b) {
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
