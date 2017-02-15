package su.hm.hprob;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import su.hm.hprob.listener.ProgressListener;
import su.hm.hprob.utils.AndroidUnit;

import static su.hm.hprob.HpbConstants.ATTR_NOT_PROVIDE;
import static su.hm.hprob.HpbConstants.PERCENT_CHAR;
import static su.hm.hprob.HpbConstants.TEXT_TOP_GAP;
import static su.hm.hprob.HpbConstants.VAL_ZERO;

/**
 * HorizontalProgressBar doesn't extend from ProgressBar, It will be drew
 * with {@linkplain Paint}.
 * <p>
 * Created by hm-su on 2017/2/14.
 */

public class HorizontalProgressBar extends View {

    // round bar default val is false.
    private static final boolean ROUND_BAR = false;

    // default radius 4px.
    private static final int RADIUS = 4;

    // default inside padding between primary rect and secondary rect.
    private static final int DEF_INSIDE_PADDING = 0;

    // the padding can not be larger than 6px.
    private static final float MAX_INSIDE_PADDING = 6f;

    // default width of hpb.
    private static final int DEF_BAR_WIDTH = 250;

    // default height of hpb.
    private static final int DEF_BAR_HEIGHT = 10;

    private static final int DEF_TEXT_SIZE = 12;

    // default bar shape is rect(0).
    private static final int BAR_SHAPE_RECT = 0;
    private static final int BAR_SHAPE_ROUND = 1;

    private static final int H_START = 0;
    private static final int H_MID = 1;
    private static final int H_END = 2;

    private static final int V_TOP = 0;
    private static final int V_MID = 1;
    private static final int V_BOTTOM = 2;

    // primary color & secondary color & text color
    private static final int DEF_PRIMARY_COLOR = Color.parseColor("#FFC107");
    private static final int DEF_SECONDARY_COLOR = Color.parseColor("#00796B");
    private static final int DEF_TEXT_COLOR = Color.BLACK;

    // default text enable is true
    private static final boolean TEXT_ENABLE = true;

    // whether the hpb is a round bar. default is false;
    private boolean isRoundBar = ROUND_BAR;

    // radius of round bar.
    private int radius;

    private int barShape;
    private int primaryColor;
    private int secondaryColor;
    private boolean isTextEnable;
    private int textColor;
    private int textSize;
    private int hTextPosition;
    private int vTextPosition;
    private float insidePadding;

    private RectF primaryRect;
    private RectF secondaryRect;
    // private RectF textBounds;

    private Paint primaryPaint;
    private Paint secondaryPaint;
    private Paint textPaint;

    // width of primary rect
    private int primaryRectWidth;

    // height of primary rect.
    private int primaryRectHeight;

    /**
     * Percent represents a rate
     * the rate is secondary comparing primary.
     */
    private float percent = 0f;

    // private ValueAnimator valueAnimator;

    // handler for updating
    private UpdateHandler updateHandler;

    // listener
    private ProgressListener mProgressListener;

    // counts 1% each time
    private int counter = 0;

    public HorizontalProgressBar(Context context) {
        this(context, null);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // init
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public HorizontalProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        // init
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    /**
     * Handles typed array and init paint and rect.
     *
     * @param context      context
     * @param attrs        attribute set
     * @param defStyleAttr def style attr
     */
    @SuppressLint("NewApi")
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.HprobAttrs, defStyleAttr, defStyleRes);
        barShape = a.getInteger(R.styleable.HprobAttrs_shape, BAR_SHAPE_RECT);
        if (BAR_SHAPE_ROUND == barShape) {
            radius = a.getDimensionPixelSize(R.styleable.HprobAttrs_radius, (int) AndroidUnit.DENSITY_PIXELS.toPixels(RADIUS));
        } else {
            radius = VAL_ZERO;
        }

        // set is round bar
        isRoundBar = BAR_SHAPE_ROUND == barShape;

        /* get primary color & secondary color */
        primaryColor = a.getColor(R.styleable.HprobAttrs_primary_color, DEF_PRIMARY_COLOR);
        secondaryColor = a.getColor(R.styleable.HprobAttrs_secondary_color, DEF_SECONDARY_COLOR);

        isTextEnable = a.getBoolean(R.styleable.HprobAttrs_text_enable, TEXT_ENABLE);
        textColor = a.getColor(R.styleable.HprobAttrs_text_color, DEF_TEXT_COLOR);
        textSize = a.getDimensionPixelSize(R.styleable.HprobAttrs_text_size, (int) AndroidUnit.SCALE_PIXELS.toPixels(DEF_TEXT_SIZE));

