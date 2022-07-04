package j$.util;

import j$.util.function.DoubleConsumer;
import j$.util.function.DoubleSupplier;
import j$.util.function.Supplier;
import java.util.NoSuchElementException;

public final class OptionalDouble {
    private static final OptionalDouble EMPTY = new OptionalDouble();
    private final boolean isPresent;
    private final double value;

    private OptionalDouble() {
        this.isPresent = false;
        this.value = Double.NaN;
    }

    public static OptionalDouble empty() {
        return EMPTY;
    }

    private OptionalDouble(double value2) {
        this.isPresent = true;
        this.value = value2;
    }

    public static OptionalDouble of(double value2) {
        return new OptionalDouble(value2);
    }

    public double getAsDouble() {
        if (this.isPresent) {
            return this.value;
        }
        throw new NoSuchElementException("No value present");
    }

    public boolean isPresent() {
        return this.isPresent;
    }

    public void ifPresent(DoubleConsumer consumer) {
        if (this.isPresent) {
            consumer.accept(this.value);
        }
    }

    public double orElse(double other) {
        return this.isPresent ? this.value : other;
    }

    public double orElseGet(DoubleSupplier other) {
        return this.isPresent ? this.value : other.getAsDouble();
    }

    public <X extends Throwable> double orElseThrow(Supplier<X> supplier) {
        if (this.isPresent) {
            return this.value;
        }
        throw ((Throwable) supplier.get());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OptionalDouble)) {
            return false;
        }
        OptionalDouble other = (OptionalDouble) obj;
        boolean z = this.isPresent;
        if (!z || !other.isPresent) {
            if (z == other.isPresent) {
                return true;
            }
            return false;
        } else if (Double.compare(this.value, other.value) == 0) {
            return true;
        } else {
            return false;
        }
    }

    public int hashCode() {
        if (this.isPresent) {
            return Double.doubleToLongBits(this.value);
        }
        return 0;
    }

    public String toString() {
        if (!this.isPresent) {
            return "OptionalDouble.empty";
        }
        return String.format("OptionalDouble[%s]", new Object[]{Double.valueOf(this.value)});
    }
}
