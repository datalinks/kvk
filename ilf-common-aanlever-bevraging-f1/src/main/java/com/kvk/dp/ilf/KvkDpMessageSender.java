package com.kvk.dp.ilf;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

class KvkDpMessageSender {

	private static String dest_A_action_1_INPUT = "src/resources/xml/test.xml";
	private static String dest_A_action_2_INPUT = "";
	private static String dest_B_action_1_INPUT = "";
	private static String dest_B_action_2_INPUT = "";
	//private static String TEST_URL = "http://dpd-SNB01-02.k94.kvk.nl/kvk-dp-testbackend-webservice/services/CalculatorPort";
	private static String TEST_URL = "http://dpd-SNB01-02.k94.kvk.nl:10555";
	 


	// Converting the XML file into a useable String
	protected static String buildInputStringFromXmlFile(
			String xmlFileName) throws IOException {
		StringBuilder result = new StringBuilder();
		BufferedReader input = new BufferedReader(new FileReader(xmlFileName));
		String line = null;
		while ((line = input.readLine()) != null) {
			result.append(line);
			result.append(System.getProperty("line.separator"));
		}
		input.close();
		return result.toString();
	}

	public static void main(String[] args) {
		KvkDpMessageSender k1 = new KvkDpMessageSender();
		try {
			String result = k1.callWebService(TEST_URL,"test SERVICe",buildInputStringFromXmlFile(dest_A_action_1_INPUT));
			System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String callWebService(String url, String serviceName, String requestBody) throws Exception {
		StringBuffer sb = new StringBuffer();
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);
		StringEntity entityIn = new StringEntity(requestBody);
		httppost.setEntity(entityIn);
		HttpResponse response = null;
		response = httpclient.execute(httppost);
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new Exception("Exception calling " + serviceName+ " statusCode " + response.getStatusLine().getStatusCode());
		}
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		String line = null;
		while ((line = rd.readLine()) != null) {
			sb.append(line);
		}
		return sb.toString();
	}

}

