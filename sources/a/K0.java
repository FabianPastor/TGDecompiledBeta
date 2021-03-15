package a;

import j$.util.Spliterator;
import j$.util.stream.CLASSNAMEl1;
import java.util.Iterator;
import java.util.stream.BaseStream;

public final /* synthetic */ class K0 implements CLASSNAMEl1 {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ BaseStream var_a;

    private /* synthetic */ K0(BaseStream baseStream) {
        this.var_a = baseStream;
    }

    public static /* synthetic */ CLASSNAMEl1 m0(BaseStream baseStream) {
        if (baseStream == null) {
            return null;
        }
        return baseStream instanceof L0 ? ((L0) baseStream).var_a : new K0(baseStream);
    }

    public /* synthetic */ void close() {
        this.var_a.close();
    }

    public /* synthetic */ boolean isParallel() {
        return this.var_a.isParallel();
    }

    public /* synthetic */ Iterator iterator() {
        return this.var_a.iterator();
    }

    public /* synthetic */ CLASSNAMEl1 onClose(Runnable runnable) {
        return m0(this.var_a.onClose(runnable));
    }

    public /* synthetic */ CLASSNAMEl1 parallel() {
        return m0(this.var_a.parallel());
    }

    public /* synthetic */ CLASSNAMEl1 sequential() {
        return m0(this.var_a.sequential());
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEj.a(this.var_a.spliterator());
    }

    public /* synthetic */ CLASSNAMEl1 unordered() {
        return m0(this.var_a.unordered());
    }
}
