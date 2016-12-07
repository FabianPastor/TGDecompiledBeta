package org.telegram.messenger.exoplayer.drm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.telegram.messenger.exoplayer.util.Assertions;

public interface DrmInitData {

    public static final class SchemeInitData {
        public final byte[] data;
        public final String mimeType;

        public SchemeInitData(String mimeType, byte[] data) {
            this.mimeType = (String) Assertions.checkNotNull(mimeType);
            this.data = (byte[]) Assertions.checkNotNull(data);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof SchemeInitData)) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            SchemeInitData other = (SchemeInitData) obj;
            if (this.mimeType.equals(other.mimeType) && Arrays.equals(this.data, other.data)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return this.mimeType.hashCode() + (Arrays.hashCode(this.data) * 31);
        }
    }

    public static final class Mapped implements DrmInitData {
        private final Map<UUID, SchemeInitData> schemeData = new HashMap();

        public SchemeInitData get(UUID schemeUuid) {
            return (SchemeInitData) this.schemeData.get(schemeUuid);
        }

        public void put(UUID schemeUuid, SchemeInitData schemeInitData) {
            this.schemeData.put(schemeUuid, schemeInitData);
        }
    }

    public static final class Universal implements DrmInitData {
        private SchemeInitData data;

        public Universal(SchemeInitData data) {
            this.data = data;
        }

        public SchemeInitData get(UUID schemeUuid) {
            return this.data;
        }
    }

    SchemeInitData get(UUID uuid);
}
