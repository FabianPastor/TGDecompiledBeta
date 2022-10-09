package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_FoundStickerSets extends TLObject {
    public static TLRPC$messages_FoundStickerSets TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_FoundStickerSets tLRPC$TL_messages_foundStickerSets;
        if (i != -NUM) {
            tLRPC$TL_messages_foundStickerSets = i != NUM ? null : new TLRPC$messages_FoundStickerSets() { // from class: org.telegram.tgnet.TLRPC$TL_messages_foundStickerSetsNotModified
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$TL_messages_foundStickerSets = new TLRPC$TL_messages_foundStickerSets();
        }
        if (tLRPC$TL_messages_foundStickerSets != null || !z) {
            if (tLRPC$TL_messages_foundStickerSets != null) {
                tLRPC$TL_messages_foundStickerSets.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messages_foundStickerSets;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_FoundStickerSets", Integer.valueOf(i)));
    }
}