        /* get text position */
        if (isTextEnable) {
            hTextPosition = a.getInteger(R.styleable.HprobAttrs_text_position_h, H_MID);
            vTextPosition = a.getInteger(R.styleable.HprobAttrs_text_position_v, V_MID);
        } else {
            hTextPosition = ATTR_NOT_PROVIDE;
            vTextPosition = ATTR_NOT_PROVIDE;
        }

        /* get & handle inside padding */
        insidePadding = a.getDimensionPixelSize(R.styleable.HprobAttrs_inside_padding, DEF_INSIDE_PADDING);
        insidePadding = AndroidUnit.px2dip(getContext(), insidePadding) > MAX_INSIDE_PADDING
                ? MAX_INSIDE_PADDING : AndroidUnit.px2dip(getContext(), insidePadding);

        // if developer provides bar_thickness, uses it as primary rect height.
        // -1 represents developer doesn't provide this attr.
        // if not, we should consider the android:layout_height.
        primaryRectHeight = a.getDimensionPixelSize(R.styleable.HprobAttrs_primary_thickness, ATTR_NOT_PROVIDE);
        if (ATTR_NOT_PROVIDE != primaryRectHeight) {
            primaryRectHeight = primaryRectHeight == VAL_ZERO ? DEF_BAR_HEIGHT : primaryRectHeight;
        }

        // must recycle.
        a.recycle();

        // init paints
        primaryRect = new RectF();
        primaryPaint = new Paint();
        primaryPaint.setAntiAlias(true);
        primaryPaint.setStyle(Paint.Style.FILL);
        primaryPaint.setColor(primaryColor);

        // create a handler
        updateHandler = new UpdateHandler(this);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        /* if android:layout_width is set match_parent or exact value, uses it.
         * otherwise provides default width.
         * */
        if (MeasureSpec.EXACTLY == widthMode) {
            primaryRectWidth = MeasureSpec.getSize(widthMeasureSpec) == VAL_ZERO ?
                    DEF_BAR_WIDTH : MeasureSpec.getSize(widthMeasureSpec);
        } else {
            primaryRectWidth = DEF_BAR_WIDTH;
        }

        if (ATTR_NOT_PROVIDE == primaryRectHeight) {
            /* the same as width */
            if (MeasureSpec.EXACTLY == heightMode) {
                primaryRectHeight = MeasureSpec.getSize(heightMeasureSpec) == VAL_ZERO ?
                        DEF_BAR_HEIGHT : MeasureSpec.getSize(heightMeasureSpec);
            } else {
                primaryRectHeight = DEF_BAR_HEIGHT;
            }
        }

        // must do
        setMeasuredDimension(
                primaryRectWidth,
                primaryRectHeight
        );

