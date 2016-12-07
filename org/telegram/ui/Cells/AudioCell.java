package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaController.AudioEntry;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.beta.R;
import org.telegram.messenger.exoplayer.C;
import org.telegram.messenger.volley.DefaultRetryPolicy;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.LayoutHelper;

public class AudioCell extends FrameLayout {
    private static Paint paint;
    private AudioEntry audioEntry;
    private TextView authorTextView;
    private CheckBox checkBox;
    private AudioCellDelegate delegate;
    private TextView genreTextView;
    private boolean needDivider;
    private ImageView playButton;
    private TextView timeTextView;
    private TextView titleTextView;

    public interface AudioCellDelegate {
        void startedPlayingAudio(MessageObject messageObject);
    }

    public AudioCell(Context context) {
        float f;
        float f2;
        int i;
        int i2 = 3;
        super(context);
        if (paint == null) {
            paint = new Paint();
            paint.setColor(-2500135);
            paint.setStrokeWidth(DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        }
        this.playButton = new ImageView(context);
        this.playButton.setScaleType(ScaleType.CENTER);
        View view = this.playButton;
        int i3 = (LocaleController.isRTL ? 5 : 3) | 48;
        if (LocaleController.isRTL) {
            f = 0.0f;
        } else {
            f = 13.0f;
        }
        if (LocaleController.isRTL) {
            f2 = 13.0f;
        } else {
            f2 = 0.0f;
        }
        addView(view, LayoutHelper.createFrame(46, 46.0f, i3, f, 13.0f, f2, 0.0f));
        this.playButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                if (AudioCell.this.audioEntry == null) {
                    return;
                }
                if (!MediaController.getInstance().isPlayingAudio(AudioCell.this.audioEntry.messageObject) || MediaController.getInstance().isAudioPaused()) {
                    ArrayList<MessageObject> arrayList = new ArrayList();
                    arrayList.add(AudioCell.this.audioEntry.messageObject);
                    if (MediaController.getInstance().setPlaylist(arrayList, AudioCell.this.audioEntry.messageObject)) {
                        AudioCell.this.playButton.setImageResource(R.drawable.audiosend_pause);
                        if (AudioCell.this.delegate != null) {
                            AudioCell.this.delegate.startedPlayingAudio(AudioCell.this.audioEntry.messageObject);
                            return;
                        }
                        return;
                    }
                    return;
                }
                MediaController.getInstance().pauseAudio(AudioCell.this.audioEntry.messageObject);
                AudioCell.this.playButton.setImageResource(R.drawable.audiosend_play);
            }
        });
        this.titleTextView = new TextView(context);
        this.titleTextView.setTextColor(-14606047);
        this.titleTextView.setTextSize(1, 16.0f);
        this.titleTextView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.titleTextView.setLines(1);
        this.titleTextView.setMaxLines(1);
        this.titleTextView.setSingleLine(true);
        this.titleTextView.setEllipsize(TruncateAt.END);
        this.titleTextView.setGravity((LocaleController.isRTL ? 5 : 3) | 48);
        view = this.titleTextView;
        if (LocaleController.isRTL) {
            i3 = 5;
        } else {
            i3 = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -2.0f, i3 | 48, LocaleController.isRTL ? 50.0f : 72.0f, 7.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        this.genreTextView = new TextView(context);
        this.genreTextView.setTextColor(-7697782);
        this.genreTextView.setTextSize(1, 14.0f);
        this.genreTextView.setLines(1);
        this.genreTextView.setMaxLines(1);
        this.genreTextView.setSingleLine(true);
        this.genreTextView.setEllipsize(TruncateAt.END);
        TextView textView = this.genreTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        textView.setGravity(i | 48);
        view = this.genreTextView;
        if (LocaleController.isRTL) {
            i3 = 5;
        } else {
            i3 = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -2.0f, i3 | 48, LocaleController.isRTL ? 50.0f : 72.0f, 28.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        this.authorTextView = new TextView(context);
        this.authorTextView.setTextColor(-7697782);
        this.authorTextView.setTextSize(1, 14.0f);
        this.authorTextView.setLines(1);
        this.authorTextView.setMaxLines(1);
        this.authorTextView.setSingleLine(true);
        this.authorTextView.setEllipsize(TruncateAt.END);
        textView = this.authorTextView;
        if (LocaleController.isRTL) {
            i = 5;
        } else {
            i = 3;
        }
        textView.setGravity(i | 48);
        view = this.authorTextView;
        if (LocaleController.isRTL) {
            i3 = 5;
        } else {
            i3 = 3;
        }
        addView(view, LayoutHelper.createFrame(-1, -2.0f, i3 | 48, LocaleController.isRTL ? 50.0f : 72.0f, 44.0f, LocaleController.isRTL ? 72.0f : 50.0f, 0.0f));
        this.timeTextView = new TextView(context);
        this.timeTextView.setTextColor(-6710887);
        this.timeTextView.setTextSize(1, 13.0f);
        this.timeTextView.setLines(1);
        this.timeTextView.setMaxLines(1);
        this.timeTextView.setSingleLine(true);
        this.timeTextView.setEllipsize(TruncateAt.END);
        textView = this.timeTextView;
        if (LocaleController.isRTL) {
            i = 3;
        } else {
            i = 5;
        }
        textView.setGravity(i | 48);
        View view2 = this.timeTextView;
        if (LocaleController.isRTL) {
            i3 = 3;
        } else {
            i3 = 5;
        }
        addView(view2, LayoutHelper.createFrame(-2, -2.0f, i3 | 48, LocaleController.isRTL ? 18.0f : 0.0f, 11.0f, LocaleController.isRTL ? 0.0f : 18.0f, 0.0f));
        this.checkBox = new CheckBox(context, R.drawable.round_check2);
        this.checkBox.setVisibility(0);
        this.checkBox.setColor(-14043401);
        view2 = this.checkBox;
        if (!LocaleController.isRTL) {
            i2 = 5;
        }
        addView(view2, LayoutHelper.createFrame(22, 22.0f, i2 | 48, LocaleController.isRTL ? 18.0f : 0.0f, 39.0f, LocaleController.isRTL ? 0.0f : 18.0f, 0.0f));
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec((this.needDivider ? 1 : 0) + AndroidUtilities.dp(72.0f), C.ENCODING_PCM_32BIT));
    }

    public void setAudio(AudioEntry entry, boolean divider, boolean checked) {
        boolean z;
        this.audioEntry = entry;
        this.titleTextView.setText(this.audioEntry.title);
        this.genreTextView.setText(this.audioEntry.genre);
        this.authorTextView.setText(this.audioEntry.author);
        this.timeTextView.setText(String.format("%d:%02d", new Object[]{Integer.valueOf(this.audioEntry.duration / 60), Integer.valueOf(this.audioEntry.duration % 60)}));
        ImageView imageView = this.playButton;
        int i = (!MediaController.getInstance().isPlayingAudio(this.audioEntry.messageObject) || MediaController.getInstance().isAudioPaused()) ? R.drawable.audiosend_play : R.drawable.audiosend_pause;
        imageView.setImageResource(i);
        this.needDivider = divider;
        if (divider) {
            z = false;
        } else {
            z = true;
        }
        setWillNotDraw(z);
        this.checkBox.setChecked(checked, false);
    }

    public void setChecked(boolean value) {
        this.checkBox.setChecked(value, true);
    }

    public void setDelegate(AudioCellDelegate audioCellDelegate) {
        this.delegate = audioCellDelegate;
    }

    public AudioEntry getAudioEntry() {
        return this.audioEntry;
    }

    protected void onDraw(Canvas canvas) {
        if (this.needDivider) {
            canvas.drawLine((float) AndroidUtilities.dp(72.0f), (float) (getHeight() - 1), (float) getWidth(), (float) (getHeight() - 1), paint);
        }
    }
}
