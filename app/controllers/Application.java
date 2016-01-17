package controllers;

import models.*;
import play.Logger;
import play.libs.OAuth2;
import play.libs.WS;
import play.mvc.Before;
import play.mvc.Controller;
import play.libs.*;
import play.libs.ws.*;
import com.google.gson.*;
import org.w3c.dom.*;
import java.util.*;

import com.google.gson.JsonObject;

public class Application extends Controller {

/*       
Donner l'autorisation :
https://graph.facebook.com/oauth/authorize?client_id=481183412053539&redirect_uri=http://aqueous-hamlet-7793.herokuapp.com/application/tryAuth

récupérer l'access token :
https://graph.facebook.com/oauth/access_token?client_id=481183412053539&client_secret=98862f0bfb3790e28919e1c26bc47384&grant_type=authorization_code&redirect_uri=http://aqueous-hamlet-7793.herokuapp.com/application/tryAuth&code=

Liste des contacts :
https://graph.facebook.com/v2.5/me/friends?access_token=
https://graph.facebook.com/v2.5/me?access_token=

*/
    public static OAuth2 FACEBOOK = new OAuth2(
            "https://graph.facebook.com/oauth/authorize",
            "https://graph.facebook.com/oauth/access_token",
            "481183412053539",
            "98862f0bfb3790e28919e1c26bc47384"
    );
    
    private static String GmailRedirectURI = "http://aqueous-hamlet-7793.herokuapp.com/" ;
    private static String GmailResponseAuth = "gmailresponseauth" ;
    private static String GmailClientID = "1012335406269-cdaq4po57r24hqq297k08kaeug1g2aba.apps.googleusercontent.com" ;
    private static String GmailClientSecret = "Ztbhi7MIdJeqv0m24_KXlGOK" ;
    
    private static String FacebookClientID = "481183412053539" ;
    private static String FacebookClientSecret = "98862f0bfb3790e28919e1c26bc47384" ;
    private static String FacebookRedirectURI = "http://aqueous-hamlet-7793.herokuapp.com/application/tryAuth" ;
    private static String FacebookAuthorizeRequest = "https://graph.facebook.com/oauth/authorize?client_id=" + FacebookClientID + "&redirect_uri=" + FacebookRedirectURI ;
    private static String FacebookTokenRequest = "https://graph.facebook.com/oauth/access_token?client_id=" + FacebookClientID + "&client_secret=" + FacebookClientSecret + "&grant_type=authorization_code&redirect_uri=" + FacebookRedirectURI +"&code=" ;
    private static String FacebookContactRequest = "https://graph.facebook.com/v2.5/me/friends?access_token=" ;
    
    // ------------------------------------------------------------------------------------------------------------------------------------
    //
    // Basics methods
    //
    
    public static void testContacts() {
    	render() ;
    }
    
    public static void about() {
    	render() ;
    }
    
    // ------------------------------------------------------------------------------------------------------------------------------------
    
    
    public static void index() {
    	String state = params.get("state") ;
    	String code = params.get("code") ;
    	
    	// Google's response : to ask authorization
    	if( state != null && state.equals(GmailResponseAuth) ) {
			
			// Redirect to import contacts of Gmail
			redirect( "http://aqueous-hamlet-7793.herokuapp.com/gmail/import?code=" + code ) ;
			
    	}
    	
    	render();
    }
    
    // Demande d'autorisation d'accès aux contacts à Gmail
    public static void gmailAuth() {
    	
    	String targetURL = "https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/contacts.readonly&redirect_uri=" + 
    		GmailRedirectURI + "&response_type=code&client_id=" + GmailClientID + "&state=" + GmailResponseAuth ;
    	
    	redirect( targetURL ) ;
    }
    
    public static void gmailImport() {
    	/*
		 * Help :
		 * - https://www.playframework.com/documentation/1.3.x/libs#WebServiceclient
		 * - https://www.playframework.com/documentation/1.3.x/libs#ParsingXMLusingXPath
		 * - JavaDoc of Java and Play Framework
		 */
		
    	String code = params.get("code") ;
    	
		// Make POST request at Google to get token
		WS.HttpResponse response = WS.url("https://www.googleapis.com/oauth2/v4/token")
			.setParameter("code", code)
			.setParameter("client_id", GmailClientID)
			.setParameter("client_secret", GmailClientSecret)
			.setParameter("redirect_uri", "http://aqueous-hamlet-7793.herokuapp.com/")
			.setParameter("grant_type", "authorization_code")
			.post() ;
		
		JsonElement jsonElt = response.getJson() ;							// Get Json response at POST request
		JsonObject jsonObject = jsonElt.getAsJsonObject() ;					// Convert JsonElement to JsonObject
		String accessToken = jsonObject.get("access_token").toString() ;	// Extract 'access_token'
		accessToken = accessToken.substring(1, accessToken.length()-1) ;	// Remove double quote on token
    	
    	/* ------------------------------------------------------------------------------------------------------------------------------------
    	//
    	// Few tests
    	//
    	
    	// Redirect to get contacts list (only test)
		//redirect( "https://www.google.com/m8/feeds/contacts/default/full?access_token=" + accessToken ) ;
    	
    	// Make GET request at Google to get contacts
		// v1
		//WS.HttpResponse contactResponse = WS.url( "https://www.google.com/m8/feeds/contacts/default/full?access_token=" + accessToken ).get() ;
		// v2
		//WS.HttpResponse contactResponse = WS.url( "https://www.google.com/m8/feeds/contacts/default/full?access_token=%s", accessToken).get() ;
		// v3
		WS.HttpResponse contactResponse = WS.url( "https://www.google.com/m8/feeds/contacts/default/full")
			//.setParameter("Authorization", "Bearer " + accessToken)	// 401 : Authorization require
			//.setHeader("Authorization", accessToken)					// 401 : Unknown authorization header
			//.authenticate("Bearer ", accessToken)						// 401 : Unknown authorization header
			.setHeader("Authorization", "Bearer " + accessToken)		// 403 : Forbidden
			.get() ;
		
    	 ------------------------------------------------------------------------------------------------------------------------------------ */
    	
    	// Make GET request at Google to get contacts
		WS.HttpResponse contactResponse = WS.url( "https://www.google.com/m8/feeds/contacts/default/full")
			.setHeader("Authorization", "Bearer " + accessToken)
			.get() ;
		
		// Debug
		String nb = Integer.toString(contactResponse.getStatus()) ;			// HTTP number of the GET request
		nb += " : " + contactResponse.getStatusText() ;						// HTTP text of the GET request
		nb += " (access_token=" + accessToken + ")" ;						// Token to get the list of contacts
		
		render(nb);
    }

    public static void tryAuth(String code) {
        WS.HttpResponse response = WS.url(FacebookTokenRequest + code)
            .get() ;

        String accessToken = response.getString();
        accessToken = accessToken.substring(13, accessToken.length()-16) ;   // Remove double quote on token;

        String contactRequest = FacebookContactRequest + accessToken;
        WS.HttpResponse contactResponse = WS.url(contactRequest)
            .get() ;
        String success =  contactResponse.getString();
        
        render(success);
    }

    public static void authOk(String access_token) {
    	
    	render(access_token);
    }

    public static void auth() {
        FACEBOOK.retrieveVerificationCode(authURL());
    }

    static String authURL() {
       	return FacebookRedirectURI;
    }
}
