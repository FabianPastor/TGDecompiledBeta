package org.aspectj.runtime.internal;

public final class Conversions {
    public static Object intObject(int i) {
        return new Integer(i);
    }

    public static Object byteObject(byte i) {
        return new Byte(i);
    }

    public static Object longObject(long i) {
        return new Long(i);
    }

    public static Object floatObject(float i) {
        return new Float(i);
    }

    public static Object doubleObject(double i) {
        return new Double(i);
    }

    public static Object booleanObject(boolean i) {
        return new Boolean(i);
    }
}
