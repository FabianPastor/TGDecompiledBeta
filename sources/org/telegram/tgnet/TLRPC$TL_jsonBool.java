package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_jsonBool extends TLRPC$JSONValue {
    public static int constructor = -NUM;
    public boolean value;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.value = abstractSerializedData.readBool(z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        abstractSerializedData.writeBool(this.value);
    }
}
