package j$.util.stream;

import j$.util.CLASSNAMEa;
import j$.util.CLASSNAMEb;
import j$.util.function.Consumer;
import j$.util.function.m;
import j$.util.y;
import java.util.Collection;

final class F1 implements B1 {
    private final Collection a;

    F1(Collection collection) {
        this.a = collection;
    }

    public B1 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public long count() {
        return (long) this.a.size();
    }

    public void forEach(Consumer consumer) {
        CLASSNAMEa.y(this.a, consumer);
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

    public /* synthetic */ B1 r(long j, long j2, m mVar) {
        return CLASSNAMEp1.q(this, j, j2, mVar);
    }

    public y spliterator() {
        Collection collection = this.a;
        return (collection instanceof CLASSNAMEb ? ((CLASSNAMEb) collection).stream() : CLASSNAMEa.i(collection)).spliterator();
    }

    public String toString() {
        return String.format("CollectionNode[%d][%s]", new Object[]{Integer.valueOf(this.a.size()), this.a});
    }
}
