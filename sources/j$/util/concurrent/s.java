package j$.util.concurrent;

final class s extends m {
    s e;
    s f;
    s g;
    s h;
    boolean i;

    s(int hash, Object key, Object val, m next, s parent) {
        super(hash, key, val, next);
        this.e = parent;
    }

    /* access modifiers changed from: package-private */
    public m a(int h2, Object k) {
        return b(h2, k, (Class) null);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x002f, code lost:
        if (r3 != null) goto L_0x0031;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final j$.util.concurrent.s b(int r8, java.lang.Object r9, java.lang.Class<?> r10) {
        /*
            r7 = this;
            if (r9 == 0) goto L_0x004c
            r0 = r7
        L_0x0003:
            j$.util.concurrent.s r1 = r0.f
            j$.util.concurrent.s r2 = r0.g
            int r3 = r0.a
            r4 = r3
            if (r3 <= r8) goto L_0x000e
            r0 = r1
            goto L_0x0048
        L_0x000e:
            if (r4 >= r8) goto L_0x0012
            r0 = r2
            goto L_0x0048
        L_0x0012:
            java.lang.Object r3 = r0.b
            r5 = r3
            if (r3 == r9) goto L_0x004b
            if (r5 == 0) goto L_0x0020
            boolean r3 = r9.equals(r5)
            if (r3 == 0) goto L_0x0020
            goto L_0x004b
        L_0x0020:
            if (r1 != 0) goto L_0x0024
            r0 = r2
            goto L_0x0048
        L_0x0024:
            if (r2 != 0) goto L_0x0028
            r0 = r1
            goto L_0x0048
        L_0x0028:
            if (r10 != 0) goto L_0x0031
            java.lang.Class r3 = j$.util.concurrent.ConcurrentHashMap.c(r9)
            r10 = r3
            if (r3 == 0) goto L_0x003f
        L_0x0031:
            int r3 = j$.util.concurrent.ConcurrentHashMap.d(r10, r9, r5)
            r6 = r3
            if (r3 == 0) goto L_0x003f
            if (r6 >= 0) goto L_0x003c
            r3 = r1
            goto L_0x003d
        L_0x003c:
            r3 = r2
        L_0x003d:
            r0 = r3
            goto L_0x0048
        L_0x003f:
            j$.util.concurrent.s r3 = r2.b(r8, r9, r10)
            r6 = r3
            if (r3 == 0) goto L_0x0047
            return r6
        L_0x0047:
            r0 = r1
        L_0x0048:
            if (r0 != 0) goto L_0x0003
            goto L_0x004c
        L_0x004b:
            return r0
        L_0x004c:
            r0 = 0
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.s.b(int, java.lang.Object, java.lang.Class):j$.util.concurrent.s");
    }
}
