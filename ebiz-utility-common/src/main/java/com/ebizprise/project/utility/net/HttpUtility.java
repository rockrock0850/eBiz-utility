package com.ebizprise.project.utility.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Logger;

import javax.net.ssl.SSLContext;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

/**
 * This class encapsulates methods for requesting a server via HTTP GET/POST and
 * provides methods for parsing response from the server.
 *
 * @author www.codejava.net
 *
 */
public class HttpUtility {
    
    private static final Logger log = Logger.getLogger("com.ebizprise.project.utility.network.HttpUtility");

	private static SSLContext sslContext;
	private static HttpURLConnection httpConn;
	private static CloseableHttpClient securityClient;
	
	static {
        try {
            sslContext = SSLContexts.custom().loadTrustMaterial((chain, authType) -> true).build();
            SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
                    sslContext, 
                    new String[] {"SSLv2Hello", "SSLv3", "TLSv1","TLSv1.1", "TLSv1.2" }, 
                    null,
                    NoopHostnameVerifier.INSTANCE);
            securityClient = HttpClients
                    .custom()
                    .setSSLSocketFactory(sslConnectionSocketFactory)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
    
    @SuppressWarnings("resource")
    public static String post (String url, String json, boolean isSecurity) throws Exception {
        log.info("HttpUtility.post() input URL : " + url);
        log.info("HttpUtility.post() input JSON : " + json);
        
        StringEntity requestBody = new StringEntity(json, ContentType.APPLICATION_JSON);
        HttpPost postMethod = new HttpPost(url);
        postMethod.setEntity(requestBody);
        CloseableHttpClient client = isSecurity ? securityClient : HttpClients.createDefault();
        HttpResponse response = client.execute(postMethod);
        String result = EntityUtils.toString(response.getEntity());

        log.info("HttpUtility.post() output JSON : " + result);
        
        return result;
    }
    
    @SuppressWarnings("resource")
    public static byte[] download (String url, String json, boolean isSecurity) throws IOException {
        StringEntity requestBody = new StringEntity(json, ContentType.APPLICATION_JSON);
        HttpPost postMethod = new HttpPost(url);
        postMethod.setEntity(requestBody);
        CloseableHttpClient client = isSecurity ? securityClient : HttpClients.createDefault();
        HttpResponse response = client.execute(postMethod);
        
        return IOUtils.toByteArray(response.getEntity().getContent());
    }

	/**
	 * Makes an HTTP request using GET method to the specified URL.
	 *
	 * @param requestURL
	 *            the URL of the remote server
	 * @return An HttpURLConnection object
	 * @throws IOException
	 *             thrown if any I/O error occurred
	 */
	public static HttpURLConnection sendGetRequest(String requestURL) throws IOException {
		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setUseCaches(false);

		httpConn.setDoInput(true); // true if we want to read server's response
		httpConn.setDoOutput(false); // false indicates this is a GET request

		return httpConn;
	}

	/**
	 * Makes an HTTP request using POST method to the specified URL.
	 *
	 * @param requestURL
	 *            the URL of the remote server
	 * @param params
	 *            A map containing POST data in form of key-value pairs
	 * @return An HttpURLConnection object
	 * @throws IOException
	 *             thrown if any I/O error occurred
	 */
	public static HttpURLConnection sendPostRequest(String requestURL, Map<String, Object> params) throws IOException {

		URL url = new URL(requestURL);
		httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod("POST");
		httpConn.setUseCaches(false);

		httpConn.setDoInput(true); // true indicates the server returns response

		StringBuffer requestParams = new StringBuffer();

		if (params != null && params.size() > 0) {

			httpConn.setDoOutput(true); // true indicates POST request

			// creates the params string, encode them using URLEncoder
			Iterator<String> paramIterator = params.keySet().iterator();
			while (paramIterator.hasNext()) {
				String key = paramIterator.next();
				String value = MapUtils.getString(params, key, "");
				requestParams.append(URLEncoder.encode(key, "UTF-8"));
				requestParams.append("=").append(URLEncoder.encode(value, "UTF-8"));
				requestParams.append("&");
			}

			// sends POST data
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write(requestParams.toString());
			writer.flush();
		}

		return httpConn;
	}

	/**
	 * 讀取檔案為byte[]
	 * @return
	 * @throws IOException
	 * @author adam.yeh
	 */
    public static byte[] readResponseAsBytes() throws IOException {
        InputStream inputStream = null;
        
        if (httpConn != null) {
            inputStream = httpConn.getInputStream();
        } else {
            throw new IOException("Connection is not established.");
        }

        return IOUtils.toByteArray(inputStream);
    }

	/**
	 * Returns only one line from the server's response. This method should be used
	 * if the server returns only a single line of String.
	 *
	 * @return a String of the server's response
	 * @throws IOException
	 *             thrown if any I/O error occurred
	 */
	public static String readSingleLineRespone() throws IOException {
		InputStream inputStream = null;
		if (httpConn != null) {
			inputStream = httpConn.getInputStream();
		} else {
			throw new IOException("Connection is not established.");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));

		String response = reader.readLine();
		reader.close();

		return response;
	}

