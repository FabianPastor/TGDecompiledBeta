package org.telegram.messenger.exoplayer2.extractor.mp4;

import android.util.Log;
import java.nio.ByteBuffer;
import java.util.UUID;
import org.telegram.messenger.exoplayer2.util.ParsableByteArray;

public final class PsshAtomUtil {
    private static final String TAG = "PsshAtomUtil";

    private static class PsshAtom {
        private final byte[] schemeData;
        private final UUID uuid;
        private final int version;

        public PsshAtom(UUID uuid, int version, byte[] schemeData) {
            this.uuid = uuid;
            this.version = version;
            this.schemeData = schemeData;
        }
    }

    private PsshAtomUtil() {
    }

    public static byte[] buildPsshAtom(UUID systemId, byte[] data) {
        return buildPsshAtom(systemId, null, data);
    }

    public static byte[] buildPsshAtom(UUID systemId, UUID[] keyIds, byte[] data) {
        int i = 0;
        boolean buildV1Atom = keyIds != null;
        int dataLength = data != null ? data.length : 0;
        int psshBoxLength = 32 + dataLength;
        if (buildV1Atom) {
            psshBoxLength += 4 + (keyIds.length * 16);
        }
        ByteBuffer psshBox = ByteBuffer.allocate(psshBoxLength);
        psshBox.putInt(psshBoxLength);
        psshBox.putInt(Atom.TYPE_pssh);
        psshBox.putInt(buildV1Atom ? 16777216 : 0);
        psshBox.putLong(systemId.getMostSignificantBits());
        psshBox.putLong(systemId.getLeastSignificantBits());
        if (buildV1Atom) {
            psshBox.putInt(keyIds.length);
            int length = keyIds.length;
            while (i < length) {
                UUID keyId = keyIds[i];
                psshBox.putLong(keyId.getMostSignificantBits());
                psshBox.putLong(keyId.getLeastSignificantBits());
                i++;
            }
        }
        if (dataLength != 0) {
            psshBox.putInt(data.length);
            psshBox.put(data);
        }
        return psshBox.array();
    }

    public static UUID parseUuid(byte[] atom) {
        PsshAtom parsedAtom = parsePsshAtom(atom);
        if (parsedAtom == null) {
            return null;
        }
        return parsedAtom.uuid;
    }

    public static int parseVersion(byte[] atom) {
        PsshAtom parsedAtom = parsePsshAtom(atom);
        if (parsedAtom == null) {
            return -1;
        }
        return parsedAtom.version;
    }

    public static byte[] parseSchemeSpecificData(byte[] atom, UUID uuid) {
        PsshAtom parsedAtom = parsePsshAtom(atom);
        if (parsedAtom == null) {
            return null;
        }
        if (uuid == null || uuid.equals(parsedAtom.uuid)) {
            return parsedAtom.schemeData;
        }
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("UUID mismatch. Expected: ");
        stringBuilder.append(uuid);
        stringBuilder.append(", got: ");
        stringBuilder.append(parsedAtom.uuid);
        stringBuilder.append(".");
        Log.w(str, stringBuilder.toString());
        return null;
    }

    private static PsshAtom parsePsshAtom(byte[] atom) {
        ParsableByteArray atomData = new ParsableByteArray(atom);
        if (atomData.limit() < 32) {
            return null;
        }
        atomData.setPosition(0);
        if (atomData.readInt() != atomData.bytesLeft() + 4 || atomData.readInt() != Atom.TYPE_pssh) {
            return null;
        }
        int atomVersion = Atom.parseFullAtomVersion(atomData.readInt());
        if (atomVersion > 1) {
            String str = TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Unsupported pssh version: ");
            stringBuilder.append(atomVersion);
            Log.w(str, stringBuilder.toString());
            return null;
        }
        UUID uuid = new UUID(atomData.readLong(), atomData.readLong());
        if (atomVersion == 1) {
            atomData.skipBytes(16 * atomData.readUnsignedIntToInt());
        }
        int dataSize = atomData.readUnsignedIntToInt();
        if (dataSize != atomData.bytesLeft()) {
            return null;
        }
        byte[] data = new byte[dataSize];
        atomData.readBytes(data, 0, dataSize);
        return new PsshAtom(uuid, atomVersion, data);
    }
}
