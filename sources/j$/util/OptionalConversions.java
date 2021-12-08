package j$.util;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class OptionalConversions {
    private OptionalConversions() {
    }

    public static <T> Optional<T> convert(Optional<T> optional) {
        if (optional == null) {
            return null;
        }
        if (optional.isPresent()) {
            return Optional.of(optional.get());
        }
        return Optional.empty();
    }

    public static <T> Optional<T> convert(Optional<T> optional) {
        if (optional == null) {
            return null;
        }
        if (optional.isPresent()) {
            return Optional.of(optional.get());
        }
        return Optional.empty();
    }

    public static OptionalDouble convert(OptionalDouble optionalDouble) {
        if (optionalDouble == null) {
            return null;
        }
        if (optionalDouble.isPresent()) {
            return OptionalDouble.of(optionalDouble.getAsDouble());
        }
        return OptionalDouble.empty();
    }

    public static OptionalDouble convert(OptionalDouble optionalDouble) {
        if (optionalDouble == null) {
            return null;
        }
        if (optionalDouble.isPresent()) {
            return OptionalDouble.of(optionalDouble.getAsDouble());
        }
        return OptionalDouble.empty();
    }

    public static OptionalLong convert(OptionalLong optionalLong) {
        if (optionalLong == null) {
            return null;
        }
        if (optionalLong.isPresent()) {
            return OptionalLong.of(optionalLong.getAsLong());
        }
        return OptionalLong.empty();
    }

    public static OptionalLong convert(OptionalLong optionalLong) {
        if (optionalLong == null) {
            return null;
        }
        if (optionalLong.isPresent()) {
            return OptionalLong.of(optionalLong.getAsLong());
        }
        return OptionalLong.empty();
    }

    public static OptionalInt convert(OptionalInt optionalInt) {
        if (optionalInt == null) {
            return null;
        }
        if (optionalInt.isPresent()) {
            return OptionalInt.of(optionalInt.getAsInt());
        }
        return OptionalInt.empty();
    }

    public static OptionalInt convert(OptionalInt optionalInt) {
        if (optionalInt == null) {
            return null;
        }
        if (optionalInt.isPresent()) {
            return OptionalInt.of(optionalInt.getAsInt());
        }
        return OptionalInt.empty();
    }
}
