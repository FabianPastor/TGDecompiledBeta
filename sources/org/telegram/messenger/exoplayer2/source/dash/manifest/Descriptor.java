package org.telegram.messenger.exoplayer2.source.dash.manifest;

import org.telegram.messenger.exoplayer2.util.Util;

public final class Descriptor {
    public final String id;
    public final String schemeIdUri;
    public final String value;

    public Descriptor(String schemeIdUri, String value, String id) {
        this.schemeIdUri = schemeIdUri;
        this.value = value;
        this.id = id;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                Descriptor other = (Descriptor) obj;
                if (!Util.areEqual(this.schemeIdUri, other.schemeIdUri) || !Util.areEqual(this.value, other.value) || !Util.areEqual(this.id, other.id)) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int result = 31 * ((31 * (this.schemeIdUri != null ? this.schemeIdUri.hashCode() : 0)) + (this.value != null ? this.value.hashCode() : 0));
        if (this.id != null) {
            i = this.id.hashCode();
        }
        return result + i;
    }
}
