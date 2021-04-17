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
    private long currentDialog;
    private int currentType;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private TextView nameTextView;
    private float onlineProgress;
    private TLRPC$User user;

    public ShareDialogCell(Context context, int i) {
        super(context);
        setWillNotDraw(false);
        this.currentType = i;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(28.0f));
        if (i == 2) {
            addView(this.imageView, LayoutHelper.createFrame(48, 48.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        } else {
            addView(this.imageView, LayoutHelper.createFrame(56, 56.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        }
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setTextColor(Theme.getColor(i == 1 ? "voipgroup_nameText" : "dialogTextBlack"));
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(2);
        this.nameTextView.setGravity(49);
        this.nameTextView.setLines(2);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, this.currentType == 2 ? 58.0f : 66.0f, 6.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 21);
        this.checkBox = checkBox2;
        checkBox2.setColor("dialogRoundCheckBox", i == 1 ? "voipgroup_inviteMembersBackground" : "dialogBackground", "dialogRoundCheckBoxCheck");
        this.checkBox.setDrawUnchecked(false);
        this.checkBox.setDrawBackgroundAsArc(4);
        this.checkBox.setProgressDelegate(new CheckBoxBase.ProgressDelegate() {
            public final void setProgress(float f) {
                ShareDialogCell.this.lambda$new$0$ShareDialogCell(f);
            }
        });
        addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, 49, 19.0f, this.currentType == 2 ? -40.0f : 42.0f, 0.0f, 0.0f));
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$new$0 */
    public /* synthetic */ void lambda$new$0$ShareDialogCell(float f) {
        float progress = 1.0f - (this.checkBox.getProgress() * 0.143f);
        this.imageView.setScaleX(progress);
        this.imageView.setScaleY(progress);
        invalidate();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(this.currentType == 2 ? 95.0f : 103.0f), NUM));
    }

    public void setDialog(int i, boolean z, CharSequence charSequence) {
        if (i > 0) {
            TLRPC$User user2 = MessagesController.getInstance(this.currentAccount).getUser(Integer.valueOf(i));
            this.user = user2;
            this.avatarDrawable.setInfo(user2);
            if (this.currentType != 2 && UserObject.isReplyUser(this.user)) {
                this.nameTextView.setText(LocaleController.getString("RepliesTitle", NUM));
                this.avatarDrawable.setAvatarType(12);
                this.imageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.user);
            } else if (this.currentType == 2 || !UserObject.isUserSelf(this.user)) {
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
                this.imageView.setForUserOrChat(this.user, this.avatarDrawable);
            } else {
                this.nameTextView.setText(LocaleController.getString("SavedMessages", NUM));
                this.avatarDrawable.setAvatarType(1);
                this.imageView.setImage((ImageLocation) null, (String) null, (Drawable) this.avatarDrawable, (Object) this.user);
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
            this.imageView.setForUserOrChat(chat, this.avatarDrawable);
        }
        this.currentDialog = (long) i;
        this.checkBox.setChecked(z, false);
    }

    public long getCurrentDialog() {
        return this.currentDialog;
    }

    public void setChecked(boolean z, boolean z2) {
        this.checkBox.setChecked(z, z2);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0033, code lost:
        r9 = r9.status;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean drawChild(android.graphics.Canvas r8, android.view.View r9, long r10) {
        /*
            r7 = this;
            boolean r10 = super.drawChild(r8, r9, r10)
            org.telegram.ui.Components.BackupImageView r11 = r7.imageView
            if (r9 != r11) goto L_0x00f9
            int r9 = r7.currentType
            r11 = 2
            if (r9 == r11) goto L_0x00f9
            org.telegram.tgnet.TLRPC$User r9 = r7.user
            if (r9 == 0) goto L_0x00f9
            boolean r9 = org.telegram.messenger.MessagesController.isSupportUser(r9)
            if (r9 != 0) goto L_0x00f9
            long r0 = android.os.SystemClock.elapsedRealtime()
            long r2 = r7.lastUpdateTime
            long r2 = r0 - r2
            r4 = 17
            int r9 = (r2 > r4 ? 1 : (r2 == r4 ? 0 : -1))
            if (r9 <= 0) goto L_0x0026
            r2 = r4
        L_0x0026:
            r7.lastUpdateTime = r0
            org.telegram.tgnet.TLRPC$User r9 = r7.user
            boolean r11 = r9.self
            r0 = 1
            if (r11 != 0) goto L_0x005d
            boolean r11 = r9.bot
            if (r11 != 0) goto L_0x005d
            org.telegram.tgnet.TLRPC$UserStatus r9 = r9.status
            if (r9 == 0) goto L_0x0045
            int r9 = r9.expires
            int r11 = r7.currentAccount
            org.telegram.tgnet.ConnectionsManager r11 = org.telegram.tgnet.ConnectionsManager.getInstance(r11)
            int r11 = r11.getCurrentTime()
            if (r9 > r11) goto L_0x005b
        L_0x0045:
            int r9 = r7.currentAccount
            org.telegram.messenger.MessagesController r9 = org.telegram.messenger.MessagesController.getInstance(r9)
            j$.util.concurrent.ConcurrentHashMap<java.lang.Integer, java.lang.Integer> r9 = r9.onlinePrivacy
            org.telegram.tgnet.TLRPC$User r11 = r7.user
            int r11 = r11.id
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)
            boolean r9 = r9.containsKey(r11)
            if (r9 == 0) goto L_0x005d
        L_0x005b:
            r9 = 1
            goto L_0x005e
        L_0x005d:
            r9 = 0
        L_0x005e:
            r11 = 0
            if (r9 != 0) goto L_0x0067
            float r1 = r7.onlineProgress
            int r1 = (r1 > r11 ? 1 : (r1 == r11 ? 0 : -1))
            if (r1 == 0) goto L_0x00f9
        L_0x0067:
            org.telegram.ui.Components.BackupImageView r1 = r7.imageView
            int r1 = r1.getBottom()
            r4 = 1086324736(0x40CLASSNAME, float:6.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            int r1 = r1 - r4
            org.telegram.ui.Components.BackupImageView r4 = r7.imageView
            int r4 = r4.getRight()
            r5 = 1092616192(0x41200000, float:10.0)
            int r5 = org.telegram.messenger.AndroidUtilities.dp(r5)
            int r4 = r4 - r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            int r6 = r7.currentType
            if (r6 != r0) goto L_0x008a
            java.lang.String r0 = "voipgroup_inviteMembersBackground"
            goto L_0x008c
        L_0x008a:
            java.lang.String r0 = "windowBackgroundWhite"
        L_0x008c:
            int r0 = org.telegram.ui.ActionBar.Theme.getColor(r0)
            r5.setColor(r0)
            float r0 = (float) r4
            float r1 = (float) r1
            r4 = 1088421888(0x40e00000, float:7.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r7.onlineProgress
            float r4 = r4 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r8.drawCircle(r0, r1, r4, r5)
            android.graphics.Paint r4 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            java.lang.String r5 = "chats_onlineCircle"
            int r5 = org.telegram.ui.ActionBar.Theme.getColor(r5)
            r4.setColor(r5)
            r4 = 1084227584(0x40a00000, float:5.0)
            int r4 = org.telegram.messenger.AndroidUtilities.dp(r4)
            float r4 = (float) r4
            float r5 = r7.onlineProgress
            float r4 = r4 * r5
            android.graphics.Paint r5 = org.telegram.ui.ActionBar.Theme.dialogs_onlineCirclePaint
            r8.drawCircle(r0, r1, r4, r5)
            r8 = 1125515264(0x43160000, float:150.0)
            if (r9 == 0) goto L_0x00e0
            float r9 = r7.onlineProgress
            r11 = 1065353216(0x3var_, float:1.0)
            int r0 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r0 >= 0) goto L_0x00f9
            float r0 = (float) r2
            float r0 = r0 / r8
            float r9 = r9 + r0
            r7.onlineProgress = r9
            int r8 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r8 <= 0) goto L_0x00d7
            r7.onlineProgress = r11
        L_0x00d7:
            org.telegram.ui.Components.BackupImageView r8 = r7.imageView
            r8.invalidate()
            r7.invalidate()
            goto L_0x00f9
        L_0x00e0:
            float r9 = r7.onlineProgress
            int r0 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r0 <= 0) goto L_0x00f9
            float r0 = (float) r2
            float r0 = r0 / r8
            float r9 = r9 - r0
            r7.onlineProgress = r9
            int r8 = (r9 > r11 ? 1 : (r9 == r11 ? 0 : -1))
            if (r8 >= 0) goto L_0x00f1
            r7.onlineProgress = r11
        L_0x00f1:
            org.telegram.ui.Components.BackupImageView r8 = r7.imageView
            r8.invalidate()
            r7.invalidate()
        L_0x00f9:
            return r10
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ShareDialogCell.drawChild(android.graphics.Canvas, android.view.View, long):boolean");
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        int left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2);
        int top = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
        Theme.checkboxSquare_checkPaint.setColor(Theme.getColor("dialogRoundCheckBox"));
        Theme.checkboxSquare_checkPaint.setAlpha((int) (this.checkBox.getProgress() * 255.0f));
        canvas.drawCircle((float) left, (float) top, (float) AndroidUtilities.dp(this.currentType == 2 ? 24.0f : 28.0f), Theme.checkboxSquare_checkPaint);
    }
}
