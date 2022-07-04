package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class AdminedChannelCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    CheckBox2 checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private TLRPC.Chat currentChannel;
    private ImageView deleteButton;
    private boolean isLast;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AdminedChannelCell(Context context, View.OnClickListener onClickListener, boolean needCheck, int padding) {
        super(context);
        Context context2 = context;
        View.OnClickListener onClickListener2 = onClickListener;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 12), 6.0f, LocaleController.isRTL ? (float) (padding + 12) : 0.0f, 6.0f));
        if (needCheck) {
            CheckBox2 checkBox2 = new CheckBox2(context2, 21);
            this.checkBox = checkBox2;
            checkBox2.setColor((String) null, "windowBackgroundWhite", "checkboxCheck");
            this.checkBox.setDrawUnchecked(false);
            this.checkBox.setDrawBackgroundAsArc(3);
            addView(this.checkBox, LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : (float) (padding + 42), 32.0f, LocaleController.isRTL ? (float) (padding + 42) : 0.0f, 0.0f));
        }
        int leftPadding = onClickListener2 == null ? 24 : 62;
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? (float) leftPadding : (float) (padding + 73), 9.5f, LocaleController.isRTL ? (float) (padding + 73) : (float) leftPadding, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.statusTextView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? (float) leftPadding : (float) (padding + 73), 32.5f, LocaleController.isRTL ? (float) (padding + 73) : (float) leftPadding, 6.0f));
        if (onClickListener2 != null) {
            ImageView imageView = new ImageView(context2);
            this.deleteButton = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.deleteButton.setImageResource(NUM);
            this.deleteButton.setOnClickListener(onClickListener2);
            this.deleteButton.setBackground(Theme.createSelectorDrawable(Theme.getColor("listSelectorSDK21")));
            this.deleteButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText"), PorterDuff.Mode.MULTIPLY));
            addView(this.deleteButton, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 7.0f : 0.0f, 6.0f, LocaleController.isRTL ? 0.0f : 7.0f, 0.0f));
        }
    }

    public void setChannel(TLRPC.Chat channel, boolean last) {
        String url = MessagesController.getInstance(this.currentAccount).linkPrefix + "/";
        this.currentChannel = channel;
        this.avatarDrawable.setInfo(channel);
        this.nameTextView.setText(channel.title);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(url + channel.username);
        stringBuilder.setSpan(new URLSpanNoUnderline(""), url.length(), stringBuilder.length(), 33);
        this.statusTextView.setText(stringBuilder);
        this.avatarImageView.setForUserOrChat(channel, this.avatarDrawable);
        this.isLast = last;
    }

    public void update() {
        this.avatarDrawable.setInfo(this.currentChannel);
        this.avatarImageView.invalidate();
    }

    public TLRPC.Chat getCurrentChannel() {
        return this.currentChannel;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(widthMeasureSpec), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) ((this.isLast ? 12 : 0) + 60)), NUM));
    }

    public boolean hasOverlappingRendering() {
        return false;
    }

    public SimpleTextView getNameTextView() {
        return this.nameTextView;
    }

    public SimpleTextView getStatusTextView() {
        return this.statusTextView;
    }

    public ImageView getDeleteButton() {
        return this.deleteButton;
    }

    public void setChecked(boolean checked, boolean animated) {
        this.checkBox.setChecked(checked, animated);
    }
}
