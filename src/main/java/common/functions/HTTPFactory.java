package common.functions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class HTTPFactory {

	public final static String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36";
	
	public static void main(String[] args) {
		// for demo
		try {
			String url = "", apiMethod = "", requestBody = "", response = "";
			Map<String, String> httpHeader = new HashMap<String, String>();
			//httpHeader.put("accept", "*/*");
			httpHeader.put("accept", "application/json;charset=UTF-8");
			httpHeader.put("contentType", "application/json;charset=UTF-8");
			httpHeader.put("charset", "UTF-8");
			httpHeader.put("language", "zh-TW");
			httpHeader.put("cacheControl", "no-cache");
			httpHeader.put("connection", "keep-alive");
			httpHeader.put("userAgent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
			//httpHeader.put("host", "localhost:8081");
			//httpHeader.put("origin", "http://localhost:8081");
			//httpHeader.put("referer", "http://localhost:8081/swagger-ui.html");
			httpHeader.put("accessControlRequestHeaders", "Sec-Fetch-Mode, Sec-Fetch-Site");
			httpHeader.put("secFetchMode", "cors");
			httpHeader.put("secFetchSite", "same-origin");
			//httpHeader.put("connectionTimeout", "600000");
			//httpHeader.put("readTimeout", "600000");
			httpHeader.put("followRedirects", "true");
			
			// find IP location test
			
			// specify the IP to find location
			//url = "http://ip-api.com/json/114.136.5.140?fields=16510975&lang=en";
			
			// not specify the IP to find IP & location
			//url = "http://ip-api.com/json";
			//apiMethod = "POST";
			
			// Online tool to check server HTTP/2, ALPN, and Server-push support.
			url = "https://http2.pro/api/v1";
			apiMethod = "POST";
			
			requestBody = "";
			
			System.out.println("Call API: " + url + " via " + apiMethod);
			response = connectHTTPv11(apiMethod, httpHeader, url, requestBody);
			System.out.println("Response from calling API: " + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// HTTP request
	public static String connectHTTPv11(String method, Map<String, String> httpHeader, String url, String requestBody)
			throws Exception {
		URL obj = new URL(url);
		HttpURLConnection huc = (HttpURLConnection) obj.openConnection();
		huc.setRequestMethod(method);
		// add request header
		huc.setRequestProperty("Accept",  httpHeader.get("accept"));
		huc.setRequestProperty("Accept-Charset", httpHeader.get("charset"));
		huc.setRequestProperty("Accept-Language", httpHeader.get("language"));
		huc.setRequestProperty("Cache-Control", httpHeader.get("cacheControl"));
		huc.setRequestProperty("Connection", httpHeader.get("connection"));
		huc.setRequestProperty("Content-Type", httpHeader.get("contentType"));
		if (httpHeader.get("host")!=null) {
			if (httpHeader.get("host").isEmpty()==false && httpHeader.get("host").equals("")==false) {
				huc.setRequestProperty("Host", httpHeader.get("host"));		
			}
		}
		if (httpHeader.get("origin")!=null) {
			if (httpHeader.get("origin").isEmpty()==false && httpHeader.get("origin").equals("")==false) {
				huc.setRequestProperty("Origin", httpHeader.get("origin"));		
			}
		}
		if (httpHeader.get("referer")!=null) {
			if (httpHeader.get("referer").isEmpty()==false && httpHeader.get("referer").equals("")==false) {
				huc.setRequestProperty("Referer", httpHeader.get("referer"));		
			}
		}
		huc.setRequestProperty("Access-Control-Request-Method", method);
		if (httpHeader.get("accessControlRequestHeaders")!=null) {
			if (httpHeader.get("accessControlRequestHeaders").isEmpty()==false && httpHeader.get("accessControlRequestHeaders").equals("")==false) {
				huc.setRequestProperty("Access-Control-Request-Headers", httpHeader.get("accessControlRequestHeaders"));
				if (httpHeader.get("secFetchMode")!=null) {
					if (httpHeader.get("secFetchMode").isEmpty()==false && httpHeader.get("secFetchMode").equals("")==false) {
						huc.setRequestProperty("Sec-Fetch-Mode", httpHeader.get("secFetchMode"));
					}
				}
				if (httpHeader.get("secFetchSite")!=null) {
					if (httpHeader.get("secFetchSite").isEmpty()==false && httpHeader.get("secFetchSite").equals("")==false) {
						huc.setRequestProperty("Sec-Fetch-Site", httpHeader.get("secFetchSite"));
					}
				}
			}
		}
		huc.setRequestProperty("User-Agent", USER_AGENT);
		if (httpHeader.get("userAgent")!=null) {
			if (httpHeader.get("userAgent").isEmpty()==false && httpHeader.get("userAgent").equals("")==false) {
				huc.setRequestProperty("User-Agent", httpHeader.get("userAgent"));		
			}
		}
		if (httpHeader.get("connectTimeout")!=null) huc.setConnectTimeout(Integer.parseInt(httpHeader.get("connectTimeout")));
		if (httpHeader.get("readTimeout")!=null) huc.setReadTimeout(Integer.parseInt(httpHeader.get("readTimeout")));
		huc.setDefaultUseCaches(false);
		huc.setUseCaches(false);
		if (httpHeader.get("cacheControl")!=null) {
			if (httpHeader.get("cacheControl").equals("no-cache")) {
				huc.setDefaultUseCaches(true);
				huc.setUseCaches(true);
			}
		}
		huc.setInstanceFollowRedirects(false);
		HttpURLConnection.setFollowRedirects(false);
		if (httpHeader.get("followRedirects")!=null) {
			if (httpHeader.get("followRedirects").equals("true")) {
				huc.setInstanceFollowRedirects(true);
				HttpURLConnection.setFollowRedirects(true);
			}
		}
		huc.setDoInput(true);
		huc.setDoOutput(false);
		huc.connect();
		//System.out.println("Sending '"+method+"' request to URL : " + url);
		if (method.equals("POST")) {
			if (requestBody!=null) {
				if (requestBody.equals("")==false) {
					huc.setDoOutput(true);
					OutputStream os = huc.getOutputStream();
					DataOutputStream dos = new DataOutputStream(os);
					if (requestBody.indexOf("{")>0) {
						System.out.print("request body - length - before clean: " + requestBody.length());
						requestBody = requestBody.substring(requestBody.indexOf("{"), requestBody.length());
						System.out.println(", after clean: " + requestBody.length());
					}
					//System.out.println("requestBody: " + requestBody);
					dos.write(requestBody.getBytes());
					dos.flush();
					dos.close();
					dos = null;
					os.flush();
					os.close();
					os = null;
				}
			}
		}
		int responseCode = huc.getResponseCode();
		if (responseCode!=200 && responseCode!=201) {
			System.out.println("Sending '"+method+"' request to URL : " + url);
			System.out.println("Response HTTP status code (error): " + responseCode + ", request body: \n" + requestBody);
		}
		InputStream is = huc.getInputStream();
		InputStreamReader isr = new InputStreamReader(is, httpHeader.get("charset"));
		BufferedReader br = new BufferedReader(isr);
		String line;
		StringBuffer sb = new StringBuffer();
		while ((line = br.readLine()) != null) sb.append(line);
		br.close();
		br = null;
		isr.close();
		isr = null;
		is.close();
		is = null;	
		huc.disconnect();
		// return result
		return sb.toString();
	}

}
