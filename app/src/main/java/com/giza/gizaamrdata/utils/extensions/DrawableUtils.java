package com.giza.gizaamrdata.utils.extensions;

import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.*;
import android.view.View;
import android.widget.ImageView;

/**
 * @author hossam.
 */
public class DrawableUtils {

    public static void setColor(View view, int color) {
        Drawable background = view.getBackground();
        if (background instanceof ShapeDrawable) {
            ((ShapeDrawable) background).getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            ((GradientDrawable) background).setColor(color);
        } else if (background instanceof ColorDrawable) {
            ((ColorDrawable) background).setColor(color);
        }
    }

    public static void setDrawableTintColor(ImageView imageView, int color) {
        imageView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public static Drawable getRotateDrawable(final Drawable d, final float angle) {
        final Drawable[] arD = {d};
        return new LayerDrawable(arD) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, d.getBounds().width() / 2, d.getBounds().height() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
    }
}