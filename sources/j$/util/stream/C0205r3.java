package j$.util.stream;

import j$.util.function.C;

/* renamed from: j$.util.stream.r3  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEr3 {
    public static CLASSNAMEs3 b(CLASSNAMEs3 _this) {
        throw new IndexOutOfBoundsException();
    }

    public static Object[] a(CLASSNAMEs3 _this, C c) {
        if (h7.a) {
            h7.b(_this.getClass(), "{0} calling Node.OfPrimitive.asArray");
            throw null;
        } else if (_this.count() < NUM) {
            T[] boxed = (Object[]) c.a((int) _this.count());
            _this.m(boxed, 0);
            return boxed;
        } else {
            throw new IllegalArgumentException("Stream size exceeds max array size");
        }
    }
}
