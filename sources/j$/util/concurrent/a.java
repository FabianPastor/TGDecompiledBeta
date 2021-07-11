package j$.util.concurrent;

import j$.util.function.BiConsumer;
import j$.util.function.BiFunction;
import j$.util.function.CLASSNAMEa;
import java.util.concurrent.ConcurrentMap;

public final /* synthetic */ class a implements BiConsumer {
    public final /* synthetic */ ConcurrentMap a;
    public final /* synthetic */ BiFunction b;

    public /* synthetic */ a(ConcurrentMap concurrentMap, BiFunction biFunction) {
        this.a = concurrentMap;
        this.b = biFunction;
    }

    public BiConsumer a(BiConsumer biConsumer) {
        biConsumer.getClass();
        return new CLASSNAMEa(this, biConsumer);
    }

    /* JADX WARNING: Removed duplicated region for block: B:1:0x0004 A[LOOP:0: B:1:0x0004->B:4:0x0012, LOOP_START, PHI: r5 
      PHI: (r5v1 java.lang.Object) = (r5v0 java.lang.Object), (r5v3 java.lang.Object) binds: [B:0:0x0000, B:4:0x0012] A[DONT_GENERATE, DONT_INLINE]] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void accept(java.lang.Object r4, java.lang.Object r5) {
        /*
            r3 = this;
            java.util.concurrent.ConcurrentMap r0 = r3.a
            j$.util.function.BiFunction r1 = r3.b
        L_0x0004:
            java.lang.Object r2 = r1.apply(r4, r5)
            boolean r5 = r0.replace(r4, r5, r2)
            if (r5 != 0) goto L_0x0014
            java.lang.Object r5 = r0.get(r4)
            if (r5 != 0) goto L_0x0004
        L_0x0014:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.concurrent.a.accept(java.lang.Object, java.lang.Object):void");
    }
}
