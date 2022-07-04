package org.telegram.messenger;

import android.text.TextUtils;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.tgnet.TLRPC;

public class UserObject {
    public static boolean isDeleted(TLRPC.User user) {
        return user == null || (user instanceof TLRPC.TL_userDeleted_old2) || (user instanceof TLRPC.TL_userEmpty) || user.deleted;
    }

    public static boolean isContact(TLRPC.User user) {
        return user != null && ((user instanceof TLRPC.TL_userContact_old2) || user.contact || user.mutual_contact);
    }

    public static boolean isUserSelf(TLRPC.User user) {
        return user != null && ((user instanceof TLRPC.TL_userSelf_old3) || user.self);
    }

    public static boolean isReplyUser(TLRPC.User user) {
        return user != null && (user.id == 708513 || user.id == NUM);
    }

    public static boolean isReplyUser(long did) {
        return did == 708513 || did == NUM;
    }

    public static String getUserName(TLRPC.User user) {
        if (user == null || isDeleted(user)) {
            return LocaleController.getString("HiddenName", NUM);
        }
        String name = ContactsController.formatName(user.first_name, user.last_name);
        if (name.length() != 0 || TextUtils.isEmpty(user.phone)) {
            return name;
        }
        PhoneFormat instance = PhoneFormat.getInstance();
        return instance.format("+" + user.phone);
    }

    public static String getFirstName(TLRPC.User user) {
        return getFirstName(user, true);
    }

    public static String getFirstName(TLRPC.User user, boolean allowShort) {
        if (user == null || isDeleted(user)) {
            return "DELETED";
        }
        String name = user.first_name;
        if (TextUtils.isEmpty(name)) {
            name = user.last_name;
        } else if (!allowShort && name.length() <= 2) {
            return ContactsController.formatName(user.first_name, user.last_name);
        }
        return !TextUtils.isEmpty(name) ? name : LocaleController.getString("HiddenName", NUM);
    }

    public static boolean hasPhoto(TLRPC.User user) {
        return (user == null || user.photo == null || (user.photo instanceof TLRPC.TL_userProfilePhotoEmpty)) ? false : true;
    }

    public static TLRPC.UserProfilePhoto getPhoto(TLRPC.User user) {
        if (hasPhoto(user)) {
            return user.photo;
        }
        return null;
    }
}
