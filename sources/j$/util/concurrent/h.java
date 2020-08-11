package j$.util.concurrent;

final class h extends m {
    final m[] e;

    h(m[] tab) {
        super(-1, (Object) null, (Object) null, (m) null);
        this.e = tab;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002d, code lost:
        if ((r4 instanceof j$.util.concurrent.h) == false) goto L_0x0035;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x002f, code lost:
        r0 = ((j$.util.concurrent.h) r4).e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0039, code lost:
        return r4.a(r8, r9);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public j$.util.concurrent.m a(int r8, java.lang.Object r9) {
        /*
            r7 = this;
            j$.util.concurrent.m[] r0 = r7.e
        L_0x0002:
            r1 = 0
            if (r9 == 0) goto L_0x0041
            if (r0 == 0) goto L_0x0041
            int r2 = r0.length
            r3 = r2
            if (r2 == 0) goto L_0x0041
            int r2 = r3 + -1
            r2 = r2 & r8
            j$.util.concurrent.m r2 = j$.util.concurrent.ConcurrentHashMap.o(r0, r2)
            r4 = r2
            if (r2 != 0) goto L_0x0016
            goto L_0x0041
        L_0x0016:
            int r2 = r4.a
            r5 = r2
            if (r2 != r8) goto L_0x0029
            java.lang.Object r2 = r4.b
            r6 = r2
            if (r2 == r9) goto L_0x0028
            if (r6 == 0) goto L_0x0029
            boolean r2 = r9.equals(r6)
            if (r2 == 0) goto L_0x0029
        L_0x0028:
            return r4
        L_0x0029:
            if (r5 >= 0) goto L_0x003a
            boolean r1 = r4 instanceof j$.util.concurrent.h
            if (r1 == 0) goto L_0x0035
            r1 = r4
            j$.util.concurrent.h r1 = (j$.util.concurrent.h) r1
            j$.util.concurrent.m[] r0 = r1.e
            goto L_0x0002
        L_0x0035:
            j$.util.concurrent.m r1 = r4.a(r8, r9)
            return r1
        L_0x003a:
            j$.util.concurrent.m r2 = r4.d
            r4 = r2
            if (r2 != 0) goto L_0x0040
            return r1
        L_0x0040:
            goto L_0x0016
        L_0x0041:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.h.a(int, java.lang.Object):j$.util.concurrent.m");
    }
}
