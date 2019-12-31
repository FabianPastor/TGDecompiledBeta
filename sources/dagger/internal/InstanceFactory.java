package dagger.internal;

import dagger.Lazy;

public final class InstanceFactory<T> implements Factory<T>, Lazy<T> {
    private final T instance;

    public static <T> Factory<T> create(T t) {
        Preconditions.checkNotNull(t, "instance cannot be null");
        return new InstanceFactory(t);
    }

    static {
        InstanceFactory instanceFactory = new InstanceFactory(null);
    }

    private InstanceFactory(T t) {
        this.instance = t;
    }

    public T get() {
        return this.instance;
    }
}
