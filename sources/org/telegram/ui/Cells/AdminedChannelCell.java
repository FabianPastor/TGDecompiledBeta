package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.text.SpannableStringBuilder;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC.Chat;
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
    private Chat currentChannel;
    private ImageView deleteButton;
    private boolean isLast;
    private SimpleTextView nameTextView;
    private SimpleTextView statusTextView;

    public boolean hasOverlappingRendering() {
        return false;
    }

    public AdminedChannelCell(Context context, OnClickListener onClickListener) {
        Context context2 = context;
        super(context);
        this.avatarImageView = new BackupImageView(context2);
        this.avatarImageView.setRoundRadius(AndroidUtilities.dp(24.0f));
        int i = 3;
        addView(this.avatarImageView, LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 12.0f, 12.0f, LocaleController.isRTL ? 12.0f : 0.0f, 0.0f));
        r0.nameTextView = new SimpleTextView(context2);
        r0.nameTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.nameTextView.setTextSize(17);
        r0.nameTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.nameTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 62.0f : 73.0f, 15.5f, LocaleController.isRTL ? 73.0f : 62.0f, 0.0f));
        r0.statusTextView = new SimpleTextView(context2);
        r0.statusTextView.setTextSize(14);
        r0.statusTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText));
        r0.statusTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText));
        r0.statusTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.statusTextView, LayoutHelper.createFrame(-1, 20.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 62.0f : 73.0f, 38.5f, LocaleController.isRTL ? 73.0f : 62.0f, 0.0f));
        r0.deleteButton = new ImageView(context2);
        r0.deleteButton.setScaleType(ScaleType.CENTER);
        r0.deleteButton.setImageResource(C0446R.drawable.msg_panel_clear);
        r0.deleteButton.setOnClickListener(onClickListener);
        r0.deleteButton.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText), Mode.MULTIPLY));
        View view = r0.deleteButton;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(48, 48.0f, i | 48, LocaleController.isRTL ? 7.0f : 0.0f, 12.0f, LocaleController.isRTL ? 0.0f : 7.0f, 0.0f));
    }

    public void setChannel(Chat chat, boolean z) {
        TLObject tLObject = chat.photo != null ? chat.photo.photo_small : null;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(MessagesController.getInstance(this.currentAccount).linkPrefix);
        stringBuilder.append("/");
        String stringBuilder2 = stringBuilder.toString();
        this.currentChannel = chat;
        this.avatarDrawable.setInfo(chat);
        this.nameTextView.setText(chat.title);
        StringBuilder stringBuilder3 = new StringBuilder();
        stringBuilder3.append(stringBuilder2);
        stringBuilder3.append(chat.username);
        CharSequence spannableStringBuilder = new SpannableStringBuilder(stringBuilder3.toString());
        spannableStringBuilder.setSpan(new URLSpanNoUnderline(TtmlNode.ANONYMOUS_REGION_ID), stringBuilder2.length(), spannableStringBuilder.length(), 33);
        this.statusTextView.setText(spannableStringBuilder);
        this.avatarImageView.setImage(tLObject, "50_50", this.avatarDrawable);
        this.isLast = z;
    }

    public void update() {
        this.avatarDrawable.setInfo(this.currentChannel);
        this.avatarImageView.invalidate();
    }

    public Chat getCurrentChannel() {
        return this.currentChannel;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp((float) (60 + (this.isLast ? 12 : 0))), NUM));
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
