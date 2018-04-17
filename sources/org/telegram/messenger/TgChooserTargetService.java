package org.telegram.messenger;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.service.chooser.ChooserTarget;
import android.service.chooser.ChooserTargetService;
import android.text.TextUtils;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.beta.R;
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.LaunchActivity;

@TargetApi(23)
public class TgChooserTargetService extends ChooserTargetService {
    private RectF bitmapRect;
    private Paint roundPaint;

    public List<ChooserTarget> onGetChooserTargets(ComponentName targetActivityName, IntentFilter matchedFilter) {
        int currentAccount = UserConfig.selectedAccount;
        List targets = new ArrayList();
        if (!UserConfig.getInstance(currentAccount).isClientActivated() || !MessagesController.getGlobalMainSettings().getBoolean("direct_share", true)) {
            return targets;
        }
        ImageLoader imageLoader = ImageLoader.getInstance();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        final ComponentName componentName = new ComponentName(getPackageName(), LaunchActivity.class.getCanonicalName());
        final int i = currentAccount;
        final List list = targets;
        final CountDownLatch countDownLatch2 = countDownLatch;
        MessagesStorage.getInstance(currentAccount).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                C05121 c05121 = this;
                ArrayList<Integer> dialogs = new ArrayList();
                ArrayList<Chat> chats = new ArrayList();
                ArrayList<User> users = new ArrayList();
                boolean z = true;
                int i = 0;
                try {
                    ArrayList<Integer> usersToLoad = new ArrayList();
                    usersToLoad.add(Integer.valueOf(UserConfig.getInstance(i).getClientUserId()));
                    ArrayList<Integer> chatsToLoad = new ArrayList();
                    SQLiteCursor cursor = MessagesStorage.getInstance(i).getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs ORDER BY date DESC LIMIT %d,%d", new Object[]{Integer.valueOf(0), Integer.valueOf(30)}), new Object[0]);
                    while (cursor.next()) {
                        long id = cursor.longValue(0);
                        int lower_id = (int) id;
                        int high_id = (int) (id >> 32);
                        if (lower_id != 0) {
                            if (high_id != 1) {
                                if (lower_id > 0) {
                                    if (!usersToLoad.contains(Integer.valueOf(lower_id))) {
                                        usersToLoad.add(Integer.valueOf(lower_id));
                                    }
                                } else if (!chatsToLoad.contains(Integer.valueOf(-lower_id))) {
                                    chatsToLoad.add(Integer.valueOf(-lower_id));
                                }
                                dialogs.add(Integer.valueOf(lower_id));
                                if (dialogs.size() == 8) {
                                    break;
                                }
                            }
                        }
                    }
                    cursor.dispose();
                    if (!chatsToLoad.isEmpty()) {
                        MessagesStorage.getInstance(i).getChatsInternal(TextUtils.join(",", chatsToLoad), chats);
                    }
                    if (!usersToLoad.isEmpty()) {
                        MessagesStorage.getInstance(i).getUsersInternal(TextUtils.join(",", usersToLoad), users);
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                int a = 0;
                while (a < dialogs.size()) {
                    int a2;
                    ArrayList<Integer> dialogs2;
                    Bundle extras = new Bundle();
                    Icon icon = null;
                    String name = null;
                    int id2 = ((Integer) dialogs.get(a)).intValue();
                    if (id2 > 0) {
                        int b = i;
                        while (b < users.size()) {
                            User user = (User) users.get(b);
                            if (user.id == id2) {
                                if (!user.bot) {
                                    a2 = a;
                                    extras.putLong("dialogId", (long) id2);
                                    if (!(user.photo == null || user.photo.photo_small == null)) {
                                        icon = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(user.photo.photo_small, z));
                                    }
                                    name = ContactsController.formatName(user.first_name, user.last_name);
                                }
                                a2 = a;
                            } else {
                                b++;
                            }
                        }
                        a2 = a;
                    } else {
                        a2 = a;
                        i = 0;
                        while (i < chats.size()) {
                            Chat chat = (Chat) chats.get(i);
                            if (chat.id != (-id2)) {
                                i++;
                            } else if (!ChatObject.isNotInChat(chat) && (!ChatObject.isChannel(chat) || chat.megagroup)) {
                                extras.putLong("dialogId", (long) id2);
                                if (!(chat.photo == null || chat.photo.photo_small == null)) {
                                    icon = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(chat.photo.photo_small, z));
                                }
                                name = chat.title;
                            }
                        }
                    }
                    String name2 = name;
                    if (name2 != null) {
                        Icon icon2;
                        if (icon == null) {
                            icon2 = Icon.createWithResource(ApplicationLoader.applicationContext, R.drawable.logo_avatar);
                        } else {
                            icon2 = icon;
                        }
                        List list = list;
                        ChooserTarget chooserTarget = r9;
                        dialogs2 = dialogs;
                        List list2 = list;
                        ChooserTarget chooserTarget2 = new ChooserTarget(name2, icon2, 1.0f, componentName, extras);
                        list2.add(chooserTarget);
                    } else {
                        dialogs2 = dialogs;
                    }
                    a = a2 + 1;
                    dialogs = dialogs2;
                    z = true;
                    i = 0;
                }
                countDownLatch2.countDown();
            }
        });
        try {
            countDownLatch.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return targets;
    }

    private Icon createRoundBitmap(File path) {
        try {
            Bitmap bitmap = BitmapFactory.decodeFile(path.toString());
            if (bitmap == null) {
                return null;
            }
            Bitmap result = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
            result.eraseColor(0);
            Canvas canvas = new Canvas(result);
            BitmapShader shader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
            if (this.roundPaint == null) {
                this.roundPaint = new Paint(1);
                this.bitmapRect = new RectF();
            }
            this.roundPaint.setShader(shader);
            this.bitmapRect.set(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
            canvas.drawRoundRect(this.bitmapRect, (float) bitmap.getWidth(), (float) bitmap.getHeight(), this.roundPaint);
            return Icon.createWithBitmap(result);
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
    }
}
