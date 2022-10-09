package org.telegram.tgnet;

import java.util.ArrayList;
/* loaded from: classes.dex */
public abstract class TLRPC$messages_Dialogs extends TLObject {
    public int count;
    public ArrayList<TLRPC$Dialog> dialogs = new ArrayList<>();
    public ArrayList<TLRPC$Message> messages = new ArrayList<>();
    public ArrayList<TLRPC$Chat> chats = new ArrayList<>();
    public ArrayList<TLRPC$User> users = new ArrayList<>();

    public static TLRPC$messages_Dialogs TLdeserialize(AbstractSerializedData abstractSerializedData, int i, boolean z) {
        TLRPC$messages_Dialogs tLRPC$messages_Dialogs;
        if (i == -NUM) {
            tLRPC$messages_Dialogs = new TLRPC$messages_Dialogs() { // from class: org.telegram.tgnet.TLRPC$TL_messages_dialogsNotModified
                public static int constructor = -NUM;

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
        } else if (i == NUM) {
            tLRPC$messages_Dialogs = new TLRPC$TL_messages_dialogs();
        } else {
            tLRPC$messages_Dialogs = i != NUM ? null : new TLRPC$messages_Dialogs() { // from class: org.telegram.tgnet.TLRPC$TL_messages_dialogsSlice
                public static int constructor = NUM;

                @Override // org.telegram.tgnet.TLObject
                public void readParams(AbstractSerializedData abstractSerializedData2, boolean z2) {
                    this.count = abstractSerializedData2.readInt32(z2);
                    int readInt32 = abstractSerializedData2.readInt32(z2);
                    if (readInt32 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt32)));
                        }
                        return;
                    }
                    int readInt322 = abstractSerializedData2.readInt32(z2);
                    for (int i2 = 0; i2 < readInt322; i2++) {
                        TLRPC$Dialog TLdeserialize = TLRPC$Dialog.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize == null) {
                            return;
                        }
                        this.dialogs.add(TLdeserialize);
                    }
                    int readInt323 = abstractSerializedData2.readInt32(z2);
                    if (readInt323 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt323)));
                        }
                        return;
                    }
                    int readInt324 = abstractSerializedData2.readInt32(z2);
                    for (int i3 = 0; i3 < readInt324; i3++) {
                        TLRPC$Message TLdeserialize2 = TLRPC$Message.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize2 == null) {
                            return;
                        }
                        this.messages.add(TLdeserialize2);
                    }
                    int readInt325 = abstractSerializedData2.readInt32(z2);
                    if (readInt325 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt325)));
                        }
                        return;
                    }
                    int readInt326 = abstractSerializedData2.readInt32(z2);
                    for (int i4 = 0; i4 < readInt326; i4++) {
                        TLRPC$Chat TLdeserialize3 = TLRPC$Chat.TLdeserialize(abstractSerializedData2, abstractSerializedData2.readInt32(z2), z2);
                        if (TLdeserialize3 == null) {
                            return;
                        }
                        this.chats.add(TLdeserialize3);
                    }
                    int readInt327 = abstractSerializedData2.readInt32(z2);
                    if (readInt327 != NUM) {
                        if (z2) {
                            throw new RuntimeException(String.format("wrong Vector magic, got %x", Integer.valueOf(readInt327)));
                        }
                        return;
                    }
                    int readInt328 = abstractSerializedData2.readInt32(z2);
                    for (int i5 = 0; i5 < readInt328; i5++) {
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
                    abstractSerializedData2.writeInt32(this.count);
                    abstractSerializedData2.writeInt32(NUM);
                    int size = this.dialogs.size();
                    abstractSerializedData2.writeInt32(size);
                    for (int i2 = 0; i2 < size; i2++) {
                        this.dialogs.get(i2).serializeToStream(abstractSerializedData2);
                    }
                    abstractSerializedData2.writeInt32(NUM);
                    int size2 = this.messages.size();
                    abstractSerializedData2.writeInt32(size2);
                    for (int i3 = 0; i3 < size2; i3++) {
                        this.messages.get(i3).serializeToStream(abstractSerializedData2);
                    }
                    abstractSerializedData2.writeInt32(NUM);
                    int size3 = this.chats.size();
                    abstractSerializedData2.writeInt32(size3);
                    for (int i4 = 0; i4 < size3; i4++) {
                        this.chats.get(i4).serializeToStream(abstractSerializedData2);
                    }
                    abstractSerializedData2.writeInt32(NUM);
                    int size4 = this.users.size();
                    abstractSerializedData2.writeInt32(size4);
                    for (int i5 = 0; i5 < size4; i5++) {
                        this.users.get(i5).serializeToStream(abstractSerializedData2);
                    }
                }
            };
        }
        if (tLRPC$messages_Dialogs != null || !z) {
            if (tLRPC$messages_Dialogs != null) {
                tLRPC$messages_Dialogs.readParams(abstractSerializedData, z);
            }
            return tLRPC$messages_Dialogs;
        }
        throw new RuntimeException(String.format("can't parse magic %x in messages_Dialogs", Integer.valueOf(i)));
    }
}
