package j$.util;

import java.util.NoSuchElementException;
/* renamed from: j$.util.k  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEk {
    private static final CLASSNAMEk c = new CLASSNAMEk();
    private final boolean a;
    private final int b;

    private CLASSNAMEk() {
        this.a = false;
        this.b = 0;
    }

    private CLASSNAMEk(int i) {
        this.a = true;
        this.b = i;
    }

    public static CLASSNAMEk a() {
        return c;
    }

    public static CLASSNAMEk d(int i) {
        return new CLASSNAMEk(i);
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
        if (!(obj instanceof CLASSNAMEk)) {
            return false;
        }
        CLASSNAMEk CLASSNAMEk = (CLASSNAMEk) obj;
        boolean z = this.a;
        if (!z || !CLASSNAMEk.a) {
            if (z == CLASSNAMEk.a) {
                return true;
            }
        } else if (this.b == CLASSNAMEk.b) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.a) {
            return this.b;
        }
        return 0;
    }

    public String toString() {
        return this.a ? String.format("OptionalInt[%s]", Integer.valueOf(this.b)) : "OptionalInt.empty";
    }
}
