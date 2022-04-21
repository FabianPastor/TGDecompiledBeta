package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class MemberRequestCell extends FrameLayout {
    private final AvatarDrawable avatarDrawable = new AvatarDrawable();
    private final BackupImageView avatarImageView;
    private TLRPC.TL_chatInviteImporter importer;
    private boolean isNeedDivider;
    private final SimpleTextView nameTextView;
    private final SimpleTextView statusTextView;

    public interface OnClickListener {
        void onAddClicked(TLRPC.TL_chatInviteImporter tL_chatInviteImporter);

        void onDismissClicked(TLRPC.TL_chatInviteImporter tL_chatInviteImporter);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public MemberRequestCell(Context context, OnClickListener clickListener, boolean isChannel) {
        super(context);
        String str;
        int i;
        OnClickListener onClickListener = clickListener;
        BackupImageView backupImageView = new BackupImageView(getContext());
        this.avatarImageView = backupImageView;
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        this.nameTextView = simpleTextView;
        SimpleTextView simpleTextView2 = new SimpleTextView(getContext());
        this.statusTextView = simpleTextView2;
        backupImageView.setRoundRadius(AndroidUtilities.dp(23.0f));
        int i2 = 5;
        addView(backupImageView, LayoutHelper.createFrame(46, 46.0f, LocaleController.isRTL ? 5 : 3, 12.0f, 8.0f, 12.0f, 0.0f));
        simpleTextView.setGravity(LocaleController.isRTL ? 5 : 3);
        simpleTextView.setMaxLines(1);
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        simpleTextView.setTextSize(17);
        simpleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, 48, LocaleController.isRTL ? 12.0f : 74.0f, 12.0f, LocaleController.isRTL ? 74.0f : 12.0f, 0.0f));
        simpleTextView2.setGravity(LocaleController.isRTL ? 5 : 3);
        simpleTextView2.setMaxLines(1);
        simpleTextView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        simpleTextView2.setTextSize(14);
        addView(simpleTextView2, LayoutHelper.createFrame(-1, -2.0f, 48, LocaleController.isRTL ? 12.0f : 74.0f, 36.0f, LocaleController.isRTL ? 74.0f : 12.0f, 0.0f));
        int btnPadding = AndroidUtilities.dp(17.0f);
        TextView addButton = new TextView(getContext());
        addButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.getColor("featuredStickers_addButton"), Theme.getColor("featuredStickers_addButtonPressed")));
        addButton.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        addButton.setMaxLines(1);
        int i3 = 0;
        addButton.setPadding(btnPadding, 0, btnPadding, 0);
        if (isChannel) {
            i = NUM;
            str = "AddToChannel";
        } else {
            i = NUM;
            str = "AddToGroup";
        }
        addButton.setText(LocaleController.getString(str, i));
        addButton.setTextColor(Theme.getColor("featuredStickers_buttonText"));
        addButton.setTextSize(14.0f);
        addButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addButton.setOnClickListener(new MemberRequestCell$$ExternalSyntheticLambda0(this, onClickListener));
        addView(addButton, LayoutHelper.createFrame(-2, 32.0f, LocaleController.isRTL ? 5 : 3, LocaleController.isRTL ? 0.0f : 73.0f, 62.0f, LocaleController.isRTL ? 73.0f : 0.0f, 0.0f));
        float addButtonWidth = addButton.getPaint().measureText(addButton.getText().toString()) + ((float) (btnPadding * 2));
        TextView dismissButton = new TextView(getContext());
        dismissButton.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(4.0f), 0, Theme.getColor("listSelectorSDK21"), -16777216));
        dismissButton.setGravity((LocaleController.isRTL ? 5 : 3) | 16);
        dismissButton.setMaxLines(1);
        dismissButton.setPadding(btnPadding, 0, btnPadding, 0);
        dismissButton.setText(LocaleController.getString("Dismiss", NUM));
        dismissButton.setTextColor(Theme.getColor("windowBackgroundWhiteBlueText"));
        dismissButton.setTextSize(14.0f);
        dismissButton.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        dismissButton.setOnClickListener(new MemberRequestCell$$ExternalSyntheticLambda1(this, onClickListener));
        FrameLayout.LayoutParams dismissLayoutParams = new FrameLayout.LayoutParams(-2, AndroidUtilities.dp(32.0f), !LocaleController.isRTL ? 3 : i2);
        dismissLayoutParams.topMargin = AndroidUtilities.dp(62.0f);
        dismissLayoutParams.leftMargin = LocaleController.isRTL ? 0 : (int) (((float) AndroidUtilities.dp(79.0f)) + addButtonWidth);
        dismissLayoutParams.rightMargin = LocaleController.isRTL ? (int) (((float) AndroidUtilities.dp(79.0f)) + addButtonWidth) : i3;
        addView(dismissButton, dismissLayoutParams);
    }

    /* renamed from: lambda$new$0$org-telegram-ui-Cells-MemberRequestCell  reason: not valid java name */
    public /* synthetic */ void m1510lambda$new$0$orgtelegramuiCellsMemberRequestCell(OnClickListener clickListener, View v) {
        TLRPC.TL_chatInviteImporter tL_chatInviteImporter;
        if (clickListener != null && (tL_chatInviteImporter = this.importer) != null) {
            clickListener.onAddClicked(tL_chatInviteImporter);
        }
    }

    /* renamed from: lambda$new$1$org-telegram-ui-Cells-MemberRequestCell  reason: not valid java name */
    public /* synthetic */ void m1511lambda$new$1$orgtelegramuiCellsMemberRequestCell(OnClickListener clickListener, View v) {
        TLRPC.TL_chatInviteImporter tL_chatInviteImporter;
        if (clickListener != null && (tL_chatInviteImporter = this.importer) != null) {
            clickListener.onDismissClicked(tL_chatInviteImporter);
        }
    }

    public void setData(LongSparseArray<TLRPC.User> users, TLRPC.TL_chatInviteImporter importer2, boolean isNeedDivider2) {
        this.importer = importer2;
        this.isNeedDivider = isNeedDivider2;
        setWillNotDraw(!isNeedDivider2);
        TLRPC.User user = users.get(importer2.user_id);
        this.avatarDrawable.setInfo(user);
        this.avatarImageView.setForUserOrChat(user, this.avatarDrawable);
        this.nameTextView.setText(UserObject.getUserName(user));
        String dateText = LocaleController.formatDateAudio((long) importer2.date, false);
        if (importer2.approved_by == 0) {
            this.statusTextView.setText(LocaleController.formatString("RequestedToJoinAt", NUM, dateText));
            return;
        }
        TLRPC.User approvedByUser = users.get(importer2.approved_by);
        if (approvedByUser != null) {
            this.statusTextView.setText(LocaleController.formatString("AddedBy", NUM, UserObject.getFirstName(approvedByUser), dateText));
            return;
        }
        this.statusTextView.setText("");
    }

    public TLRPC.TL_chatInviteImporter getImporter() {
        return this.importer;
    }

    public BackupImageView getAvatarImageView() {
        return this.avatarImageView;
    }

    public String getStatus() {
        return this.statusTextView.getText().toString();
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(107.0f), NUM));
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.isNeedDivider) {
            canvas.drawLine(LocaleController.isRTL ? 0.0f : (float) AndroidUtilities.dp(72.0f), (float) (getMeasuredHeight() - 1), (float) (getMeasuredWidth() - (LocaleController.isRTL ? AndroidUtilities.dp(72.0f) : 0)), (float) (getMeasuredHeight() - 1), Theme.dividerPaint);
        }
    }
}
