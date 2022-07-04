package org.telegram.tgnet;

public class TLRPC$TL_channelAdminRights_layer92 extends TLObject {
    public static int constructor = NUM;
    public boolean add_admins;
    public boolean ban_users;
    public boolean change_info;
    public boolean delete_messages;
    public boolean edit_messages;
    public int flags;
    public boolean invite_users;
    public boolean manage_call;
    public boolean pin_messages;
    public boolean post_messages;

    public static TLRPC$TL_channelAdminRights_layer92 TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        if (constructor == i) {
            TLRPC$TL_channelAdminRights_layer92 tLRPC$TL_channelAdminRights_layer92 = new TLRPC$TL_channelAdminRights_layer92();
            tLRPC$TL_channelAdminRights_layer92.readParams(abstractSerializedData, z);
            return tLRPC$TL_channelAdminRights_layer92;
        } else if (!z) {
            return null;
        } else {
            throw new RuntimeException(String.format("can't parse magic %x in TL_channelAdminRights_layer92", new Object[]{Integer.valueOf(i)}));
        }
    }

    public void readParams(AbstractSerializedData abstractSerializedData, boolean z) {
        int readInt32 = abstractSerializedData.readInt32(z);
        this.flags = readInt32;
        boolean z2 = false;
        this.change_info = (readInt32 & 1) != 0;
        this.post_messages = (readInt32 & 2) != 0;
        this.edit_messages = (readInt32 & 4) != 0;
        this.delete_messages = (readInt32 & 8) != 0;
        this.ban_users = (readInt32 & 16) != 0;
        this.invite_users = (readInt32 & 32) != 0;
        this.pin_messages = (readInt32 & 128) != 0;
        this.add_admins = (readInt32 & 512) != 0;
        if ((readInt32 & 1024) != 0) {
            z2 = true;
        }
        this.manage_call = z2;
    }

    public void serializeToStream(AbstractSerializedData abstractSerializedData) {
        abstractSerializedData.writeInt32(constructor);
        int i = this.change_info ? this.flags | 1 : this.flags & -2;
        this.flags = i;
        int i2 = this.post_messages ? i | 2 : i & -3;
        this.flags = i2;
        int i3 = this.edit_messages ? i2 | 4 : i2 & -5;
        this.flags = i3;
        int i4 = this.delete_messages ? i3 | 8 : i3 & -9;
        this.flags = i4;
        int i5 = this.ban_users ? i4 | 16 : i4 & -17;
        this.flags = i5;
        int i6 = this.invite_users ? i5 | 32 : i5 & -33;
        this.flags = i6;
        int i7 = this.pin_messages ? i6 | 128 : i6 & -129;
        this.flags = i7;
        int i8 = this.add_admins ? i7 | 512 : i7 & -513;
        this.flags = i8;
        int i9 = this.manage_call ? i8 | 1024 : i8 & -1025;
        this.flags = i9;
        abstractSerializedData.writeInt32(i9);
    }
}
