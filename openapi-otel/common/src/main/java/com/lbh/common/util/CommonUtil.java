package com.lbh.common.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class CommonUtil {

	public static String toJson(Object obj) {
		return toJson(obj, true);
	}

	public static String toJson(Object obj, boolean isPretty) {
		Gson gson = null;
		if (isPretty) {
			gson = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyyMMddHHmmss").create();
		} else {
			gson = new GsonBuilder().setDateFormat("yyyyMMddHHmmss").create();
		}
		
		String json = gson.toJson(obj);

		return json;
	}

}
