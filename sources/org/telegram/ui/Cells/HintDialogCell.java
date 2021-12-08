package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CounterView;
import org.telegram.ui.Components.LayoutHelper;

public class HintDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    CheckBox2 checkBox;
    CounterView counterView;
    private int currentAccount = UserConfig.selectedAccount;
    private TLRPC.User currentUser;
    private long dialogId;
    private final boolean drawCheckbox;
    private BackupImageView imageView;
    private int lastUnreadCount;
    private TextView nameTextView;
    private RectF rect = new RectF();
    float showOnlineProgress;
    boolean wasDraw;

    public HintDialogCell(Context context, boolean drawCheckbox2) {
        super(context);
        this.drawCheckbox = drawCheckbox2;
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
        CounterView counterView2 = new CounterView(context, (Theme.ResourcesProvider) null);
        this.counterView = counterView2;
        addView(counterView2, LayoutHelper.createFrame(-1, 28.0f, 48, 0.0f, 4.0f, 0.0f, 0.0f));
        this.counterView.setColors("chats_unreadCounterText", "chats_unreadCounter");
        this.counterView.setGravity(5);
        if (drawCheckbox2) {
            CheckBox2 checkBox2 = new CheckBox2(context, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor("dialogRoundCheckBox", "dialogBackground", "dialogRoundCheckBoxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(4);
            this.checkBox.setProgressDelegate(new HintDialogCell$$ExternalSyntheticLambda0(this));
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 49, 19.0f, 42.0f, 0.0f, 0.0f));
            this.checkBox.setChecked(true, false);
            setWillNotDraw(false);
        }
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-HintDialogCell  reason: not valid java name */
    public /* synthetic */ void m1541lambda$new$0$orgtelegramuiCellsHintDialogCell(float progress) {
        float scale = 1.0f - (this.checkBox.getProgress() * 0.143f);
        this.imageView.setScaleX(scale);
        this.imageView.setScaleY(scale);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(86.0f), NUM));
        this.counterView.counterDrawable.horizontalPadding = (float) AndroidUtilities.dp(13.0f);
    }

    public void update(int mask) {
        if (!((MessagesController.UPDATE_MASK_STATUS & mask) == 0 || this.currentUser == null)) {
            this.currentUser = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.currentUser.id));
            this.imageView.invalidate();
            invalidate();
        }
        if (mask == 0 || (MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE & mask) != 0 || (MessagesController.UPDATE_MASK_NEW_MESSAGE & mask) != 0) {
            TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).dialogs_dict.get(this.dialogId);
            if (dialog == null || dialog.unread_count == 0) {
                this.lastUnreadCount = 0;
                this.counterView.setCount(0, this.wasDraw);
            } else if (this.lastUnreadCount != dialog.unread_count) {
                int i = dialog.unread_count;
                this.lastUnreadCount = i;
                this.counterView.setCount(i, this.wasDraw);
            }
        }
    }

    public void update() {
        if (DialogObject.isUserDialog(this.dialogId)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.dialogId));
            this.currentUser = user;
            this.avatarDrawable.setInfo(user);
            return;
        }
        this.avatarDrawable.setInfo(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.dialogId)));
        this.currentUser = null;
    }

    public void setDialog(long uid, boolean counter, CharSequence name) {
        if (this.dialogId != uid) {
            this.wasDraw = false;
            invalidate();
        }
        this.dialogId = uid;
        if (DialogObject.isUserDialog(uid)) {
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(uid));
            this.currentUser = user;
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (user != null) {
                this.nameTextView.setText(UserObject.getFirstName(user));
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(this.currentUser);
            this.imageView.setForUserOrChat(this.currentUser, this.avatarDrawable);
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-uid));
            if (name != null) {
                this.nameTextView.setText(name);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(chat);
            this.currentUser = null;
            this.imageView.setForUserOrChat(chat, this.avatarDrawable);
        }
        if (counter) {
            update(0);
        }
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0083  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean drawChild(android.graphics.Canvas r10, android.view.View r11, long r12) {
        /*
            r9 = this;
            boolean r0 = super.drawChild(r10, r11, r12)
            org.telegram.ui.Components.BackupImageView r1 = r9.imageView
            if (r11 != r1) goto L_0x00d0
            org.telegram.tgnet.TLRPC$User r1 = r9.currentUser
            r2 = 1
            if (r1 == 0) goto L_0x0041
            boolean r1 = r1.bot
            if (r1 != 0) goto L_0x0041
            org.telegram.tgnet.TLRPC$User r1 = r9.currentUser
            org.telegram.tgnet.TLRPC$UserStatus r1 = r1.status
            if (r1 == 0) goto L_0x0029
            org.telegram.tgnet.TLRPC$User r1 = r9.currentUser
            org.telegram.tgnet.TLRPC$UserStatus r1 = r1.status
            int r1 = r1.expires
            int r3 = r9.currentAccount
            org.telegram.tgnet.ConnectionsManager r3 = org.telegram.tgnet.ConnectionsManager.getInstance(r3)
            int r3 = r3.getCurrentTime()
            if (r1 > r3) goto L_0x003f
        L_0x0029:
            int r1 = r9.currentAccount
            org.telegram.messenger.MessagesController r1 = org.telegram.messenger.MessagesController.getInstance(r1)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Long, java.lang.Integer> r1 = r1.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r3 = r9.currentUser
            long r3 = r3.id
            java.lang.Long r3 = java.lang.Long.valueOf(r3)
            boolean r1 = r1.containsKey(r3)
            if (r1 == 0) goto L_0x0041
        L_0x003f:
            r1 = 1
            goto L_0x0042
        L_0x0041:
            r1 = 0
        L_0x0042:
            boolean r3 = r9.wasDraw
            r4 = 1065353216(0x3var_, float:1.0)
            r5 = 0
            if (r3 != 0) goto L_0x0051
            if (r1 == 0) goto L_0x004e
            r3 = 1065353216(0x3var_, float:1.0)
            goto L_0x004f
        L_0x004e:
            r3 = 0
        L_0x004f:
            r9.showOnlineProgress = r3
        L_0x0051:
            r3 = 1037726734(0x3dda740e, float:0.10666667)
            if (r1 == 0) goto L_0x0069
            float r6 = r9.showOnlineProgress
            int r7 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r7 == 0) goto L_0x0069
            float r6 = r6 + r3
            r9.showOnlineProgress = r6
            int r3 = (r6 > r4 ? 1 : (r6 == r4 ? 0 : -1))
            if (r3 <= 0) goto L_0x0065
            r9.showOnlineProgress = r4
        L_0x0065:
            r9.invalidate()
            goto L_0x007d
        L_0x0069:
            if (r1 != 0) goto L_0x007d
            float r4 = r9.showOnlineProgress
            int r6 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r6 == 0) goto L_0x007d
            float r4 = r4 - r3
            r9.showOnlineProgress = r4
            int r3 = (r4 > r5 ? 1 : (r4 == r5 ? 0 : -1))
            if (r3 >= 0) goto L_0x007a
            r9.showOnlineProgress = r5
        L_0x007a:
            r9.invalidate()
        L_0x007d:
            float r3 = r9.showOnlineProgress
            int r3 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r3 == 0) goto L_0x00ce
            r3 = 1112801280(0x42540000, float:53.0)
            int r3 = org.telegram.messenger.AndroidUtilities.dp(r3)
            r4 = 1114374144(0x426CLASSNAME, float:59.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            r10.save()
            float r5 = r9.showOnlineProgress
            float r6 = (float) r4
            float r7 = (float) r3
            r10.scale(r5, r5, r6, r7)
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r6 = "windowBackgroundWhite"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setColor(r6)
            float r5 = (float) r4
            float r6 = (float) r3
            r7 = 1088421888(0x40e00000, float:7.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            android.graphics.Paint r8 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r10.drawCircle(r5, r6, r7, r8)
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r6 = "chats_onlineCircle"
            int r6 = org.telegram.ui.ActionBar.Theme.getColor(r6)
            r5.setColor(r6)
            float r5 = (float) r4
            float r6 = (float) r3
            r7 = 1084227584(0x40a00000, float:5.0)
            int r7 = org.telegram.messenger.AndroidUtilities.dp(r7)
            float r7 = (float) r7
            android.graphics.Paint r8 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r10.drawCircle(r5, r6, r7, r8)
            r10.restore()
        L_0x00ce:
            r9.wasDraw = r2
        L_0x00d0:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.HintDialogCell.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        if (this.drawCheckbox) {
            int cx = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2);
            int cy = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
            Theme.checkboxSquare_checkPaint.setColor(Theme.getColor("dialogRoundCheckBox"));
            Theme.checkboxSquare_checkPaint.setAlpha((int) (this.checkBox.getProgress() * 255.0f));
            canvas.drawCircle((float) cx, (float) cy, (float) AndroidUtilities.dp(28.0f), Theme.checkboxSquare_checkPaint);
        }
    }

    public void setChecked(boolean checked, boolean animated) {
        if (this.drawCheckbox) {
            this.checkBox.setChecked(checked, animated);
        }
    }

    public long getDialogId() {
        return this.dialogId;
    }
}
