package org.telegram.messenger.exoplayer.dash.mpd;

import java.util.UUID;
import org.telegram.messenger.exoplayer.drm.DrmInitData.SchemeInitData;
import org.telegram.messenger.exoplayer.util.Assertions;
import org.telegram.messenger.exoplayer.util.Util;

public class ContentProtection {
    public final SchemeInitData data;
    public final String schemeUriId;
    public final UUID uuid;

    public ContentProtection(String schemeUriId, UUID uuid, SchemeInitData data) {
        this.schemeUriId = (String) Assertions.checkNotNull(schemeUriId);
        this.uuid = uuid;
        this.data = data;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ContentProtection)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        ContentProtection other = (ContentProtection) obj;
        if (this.schemeUriId.equals(other.schemeUriId) && Util.areEqual(this.uuid, other.uuid) && Util.areEqual(this.data, other.data)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int hashCode;
        int i = 0;
        int hashCode2 = this.schemeUriId.hashCode() * 37;
        if (this.uuid != null) {
            hashCode = this.uuid.hashCode();
        } else {
            hashCode = 0;
        }
        hashCode = (hashCode2 + hashCode) * 37;
        if (this.data != null) {
            i = this.data.hashCode();
        }
        return hashCode + i;
    }
}
