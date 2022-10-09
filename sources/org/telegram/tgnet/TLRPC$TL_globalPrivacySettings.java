package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_globalPrivacySettings extends TLObject {
    public static int constructor = -NUM;
    public boolean archive_and_mute_new_noncontact_peers;
    public int flags;

    public static TLRPC$TL_globalPrivacySettings TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_globalPrivacySettings", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_globalPrivacySettings tLRPC$TL_globalPrivacySettings = new TLRPC$TL_globalPrivacySettings();
        tLRPC$TL_globalPrivacySettings.readParams(abstractSerializedData, z);
        return tLRPC$TL_globalPrivacySettings;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        if ((readInt32 & 1) != 0) {
            this.archive_and_mute_new_noncontact_peers = abstractSerializedData.readBool(z);
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeInt32(this.flags);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeBool(this.archive_and_mute_new_noncontact_peers);
        }
    }
}
