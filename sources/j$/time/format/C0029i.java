package j$.time.format;

import java.util.List;

/* renamed from: j$.time.format.i  reason: case insensitive filesystem */
final class CLASSNAMEi implements CLASSNAMEj {
    private final CLASSNAMEj[] a;
    private final boolean b;

    CLASSNAMEi(List printerParsers, boolean optional) {
        this((CLASSNAMEj[]) printerParsers.toArray(new CLASSNAMEj[printerParsers.size()]), optional);
    }

    CLASSNAMEi(CLASSNAMEj[] printerParsers, boolean optional) {
        this.a = printerParsers;
        this.b = optional;
    }

    public CLASSNAMEi a(boolean optional) {
        if (optional == this.b) {
            return this;
        }
        return new CLASSNAMEi(this.a, optional);
    }

    public boolean i(C context, StringBuilder buf) {
        int length = buf.length();
        if (this.b) {
            context.h();
        }
        try {
            for (CLASSNAMEj pp : this.a) {
                if (!pp.i(context, buf)) {
                    buf.setLength(length);
                    return true;
                }
            }
            if (this.b) {
                context.b();
            }
            return true;
        } finally {
            if (this.b) {
                context.b();
            }
        }
    }

    public int p(A context, CharSequence text, int position) {
        if (this.b) {
            context.r();
            int pos = position;
            for (CLASSNAMEj pp : this.a) {
                pos = pp.p(context, text, pos);
                if (pos < 0) {
                    context.f(false);
                    return position;
                }
            }
            context.f(true);
            return pos;
        }
        for (CLASSNAMEj pp2 : this.a) {
            position = pp2.p(context, text, position);
            if (position < 0) {
                break;
            }
        }
        return position;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        if (this.a != null) {
            buf.append(this.b ? "[" : "(");
            for (CLASSNAMEj pp : this.a) {
                buf.append(pp);
            }
            buf.append(this.b ? "]" : ")");
        }
        return buf.toString();
    }
}
