package org.telegram.messenger;

import android.text.TextUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.TL_userDeleted_old2;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_userSelf_old3;
import org.telegram.tgnet.TLRPC.User;

public class UserObject
{
  public static String getFirstName(TLRPC.User paramUser)
  {
    if ((paramUser == null) || (isDeleted(paramUser))) {
      paramUser = "DELETED";
    }
    for (;;)
    {
      return paramUser;
      String str1 = paramUser.first_name;
      String str2;
      if (str1 != null)
      {
        str2 = str1;
        if (str1.length() != 0) {}
      }
      else
      {
        str2 = paramUser.last_name;
      }
      paramUser = str2;
      if (TextUtils.isEmpty(str2)) {
        paramUser = LocaleController.getString("HiddenName", NUM);
      }
    }
  }
  
  public static String getUserName(TLRPC.User paramUser)
  {
    Object localObject;
    if ((paramUser == null) || (isDeleted(paramUser))) {
      localObject = LocaleController.getString("HiddenName", NUM);
    }
    for (;;)
    {
      return (String)localObject;
      String str = ContactsController.formatName(paramUser.first_name, paramUser.last_name);
      localObject = str;
      if (str.length() == 0)
      {
        localObject = str;
        if (paramUser.phone != null)
        {
          localObject = str;
          if (paramUser.phone.length() != 0) {
            localObject = PhoneFormat.getInstance().format("+" + paramUser.phone);
          }
        }
      }
    }
  }
  
  public static boolean isContact(TLRPC.User paramUser)
  {
    if ((paramUser != null) && (((paramUser instanceof TLRPC.TL_userContact_old2)) || (paramUser.contact) || (paramUser.mutual_contact))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isDeleted(TLRPC.User paramUser)
  {
    if ((paramUser == null) || ((paramUser instanceof TLRPC.TL_userDeleted_old2)) || ((paramUser instanceof TLRPC.TL_userEmpty)) || (paramUser.deleted)) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
  
  public static boolean isUserSelf(TLRPC.User paramUser)
  {
    if ((paramUser != null) && (((paramUser instanceof TLRPC.TL_userSelf_old3)) || (paramUser.self))) {}
    for (boolean bool = true;; bool = false) {
      return bool;
    }
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/UserObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */