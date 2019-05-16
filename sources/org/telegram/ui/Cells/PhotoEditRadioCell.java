package org.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class PhotoEditRadioCell extends FrameLayout {
    private int currentColor;
    private int currentType;
    private TextView nameTextView;
    private OnClickListener onClickListener;
    private LinearLayout tintButtonsContainer;
    private final int[] tintHighlighsColors = new int[]{0, -1076602, -1388894, -859780, -5968466, -7742235, -13726776, -3303195};
    private final int[] tintShadowColors = new int[]{0, -45747, -753630, -13056, -8269183, -9321002, -16747844, -10080879};

    public PhotoEditRadioCell(Context context) {
        super(context);
        this.nameTextView = new TextView(context);
        this.nameTextView.setGravity(5);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(80, -2.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
        this.tintButtonsContainer = new LinearLayout(context);
        this.tintButtonsContainer.setOrientation(0);
        for (int i = 0; i < this.tintShadowColors.length; i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            radioButton.setTag(Integer.valueOf(i));
            this.tintButtonsContainer.addView(radioButton, LayoutHelper.createLinear(0, -1, 1.0f / ((float) this.tintShadowColors.length)));
            radioButton.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    RadioButton radioButton = (RadioButton) view;
                    PhotoEditRadioCell photoEditRadioCell;
                    if (PhotoEditRadioCell.this.currentType == 0) {
                        photoEditRadioCell = PhotoEditRadioCell.this;
                        photoEditRadioCell.currentColor = photoEditRadioCell.tintShadowColors[((Integer) radioButton.getTag()).intValue()];
                    } else {
                        photoEditRadioCell = PhotoEditRadioCell.this;
                        photoEditRadioCell.currentColor = photoEditRadioCell.tintHighlighsColors[((Integer) radioButton.getTag()).intValue()];
                    }
                    PhotoEditRadioCell.this.updateSelectedTintButton(true);
                    PhotoEditRadioCell.this.onClickListener.onClick(PhotoEditRadioCell.this);
                }
            });
        }
        addView(this.tintButtonsContainer, LayoutHelper.createFrame(-1, 40.0f, 51, 96.0f, 0.0f, 24.0f, 0.0f));
    }

    public int getCurrentColor() {
        return this.currentColor;
    }

    private void updateSelectedTintButton(boolean z) {
        int childCount = this.tintButtonsContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.tintButtonsContainer.getChildAt(i);
            if (childAt instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) childAt;
                int intValue = ((Integer) radioButton.getTag()).intValue();
                radioButton.setChecked(this.currentColor == (this.currentType == 0 ? this.tintShadowColors[intValue] : this.tintHighlighsColors[intValue]), z);
                int i2 = -1;
                int i3 = intValue == 0 ? -1 : this.currentType == 0 ? this.tintShadowColors[intValue] : this.tintHighlighsColors[intValue];
                if (intValue != 0) {
                    i2 = this.currentType == 0 ? this.tintShadowColors[intValue] : this.tintHighlighsColors[intValue];
                }
                radioButton.setColor(i3, i2);
            }
        }
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    /* Access modifiers changed, original: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(i), NUM), MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
    }

    public void setIconAndTextAndValue(String str, int i, int i2) {
        this.currentType = i;
        this.currentColor = i2;
        TextView textView = this.nameTextView;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str.substring(0, 1).toUpperCase());
        stringBuilder.append(str.substring(1).toLowerCase());
        textView.setText(stringBuilder.toString());
        updateSelectedTintButton(false);
    }
}
