package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_autoDownloadSettings extends TLObject {
    public static int constructor = NUM;
    public TLRPC$TL_autoDownloadSettings high;
    public TLRPC$TL_autoDownloadSettings low;
    public TLRPC$TL_autoDownloadSettings medium;

    public static TLRPC$TL_account_autoDownloadSettings TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor != i) {
            if (z) {
                throw new RuntimeException(String.format("can't parse magic %x in TL_account_autoDownloadSettings", Integer.valueOf(i)));
            }
            return null;
        }
        TLRPC$TL_account_autoDownloadSettings tLRPC$TL_account_autoDownloadSettings = new TLRPC$TL_account_autoDownloadSettings();
        tLRPC$TL_account_autoDownloadSettings.readParams(abstractSerializedData, z);
        return tLRPC$TL_account_autoDownloadSettings;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.low = TLRPC$TL_autoDownloadSettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.medium = TLRPC$TL_autoDownloadSettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.high = TLRPC$TL_autoDownloadSettings.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.low.serializeToStream(abstractSerializedData);
        this.medium.serializeToStream(abstractSerializedData);
        this.high.serializeToStream(abstractSerializedData);
    }
}
