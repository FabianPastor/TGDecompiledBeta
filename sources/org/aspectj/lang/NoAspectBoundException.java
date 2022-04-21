package org.aspectj.lang;

public class NoAspectBoundException extends RuntimeException {
    Throwable cause;

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public NoAspectBoundException(java.lang.String r3, java.lang.Throwable r4) {
        /*
            r2 = this;
            if (r4 != 0) goto L_0x0004
            r0 = r3
            goto L_0x001d
        L_0x0004:
            java.lang.StringBuffer r0 = new java.lang.StringBuffer
            r0.<init>()
            java.lang.String r1 = "Exception while initializing "
            r0.append(r1)
            r0.append(r3)
            java.lang.String r1 = ": "
            r0.append(r1)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
        L_0x001d:
            r2.<init>(r0)
            r2.cause = r4
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.aspectj.lang.NoAspectBoundException.<init>(java.lang.String, java.lang.Throwable):void");
    }

    public NoAspectBoundException() {
    }

    public Throwable getCause() {
        return this.cause;
    }
}
