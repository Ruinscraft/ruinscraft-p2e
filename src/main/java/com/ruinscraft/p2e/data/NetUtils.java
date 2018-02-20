package com.ruinscraft.p2e.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.net.URLEncoder;

public class NetUtils {
	
	public static boolean isOpen(String address) {
		Socket socket = null;
		try {
			socket = new Socket(address, 80);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static String encodeUrl(String url) {
		try {
			return URLEncoder.encode(url, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getResponse(String urlString) {
		try {
			String content = "";
			URL url = new URL(urlString);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setRequestMethod("GET");
			httpURLConnection.setConnectTimeout(15000);
			httpURLConnection.setReadTimeout(15000);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setAllowUserInteraction(false);
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(httpURLConnection.getInputStream()));
			String inputLine;
			while ((inputLine = bufferedReader.readLine()) != null) {
				content = content + inputLine;
			}
			bufferedReader.close();
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not make HTTP request.");
		}
		return null;
	}

	public static String generateWebPurifyUrl(String message, String apiKey) {
		message = encodeUrl(message);
		return "http://api1.webpurify.com/services/rest/?api_key=" + apiKey
				+ "&method=webpurify.live.check&format=json&text=" + message;
	}
	
}
