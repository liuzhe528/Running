package com.lzstudio.healthy.view.myprogressview;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

public class MyProgressView extends View {
	/**
	 * 进度条的最大值
	 */
	private int maxProgress = 2000;
	/**
	 * 进度条的当前值
	 */
	private int currentProgress;
	/**
	 * 弧形区域画笔
	 */
	private Paint mArcPaint;
	private Paint mTimePaint;
	/**
	 * 文本画笔
	 */
	private Paint mTextPaint;
	/**
	 * 渐变色
	 */
	private LinearGradient shader;
	/**
	 * 绘制的区域
	 */
	private RectF area;
	/**
	 * 中心点
	 */
	private float centerX;
	private float centerY;
	/**
	 * 直径
	 */
	private int radius;
	/**
	 * 文字大小
	 */
	private float textSize = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 25, getResources().getDisplayMetrics());
	/**
	 * 文字大小
	 */
	private float textTimeSize = TypedValue.applyDimension(
			TypedValue.COMPLEX_UNIT_SP, 20, getResources().getDisplayMetrics());
	/**
	 * 预先绘制的圆
	 */
	private Paint nullArcPaint;
	/**
	 * 画笔的宽度
	 */
	private float paintWidth;

	public MyProgressView(Context context) {
		this(context, null);
	}

	public MyProgressView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public MyProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	/**
	 * 初始化要用到的参数
	 */
	private void init() {
		// 初始化空圆画笔
		nullArcPaint = new Paint();
		nullArcPaint.setColor(0xffCBF1E8);
		nullArcPaint.setStyle(Style.STROKE);
		nullArcPaint.setAntiAlias(true);
		// 初始化弧形画笔
		mArcPaint = new Paint();
		mArcPaint.setColor(Color.BLACK);
		mArcPaint.setStyle(Style.STROKE);
		mArcPaint.setAntiAlias(true);
		// 初始化文本画笔
		mTextPaint = new Paint();
		mTextPaint.setColor(0xff232323);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStrokeWidth(2f);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setTextSize(textSize);
		// 初始化时间画笔
		mTimePaint = new Paint();
		mTimePaint.setColor(0x993D3D3D);
		mTimePaint.setAntiAlias(true);
		mTimePaint.setStrokeWidth(2f);
		mTimePaint.setStyle(Style.FILL);
		mTimePaint.setTextSize(textTimeSize);
		shader = new LinearGradient(0, 0, 500, 0, new int[] { Color.GREEN,
				Color.YELLOW, Color.RED }, null, Shader.TileMode.CLAMP);
		mArcPaint.setShader(shader);
		sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int width;
		if (getMeasuredHeight() > getMeasuredWidth()) {
			width = getMeasuredWidth();
		} else {
			width = getMeasuredHeight();
		}
		centerX = getMeasuredWidth() / 2;
		centerY = getMeasuredHeight() / 2;
		radius = width - getPaddingLeft();
		paintWidth = getPaddingLeft();
		setMeasuredDimension(width, width);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		area = new RectF(centerX - radius / 2, centerY - radius / 2, centerX
				+ radius / 2, centerY + radius / 2);
		nullArcPaint.setStrokeWidth(paintWidth);
		mArcPaint.setStrokeWidth(paintWidth * 2 / 3);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawArc(area, 120, 300, false, nullArcPaint);
		if (currentProgress > maxProgress) {
			canvas.drawArc(area, 120, 300, false, mArcPaint);
		} else {
			canvas.drawArc(area, 120, 300 * currentProgress / maxProgress,
					false, mArcPaint);
		}
		drawText(canvas);
		drawTime(canvas);
	}

	private void drawTime(Canvas canvas) {
		String text = "用时:  " + sdf.format(time * 1000);
		float textWidth = mTimePaint.measureText(text);
		canvas.drawText(text, centerX - textWidth / 2, centerY + textH + 10,
				mTimePaint);
	}

	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private long time = 0;

	public void setTime(long time) {
		this.time = time;
		invalidate();
	}

	private float top;
	private float bottom;
	private float textH;

	private void drawText(Canvas canvas) {
		String text = currentProgress + " 步";
		float textWidth = mTextPaint.measureText(text);
		FontMetrics fm = mTextPaint.getFontMetrics();
		top = fm.top;
		bottom = fm.bottom;
		textH = bottom - top;
		canvas.drawText(text, centerX - textWidth / 2, centerY, mTextPaint);
	}

	public int getMaxProgress() {
		return maxProgress;
	}

	public void setMaxProgress(int maxProgress) {
		this.maxProgress = maxProgress;
		invalidate();
	}

	public int getCurrentProgress() {
		return currentProgress;
	}

	public void setCurrentProgress(int currentProgress) {
		this.currentProgress = currentProgress;
		invalidate();
	}

}
