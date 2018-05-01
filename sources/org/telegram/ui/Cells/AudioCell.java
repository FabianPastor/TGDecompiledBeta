package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.C0446R;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AudioEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;

public class AudioCell extends FrameLayout {
    private AudioEntry audioEntry;
    private TextView authorTextView;
    private CheckBox checkBox;
    private int currentAccount = UserConfig.selectedAccount;
    private AudioCellDelegate delegate;
    private TextView genreTextView;
    private boolean needDivider;
    private ImageView playButton;
    private TextView timeTextView;
    private TextView titleTextView;

    /* renamed from: org.telegram.ui.Cells.AudioCell$1 */
    class C08691 implements OnClickListener {
        C08691() {
        }

        public void onClick(View view) {
            if (AudioCell.this.audioEntry == null) {
                return;
            }
            if (MediaController.getInstance().isPlayingMessage(AudioCell.this.audioEntry.messageObject) == null || MediaController.getInstance().isMessagePaused() != null) {
                view = new ArrayList();
                view.add(AudioCell.this.audioEntry.messageObject);
                if (MediaController.getInstance().setPlaylist(view, AudioCell.this.audioEntry.messageObject) != null) {
                    AudioCell.this.setPlayDrawable(true);
                    if (AudioCell.this.delegate != null) {
                        AudioCell.this.delegate.startedPlayingAudio(AudioCell.this.audioEntry.messageObject);
                        return;
                    }
                    return;
                }
                return;
            }
            MediaController.getInstance().pauseMessage(AudioCell.this.audioEntry.messageObject);
            AudioCell.this.setPlayDrawable(false);
        }
    }

    public interface AudioCellDelegate {
        void startedPlayingAudio(MessageObject messageObject);
    }

    public AudioCell(Context context) {
        Context context2 = context;
        super(context);
        this.playButton = new ImageView(context2);
        int i = 3;
        addView(this.playButton, LayoutHelper.createFrame(46, 46.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 0.0f : 13.0f, 13.0f, LocaleController.isRTL ? 13.0f : 0.0f, 0.0f));
        r0.playButton.setOnClickListener(new C08691());
        r0.titleTextView = new TextView(context2);
        r0.titleTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
        r0.titleTextView.setTextSize(1, 16.0f);
        r0.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        r0.titleTextView.setLines(1);
        r0.titleTextView.setMaxLines(1);
        r0.titleTextView.setSingleLine(true);
        r0.titleTextView.setEllipsize(TruncateAt.END);
        r0.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.titleTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 50.0f : 72.0f, 7.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        r0.genreTextView = new TextView(context2);
        r0.genreTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        r0.genreTextView.setTextSize(1, 14.0f);
        r0.genreTextView.setLines(1);
        r0.genreTextView.setMaxLines(1);
        r0.genreTextView.setSingleLine(true);
        r0.genreTextView.setEllipsize(TruncateAt.END);
        r0.genreTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.genreTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 50.0f : 72.0f, 28.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        r0.authorTextView = new TextView(context2);
        r0.authorTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2));
        r0.authorTextView.setTextSize(1, 14.0f);
        r0.authorTextView.setLines(1);
        r0.authorTextView.setMaxLines(1);
        r0.authorTextView.setSingleLine(true);
        r0.authorTextView.setEllipsize(TruncateAt.END);
        r0.authorTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        addView(r0.authorTextView, LayoutHelper.createFrame(-1, -2.0f, (LocaleController.isRTL ? 5 : 3) | 48, LocaleController.isRTL ? 50.0f : 72.0f, 44.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        r0.timeTextView = new TextView(context2);
        r0.timeTextView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText3));
        r0.timeTextView.setTextSize(1, 13.0f);
        r0.timeTextView.setLines(1);
        r0.timeTextView.setMaxLines(1);
        r0.timeTextView.setSingleLine(true);
        r0.timeTextView.setEllipsize(TruncateAt.END);
        r0.timeTextView.setGravity((LocaleController.isRTL ? 3 : 5) | 48);
        addView(r0.timeTextView, LayoutHelper.createFrame(-2, -2.0f, (LocaleController.isRTL ? 3 : 5) | 48, LocaleController.isRTL ? 18.0f : 0.0f, 11.0f, LocaleController.isRTL ? 0.0f : 18.0f, 0.0f));
        r0.checkBox = new CheckBox(context2, C0446R.drawable.round_check2);
        r0.checkBox.setVisibility(0);
        r0.checkBox.setColor(Theme.getColor(Theme.key_musicPicker_checkbox), Theme.getColor(Theme.key_musicPicker_checkboxCheck));
        View view = r0.checkBox;
        if (!LocaleController.isRTL) {
            i = 5;
        }
        addView(view, LayoutHelper.createFrame(22, 22.0f, i | 48, LocaleController.isRTL ? 18.0f : 0.0f, 39.0f, LocaleController.isRTL ? 0.0f : 18.0f, 0.0f));
    }

    private void setPlayDrawable(boolean z) {
        Drawable createSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(46.0f), Theme.getColor(Theme.key_musicPicker_buttonBackground), Theme.getColor(Theme.key_musicPicker_buttonBackground));
        z = getResources().getDrawable(z ? true : true);
        z.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_musicPicker_buttonIcon), Mode.MULTIPLY));
        Drawable combinedDrawable = new CombinedDrawable(createSimpleSelectorCircleDrawable, z);
        combinedDrawable.setCustomSize(AndroidUtilities.dp(46.0f), AndroidUtilities.dp(46.0f));
        this.playButton.setBackgroundDrawable(combinedDrawable);
    }

    public ImageView getPlayButton() {
        return this.playButton;
    }

    public TextView getTitleTextView() {
        return this.titleTextView;
    }

    public TextView getGenreTextView() {
        return this.genreTextView;
    }

    public TextView getTimeTextView() {
        return this.timeTextView;
    }

    public TextView getAuthorTextView() {
        return this.authorTextView;
    }

    public CheckBox getCheckBox() {
        return this.checkBox;
    }

    protected void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(72.0f) + this.needDivider, NUM));
    }

    public void setAudio(AudioEntry audioEntry, boolean z, boolean z2) {
        this.audioEntry = audioEntry;
        this.titleTextView.setText(this.audioEntry.title);
        this.genreTextView.setText(this.audioEntry.genre);
        this.authorTextView.setText(this.audioEntry.author);
        this.timeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(this.audioEntry.duration / 60), Integer.valueOf(this.audioEntry.duration % 60)}));
        audioEntry = (MediaController.getInstance().isPlayingMessage(this.audioEntry.messageObject) == null || MediaController.getInstance().isMessagePaused() != null) ? null : 1;
        setPlayDrawable(audioEntry);
        this.needDivider = z;
        setWillNotDraw(z ^ 1);
        this.checkBox.setChecked(z2, false);
    }

    public void setChecked(boolean z) {
        this.checkBox.setChecked(z, true);
    }

    public void setDelegate(AudioCellDelegate audioCellDelegate) {
        this.delegate = audioCellDelegate;
    }

    public AudioEntry getAudioEntry() {
        return this.audioEntry;
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), Theme.dividerPaint);
        }
    }
}
