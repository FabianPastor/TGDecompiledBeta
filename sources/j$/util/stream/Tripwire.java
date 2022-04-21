package j$.util.stream;

import java.security.AccessController;

final class Tripwire {
    static final boolean ENABLED = ((Boolean) AccessController.doPrivileged(Tripwire$$ExternalSyntheticLambda0.INSTANCE)).booleanValue();
    private static final String TRIPWIRE_PROPERTY = "org.openjdk.java.util.stream.tripwire";

    private Tripwire() {
    }

    static void trip(Class<?> trippingClass, String msg) {
        throw new UnsupportedOperationException(trippingClass + " tripwire tripped but logging not supported: " + msg);
    }
}
