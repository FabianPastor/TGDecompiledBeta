package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.TLRPC$ChatFull;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.StatisticActivity;

public class StatisticPostInfoCell extends FrameLayout {
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
        TextView textView = new TextView(context2);
        this.message = textView;
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.message.setTextSize(15.0f);
        this.message.setTextColor(-16777216);
        this.message.setLines(1);
        this.message.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView2 = new TextView(context2);
        this.views = textView2;
        textView2.setTextSize(15.0f);
        this.views.setTextColor(-16777216);
        linearLayout2.addView(this.message, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 0, 16, 0));
        linearLayout2.addView(this.views, LayoutHelper.createLinear(-2, -2));
        linearLayout.addView(linearLayout2, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 8.0f, 0.0f, 0.0f));
        TextView textView3 = new TextView(context2);
        this.date = textView3;
        textView3.setTextSize(13.0f);
        this.date.setTextColor(-16777216);
        this.date.setLines(1);
        this.date.setEllipsize(TextUtils.TruncateAt.END);
        TextView textView4 = new TextView(context2);
        this.shares = textView4;
        textView4.setTextSize(13.0f);
        this.shares.setTextColor(-16777216);
        LinearLayout linearLayout3 = new LinearLayout(context2);
        linearLayout3.setOrientation(0);
        linearLayout3.addView(this.date, LayoutHelper.createLinear(0, -2, 1.0f, 0, 0, 0, 8, 0));
        linearLayout3.addView(this.shares, LayoutHelper.createLinear(-2, -2));
        linearLayout.addView(linearLayout3, LayoutHelper.createFrame(-1, -2.0f, 8388659, 0.0f, 2.0f, 0.0f, 0.0f));
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 0, 72.0f, 0.0f, 12.0f, 0.0f));
        this.message.setTextColor(Theme.getColor("dialogTextBlack"));
        this.views.setTextColor(Theme.getColor("dialogTextBlack"));
        this.date.setTextColor(Theme.getColor("dialogTextGray4"));
        this.shares.setTextColor(Theme.getColor("dialogTextGray4"));
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(56.0f), NUM));
    }

    public void setData(StatisticActivity.RecentPostInfo recentPostInfo) {
        StatisticActivity.RecentPostInfo recentPostInfo2 = recentPostInfo;
        MessageObject messageObject = recentPostInfo2.message;
        ArrayList<TLRPC$PhotoSize> arrayList = messageObject.photoThumbs;
        if (arrayList != null) {
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, AndroidUtilities.getPhotoSize());
            this.imageView.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize, messageObject.photoThumbsObject), "50_50", (String) null, (Drawable) null, messageObject);
            this.imageView.setRoundRadius(AndroidUtilities.dp(4.0f));
        } else if (this.chat.chat_photo.sizes.size() > 0) {
            this.imageView.setImage(ImageLocation.getForPhoto(this.chat.chat_photo.sizes.get(0), this.chat.chat_photo), "50_50", (String) null, (Drawable) null, this.chat);
            this.imageView.setRoundRadius(AndroidUtilities.dp(46.0f) >> 1);
        }
        CharSequence charSequence = messageObject.caption;
        if (charSequence == null) {
            charSequence = messageObject.messageText;
        }
        this.message.setText(charSequence.toString().replace("\n", " ").trim());
        this.views.setText(String.format(LocaleController.getPluralString("Views", recentPostInfo2.counters.views), new Object[]{formatCount(recentPostInfo2.counters.views)}));
        this.date.setText(LocaleController.formatDateAudio((long) recentPostInfo2.message.messageOwner.date));
        this.shares.setText(String.format(LocaleController.getPluralString("Shares", recentPostInfo2.counters.forwards), new Object[]{formatCount(recentPostInfo2.counters.forwards)}));
    }

    public String formatCount(int i) {
        if (i < 1000) {
            return Integer.toString(i);
        }
        ArrayList arrayList = new ArrayList();
        while (i != 0) {
            int i2 = i % 1000;
            i /= 1000;
            if (i > 0) {
                arrayList.add(String.format(Locale.ENGLISH, "%03d", new Object[]{Integer.valueOf(i2)}));
            } else {
                arrayList.add(Integer.toString(i2));
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            sb.append((String) arrayList.get(size));
            if (size != 0) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
