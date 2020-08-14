package j$.util;

import j$.CLASSNAMEd;
import j$.CLASSNAMEe;
import java.util.NoSuchElementException;

/* renamed from: j$.util.z  reason: case insensitive filesystem */
public final class CLASSNAMEz {
    private static final CLASSNAMEz b = new CLASSNAMEz();
    private final Object a;

    private CLASSNAMEz() {
        this.a = null;
    }

    public static CLASSNAMEz a() {
        return b;
    }

    private CLASSNAMEz(Object value) {
        value.getClass();
        this.a = value;
    }

    public static CLASSNAMEz d(Object value) {
        return new CLASSNAMEz(value);
    }

    public Object b() {
        Object obj = this.a;
        if (obj != null) {
            return obj;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean c() {
        return this.a != null;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CLASSNAMEz)) {
            return false;
        }
        return CLASSNAMEe.a(this.a, ((CLASSNAMEz) obj).a);
    }

    public int hashCode() {
        return CLASSNAMEd.a(this.a);
    }

    public String toString() {
        Object obj = this.a;
        if (obj == null) {
            return "Optional.empty";
        }
        return String.format("Optional[%s]", new Object[]{obj});
    }
}
