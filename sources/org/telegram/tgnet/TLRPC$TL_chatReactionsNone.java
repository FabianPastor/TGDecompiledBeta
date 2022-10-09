package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_chatReactionsNone extends TLRPC$ChatReactions {
    public static int constructor = -NUM;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
