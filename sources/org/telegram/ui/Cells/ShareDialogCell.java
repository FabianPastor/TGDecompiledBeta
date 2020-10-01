package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.LayoutHelper;

public class ShareDialogCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private CheckBox2 checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private TextView nameTextView;
    private float onlineProgress;
    private TLRPC$User user;

    public ShareDialogCell(Context context) {
        super(context);
        setWillNotDraw(false);
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(28.0f));
        addView(this.imageView, LayoutHelper.createFrame(56, 56.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor("dialogTextBlack"));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(2);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(2);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, 66.0f, 6.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setColor("dialogRoundCheckBox", "dialogBackground", "dialogRoundCheckBoxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(4);
        this.checkBox.setProgressDelegate(new CheckBoxBase.ProgressDelegate() {
            public final void setProgress(float f) {
                ShareDialogCell.this.lambda$new$0$ShareDialogCell(f);
            }
        });
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 49, 19.0f, 42.0f, 0.0f, 0.0f));
    }

    public /* synthetic */ void lambda$new$0$ShareDialogCell(float f) {
        float progress = 1.0f - (this.checkBox.getProgress() * 0.143f);
        this.imageView.setScaleX(progress);
        this.imageView.setScaleY(progress);
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(103.0f), NUM));
    }

    public void setDialog(int i, boolean z, CharSequence charSequence) {
        if (i > 0) {
            TLRPC$User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            this.user = user2;
            this.avatarDrawable.setInfo(user2);
            if (UserObject.isReplyUser(this.user)) {
                this.nameTextView.setText(LocaleController.getString("RepliesTitle", NUM));
                this.avatarDrawable.setAvatarType(12);
                this.imageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.user);
            } else if (UserObject.isUserSelf(this.user)) {
                this.nameTextView.setText(LocaleController.getString("SavedMessages", NUM));
                this.avatarDrawable.setAvatarType(1);
                this.imageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.user);
            } else {
                if (charSequence != null) {
                    this.nameTextView.setText(charSequence);
                } else {
                    TLRPC$User tLRPC$User = this.user;
                    if (tLRPC$User != null) {
                        this.nameTextView.setText(ContactsController.formatName(tLRPC$User.first_name, tLRPC$User.last_name));
                    } else {
                        this.nameTextView.setText("");
                    }
                }
                this.imageView.setImage(ImageLocation.getForUser(this.user, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.user);
            }
        } else {
            this.user = null;
            TLRPC$Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Integer.valueOf(-i));
            if (charSequence != null) {
                this.nameTextView.setText(charSequence);
            } else if (chat != null) {
                this.nameTextView.setText(chat.title);
            } else {
                this.nameTextView.setText("");
            }
            this.avatarDrawable.setInfo(chat);
            this.imageView.setImage(ImageLocation.getForChat(chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) chat);
        }
        this.checkBox.setChecked(z, false);
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002d, code lost:
        r8 = r8.status;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean drawChild(android.graphics.Canvas r7, android.view.View r8, long r9) {
        /*
            r6 = this;
            boolean r9 = super.drawChild(r7, r8, r9)
            org.telegram.ui.Components.BackupImageView r10 = r6.imageView
            if (r8 != r10) goto L_0x00ed
            org.telegram.tgnet.TLRPC$User r8 = r6.user
            if (r8 == 0) goto L_0x00ed
            boolean r8 = org.telegram.messenger.MessagesController.isSupportUser(r8)
            if (r8 != 0) goto L_0x00ed
            long r0 = android.os.SystemClock.elapsedRealtime()
            long r2 = r6.lastUpdateTime
            long r2 = r0 - r2
            r4 = 17
            int r8 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r8 <= 0) goto L_0x0021
            r2 = r4
        L_0x0021:
            r6.lastUpdateTime = r0
            org.telegram.tgnet.TLRPC$User r8 = r6.user
            boolean r10 = r8.self
            if (r10 != 0) goto L_0x0057
            boolean r10 = r8.bot
            if (r10 != 0) goto L_0x0057
            org.telegram.tgnet.TLRPC$UserStatus r8 = r8.status
            if (r8 == 0) goto L_0x003f
            int r8 = r8.expires
            int r10 = r6.currentAccount
            org.telegram.tgnet.ConnectionsManager r10 = org.telegram.tgnet.ConnectionsManager.getInstance(r10)
            int r10 = r10.getCurrentTime()
            if (r8 > r10) goto L_0x0055
        L_0x003f:
            int r8 = r6.currentAccount
            org.telegram.messenger.MessagesController r8 = org.telegram.messenger.MessagesController.getInstance(r8)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r8 = r8.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r10 = r6.user
            int r10 = r10.id
            java.lang.Integer r10 = java.lang.Integer.valueOf(r10)
            boolean r8 = r8.containsKey(r10)
            if (r8 == 0) goto L_0x0057
        L_0x0055:
            r8 = 1
            goto L_0x0058
        L_0x0057:
            r8 = 0
        L_0x0058:
            r10 = 0
            if (r8 != 0) goto L_0x0061
            float r0 = r6.onlineProgress
            int r0 = (r0 > r10 ? 1 : (r0 == r10 ? 0 : -1))
            if (r0 == 0) goto L_0x00ed
        L_0x0061:
            org.telegram.ui.Components.BackupImageView r0 = r6.imageView
            int r0 = r0.getBottom()
            r1 = 1086324736(0x40CLASSNAME, float:6.0)
            int r1 = org.telegram.messenger.AndroidUtilities.dp(r1)
            int r0 = r0 - r1
            org.telegram.ui.Components.BackupImageView r1 = r6.imageView
            int r1 = r1.getRight()
            r4 = 1092616192(0x41200000, float:10.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 - r4
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r5 = "windowBackgroundWhite"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setColor(r5)
            float r1 = (float) r1
            float r0 = (float) r0
            r4 = 1088421888(0x40e00000, float:7.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r6.onlineProgress
            float r4 = r4 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r7.drawCircle(r1, r0, r4, r5)
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r5 = "chats_onlineCircle"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setColor(r5)
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r6.onlineProgress
            float r4 = r4 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r7.drawCircle(r1, r0, r4, r5)
            r7 = 1125515264(0x43160000, float:150.0)
            if (r8 == 0) goto L_0x00d4
            float r8 = r6.onlineProgress
            r10 = 1065353216(0x3var_, float:1.0)
            int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r0 >= 0) goto L_0x00ed
            float r0 = (float) r2
            float r0 = r0 / r7
            float r8 = r8 + r0
            r6.onlineProgress = r8
            int r7 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r7 <= 0) goto L_0x00cb
            r6.onlineProgress = r10
        L_0x00cb:
            org.telegram.ui.Components.BackupImageView r7 = r6.imageView
            r7.invalidate()
            r6.invalidate()
            goto L_0x00ed
        L_0x00d4:
            float r8 = r6.onlineProgress
            int r0 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r0 <= 0) goto L_0x00ed
            float r0 = (float) r2
            float r0 = r0 / r7
            float r8 = r8 - r0
            r6.onlineProgress = r8
            int r7 = (r8 > r10 ? 1 : (r8 == r10 ? 0 : -1))
            if (r7 >= 0) goto L_0x00e5
            r6.onlineProgress = r10
        L_0x00e5:
            org.telegram.ui.Components.BackupImageView r7 = r6.imageView
            r7.invalidate()
            r6.invalidate()
        L_0x00ed:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ShareDialogCell.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2);
        int top = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
        Theme.checkboxSquare_checkPaint.setColor(Theme.getColor("dialogRoundCheckBox"));
        Theme.checkboxSquare_checkPaint.setAlpha((int) (this.checkBox.getProgress() * 255.0f));
        canvas.drawCircle((float) left, (float) top, (float) AndroidUtilities.dp(28.0f), Theme.checkboxSquare_checkPaint);
    }
}
