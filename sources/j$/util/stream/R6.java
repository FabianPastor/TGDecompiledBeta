package j$.util.stream;

import j$.util.N;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import java.util.Comparator;

final class R6 extends S6 implements Spliterator {
    public /* synthetic */ Comparator getComparator() {
        N.a(this);
        throw null;
    }

    public /* synthetic */ long getExactSizeIfKnown() {
        return N.b(this);
    }

    public /* synthetic */ boolean hasCharacteristics(int i) {
        return N.c(this, i);
    }

    R6(Spliterator spliterator, long sliceOrigin, long sliceFence) {
        this(spliterator, sliceOrigin, sliceFence, 0, Math.min(spliterator.estimateSize(), sliceFence));
    }

    private R6(Spliterator spliterator, long sliceOrigin, long sliceFence, long origin, long fence) {
        super(spliterator, sliceOrigin, sliceFence, origin, fence);
    }

    /* access modifiers changed from: protected */
    public Spliterator b(Spliterator spliterator, long sliceOrigin, long sliceFence, long origin, long fence) {
        return new R6(spliterator, sliceOrigin, sliceFence, origin, fence);
    }

    public boolean a(Consumer consumer) {
        long j;
        consumer.getClass();
        if (this.a >= this.e) {
            return false;
        }
        while (true) {
            long j2 = this.a;
            j = this.d;
            if (j2 <= j) {
                break;
            }
            this.c.a(D0.a);
            this.d++;
        }
        if (j >= this.e) {
            return false;
        }
        this.d = j + 1;
        return this.c.a(consumer);
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: MethodInlineVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.visitors.MethodInlineVisitor.inlineMth(MethodInlineVisitor.java:57)
        	at jadx.core.dex.visitors.MethodInlineVisitor.visit(MethodInlineVisitor.java:47)
        */
    static /* synthetic */ void h() {
        /*
            r0 = 0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.R6.h():void");
    }

    public void forEachRemaining(Consumer consumer) {
        consumer.getClass();
        long j = this.a;
        long j2 = this.e;
        if (j < j2) {
            long j3 = this.d;
            if (j3 < j2) {
                if (j3 < j || j3 + this.c.estimateSize() > this.b) {
                    while (this.a > this.d) {
                        this.c.a(C0.a);
                        this.d++;
                    }
                    while (this.d < this.e) {
                        this.c.a(consumer);
                        this.d++;
                    }
                    return;
                }
                this.c.forEachRemaining(consumer);
                this.d = this.e;
            }
        }
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: MethodInlineVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.visitors.MethodInlineVisitor.inlineMth(MethodInlineVisitor.java:57)
        	at jadx.core.dex.visitors.MethodInlineVisitor.visit(MethodInlineVisitor.java:47)
        */
    static /* synthetic */ void g() {
        /*
            r0 = 0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.R6.g():void");
    }
}
