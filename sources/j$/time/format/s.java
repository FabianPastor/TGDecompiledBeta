package j$.time.format;

import j$.time.LocalDate;
import j$.time.t.f;
import j$.time.t.p;
import j$.time.u.B;

final class s extends n {
    static final LocalDate i = LocalDate.a0(2000, 1, 1);
    private final int g;
    private final f h;

    /* synthetic */ s(B x0, int x1, int x2, int x3, f x4, int x5, CLASSNAMEe x6) {
        this(x0, x1, x2, x3, x4, x5);
    }

    s(B field, int minWidth, int maxWidth, int baseValue, f baseDate) {
        this(field, minWidth, maxWidth, baseValue, baseDate, 0);
        if (minWidth < 1 || minWidth > 10) {
            throw new IllegalArgumentException("The minWidth must be from 1 to 10 inclusive but was " + minWidth);
        } else if (maxWidth < 1 || maxWidth > 10) {
            throw new IllegalArgumentException("The maxWidth must be from 1 to 10 inclusive but was " + minWidth);
        } else if (maxWidth < minWidth) {
            throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + maxWidth + " < " + minWidth);
        } else if (baseDate != null) {
        } else {
            if (!field.p().i((long) baseValue)) {
                throw new IllegalArgumentException("The base value must be within the range of the field");
            } else if (((long) baseValue) + n.f[maxWidth] > 2147483647L) {
                throw new j$.time.f("Unable to add printer-parser as the range exceeds the capacity of an int");
            }
        }
    }

    private s(B field, int minWidth, int maxWidth, int baseValue, f baseDate, int subsequentWidth) {
        super(field, minWidth, maxWidth, J.NOT_NEGATIVE, subsequentWidth);
        this.g = baseValue;
        this.h = baseDate;
    }

    /* access modifiers changed from: package-private */
    public long b(C context, long value) {
        long absValue = Math.abs(value);
        int baseValue = this.g;
        if (this.h != null) {
            baseValue = p.e(context.e()).o(this.h).i(this.a);
        }
        if (value >= ((long) baseValue)) {
            long[] jArr = n.f;
            int i2 = this.b;
            if (value < ((long) baseValue) + jArr[i2]) {
                return absValue % jArr[i2];
            }
        }
        return absValue % n.f[this.c];
    }

    /* access modifiers changed from: package-private */
    /* renamed from: d */
    public int g(A context, long value, int errorPos, int successPos) {
        long range;
        long value2;
        int baseValue = this.g;
        if (this.h != null) {
            int baseValue2 = context.h().o(this.h).i(this.a);
            context.a(new CLASSNAMEd(this, context, value, errorPos, successPos));
            baseValue = baseValue2;
        } else {
            A a = context;
        }
        int parseLen = successPos - errorPos;
        int i2 = this.b;
        if (parseLen != i2 || value < 0) {
            range = value;
        } else {
            long range2 = n.f[i2];
            long basePart = ((long) baseValue) - (((long) baseValue) % range2);
            if (baseValue > 0) {
                value2 = basePart + value;
            } else {
                value2 = basePart - value;
            }
            if (value2 < ((long) baseValue)) {
                range = value2 + range2;
            } else {
                range = value2;
            }
        }
        return context.o(this.a, range, errorPos, successPos);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: h */
    public s e() {
        if (this.e == -1) {
            return this;
        }
        return new s(this.a, this.b, this.c, this.g, this.h, -1);
    }

    /* access modifiers changed from: package-private */
    /* renamed from: j */
    public s f(int subsequentWidth) {
        return new s(this.a, this.b, this.c, this.g, this.h, this.e + subsequentWidth);
    }

    /* access modifiers changed from: package-private */
    public boolean c(A context) {
        if (!context.l()) {
            return false;
        }
        return super.c(context);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ReducedValue(");
        sb.append(this.a);
        sb.append(",");
        sb.append(this.b);
        sb.append(",");
        sb.append(this.c);
        sb.append(",");
        Object obj = this.h;
        if (obj == null) {
            obj = Integer.valueOf(this.g);
        }
        sb.append(obj);
        sb.append(")");
        return sb.toString();
    }
}
