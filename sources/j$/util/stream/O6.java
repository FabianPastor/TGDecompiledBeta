package j$.util.stream;

import j$.util.Q;
import j$.util.S;
import j$.util.function.B;
import j$.util.function.Consumer;

final class O6 extends Q6 implements S {
    public /* synthetic */ boolean a(Consumer consumer) {
        return Q.b(this, consumer);
    }

    public /* synthetic */ void forEachRemaining(Consumer consumer) {
        Q.a(this, consumer);
    }

    public /* bridge */ /* synthetic */ void c(B b) {
        super.forEachRemaining(b);
    }

    public /* bridge */ /* synthetic */ boolean f(B b) {
        return super.tryAdvance(b);
    }

    public /* bridge */ /* synthetic */ S trySplit() {
        return (S) super.trySplit();
    }

    O6(S s, long sliceOrigin, long sliceFence) {
        super(s, sliceOrigin, sliceFence);
    }

    O6(S s, long sliceOrigin, long sliceFence, long origin, long fence) {
        super(s, sliceOrigin, sliceFence, origin, fence);
    }

    /* access modifiers changed from: protected */
    /* renamed from: l */
    public S b(S s, long sliceOrigin, long sliceFence, long origin, long fence) {
        return new O6(s, sliceOrigin, sliceFence, origin, fence);
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
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.O6.k():void");
    }

    /* access modifiers changed from: protected */
    /* renamed from: h */
    public B g() {
        return A0.a;
    }
}
