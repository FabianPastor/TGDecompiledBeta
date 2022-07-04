package j$.util;

import j$.util.function.IntConsumer;
import j$.util.function.IntSupplier;
import j$.util.function.Supplier;
import java.util.NoSuchElementException;

public final class OptionalInt {
    private static final OptionalInt EMPTY = new OptionalInt();
    private final boolean isPresent;
    private final int value;

    private OptionalInt() {
        this.isPresent = false;
        this.value = 0;
    }

    public static OptionalInt empty() {
        return EMPTY;
    }

    private OptionalInt(int value2) {
        this.isPresent = true;
        this.value = value2;
    }

    public static OptionalInt of(int value2) {
        return new OptionalInt(value2);
    }

    public int getAsInt() {
        if (this.isPresent) {
            return this.value;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean isPresent() {
        return this.isPresent;
    }

    public void ifPresent(IntConsumer consumer) {
        if (this.isPresent) {
            consumer.accept(this.value);
        }
    }

    public int orElse(int other) {
        return this.isPresent ? this.value : other;
    }

    public int orElseGet(IntSupplier other) {
        return this.isPresent ? this.value : other.getAsInt();
    }

    public <X extends Throwable> int orElseThrow(Supplier<X> supplier) {
        if (this.isPresent) {
            return this.value;
        }
        throw ((Throwable) supplier.get());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OptionalInt)) {
            return false;
        }
        OptionalInt other = (OptionalInt) obj;
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
            return this.value;
        }
        return 0;
    }

    public String toString() {
        if (!this.isPresent) {
            return "OptionalInt.empty";
        }
        return String.format("OptionalInt[%s]", new Object[]{Integer.valueOf(this.value)});
    }
}
