package com.tnc.widgets;

import com.tnc.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

public class MessagePushDisplayView extends RelativeLayout {

	Context mContext;
	
	
	public MessagePushDisplayView(Context context) {
		super(context);
		mContext=context;
		//Inflate and attach the content
		View v=LayoutInflater.from(context).inflate(R.layout.pushdisplaylayout, this);
		
		Button btn = (Button)v.findViewById(R.id.btnYes);
		btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//system.out.println("Btn yes click -------------");
			}
		});

	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {

	    // ATTENTION: GET THE X,Y OF EVENT FROM THE PARAMETER
	    // THEN CHECK IF THAT IS INSIDE YOUR DESIRED AREA
		//system.out.println("Btn onTouch Event -------------");

//	    Toast.makeText(getContext(),"onTouchEvent", Toast.LENGTH_LONG).show();
	    return true;
	}
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if(mContext instanceof ChatHeadService)
//		{
//			//system.out.println("-------------------test done");
//			((ChatHeadService)mContext).stopSelf();
//		}
//
//		return true;
//	}

}
