package org.telegram.messenger.exoplayer2;

import java.io.IOException;

public class ParserException extends IOException {
    public ParserException(String str) {
        super(str);
    }

    public ParserException(Throwable th) {
        super(th);
    }

    public ParserException(String str, Throwable th) {
        super(str, th);
    }
}
