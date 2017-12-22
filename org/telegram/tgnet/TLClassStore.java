package org.telegram.tgnet;

import java.util.HashMap;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.TLRPC.TL_config;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageLayer;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageService;
import org.telegram.tgnet.TLRPC.TL_decryptedMessageService_layer8;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage_layer17;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage_layer45;
import org.telegram.tgnet.TLRPC.TL_decryptedMessage_layer8;
import org.telegram.tgnet.TLRPC.TL_error;
import org.telegram.tgnet.TLRPC.TL_messageEncryptedAction;
import org.telegram.tgnet.TLRPC.TL_message_secret;
import org.telegram.tgnet.TLRPC.TL_message_secret_layer72;
import org.telegram.tgnet.TLRPC.TL_message_secret_old;
import org.telegram.tgnet.TLRPC.TL_null;
import org.telegram.tgnet.TLRPC.TL_updateShort;
import org.telegram.tgnet.TLRPC.TL_updateShortChatMessage;
import org.telegram.tgnet.TLRPC.TL_updateShortMessage;
import org.telegram.tgnet.TLRPC.TL_updateShortSentMessage;
import org.telegram.tgnet.TLRPC.TL_updates;
import org.telegram.tgnet.TLRPC.TL_updatesCombined;
import org.telegram.tgnet.TLRPC.TL_updatesTooLong;

public class TLClassStore {
    static TLClassStore store = null;
    private HashMap<Integer, Class> classStore = new HashMap();

    public TLClassStore() {
        this.classStore.put(Integer.valueOf(TL_error.constructor), TL_error.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessageService.constructor), TL_decryptedMessageService.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessage.constructor), TL_decryptedMessage.class);
        this.classStore.put(Integer.valueOf(TL_config.constructor), TL_config.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessageLayer.constructor), TL_decryptedMessageLayer.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessage_layer17.constructor), TL_decryptedMessage.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessage_layer45.constructor), TL_decryptedMessage_layer45.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessageService_layer8.constructor), TL_decryptedMessageService_layer8.class);
        this.classStore.put(Integer.valueOf(TL_decryptedMessage_layer8.constructor), TL_decryptedMessage_layer8.class);
        this.classStore.put(Integer.valueOf(TL_message_secret.constructor), TL_message_secret.class);
        this.classStore.put(Integer.valueOf(TL_message_secret_layer72.constructor), TL_message_secret_layer72.class);
        this.classStore.put(Integer.valueOf(TL_message_secret_old.constructor), TL_message_secret_old.class);
        this.classStore.put(Integer.valueOf(TL_messageEncryptedAction.constructor), TL_messageEncryptedAction.class);
        this.classStore.put(Integer.valueOf(TL_null.constructor), TL_null.class);
        this.classStore.put(Integer.valueOf(TL_updateShortChatMessage.constructor), TL_updateShortChatMessage.class);
        this.classStore.put(Integer.valueOf(TL_updates.constructor), TL_updates.class);
        this.classStore.put(Integer.valueOf(TL_updateShortMessage.constructor), TL_updateShortMessage.class);
        this.classStore.put(Integer.valueOf(TL_updateShort.constructor), TL_updateShort.class);
        this.classStore.put(Integer.valueOf(TL_updatesCombined.constructor), TL_updatesCombined.class);
        this.classStore.put(Integer.valueOf(TL_updateShortSentMessage.constructor), TL_updateShortSentMessage.class);
        this.classStore.put(Integer.valueOf(TL_updatesTooLong.constructor), TL_updatesTooLong.class);
    }

    public static TLClassStore Instance() {
        if (store == null) {
            store = new TLClassStore();
        }
        return store;
    }

    public TLObject TLdeserialize(NativeByteBuffer stream, int constructor, boolean exception) {
        Class objClass = (Class) this.classStore.get(Integer.valueOf(constructor));
        if (objClass == null) {
            return null;
        }
        try {
            TLObject response = (TLObject) objClass.newInstance();
            response.readParams(stream, exception);
            return response;
        } catch (Throwable e) {
            FileLog.e(e);
            return null;
        }
    }
}
