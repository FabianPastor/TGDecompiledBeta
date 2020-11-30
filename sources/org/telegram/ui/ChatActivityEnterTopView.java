package org.telegram.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChatActivityEnterTopView extends FrameLayout {
    private boolean editMode;
    private EditView editView;
    private View replyView;

    public ChatActivityEnterTopView(Context context) {
        super(context);
    }

    public void addReplyView(View view, FrameLayout.LayoutParams layoutParams) {
        if (this.replyView == null) {
            this.replyView = view;
            addView(view, layoutParams);
        }
    }

    public void addEditView(EditView editView2, FrameLayout.LayoutParams layoutParams) {
        if (this.editView == null) {
            this.editView = editView2;
            editView2.setVisibility(8);
            addView(editView2, layoutParams);
        }
    }

    public void setEditMode(boolean z) {
        if (z != this.editMode) {
            this.editMode = z;
            int i = 8;
            this.replyView.setVisibility(z ? 8 : 0);
            EditView editView2 = this.editView;
            if (z) {
                i = 0;
            }
            editView2.setVisibility(i);
        }
    }

    public boolean isEditMode() {
        return this.editMode;
    }

    public EditView getEditView() {
        return this.editView;
    }

    public static class EditView extends LinearLayout {
        private EditViewButton[] buttons = new EditViewButton[2];

        public EditView(Context context) {
            super(context);
        }

        public void addButton(EditViewButton editViewButton, LinearLayout.LayoutParams layoutParams) {
            int childCount = getChildCount();
            if (childCount < 2) {
                this.buttons[childCount] = editViewButton;
                addView(editViewButton, layoutParams);
            }
        }

        public EditViewButton[] getButtons() {
            return this.buttons;
        }
    }

    public static class EditViewButton extends LinearLayout {
        private boolean editButton;
        private ImageView imageView;
        private TextView textView;

        public EditViewButton(Context context) {
            super(context);
        }

        public void addImageView(ImageView imageView2, LinearLayout.LayoutParams layoutParams) {
            if (this.imageView == null) {
                this.imageView = imageView2;
                addView(imageView2, layoutParams);
            }
        }

        public void addTextView(TextView textView2, LinearLayout.LayoutParams layoutParams) {
            if (this.textView == null) {
                this.textView = textView2;
                addView(textView2, layoutParams);
            }
        }

        public ImageView getImageView() {
            return this.imageView;
        }

        public TextView getTextView() {
            return this.textView;
        }

        public void setEditButton(boolean z) {
            this.editButton = z;
        }

        public boolean isEditButton() {
            return this.editButton;
        }
    }
}
