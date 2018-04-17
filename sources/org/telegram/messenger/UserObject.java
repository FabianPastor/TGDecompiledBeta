package org.telegram.messenger;

import android.text.TextUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.TL_userContact_old2;
import org.telegram.tgnet.TLRPC.TL_userDeleted_old2;
import org.telegram.tgnet.TLRPC.TL_userEmpty;
import org.telegram.tgnet.TLRPC.TL_userSelf_old3;
import org.telegram.tgnet.TLRPC.User;

public class UserObject {
    public static boolean isDeleted(User user) {
        if (!(user == null || (user instanceof TL_userDeleted_old2) || (user instanceof TL_userEmpty))) {
            if (!user.deleted) {
                return false;
            }
        }
        return true;
    }

    public static boolean isContact(User user) {
        return user != null && ((user instanceof TL_userContact_old2) || user.contact || user.mutual_contact);
    }

    public static boolean isUserSelf(User user) {
        return user != null && ((user instanceof TL_userSelf_old3) || user.self);
    }

    public static String getUserName(User user) {
        if (user != null) {
            if (!isDeleted(user)) {
                String format;
                String name = ContactsController.formatName(user.first_name, user.last_name);
                if (name.length() == 0 && user.phone != null) {
                    if (user.phone.length() != 0) {
                        PhoneFormat instance = PhoneFormat.getInstance();
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("+");
                        stringBuilder.append(user.phone);
                        format = instance.format(stringBuilder.toString());
                        return format;
                    }
                }
                format = name;
                return format;
            }
        }
        return LocaleController.getString("HiddenName", R.string.HiddenName);
    }

    public static String getFirstName(User user) {
        if (user != null) {
            if (!isDeleted(user)) {
                String name = user.first_name;
                if (name == null || name.length() == 0) {
                    name = user.last_name;
                }
                return !TextUtils.isEmpty(name) ? name : LocaleController.getString("HiddenName", R.string.HiddenName);
            }
        }
        return "DELETED";
    }
}
