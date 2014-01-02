package com.example.whattoeat;



import javax.xml.datatype.DatatypeConstants.Field;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

public class WebViewActivity extends Activity {

	WebView wv;
	ProgressDialog pd;
	Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_web_view);
		
		
		Bundle bundle = getIntent().getExtras();
		//String title = bundle.getString("title");
		
		String restWeb = bundle.getString("restWeb");
		
		 init();
		 loadurl(wv,restWeb);
		
		 handler = new Handler()
		    {
		    	public void handleMessage(Message msg)
		    	{//一個handler
		    		if (!Thread.currentThread().isInterrupted())
			    	{
			    		switch (msg.what)
			    		{
			    			case 0:
			    				pd.show();//顯示進度條
			    	        	break;
			    	        case 1:
			    	        	pd.hide();//隱藏進度條
			    	        	break;
			    		}
			    	}
		    		super.handleMessage(msg);
		    	}
		    };
		
		
		
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.web_view, menu);
		return true;
	}
	
	public void init() //初始化
	{
    	wv = (WebView)findViewById(R.id.mybrowser);
    	wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setJavaScriptEnabled(true);//可用JS
        wv.setScrollBarStyle(0);//隱藏滾滾條
		
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		WebSettings webSettings =wv.getSettings();
	 	//webSettings.setJavaScriptEnabled(true);
	 	webSettings.setBuiltInZoomControls(true); 
	 	setZoomControlGone(wv);
        
        
        
        wv.setWebViewClient(new WebViewClient()
        {   
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            	loadurl(view,url);//載入網頁
                return true;   
            }//重寫點擊動作，用webview載入
 
        });
        wv.setWebChromeClient(new WebChromeClient(){
        	public void onProgressChanged(WebView view,int progress){//載入進度改變而觸發
             	if(progress==100){
            		handler.sendEmptyMessage(1);//如果全部載入，隱藏對話框
            	}   
                super.onProgressChanged(view, progress);   
            }   
        });
 
    	pd=new ProgressDialog(WebViewActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("數據載入中，請稍候！");
    }

	//縮放
	public void setZoomControlGone(View view) {
		Class classType;
		java.lang.reflect.Field field;
		try {
			classType = WebView.class;
			field = classType.getDeclaredField("mZoomButtonsController");
			field.setAccessible(true);
			ZoomButtonsController mZoomButtonsController = new ZoomButtonsController(view);
			mZoomButtonsController.getZoomControls().setVisibility(View.GONE);
			try {
				field.set(view, mZoomButtonsController);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
	
	 public void loadurl(final WebView view,final String url)
	 {
		 new Thread()
		 {
			 public void run()
			 {
				 handler.sendEmptyMessage(0);
				 view.loadUrl(url);//載入網頁
			 }
		 }.start();
	}
}
