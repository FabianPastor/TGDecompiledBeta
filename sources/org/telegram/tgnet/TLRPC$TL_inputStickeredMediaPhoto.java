package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_inputStickeredMediaPhoto extends TLRPC$InputStickeredMedia {
    public static int constructor = NUM;
    public TLRPC$InputPhoto id;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.id = TLRPC$InputPhoto.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.id.serializeToStream(abstractSerializedData);
    }
}
