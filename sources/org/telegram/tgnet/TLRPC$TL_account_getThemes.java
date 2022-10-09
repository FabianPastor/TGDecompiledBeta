package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_account_getThemes extends TLObject {
    public static int constructor = NUM;
    public String format;
    public long hash;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$account_Themes.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.format);
        abstractSerializedData.writeInt64(this.hash);
    }
}
