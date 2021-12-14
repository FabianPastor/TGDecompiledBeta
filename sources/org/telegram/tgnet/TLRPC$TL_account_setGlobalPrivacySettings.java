package org.telegram.tgnet;

public class TLRPC$TL_account_setGlobalPrivacySettings extends TLObject {
    public static int constructor = NUM;
    public TLRPC$TL_globalPrivacySettings settings;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_globalPrivacySettings.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.settings.serializeToStream(abstractSerializedData);
    }
}
