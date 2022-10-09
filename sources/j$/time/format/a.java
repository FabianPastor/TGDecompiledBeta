package j$.time.format;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;
/* loaded from: classes2.dex */
public final class a {
    private final f a;

    static {
        p pVar = new p();
        j$.time.temporal.a aVar = j$.time.temporal.a.YEAR;
        s sVar = s.EXCEEDS_PAD;
        p l = pVar.l(aVar, 4, 10, sVar);
        l.e('-');
        j$.time.temporal.a aVar2 = j$.time.temporal.a.MONTH_OF_YEAR;
        l.k(aVar2, 2);
        l.e('-');
        j$.time.temporal.a aVar3 = j$.time.temporal.a.DAY_OF_MONTH;
        l.k(aVar3, 2);
        j$.time.chrono.c cVar = j$.time.chrono.c.a;
        a u = l.u(1, cVar);
        p pVar2 = new p();
        pVar2.p();
        pVar2.a(u);
        pVar2.h();
        pVar2.u(1, cVar);
        p pVar3 = new p();
        pVar3.p();
        pVar3.a(u);
        pVar3.o();
        pVar3.h();
        pVar3.u(1, cVar);
        p pVar4 = new p();
        j$.time.temporal.a aVar4 = j$.time.temporal.a.HOUR_OF_DAY;
        pVar4.k(aVar4, 2);
        pVar4.e(':');
        j$.time.temporal.a aVar5 = j$.time.temporal.a.MINUTE_OF_HOUR;
        pVar4.k(aVar5, 2);
        pVar4.o();
        pVar4.e(':');
        j$.time.temporal.a aVar6 = j$.time.temporal.a.SECOND_OF_MINUTE;
        pVar4.k(aVar6, 2);
        pVar4.o();
        pVar4.b(j$.time.temporal.a.NANO_OF_SECOND, 0, 9, true);
        a u2 = pVar4.u(1, null);
        p pVar5 = new p();
        pVar5.p();
        pVar5.a(u2);
        pVar5.h();
        pVar5.u(1, null);
        p pVar6 = new p();
        pVar6.p();
        pVar6.a(u2);
        pVar6.o();
        pVar6.h();
        pVar6.u(1, null);
        p pVar7 = new p();
        pVar7.p();
        pVar7.a(u);
        pVar7.e('T');
        pVar7.a(u2);
        a u3 = pVar7.u(1, cVar);
        p pVar8 = new p();
        pVar8.p();
        pVar8.a(u3);
        pVar8.h();
        a u4 = pVar8.u(1, cVar);
        p pVar9 = new p();
        pVar9.a(u4);
        pVar9.o();
        pVar9.e('[');
        pVar9.q();
        pVar9.m();
        pVar9.e(']');
        pVar9.u(1, cVar);
        p pVar10 = new p();
        pVar10.a(u3);
        pVar10.o();
        pVar10.h();
        pVar10.o();
        pVar10.e('[');
        pVar10.q();
        pVar10.m();
        pVar10.e(']');
        pVar10.u(1, cVar);
        p pVar11 = new p();
        pVar11.p();
        p l2 = pVar11.l(aVar, 4, 10, sVar);
        l2.e('-');
        l2.k(j$.time.temporal.a.DAY_OF_YEAR, 3);
        l2.o();
        l2.h();
        l2.u(1, cVar);
        p pVar12 = new p();
        pVar12.p();
        p l3 = pVar12.l(j$.time.temporal.j.c, 4, 10, sVar);
        l3.f("-W");
        l3.k(j$.time.temporal.j.b, 2);
        l3.e('-');
        j$.time.temporal.a aVar7 = j$.time.temporal.a.DAY_OF_WEEK;
        l3.k(aVar7, 1);
        l3.o();
        l3.h();
        l3.u(1, cVar);
        p pVar13 = new p();
        pVar13.p();
        pVar13.c();
        pVar13.u(1, null);
        p pVar14 = new p();
        pVar14.p();
        pVar14.k(aVar, 4);
        pVar14.k(aVar2, 2);
        pVar14.k(aVar3, 2);
        pVar14.o();
        pVar14.g("+HHMMss", "Z");
        pVar14.u(1, cVar);
        HashMap hashMap = new HashMap();
        hashMap.put(1L, "Mon");
        hashMap.put(2L, "Tue");
        hashMap.put(3L, "Wed");
        hashMap.put(4L, "Thu");
        hashMap.put(5L, "Fri");
        hashMap.put(6L, "Sat");
        hashMap.put(7L, "Sun");
        HashMap hashMap2 = new HashMap();
        hashMap2.put(1L, "Jan");
        hashMap2.put(2L, "Feb");
        hashMap2.put(3L, "Mar");
        hashMap2.put(4L, "Apr");
        hashMap2.put(5L, "May");
        hashMap2.put(6L, "Jun");
        hashMap2.put(7L, "Jul");
        hashMap2.put(8L, "Aug");
        hashMap2.put(9L, "Sep");
        hashMap2.put(10L, "Oct");
        hashMap2.put(11L, "Nov");
        hashMap2.put(12L, "Dec");
        p pVar15 = new p();
        pVar15.p();
        pVar15.r();
        pVar15.o();
        pVar15.i(aVar7, hashMap);
        pVar15.f(", ");
        pVar15.n();
        p l4 = pVar15.l(aVar3, 1, 2, s.NOT_NEGATIVE);
        l4.e(' ');
        l4.i(aVar2, hashMap2);
        l4.e(' ');
        l4.k(aVar, 4);
        l4.e(' ');
        l4.k(aVar4, 2);
        l4.e(':');
        l4.k(aVar5, 2);
        l4.o();
        l4.e(':');
        l4.k(aVar6, 2);
        l4.n();
        l4.e(' ');
        l4.g("+HHMM", "GMT");
        l4.u(2, cVar);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public a(f fVar, Locale locale, r rVar, int i, Set set, j$.time.chrono.b bVar) {
        if (fVar != null) {
            this.a = fVar;
            if (locale == null) {
                throw new NullPointerException("locale");
            }
            if (rVar == null) {
                throw new NullPointerException("decimalStyle");
            }
            if (i == 0) {
                throw new NullPointerException("resolverStyle");
            }
            return;
        }
        throw new NullPointerException("printerParser");
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public f a(boolean z) {
        return this.a.a(z);
    }

    public String toString() {
        String fVar = this.a.toString();
        return fVar.startsWith("[") ? fVar : fVar.substring(1, fVar.length() - 1);
    }
}
