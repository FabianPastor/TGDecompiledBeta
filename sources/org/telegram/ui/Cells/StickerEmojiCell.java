package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC$Document;
import org.telegram.tgnet.TLRPC$DocumentAttribute;
import org.telegram.tgnet.TLRPC$PhotoSize;
import org.telegram.tgnet.TLRPC$TL_documentAttributeSticker;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;

public class StickerEmojiCell extends FrameLayout {
    private static AccelerateInterpolator interpolator = new AccelerateInterpolator(0.5f);
    private float alpha = 1.0f;
    private boolean changingAlpha;
    private int currentAccount = UserConfig.selectedAccount;
    private TextView emojiTextView;
    private boolean fromEmojiPanel;
    private BackupImageView imageView;
    private long lastUpdateTime;
    private Object parentObject;
    private boolean recent;
    private float scale;
    private boolean scaled;
    private TLRPC$Document sticker;
    private long time;

    public StickerEmojiCell(Context context, boolean z) {
        super(context);
        this.fromEmojiPanel = z;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        backupImageView.setAspectFit(true);
        this.imageView.setLayerNum(1);
        addView(this.imageView, LayoutHelper.createFrame(66, 66, 17));
        TextView textView = new TextView(context);
        this.emojiTextView = textView;
        textView.setTextSize(1, 16.0f);
        addView(this.emojiTextView, LayoutHelper.createFrame(28, 28, 85));
        setFocusable(true);
    }

    public TLRPC$Document getSticker() {
        return this.sticker;
    }

    public Object getParentObject() {
        return this.parentObject;
    }

    public boolean isRecent() {
        return this.recent;
    }

    public void setRecent(boolean z) {
        this.recent = z;
    }

    public void setSticker(TLRPC$Document tLRPC$Document, Object obj, boolean z) {
        setSticker(tLRPC$Document, obj, (String) null, z);
    }

