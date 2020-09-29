package j$.time.format;

import j$.time.u.B;
import java.util.Iterator;
import java.util.Locale;

/* renamed from: j$.time.format.e  reason: case insensitive filesystem */
class CLASSNAMEe extends F {
    final /* synthetic */ E c;

    CLASSNAMEe(z this$0, E e) {
        this.c = e;
    }

    public String i(B field, long value, K style, Locale locale) {
        return this.c.a(value, style);
    }

    public Iterator k(B field, K style, Locale locale) {
        return this.c.b(style);
    }
}
