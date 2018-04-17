package org.aspectj.lang;

public class NoAspectBoundException extends RuntimeException {
    Throwable cause;

    public NoAspectBoundException(String aspectName, Throwable inner) {
        String str;
        if (inner == null) {
            str = aspectName;
        } else {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("Exception while initializing ");
            stringBuffer.append(aspectName);
            stringBuffer.append(": ");
            stringBuffer.append(inner);
            str = stringBuffer.toString();
        }
        super(str);
        this.cause = inner;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