        if (null != primaryRect) {
            int top = 0, left = 0;
            primaryRect.set(left, top, primaryRectWidth, primaryRectHeight);
        }
    }

    /**
     * Set percent value.
     *
     * @param percent value
     */
    public void setPercent(float percent) {
        if (percent < 0)
            throw new IllegalArgumentException("Percent can't be smaller than zero now.");

        this.percent = percent;

        // when we set percent again and the percent is smaller than counter, we need to draw from 0;
        if (percent < counter) {
            counter = 0;
        }

        /* send msg */
        Message msg = updateHandler.obtainMessage();
        msg.what = 0x1;
        msg.arg1 = counter;
        updateHandler.sendMessage(msg);
    }

    /**
     * Get percent value.
     *
     * @return percent value
     */
    public float getPercent() {
        return this.percent;
    }

    /**
     * Whether the percent value is bigger than 100%
     *
     * @return true bigger than 1 otherwise false
     */
    private boolean isPercentValueOverflow() {
        return counter >= 100.0f;
    }

    /**
     * This method is used to check right of secondary rect.
     * In order to prevent from the right value is smaller than 0;
     *
     * @param primaryRect secondary rect
     * @return correct right value
     */
    private float handleRightValue(RectF primaryRect) {
        return primaryRect.right - insidePadding;
    }

    // bridge method
    private void notifyRedraw() {
        // re-draw
        this.invalidate();
    }

    /**
     * draw the secondary rect.
     */
    private void drawSecondaryRect(Canvas canvas) {
        if (null == secondaryRect) {
            secondaryRect = new RectF();
        }

        /* handles position with insidePadding */
        // float top = primaryRect.top + AndroidUnit.SCALE_PIXELS.toPixels(insidePadding);
        float top = primaryRect.top + insidePadding;
        // float left = primaryRect.left + AndroidUnit.SCALE_PIXELS.toPixels(insidePadding);
        float left = primaryRect.left + insidePadding;
        // float bottom = primaryRect.bottom - AndroidUnit.SCALE_PIXELS.toPixels(insidePadding);
        float bottom = primaryRect.bottom - insidePadding;
        // float right = handleRightValue(primaryRect);
        // float right = handleRightValue(primaryRect);
        float right = insidePadding + primaryRectWidth / 100.0f * counter;

        // fix the offset that float takes.
        if (isPercentValueOverflow()) {
            right = handleRightValue(primaryRect);
            Log.i("SW", String.valueOf(right));
        }

        // set the rect values.
        secondaryRect.set(left, top, right, bottom);

        /* setup secondary paint */
        if (secondaryPaint == null) {
            secondaryPaint = new Paint();
            secondaryPaint.setAntiAlias(true);
            secondaryPaint.setColor(secondaryColor);
            secondaryPaint.setStyle(Paint.Style.FILL);
        }

        // canvas draw
        if (!isRoundBar) {
            canvas.drawRect(secondaryRect, secondaryPaint);
        } else {
            canvas.drawRoundRect(secondaryRect, radius, radius, secondaryPaint);
        }
    }

    /**
     * Update the secondary rect right value.
     */
    private void updateSecondaryRect() {
        Log.i("Counter is", counter + "");
        if (mProgressListener != null)
            mProgressListener.onProgress(this, counter);

//        if (valueAnimator == null) {
//            valueAnimator = ObjectAnimator.ofFloat(0f, percent);
//            valueAnimator.setDuration(1000);
//            valueAnimator.setInterpolator(new DecelerateInterpolator());
//            valueAnimator.start();
//        }

        if (counter < percent) {
            counter++;

            // notify to redraw
            notifyRedraw();

            // send msg
            Message msg = updateHandler.obtainMessage();
            msg.arg1 = counter;
            msg.what = 0x1;
            updateHandler.sendMessage(msg);
        } else {
            updateHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * draw text on it.
     */
    private void drawText(Canvas canvas) {
        // init text paint and rect
        if (textPaint == null) {
            textPaint = new Paint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(textSize);
            textPaint.setColor(textColor);
            textPaint.setLinearText(true);
        }

        float x = getHorTextPosition(hTextPosition);
        float y = getVerTextPosition(vTextPosition);
        String percentVal = String.valueOf(counter).concat(PERCENT_CHAR);

        // draw text
        canvas.drawText(percentVal, x, y, textPaint);
    }

    /**
     * When text is larger than bar height.
     *
     * @return text size
     */
//    private float handleTextSizeOverflow() {
//        // float fontHeight = textPaint.getFontMetrics().top - textPaint.getFontMetrics().bottom;
//        return textPaint.getTextSize() >= primaryRectHeight ? primaryRectHeight : textPaint.getTextSize();
//    }

    /**
     * Get text vertical position
     *
     * @param vPos type of vertical
     * @return position of y
     */
    private float getVerTextPosition(int vPos) {
        /* calculate offset */
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        float fontTotalHeight = fontMetrics.bottom - fontMetrics.top;
        float offY = fontTotalHeight / 2 - fontMetrics.bottom;

        switch (vPos) {
            case V_TOP: {
                return primaryRect.top - TEXT_TOP_GAP + offY;
            }

            case V_MID: {
                return primaryRect.centerY() + offY;
            }

            case V_BOTTOM: {
                return primaryRect.top + TEXT_TOP_GAP + offY;
            }

            default: {
                return primaryRect.centerY() + offY;
            }
        }
    }

    /**
     * Get text horizontal position
     *
     * @param hPos type of horizontal
     * @return position of x
     */
    private float getHorTextPosition(int hPos) {
        switch (hPos) {
            case H_START: {
                return primaryRect.left;
            }

            case H_MID: {
                return primaryRect.centerX() - ((ViewGroup) getParent()).getPaddingLeft();
            }

            case H_END: {
                return primaryRect.right;
            }

            default: {
                return primaryRect.centerX() - ((ViewGroup) getParent()).getPaddingLeft();
            }
        }
    }

    /**
     * Set an listener for hpb.
     *
     * @param listener ProgressListener
     */
    public void setProgressListener(ProgressListener listener) {
        this.mProgressListener = listener;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isRoundBar) {
            canvas.drawRect(primaryRect, primaryPaint);
        } else {
            canvas.drawRoundRect(primaryRect, radius, radius, primaryPaint);
        }

        // draw secondary canvas
        drawSecondaryRect(canvas);

        // draw text
        if (isTextEnable)
            drawText(canvas);

        // if we don't change canvas states, there is no need to call canvas.save();
        // canvas.save();
    }

    /* define a static handler and prevent from memory leak */
    private static class UpdateHandler extends Handler {

        WeakReference<View> mViewWeakRef;

        UpdateHandler(View view) {
            mViewWeakRef = new WeakReference<>(view);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // update
                case 0x1:
                    ((HorizontalProgressBar) mViewWeakRef.get()).updateSecondaryRect();
                    break;

                // remove
                case 0x2:
                    removeCallbacksAndMessages(null);
                    break;
            }
        }
    }
}
