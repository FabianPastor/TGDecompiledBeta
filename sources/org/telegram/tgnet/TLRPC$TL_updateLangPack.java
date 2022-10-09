package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updateLangPack extends TLRPC$Update {
    public static int constructor = NUM;
    public TLRPC$TL_langPackDifference difference;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.difference = TLRPC$TL_langPackDifference.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.difference.serializeToStream(abstractSerializedData);
    }
}
