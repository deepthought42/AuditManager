package com.looksee.auth;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.auth0.client.mgmt.ManagementAPI;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class Auth0ManagementApi {
	private static Logger log = LoggerFactory.getLogger(Auth0ManagementApi.class);

	private static String client_id = "aSlsPI5ENJXKSYHyxaG6oxo46peRT25N";
	private static String client_secret = "cz0DkblgOaI_LMIxXayoAja6ebcBKVnaE3eYzuBjj-0aBuOuJUjDE8mNpUdAPz51";
	private static String domain = "look-see.us.auth0.com";
	private static String audience_url = "https://" + domain + "api/v2/";
	private static String api_token = "wWn9rubrIFRQZI7buiYVsadVQi6ewtQH";
	
	//STAGING
	/*
	private static String client_id = "eAlhwgR8u68lHeX4M5R0VNqPycg7LEEo";
	private static String client_secret = "vCo_aM0QgeQY6Ymwj2KsdeCLPahjyVVHQZYnmTfsgP8ScCLprV7n2GIHiuRKucNp";
	private static String base_url = "https://staging-qanairy.auth0.com/";
	private static String audience_url = base_url + "api/v2/";
	private static String api_token = "jEhM2ZhIWwy49YiJsNE9g9YtLEEn3Vxw";
*/
	
	private ManagementAPI mgmt_api;

	public Auth0ManagementApi(String access_token){
		this.mgmt_api = new ManagementAPI(domain, api_token);
	}
	
	public static String getToken() throws UnirestException{
		HttpResponse<String> response1 = Unirest.post(domain + "oauth/token")
					  .header("content-type", "application/json")
					  .body("{\"client_id\":\"" + client_id + "\",\"client_secret\":\"" + client_secret + "\",\"audience\":\"" + audience_url + "\",\"grant_type\":\"client_credentials\"}")
					  .asString();		

		return jsonMap(response1).get("access_token").toString();
	}
	
	public static Map<String, Object> jsonMap(HttpResponse<String> resp){
		Map<String, Object> map = new HashMap<String, Object>();

		try {
			ObjectMapper mapper = new ObjectMapper();
			
			// convert JSON string to Map
			map = mapper.readValue(resp.getBody(), new TypeReference<Map<String, Object>>(){});
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return map;
	}
	
	public static HttpResponse<String> updateUserAppMetadata(String user_id, String app_metadata_body) throws UnirestException{
    	String token = getToken();
    	String request_url = audience_url+"users/" + user_id;
		HttpResponse<String> response = Unirest.patch(request_url)
				  .header("content-type", "application/json")
				  .header("Authorization", "Bearer " + token)
				  .body("{\"app_metadata\": " + app_metadata_body + "}")
				  .asString();
		
		return response;
	}

	public static HttpResponse<String> deleteUser(String user_id) throws UnirestException {
		String token = getToken();
		log.info("USER ID PASSED FOR DELETION :::::::::::::     "+user_id);
    	String request_url = audience_url+"users/" + user_id;
		HttpResponse<String> response = Unirest.delete(request_url)
				  .header("content-type", "application/json")
				  .header("Authorization", "Bearer " + token)
				  .asString();
		
		return response;
	}

	public ManagementAPI getApi() {
		return mgmt_api;
	}
}
