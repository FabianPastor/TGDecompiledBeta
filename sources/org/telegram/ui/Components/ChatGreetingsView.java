package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Point;
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
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$TL_documentAttributeImageSize;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;

public class ChatGreetingsView extends LinearLayout {
    private final int currentAccount;
    private TextView descriptionView;
    boolean ignoreLayot;
    private Listener listener;
    private TLRPC$Document preloadedGreetingsSticker;
    public BackupImageView stickerToSendView;
    private TextView titleView;

    public interface Listener {
        void onGreetings(TLRPC$Document tLRPC$Document);
    }

    public ChatGreetingsView(Context context, TLRPC$User tLRPC$User, int i, int i2, TLRPC$Document tLRPC$Document) {
        super(context);
        setOrientation(1);
        this.currentAccount = i2;
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
        if (i <= 0) {
            this.titleView.setText(LocaleController.getString("NoMessages", NUM));
            this.descriptionView.setText(LocaleController.getString("NoMessagesGreetingsDescription", NUM));
        } else {
            this.titleView.setText(LocaleController.formatString("NearbyPeopleGreetingsMessage", NUM, tLRPC$User.first_name, LocaleController.formatDistance((float) i, 1)));
            this.descriptionView.setText(LocaleController.getString("NearbyPeopleGreetingsDescription", NUM));
        }
        this.preloadedGreetingsSticker = tLRPC$Document;
        if (tLRPC$Document == null) {
            this.preloadedGreetingsSticker = MediaDataController.getInstance(i2).getGreetingsSticker();
        }
        setSticker(this.preloadedGreetingsSticker);
    }

    private void setSticker(TLRPC$Document tLRPC$Document) {
        if (tLRPC$Document != null) {
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$Document, "chat_serviceBackground", 1.0f);
            if (svgThumb != null) {
                this.stickerToSendView.setImage(ImageLocation.getForDocument(tLRPC$Document), createFilter(tLRPC$Document), (Drawable) svgThumb, 0, (Object) tLRPC$Document);
            } else {
                this.stickerToSendView.setImage(ImageLocation.getForDocument(tLRPC$Document), createFilter(tLRPC$Document), ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document.thumbs, 90), tLRPC$Document), (String) null, 0, (Object) tLRPC$Document);
            }
            this.stickerToSendView.setOnClickListener(new View.OnClickListener(tLRPC$Document) {
                public final /* synthetic */ TLRPC$Document f$1;

                {
                    this.f$1 = r2;
                }

                public final void onClick(View view) {
                    ChatGreetingsView.this.lambda$setSticker$0$ChatGreetingsView(this.f$1, view);
                }
            });
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: lambda$setSticker$0 */
    public /* synthetic */ void lambda$setSticker$0$ChatGreetingsView(TLRPC$Document tLRPC$Document, View view) {
        Listener listener2 = this.listener;
        if (listener2 != null) {
            listener2.onGreetings(tLRPC$Document);
        }
    }

    public static String createFilter(TLRPC$Document tLRPC$Document) {
        float f;
        float f2;
        int i;
        int i2;
        if (AndroidUtilities.isTablet()) {
            f2 = (float) AndroidUtilities.getMinTabletSide();
            f = 0.4f;
        } else {
            Point point = AndroidUtilities.displaySize;
            f2 = (float) Math.min(point.x, point.y);
            f = 0.5f;
        }
        float f3 = f2 * f;
        int i3 = 0;
        while (true) {
            if (i3 >= tLRPC$Document.attributes.size()) {
                i = 0;
                i2 = 0;
                break;
            }
            TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document.attributes.get(i3);
            if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeImageSize) {
                i = tLRPC$DocumentAttribute.w;
                i2 = tLRPC$DocumentAttribute.h;
                break;
            }
            i3++;
        }
        if (MessageObject.isAnimatedStickerDocument(tLRPC$Document, true) && i == 0 && i2 == 0) {
            i = 512;
            i2 = 512;
        }
        if (i == 0) {
            i2 = (int) f3;
            i = i2 + AndroidUtilities.dp(100.0f);
        }
        int i4 = (int) (((float) i2) * (f3 / ((float) i)));
        int i5 = (int) f3;
        float f4 = (float) i4;
        if (f4 > f3) {
            int i6 = i5;
            i5 = (int) (((float) i5) * (f3 / f4));
            i4 = i6;
        }
        float f5 = (float) i5;
        float f6 = AndroidUtilities.density;
        return String.format(Locale.US, "%d_%d", new Object[]{Integer.valueOf((int) (f5 / f6)), Integer.valueOf((int) (((float) i4) / f6))});
    }

    private void updateColors() {
        this.titleView.setTextColor(Theme.getColor("chat_serviceText"));
        this.descriptionView.setTextColor(Theme.getColor("chat_serviceText"));
    }

    public void setListener(Listener listener2) {
        this.listener = listener2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        this.ignoreLayot = true;
        this.descriptionView.setVisibility(0);
        this.stickerToSendView.setVisibility(0);
        super.onMeasure(i, i2);
        if (getMeasuredHeight() > View.MeasureSpec.getSize(i2)) {
            this.descriptionView.setVisibility(8);
            this.stickerToSendView.setVisibility(8);
        } else {
            this.descriptionView.setVisibility(0);
            this.stickerToSendView.setVisibility(0);
        }
        this.ignoreLayot = false;
        super.onMeasure(i, i2);
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
            TLRPC$Document greetingsSticker = MediaDataController.getInstance(this.currentAccount).getGreetingsSticker();
            this.preloadedGreetingsSticker = greetingsSticker;
            setSticker(greetingsSticker);
        }
    }
}
