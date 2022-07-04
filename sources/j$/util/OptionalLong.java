package j$.util;

import j$.util.function.LongConsumer;
import j$.util.function.LongSupplier;
import j$.util.function.Supplier;
import java.util.NoSuchElementException;

public final class OptionalLong {
    private static final OptionalLong EMPTY = new OptionalLong();
    private final boolean isPresent;
    private final long value;

    private OptionalLong() {
        this.isPresent = false;
        this.value = 0;
    }

    public static OptionalLong empty() {
        return EMPTY;
    }

    private OptionalLong(long value2) {
        this.isPresent = true;
        this.value = value2;
    }

    public static OptionalLong of(long value2) {
        return new OptionalLong(value2);
    }

    public long getAsLong() {
        if (this.isPresent) {
            return this.value;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean isPresent() {
        return this.isPresent;
    }

    public void ifPresent(LongConsumer consumer) {
        if (this.isPresent) {
            consumer.accept(this.value);
        }
    }

    public long orElse(long other) {
        return this.isPresent ? this.value : other;
    }

    public long orElseGet(LongSupplier other) {
        return this.isPresent ? this.value : other.getAsLong();
    }

    public <X extends Throwable> long orElseThrow(Supplier<X> supplier) {
        if (this.isPresent) {
            return this.value;
        }
        throw ((Throwable) supplier.get());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OptionalLong)) {
            return false;
        }
        OptionalLong other = (OptionalLong) obj;
        boolean z = this.isPresent;
        if (!z || !other.isPresent) {
            if (z == other.isPresent) {
                return true;
            }
            return false;
        } else if (this.value == other.value) {
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        if (this.isPresent) {
            return OptionalLong$$ExternalSyntheticBackport0.m(this.value);
        }
        return 0;
    }

    public String toString() {
        if (!this.isPresent) {
            return "OptionalLong.empty";
        }
        return String.format("OptionalLong[%s]", new Object[]{Long.valueOf(this.value)});
    }
}