    public void setSticker(TLRPC$Document tLRPC$Document, Object obj, String str, boolean z) {
        boolean z2;
        TLRPC$Document tLRPC$Document2 = tLRPC$Document;
        String str2 = str;
        if (tLRPC$Document2 != null) {
            this.sticker = tLRPC$Document2;
            this.parentObject = obj;
            TLRPC$PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(tLRPC$Document2.thumbs, 90);
            boolean z3 = this.fromEmojiPanel;
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tLRPC$Document2, z3 ? "emptyListPlaceholder" : "windowBackgroundGray", z3 ? 0.2f : 1.0f);
            if (MessageObject.canAutoplayAnimatedSticker(tLRPC$Document)) {
                if (svgThumb != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "80_80", (String) null, (Drawable) svgThumb, this.parentObject);
                } else if (closestPhotoSizeWithSize != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "80_80", ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document2), (String) null, 0, this.parentObject);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), "80_80", (String) null, (Drawable) null, this.parentObject);
                }
            } else if (svgThumb != null) {
                if (closestPhotoSizeWithSize != null) {
                    this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document2), (String) null, "webp", (Drawable) svgThumb, this.parentObject);
                } else {
                    this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), (String) null, "webp", (Drawable) svgThumb, this.parentObject);
                }
            } else if (closestPhotoSizeWithSize != null) {
                this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, tLRPC$Document2), (String) null, "webp", (Drawable) null, this.parentObject);
            } else {
                this.imageView.setImage(ImageLocation.getForDocument(tLRPC$Document), (String) null, "webp", (Drawable) null, this.parentObject);
            }
            if (str2 != null) {
                TextView textView = this.emojiTextView;
                textView.setText(Emoji.replaceEmoji(str2, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                this.emojiTextView.setVisibility(0);
            } else if (z) {
                int i = 0;
                while (true) {
                    if (i >= tLRPC$Document2.attributes.size()) {
                        break;
                    }
                    TLRPC$DocumentAttribute tLRPC$DocumentAttribute = tLRPC$Document2.attributes.get(i);
                    if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                        String str3 = tLRPC$DocumentAttribute.alt;
                        if (str3 != null && str3.length() > 0) {
                            TextView textView2 = this.emojiTextView;
                            textView2.setText(Emoji.replaceEmoji(tLRPC$DocumentAttribute.alt, textView2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                            z2 = true;
                        }
                    } else {
                        i++;
                    }
                }
                z2 = false;
                if (!z2) {
                    this.emojiTextView.setText(Emoji.replaceEmoji(MediaDataController.getInstance(this.currentAccount).getEmojiForSticker(this.sticker.id), this.emojiTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                }
                this.emojiTextView.setVisibility(0);
            } else {
                this.emojiTextView.setVisibility(4);
            }
        }
    }

    public void disable() {
        this.changingAlpha = true;
        this.alpha = 0.5f;
        this.time = 0;
        this.imageView.getImageReceiver().setAlpha(this.alpha);
        this.imageView.invalidate();
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public void setScaled(boolean z) {
        this.scaled = z;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public boolean isDisabled() {
        return this.changingAlpha;
    }

    public boolean showingBitmap() {
        return this.imageView.getImageReceiver().getBitmap() != null;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public void invalidate() {
        this.emojiTextView.invalidate();
        super.invalidate();
    }

    /* access modifiers changed from: protected */
    public boolean drawChild(Canvas canvas, View view, long j) {
        boolean z;
        boolean drawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && (this.changingAlpha || ((z && this.scale != 0.8f) || (!(z = this.scaled) && this.scale != 1.0f)))) {
            long currentTimeMillis = System.currentTimeMillis();
            long j2 = currentTimeMillis - this.lastUpdateTime;
            this.lastUpdateTime = currentTimeMillis;
            if (this.changingAlpha) {
                long j3 = this.time + j2;
                this.time = j3;
                if (j3 > 1050) {
                    this.time = 1050;
                }
                float interpolation = (interpolator.getInterpolation(((float) this.time) / 1050.0f) * 0.5f) + 0.5f;
                this.alpha = interpolation;
                if (interpolation >= 1.0f) {
                    this.changingAlpha = false;
                    this.alpha = 1.0f;
                }
                this.imageView.getImageReceiver().setAlpha(this.alpha);
            } else {
                if (this.scaled) {
                    float f = this.scale;
                    if (f != 0.8f) {
                        float f2 = f - (((float) j2) / 400.0f);
                        this.scale = f2;
                        if (f2 < 0.8f) {
                            this.scale = 0.8f;
                        }
                    }
                }
                float f3 = this.scale + (((float) j2) / 400.0f);
                this.scale = f3;
                if (f3 > 1.0f) {
                    this.scale = 1.0f;
                }
            }
            this.imageView.setScaleX(this.scale);
            this.imageView.setScaleY(this.scale);
            this.imageView.invalidate();
            invalidate();
        }
        return drawChild;
    }

    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        String string = LocaleController.getString("AttachSticker", NUM);
        if (this.sticker != null) {
            int i = 0;
            while (true) {
                if (i >= this.sticker.attributes.size()) {
                    break;
                }
                TLRPC$DocumentAttribute tLRPC$DocumentAttribute = this.sticker.attributes.get(i);
                if (tLRPC$DocumentAttribute instanceof TLRPC$TL_documentAttributeSticker) {
                    String str = tLRPC$DocumentAttribute.alt;
                    if (str != null && str.length() > 0) {
                        TextView textView = this.emojiTextView;
                        textView.setText(Emoji.replaceEmoji(tLRPC$DocumentAttribute.alt, textView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16.0f), false));
                        string = tLRPC$DocumentAttribute.alt + " " + string;
                    }
                } else {
                    i++;
                }
            }
        }
        accessibilityNodeInfo.setContentDescription(string);
        accessibilityNodeInfo.setEnabled(true);
    }
}
