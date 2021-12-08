package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.Theme;

public class SenderSelectView extends View {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private ImageReceiver avatarImage = new ImageReceiver(this);
    private Paint backgroundPaint = new Paint(1);
    private RectF mTempRect = new RectF();
    private MenuDrawable menuDrawable = new MenuDrawable() {
        public void invalidateSelf() {
            super.invalidateSelf();
            SenderSelectView.this.invalidate();
        }
    };
    private Drawable selectorDrawable;

    public SenderSelectView(Context context) {
        super(context);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.menuDrawable.setMiniIcon(true);
        this.menuDrawable.setRotateToBack(false);
        this.menuDrawable.setRotation(0.0f, false);
        this.menuDrawable.setRoundCap();
        this.menuDrawable.setCallback(this);
        updateColors();
    }

    public SenderSelectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.menuDrawable.setMiniIcon(true);
        this.menuDrawable.setRotateToBack(false);
        this.menuDrawable.setRotation(0.0f, false);
        this.menuDrawable.setRoundCap();
        this.menuDrawable.setCallback(this);
        updateColors();
    }

    public SenderSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.avatarImage.setRoundRadius(AndroidUtilities.dp(28.0f));
        this.menuDrawable.setMiniIcon(true);
        this.menuDrawable.setRotateToBack(false);
        this.menuDrawable.setRotation(0.0f, false);
        this.menuDrawable.setRoundCap();
        this.menuDrawable.setCallback(this);
        updateColors();
    }

    private void updateColors() {
        this.backgroundPaint.setColor(Theme.getColor("chat_messagePanelVoiceBackground"));
        int textColor = Theme.getColor("chat_messagePanelVoicePressed");
        this.menuDrawable.setBackColor(textColor);
        this.menuDrawable.setIconColor(textColor);
        Drawable createSimpleSelectorRoundRectDrawable = Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(16.0f), 0, Theme.getColor("windowBackgroundWhite"));
        this.selectorDrawable = createSimpleSelectorRoundRectDrawable;
        createSimpleSelectorRoundRectDrawable.setCallback(this);
    }

    /* access modifiers changed from: protected */
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    /* access modifiers changed from: protected */
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        this.avatarImage.setImageCoords(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.avatarImage.draw(canvas);
        int alpha = (int) (this.menuDrawable.getCurrentRotation() * 255.0f);
        this.backgroundPaint.setAlpha(alpha);
        this.mTempRect.set(0.0f, 0.0f, (float) getWidth(), (float) getHeight());
        canvas.drawRoundRect(this.mTempRect, (float) AndroidUtilities.dp(16.0f), (float) AndroidUtilities.dp(16.0f), this.backgroundPaint);
        canvas.save();
        canvas.translate((float) AndroidUtilities.dp(4.0f), (float) AndroidUtilities.dp(4.0f));
        this.menuDrawable.setAlpha(alpha);
        this.menuDrawable.setBounds(0, 0, getWidth(), getHeight());
        this.menuDrawable.draw(canvas);
        canvas.restore();
        this.selectorDrawable.setBounds(0, 0, getWidth(), getHeight());
        this.selectorDrawable.draw(canvas);
    }

    public void setAvatar(TLObject obj) {
        this.avatarDrawable.setInfo(obj);
        this.avatarImage.setForUserOrChat(obj, this.avatarDrawable);
    }

    public void setProgress(float progress) {
        setProgress(progress, true);
    }

    public void setProgress(float progress, boolean animate) {
        this.menuDrawable.setRotation(progress, animate);
    }

    public float getProgress() {
        return this.menuDrawable.getCurrentRotation();
    }

    /* access modifiers changed from: protected */
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || this.selectorDrawable == who;
    }

    /* access modifiers changed from: protected */
    public void drawableStateChanged() {
        super.drawableStateChanged();
        this.selectorDrawable.setState(getDrawableState());
    }

    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        this.selectorDrawable.jumpToCurrentState();
    }
}
