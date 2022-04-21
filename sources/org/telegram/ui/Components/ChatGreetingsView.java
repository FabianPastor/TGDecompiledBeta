package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class ChatGreetingsView extends LinearLayout {
    private final int currentAccount;
    private TextView descriptionView;
    boolean ignoreLayot;
    private Listener listener;
    private TLRPC.Document preloadedGreetingsSticker;
    private final Theme.ResourcesProvider resourcesProvider;
    public BackupImageView stickerToSendView;
    private TextView titleView;

    public interface Listener {
        void onGreetings(TLRPC.Document document);
    }

    public ChatGreetingsView(Context context, TLRPC.User user, int distance, int currentAccount2, TLRPC.Document sticker, Theme.ResourcesProvider resourcesProvider2) {
        super(context);
        setOrientation(1);
        this.currentAccount = currentAccount2;
        this.resourcesProvider = resourcesProvider2;
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTextSize(1, 14.0f);
        this.titleView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleView.setGravity(1);
        TextView textView2 = new TextView(context);
        this.descriptionView = textView2;
        textView2.setTextSize(1, 14.0f);
        this.descriptionView.setGravity(1);
        this.stickerToSendView = new BackupImageView(context);
        addView(this.titleView, LayoutHelper.createLinear(-1, -2, 20.0f, 14.0f, 20.0f, 14.0f));
        addView(this.descriptionView, LayoutHelper.createLinear(-1, -2, 20.0f, 12.0f, 20.0f, 0.0f));
        addView(this.stickerToSendView, LayoutHelper.createLinear(112, 112, 1, 0, 16, 0, 16));
        updateColors();
        if (distance <= 0) {
            this.titleView.setText(LocaleController.getString("NoMessages", NUM));
            this.descriptionView.setText(LocaleController.getString("NoMessagesGreetingsDescription", NUM));
        } else {
            this.titleView.setText(LocaleController.formatString("NearbyPeopleGreetingsMessage", NUM, user.first_name, LocaleController.formatDistance((float) distance, 1)));
            this.descriptionView.setText(LocaleController.getString("NearbyPeopleGreetingsDescription", NUM));
        }
        this.preloadedGreetingsSticker = sticker;
        if (sticker == null) {
            this.preloadedGreetingsSticker = MediaDataController.getInstance(currentAccount2).getGreetingsSticker();
        }
        setSticker(this.preloadedGreetingsSticker);
    }

    private void setSticker(TLRPC.Document sticker) {
        if (sticker != null) {
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(sticker, "chat_serviceBackground", 1.0f);
            if (svgThumb != null) {
                this.stickerToSendView.setImage(ImageLocation.getForDocument(sticker), createFilter(sticker), (Drawable) svgThumb, 0, (Object) sticker);
            } else {
                this.stickerToSendView.setImage(ImageLocation.getForDocument(sticker), createFilter(sticker), ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(sticker.thumbs, 90), sticker), (String) null, 0, (Object) sticker);
            }
            this.stickerToSendView.setOnClickListener(new ChatGreetingsView$$ExternalSyntheticLambda0(this, sticker));
        }
    }

    /* renamed from: lambda$setSticker$0$org-telegram-ui-Components-ChatGreetingsView  reason: not valid java name */
    public /* synthetic */ void m3903lambda$setSticker$0$orgtelegramuiComponentsChatGreetingsView(TLRPC.Document sticker, View v) {
        Listener listener2 = this.listener;
        if (listener2 != null) {
            listener2.onGreetings(sticker);
        }
    }

    public static String createFilter(TLRPC.Document document) {
        float maxWidth;
        float maxHeight;
        int photoWidth = 0;
        int photoHeight = 0;
        if (AndroidUtilities.isTablet()) {
            maxHeight = ((float) AndroidUtilities.getMinTabletSide()) * 0.4f;
            maxWidth = maxHeight;
        } else {
            maxHeight = ((float) Math.min(AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y)) * 0.5f;
            maxWidth = maxHeight;
        }
        int a = 0;
        while (true) {
            if (a >= document.attributes.size()) {
                break;
            }
            TLRPC.DocumentAttribute attribute = document.attributes.get(a);
            if (attribute instanceof TLRPC.TL_documentAttributeImageSize) {
                photoWidth = attribute.w;
                photoHeight = attribute.h;
                break;
            }
            a++;
        }
        if (MessageObject.isAnimatedStickerDocument(document, true) && photoWidth == 0 && photoHeight == 0) {
            photoHeight = 512;
            photoWidth = 512;
        }
        if (photoWidth == 0) {
            photoHeight = (int) maxHeight;
            photoWidth = photoHeight + AndroidUtilities.dp(100.0f);
        }
        int photoHeight2 = (int) (((float) photoHeight) * (maxWidth / ((float) photoWidth)));
        int photoWidth2 = (int) maxWidth;
        if (((float) photoHeight2) > maxHeight) {
            photoWidth2 = (int) (((float) photoWidth2) * (maxHeight / ((float) photoHeight2)));
            photoHeight2 = (int) maxHeight;
        }
        return String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (((float) photoWidth2) / AndroidUtilities.density)), Integer.valueOf((int) (((float) photoHeight2) / AndroidUtilities.density))});
    }

    private void updateColors() {
        this.titleView.setTextColor(getThemedColor("chat_serviceText"));
        this.descriptionView.setTextColor(getThemedColor("chat_serviceText"));
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        this.ignoreLayot = true;
        this.descriptionView.setVisibility(0);
        this.stickerToSendView.setVisibility(0);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (getMeasuredHeight() > View.MeasureSpec.getSize(heightMeasureSpec)) {
            this.descriptionView.setVisibility(8);
            this.stickerToSendView.setVisibility(8);
        } else {
            this.descriptionView.setVisibility(0);
            this.stickerToSendView.setVisibility(0);
        }
        this.ignoreLayot = false;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void requestLayout() {
        if (!this.ignoreLayot) {
            super.requestLayout();
        }
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        fetchSticker();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void fetchSticker() {
        if (this.preloadedGreetingsSticker == null) {
            TLRPC.Document greetingsSticker = MediaDataController.getInstance(this.currentAccount).getGreetingsSticker();
            this.preloadedGreetingsSticker = greetingsSticker;
            setSticker(greetingsSticker);
        }
    }

    private int getThemedColor(String key) {
        Theme.ResourcesProvider resourcesProvider2 = this.resourcesProvider;
        Integer color = resourcesProvider2 != null ? resourcesProvider2.getColor(key) : null;
        return color != null ? color.intValue() : Theme.getColor(key);
    }
}
