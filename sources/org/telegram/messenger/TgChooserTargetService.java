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
import android.graphics.Shader;
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
import org.telegram.tgnet.TLRPC.Chat;
import org.telegram.tgnet.TLRPC.User;
import org.telegram.ui.LaunchActivity;

@TargetApi(23)
public class TgChooserTargetService extends ChooserTargetService {
    private RectF bitmapRect;
    private Paint roundPaint;

    public List<ChooserTarget> onGetChooserTargets(ComponentName componentName, IntentFilter intentFilter) {
        final int i = UserConfig.selectedAccount;
        componentName = new ArrayList();
        if (UserConfig.getInstance(i).isClientActivated() == null || MessagesController.getGlobalMainSettings().getBoolean("direct_share", true) == null) {
            return componentName;
        }
        ImageLoader.getInstance();
        intentFilter = new CountDownLatch(1);
        final ComponentName componentName2 = new ComponentName(getPackageName(), LaunchActivity.class.getCanonicalName());
        final ComponentName componentName3 = componentName;
        final IntentFilter intentFilter2 = intentFilter;
        MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new Runnable() {
            public void run() {
                int i;
                ArrayList arrayList = new ArrayList();
                ArrayList arrayList2 = new ArrayList();
                ArrayList arrayList3 = new ArrayList();
                try {
                    Iterable arrayList4 = new ArrayList();
                    arrayList4.add(Integer.valueOf(UserConfig.getInstance(i).getClientUserId()));
                    Iterable arrayList5 = new ArrayList();
                    SQLiteCursor queryFinalized = MessagesStorage.getInstance(i).getDatabase().queryFinalized(String.format(Locale.US, "SELECT did FROM dialogs ORDER BY date DESC LIMIT %d,%d", new Object[]{Integer.valueOf(0), Integer.valueOf(30)}), new Object[0]);
                    while (queryFinalized.next()) {
                        long longValue = queryFinalized.longValue(0);
                        int i2 = (int) longValue;
                        i = (int) (longValue >> 32);
                        if (i2 != 0) {
                            if (i != 1) {
                                if (i2 <= 0) {
                                    i = -i2;
                                    if (!arrayList5.contains(Integer.valueOf(i))) {
                                        arrayList5.add(Integer.valueOf(i));
                                    }
                                } else if (!arrayList4.contains(Integer.valueOf(i2))) {
                                    arrayList4.add(Integer.valueOf(i2));
                                }
                                arrayList.add(Integer.valueOf(i2));
                                if (arrayList.size() == 8) {
                                    break;
                                }
                            }
                        }
                    }
                    queryFinalized.dispose();
                    if (!arrayList5.isEmpty()) {
                        MessagesStorage.getInstance(i).getChatsInternal(TextUtils.join(",", arrayList5), arrayList2);
                    }
                    if (!arrayList4.isEmpty()) {
                        MessagesStorage.getInstance(i).getUsersInternal(TextUtils.join(",", arrayList4), arrayList3);
                    }
                } catch (Throwable e) {
                    FileLog.m3e(e);
                }
                for (int i3 = 0; i3 < arrayList.size(); i3++) {
                    String formatName;
                    CharSequence charSequence;
                    Bundle bundle = new Bundle();
                    int intValue = ((Integer) arrayList.get(i3)).intValue();
                    Icon icon = null;
                    if (intValue > 0) {
                        i = 0;
                        while (i < arrayList3.size()) {
                            User user = (User) arrayList3.get(i);
                            if (user.id == intValue) {
                                if (!user.bot) {
                                    bundle.putLong("dialogId", (long) intValue);
                                    if (!(user.photo == null || user.photo.photo_small == null)) {
                                        icon = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(user.photo.photo_small, true));
                                    }
                                    formatName = ContactsController.formatName(user.first_name, user.last_name);
                                }
                                formatName = null;
                            } else {
                                i++;
                            }
                        }
                        formatName = null;
                    } else {
                        i = 0;
                        while (i < arrayList2.size()) {
                            Chat chat = (Chat) arrayList2.get(i);
                            if (chat.id == (-intValue)) {
                                if (!ChatObject.isNotInChat(chat) && (!ChatObject.isChannel(chat) || chat.megagroup)) {
                                    bundle.putLong("dialogId", (long) intValue);
                                    if (!(chat.photo == null || chat.photo.photo_small == null)) {
                                        icon = TgChooserTargetService.this.createRoundBitmap(FileLoader.getPathToAttach(chat.photo.photo_small, true));
                                    }
                                    formatName = chat.title;
                                }
                                charSequence = null;
                                if (charSequence != null) {
                                    componentName3.add(new ChooserTarget(charSequence, icon == null ? Icon.createWithResource(ApplicationLoader.applicationContext, C0446R.drawable.logo_avatar) : icon, 1.0f, componentName2, bundle));
                                }
                            } else {
                                i++;
                            }
                        }
                        charSequence = null;
                        if (charSequence != null) {
                            if (icon == null) {
                            }
                            componentName3.add(new ChooserTarget(charSequence, icon == null ? Icon.createWithResource(ApplicationLoader.applicationContext, C0446R.drawable.logo_avatar) : icon, 1.0f, componentName2, bundle));
                        }
                    }
                    charSequence = formatName;
                    if (charSequence != null) {
                        if (icon == null) {
                        }
                        componentName3.add(new ChooserTarget(charSequence, icon == null ? Icon.createWithResource(ApplicationLoader.applicationContext, C0446R.drawable.logo_avatar) : icon, 1.0f, componentName2, bundle));
                    }
                }
                intentFilter2.countDown();
            }
        });
        try {
            intentFilter.await();
        } catch (Throwable e) {
            FileLog.m3e(e);
        }
        return componentName;
    }

    private Icon createRoundBitmap(File file) {
        try {
            file = BitmapFactory.decodeFile(file.toString());
            if (file != null) {
                Bitmap createBitmap = Bitmap.createBitmap(file.getWidth(), file.getHeight(), Config.ARGB_8888);
                createBitmap.eraseColor(0);
                Canvas canvas = new Canvas(createBitmap);
                Shader bitmapShader = new BitmapShader(file, TileMode.CLAMP, TileMode.CLAMP);
                if (this.roundPaint == null) {
                    this.roundPaint = new Paint(1);
                    this.bitmapRect = new RectF();
                }
                this.roundPaint.setShader(bitmapShader);
                this.bitmapRect.set(0.0f, 0.0f, (float) file.getWidth(), (float) file.getHeight());
                canvas.drawRoundRect(this.bitmapRect, (float) file.getWidth(), (float) file.getHeight(), this.roundPaint);
                return Icon.createWithBitmap(createBitmap);
            }
        } catch (Throwable th) {
            FileLog.m3e(th);
        }
        return null;
    }
}
