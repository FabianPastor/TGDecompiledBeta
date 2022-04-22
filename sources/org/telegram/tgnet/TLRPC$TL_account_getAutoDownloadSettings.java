package org.telegram.tgnet;

public class TLRPC$TL_account_getAutoDownloadSettings extends TLObject {
    public static int constructor = NUM;

    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$TL_account_autoDownloadSettings.TLdeserialize(abstractSerializedData, i, z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
