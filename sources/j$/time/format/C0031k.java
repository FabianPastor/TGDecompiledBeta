package j$.time.format;

import j$.CLASSNAMEp;
import j$.time.u.B;
import j$.time.u.G;
import java.math.BigDecimal;
import java.math.RoundingMode;

/* renamed from: j$.time.format.k  reason: case insensitive filesystem */
final class CLASSNAMEk implements CLASSNAMEj {
    private final B a;
    private final int b;
    private final int c;
    private final boolean d;

    CLASSNAMEk(B field, int minWidth, int maxWidth, boolean decimalPoint) {
        CLASSNAMEp.a(field, "field");
        if (!field.p().f()) {
            throw new IllegalArgumentException("Field must have a fixed set of values: " + field);
        } else if (minWidth < 0 || minWidth > 9) {
            throw new IllegalArgumentException("Minimum width must be from 0 to 9 inclusive but was " + minWidth);
        } else if (maxWidth < 1 || maxWidth > 9) {
            throw new IllegalArgumentException("Maximum width must be from 1 to 9 inclusive but was " + maxWidth);
        } else if (maxWidth >= minWidth) {
            this.a = field;
            this.b = minWidth;
            this.c = maxWidth;
            this.d = decimalPoint;
        } else {
            throw new IllegalArgumentException("Maximum width must exceed or equal the minimum width but " + maxWidth + " < " + minWidth);
        }
    }

    public boolean i(C context, StringBuilder buf) {
        Long value = context.f(this.a);
        if (value == null) {
            return false;
        }
        G decimalStyle = context.c();
        BigDecimal fraction = b(value.longValue());
        if (fraction.scale() != 0) {
            String str = decimalStyle.a(fraction.setScale(Math.min(Math.max(fraction.scale(), this.b), this.c), RoundingMode.FLOOR).toPlainString().substring(2));
            if (this.d) {
                buf.append(decimalStyle.c());
            }
            buf.append(str);
            return true;
        } else if (this.b <= 0) {
            return true;
        } else {
            if (this.d) {
                buf.append(decimalStyle.c());
            }
            for (int i = 0; i < this.b; i++) {
                buf.append(decimalStyle.f());
            }
            return true;
        }
    }

    public int p(A context, CharSequence text, int position) {
        int pos;
        int position2 = position;
        int effectiveMin = context.l() ? this.b : 0;
        int effectiveMax = context.l() ? this.c : 9;
        int length = text.length();
        if (position2 == length) {
            return effectiveMin > 0 ? position2 ^ -1 : position2;
        }
        if (this.d) {
            if (text.charAt(position) != context.g().c()) {
                return effectiveMin > 0 ? position2 ^ -1 : position2;
            }
            position2++;
        }
        int minEndPos = position2 + effectiveMin;
        if (minEndPos > length) {
            return position2 ^ -1;
        }
        int maxEndPos = Math.min(position2 + effectiveMax, length);
        int pos2 = position2;
        int total = 0;
        while (true) {
            if (pos2 >= maxEndPos) {
                CharSequence charSequence = text;
                pos = pos2;
                break;
            }
            int pos3 = pos2 + 1;
            int digit = context.g().b(text.charAt(pos2));
            if (digit >= 0) {
                total = (total * 10) + digit;
                pos2 = pos3;
            } else if (pos3 < minEndPos) {
                return position2 ^ -1;
            } else {
                pos = pos3 - 1;
            }
        }
        BigDecimal fraction = new BigDecimal(total).movePointLeft(pos - position2);
        BigDecimal bigDecimal = fraction;
        return context.o(this.a, a(fraction), position2, pos);
    }

    private BigDecimal b(long value) {
        G range = this.a.p();
        range.b(value, this.a);
        BigDecimal minBD = BigDecimal.valueOf(range.e());
        BigDecimal fraction = BigDecimal.valueOf(value).subtract(minBD).divide(BigDecimal.valueOf(range.d()).subtract(minBD).add(BigDecimal.ONE), 9, RoundingMode.FLOOR);
        return fraction.compareTo(BigDecimal.ZERO) == 0 ? BigDecimal.ZERO : fraction.stripTrailingZeros();
    }

    private long a(BigDecimal fraction) {
        G range = this.a.p();
        BigDecimal minBD = BigDecimal.valueOf(range.e());
        return fraction.multiply(BigDecimal.valueOf(range.d()).subtract(minBD).add(BigDecimal.ONE)).setScale(0, RoundingMode.FLOOR).add(minBD).longValueExact();
    }

    public String toString() {
        String decimal = this.d ? ",DecimalPoint" : "";
        return "Fraction(" + this.a + "," + this.b + "," + this.c + decimal + ")";
    }
}
