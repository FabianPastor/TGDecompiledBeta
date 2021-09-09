package org.telegram.tgnet;

public class TLRPC$TL_channel_layer92 extends TLRPC$TL_channel {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.creator = (readInt32 & 1) != 0;
        this.left = (readInt32 & 4) != 0;
        this.broadcast = (readInt32 & 32) != 0;
        this.verified = (readInt32 & 128) != 0;
        this.megagroup = (readInt32 & 256) != 0;
        this.restricted = (readInt32 & 512) != 0;
        this.signatures = (readInt32 & 2048) != 0;
        if ((readInt32 & 4096) != 0) {
            z2 = true;
        }
        this.min = z2;
        this.id = (long) abstractSerializedData.readInt32(z);
        if ((this.flags & 8192) != 0) {
            this.access_hash = abstractSerializedData.readInt64(z);
        }
        this.title = abstractSerializedData.readString(z);
        if ((this.flags & 64) != 0) {
            this.username = abstractSerializedData.readString(z);
        }
        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.date = abstractSerializedData.readInt32(z);
        this.version = abstractSerializedData.readInt32(z);
        if ((this.flags & 512) != 0) {
            abstractSerializedData.readString(z);
        }
        if ((this.flags & 16384) != 0) {
            TLRPC$TL_channelAdminRights_layer92 TLdeserialize = TLRPC$TL_channelAdminRights_layer92.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.admin_rights_layer92 = TLdeserialize;
            this.admin_rights = TLRPC$Chat.mergeAdminRights(TLdeserialize);
        }
        if ((this.flags & 32768) != 0) {
            TLRPC$TL_channelBannedRights_layer92 TLdeserialize2 = TLRPC$TL_channelBannedRights_layer92.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
            this.banned_rights_layer92 = TLdeserialize2;
            this.banned_rights = TLRPC$Chat.mergeBannedRights(TLdeserialize2);
        }
        if ((this.flags & 131072) != 0) {
            this.participants_count = abstractSerializedData.readInt32(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.creator ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.left ? i | 4 : i & -5;
        this.flags = i2;
        int i3 = this.broadcast ? i2 | 32 : i2 & -33;
        this.flags = i3;
        int i4 = this.verified ? i3 | 128 : i3 & -129;
        this.flags = i4;
        int i5 = this.megagroup ? i4 | 256 : i4 & -257;
        this.flags = i5;
        int i6 = this.restricted ? i5 | 512 : i5 & -513;
        this.flags = i6;
        int i7 = this.signatures ? i6 | 2048 : i6 & -2049;
        this.flags = i7;
        int i8 = this.min ? i7 | 4096 : i7 & -4097;
        this.flags = i8;
        abstractSerializedData.writeInt32(i8);
        abstractSerializedData.writeInt32((int) this.id);
        if ((this.flags & 8192) != 0) {
            abstractSerializedData.writeInt64(this.access_hash);
        }
        abstractSerializedData.writeString(this.title);
        if ((this.flags & 64) != 0) {
            abstractSerializedData.writeString(this.username);
        }
        this.photo.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.version);
        if ((this.flags & 512) != 0) {
            abstractSerializedData.writeString("");
        }
        if ((this.flags & 16384) != 0) {
            this.admin_rights_layer92.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 32768) != 0) {
            this.banned_rights_layer92.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 131072) != 0) {
            abstractSerializedData.writeInt32(this.participants_count);
        }
    }
}
