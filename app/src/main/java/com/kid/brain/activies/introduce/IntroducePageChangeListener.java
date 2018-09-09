package com.kid.brain.activies.introduce;

import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageView;

import com.kid.brain.R;


public class IntroducePageChangeListener implements OnPageChangeListener {

	private ImageView[] imageViews;
	private IEndOfIntroduceListener iEndOfIntroduceListener;

	public IntroducePageChangeListener(ImageView[] imageViews) {
		super();
		this.imageViews = imageViews;
	}

	public void onEndOfIntroduceListener(IEndOfIntroduceListener iEndOfIntroduceListener){
		this.iEndOfIntroduceListener = iEndOfIntroduceListener;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		iEndOfIntroduceListener.introducePage(position);
		for (int i = 0; i < imageViews.length; i++) {
			imageViews[position].setImageResource(R.drawable.circle_blue);
			if (position != i) {
				imageViews[i].setImageResource(R.drawable.circle_gray);
			}
		}
		if( position == imageViews.length-1){
			iEndOfIntroduceListener.endOfIntroduce(true);
		}else{
			iEndOfIntroduceListener.endOfIntroduce(false);
		}
	}

	public interface IEndOfIntroduceListener{
		void introducePage(int position);
		void endOfIntroduce(boolean isEndOfIntroduce);
	}
}