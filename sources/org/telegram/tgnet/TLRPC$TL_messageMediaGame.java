package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_messageMediaGame extends TLRPC$MessageMedia {
    public static int constructor = -38694904;

    @Override // org.telegram.tgnet.TLObject
    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.game = TLRPC$TL_game.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.game.serializeToStream(abstractSerializedData);
    }
}
