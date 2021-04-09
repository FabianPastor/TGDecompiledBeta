package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$Dialog;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CounterView;
import org.telegram.ui.Components.LayoutHelper;

public class HintDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    CounterView counterView;
    private int currentAccount;
    private TLRPC$User currentUser;
    private long dialog_id;
    private BackupImageView imageView;
    private int lastUnreadCount;
    private TextView nameTextView;
    float showOnlineProgress;
    boolean wasDraw;

    public HintDialogCell(Context context) {
        super(context);
        new RectF();
        this.currentAccount = UserConfig.selectedAccount;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(27.0f));
        addView(this.imageView, LayoutHelper.createFrame(54, 54.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(1);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 64.0f, 6.0f, 0.0f));
        CounterView counterView2 = new CounterView(context);
        this.counterView = counterView2;
        addView(counterView2, LayoutHelper.createFrame(-1, 28.0f, 48, 0.0f, 4.0f, 0.0f, 0.0f));
        this.counterView.setColors("chats_unreadCounterText", "chats_unreadCounter");
        this.counterView.setGravity(5);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), NUM));
        this.counterView.horizontalPadding = (float) AndroidUtilities.dp(13.0f);
    }

    public void update(int i) {
        int i2;
        if (!((i & 4) == 0 || this.currentUser == null)) {
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(this.currentUser.id));
            this.imageView.invalidate();
            invalidate();
        }
        if (i == 0 || (i & 256) != 0 || (i & 2048) != 0) {
            TLRPC$Dialog tLRPC$Dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialog_id);
            if (tLRPC$Dialog == null || (i2 = tLRPC$Dialog.unread_count) == 0) {
                this.lastUnreadCount = 0;
                this.counterView.setCount(0, this.wasDraw);
            } else if (this.lastUnreadCount != i2) {
                this.lastUnreadCount = i2;
                this.counterView.setCount(i2, this.wasDraw);
            }
        }
    }

    public void update() {
        int i = (int) this.dialog_id;
        if (i > 0) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            this.currentUser = user;
            this.avatarDrawable.setInfo(user);
            return;
        }
        this.avatarDrawable.setInfo(MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i)));
        this.currentUser = null;
    }

    public void setDialog(int i, boolean z, CharSequence charSequence) {
        long j = (long) i;
        if (this.dialog_id != j) {
            this.wasDraw = false;
            invalidate();
        }
        this.dialog_id = j;
        if (i > 0) {
            TLRPC$User user = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            this.currentUser = user;
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (user != null) {
                this.nameTextView.setText(UserObject.getFirstName(user));
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(this.currentUser);
            this.imageView.setImage(ImageLocation.getForUserOrChat(this.currentUser, 1), "50_50", ImageLocation.getForUserOrChat(this.currentUser, 2), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentUser);
        } else {
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(chat);
            this.currentUser = null;
            this.imageView.setImage(ImageLocation.getForUserOrChat(chat, 1), "50_50", ImageLocation.getForUserOrChat(chat, 2), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
        }
        if (z) {
            update(0);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0011, code lost:
        r7 = r7.status;
     */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x007d  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean drawChild(android.graphics.Canvas r6, android.view.View r7, long r8) {
        /*
            r5 = this;
            boolean r8 = super.drawChild(r6, r7, r8)
            org.telegram.ui.Components.BackupImageView r9 = r5.imageView
            if (r7 != r9) goto L_0x00c6
            org.telegram.tgnet.TLRPC$User r7 = r5.currentUser
            r9 = 1
            if (r7 == 0) goto L_0x003b
            boolean r0 = r7.bot
            if (r0 != 0) goto L_0x003b
            org.telegram.tgnet.TLRPC$UserStatus r7 = r7.status
            if (r7 == 0) goto L_0x0023
            int r7 = r7.expires
            int r0 = r5.currentAccount
            org.telegram.tgnet.ConnectionsManager r0 = org.telegram.tgnet.ConnectionsManager.getInstance(r0)
            int r0 = r0.getCurrentTime()
            if (r7 > r0) goto L_0x0039
        L_0x0023:
            int r7 = r5.currentAccount
            org.telegram.messenger.MessagesController r7 = org.telegram.messenger.MessagesController.getInstance(r7)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r7 = r7.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r0 = r5.currentUser
            int r0 = r0.id
            java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
            boolean r7 = r7.containsKey(r0)
            if (r7 == 0) goto L_0x003b
        L_0x0039:
            r7 = 1
            goto L_0x003c
        L_0x003b:
            r7 = 0
        L_0x003c:
            boolean r0 = r5.wasDraw
            r1 = 1065353216(0x3var_, float:1.0)
            r2 = 0
            if (r0 != 0) goto L_0x004b
            if (r7 == 0) goto L_0x0048
            r0 = 1065353216(0x3var_, float:1.0)
            goto L_0x0049
        L_0x0048:
            r0 = 0
        L_0x0049:
            r5.showOnlineProgress = r0
        L_0x004b:
            r0 = 1037726734(0x3dda740e, float:0.10666667)
            if (r7 == 0) goto L_0x0063
            float r3 = r5.showOnlineProgress
            int r4 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r4 == 0) goto L_0x0063
            float r3 = r3 + r0
            r5.showOnlineProgress = r3
            int r7 = (r3 > r1 ? 1 : (r3 == r1 ? 0 : -1))
            if (r7 <= 0) goto L_0x005f
            r5.showOnlineProgress = r1
        L_0x005f:
            r5.invalidate()
            goto L_0x0077
        L_0x0063:
            if (r7 != 0) goto L_0x0077
            float r7 = r5.showOnlineProgress
            int r1 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r1 == 0) goto L_0x0077
            float r7 = r7 - r0
            r5.showOnlineProgress = r7
            int r7 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r7 >= 0) goto L_0x0074
            r5.showOnlineProgress = r2
        L_0x0074:
            r5.invalidate()
        L_0x0077:
            float r7 = r5.showOnlineProgress
            int r7 = (r7 > r2 ? 1 : (r7 == r2 ? 0 : -1))
            if (r7 == 0) goto L_0x00c4
            r7 = 1112801280(0x42540000, float:53.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            r0 = 1114374144(0x426CLASSNAME, float:59.0)
            int r0 = org.telegram.messenger.AndroidUtilities.dp(r0)
            r6.save()
            float r1 = r5.showOnlineProgress
            float r0 = (float) r0
            float r7 = (float) r7
            r6.scale(r1, r1, r0, r7)
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r2 = "windowBackgroundWhite"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            r1 = 1088421888(0x40e00000, float:7.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r6.drawCircle(r0, r7, r1, r2)
            android.graphics.Paint r1 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r2 = "chats_onlineCircle"
            int r2 = org.telegram.ui.ActionBar.Theme.getColor(r2)
            r1.setColor(r2)
            r1 = 1084227584(0x40a00000, float:5.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            float r1 = (float) r1
            android.graphics.Paint r2 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r6.drawCircle(r0, r7, r1, r2)
            r6.restore()
        L_0x00c4:
            r5.wasDraw = r9
        L_0x00c6:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.HintDialogCell.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
    }
}
