package j$.util;

import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;

public class A {
    public static Optional a(CLASSNAMEz zVar) {
        if (zVar == null) {
            return null;
        }
        if (zVar.c()) {
            return Optional.of(zVar.b());
        }
        return Optional.empty();
    }

    public static OptionalDouble b(B optionalDouble) {
        if (optionalDouble == null) {
            return null;
        }
        if (optionalDouble.c()) {
            return OptionalDouble.of(optionalDouble.b());
        }
        return OptionalDouble.empty();
    }

    public static OptionalLong d(D optionalLong) {
        if (optionalLong == null) {
            return null;
        }
        if (optionalLong.c()) {
            return OptionalLong.of(optionalLong.b());
        }
        return OptionalLong.empty();
    }

    public static OptionalInt c(C optionalInt) {
        if (optionalInt == null) {
            return null;
        }
        if (optionalInt.c()) {
            return OptionalInt.of(optionalInt.b());
        }
        return OptionalInt.empty();
    }
}
