package org.telegram.tgnet;

public class TLRPC$TL_channelBannedRights_layer92 extends TLObject {
    public static int constructor = NUM;
    public boolean embed_links;
    public int flags;
    public boolean send_games;
    public boolean send_gifs;
    public boolean send_inline;
    public boolean send_media;
    public boolean send_messages;
    public boolean send_stickers;
    public int until_date;
    public boolean view_messages;

    public static TLRPC$TL_channelBannedRights_layer92 TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_channelBannedRights_layer92 tLRPC$TL_channelBannedRights_layer92 = new TLRPC$TL_channelBannedRights_layer92();
            tLRPC$TL_channelBannedRights_layer92.readParams(abstractSerializedData, z);
            return tLRPC$TL_channelBannedRights_layer92;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_channelBannedRights_layer92", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = true;
        this.view_messages = (readInt32 & 1) != 0;
        this.send_messages = (this.flags & 2) != 0;
        this.send_media = (this.flags & 4) != 0;
        this.send_stickers = (this.flags & 8) != 0;
        this.send_gifs = (this.flags & 16) != 0;
        this.send_games = (this.flags & 32) != 0;
        this.send_inline = (this.flags & 64) != 0;
        if ((this.flags & 128) == 0) {
            z2 = false;
        }
        this.embed_links = z2;
        this.until_date = abstractSerializedData.readInt32(z);
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.view_messages ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.send_messages ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.send_media ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.send_stickers ? i3 | 8 : i3 & -9;
        this.flags = i4;
        int i5 = this.send_gifs ? i4 | 16 : i4 & -17;
        this.flags = i5;
        int i6 = this.send_games ? i5 | 32 : i5 & -33;
        this.flags = i6;
        int i7 = this.send_inline ? i6 | 64 : i6 & -65;
        this.flags = i7;
        int i8 = this.embed_links ? i7 | 128 : i7 & -129;
        this.flags = i8;
        abstractSerializedData.writeInt32(i8);
        abstractSerializedData.writeInt32(this.until_date);
    }
}
