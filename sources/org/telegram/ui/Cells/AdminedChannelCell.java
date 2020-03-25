package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Chat;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.URLSpanNoUnderline;

public class AdminedChannelCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private int currentAccount = UserConfig.selectedAccount;
    private TLRPC$Chat currentChannel;
    private ImageView deleteButton;
    private boolean isLast;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public AdminedChannelCell(Context context, View.OnClickListener onClickListener) {
        super(context);
        Context context2 = context;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i = 5;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 12.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context2);
        this.nameTextView = simpleTextView;
        simpleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.nameTextView.setTextSize(17);
        this.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 62.0f : 73.0f, 15.5f, LocaleController.isRTL ? 73.0f : 62.0f, 0.0f));
        SimpleTextView simpleTextView2 = new SimpleTextView(context2);
        this.statusTextView = simpleTextView2;
        simpleTextView2.setTextSize(14);
        this.statusTextView.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
        this.statusTextView.setLinkTextColor(Theme.getColor("windowBackgroundWhiteLinkText"));
        this.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(this.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 62.0f : 73.0f, 38.5f, LocaleController.isRTL ? 73.0f : 62.0f, 0.0f));
        ImageView imageView = new ImageView(context2);
        this.deleteButton = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.deleteButton.setImageResource(NUM);
        this.deleteButton.setOnClickListener(onClickListener);
        this.deleteButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText"), PorterDuff.Mode.MULTIPLY));
        addView(this.deleteButton, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : i) | 48, LocaleController.isRTL ? 7.0f : 0.0f, 12.0f, LocaleController.isRTL ? 0.0f : 7.0f, 0.0f));
    }

    public void setChannel(TLRPC$Chat tLRPC$Chat, boolean z) {
        String str = MessagesController.getInstance(this.currentAccount).linkPrefix + "/";
        this.currentChannel = tLRPC$Chat;
        this.avatarDrawable.setInfo(tLRPC$Chat);
        this.nameTextView.setText(tLRPC$Chat.title);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str + tLRPC$Chat.username);
        spannableStringBuilder.setSpan(new URLSpanNoUnderline(""), str.length(), spannableStringBuilder.length(), 33);
        this.statusTextView.setText(spannableStringBuilder);
        this.avatarImageView.setImage(ImageLocation.getForChat(tLRPC$Chat, false), "50_50", (Drawable) this.avatarDrawable, (Object) this.currentChannel);
        this.isLast = z;
    }

    public void update() {
        this.avatarDrawable.setInfo(this.currentChannel);
        this.avatarImageView.invalidate();
    }

    public TLRPC$Chat getCurrentChannel() {
        return this.currentChannel;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) ((this.isLast ? 12 : 0) + 60)), NUM));
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
}
