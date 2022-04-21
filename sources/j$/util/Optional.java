package j$.util;

import j$.util.function.Consumer;
import j$.util.function.Function;
import j$.util.function.Predicate;
import j$.util.function.Supplier;
import java.util.NoSuchElementException;

public final class Optional<T> {
    private static final Optional<?> EMPTY = new Optional<>();
    private final T value;

    private Optional() {
        this.value = null;
    }

    public static <T> Optional<T> empty() {
        return EMPTY;
    }

    private Optional(T value2) {
        value2.getClass();
        this.value = value2;
    }

    public static <T> Optional<T> of(T value2) {
        return new Optional<>(value2);
    }

    public static <T> Optional<T> ofNullable(T value2) {
        return value2 == null ? empty() : of(value2);
    }

    public T get() {
        T t = this.value;
        if (t != null) {
            return t;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean isPresent() {
        return this.value != null;
    }

    public void ifPresent(Consumer<? super T> consumer) {
        T t = this.value;
        if (t != null) {
            consumer.accept(t);
        }
    }

    public Optional<T> filter(Predicate<? super T> predicate) {
        predicate.getClass();
        if (!isPresent()) {
            return this;
        }
        return predicate.test(this.value) ? this : empty();
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> function) {
        function.getClass();
        if (!isPresent()) {
            return empty();
        }
        return ofNullable(function.apply(this.value));
    }

    public <U> Optional<U> flatMap(Function<? super T, Optional<U>> function) {
        function.getClass();
        if (!isPresent()) {
            return empty();
        }
        Optional<U> apply = function.apply(this.value);
        apply.getClass();
        return apply;
    }

    public T orElse(T other) {
        T t = this.value;
        return t != null ? t : other;
    }

    public T orElseGet(Supplier<? extends T> supplier) {
        T t = this.value;
        return t != null ? t : supplier.get();
    }

    public <X extends Throwable> T orElseThrow(Supplier<? extends X> supplier) {
        T t = this.value;
        if (t != null) {
            return t;
        }
        throw ((Throwable) supplier.get());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Optional)) {
            return false;
        }
        return Objects.equals(this.value, ((Optional) obj).value);
    }

    public int hashCode() {
        return Objects.hashCode(this.value);
    }

    public String toString() {
        T t = this.value;
        if (t == null) {
            return "Optional.empty";
        }
        return String.format("Optional[%s]", new Object[]{t});
    }
}
