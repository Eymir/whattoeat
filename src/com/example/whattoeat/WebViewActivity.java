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
		    	{//�@��handler
		    		if (!Thread.currentThread().isInterrupted())
			    	{
			    		switch (msg.what)
			    		{
			    			case 0:
			    				pd.show();//��ܶi�ױ�
			    	        	break;
			    	        case 1:
			    	        	pd.hide();//���öi�ױ�
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
	
	public void init() //��l��
	{
    	wv = (WebView)findViewById(R.id.mybrowser);
    	wv.getSettings().setUseWideViewPort(true);
        wv.getSettings().setJavaScriptEnabled(true);//�i��JS
        wv.setScrollBarStyle(0);//���úu�u��
		
        wv.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		WebSettings webSettings =wv.getSettings();
	 	//webSettings.setJavaScriptEnabled(true);
	 	webSettings.setBuiltInZoomControls(true); 
	 	setZoomControlGone(wv);
        
        
        
        wv.setWebViewClient(new WebViewClient()
        {   
            public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
            	loadurl(view,url);//���J����
                return true;   
            }//���g�I���ʧ@�A��webview���J
 
        });
        wv.setWebChromeClient(new WebChromeClient(){
        	public void onProgressChanged(WebView view,int progress){//���J�i�ק��ܦ�Ĳ�o
             	if(progress==100){
            		handler.sendEmptyMessage(1);//�p�G�������J�A���ù�ܮ�
            	}   
                super.onProgressChanged(view, progress);   
            }   
        });
 
    	pd=new ProgressDialog(WebViewActivity.this);
        pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd.setMessage("�ƾڸ��J���A�еy�ԡI");
    }

	//�Y��
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
				 view.loadUrl(url);//���J����
			 }
		 }.start();
	}
}
