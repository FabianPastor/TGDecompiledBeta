package j$.util.stream;

import j$.util.Spliterator;
import j$.util.function.C;
import java.util.stream.Node;

/* renamed from: j$.util.stream.g3  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEg3 {
    public static int b() {
        return 0;
    }

    public static CLASSNAMEt3 a(CLASSNAMEt3 _this) {
        throw new IndexOutOfBoundsException();
    }

    public static CLASSNAMEt3 d(CLASSNAMEt3 _this, long from, long to, C c) {
        if (from == 0 && to == _this.count()) {
            return _this;
        }
        Spliterator spliterator = _this.spliterator();
        long size = to - from;
        Node.Builder<T> nodeBuilder = CLASSNAMEp4.e(size, c);
        nodeBuilder.s(size);
        for (int i = 0; ((long) i) < from && spliterator.a(CLASSNAMEk0.a); i++) {
        }
        for (int i2 = 0; ((long) i2) < size && spliterator.a(nodeBuilder); i2++) {
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
    public static /* synthetic */ void e() {
        /*
            r0 = 0
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: j$.util.stream.CLASSNAMEg3.e():void");
    }

    public static CLASSNAMEv6 c(CLASSNAMEt3 _this) {
        return CLASSNAMEv6.REFERENCE;
    }
}
