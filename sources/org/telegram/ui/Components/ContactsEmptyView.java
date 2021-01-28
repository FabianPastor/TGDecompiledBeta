package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$TL_messages_stickerSet;
import org.telegram.ui.ActionBar.Theme;

public class ContactsEmptyView extends LinearLayout implements NotificationCenter.NotificationCenterDelegate {
    private int currentAccount = UserConfig.selectedAccount;
    private LoadingStickerDrawable drawable;
    private ArrayList<ImageView> imageViews = new ArrayList<>();
    private BackupImageView stickerView;
    private ArrayList<TextView> textViews = new ArrayList<>();
    private TextView titleTextView;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ContactsEmptyView(Context context) {
        super(context);
        Context context2 = context;
        setPadding(0, AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f));
        setOrientation(1);
        this.stickerView = new BackupImageView(context2);
        LoadingStickerDrawable loadingStickerDrawable = new LoadingStickerDrawable(this.stickerView, "m418 282.6CLASSNAME.4-21.1 20.2-44.9 20.2-70.8 0-88.3-79.8-175.3-178.9-175.3-100.1 0-178.9 88-178.9 175.3 0 46.6 16.9 73.1 29.1 86.1-19.3 23.4-30.9 52.3-34.6 86.1-2.5 22.7 3.2 41.4 17.4 57.3 14.3 16 51.7 35 148.1 35 41.2 0 119.9-5.3 156.7-18.3 49.5-17.4 59.2-41.1 59.2-76.2 0-41.5-12.9-74.8-38.3-99.2z", AndroidUtilities.dp(130.0f), AndroidUtilities.dp(130.0f));
        this.drawable = loadingStickerDrawable;
        this.stickerView.setImageDrawable(loadingStickerDrawable);
        addView(this.stickerView, LayoutHelper.createLinear(130, 130, 49, 0, 2, 0, 0));
        TextView textView = new TextView(context2);
        this.titleTextView = textView;
        textView.setTextSize(1, 20.0f);
        this.titleTextView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.titleTextView.setGravity(1);
        this.titleTextView.setText(LocaleController.getString("NoContactsYet", NUM));
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setMaxWidth(AndroidUtilities.dp(260.0f));
        addView(this.titleTextView, LayoutHelper.createLinear(-2, -2, 49, 0, 18, 0, 14));
        LinearLayout linearLayout = new LinearLayout(context2);
        linearLayout.setOrientation(1);
        addView(linearLayout, LayoutHelper.createLinear(-2, -2, 49));
        int i = 0;
        while (true) {
            int i2 = 3;
            if (i < 3) {
                LinearLayout linearLayout2 = new LinearLayout(context2);
                linearLayout2.setOrientation(0);
                linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-2, -2, LocaleController.isRTL ? 5 : 3, 0, 8, 0, 0));
                ImageView imageView = new ImageView(context2);
                imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor("windowBackgroundWhiteGrayText"), PorterDuff.Mode.MULTIPLY));
                imageView.setImageResource(NUM);
                this.imageViews.add(imageView);
                TextView textView2 = new TextView(context2);
                textView2.setTextSize(1, 15.0f);
                textView2.setTextColor(Theme.getColor("windowBackgroundWhiteGrayText"));
                textView2.setMaxWidth(AndroidUtilities.dp(260.0f));
                this.textViews.add(textView2);
                textView2.setGravity((LocaleController.isRTL ? 5 : i2) | 16);
                if (i == 0) {
                    textView2.setText(LocaleController.getString("NoContactsYetLine1", NUM));
                } else if (i == 1) {
                    textView2.setText(LocaleController.getString("NoContactsYetLine2", NUM));
                } else if (i == 2) {
                    textView2.setText(LocaleController.getString("NoContactsYetLine3", NUM));
                }
                if (LocaleController.isRTL) {
                    linearLayout2.addView(textView2, LayoutHelper.createLinear(-2, -2));
                    linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2, 8.0f, 7.0f, 0.0f, 0.0f));
                } else {
                    linearLayout2.addView(imageView, LayoutHelper.createLinear(-2, -2, 0.0f, 8.0f, 8.0f, 0.0f));
                    linearLayout2.addView(textView2, LayoutHelper.createLinear(-2, -2));
                }
                i++;
            } else {
                return;
            }
        }
    }

    private void setSticker() {
        TLRPC$TL_messages_stickerSet stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByName("tg_placeholders");
        if (stickerSetByName == null) {
            stickerSetByName = MediaDataController.getInstance(this.currentAccount).getStickerSetByEmojiOrName("tg_placeholders");
        }
        TLRPC$TL_messages_stickerSet tLRPC$TL_messages_stickerSet = stickerSetByName;
        if (tLRPC$TL_messages_stickerSet == null || tLRPC$TL_messages_stickerSet.documents.size() < 1) {
            MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName("tg_placeholders", false, true);
            this.stickerView.setImageDrawable(this.drawable);
            return;
        }
        this.stickerView.setImage(ImageLocation.getForDocument(tLRPC$TL_messages_stickerSet.documents.get(0)), "130_130", "tgs", (Drawable) this.drawable, (Object) tLRPC$TL_messages_stickerSet);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setSticker();
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.diceStickersDidLoad);
    }

    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.diceStickersDidLoad && "tg_placeholders".equals(objArr[0])) {
            setSticker();
        }
    }
}
