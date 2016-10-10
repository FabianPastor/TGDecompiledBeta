package org.telegram.tgnet;

import java.util.HashMap;
import org.telegram.messenger.FileLog;

public class TLClassStore
{
  static TLClassStore store = null;
  private HashMap<Integer, Class> classStore = new HashMap();
  
  public TLClassStore()
  {
    this.classStore.put(Integer.valueOf(TLRPC.TL_error.constructor), TLRPC.TL_error.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_decryptedMessageService.constructor), TLRPC.TL_decryptedMessageService.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_decryptedMessage.constructor), TLRPC.TL_decryptedMessage.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_config.constructor), TLRPC.TL_config.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_decryptedMessageLayer.constructor), TLRPC.TL_decryptedMessageLayer.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_decryptedMessage_layer17.constructor), TLRPC.TL_decryptedMessage.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_decryptedMessageService_layer8.constructor), TLRPC.TL_decryptedMessageService_layer8.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_decryptedMessage_layer8.constructor), TLRPC.TL_decryptedMessage_layer8.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_message_secret.constructor), TLRPC.TL_message_secret.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_message_secret_old.constructor), TLRPC.TL_message_secret_old.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_messageEncryptedAction.constructor), TLRPC.TL_messageEncryptedAction.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_null.constructor), TLRPC.TL_null.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_updateShortChatMessage.constructor), TLRPC.TL_updateShortChatMessage.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_updates.constructor), TLRPC.TL_updates.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_updateShortMessage.constructor), TLRPC.TL_updateShortMessage.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_updateShort.constructor), TLRPC.TL_updateShort.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_updatesCombined.constructor), TLRPC.TL_updatesCombined.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_updateShortSentMessage.constructor), TLRPC.TL_updateShortSentMessage.class);
    this.classStore.put(Integer.valueOf(TLRPC.TL_updatesTooLong.constructor), TLRPC.TL_updatesTooLong.class);
  }
  
  public static TLClassStore Instance()
  {
    if (store == null) {
      store = new TLClassStore();
    }
    return store;
  }
  
  public TLObject TLdeserialize(NativeByteBuffer paramNativeByteBuffer, int paramInt, boolean paramBoolean)
  {
    Object localObject = (Class)this.classStore.get(Integer.valueOf(paramInt));
    if (localObject != null) {
      try
      {
        localObject = (TLObject)((Class)localObject).newInstance();
        ((TLObject)localObject).readParams(paramNativeByteBuffer, paramBoolean);
        return (TLObject)localObject;
      }
      catch (Throwable paramNativeByteBuffer)
      {
        FileLog.e("tmessages", paramNativeByteBuffer);
        return null;
      }
    }
    return null;
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/tgnet/TLClassStore.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */