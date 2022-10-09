package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$TopPeerCategory extends TLObject {
    public static TLRPC$TopPeerCategory TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TopPeerCategory tLRPC$TopPeerCategory;
        switch (i) {
            case -1472172887:
                tLRPC$TopPeerCategory = new TLRPC$TopPeerCategory() { // from class: org.telegram.tgnet.TLRPC$TL_topPeerCategoryForwardUsers
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1419371685:
                tLRPC$TopPeerCategory = new TLRPC$TopPeerCategory() { // from class: org.telegram.tgnet.TLRPC$TL_topPeerCategoryBotsPM
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -1122524854:
                tLRPC$TopPeerCategory = new TLRPC$TopPeerCategory() { // from class: org.telegram.tgnet.TLRPC$TL_topPeerCategoryGroups
                    public static int constructor = -NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case -68239120:
                tLRPC$TopPeerCategory = new TLRPC$TopPeerCategory() { // from class: org.telegram.tgnet.TLRPC$TL_topPeerCategoryForwardChats
                    public static int constructor = -68239120;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 104314861:
                tLRPC$TopPeerCategory = new TLRPC$TL_topPeerCategoryCorrespondents();
                break;
            case 344356834:
                tLRPC$TopPeerCategory = new TLRPC$TL_topPeerCategoryBotsInline();
                break;
            case 371037736:
                tLRPC$TopPeerCategory = new TLRPC$TopPeerCategory() { // from class: org.telegram.tgnet.TLRPC$TL_topPeerCategoryChannels
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            case 511092620:
                tLRPC$TopPeerCategory = new TLRPC$TopPeerCategory() { // from class: org.telegram.tgnet.TLRPC$TL_topPeerCategoryPhoneCalls
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                    }
                };
                break;
            default:
                tLRPC$TopPeerCategory = null;
                break;
        }
        if (tLRPC$TopPeerCategory != null || !z) {
            if (tLRPC$TopPeerCategory != null) {
                tLRPC$TopPeerCategory.readParams(abstractSerializedData, z);
            }
            return tLRPC$TopPeerCategory;
        }
        throw new RuntimeException(String.format("can't parse magic %x in TopPeerCategory", Integer.valueOf(i)));
    }
}
