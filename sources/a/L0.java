package a;

import j$.util.stream.CLASSNAMEl1;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.stream.BaseStream;

public final /* synthetic */ class L0 implements BaseStream {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CLASSNAMEl1 var_a;

    private /* synthetic */ L0(CLASSNAMEl1 l1Var) {
        this.var_a = l1Var;
    }

    public static /* synthetic */ BaseStream m0(CLASSNAMEl1 l1Var) {
        if (l1Var == null) {
            return null;
        }
        return l1Var instanceof K0 ? ((K0) l1Var).var_a : new L0(l1Var);
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

    public /* synthetic */ BaseStream onClose(Runnable runnable) {
        return m0(this.var_a.onClose(runnable));
    }

    public /* synthetic */ BaseStream parallel() {
        return m0(this.var_a.parallel());
    }

    public /* synthetic */ BaseStream sequential() {
        return m0(this.var_a.sequential());
    }

    public /* synthetic */ Spliterator spliterator() {
        return CLASSNAMEk.a(this.var_a.spliterator());
    }

    public /* synthetic */ BaseStream unordered() {
        return m0(this.var_a.unordered());
    }
}
