package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_updateTheme extends TLRPC$Update {
    public static int constructor = -NUM;
    public TLRPC$Theme theme;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.theme = TLRPC$Theme.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.theme.serializeToStream(abstractSerializedData);
    }
}
