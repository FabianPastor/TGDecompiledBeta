package j$.util.stream;

import j$.util.concurrent.ConcurrentHashMap;
import j$.util.function.BiConsumer;
import j$.util.function.Consumer;
import j$.util.function.Predicate;
import j$.util.function.y;
import j$.wrappers.CLASSNAMEj0;
import j$.wrappers.E;
import j$.wrappers.V;
import java.util.concurrent.atomic.AtomicBoolean;

/* renamed from: j$.util.stream.o  reason: case insensitive filesystem */
public final /* synthetic */ class CLASSNAMEo implements Consumer, y {
    public final /* synthetic */ int a = 5;
    public final /* synthetic */ Object b;
    public final /* synthetic */ Object c;

    public /* synthetic */ CLASSNAMEo(BiConsumer biConsumer, Object obj) {
        this.b = biConsumer;
        this.c = obj;
    }

    public void accept(Object obj) {
        switch (this.a) {
            case 0:
                AtomicBoolean atomicBoolean = (AtomicBoolean) this.b;
                ConcurrentHashMap concurrentHashMap = (ConcurrentHashMap) this.c;
                if (obj == null) {
                    atomicBoolean.set(true);
                    return;
                } else {
                    concurrentHashMap.putIfAbsent(obj, Boolean.TRUE);
                    return;
                }
            case 5:
                ((BiConsumer) this.b).accept(this.c, obj);
                return;
            default:
                ((CLASSNAMEm4) this.b).f((Consumer) this.c, obj);
                return;
        }
    }

    public /* synthetic */ Consumer andThen(Consumer consumer) {
        switch (this.a) {
            case 0:
                return Consumer.CC.$default$andThen(this, consumer);
            case 5:
                return Consumer.CC.$default$andThen(this, consumer);
            default:
                return Consumer.CC.$default$andThen(this, consumer);
        }
    }

    public Object get() {
        switch (this.a) {
            case 1:
                return new CLASSNAMEi1((CLASSNAMEk1) this.b, (E) this.c);
            case 2:
                return new CLASSNAMEg1((CLASSNAMEk1) this.b, (V) this.c);
            case 3:
                return new CLASSNAMEh1((CLASSNAMEk1) this.b, (CLASSNAMEj0) this.c);
            default:
                return new CLASSNAMEf1((CLASSNAMEk1) this.b, (Predicate) this.c);
        }
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEk1 k1Var, Predicate predicate) {
        this.b = k1Var;
        this.c = predicate;
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEk1 k1Var, E e) {
        this.b = k1Var;
        this.c = e;
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEk1 k1Var, V v) {
        this.b = k1Var;
        this.c = v;
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEk1 k1Var, CLASSNAMEj0 j0Var) {
        this.b = k1Var;
        this.c = j0Var;
    }

    public /* synthetic */ CLASSNAMEo(CLASSNAMEm4 m4Var, Consumer consumer) {
        this.b = m4Var;
        this.c = consumer;
    }

    public /* synthetic */ CLASSNAMEo(AtomicBoolean atomicBoolean, ConcurrentHashMap concurrentHashMap) {
        this.b = atomicBoolean;
        this.c = concurrentHashMap;
    }
}
