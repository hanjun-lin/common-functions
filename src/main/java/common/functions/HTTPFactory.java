package common.functions;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Security;
import java.util.HashMap;
import java.util.Map;

import org.conscrypt.Conscrypt;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Request.Builder;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HTTPFactory {

	public final static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.113 Safari/537.36";

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
			//url = "https://http2.pro/api/v1";
			//apiMethod = "POST";
			
			// using local api server for test to validate htto client
			url = "http://localhost:8080/apiserver-servlet/GetClientConnectionInfo";
			apiMethod = "POST";

			requestBody = "";
			
			System.out.println("Call API: " + url + " via " + apiMethod);
			//response = connectHTTPv11(apiMethod, httpHeader, url, requestBody);
			response = connectHTTPv2(apiMethod, httpHeader, url, requestBody);
			System.out.println("Response from calling API: " + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// HTTP request
	public static String connectHTTPv11(String method, Map<String, String> httpHeader, String url, String requestBody) throws Exception {
		URL obj = new URL(url);
		HttpURLConnection huc = (HttpURLConnection) obj.openConnection();
		huc.setRequestMethod(method);
		// add request header
		huc.setRequestProperty("Access-Control-Request-Method", method);
		huc.setRequestProperty("User-Agent", USER_AGENT);
		huc.setDefaultUseCaches(false);
		huc.setUseCaches(false);
		if (httpHeader.get("cacheControl")!=null) {
			if (!httpHeader.get("cacheControl").equals("no-cache")) {
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
		huc = setupHttpHeaders(huc, httpHeader);
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
		br.close(); br = null;
		isr.close(); isr = null;
		is.close(); is = null;	
		huc.disconnect();
		// return result
		return sb.toString();
	}

	private static HttpURLConnection setupHttpHeaders(HttpURLConnection huc, Map<String, String> httpHeader) {
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
		if (httpHeader.get("userAgent")!=null) {
			if (httpHeader.get("userAgent").isEmpty()==false && httpHeader.get("userAgent").equals("")==false) {
				huc.setRequestProperty("User-Agent", httpHeader.get("userAgent"));		
			}
		}
		if (httpHeader.get("connectTimeout")!=null) huc.setConnectTimeout(Integer.parseInt(httpHeader.get("connectTimeout")));
		if (httpHeader.get("readTimeout")!=null) huc.setReadTimeout(Integer.parseInt(httpHeader.get("readTimeout")));
		return huc;
	}

	// HTTP request via OkHttp library
	// https://square.github.io/okhttp/
	// https://www.vogella.com/tutorials/JavaLibrary-OkHttp/article.html
	public static String connectHTTPv2(String httpMethod, Map<String, String> httpHeader, String urlToConnect, String requestBody) throws Exception {
		String responseStr;
		// for HTTP/2 use conscrypt to get ALPN support in jdk8
		// use conscrypt-openjdk-uber jar to include all dependencies
		Security.insertProviderAt(Conscrypt.newProvider(), 1);
		// avoid creating several instances, should be singleon
		OkHttpClient client = new OkHttpClient();

		HttpUrl.Builder urlBuilder = HttpUrl.parse(urlToConnect).newBuilder();
		//urlBuilder.addQueryParameter("v", "1.0");
		//urlBuilder.addQueryParameter("user", "vogella");
		String urlIncludeURLParameters = urlBuilder.build().toString();
		
		Request request = null;
		Builder rb = null;
		if (null != requestBody) {
			if (!"".equals(requestBody)) { // for POST, PUT, DELETE etc which has request body
				MediaType mt = MediaType.parse(httpHeader.get("contentType"));
				RequestBody body = RequestBody.create(requestBody, mt);
				if ("POST".equalsIgnoreCase(httpMethod)) {
					rb = new Request.Builder()
							.header("User-Agent", USER_AGENT)
							.url(urlIncludeURLParameters)
							.post(body);
				} else if ("PUT".equalsIgnoreCase(httpMethod)) {
					rb = new Request.Builder()
							.header("User-Agent", USER_AGENT)
							.url(urlIncludeURLParameters)
							.put(body);
				} else if ("PATCH".equalsIgnoreCase(httpMethod)) {
					rb = new Request.Builder()
							.header("User-Agent", USER_AGENT)
							.url(urlIncludeURLParameters)
							.put(body);
				} else if ("DELETE".equalsIgnoreCase(httpMethod)) {
					rb = new Request.Builder()
							.header("User-Agent", USER_AGENT)
							.url(urlIncludeURLParameters)
							.delete(body);
				} else {
					rb = new Request.Builder()
							.header("User-Agent", USER_AGENT)
							.url(urlIncludeURLParameters)
							.post(body);
				}
			} else { // for GET etc which do not have request body
				rb = new Request.Builder()
						.header("User-Agent", USER_AGENT)
						.url(urlIncludeURLParameters);
			}
		} else { // for GET etc which do not have request body
			rb = new Request.Builder()
					.header("User-Agent", USER_AGENT)
					.url(urlIncludeURLParameters);
		}

		/*
		Headers.Builder builder = new Headers.Builder();
		for (HttpHeader hh : ht.HttpRequestHeader) {
			builder.add(hh.Name, hh.Value);
		}
		Headers h = builder.build();
		request = new Request.Builder()
			    .header("Authorization", "your token")
			    .url("https://api.github.com/users/vogella")
			    .build();
		*/

		rb = rb.header("Access-Control-Request-Method", httpMethod);
		rb = rb.header("User-Agent", USER_AGENT);
		rb = setupOkHttpHeaders(rb, httpHeader);
		
		request = rb.build();
		
		Response response = client.newCall(request).execute();
		responseStr = response.body().string();
		return responseStr;
	}
	
	private static Builder setupOkHttpHeaders(Builder rb, Map<String, String> httpHeader) {
		if (null != httpHeader.get("accept")) {
			if (false == httpHeader.get("accept").isEmpty() && false == "".equals(httpHeader.get("accept"))) {
				rb = rb.addHeader("Accept", httpHeader.get("accept"));		
			}
		}
		if (null != httpHeader.get("charset")) {
			if (false == httpHeader.get("charset").isEmpty() && false == "".equals(httpHeader.get("charset"))) {
				rb = rb.addHeader("Accept-Charset", httpHeader.get("charset"));		
			}
		}
		if (null != httpHeader.get("language")) {
			if (false == httpHeader.get("language").isEmpty() && false == "".equals(httpHeader.get("language"))) {
				rb = rb.addHeader("Accept-Language", httpHeader.get("language"));		
			}
		}
		if (null != httpHeader.get("cacheControl")) {
			if (false == httpHeader.get("cacheControl").isEmpty() && false == "".equals(httpHeader.get("cacheControl"))) {
				rb = rb.addHeader("Cache-Control", httpHeader.get("cacheControl"));
			}
		}
		if (null != httpHeader.get("connection")) {
			if (false == httpHeader.get("connection").isEmpty() && false == "".equals(httpHeader.get("connection"))) {
				rb = rb.addHeader("Connection", httpHeader.get("connection"));
			}
		}
		if (null != httpHeader.get("contentType")) {
			if (false == httpHeader.get("contentType").isEmpty() && false == "".equals(httpHeader.get("contentType"))) {
				rb = rb.addHeader("Content-Type", httpHeader.get("contentType"));
			}
		}
		if (null != httpHeader.get("host")) {
			if (false == httpHeader.get("host").isEmpty() && false == "".equals(httpHeader.get("host"))) {
				rb = rb.addHeader("Host", httpHeader.get("host"));		
			}
		}
		if (null != httpHeader.get("origin")) {
			if (false == httpHeader.get("origin").isEmpty() && false == "".equals(httpHeader.get("origin"))) {
				rb = rb.addHeader("Origin", httpHeader.get("origin"));		
			}
		}
		if (null != httpHeader.get("referer")) {
			if (false == httpHeader.get("referer").isEmpty() && false == "".equals(httpHeader.get("referer"))) {
				rb = rb.addHeader("Referer", httpHeader.get("referer"));		
			}
		}
		if (null != httpHeader.get("userAgent")) {
			if (false == httpHeader.get("userAgent").isEmpty() && false == "".equals(httpHeader.get("userAgent"))) {
				rb = rb.header("User-Agent", httpHeader.get("userAgent"));		
			}
		}
		if (null != httpHeader.get("accessControlRequestHeaders")) {
			if (false == httpHeader.get("accessControlRequestHeaders").isEmpty() && false == "".equals(httpHeader.get("accessControlRequestHeaders"))) {
				rb = rb.addHeader("Access-Control-Request-Headers", httpHeader.get("accessControlRequestHeaders"));
				if (httpHeader.get("secFetchMode")!=null) {
					if (false == httpHeader.get("secFetchMode").isEmpty() && false == "".equals(httpHeader.get("secFetchMode"))) {
						rb = rb.addHeader("Sec-Fetch-Mode", httpHeader.get("secFetchMode"));
					}
				}
				if (httpHeader.get("secFetchSite")!=null) {
					if (false == httpHeader.get("secFetchSite").isEmpty() && false == "".equals(httpHeader.get("secFetchSite"))) {
						rb = rb.addHeader("Sec-Fetch-Site", httpHeader.get("secFetchSite"));
					}
				}
			}
		}
		
		return rb;
	}
}