package j$.time.format;

import j$.time.i;
import j$.time.o;
import j$.time.p;
import j$.time.u.C;
import j$.time.u.j;
import j$.time.u.w;
import j$.util.concurrent.ConcurrentHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

final class y extends x {
    private static final Map i = new ConcurrentHashMap();
    private final K e;
    private Set f;
    private final Map g = new HashMap();
    private final Map h = new HashMap();

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    y(j$.time.format.K r5, java.util.Set r6) {
        /*
            r4 = this;
            j$.time.u.D r0 = j$.time.u.C.m()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "ZoneText("
            r1.append(r2)
            r1.append(r5)
            java.lang.String r2 = ")"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r4.<init>(r0, r1)
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r4.g = r0
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            r4.h = r0
            java.lang.String r0 = "textStyle"
            j$.CLASSNAMEp.a(r5, r0)
            r0 = r5
            j$.time.format.K r0 = (j$.time.format.K) r0
            r4.e = r0
            if (r6 == 0) goto L_0x005e
            int r0 = r6.size()
            if (r0 == 0) goto L_0x005e
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            r4.f = r0
            java.util.Iterator r0 = r6.iterator()
        L_0x0048:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x005e
            java.lang.Object r1 = r0.next()
            j$.time.o r1 = (j$.time.o) r1
            java.util.Set r2 = r4.f
            java.lang.String r3 = r1.getId()
            r2.add(r3)
            goto L_0x0048
        L_0x005e:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.format.y.<init>(j$.time.format.K, java.util.Set):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:8:0x0026, code lost:
        if (r5 == null) goto L_0x0028;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String c(java.lang.String r11, int r12, java.util.Locale r13) {
        /*
            r10 = this;
            j$.time.format.K r0 = r10.e
            j$.time.format.K r1 = j$.time.format.K.NARROW
            if (r0 != r1) goto L_0x0008
            r0 = 0
            return r0
        L_0x0008:
            java.util.Map r0 = i
            java.lang.Object r0 = r0.get(r11)
            java.lang.ref.SoftReference r0 = (java.lang.ref.SoftReference) r0
            r1 = 0
            r2 = 5
            r3 = 3
            r4 = 1
            if (r0 == 0) goto L_0x0028
            java.lang.Object r5 = r0.get()
            java.util.Map r5 = (java.util.Map) r5
            r1 = r5
            if (r5 == 0) goto L_0x0028
            java.lang.Object r5 = r1.get(r13)
            java.lang.String[] r5 = (java.lang.String[]) r5
            r6 = r5
            if (r5 != 0) goto L_0x0066
        L_0x0028:
            java.util.TimeZone r5 = java.util.TimeZone.getTimeZone(r11)
            r6 = 7
            java.lang.String[] r6 = new java.lang.String[r6]
            r7 = 0
            r6[r7] = r11
            java.lang.String r8 = r5.getDisplayName(r7, r4, r13)
            r6[r4] = r8
            java.lang.String r8 = r5.getDisplayName(r7, r7, r13)
            r9 = 2
            r6[r9] = r8
            java.lang.String r8 = r5.getDisplayName(r4, r4, r13)
            r6[r3] = r8
            java.lang.String r7 = r5.getDisplayName(r4, r7, r13)
            r8 = 4
            r6[r8] = r7
            r6[r2] = r11
            r7 = 6
            r6[r7] = r11
            if (r1 != 0) goto L_0x0059
            j$.util.concurrent.ConcurrentHashMap r7 = new j$.util.concurrent.ConcurrentHashMap
            r7.<init>()
            r1 = r7
        L_0x0059:
            r1.put(r13, r6)
            java.util.Map r7 = i
            java.lang.ref.SoftReference r8 = new java.lang.ref.SoftReference
            r8.<init>(r1)
            r7.put(r11, r8)
        L_0x0066:
            if (r12 == 0) goto L_0x007e
            if (r12 == r4) goto L_0x0074
            j$.time.format.K r3 = r10.e
            int r3 = r3.i()
            int r3 = r3 + r2
            r2 = r6[r3]
            return r2
        L_0x0074:
            j$.time.format.K r2 = r10.e
            int r2 = r2.i()
            int r2 = r2 + r3
            r2 = r6[r2]
            return r2
        L_0x007e:
            j$.time.format.K r2 = r10.e
            int r2 = r2.i()
            int r2 = r2 + r4
            r2 = r6[r2]
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.format.y.c(java.lang.String, int, java.util.Locale):java.lang.String");
    }

    public boolean i(C context, StringBuilder buf) {
        o zone = (o) context.g(C.n());
        int i2 = 0;
        if (zone == null) {
            return false;
        }
        String zname = zone.getId();
        if (!(zone instanceof p)) {
            w dt = context.e();
            if (!dt.h(j.INSTANT_SECONDS)) {
                i2 = 2;
            } else if (zone.A().i(i.L(dt))) {
                i2 = 1;
            }
            String name = c(zname, i2, context.d());
            if (name != null) {
                zname = name;
            }
        }
        buf.append(zname);
        return true;
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004c, code lost:
        if (r9 == null) goto L_0x004e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public j$.time.format.r a(j$.time.format.A r18) {
        /*
            r17 = this;
            r0 = r17
            j$.time.format.K r1 = r0.e
            j$.time.format.K r2 = j$.time.format.K.NARROW
            if (r1 != r2) goto L_0x000d
            j$.time.format.r r1 = super.a(r18)
            return r1
        L_0x000d:
            java.util.Locale r1 = r18.i()
            boolean r2 = r18.k()
            java.util.Set r3 = j$.time.v.g.a()
            r4 = r3
            java.util.HashSet r4 = (java.util.HashSet) r4
            int r4 = r4.size()
            if (r2 == 0) goto L_0x0025
            java.util.Map r5 = r0.g
            goto L_0x0027
        L_0x0025:
            java.util.Map r5 = r0.h
        L_0x0027:
            r6 = 0
            r7 = 0
            r8 = 0
            java.lang.Object r9 = r5.get(r1)
            java.util.Map$Entry r9 = (java.util.Map.Entry) r9
            r6 = r9
            if (r9 == 0) goto L_0x004e
            java.lang.Object r9 = r6.getKey()
            java.lang.Integer r9 = (java.lang.Integer) r9
            int r9 = r9.intValue()
            if (r9 != r4) goto L_0x004e
            java.lang.Object r9 = r6.getValue()
            java.lang.ref.SoftReference r9 = (java.lang.ref.SoftReference) r9
            java.lang.Object r9 = r9.get()
            j$.time.format.r r9 = (j$.time.format.r) r9
            r7 = r9
            if (r9 != 0) goto L_0x00d5
        L_0x004e:
            j$.time.format.r r7 = j$.time.format.r.f(r18)
            java.text.DateFormatSymbols r9 = java.text.DateFormatSymbols.getInstance(r1)
            java.lang.String[][] r8 = r9.getZoneStrings()
            int r9 = r8.length
            r10 = 0
            r11 = 0
        L_0x005d:
            if (r11 >= r9) goto L_0x008f
            r14 = r8[r11]
            r15 = r14[r10]
            r12 = r3
            java.util.HashSet r12 = (java.util.HashSet) r12
            boolean r12 = r12.contains(r15)
            if (r12 != 0) goto L_0x006d
            goto L_0x008c
        L_0x006d:
            r7.a(r15, r15)
            java.lang.String r12 = j$.time.format.L.b(r15, r1)
            j$.time.format.K r15 = r0.e
            j$.time.format.K r13 = j$.time.format.K.FULL
            if (r15 != r13) goto L_0x007d
            r16 = 1
            goto L_0x007f
        L_0x007d:
            r16 = 2
        L_0x007f:
            r13 = r16
        L_0x0081:
            int r15 = r14.length
            if (r13 >= r15) goto L_0x008c
            r15 = r14[r13]
            r7.a(r15, r12)
            int r13 = r13 + 2
            goto L_0x0081
        L_0x008c:
            int r11 = r11 + 1
            goto L_0x005d
        L_0x008f:
            java.util.Set r9 = r0.f
            if (r9 == 0) goto L_0x00c4
            int r9 = r8.length
            r11 = 0
        L_0x0095:
            if (r11 >= r9) goto L_0x00c4
            r12 = r8[r11]
            r13 = r12[r10]
            java.util.Set r14 = r0.f
            boolean r14 = r14.contains(r13)
            if (r14 == 0) goto L_0x00c1
            r14 = r3
            java.util.HashSet r14 = (java.util.HashSet) r14
            boolean r14 = r14.contains(r13)
            if (r14 != 0) goto L_0x00ad
            goto L_0x00c1
        L_0x00ad:
            j$.time.format.K r14 = r0.e
            j$.time.format.K r15 = j$.time.format.K.FULL
            if (r14 != r15) goto L_0x00b5
            r14 = 1
            goto L_0x00b6
        L_0x00b5:
            r14 = 2
        L_0x00b6:
            int r15 = r12.length
            if (r14 >= r15) goto L_0x00c1
            r15 = r12[r14]
            r7.a(r15, r13)
            int r14 = r14 + 2
            goto L_0x00b6
        L_0x00c1:
            int r11 = r11 + 1
            goto L_0x0095
        L_0x00c4:
            java.util.AbstractMap$SimpleImmutableEntry r9 = new java.util.AbstractMap$SimpleImmutableEntry
            java.lang.Integer r10 = java.lang.Integer.valueOf(r4)
            java.lang.ref.SoftReference r11 = new java.lang.ref.SoftReference
            r11.<init>(r7)
            r9.<init>(r10, r11)
            r5.put(r1, r9)
        L_0x00d5:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.format.y.a(j$.time.format.A):j$.time.format.r");
    }
}
