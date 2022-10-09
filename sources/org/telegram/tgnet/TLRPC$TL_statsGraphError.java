package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_statsGraphError extends TLRPC$StatsGraph {
    public static int constructor = -NUM;
    public String error;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.error = abstractSerializedData.readString(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeString(this.error);
    }
}
