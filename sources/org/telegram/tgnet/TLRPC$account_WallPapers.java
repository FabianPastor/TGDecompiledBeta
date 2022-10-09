package org.telegram.tgnet;
/* loaded from: classes.dex */
public abstract class TLRPC$account_WallPapers extends TLObject {
    public static TLRPC$account_WallPapers TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$account_WallPapers tLRPC$TL_account_wallPapers;
        if (i != -NUM) {
            tLRPC$TL_account_wallPapers = i != NUM ? null : new TLRPC$account_WallPapers() { // from class: org.telegram.tgnet.TLRPC$TL_account_wallPapersNotModified
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                }
            };
        } else {
            tLRPC$TL_account_wallPapers = new TLRPC$TL_account_wallPapers();
        }
        if (tLRPC$TL_account_wallPapers != null || !z) {
            if (tLRPC$TL_account_wallPapers != null) {
                tLRPC$TL_account_wallPapers.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_account_wallPapers;
        }
        throw new RuntimeException(String.format("can't parse magic %x in account_WallPapers", Integer.valueOf(i)));
    }
}
