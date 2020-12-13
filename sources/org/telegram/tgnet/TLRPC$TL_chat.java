package org.telegram.tgnet;

public class TLRPC$TL_chat extends TLRPC$Chat {
    public static int constructor = NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.creator = (readInt32 & 1) != 0;
        this.kicked = (readInt32 & 2) != 0;
        this.left = (readInt32 & 4) != 0;
        this.deactivated = (readInt32 & 32) != 0;
        this.call_active = (8388608 & readInt32) != 0;
        if ((readInt32 & 16777216) != 0) {
            z2 = true;
        }
        this.call_not_empty = z2;
        this.id = abstractSerializedData.readInt32(z);
        this.title = abstractSerializedData.readString(z);
        this.photo = TLRPC$ChatPhoto.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        this.participants_count = abstractSerializedData.readInt32(z);
        this.date = abstractSerializedData.readInt32(z);
        this.version = abstractSerializedData.readInt32(z);
        if ((this.flags & 64) != 0) {
            this.migrated_to = TLRPC$InputChannel.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 16384) != 0) {
            this.admin_rights = TLRPC$TL_chatAdminRights.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 262144) != 0) {
            this.default_banned_rights = TLRPC$TL_chatBannedRights.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.creator ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.kicked ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.left ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.deactivated ? i3 | 32 : i3 & -33;
        this.flags = i4;
        int i5 = this.call_active ? i4 | 8388608 : i4 & -8388609;
        this.flags = i5;
        int i6 = this.call_not_empty ? i5 | 16777216 : i5 & -16777217;
        this.flags = i6;
        abstractSerializedData.writeInt32(i6);
        abstractSerializedData.writeInt32(this.id);
        abstractSerializedData.writeString(this.title);
        this.photo.serializeToStream(abstractSerializedData);
        abstractSerializedData.writeInt32(this.participants_count);
        abstractSerializedData.writeInt32(this.date);
        abstractSerializedData.writeInt32(this.version);
        if ((this.flags & 64) != 0) {
            this.migrated_to.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 16384) != 0) {
            this.admin_rights.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 262144) != 0) {
            this.default_banned_rights.serializeToStream(abstractSerializedData);
        }
    }
}
