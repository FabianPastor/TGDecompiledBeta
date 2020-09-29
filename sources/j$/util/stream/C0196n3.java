package j$.util.stream;

import j$.util.S;
import j$.util.function.B;
import j$.util.function.Consumer;

/* renamed from: j$.util.stream.n3  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEn3 {
    public static void c(CLASSNAMEo3 _this, Consumer consumer) {
        if (consumer instanceof B) {
            _this.j((B) consumer);
        } else if (!h7.a) {
            ((S) _this.spliterator()).forEachRemaining(consumer);
        } else {
            h7.b(_this.getClass(), "{0} calling Node.OfInt.forEachRemaining(Consumer)");
            throw null;
        }
    }

    public static void a(CLASSNAMEo3 _this, Integer[] boxed, int offset) {
        if (!h7.a) {
            int[] array = (int[]) _this.i();
            for (int i = 0; i < array.length; i++) {
                boxed[offset + i] = Integer.valueOf(array[i]);
            }
            return;
        }
        h7.b(_this.getClass(), "{0} calling Node.OfInt.copyInto(Integer[], int)");
        throw null;
    }

    public static CLASSNAMEo3 g(CLASSNAMEo3 _this, long from, long to) {
        if (from == 0 && to == _this.count()) {
            return _this;
        }
        long size = to - from;
        S spliterator = (S) _this.spliterator();
        CLASSNAMEi3 nodeBuilder = CLASSNAMEp4.s(size);
        nodeBuilder.s(size);
        for (int i = 0; ((long) i) < from && spliterator.f(CLASSNAMEi0.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < size && spliterator.f(nodeBuilder); i2++) {
        }
        nodeBuilder.r();
        return nodeBuilder.b();
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: MethodInlineVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:659)
        	at java.util.ArrayList.get(ArrayList.java:435)
        	at jadx.core.dex.visitors.MethodInlineVisitor.inlineMth(MethodInlineVisitor.java:57)
        	at jadx.core.dex.visitors.MethodInlineVisitor.visit(MethodInlineVisitor.java:47)
        */
    public static /* synthetic */ void i() {
        /*
            r0 = 0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEn3.i():void");
    }

    public static int[] f(CLASSNAMEo3 _this, int count) {
        return new int[count];
    }

    public static CLASSNAMEv6 d(CLASSNAMEo3 _this) {
        return CLASSNAMEv6.INT_VALUE;
    }
}
