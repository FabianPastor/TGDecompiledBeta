package j$.time.format;

import j$.time.temporal.k;

final class h implements g {
    private final k a;
    private final int b;
    private final int c;
    private final boolean d;

    h(k kVar, int i, int i2, boolean z) {
        if (kVar == null) {
            throw new NullPointerException("field");
        } else if (!kVar.a().b()) {
            throw new IllegalArgumentException("Field must have a fixed set of values: " + kVar);
        } else if (i < 0 || i > 9) {
            throw new IllegalArgumentException("Minimum width must be from 0 to 9 inclusive but was " + i);
        } else if (i2 < 1 || i2 > 9) {
            throw new IllegalArgumentException("Maximum width must be from 1 to 9 inclusive but was " + i2);
        } else if (i2 >= i) {
            this.a = kVar;
            this.b = i;
            this.c = i2;
            this.d = z;
        } else {
            throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + i2 + " < " + i);
        }
    }

    public String toString() {
        String str = this.d ? ",DecimalPoint" : "";
        return "Fraction(" + this.a + "," + this.b + "," + this.c + str + ")";
    }
}
