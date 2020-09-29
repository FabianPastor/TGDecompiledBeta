package j$.time.format;

import j$.CLASSNAMEp;
import j$.time.o;
import j$.time.t.q;
import j$.time.t.t;
import j$.time.u.B;
import j$.time.u.w;
import j$.util.function.Consumer;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Set;

final class A {
    private DateTimeFormatter a;
    private boolean b = true;
    private boolean c = true;
    private final ArrayList d;
    private ArrayList e;

    A(DateTimeFormatter formatter) {
        ArrayList arrayList = new ArrayList();
        this.d = arrayList;
        this.e = null;
        this.a = formatter;
        arrayList.add(new H());
    }

    /* access modifiers changed from: package-private */
    public A d() {
        A newContext = new A(this.a);
        newContext.b = this.b;
        newContext.c = this.c;
        return newContext;
    }

    /* access modifiers changed from: package-private */
    public Locale i() {
        return this.a.f();
    }

    /* access modifiers changed from: package-private */
    public G g() {
        return this.a.e();
    }

    /* access modifiers changed from: package-private */
    public q h() {
        q chrono = e().c;
        if (chrono != null) {
            return chrono;
        }
        q chrono2 = this.a.d();
        if (chrono2 == null) {
            return t.a;
        }
        return chrono2;
    }

    /* access modifiers changed from: package-private */
    public boolean k() {
        return this.b;
    }

    /* access modifiers changed from: package-private */
    public void m(boolean caseSensitive) {
        this.b = caseSensitive;
    }

    /* access modifiers changed from: package-private */
    public boolean s(CharSequence cs1, int offset1, CharSequence cs2, int offset2, int length) {
        if (offset1 + length > cs1.length() || offset2 + length > cs2.length()) {
            return false;
        }
        if (k()) {
            for (int i = 0; i < length; i++) {
                if (cs1.charAt(offset1 + i) != cs2.charAt(offset2 + i)) {
                    return false;
                }
            }
            return true;
        }
        for (int i2 = 0; i2 < length; i2++) {
            char ch1 = cs1.charAt(offset1 + i2);
            char ch2 = cs2.charAt(offset2 + i2);
            if (ch1 != ch2 && Character.toUpperCase(ch1) != Character.toUpperCase(ch2) && Character.toLowerCase(ch1) != Character.toLowerCase(ch2)) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean b(char ch1, char ch2) {
        if (k()) {
            return ch1 == ch2;
        }
        return c(ch1, ch2);
    }

    static boolean c(char c1, char c2) {
        return c1 == c2 || Character.toUpperCase(c1) == Character.toUpperCase(c2) || Character.toLowerCase(c1) == Character.toLowerCase(c2);
    }

    /* access modifiers changed from: package-private */
    public boolean l() {
        return this.c;
    }

    /* access modifiers changed from: package-private */
    public void q(boolean strict) {
        this.c = strict;
    }

    /* access modifiers changed from: package-private */
    public void r() {
        this.d.add(e().j());
    }

    /* access modifiers changed from: package-private */
    public void f(boolean successful) {
        if (successful) {
            ArrayList arrayList = this.d;
            arrayList.remove(arrayList.size() - 2);
            return;
        }
        ArrayList arrayList2 = this.d;
        arrayList2.remove(arrayList2.size() - 1);
    }

    private H e() {
        ArrayList arrayList = this.d;
        return (H) arrayList.get(arrayList.size() - 1);
    }

    /* access modifiers changed from: package-private */
    public w t(I resolverStyle, Set resolverFields) {
        H parsed = e();
        parsed.c = h();
        o oVar = parsed.b;
        if (oVar == null) {
            oVar = this.a.g();
        }
        parsed.b = oVar;
        parsed.o(resolverStyle, resolverFields);
        return parsed;
    }

    /* access modifiers changed from: package-private */
    public Long j(B field) {
        return (Long) e().a.get(field);
    }

    /* access modifiers changed from: package-private */
    public int o(B field, long value, int errorPos, int successPos) {
        CLASSNAMEp.a(field, "field");
        Long old = (Long) e().a.put(field, Long.valueOf(value));
        return (old == null || old.longValue() == value) ? successPos : errorPos ^ -1;
    }

    /* access modifiers changed from: package-private */
    public void a(Consumer consumer) {
        if (this.e == null) {
            this.e = new ArrayList();
        }
        this.e.add(consumer);
    }

    /* access modifiers changed from: package-private */
    public void n(o zone) {
        CLASSNAMEp.a(zone, "zone");
        e().b = zone;
    }

    /* access modifiers changed from: package-private */
    public void p() {
        e().d = true;
    }

    public String toString() {
        return e().toString();
    }
}
