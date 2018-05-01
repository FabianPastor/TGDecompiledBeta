package org.telegram.messenger;

import org.telegram.tgnet.TLRPC.TL_dialog;

public class DialogObject
{
  public static boolean isChannel(TLRPC.TL_dialog paramTL_dialog)
  {
    if ((paramTL_dialog != null) && ((paramTL_dialog.flags & 0x1) != 0)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/DialogObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */