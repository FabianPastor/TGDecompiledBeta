package org.telegram.messenger;

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
    String str1;
    do
    {
      return paramUser;
      String str2 = paramUser.first_name;
      if (str2 != null)
      {
        str1 = str2;
        if (str2.length() != 0) {}
      }
      else
      {
        str1 = paramUser.last_name;
      }
      if (str1 == null) {
        break;
      }
      paramUser = str1;
    } while (str1.length() > 0);
    return LocaleController.getString("HiddenName", 2131165729);
  }
  
  public static String getUserName(TLRPC.User paramUser)
  {
    Object localObject;
    if ((paramUser == null) || (isDeleted(paramUser))) {
      localObject = LocaleController.getString("HiddenName", 2131165729);
    }
    do
    {
      String str;
      do
      {
        do
        {
          return (String)localObject;
          str = ContactsController.formatName(paramUser.first_name, paramUser.last_name);
          localObject = str;
        } while (str.length() != 0);
        localObject = str;
      } while (paramUser.phone == null);
      localObject = str;
    } while (paramUser.phone.length() == 0);
    return PhoneFormat.getInstance().format("+" + paramUser.phone);
  }
  
  public static boolean isContact(TLRPC.User paramUser)
  {
    return ((paramUser instanceof TLRPC.TL_userContact_old2)) || (paramUser.contact) || (paramUser.mutual_contact);
  }
  
  public static boolean isDeleted(TLRPC.User paramUser)
  {
    return (paramUser == null) || ((paramUser instanceof TLRPC.TL_userDeleted_old2)) || ((paramUser instanceof TLRPC.TL_userEmpty)) || (paramUser.deleted);
  }
  
  public static boolean isUserSelf(TLRPC.User paramUser)
  {
    return ((paramUser instanceof TLRPC.TL_userSelf_old3)) || (paramUser.self);
  }
}


/* Location:              /home/fabian/Escritorio/Proyectos/Android/Dev/jardecompiler/TMessagesProj-fat-debug.jar!/org/telegram/messenger/UserObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1-SNAPSHOT-20140817
 */