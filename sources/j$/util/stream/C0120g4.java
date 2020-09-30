package j$.util.stream;

import j$.util.Spliterator;

/* renamed from: j$.util.stream.g4  reason: case insensitive filesystem */
final class CLASSNAMEg4 extends CLASSNAMEh4 implements G5 {
    private final Object[] h;

    CLASSNAMEg4(Spliterator spliterator, CLASSNAMEq4 helper, Object[] array) {
        super(spliterator, helper, array.length);
        this.h = array;
    }

    CLASSNAMEg4(CLASSNAMEg4 parent, Spliterator spliterator, long offset, long length) {
        super(parent, spliterator, offset, length, parent.h.length);
        this.h = parent.h;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: c */
    public CLASSNAMEg4 a(Spliterator spliterator, long offset, long size) {
        return new CLASSNAMEg4(this, spliterator, offset, size);
    }

    public void accept(Object value) {
        int i = this.f;
        if (i < this.g) {
            Object[] objArr = this.h;
            this.f = i + 1;
            objArr[i] = value;
            return;
        }
        throw new IndexOutOfBoundsException(Integer.toString(this.f));
    }
}
