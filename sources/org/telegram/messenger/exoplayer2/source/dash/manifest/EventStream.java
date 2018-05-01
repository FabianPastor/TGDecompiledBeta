package org.telegram.messenger.exoplayer2.source.dash.manifest;

import org.telegram.messenger.exoplayer2.metadata.emsg.EventMessage;

public final class EventStream {
    public final EventMessage[] events;
    public final long[] presentationTimesUs;
    public final String schemeIdUri;
    public final long timescale;
    public final String value;

    public EventStream(String str, String str2, long j, long[] jArr, EventMessage[] eventMessageArr) {
        this.schemeIdUri = str;
        this.value = str2;
        this.timescale = j;
        this.presentationTimesUs = jArr;
        this.events = eventMessageArr;
    }

    public String id() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.schemeIdUri);
        stringBuilder.append("/");
        stringBuilder.append(this.value);
        return stringBuilder.toString();
    }
}
