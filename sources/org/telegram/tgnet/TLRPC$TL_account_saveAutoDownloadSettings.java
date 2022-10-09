package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_saveAutoDownloadSettings extends TLObject {
    public static int constructor = NUM;
    public int flags;
    public boolean high;
    public boolean low;
    public TLRPC$TL_autoDownloadSettings settings;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$Bool.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.low ? this.flags | 1 : this.flags & (-2);
        this.flags = i;
        int i2 = this.high ? i | 2 : i & (-3);
        this.flags = i2;
        abstractSerializedData.writeInt32(i2);
        this.settings.serializeToStream(abstractSerializedData);
    }
}
