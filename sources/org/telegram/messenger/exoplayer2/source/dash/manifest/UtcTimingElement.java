package org.telegram.messenger.exoplayer2.source.dash.manifest;

public final class UtcTimingElement {
    public final String schemeIdUri;
    public final String value;

    public UtcTimingElement(String schemeIdUri, String value) {
        this.schemeIdUri = schemeIdUri;
        this.value = value;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.schemeIdUri);
        stringBuilder.append(", ");
        stringBuilder.append(this.value);
        return stringBuilder.toString();
    }
}
