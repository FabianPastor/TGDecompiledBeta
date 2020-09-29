package j$.time.format;

import j$.time.t.q;
import j$.time.t.t;
import j$.time.u.B;
import j$.time.u.C;
import java.util.Iterator;
import java.util.Map;

final class v implements CLASSNAMEj {
    private final B a;
    private final K b;
    private final F c;
    private volatile n d;

    v(B field, K textStyle, F provider) {
        this.a = field;
        this.b = textStyle;
        this.c = provider;
    }

    public boolean i(C context, StringBuilder buf) {
        String text;
        Long value = context.f(this.a);
        if (value == null) {
            return false;
        }
        q chrono = (q) context.e().r(C.a());
        if (chrono == null || chrono == t.a) {
            text = this.c.i(this.a, value.longValue(), this.b, context.d());
        } else {
            text = this.c.h(chrono, this.a, value.longValue(), this.b, context.d());
        }
        if (text == null) {
            return a().i(context, buf);
        }
        buf.append(text);
        return true;
    }

    public int p(A context, CharSequence parseText, int position) {
        Iterator<Map.Entry<String, Long>> it;
        int i = position;
        int length = parseText.length();
        if (i < 0 || i > length) {
            A a2 = context;
            CharSequence charSequence = parseText;
            throw new IndexOutOfBoundsException();
        }
        K style = context.l() ? this.b : null;
        q chrono = context.h();
        if (chrono == null || chrono == t.a) {
            it = this.c.k(this.a, style, context.i());
        } else {
            it = this.c.j(chrono, this.a, style, context.i());
        }
        if (it != null) {
            while (it.hasNext()) {
                Map.Entry<String, Long> entry = it.next();
                String itText = entry.getKey();
                if (context.s(itText, 0, parseText, position, itText.length())) {
                    return context.o(this.a, entry.getValue().longValue(), position, i + itText.length());
                }
            }
            if (context.l()) {
                return i ^ -1;
            }
        }
        A a3 = context;
        return a().p(context, parseText, i);
    }

    private n a() {
        if (this.d == null) {
            this.d = new n(this.a, 1, 19, J.NORMAL);
        }
        return this.d;
    }

    public String toString() {
        if (this.b == K.FULL) {
            return "Text(" + this.a + ")";
        }
        return "Text(" + this.a + "," + this.b + ")";
    }
}
