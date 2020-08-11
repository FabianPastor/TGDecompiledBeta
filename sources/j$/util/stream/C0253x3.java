package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Spliterator;
import j$.util.function.C;
import j$.util.function.Consumer;
import java.util.Collection;

/* renamed from: j$.util.stream.x3  reason: case insensitive filesystem */
final class CLASSNAMEx3 implements CLASSNAMEt3 {
    private final Collection a;

    public /* synthetic */ CLASSNAMEt3 c(long j, long j2, C c) {
        return CLASSNAMEg3.d(this, j, j2, c);
    }

    public /* synthetic */ CLASSNAMEt3 d(int i) {
        CLASSNAMEg3.a(this);
        throw null;
    }

    public /* synthetic */ int w() {
        CLASSNAMEg3.b();
        return 0;
    }

    CLASSNAMEx3(Collection c) {
        this.a = c;
    }

    public Spliterator spliterator() {
        return CLASSNAMEk.d(this.a).spliterator();
    }

    public void m(Object[] array, int offset) {
        for (T t : this.a) {
            array[offset] = t;
            offset++;
        }
    }

    public Object[] x(C c) {
        Collection collection = this.a;
        return collection.toArray((Object[]) c.a(collection.size()));
    }

    public long count() {
        return (long) this.a.size();
    }

    public void forEach(Consumer consumer) {
        CLASSNAMEk.a(this.a, consumer);
    }

    public String toString() {
        return String.format("CollectionNode[%d][%s]", new Object[]{Integer.valueOf(this.a.size()), this.a});
    }
}
