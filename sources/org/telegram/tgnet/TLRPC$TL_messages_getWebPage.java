package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messages_getWebPage extends TLObject {
    public static int constructor = NUM;
    public int hash;
    public String url;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        return TLRPC$WebPage.TLdeserialize(abstractSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.url);
        abstractSerializedData.writeInt32(this.hash);
    }
}
