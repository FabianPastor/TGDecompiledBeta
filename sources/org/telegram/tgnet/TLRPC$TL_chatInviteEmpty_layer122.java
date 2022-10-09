package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_chatInviteEmpty_layer122 extends TLRPC$TL_chatInviteExported {
    public static int constructor = NUM;

    @Override // org.telegram.tgnet.TLRPC$TL_chatInviteExported, org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
