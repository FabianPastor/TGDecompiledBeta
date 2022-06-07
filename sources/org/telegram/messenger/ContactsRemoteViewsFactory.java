package org.telegram.messenger;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$ChatPhoto;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$FileLocation;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.tgnet.TLRPC$UserProfilePhoto;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;

/* compiled from: ContactsWidgetService */
class ContactsRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private AccountInstance accountInstance;
    private int appWidgetId;
    private RectF bitmapRect;
    private boolean deleted;
    private LongSparseArray<TLRPC$Dialog> dialogs = new LongSparseArray<>();
    private ArrayList<Long> dids = new ArrayList<>();
    private Context mContext;
    private Paint roundPaint;

    public long getItemId(int i) {
        return (long) i;
    }

    public RemoteViews getLoadingView() {
        return null;
    }

    public int getViewTypeCount() {
        return 2;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDestroy() {
    }

    public ContactsRemoteViewsFactory(Context context, Intent intent) {
        this.mContext = context;
        Theme.createDialogsResources(context);
        boolean z = false;
        this.appWidgetId = intent.getIntExtra("appWidgetId", 0);
        SharedPreferences sharedPreferences = context.getSharedPreferences("shortcut_widget", 0);
        int i = sharedPreferences.getInt("account" + this.appWidgetId, -1);
        if (i >= 0) {
            this.accountInstance = AccountInstance.getInstance(i);
        }
        StringBuilder sb = new StringBuilder();
        sb.append("deleted");
        sb.append(this.appWidgetId);
        this.deleted = (sharedPreferences.getBoolean(sb.toString(), false) || this.accountInstance == null) ? true : z;
    }

    public void onCreate() {
        ApplicationLoader.postInitApplication();
    }

    public int getCount() {
        if (this.deleted) {
            return 1;
        }
        return ((int) Math.ceil((double) (((float) this.dids.size()) / 2.0f))) + 1;
    }

    public RemoteViews getViewAt(int i) {
        TLRPC$FileLocation tLRPC$FileLocation;
        String str;
        TLRPC$Chat tLRPC$Chat;
        TLRPC$User tLRPC$User;
        int i2;
        String str2;
        Bitmap bitmap;
        AvatarDrawable avatarDrawable;
        TLRPC$UserProfilePhoto tLRPC$UserProfilePhoto;
        int i3 = i;
        if (this.deleted) {
            RemoteViews remoteViews = new RemoteViews(this.mContext.getPackageName(), NUM);
            remoteViews.setTextViewText(NUM, LocaleController.getString("WidgetLoggedOff", NUM));
            return remoteViews;
        } else if (i3 >= getCount() - 1) {
            RemoteViews remoteViews2 = new RemoteViews(this.mContext.getPackageName(), NUM);
            remoteViews2.setTextViewText(NUM, LocaleController.getString("TapToEditWidgetShort", NUM));
            Bundle bundle = new Bundle();
            bundle.putInt("appWidgetId", this.appWidgetId);
            bundle.putInt("appWidgetType", 1);
            bundle.putInt("currentAccount", this.accountInstance.getCurrentAccount());
            Intent intent = new Intent();
            intent.putExtras(bundle);
            remoteViews2.setOnClickFillInIntent(NUM, intent);
            return remoteViews2;
        } else {
            RemoteViews remoteViews3 = new RemoteViews(this.mContext.getPackageName(), NUM);
            int i4 = 0;
            while (i4 < 2) {
                int i5 = (i3 * 2) + i4;
                if (i5 >= this.dids.size()) {
                    remoteViews3.setViewVisibility(i4 == 0 ? NUM : NUM, 4);
                } else {
                    remoteViews3.setViewVisibility(i4 == 0 ? NUM : NUM, 0);
                    Long l = this.dids.get(i5);
                    if (DialogObject.isUserDialog(l.longValue())) {
                        tLRPC$User = this.accountInstance.getMessagesController().getUser(l);
                        if (UserObject.isUserSelf(tLRPC$User)) {
                            str = LocaleController.getString("SavedMessages", NUM);
                        } else if (UserObject.isReplyUser(tLRPC$User)) {
                            str = LocaleController.getString("RepliesTitle", NUM);
                        } else if (UserObject.isDeleted(tLRPC$User)) {
                            str = LocaleController.getString("HiddenName", NUM);
                        } else {
                            str = UserObject.getFirstName(tLRPC$User);
                        }
                        if (UserObject.isReplyUser(tLRPC$User) || UserObject.isUserSelf(tLRPC$User) || tLRPC$User == null || (tLRPC$UserProfilePhoto = tLRPC$User.photo) == null || (tLRPC$FileLocation = tLRPC$UserProfilePhoto.photo_small) == null || tLRPC$FileLocation.volume_id == 0 || tLRPC$FileLocation.local_id == 0) {
                            tLRPC$Chat = null;
                            tLRPC$FileLocation = null;
                        } else {
                            tLRPC$Chat = null;
                        }
                    } else {
                        TLRPC$Chat chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(-l.longValue()));
                        if (chat != null) {
                            str = chat.title;
                            TLRPC$ChatPhoto tLRPC$ChatPhoto = chat.photo;
                            if (!(tLRPC$ChatPhoto == null || (tLRPC$FileLocation = tLRPC$ChatPhoto.photo_small) == null || tLRPC$FileLocation.volume_id == 0 || tLRPC$FileLocation.local_id == 0)) {
                                tLRPC$Chat = chat;
                                tLRPC$User = null;
                            }
                        } else {
                            str = "";
                        }
                        tLRPC$Chat = chat;
                        tLRPC$User = null;
                        tLRPC$FileLocation = null;
                    }
                    remoteViews3.setTextViewText(i4 == 0 ? NUM : NUM, str);
                    if (tLRPC$FileLocation != null) {
                        try {
                            bitmap = BitmapFactory.decodeFile(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(tLRPC$FileLocation, true).toString());
                        } catch (Throwable th) {
                            FileLog.e(th);
                        }
                    } else {
                        bitmap = null;
                    }
                    int dp = AndroidUtilities.dp(48.0f);
                    Bitmap createBitmap = Bitmap.createBitmap(dp, dp, Bitmap.Config.ARGB_8888);
                    createBitmap.eraseColor(0);
                    Canvas canvas = new Canvas(createBitmap);
                    if (bitmap == null) {
                        if (tLRPC$User != null) {
                            avatarDrawable = new AvatarDrawable(tLRPC$User);
                            if (UserObject.isReplyUser(tLRPC$User)) {
                                avatarDrawable.setAvatarType(12);
                            } else if (UserObject.isUserSelf(tLRPC$User)) {
                                avatarDrawable.setAvatarType(1);
                            }
                        } else {
                            avatarDrawable = new AvatarDrawable(tLRPC$Chat);
                        }
                        avatarDrawable.setBounds(0, 0, dp, dp);
                        avatarDrawable.draw(canvas);
                    } else {
                        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                        BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
                        if (this.roundPaint == null) {
                            this.roundPaint = new Paint(1);
                            this.bitmapRect = new RectF();
                        }
                        float width = ((float) dp) / ((float) bitmap.getWidth());
                        canvas.save();
                        canvas.scale(width, width);
                        this.roundPaint.setShader(bitmapShader);
                        this.bitmapRect.set(0.0f, 0.0f, (float) bitmap.getWidth(), (float) bitmap.getHeight());
                        canvas.drawRoundRect(this.bitmapRect, (float) bitmap.getWidth(), (float) bitmap.getHeight(), this.roundPaint);
                        canvas.restore();
                    }
                    canvas.setBitmap((Bitmap) null);
                    remoteViews3.setImageViewBitmap(i4 == 0 ? NUM : NUM, createBitmap);
                    TLRPC$Dialog tLRPC$Dialog = this.dialogs.get(l.longValue());
                    int i6 = NUM;
                    if (tLRPC$Dialog == null || (i2 = tLRPC$Dialog.unread_count) <= 0) {
                        if (i4 != 0) {
                            i6 = NUM;
                        }
                        remoteViews3.setViewVisibility(i6, 8);
                    } else {
                        if (i2 > 99) {
                            str2 = String.format("%d+", new Object[]{99});
                        } else {
                            str2 = String.format("%d", new Object[]{Integer.valueOf(i2)});
                        }
                        remoteViews3.setTextViewText(i4 == 0 ? NUM : NUM, str2);
                        if (i4 != 0) {
                            i6 = NUM;
                        }
                        remoteViews3.setViewVisibility(i6, 0);
                    }
                    Bundle bundle2 = new Bundle();
                    if (DialogObject.isUserDialog(l.longValue())) {
                        bundle2.putLong("userId", l.longValue());
                    } else {
                        bundle2.putLong("chatId", -l.longValue());
                    }
                    bundle2.putInt("currentAccount", this.accountInstance.getCurrentAccount());
                    Intent intent2 = new Intent();
                    intent2.putExtras(bundle2);
                    remoteViews3.setOnClickFillInIntent(i4 == 0 ? NUM : NUM, intent2);
                }
                i4++;
            }
            return remoteViews3;
        }
    }

    public void onDataSetChanged() {
        this.dids.clear();
        AccountInstance accountInstance2 = this.accountInstance;
        if (accountInstance2 != null && accountInstance2.getUserConfig().isClientActivated()) {
            ArrayList arrayList = new ArrayList();
            ArrayList arrayList2 = new ArrayList();
            this.accountInstance.getMessagesStorage().getWidgetDialogs(this.appWidgetId, 1, this.dids, this.dialogs, new LongSparseArray(), arrayList, arrayList2);
            this.accountInstance.getMessagesController().putUsers(arrayList, true);
            this.accountInstance.getMessagesController().putChats(arrayList2, true);
        }
    }
}
