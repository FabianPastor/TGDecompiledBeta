package dagger.internal;

import dagger.Lazy;
import javax.inject.Provider;

public final class DoubleCheck<T> implements Provider<T>, Lazy<T> {
    private static final Object UNINITIALIZED = new Object();
    private volatile Object instance = UNINITIALIZED;
    private volatile Provider<T> provider;

    private DoubleCheck(Provider<T> provider) {
        this.provider = provider;
    }

    public T get() {
        T t = this.instance;
        if (t == UNINITIALIZED) {
            synchronized (this) {
                t = this.instance;
                if (t == UNINITIALIZED) {
                    t = this.provider.get();
                    reentrantCheck(this.instance, t);
                    this.instance = t;
                    this.provider = null;
                }
            }
        }
        return t;
    }

    public static Object reentrantCheck(Object obj, Object obj2) {
        if ((obj != UNINITIALIZED ? 1 : null) == null || obj == obj2) {
            return obj2;
        }
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Scoped provider was invoked recursively returning different results: ");
        stringBuilder.append(obj);
        stringBuilder.append(" & ");
        stringBuilder.append(obj2);
        stringBuilder.append(". This is likely due to a circular dependency.");
        throw new IllegalStateException(stringBuilder.toString());
    }

    public static <P extends Provider<T>, T> Provider<T> provider(P p) {
        Preconditions.checkNotNull(p);
        if (p instanceof DoubleCheck) {
            return p;
        }
        return new DoubleCheck(p);
    }
}
