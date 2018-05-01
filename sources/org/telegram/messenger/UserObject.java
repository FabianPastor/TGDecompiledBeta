package org.telegram.messenger;

import android.text.TextUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.TL_userDeleted_old2;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_userSelf_old3;
import org.telegram.tgnet.TLRPC.User;

public class UserObject {
    public static boolean isDeleted(User user) {
        if (!(user == null || (user instanceof TL_userDeleted_old2) || (user instanceof TL_userEmpty))) {
            if (user.deleted == null) {
                return null;
            }
        }
        return true;
    }

    public static boolean isContact(User user) {
        return (user == null || !((user instanceof TL_userContact_old2) || user.contact || user.mutual_contact != null)) ? null : true;
    }

    public static boolean isUserSelf(User user) {
        return (user == null || (!(user instanceof TL_userSelf_old3) && user.self == null)) ? null : true;
    }

    public static String getUserName(User user) {
        if (user != null) {
            if (!isDeleted(user)) {
                String formatName = ContactsController.formatName(user.first_name, user.last_name);
                if (formatName.length() == 0 && user.phone != null) {
                    if (user.phone.length() != 0) {
                        PhoneFormat instance = PhoneFormat.getInstance();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("+");
                        stringBuilder.append(user.phone);
                        formatName = instance.format(stringBuilder.toString());
                    }
                }
                return formatName;
            }
        }
        return LocaleController.getString("HiddenName", C0446R.string.HiddenName);
    }

    public static String getFirstName(User user) {
        if (user != null) {
            if (!isDeleted(user)) {
                String str = user.first_name;
                if (str == null || str.length() == 0) {
                    str = user.last_name;
                }
                if (TextUtils.isEmpty(str) != null) {
                    str = LocaleController.getString("HiddenName", C0446R.string.HiddenName);
                }
                return str;
            }
        }
        return "DELETED";
    }
}
