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
    private ImageView iconView;
    private Paint outlinePaint = new Paint(1);
    private float outlineProgress;
    private Path path = new Path();
    private float radius = ((float) AndroidUtilities.dp(32.0f));
    private BackupImageView reactView;
    private RectF rect = new RectF();

    public ReactionTabHolderView(Context context) {
        super(context);
        this.iconView = new ImageView(context);
        Drawable mutate = ContextCompat.getDrawable(context, NUM).mutate();
        mutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor("avatar_nameInMessageBlue"), PorterDuff.Mode.MULTIPLY));
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
        this.outlinePaint.setColor(Theme.getColor("avatar_nameInMessageBlue"));
        this.bgPaint.setColor(Theme.getColor("avatar_nameInMessageBlue"));
        this.bgPaint.setAlpha(16);
        View view = new View(context);
        view.setBackground(Theme.getSelectorDrawable(this.bgPaint.getColor(), false));
        addView(view, LayoutHelper.createFrame(-1, -1.0f));
        setWillNotDraw(false);
    }

    public void setOutlineProgress(float f) {
        this.outlineProgress = f;
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
        int save = canvas.save();
        this.path.rewind();
        this.rect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        Path path2 = this.path;
        RectF rectF = this.rect;
        float f = this.radius;
        path2.addRoundRect(rectF, f, f, Path.Direction.CW);
        canvas.clipPath(this.path);
        RectF rectF2 = this.rect;
        float f2 = this.radius;
        canvas.drawRoundRect(rectF2, f2, f2, this.bgPaint);
        super.dispatchDraw(canvas);
        this.outlinePaint.setAlpha((int) (this.outlineProgress * 255.0f));
        float strokeWidth = this.outlinePaint.getStrokeWidth();
        this.rect.set(strokeWidth, strokeWidth, ((float) getWidth()) - strokeWidth, ((float) getHeight()) - strokeWidth);
        RectF rectF3 = this.rect;
        float f3 = this.radius;
        canvas.drawRoundRect(rectF3, f3, f3, this.outlinePaint);
        canvas.restoreToCount(save);
    }
}
