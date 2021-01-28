package org.telegram.ui.Cells;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadioButton;

public class PhotoEditRadioCell extends FrameLayout {
    /* access modifiers changed from: private */
    public int currentColor;
    /* access modifiers changed from: private */
    public int currentType;
    private TextView nameTextView;
    /* access modifiers changed from: private */
    public View.OnClickListener onClickListener;
    private LinearLayout tintButtonsContainer;
    /* access modifiers changed from: private */
    public final int[] tintHighlighsColors = {0, -1076602, -1388894, -859780, -5968466, -7742235, -13726776, -3303195};
    /* access modifiers changed from: private */
    public final int[] tintShadowColors = {0, -45747, -753630, -13056, -8269183, -9321002, -16747844, -10080879};

    public PhotoEditRadioCell(Context context) {
        super(context);
        TextView textView = new TextView(context);
        this.nameTextView = textView;
        textView.setGravity(5);
        this.nameTextView.setTextColor(-1);
        this.nameTextView.setTextSize(1, 12.0f);
        this.nameTextView.setMaxLines(1);
        this.nameTextView.setSingleLine(true);
        this.nameTextView.setEllipsize(TextUtils.TruncateAt.END);
        addView(this.nameTextView, LayoutHelper.createFrame(80, -2.0f, 19, 0.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.tintButtonsContainer = linearLayout;
        linearLayout.setOrientation(0);
        for (int i = 0; i < this.tintShadowColors.length; i++) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setSize(AndroidUtilities.dp(20.0f));
            radioButton.setTag(Integer.valueOf(i));
            this.tintButtonsContainer.addView(radioButton, LayoutHelper.createLinear(0, -1, 1.0f / ((float) this.tintShadowColors.length)));
            radioButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    RadioButton radioButton = (RadioButton) view;
                    if (PhotoEditRadioCell.this.currentType == 0) {
                        PhotoEditRadioCell photoEditRadioCell = PhotoEditRadioCell.this;
                        int unused = photoEditRadioCell.currentColor = photoEditRadioCell.tintShadowColors[((Integer) radioButton.getTag()).intValue()];
                    } else {
                        PhotoEditRadioCell photoEditRadioCell2 = PhotoEditRadioCell.this;
                        int unused2 = photoEditRadioCell2.currentColor = photoEditRadioCell2.tintHighlighsColors[((Integer) radioButton.getTag()).intValue()];
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

    /* access modifiers changed from: private */
    public void updateSelectedTintButton(boolean z) {
        int i;
        int childCount = this.tintButtonsContainer.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.tintButtonsContainer.getChildAt(i2);
            if (childAt instanceof RadioButton) {
                RadioButton radioButton = (RadioButton) childAt;
                int intValue = ((Integer) radioButton.getTag()).intValue();
                radioButton.setChecked(this.currentColor == (this.currentType == 0 ? this.tintShadowColors[intValue] : this.tintHighlighsColors[intValue]), z);
                int i3 = -1;
                if (intValue == 0) {
                    i = -1;
                } else {
                    i = this.currentType == 0 ? this.tintShadowColors[intValue] : this.tintHighlighsColors[intValue];
                }
                if (intValue != 0) {
                    i3 = this.currentType == 0 ? this.tintShadowColors[intValue] : this.tintHighlighsColors[intValue];
                }
                radioButton.setColor(i, i3);
            }
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener2) {
        this.onClickListener = onClickListener2;
    }

    /* access modifiers changed from: protected */
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), NUM), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(40.0f), NUM));
    }

    public void setIconAndTextAndValue(String str, int i, int i2) {
        this.currentType = i;
        this.currentColor = i2;
        TextView textView = this.nameTextView;
        textView.setText(str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase());
        updateSelectedTintButton(false);
    }
}
