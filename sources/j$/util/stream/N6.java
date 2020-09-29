package j$.util.stream;

import j$.util.O;
import j$.util.P;
import j$.util.function.CLASSNAMEt;
import j$.util.function.Consumer;

final class N6 extends Q6 implements P {
    public /* synthetic */ boolean a(Consumer consumer) {
        return O.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        O.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void e(CLASSNAMEt tVar) {
        super.forEachRemaining(tVar);
    }

    public /* bridge */ /* synthetic */ boolean j(CLASSNAMEt tVar) {
        return super.tryAdvance(tVar);
    }

    public /* bridge */ /* synthetic */ P trySplit() {
        return (P) super.trySplit();
    }

    N6(P s, long sliceOrigin, long sliceFence) {
        super(s, sliceOrigin, sliceFence);
    }

    N6(P s, long sliceOrigin, long sliceFence, long origin, long fence) {
        super(s, sliceOrigin, sliceFence, origin, fence);
    }

    /* access modifiers changed from: protected */
    /* renamed from: l */
    public P b(P s, long sliceOrigin, long sliceFence, long origin, long fence) {
        return new N6(s, sliceOrigin, sliceFence, origin, fence);
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: MethodInlineVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.visitors.MethodInlineVisitor.inlineMth(MethodInlineVisitor.java:57)
        	at jadx.core.dex.visitors.MethodInlineVisitor.visit(MethodInlineVisitor.java:47)
        */
    static /* synthetic */ void k() {
        /*
            r0 = 0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.N6.k():void");
    }

    /* access modifiers changed from: protected */
    /* renamed from: h */
    public CLASSNAMEt g() {
        return CLASSNAMEz0.a;
    }
}
