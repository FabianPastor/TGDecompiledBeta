package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_jsonNumber extends TLRPC$JSONValue {
    public static int constructor = NUM;
    public double value;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.value = abstractSerializedData.readDouble(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeDouble(this.value);
    }
}
