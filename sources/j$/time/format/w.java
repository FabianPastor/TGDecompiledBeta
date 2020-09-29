package j$.time.format;

import j$.time.u.B;
import j$.time.u.I;
import java.util.Locale;

final class w implements CLASSNAMEj {
    private char a;
    private int b;

    w(char chr, int count) {
        this.a = chr;
        this.b = count;
    }

    public boolean i(C context, StringBuilder buf) {
        return a(context.d()).i(context, buf);
    }

    public int p(A context, CharSequence text, int position) {
        return a(context.i()).p(context, text, position);
    }

    private CLASSNAMEj a(Locale locale) {
        B field;
        I weekDef = I.h(locale);
        char c = this.a;
        if (c == 'W') {
            field = weekDef.j();
        } else if (c == 'Y') {
            B field2 = weekDef.i();
            if (this.b == 2) {
                return new s(field2, 2, 2, 0, s.i, 0, (CLASSNAMEe) null);
            }
            int i = this.b;
            return new n(field2, i, 19, i < 4 ? J.NORMAL : J.EXCEEDS_PAD, -1);
        } else if (c == 'c' || c == 'e') {
            field = weekDef.d();
        } else if (c == 'w') {
            field = weekDef.k();
        } else {
            throw new IllegalStateException("unreachable");
        }
        return new n(field, this.b == 2 ? 2 : 1, 2, J.NOT_NEGATIVE);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(30);
        sb.append("Localized(");
        char c = this.a;
        if (c == 'Y') {
            int i = this.b;
            if (i == 1) {
                sb.append("WeekBasedYear");
            } else if (i == 2) {
                sb.append("ReducedValue(WeekBasedYear,2,2,2000-01-01)");
            } else {
                sb.append("WeekBasedYear,");
                sb.append(this.b);
                sb.append(",");
                sb.append(19);
                sb.append(",");
                sb.append(this.b < 4 ? J.NORMAL : J.EXCEEDS_PAD);
            }
        } else {
            if (c == 'W') {
                sb.append("WeekOfMonth");
            } else if (c == 'c' || c == 'e') {
                sb.append("DayOfWeek");
            } else if (c == 'w') {
                sb.append("WeekOfWeekBasedYear");
            }
            sb.append(",");
            sb.append(this.b);
        }
        sb.append(")");
        return sb.toString();
    }
}
