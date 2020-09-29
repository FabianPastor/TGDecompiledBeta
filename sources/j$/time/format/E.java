package j$.time.format;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

final class E {
    private final Map a;
    private final Map b;

    /* JADX WARNING: Removed duplicated region for block: B:6:0x003c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    E(java.util.Map r11) {
        /*
            r10 = this;
            r10.<init>()
            r10.a = r11
            java.util.HashMap r0 = new java.util.HashMap
            r0.<init>()
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            java.util.Set r2 = r11.entrySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x0017:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x0081
            java.lang.Object r3 = r2.next()
            java.util.Map$Entry r3 = (java.util.Map.Entry) r3
            java.util.HashMap r4 = new java.util.HashMap
            r4.<init>()
            java.lang.Object r5 = r3.getValue()
            java.util.Map r5 = (java.util.Map) r5
            java.util.Set r5 = r5.entrySet()
            java.util.Iterator r5 = r5.iterator()
        L_0x0036:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x0060
            java.lang.Object r6 = r5.next()
            java.util.Map$Entry r6 = (java.util.Map.Entry) r6
            java.lang.Object r7 = r6.getValue()
            java.lang.String r7 = (java.lang.String) r7
            java.lang.Object r8 = r6.getValue()
            java.lang.String r8 = (java.lang.String) r8
            java.lang.Object r9 = r6.getKey()
            java.lang.Long r9 = (java.lang.Long) r9
            java.util.Map$Entry r8 = j$.time.format.F.c(r8, r9)
            java.lang.Object r7 = r4.put(r7, r8)
            if (r7 == 0) goto L_0x005f
            goto L_0x0036
        L_0x005f:
            goto L_0x0036
        L_0x0060:
            java.util.ArrayList r5 = new java.util.ArrayList
            java.util.Collection r6 = r4.values()
            r5.<init>(r6)
            java.util.Comparator r6 = j$.time.format.F.b
            java.util.Collections.sort(r5, r6)
            java.lang.Object r6 = r3.getKey()
            j$.time.format.K r6 = (j$.time.format.K) r6
            r0.put(r6, r5)
            r1.addAll(r5)
            r6 = 0
            r0.put(r6, r1)
            goto L_0x0017
        L_0x0081:
            java.util.Comparator r2 = j$.time.format.F.b
            java.util.Collections.sort(r1, r2)
            r10.b = r0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.time.format.E.<init>(java.util.Map):void");
    }

    /* access modifiers changed from: package-private */
    public String a(long value, K style) {
        Map<Long, String> map = (Map) this.a.get(style);
        if (map != null) {
            return map.get(Long.valueOf(value));
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public Iterator b(K style) {
        List<Map.Entry<String, Long>> list = (List) this.b.get(style);
        if (list != null) {
            return list.iterator();
        }
        return null;
    }
}
