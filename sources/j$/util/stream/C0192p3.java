package j$.util.stream;

import j$.util.CLASSNAMEk;
import j$.util.Collection;
import j$.util.Spliterator;
import j$.util.function.Consumer;
import j$.util.function.v;
import java.util.Collection;

/* renamed from: j$.util.stream.p3  reason: case insensitive filesystem */
final class CLASSNAMEp3 implements CLASSNAMEl3 {
    private final Collection a;

    CLASSNAMEp3(Collection collection) {
        this.a = collection;
    }

    public CLASSNAMEl3 b(int i) {
        throw new IndexOutOfBoundsException();
    }

    public long count() {
        return (long) this.a.size();
    }

    public void forEach(Consumer consumer) {
        CLASSNAMEk.s(this.a, consumer);
    }

    public void j(Object[] objArr, int i) {
        for (Object obj : this.a) {
            objArr[i] = obj;
            i++;
        }
    }

    public /* synthetic */ int o() {
        return 0;
    }

    public Object[] q(v vVar) {
        Collection collection = this.a;
        return collection.toArray((Object[]) vVar.apply(collection.size()));
    }

    public /* synthetic */ CLASSNAMEl3 r(long j, long j2, v vVar) {
        return CLASSNAMEc3.n(this, j, j2, vVar);
    }

    public Spliterator spliterator() {
        Collection collection = this.a;
        return (collection instanceof j$.util.Collection ? ((j$.util.Collection) collection).stream() : Collection.CC.$default$stream(collection)).spliterator();
    }

    public String toString() {
        return String.format("CollectionNode[%d][%s]", new Object[]{Integer.valueOf(this.a.size()), this.a});
    }
}
