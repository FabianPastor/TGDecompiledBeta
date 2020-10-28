package j$.util.concurrent;

final class h extends m {
    final m[] e;

    h(m[] mVarArr) {
        super(-1, (Object) null, (Object) null, (m) null);
        this.e = mVarArr;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0027, code lost:
        if ((r0 instanceof j$.util.concurrent.h) == false) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0029, code lost:
        r0 = ((j$.util.concurrent.h) r0).e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0032, code lost:
        return r0.a(r5, r6);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public j$.util.concurrent.m a(int r5, java.lang.Object r6) {
        /*
            r4 = this;
            j$.util.concurrent.m[] r0 = r4.e
        L_0x0002:
            r1 = 0
            if (r0 == 0) goto L_0x0037
            int r2 = r0.length
            if (r2 == 0) goto L_0x0037
            int r2 = r2 + -1
            r2 = r2 & r5
            j$.util.concurrent.m r0 = j$.util.concurrent.ConcurrentHashMap.n(r0, r2)
            if (r0 != 0) goto L_0x0012
            goto L_0x0037
        L_0x0012:
            int r2 = r0.a
            if (r2 != r5) goto L_0x0023
            java.lang.Object r3 = r0.b
            if (r3 == r6) goto L_0x0022
            if (r3 == 0) goto L_0x0023
            boolean r3 = r6.equals(r3)
            if (r3 == 0) goto L_0x0023
        L_0x0022:
            return r0
        L_0x0023:
            if (r2 >= 0) goto L_0x0033
            boolean r1 = r0 instanceof j$.util.concurrent.h
            if (r1 == 0) goto L_0x002e
            j$.util.concurrent.h r0 = (j$.util.concurrent.h) r0
            j$.util.concurrent.m[] r0 = r0.e
            goto L_0x0002
        L_0x002e:
            j$.util.concurrent.m r5 = r0.a(r5, r6)
            return r5
        L_0x0033:
            j$.util.concurrent.m r0 = r0.d
            if (r0 != 0) goto L_0x0012
        L_0x0037:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.h.a(int, java.lang.Object):j$.util.concurrent.m");
    }
}
