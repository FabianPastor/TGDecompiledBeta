package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.StatisticActivity;

public class StatisticPostInfoCell extends FrameLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private final TLRPC$ChatFull chat;
    private TextView date;
    private BackupImageView imageView;
    private TextView message;
    private TextView shares;
    private TextView views;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public StatisticPostInfoCell(Context context, TLRPC$ChatFull tLRPC$ChatFull) {
        super(context);
        Context context2 = context;
        this.chat = tLRPC$ChatFull;
        BackupImageView backupImageView = new BackupImageView(context2);
        this.imageView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrame(46, 46.0f, 8388627, 12.0f, 0.0f, 16.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        LinearLayout linearLayout2 = new LinearLayout(context2);
        linearLayout2.setOrientation(0);
        AnonymousClass1 r6 = new TextView(this, context2) {
            AnimatedEmojiSpan.EmojiGroupedSpans stack;

            public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                super.setText(charSequence, bufferType);
                this.stack = AnimatedEmojiSpan.update(0, (View) this, this.stack, getLayout());
            }

            /* access modifiers changed from: protected */
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                this.stack = AnimatedEmojiSpan.update(0, (View) this, this.stack, getLayout());
            }

            /* access modifiers changed from: protected */
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                AnimatedEmojiSpan.release((View) this, this.stack);
            }

            /* access modifiers changed from: protected */
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                AnimatedEmojiSpan.drawAnimatedEmojis(canvas, getLayout(), this.stack, 0.0f, (List<SpoilerEffect>) null, 0.0f, 0.0f, 0.0f, 1.0f);
            }
        };
        this.message = r6;
        r6.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.message.setTextSize(1, 15.0f);
        this.message.setTextColor(-16777216);
        this.message.setLines(1);
        this.message.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView = new TextView(context2);
        this.views = textView;
        textView.setTextSize(1, 15.0f);
        this.views.setTextColor(-16777216);
        linearLayout2.addView(this.message, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 0, 16, 0));
        linearLayout2.addView(this.views, LayoutHelper.createLinear(-2, -2));
        linearLayout.addView(linearLayout2, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 8.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context2);
        this.date = textView2;
        textView2.setTextSize(1, 13.0f);
        this.date.setTextColor(-16777216);
        this.date.setLines(1);
        this.date.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView3 = new TextView(context2);
        this.shares = textView3;
        textView3.setTextSize(1, 13.0f);
        this.shares.setTextColor(-16777216);
        LinearLayout linearLayout3 = new LinearLayout(context2);
        linearLayout3.setOrientation(0);
        linearLayout3.addView(this.date, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 0, 8, 0));
        linearLayout3.addView(this.shares, LayoutHelper.createLinear(-2, -2));
        linearLayout.addView(linearLayout3, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 2.0f, 0.0f, 8.0f));
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 72.0f, 0.0f, 12.0f, 0.0f));
        this.message.setTextColor(Theme.getColor("dialogTextBlack"));
        this.views.setTextColor(Theme.getColor("dialogTextBlack"));
        this.date.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
        this.shares.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText3"));
    }

    public void setData(StatisticActivity.RecentPostInfo recentPostInfo) {
        CharSequence charSequence;
        MessageObject messageObject = recentPostInfo.message;
        ArrayList<TLRPC$PhotoSize> arrayList = messageObject.photoThumbs;
        if (arrayList != null) {
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
            TLRPC$PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 50);
            this.imageView.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize, messageObject.photoThumbsObject), "50_50", ImageLocation.getForObject(closestPhotoSizeWithSize2, messageObject.photoThumbsObject), "b1", 0, (Object) messageObject);
            this.imageView.setRoundRadius(AndroidUtilities.dp(4.0f));
        } else if (this.chat.chat_photo.sizes.size() > 0) {
            this.imageView.setImage(ImageLocation.getForPhoto(this.chat.chat_photo.sizes.get(0), this.chat.chat_photo), "50_50", (String) null, (Drawable) null, (Object) this.chat);
            this.imageView.setRoundRadius(AndroidUtilities.dp(46.0f) >> 1);
        }
        if (messageObject.isMusic()) {
            charSequence = String.format("%s, %s", new Object[]{messageObject.getMusicTitle().trim(), messageObject.getMusicAuthor().trim()});
        } else {
            charSequence = messageObject.caption;
            if (charSequence == null) {
                charSequence = messageObject.messageText;
            }
        }
        this.message.setText(AndroidUtilities.trim(AndroidUtilities.replaceNewLines(new SpannableStringBuilder(charSequence)), (int[]) null));
        this.views.setText(String.format(LocaleController.getPluralString("Views", recentPostInfo.counters.views), new Object[]{AndroidUtilities.formatCount(recentPostInfo.counters.views)}));
        this.date.setText(LocaleController.formatDateAudio((long) recentPostInfo.message.messageOwner.date, false));
        this.shares.setText(String.format(LocaleController.getPluralString("Shares", recentPostInfo.counters.forwards), new Object[]{AndroidUtilities.formatCount(recentPostInfo.counters.forwards)}));
    }

    public void setData(StatisticActivity.MemberData memberData) {
        this.avatarDrawable.setInfo(memberData.user);
        this.imageView.setForUserOrChat(memberData.user, this.avatarDrawable);
        this.imageView.setRoundRadius(AndroidUtilities.dp(46.0f) >> 1);
        this.message.setText(memberData.user.first_name);
        this.date.setText(memberData.description);
        this.views.setVisibility(8);
        this.shares.setVisibility(8);
    }
}
