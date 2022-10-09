package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$updates_ChannelDifference extends TLObject {
    public TLRPC$Dialog dialog;
    public int flags;
    public boolean isFinal;
    public int pts;
    public int timeout;
    public ArrayList<TLRPC$Message> new_messages = new ArrayList<>();
    public ArrayList<TLRPC$Update> other_updates = new ArrayList<>();
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();
    public ArrayList<TLRPC$Message> messages = new ArrayList<>();

    public static TLRPC$updates_ChannelDifference TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$updates_ChannelDifference tLRPC$updates_ChannelDifference;
        if (i == -NUM) {
            tLRPC$updates_ChannelDifference = new TLRPC$updates_ChannelDifference() { // from class: org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceTooLong
                public static int constructor = -NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    this.flags = readInt32;
                    this.isFinal = (readInt32 & 1) != 0;
                    if ((readInt32 & 2) != 0) {
                        this.timeout = abstractSerializedData2.readInt32(z2);
                    }
                    this.dialog = TLRPC$Dialog.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    if (readInt322 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                        }
                        return;
                    }
                    int readInt323 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt323; i2++) {
                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize == null) {
                            return;
                        }
                        this.messages.add(TLdeserialize);
                    }
                    int readInt324 = abstractSerializedData2.readInt32(z2);
                    if (readInt324 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                        }
                        return;
                    }
                    int readInt325 = abstractSerializedData2.readInt32(z2);
                    for (int i3 = 0; i3 < readInt325; i3++) {
                        TLRPC$Chat TLdeserialize2 = TLRPC$Chat.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize2 == null) {
                            return;
                        }
                        this.chats.add(TLdeserialize2);
                    }
                    int readInt326 = abstractSerializedData2.readInt32(z2);
                    if (readInt326 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt326)));
                        }
                        return;
                    }
                    int readInt327 = abstractSerializedData2.readInt32(z2);
                    for (int i4 = 0; i4 < readInt327; i4++) {
                        TLRPC$User TLdeserialize3 = TLRPC$User.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize3 == null) {
                            return;
                        }
                        this.users.add(TLdeserialize3);
                    }
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    int i2 = this.isFinal ? this.flags | 1 : this.flags & (-2);
                    this.flags = i2;
                    abstractSerializedData2.writeInt32(i2);
                    if ((this.flags & 2) != 0) {
                        abstractSerializedData2.writeInt32(this.timeout);
                    }
                    this.dialog.serializeToStream(abstractSerializedData2);
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.messages.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i3 = 0; i3 < size; i3++) {
                        this.messages.get(i3).serializeToStream(abstractSerializedData2);
                    }
                    abstractSerializedData2.writeInt32(NUM);
                    int size2 = this.chats.size();
                    abstractSerializedData2.writeInt32(size2);
                    for (int i4 = 0; i4 < size2; i4++) {
                        this.chats.get(i4).serializeToStream(abstractSerializedData2);
                    }
                    abstractSerializedData2.writeInt32(NUM);
                    int size3 = this.users.size();
                    abstractSerializedData2.writeInt32(size3);
                    for (int i5 = 0; i5 < size3; i5++) {
                        this.users.get(i5).serializeToStream(abstractSerializedData2);
                    }
                }
            };
        } else if (i != NUM) {
            tLRPC$updates_ChannelDifference = i != NUM ? null : new TLRPC$updates_ChannelDifference() { // from class: org.telegram.tgnet.TLRPC$TL_updates_channelDifferenceEmpty
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    this.flags = readInt32;
                    boolean z3 = true;
                    if ((readInt32 & 1) == 0) {
                        z3 = false;
                    }
                    this.isFinal = z3;
                    this.pts = abstractSerializedData2.readInt32(z2);
                    if ((this.flags & 2) != 0) {
                        this.timeout = abstractSerializedData2.readInt32(z2);
                    }
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    int i2 = this.isFinal ? this.flags | 1 : this.flags & (-2);
                    this.flags = i2;
                    abstractSerializedData2.writeInt32(i2);
                    abstractSerializedData2.writeInt32(this.pts);
                    if ((this.flags & 2) != 0) {
                        abstractSerializedData2.writeInt32(this.timeout);
                    }
                }
            };
        } else {
            tLRPC$updates_ChannelDifference = new TLRPC$updates_ChannelDifference() { // from class: org.telegram.tgnet.TLRPC$TL_updates_channelDifference
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    this.flags = readInt32;
                    this.isFinal = (readInt32 & 1) != 0;
                    this.pts = abstractSerializedData2.readInt32(z2);
                    if ((this.flags & 2) != 0) {
                        this.timeout = abstractSerializedData2.readInt32(z2);
                    }
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    if (readInt322 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt322)));
                        }
                        return;
                    }
                    int readInt323 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt323; i2++) {
                        TLRPC$Message TLdeserialize = TLRPC$Message.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize == null) {
                            return;
                        }
                        this.new_messages.add(TLdeserialize);
                    }
                    int readInt324 = abstractSerializedData2.readInt32(z2);
                    if (readInt324 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt324)));
                        }
                        return;
                    }
                    int readInt325 = abstractSerializedData2.readInt32(z2);
                    for (int i3 = 0; i3 < readInt325; i3++) {
                        TLRPC$Update TLdeserialize2 = TLRPC$Update.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize2 == null) {
                            return;
                        }
                        this.other_updates.add(TLdeserialize2);
                    }
                    int readInt326 = abstractSerializedData2.readInt32(z2);
                    if (readInt326 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt326)));
                        }
                        return;
                    }
                    int readInt327 = abstractSerializedData2.readInt32(z2);
                    for (int i4 = 0; i4 < readInt327; i4++) {
                        TLRPC$Chat TLdeserialize3 = TLRPC$Chat.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize3 == null) {
                            return;
                        }
                        this.chats.add(TLdeserialize3);
                    }
                    int readInt328 = abstractSerializedData2.readInt32(z2);
                    if (readInt328 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt328)));
                        }
                        return;
                    }
                    int readInt329 = abstractSerializedData2.readInt32(z2);
                    for (int i5 = 0; i5 < readInt329; i5++) {
                        TLRPC$User TLdeserialize4 = TLRPC$User.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize4 == null) {
                            return;
                        }
                        this.users.add(TLdeserialize4);
                    }
                }

                @Override // org.telegram.tgnet.TLObject
                public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                    abstractSerializedData2.writeInt32(constructor);
                    int i2 = this.isFinal ? this.flags | 1 : this.flags & (-2);
                    this.flags = i2;
                    abstractSerializedData2.writeInt32(i2);
                    abstractSerializedData2.writeInt32(this.pts);
                    if ((this.flags & 2) != 0) {
                        abstractSerializedData2.writeInt32(this.timeout);
                    }
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.new_messages.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i3 = 0; i3 < size; i3++) {
                        this.new_messages.get(i3).serializeToStream(abstractSerializedData2);
                    }
                    abstractSerializedData2.writeInt32(NUM);
                    int size2 = this.other_updates.size();
                    abstractSerializedData2.writeInt32(size2);
                    for (int i4 = 0; i4 < size2; i4++) {
                        this.other_updates.get(i4).serializeToStream(abstractSerializedData2);
                    }
                    abstractSerializedData2.writeInt32(NUM);
                    int size3 = this.chats.size();
                    abstractSerializedData2.writeInt32(size3);
                    for (int i5 = 0; i5 < size3; i5++) {
                        this.chats.get(i5).serializeToStream(abstractSerializedData2);
                    }
                    abstractSerializedData2.writeInt32(NUM);
                    int size4 = this.users.size();
                    abstractSerializedData2.writeInt32(size4);
                    for (int i6 = 0; i6 < size4; i6++) {
                        this.users.get(i6).serializeToStream(abstractSerializedData2);
                    }
                }
            };
        }
        if (tLRPC$updates_ChannelDifference != null || !z) {
            if (tLRPC$updates_ChannelDifference != null) {
                tLRPC$updates_ChannelDifference.readParams(abstractSerializedData, z);
            }
            return tLRPC$updates_ChannelDifference;
        }
        throw new RuntimeException(String.format("can't parse magic %x in updates_ChannelDifference", Integer.valueOf(i)));
    }
}
