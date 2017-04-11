package org.telegram.messenger.exoplayer2.source.dash.manifest;

import org.telegram.messenger.exoplayer2.util.Util;

public class SchemeValuePair {
    public final String schemeIdUri;
    public final String value;

    public SchemeValuePair(String schemeIdUri, String value) {
        this.schemeIdUri = schemeIdUri;
        this.value = value;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SchemeValuePair other = (SchemeValuePair) obj;
        if (Util.areEqual(this.schemeIdUri, other.schemeIdUri) && Util.areEqual(this.value, other.value)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (this.schemeIdUri != null ? this.schemeIdUri.hashCode() : 0) * 31;
        if (this.value != null) {
            i = this.value.hashCode();
        }
        return hashCode + i;
    }
}
