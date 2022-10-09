package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_emojiStatusEmpty extends TLRPC$EmojiStatus {
    public static int constructor = NUM;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
