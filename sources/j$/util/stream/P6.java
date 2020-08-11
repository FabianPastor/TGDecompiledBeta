package j$.util.stream;

import j$.util.T;
import j$.util.U;
import j$.util.function.Consumer;
import j$.util.function.J;

final class P6 extends Q6 implements U {
    public /* synthetic */ boolean a(Consumer consumer) {
        return T.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        T.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void d(J j) {
        super.forEachRemaining(j);
    }

    public /* bridge */ /* synthetic */ boolean i(J j) {
        return super.tryAdvance(j);
    }

    public /* bridge */ /* synthetic */ U trySplit() {
        return (U) super.trySplit();
    }

    P6(U s, long sliceOrigin, long sliceFence) {
        super(s, sliceOrigin, sliceFence);
    }

    P6(U s, long sliceOrigin, long sliceFence, long origin, long fence) {
        super(s, sliceOrigin, sliceFence, origin, fence);
    }

    /* access modifiers changed from: protected */
    /* renamed from: l */
    public U b(U s, long sliceOrigin, long sliceFence, long origin, long fence) {
        return new P6(s, sliceOrigin, sliceFence, origin, fence);
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: MethodInlineVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.visitors.MethodInlineVisitor.inlineMth(MethodInlineVisitor.java:57)
        	at jadx.core.dex.visitors.MethodInlineVisitor.visit(MethodInlineVisitor.java:47)
        */
    static /* synthetic */ void k() {
        /*
            r0 = 0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.P6.k():void");
    }

    /* access modifiers changed from: protected */
    /* renamed from: h */
    public J g() {
        return B0.a;
    }
}
