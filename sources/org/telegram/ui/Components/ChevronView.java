package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.telegram.messenger.AndroidUtilities;

public class ChevronView extends View {
    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_RIGHT = 0;
    private int direction;
    private Paint paint = new Paint(1);
    private Path path = new Path();

    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }

    public ChevronView(Context context, int direction2) {
        super(context);
        this.paint.setStrokeWidth((float) AndroidUtilities.dp(1.75f));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.direction = direction2;
    }

    public void setColor(int color) {
        this.paint.setColor(color);
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float stroke = this.paint.getStrokeWidth();
        this.path.rewind();
        switch (this.direction) {
            case 0:
                this.path.moveTo(((float) getPaddingLeft()) + stroke, ((float) getPaddingTop()) + stroke);
                this.path.lineTo((((float) getWidth()) - stroke) - ((float) getPaddingRight()), ((float) getHeight()) / 2.0f);
                this.path.lineTo(((float) getPaddingLeft()) + stroke, (((float) getHeight()) - stroke) - ((float) getPaddingBottom()));
                break;
            case 1:
                this.path.moveTo((((float) getWidth()) - stroke) - ((float) getPaddingRight()), ((float) getPaddingTop()) + stroke);
                this.path.lineTo(((float) getPaddingLeft()) + stroke, ((float) getHeight()) / 2.0f);
                this.path.lineTo((((float) getWidth()) - stroke) - ((float) getPaddingBottom()), (((float) getHeight()) - stroke) - ((float) getPaddingBottom()));
                break;
        }
        canvas.drawPath(this.path, this.paint);
    }
}
