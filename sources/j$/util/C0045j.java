package j$.util;

import java.util.NoSuchElementException;
/* renamed from: j$.util.j  reason: case insensitive filesystem */
/* loaded from: classes2.dex */
public final class CLASSNAMEj {
    private static final CLASSNAMEj c = new CLASSNAMEj();
    private final boolean a;
    private final double b;

    private CLASSNAMEj() {
        this.a = false;
        this.b = Double.NaN;
    }

    private CLASSNAMEj(double d) {
        this.a = true;
        this.b = d;
    }

    public static CLASSNAMEj a() {
        return c;
    }

    public static CLASSNAMEj d(double d) {
        return new CLASSNAMEj(d);
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
        if (!(obj instanceof CLASSNAMEj)) {
            return false;
        }
        CLASSNAMEj CLASSNAMEj = (CLASSNAMEj) obj;
        boolean z = this.a;
        if (!z || !CLASSNAMEj.a) {
            if (z == CLASSNAMEj.a) {
                return true;
            }
        } else if (Double.compare(this.b, CLASSNAMEj.b) == 0) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        if (this.a) {
            long doubleToLongBits = Double.doubleToLongBits(this.b);
            return (int) (doubleToLongBits ^ (doubleToLongBits >>> 32));
        }
        return 0;
    }

    public String toString() {
        return this.a ? String.format("OptionalDouble[%s]", Double.valueOf(this.b)) : "OptionalDouble.empty";
    }
}
