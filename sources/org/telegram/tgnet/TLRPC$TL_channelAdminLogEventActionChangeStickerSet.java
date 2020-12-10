package org.telegram.tgnet;

public class TLRPC$TL_channelAdminLogEventActionChangeStickerSet extends TLRPC$ChannelAdminLogEventAction {
    public static int constructor = -NUM;
    public TLRPC$InputStickerSet new_stickerset;
    public TLRPC$InputStickerSet prev_stickerset;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        this.prev_stickerset = TLRPC$InputStickerSet.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.new_stickerset = TLRPC$InputStickerSet.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        this.prev_stickerset.serializeToStream(abstractSerializedData);
        this.new_stickerset.serializeToStream(abstractSerializedData);
    }
}
