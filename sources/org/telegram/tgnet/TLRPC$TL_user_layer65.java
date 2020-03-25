package org.telegram.tgnet;

public class TLRPC$TL_user_layer65 extends TLRPC$TL_user {
    public static int constructor = -NUM;

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.self = (readInt32 & 1024) != 0;
        this.contact = (this.flags & 2048) != 0;
        this.mutual_contact = (this.flags & 4096) != 0;
        this.deleted = (this.flags & 8192) != 0;
        this.bot = (this.flags & 16384) != 0;
        this.bot_chat_history = (this.flags & 32768) != 0;
        this.bot_nochats = (this.flags & 65536) != 0;
        this.verified = (this.flags & 131072) != 0;
        this.restricted = (this.flags & 262144) != 0;
        this.min = (this.flags & 1048576) != 0;
        if ((this.flags & 2097152) != 0) {
            z2 = true;
        }
        this.bot_inline_geo = z2;
        this.id = abstractSerializedData.readInt32(z);
        if ((this.flags & 1) != 0) {
            this.access_hash = abstractSerializedData.readInt64(z);
        }
        if ((this.flags & 2) != 0) {
            this.first_name = abstractSerializedData.readString(z);
        }
        if ((this.flags & 4) != 0) {
            this.last_name = abstractSerializedData.readString(z);
        }
        if ((this.flags & 8) != 0) {
            this.username = abstractSerializedData.readString(z);
        }
        if ((this.flags & 16) != 0) {
            this.phone = abstractSerializedData.readString(z);
        }
        if ((this.flags & 32) != 0) {
            this.photo = TLRPC$UserProfilePhoto.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 64) != 0) {
            this.status = TLRPC$UserStatus.TLdeserialize(abstractSerializedData, abstractSerializedData.readInt32(z), z);
        }
        if ((this.flags & 16384) != 0) {
            this.bot_info_version = abstractSerializedData.readInt32(z);
        }
        if ((this.flags & 262144) != 0) {
            abstractSerializedData.readString(z);
        }
        if ((this.flags & 524288) != 0) {
            this.bot_inline_placeholder = abstractSerializedData.readString(z);
        }
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.self ? this.flags | 1024 : this.flags & -1025;
        this.flags = i;
        int i2 = this.contact ? i | 2048 : i & -2049;
        this.flags = i2;
        int i3 = this.mutual_contact ? i2 | 4096 : i2 & -4097;
        this.flags = i3;
        int i4 = this.deleted ? i3 | 8192 : i3 & -8193;
        this.flags = i4;
        int i5 = this.bot ? i4 | 16384 : i4 & -16385;
        this.flags = i5;
        int i6 = this.bot_chat_history ? i5 | 32768 : i5 & -32769;
        this.flags = i6;
        int i7 = this.bot_nochats ? i6 | 65536 : i6 & -65537;
        this.flags = i7;
        int i8 = this.verified ? i7 | 131072 : i7 & -131073;
        this.flags = i8;
        int i9 = this.restricted ? i8 | 262144 : i8 & -262145;
        this.flags = i9;
        int i10 = this.min ? i9 | 1048576 : i9 & -1048577;
        this.flags = i10;
        int i11 = this.bot_inline_geo ? i10 | 2097152 : i10 & -2097153;
        this.flags = i11;
        abstractSerializedData.writeInt32(i11);
        abstractSerializedData.writeInt32(this.id);
        if ((this.flags & 1) != 0) {
            abstractSerializedData.writeInt64(this.access_hash);
        }
        if ((this.flags & 2) != 0) {
            abstractSerializedData.writeString(this.first_name);
        }
        if ((this.flags & 4) != 0) {
            abstractSerializedData.writeString(this.last_name);
        }
        if ((this.flags & 8) != 0) {
            abstractSerializedData.writeString(this.username);
        }
        if ((this.flags & 16) != 0) {
            abstractSerializedData.writeString(this.phone);
        }
        if ((this.flags & 32) != 0) {
            this.photo.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 64) != 0) {
            this.status.serializeToStream(abstractSerializedData);
        }
        if ((this.flags & 16384) != 0) {
            abstractSerializedData.writeInt32(this.bot_info_version);
        }
        if ((this.flags & 262144) != 0) {
            abstractSerializedData.writeString("");
        }
        if ((this.flags & 524288) != 0) {
            abstractSerializedData.writeString(this.bot_inline_placeholder);
        }
    }
}
