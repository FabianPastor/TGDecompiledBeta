package org.webrtc;

import org.webrtc.Logging;

class JNILogging {
    private final Loggable loggable;

    public JNILogging(Loggable loggable2) {
        this.loggable = loggable2;
    }

    public void logToInjectable(String message, Integer severity, String tag) {
        this.loggable.onLogMessage(message, Logging.Severity.values()[severity.intValue()], tag);
    }
}
