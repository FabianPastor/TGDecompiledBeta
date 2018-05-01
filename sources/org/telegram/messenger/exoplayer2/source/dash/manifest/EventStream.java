package org.telegram.messenger.exoplayer2.source.dash.manifest;

import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage;

public final class EventStream {
    public final EventMessage[] events;
    public final long[] presentationTimesUs;
    public final String schemeIdUri;
    public final long timescale;
    public final String value;

    public EventStream(String schemeIdUri, String value, long timescale, long[] presentationTimesUs, EventMessage[] events) {
        this.schemeIdUri = schemeIdUri;
        this.value = value;
        this.timescale = timescale;
        this.presentationTimesUs = presentationTimesUs;
        this.events = events;
    }

    public String id() {
        return this.schemeIdUri + "/" + this.value;
    }
}
