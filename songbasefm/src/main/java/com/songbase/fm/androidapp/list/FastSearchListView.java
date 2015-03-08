package com.songbase.fm.androidapp.list;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;

public class FastSearchListView extends ListView {

	private Context ctx;

	private static int indWidth = 30;
	private String[] sections;
	private float scaledWidth;
	private float fastSearchPosition;
	private float scaledFastSearchPaddingRight;
	private float fastSearchPaddingRight = 0;
	private boolean touchingFastSearch;

	private int indexSize;
	private String section;
	private boolean showLetter = true;
	private Handler listHandler;

	private float mouseDownY = 0;

	public FastSearchListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		ctx = context;

	}

	public FastSearchListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		ctx = context;

	}

	public FastSearchListView(Context context, String keyList) {
		super(context);
		ctx = context;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);

		if (ListController.useSections) {

			// For grafical Laylout
			if (sections == null) {
				sections = new String[1];
				sections[0] = "#";
			}

			scaledFastSearchPaddingRight = fastSearchPaddingRight
					* getSizeInPixel(ctx);
			scaledWidth = indWidth * getSizeInPixel(ctx);
			fastSearchPosition = this.getWidth() - this.getPaddingRight()
					- scaledWidth - scaledFastSearchPaddingRight;

			Paint p = new Paint();
			p.setColor(Color.BLACK);
			p.setAlpha(240);

			canvas.drawRect(fastSearchPosition, this.getPaddingTop(),
					fastSearchPosition + scaledWidth,
					this.getHeight() - this.getPaddingBottom(), p);

			indexSize = (this.getHeight() - this.getPaddingTop() - getPaddingBottom())
					/ sections.length;// - 10 * (int)
			// getSizeInPixel(ctx)
			//

			Paint textPaint = new Paint();
			textPaint.setColor(Color.WHITE);
			textPaint.setTextSize(scaledWidth / 2);

			Log.e("SIZE", textPaint.getTextSize() + "    " + indexSize + "  "
					+ indexSize / getSizeInPixel(ctx));

			if (textPaint.getTextSize() > indexSize)
				textPaint.setTextSize((float) (indexSize));

			for (int i = 0; i < sections.length; i++) {

				canvas.drawText(sections[i].toUpperCase(), fastSearchPosition
						+ textPaint.getTextSize() / 2, getPaddingTop()
						+ indexSize * (i + 1), textPaint);

			}

			// We draw the letter in the middle
			if (showLetter & section != null && !section.equals("")) {

				Paint textPaint2 = new Paint();
				textPaint2.setColor(Color.WHITE);
				textPaint2.setTextSize(2 * scaledWidth);

				canvas.drawText(section.toUpperCase(), getWidth() - 3.5f
						* scaledWidth, mouseDownY + scaledWidth / 2, textPaint2);
			}

		}
	}

	private static float getSizeInPixel(Context ctx) {
		return ctx.getResources().getDisplayMetrics().density;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		if (adapter instanceof SectionIndexer)
			sections = (String[]) ((SectionIndexer) adapter).getSections();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX();
		float y = event.getY() - this.getPaddingTop() - getPaddingBottom();
		mouseDownY = y;

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN: {

			if (!ListController.useSections
					|| (x < fastSearchPosition || x > fastSearchPosition
							+ scaledWidth))

				return super.onTouchEvent(event);
			else {
				touchingFastSearch = true;
				// We touched the index bar
				showLetter = true;

				int currentPosition = (int) Math.floor(y / indexSize);
				if (currentPosition > sections.length - 1)
					currentPosition = sections.length - 1;
				if (currentPosition < 0)
					currentPosition = 0;
				section = sections[currentPosition];
				this.setSelection(((SectionIndexer) getAdapter())
						.getPositionForSection(currentPosition));
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {

			if (!ListController.useSections
					|| (x < fastSearchPosition || x > fastSearchPosition
							+ scaledWidth)) {
				if (touchingFastSearch) {
					touchingFastSearch = false;
					listHandler = new ListHandler();
					listHandler.sendEmptyMessageDelayed(0, 150);
				}

				return super.onTouchEvent(event);

			} else {

				y = event.getY();
				int currentPosition = (int) Math.floor(y / indexSize);

				if (currentPosition > sections.length - 1)
					currentPosition = sections.length - 1;
				if (currentPosition < 0)
					currentPosition = 0;

				section = sections[currentPosition];
				this.setSelection(((SectionIndexer) getAdapter())
						.getPositionForSection(currentPosition));

			}
			break;

		}
		case MotionEvent.ACTION_UP: {

			touchingFastSearch = false;
			listHandler = new ListHandler();
			listHandler.sendEmptyMessageDelayed(0, 150);

			return super.onTouchEvent(event);

		}
		}
		return true;
	}

	private class ListHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			showLetter = false;
			FastSearchListView.this.invalidate();
		}

	}
}