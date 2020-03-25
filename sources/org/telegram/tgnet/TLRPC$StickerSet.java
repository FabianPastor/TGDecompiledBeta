package org.telegram.tgnet;

public abstract class TLRPC$StickerSet extends TLObject {
    public long access_hash;
    public boolean animated;
    public boolean archived;
    public int count;
    public int flags;
    public int hash;
    public long id;
    public boolean installed;
    public int installed_date;
    public boolean masks;
    public boolean official;
    public String short_name;
    public TLRPC$PhotoSize thumb;
    public int thumb_dc_id;
    public String title;

    public static TLRPC$StickerSet TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$TL_stickerSet tLRPC$TL_stickerSet;
        switch (i) {
            case -1482409193:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet_old();
                break;
            case -852477119:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet_layer75();
                break;
            case -290164953:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet();
                break;
            case 1434820921:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet_layer96();
                break;
            case 1787870391:
                tLRPC$TL_stickerSet = new TLRPC$TL_stickerSet_layer97();
                break;
            default:
                tLRPC$TL_stickerSet = null;
                break;
        }
        if (tLRPC$TL_stickerSet != null || !z) {
            if (tLRPC$TL_stickerSet != null) {
                tLRPC$TL_stickerSet.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_stickerSet;
        }
        throw new RuntimeException(String.format("can't parse magic %x in StickerSet", new Object[]{Integer.valueOf(i)}));
    }
}
