package org.telegram.messenger.exoplayer2.source.dash.manifest;

public final class UtcTimingElement {
    public final String schemeIdUri;
    public final String value;

    public UtcTimingElement(String str, String str2) {
        this.schemeIdUri = str;
        this.value = str2;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(this.schemeIdUri);
        stringBuilder.append(", ");
        stringBuilder.append(this.value);
        return stringBuilder.toString();
    }
}
