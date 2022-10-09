package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_Messages extends TLObject {
    public ArrayList<TLRPC$Document> animatedEmoji;
    public int count;
    public int flags;
    public boolean inexact;
    public int next_rate;
    public int offset_id_offset;
    public int pts;
    public ArrayList<TLRPC$Message> messages = new ArrayList<>();
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$messages_Messages TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_Messages tLRPC$TL_messages_messages;
        switch (i) {
            case -1938715001:
                tLRPC$TL_messages_messages = new TLRPC$TL_messages_messages();
                break;
            case 978610270:
                tLRPC$TL_messages_messages = new TLRPC$messages_Messages() { // from class: org.telegram.tgnet.TLRPC$TL_messages_messagesSlice
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        this.inexact = (readInt32 & 2) != 0;
                        this.count = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 1) != 0) {
                            this.next_rate = abstractSerializedData2.readInt32(z2);
                        }
                        if ((this.flags & 4) != 0) {
                            this.offset_id_offset = abstractSerializedData2.readInt32(z2);
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
                        int i2 = this.inexact ? this.flags | 2 : this.flags & (-3);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeInt32(this.count);
                        if ((this.flags & 1) != 0) {
                            abstractSerializedData2.writeInt32(this.next_rate);
                        }
                        if ((this.flags & 4) != 0) {
                            abstractSerializedData2.writeInt32(this.offset_id_offset);
                        }
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
                break;
            case 1682413576:
                tLRPC$TL_messages_messages = new TLRPC$messages_Messages() { // from class: org.telegram.tgnet.TLRPC$TL_messages_channelMessages
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        int readInt32 = abstractSerializedData2.readInt32(z2);
                        this.flags = readInt32;
                        this.inexact = (readInt32 & 2) != 0;
                        this.pts = abstractSerializedData2.readInt32(z2);
                        this.count = abstractSerializedData2.readInt32(z2);
                        if ((this.flags & 4) != 0) {
                            this.offset_id_offset = abstractSerializedData2.readInt32(z2);
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
                        int i2 = this.inexact ? this.flags | 2 : this.flags & (-3);
                        this.flags = i2;
                        abstractSerializedData2.writeInt32(i2);
                        abstractSerializedData2.writeInt32(this.pts);
                        abstractSerializedData2.writeInt32(this.count);
                        if ((this.flags & 4) != 0) {
                            abstractSerializedData2.writeInt32(this.offset_id_offset);
                        }
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
                break;
            case 1951620897:
                tLRPC$TL_messages_messages = new TLRPC$messages_Messages() { // from class: org.telegram.tgnet.TLRPC$TL_messages_messagesNotModified
                    public static int constructor = NUM;

                    @Override // org.telegram.tgnet.TLObject
                    public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                        this.count = abstractSerializedData2.readInt32(z2);
                    }

                    @Override // org.telegram.tgnet.TLObject
                    public void serializeToStream(AbstractSerializedData abstractSerializedData2) {
                        abstractSerializedData2.writeInt32(constructor);
                        abstractSerializedData2.writeInt32(this.count);
                    }
                };
                break;
            default:
                tLRPC$TL_messages_messages = null;
                break;
        }
        if (tLRPC$TL_messages_messages != null || !z) {
            if (tLRPC$TL_messages_messages != null) {
                tLRPC$TL_messages_messages.readParams(abstractSerializedData, z);
            }
            return tLRPC$TL_messages_messages;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_Messages", Integer.valueOf(i)));
    }
}
