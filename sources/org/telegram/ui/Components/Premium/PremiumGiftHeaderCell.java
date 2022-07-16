package org.telegram.ui.Components.Premium;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLRPC$User;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.StarParticlesView;

public class PremiumGiftHeaderCell extends LinearLayout {
    private AvatarDrawable avatarDrawable = new AvatarDrawable();
    private BackupImageView avatarImageView;
    private StarParticlesView.Drawable drawable;
    private TextView subtitleView;
    private TextView titleView;

    public PremiumGiftHeaderCell(Context context) {
        super(context);
        setOrientation(1);
        BackupImageView backupImageView = new BackupImageView(context);
        this.avatarImageView = backupImageView;
        backupImageView.setRoundRadius(AndroidUtilities.dp(50.0f));
        addView(this.avatarImageView, LayoutHelper.createLinear(100, 100, 1, 0, 28, 0, 0));
        TextView textView = new TextView(context);
        this.titleView = textView;
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleView.setTextSize(1, 22.0f);
        this.titleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.titleView.setGravity(1);
        addView(this.titleView, LayoutHelper.createLinear(-2, -2, 1, 24, 24, 24, 0));
        TextView textView2 = new TextView(context);
        this.subtitleView = textView2;
        textView2.setTextSize(1, 15.0f);
        this.subtitleView.setTextColor(Theme.getColor("windowBackgroundWhiteBlackText"));
        this.subtitleView.setGravity(1);
        addView(this.subtitleView, LayoutHelper.createFrame(-2, -2.0f, 1, 24.0f, 8.0f, 24.0f, 28.0f));
        setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        StarParticlesView.Drawable drawable2 = new StarParticlesView.Drawable(50);
        this.drawable = drawable2;
        drawable2.useGradient = true;
        drawable2.roundEffect = true;
        drawable2.init();
        setWillNotDraw(false);
    }

    /* access modifiers changed from: protected */
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        float x = this.avatarImageView.getX() + (((float) this.avatarImageView.getWidth()) / 2.0f);
        float paddingTop = ((((float) this.avatarImageView.getPaddingTop()) + this.avatarImageView.getY()) + (((float) this.avatarImageView.getHeight()) / 2.0f)) - ((float) AndroidUtilities.dp(3.0f));
        float dp = (float) AndroidUtilities.dp(32.0f);
        this.drawable.rect.set(x - dp, paddingTop - dp, x + dp, paddingTop + dp);
        if (z) {
            this.drawable.resetPositions();
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.drawable.onDraw(canvas);
        invalidate();
    }

    public void bind(TLRPC$User tLRPC$User) {
        this.avatarDrawable.setInfo(tLRPC$User);
        this.avatarImageView.setForUserOrChat(tLRPC$User, this.avatarDrawable);
        this.titleView.setText(AndroidUtilities.replaceTags(LocaleController.getString(NUM)));
        this.subtitleView.setText(AndroidUtilities.replaceTags(LocaleController.formatString(NUM, tLRPC$User.first_name)));
    }
}
