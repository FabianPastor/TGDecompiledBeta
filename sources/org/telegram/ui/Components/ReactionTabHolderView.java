package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.tgnet.TLRPC$TL_availableReaction;
import org.telegram.tgnet.TLRPC$TL_reactionCount;
import org.telegram.ui.ActionBar.Theme;

public class ReactionTabHolderView extends FrameLayout {
    private Paint bgPaint = new Paint(1);
    private TextView counterView;
    Drawable drawable;
    private ImageView iconView;
    private Paint outlinePaint = new Paint(1);
    private float outlineProgress;
    private float radius;
    private BackupImageView reactView;
    private RectF rect;

    public ReactionTabHolderView(Context context) {
        super(context);
        new Path();
        this.rect = new RectF();
        this.radius = (float) AndroidUtilities.dp(32.0f);
        View view = new View(context);
        view.setBackground(Theme.createSimpleSelectorRoundRectDrawable((int) this.radius, 0, Theme.getColor("chat_inReactionButtonTextSelected")));
        addView(view, LayoutHelper.createFrame(-1, -1.0f));
        this.iconView = new ImageView(context);
        Drawable mutate = ContextCompat.getDrawable(context, NUM).mutate();
        this.drawable = mutate;
        this.iconView.setImageDrawable(mutate);
        addView(this.iconView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 8.0f, 0.0f, 8.0f, 0.0f));
        BackupImageView backupImageView = new BackupImageView(context);
        this.reactView = backupImageView;
        addView(backupImageView, LayoutHelper.createFrameRelatively(24.0f, 24.0f, 8388627, 8.0f, 0.0f, 8.0f, 0.0f));
        TextView textView = new TextView(context);
        this.counterView = textView;
        textView.setTextColor(Theme.getColor("avatar_nameInMessageBlue"));
        this.counterView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        addView(this.counterView, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388627, 40.0f, 0.0f, 8.0f, 0.0f));
        this.outlinePaint.setStyle(Paint.Style.STROKE);
        this.outlinePaint.setStrokeWidth((float) AndroidUtilities.dp(1.0f));
        setWillNotDraw(false);
        setOutlineProgress(this.outlineProgress);
    }

    public void setOutlineProgress(float f) {
        this.outlineProgress = f;
        int color = Theme.getColor("chat_inReactionButtonBackground");
        int alphaComponent = ColorUtils.setAlphaComponent(Theme.getColor("chat_inReactionButtonBackground"), 16);
        int blendARGB = ColorUtils.blendARGB(Theme.getColor("chat_inReactionButtonText"), Theme.getColor("chat_inReactionButtonTextSelected"), f);
        this.bgPaint.setColor(ColorUtils.blendARGB(alphaComponent, color, f));
        this.counterView.setTextColor(blendARGB);
        this.drawable.setColorFilter(new PorterDuffColorFilter(blendARGB, PorterDuff.Mode.MULTIPLY));
        invalidate();
    }

    public void setCounter(int i) {
        this.counterView.setText(String.format("%s", new Object[]{LocaleController.formatShortNumber(i, (int[]) null)}));
        this.iconView.setVisibility(0);
        this.reactView.setVisibility(8);
    }

    public void setCounter(int i, TLRPC$TL_reactionCount tLRPC$TL_reactionCount) {
        this.counterView.setText(String.format("%s", new Object[]{LocaleController.formatShortNumber(tLRPC$TL_reactionCount.count, (int[]) null)}));
        String str = tLRPC$TL_reactionCount.reaction;
        for (TLRPC$TL_availableReaction next : MediaDataController.getInstance(i).getReactionsList()) {
            if (next.reaction.equals(str)) {
                this.reactView.setImage(ImageLocation.getForDocument(next.static_icon), "50_50", "webp", (Drawable) DocumentObject.getSvgThumb(next.static_icon, "windowBackgroundGray", 1.0f), (Object) next);
                this.reactView.setVisibility(0);
                this.iconView.setVisibility(8);
                return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dispatchDraw(Canvas canvas) {
        this.rect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        RectF rectF = this.rect;
        float f = this.radius;
        canvas.drawRoundRect(rectF, f, f, this.bgPaint);
        super.dispatchDraw(canvas);
    }
}