	/**
	 * Returns an array of lines from the server's response. This method should be
	 * used if the server returns multiple lines of String.
	 *
	 * @return an array of Strings of the server's response
	 * @throws IOException
	 *             thrown if any I/O error occurred
	 */
	public static String[] readMultipleLinesRespone() throws IOException {
		InputStream inputStream = null;
		if (httpConn != null) {
			inputStream = httpConn.getInputStream();
		} else {
			throw new IOException("Connection is not established.");
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		List<String> response = new ArrayList<String>();

		String line = "";
		while ((line = reader.readLine()) != null) {
			response.add(line);
		}
		reader.close();

		return response.toArray(new String[0]);
	}
	
    /**
     * post請求
     * 
     * @param url
     * @param params
     * @param connetTimeout
     *            毫秒
     * @param readTimeout
     *            毫秒
     * @return
     */
    private static HttpURLConnection post(String url, Map<String, Object> params,
            int connetTimeout, int readTimeout) {
        String uuid = UUID.randomUUID().toString();
        //HttpURLConnection connection = null;
        BufferedReader in = null;
        DataOutputStream dos = null;
        StringBuffer result = new StringBuffer();
        try {
            URL realUrl = new URL(url);
            httpConn = (HttpURLConnection) realUrl.openConnection();
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestProperty("Connection", "Keep-Alive");// 維持長連接
            httpConn.setRequestProperty("Charset", "UTF-8");
            if (connetTimeout > 0) {
                httpConn.setConnectTimeout(connetTimeout);
            }
            if (readTimeout > 0) {
                httpConn.setReadTimeout(readTimeout);
            }
            httpConn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            httpConn.connect();
            dos = new DataOutputStream(httpConn.getOutputStream());
            dos.writeBytes(iteratorMapParam(params));
            dos.flush();

            in = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.warning(logParam(uuid, "http post 報錯{}", e));
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.warning(logParam(uuid, "http post 關閉讀取流報錯{}", e));
                }
            }
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    log.warning(logParam(uuid, "http post 關閉讀取流報錯{}", e));
                }
            }
        }
        return httpConn;
    }
    
    /**
     * post請求
     * 
     * @param url
     * @param params
     * @return
     */
    public static HttpURLConnection post(String url, Map<String, Object> params) {
        return post(url, params, -1, -1);
    }

    private static String iteratorMapParam(Map<String, Object> map)
            throws UnsupportedEncodingException {
        StringBuffer sb = new StringBuffer();
        for (Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey())
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue().toString(),
                            "utf-8")).append("&");
        }
        return sb.toString();
    }
 
    /**
     * 
     * @param uuid
     * @param msg
     * @param e
     * @return
     */
    private static String logParam(String uuid, String msg, Throwable e) {
        StringBuilder sb = new StringBuilder("[HttpUtils http service]");
        if (uuid == null || uuid.length() <= 0) {
            uuid = "UNKNOWN";
        }
        sb.append("[UUID:").append(uuid).append("]");
        if (e == null) {
            sb.append("[Error Message]:").append(msg);
        } else {
            sb.append("[Error Message]:").append(msg + "\n" + e);
        }
        return sb.toString();
    }    

	/**
	 * Closes the connection if opened
	 */
	public static void disconnect() {
		if (httpConn != null) {
			httpConn.disconnect();
		}
	}

	/**
	 * Get localhost ip
	 * @return
	 */
	public static String getLocalIp() {
		InetAddress ip = null;
		try {
			ip = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip.getHostAddress();
	}
}
