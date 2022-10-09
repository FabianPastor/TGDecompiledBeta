package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_pageBlockAnchor extends TLRPC$PageBlock {
    public static int constructor = -NUM;
    public String name;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.name = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.name);
    }
}
