package com.bsecure.getlucky.common;

import android.content.Context;
import android.webkit.JavascriptInterface;


import com.bsecure.getlucky.GetLucky;

import org.json.JSONException;
import org.json.JSONObject;


public class ContainerScriptInterface {

	private GetLucky manager = null;

	Context context = null;

	public ContainerScriptInterface(GetLucky manager) {
		this.manager = manager;		
	}

	@JavascriptInterface
	public void exec(String service, String action)
			throws JSONException {

		if(service.equalsIgnoreCase("myooredoolp")) {
			if(action.equalsIgnoreCase("back")) {
				//manager.finish();
				manager.onKeyDown(4, null);
			}
		}

	}

	@JavascriptInterface
	public String exec(String service, String action, String arg) throws JSONException {

		if(service.equalsIgnoreCase("myooredoolp")) {

			 if(action.equalsIgnoreCase("loadextbrowser")) {

			 	if(arg != null) {

					JSONObject jsonObject = new JSONObject(arg);

					String link = jsonObject.optString("link");

					if(link.length() > 0 && link.startsWith("http")) {

						//manager.lauchExternalBrowser(link);

					}

				}

			}

		}

		return null;

	}

}
