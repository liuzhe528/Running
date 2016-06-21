package com.lzstudio.healthy.view.shop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

public class GuaGuaView extends View {
	/**
	 * 绘制文字的画笔
	 */
	private Paint mTextPaint;
	/**
	 * 手指笔迹
	 */
	private Paint mOutPaint;
	/**
	 * 显示笔迹的位图
	 */
	private Bitmap mBitmap;

	/**
	 * 画布
	 */
	private Canvas mCanvas;

	private float mLastX;
	private float mLastY;

	private Path mPath = new Path();
	private String mText = "";
	private float centerx;
	private float centery;
	private int width;
	private int height;

	public GuaGuaView(Context context) {
		this(context, null);
	}

	public GuaGuaView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GuaGuaView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		// 初始化文字画笔
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		mTextPaint.setStyle(Style.FILL);
		mTextPaint.setColor(Color.DKGRAY);
		int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				25, context.getResources().getDisplayMetrics());
		mTextPaint.setTextSize(size);
		// 初始化手指画笔
		mOutPaint = new Paint();
		mOutPaint.setAntiAlias(true);
		mOutPaint.setDither(true);
		mOutPaint.setStyle(Style.STROKE);
		mOutPaint.setStrokeJoin(Join.ROUND);
		mOutPaint.setStrokeCap(Cap.ROUND);
		mOutPaint.setStrokeWidth(30);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		width = getMeasuredWidth();
		height = getMeasuredHeight();
		centerx = width / 2;
		centery = height / 2;
		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		mCanvas.drawColor(Color.parseColor("#c0c0c0"));
	}

	@Override
	protected void onDraw(Canvas canvas) {
		float w = mTextPaint.measureText(mText);
		canvas.drawText(mText, centerx - w / 2, centery + 5, mTextPaint);
		drawPath();
		canvas.drawBitmap(mBitmap, 0, 0, null);
	}

	private void drawPath() {
		mOutPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		mCanvas.drawPath(mPath, mOutPaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastX = x;
			mLastY = y;
			mPath.moveTo(x, y);
			break;

		case MotionEvent.ACTION_MOVE:
			int dx = (int) Math.abs(x - mLastX);
			int dy = (int) Math.abs(y - mLastY);
			if (dx > 3 || dy > 3) {
				mPath.lineTo(x, y);
			}
			mLastX = x;
			mLastY = y;
			break;
		}
		invalidate();
		return true;
	}

	public void setmText(String mText) {
		this.mText = mText;
		invalidate();
	}

}
