package org.telegram.tgnet;

public class TLRPC$TL_globalPrivacySettings extends TLObject {
    public static int constructor = -NUM;
    public boolean archive_and_mute_new_noncontact_peers;
    public int flags;

    public static TLRPC$TL_globalPrivacySettings TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_globalPrivacySettings tLRPC$TL_globalPrivacySettings = new TLRPC$TL_globalPrivacySettings();
            tLRPC$TL_globalPrivacySettings.readParams(abstractSerializedData, z);
            return tLRPC$TL_globalPrivacySettings;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_globalPrivacySettings", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.archive_and_mute_new_noncontact_peers = abstractSerializedData.readBool(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeBool(this.archive_and_mute_new_noncontact_peers);
        }
    }
}
