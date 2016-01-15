package controllers;

import models.*;
import play.Logger;
import play.libs.OAuth2;
import play.libs.WS;
import play.mvc.Before;
import play.mvc.Controller;
//import play.libs.WS;
import play.libs.*;
import play.libs.ws.*;
import com.google.gson.*;
import org.w3c.dom.*;
import java.util.*;
/*import java.net.*;
import java.io.*;*/

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
    private static String GmailTokenURI = "http://aqueous-hamlet-7793.herokuapp.com/gmail/oauth2callback" ;
    private static String GmailClientID = "1012335406269-cdaq4po57r24hqq297k08kaeug1g2aba.apps.googleusercontent.com" ;
    private static String GmailClientSecret = "Ztbhi7MIdJeqv0m24_KXlGOK" ;
    
    private static String FacebookClientID = "481183412053539" ;
    private static String FacebookClientSecret = "98862f0bfb3790e28919e1c26bc47384" ;
    private static String FacebookRedirectURI = "http://aqueous-hamlet-7793.herokuapp.com/application/tryAuth" ;
    private static String FacebookAuthorizeRequest = "https://graph.facebook.com/oauth/authorize?client_id=" + FacebookClientID + "&redirect_uri=" + FacebookRedirectURI ;
    private static String FacebookTokenRequest = "https://graph.facebook.com/oauth/access_token?client_id=" + FacebookClientID + "&client_secret=" + FacebookClientSecret + "&grant_type=authorization_code&redirect_uri=" + FacebookRedirectURI +"&code=" ;
    private static String FacebookContactRequest = "https://graph.facebook.com/v2.5/me/friends?access_token=" ;
    
    
    public static void testContacts() {
    	render() ;
    }
    
    // Demande d'autorisation d'accès aux contacts à Gmail
    public static void gmailAuth() {
    	
    	String targetURL = "https://accounts.google.com/o/oauth2/v2/auth?scope=https://www.googleapis.com/auth/contacts.readonly&redirect_uri=" + 
    		GmailRedirectURI + "&response_type=code&client_id=" + GmailClientID + "&state=" + GmailResponseAuth ;
    	
    	redirect( targetURL ) ;
    }
    
    public static void gmailCallback() {
    	/*String state = params.get("state") ;
    	String code = params.get("code") ;
    	if( state.equals(GmailTokenURI) ) {
    		redirect( GmailTokenURI + "?code=" + code ) ;
    	}
    	
    	render(state) ;*/
    	String code = params.get("code") ;
    	render(code) ;
    }
    
    public static void testPage() {
    	render() ;
    	
    	/*
    	String ur="www.exempl.ma\index.jsp";\\ton URL
		String post="nom=toto&age=12"\\
		URL url = new URL(ur);
		URLConnection  conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
		writer.write(post);
		writer.flush();
		//recuperation du code html
		String reponse=null,ligne = null;
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((ligne = reader.readLine()) != null) {
				reponse+= ligne.trim()+"\n";
		}
		System.out.print(reponse);
		*/
		/*
		// Code adapté de https://openclassrooms.com/forum/sujet/envoi-requete-http-post-66456
		try
		{
			String paramPost="nom=toto";
			URL url = new URL( "http://aqueous-hamlet-7793.herokuapp.com/" );
			URLConnection  conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
			writer.write(paramPost);
			writer.flush();
			//recuperation du code html
			String reponse=null,ligne = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			while ((ligne = reader.readLine()) != null) {
					reponse+= ligne.trim()+"\n";
			}
			//System.out.print(reponse);
			render(reponse) ;
		} catch(Exception e) {
			redirect( "http://localhost:9000/" ) ;
		}
		*/
    }
    
    public static void index() {
    	String state = params.get("state") ;
    	String code = params.get("code") ;
    	
    	String flag = "TEST" ;
    	
    	// Google's response : to ask authorization
    	if( state != null && state.equals(GmailResponseAuth) ) {
    		
			// Redirect to get contacts list (only test)
			//redirect( "https://www.google.com/m8/feeds/contacts/default/full?access_token=" + accessToken ) ;
			
			// Redirect to import contacts of Gmail
			redirect( "http://aqueous-hamlet-7793.herokuapp.com/gmail/import?code=" + code ) ;
    	}
    	
    	render(flag);
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
			//.setHeader("Authorization", "Bearer " + accessToken)		// 403 : Forbidden
			.setHeader("Authorization", "Bearer " + accessToken)
			.get() ;
		
		// Sample
	//	WS.HttpResponse contactResponse = WS.url( "https://www.playframework.com/documentation/1.3.x/libs#ParsingXMLusingXPath?toto=%s", "tata" ).get() ;
		
	//	org.w3c.dom.Document xmlDoc = contactResponse.getXml() ;						// Get Xml document from response at GET request
		String nb = Integer.toString(contactResponse.getStatus()) ;
		nb += " : " + contactResponse.getStatusText() ;
		nb += " (access_token=" + accessToken + ")" ;
		
		//String nb = contactResponse.getString() ;
		
		ArrayList contactsList = new ArrayList() ;
		
		/*for( Node entry: XPath.selectNodes("//entry", xmlDoc) ) { // /feed/entry
			String name = XPath.selectText("//title", entry);
			contactsList.add( name ) ;
		}*/
		
		/*NodeList nodes = xmlDoc.getElementsByTagNameNS("http://www.w3.org/2005/Atom", "entry") ;
		int nb = nodes.getLength() ;
		*/
		/*for( :  ) {
			
		}*/
		
	//	String nb = contactResponse.success() ? "success" : "fail" ;
		//String nb = xmlDoc.getDocumentElement().getTagName() ;
		
		//render(contactsList) ;
		//int nb = contactsList.size();
		render(nb);
    }
    
    public static void about() {
    	render() ;
    }

    public static void tryAuth(String code) {
    	/*String targetURL = FacebookTokenRequest + code;
        
        redirect( targetURL ) ;
*/

        WS.HttpResponse response = WS.url(FacebookTokenRequest + code)
            .post() ;

        JsonElement jsonElt = response.getJson() ;                          // Get Json response at POST request
        JsonObject jsonObject = jsonElt.getAsJsonObject() ;                 // Convert JsonElement to JsonObject
        String accessToken = jsonObject.get("access_token").toString() ;    // Extract 'access_token'
        accessToken = accessToken.substring(13, accessToken.length()-16) ;    // Remove double quote on token

        String contactRequest = FacebookContactRequest + accessToken;
        
        WS.HttpResponse response = WS.url(contactRequest)
            .get() ;
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
/*
    @Before
    static void setuser() {
        /*User user = null;
        if (session.contains("uid")) {
            Logger.info("existing user: " + session.get("uid"));
            user = User.get(Long.parseLong(session.get("uid")));
        }
        if (user == null) {
            user = User.createNew();
            session.put("uid", user.uid);
        }
        renderArgs.put("user", user);
    }

    static User connected() {
        return (User)renderArgs.get("user");
    }

    public static void index() {
        User u = connected();
        JsonObject me = null;
        if (u != null && u.access_token != null) {
            me = WS.url("https://graph.facebook.com/me?access_token=%s", WS.encode(u.access_token)).get().getJson().getAsJsonObject();
        }
        render(me);
    }

    public static void auth() {
        if (OAuth2.isCodeResponse()) {
            User u = connected();
            OAuth2.Response response = FACEBOOK.retrieveAccessToken(authURL());
            u.access_token = response.accessToken;
            u.save();
            index();
        }
        FACEBOOK.retrieveVerificationCode(authURL());
    }*/
}
