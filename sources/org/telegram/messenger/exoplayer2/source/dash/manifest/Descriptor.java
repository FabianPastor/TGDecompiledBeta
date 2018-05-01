package org.telegram.messenger.exoplayer2.source.dash.manifest;

import org.telegram.messenger.exoplayer2.util.Util;

public final class Descriptor {
    public final String id;
    public final String schemeIdUri;
    public final String value;

    public Descriptor(String str, String str2, String str3) {
        this.schemeIdUri = str;
        this.value = str2;
        this.id = str3;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj != null) {
            if (getClass() == obj.getClass()) {
                Descriptor descriptor = (Descriptor) obj;
                if (!Util.areEqual(this.schemeIdUri, descriptor.schemeIdUri) || !Util.areEqual(this.value, descriptor.value) || Util.areEqual(this.id, descriptor.id) == null) {
                    z = false;
                }
                return z;
            }
        }
        return false;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = 31 * (((this.schemeIdUri != null ? this.schemeIdUri.hashCode() : 0) * 31) + (this.value != null ? this.value.hashCode() : 0));
        if (this.id != null) {
            i = this.id.hashCode();
        }
        return hashCode + i;
    }
}
