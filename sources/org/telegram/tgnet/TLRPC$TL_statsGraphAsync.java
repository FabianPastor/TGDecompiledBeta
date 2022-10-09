package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_statsGraphAsync extends TLRPC$StatsGraph {
    public static int constructor = NUM;
    public String token;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.token = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.token);
    }
}
