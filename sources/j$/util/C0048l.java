package j$.util;

import java.util.NoSuchElementException;
/* renamed from: j$.util.l  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEl {
    private static final CLASSNAMEl c = new CLASSNAMEl();
    private final boolean a;
    private final long b;

    private CLASSNAMEl() {
        this.a = false;
        this.b = 0L;
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
        CLASSNAMEl CLASSNAMEl = (CLASSNAMEl) obj;
        boolean z = this.a;
        if (!z || !CLASSNAMEl.a) {
            if (z == CLASSNAMEl.a) {
                return true;
            }
        } else if (this.b == CLASSNAMEl.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.a) {
            long j = this.b;
            return (int) (j ^ (j >>> 32));
        }
        return 0;
    }

    public String toString() {
        return this.a ? String.format("OptionalLong[%s]", Long.valueOf(this.b)) : "OptionalLong.empty";
    }
}
