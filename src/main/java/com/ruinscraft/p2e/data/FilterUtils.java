package com.ruinscraft.p2e.data;

import com.google.common.base.CharMatcher;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class FilterUtils {

	public static boolean isASCII(String string) {
		return CharMatcher.ascii().matchesAllOf(string);
	}

	public static boolean isAppropriate(String string, String webpurifyApiKey) {
		try {
			if (string.isEmpty()) {
				return true;
			}
			if (!NetUtils.isOpen("api1.webpurify.com")) {
				return true;
			}
			String url = NetUtils.generateWebPurifyUrl(string, webpurifyApiKey);
			String response = NetUtils.getResponse(url);
			JsonParser jsonParser = new JsonParser();
			JsonObject json = jsonParser.parse(response).getAsJsonObject();
			JsonObject jsonResponse = json.get("rsp").getAsJsonObject();
			if (jsonResponse.get("found").getAsInt() > 0) {
				return false;
			}
			return true;
		} catch (Exception e) {
			System.out.println("Error contacting webpurify.");
		}
		return true;
	}

}