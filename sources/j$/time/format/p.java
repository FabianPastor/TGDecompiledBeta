package j$.time.format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
/* loaded from: classes2.dex */
public final class p {
    private p a;
    private final p b;
    private final List c;
    private final boolean d;
    private int e;

    static {
        HashMap hashMap = new HashMap();
        hashMap.put('G', j$.time.temporal.a.ERA);
        hashMap.put('y', j$.time.temporal.a.YEAR_OF_ERA);
        hashMap.put('u', j$.time.temporal.a.YEAR);
        j$.time.temporal.k kVar = j$.time.temporal.j.a;
        hashMap.put('Q', kVar);
        hashMap.put('q', kVar);
        j$.time.temporal.a aVar = j$.time.temporal.a.MONTH_OF_YEAR;
        hashMap.put('M', aVar);
        hashMap.put('L', aVar);
        hashMap.put('D', j$.time.temporal.a.DAY_OF_YEAR);
        hashMap.put('d', j$.time.temporal.a.DAY_OF_MONTH);
        hashMap.put('F', j$.time.temporal.a.ALIGNED_DAY_OF_WEEK_IN_MONTH);
        j$.time.temporal.a aVar2 = j$.time.temporal.a.DAY_OF_WEEK;
        hashMap.put('E', aVar2);
        hashMap.put('c', aVar2);
        hashMap.put('e', aVar2);
        hashMap.put('a', j$.time.temporal.a.AMPM_OF_DAY);
        hashMap.put('H', j$.time.temporal.a.HOUR_OF_DAY);
        hashMap.put('k', j$.time.temporal.a.CLOCK_HOUR_OF_DAY);
        hashMap.put('K', j$.time.temporal.a.HOUR_OF_AMPM);
        hashMap.put('h', j$.time.temporal.a.CLOCK_HOUR_OF_AMPM);
        hashMap.put('m', j$.time.temporal.a.MINUTE_OF_HOUR);
        hashMap.put('s', j$.time.temporal.a.SECOND_OF_MINUTE);
        j$.time.temporal.a aVar3 = j$.time.temporal.a.NANO_OF_SECOND;
        hashMap.put('S', aVar3);
        hashMap.put('A', j$.time.temporal.a.MILLI_OF_DAY);
        hashMap.put('n', aVar3);
        hashMap.put('N', j$.time.temporal.a.NANO_OF_DAY);
    }

    public p() {
        this.a = this;
        this.c = new ArrayList();
        this.e = -1;
        this.b = null;
        this.d = false;
    }

    private p(p pVar, boolean z) {
        this.a = this;
        this.c = new ArrayList();
        this.e = -1;
        this.b = pVar;
        this.d = z;
    }

    private int d(g gVar) {
        if (gVar != null) {
            p pVar = this.a;
            pVar.getClass();
            pVar.c.add(gVar);
            p pVar2 = this.a;
            pVar2.e = -1;
            return pVar2.c.size() - 1;
        }
        throw new NullPointerException("pp");
    }

    private p j(j jVar) {
        j b;
        s sVar;
        p pVar = this.a;
        int i = pVar.e;
        if (i >= 0) {
            j jVar2 = (j) pVar.c.get(i);
            if (jVar.b == jVar.c) {
                sVar = jVar.d;
                if (sVar == s.NOT_NEGATIVE) {
                    b = jVar2.c(jVar.c);
                    d(jVar.b());
                    this.a.e = i;
                    this.a.c.set(i, b);
                }
            }
            b = jVar2.b();
            this.a.e = d(jVar);
            this.a.c.set(i, b);
        } else {
            pVar.e = d(jVar);
        }
        return this;
    }

    private a t(Locale locale, int i, j$.time.chrono.b bVar) {
        if (locale != null) {
            while (this.a.b != null) {
                n();
            }
            return new a(new f(this.c, false), locale, r.a, i, null, bVar);
        }
        throw new NullPointerException("locale");
    }

    public p a(a aVar) {
        if (aVar != null) {
            d(aVar.a(false));
            return this;
        }
        throw new NullPointerException("formatter");
    }

    public p b(j$.time.temporal.k kVar, int i, int i2, boolean z) {
        d(new h(kVar, i, i2, z));
        return this;
    }

    public p c() {
        d(new i(-2));
        return this;
    }

    public p e(char c) {
        d(new e(c));
        return this;
    }

    public p f(String str) {
        if (str.length() > 0) {
            d(str.length() == 1 ? new e(str.charAt(0)) : new m(str));
        }
        return this;
    }

    public p g(String str, String str2) {
        d(new k(str, str2));
        return this;
    }

    public p h() {
        d(k.d);
        return this;
    }

    public p i(j$.time.temporal.k kVar, Map map) {
        if (kVar != null) {
            LinkedHashMap linkedHashMap = new LinkedHashMap(map);
            t tVar = t.FULL;
            d(new n(kVar, tVar, new c(this, new q(Collections.singletonMap(tVar, linkedHashMap)))));
            return this;
        }
        throw new NullPointerException("field");
    }

    public p k(j$.time.temporal.k kVar, int i) {
        if (kVar != null) {
            if (i >= 1 && i <= 19) {
                j(new j(kVar, i, i, s.NOT_NEGATIVE));
                return this;
            }
            throw new IllegalArgumentException("The width must be from 1 to 19 inclusive but was " + i);
        }
        throw new NullPointerException("field");
    }

    public p l(j$.time.temporal.k kVar, int i, int i2, s sVar) {
        if (i == i2 && sVar == s.NOT_NEGATIVE) {
            k(kVar, i2);
            return this;
        } else if (kVar == null) {
            throw new NullPointerException("field");
        } else {
            if (sVar == null) {
                throw new NullPointerException("signStyle");
            }
            if (i < 1 || i > 19) {
                throw new IllegalArgumentException("The minimum width must be from 1 to 19 inclusive but was " + i);
            } else if (i2 < 1 || i2 > 19) {
                throw new IllegalArgumentException("The maximum width must be from 1 to 19 inclusive but was " + i2);
            } else if (i2 >= i) {
                j(new j(kVar, i, i2, sVar));
                return this;
            } else {
                throw new IllegalArgumentException("The maximum width must exceed or equal the minimum width but " + i2 + " < " + i);
            }
        }
    }

    public p m() {
        d(new o(b.a, "ZoneRegionId()"));
        return this;
    }

    public p n() {
        p pVar = this.a;
        if (pVar.b != null) {
            if (pVar.c.size() > 0) {
                p pVar2 = this.a;
                f fVar = new f(pVar2.c, pVar2.d);
                this.a = this.a.b;
                d(fVar);
            } else {
                this.a = this.a.b;
            }
            return this;
        }
        throw new IllegalStateException("Cannot call optionalEnd() as there was no previous call to optionalStart()");
    }

    public p o() {
        p pVar = this.a;
        pVar.e = -1;
        this.a = new p(pVar, true);
        return this;
    }

    public p p() {
        d(l.INSENSITIVE);
        return this;
    }

    public p q() {
        d(l.SENSITIVE);
        return this;
    }

    public p r() {
        d(l.LENIENT);
        return this;
    }

    public a s() {
        return t(Locale.getDefault(), 2, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public a u(int i, j$.time.chrono.b bVar) {
        return t(Locale.getDefault(), i, bVar);
    }
}
