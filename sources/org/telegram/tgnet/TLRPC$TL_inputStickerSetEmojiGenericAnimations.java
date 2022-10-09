package org.telegram.tgnet;
/* loaded from: classes.dex */
public class TLRPC$TL_inputStickerSetEmojiGenericAnimations extends TLRPC$InputStickerSet {
    public static int constructor = 80008398;

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
    }
}
