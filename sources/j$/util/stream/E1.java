package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.CLASSNAMEb;
import j$.util.Collection$EL;
import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.u;
import java.util.Collection;

final class E1 implements A1 {
    private final Collection a;

    E1(Collection collection) {
        this.a = collection;
    }

    public A1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public long count() {
        return (long) this.a.size();
    }

    public void forEach(Consumer consumer) {
        Collection$EL.a(this.a, consumer);
    }

    public void i(Object[] objArr, int i) {
        for (Object obj : this.a) {
            objArr[i] = obj;
            i++;
        }
    }

    public /* synthetic */ int p() {
        return 0;
    }

    public Object[] q(m mVar) {
        Collection collection = this.a;
        return collection.toArray((Object[]) mVar.apply(collection.size()));
    }

    public /* synthetic */ A1 r(long j, long j2, m mVar) {
        return CLASSNAMEo1.q(this, j, j2, mVar);
    }

    public u spliterator() {
        Collection collection = this.a;
        return (collection instanceof CLASSNAMEb ? ((CLASSNAMEb) collection).stream() : CLASSNAMEa.i(collection)).spliterator();
    }

    public String toString() {
        return String.format("CollectionNode[%d][%s]", new Object[]{Integer.valueOf(this.a.size()), this.a});
    }
}
