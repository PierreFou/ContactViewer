package controllers;

import models.*;
import play.Logger;
import play.libs.OAuth2;
import play.libs.WS;
import play.mvc.Before;
import play.mvc.Controller;
//import play.libs.WS;
import play.libs.ws.*;
import com.google.gson.*;
/*import java.net.*;
import java.io.*;*/

import com.google.gson.JsonObject;

public class Application extends Controller {


//        https://graph.facebook.com/oauth/access_token?redirect_uri=http://loisant.org&client_id=
    public static OAuth2 FACEBOOK = new OAuth2(
            "https://graph.facebook.com/oauth/authorize",
            "https://graph.facebook.com/oauth/access_token",
            "481183412053539",
            "98862f0bfb3790e28919e1c26bc47384"
    );
    
    private static String GmailRedirectURI = "http://aqueous-hamlet-7793.herokuapp.com/" ;
    private static String GmailResponseAuth = "gmailresponseauth" ;
    private static String GmailTokenURI = "http://aqueous-hamlet-7793.herokuapp.com/gmail/oauth2callback" ;
    private static String GmailClientID = "1012335406269-bbij7i5fc8ouhefgf6qlnnh878b80vm0.apps.googleusercontent.com" ;
    private static String GmailClientSecret = "hYbMsEPhgw0sCFZvtqzkzR4F" ;
    
    
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
    	
    	String flag = "null" ;
    	
    	// réponse de Gmail à la demande d'autorisation
    	if( state != null && state.equals(GmailResponseAuth) ) {
    		//https://www.googleapis.com/oauth2/v4/token
    		
    		/*
POST request

code=4/P7q7W91a-oMsCeLvIaQm6bTrgtp7&
client_id=8819981768.apps.googleusercontent.com&
client_secret={client_secret}&
redirect_uri=https://oauth2-login-demo.appspot.com/code&
grant_type=authorization_code
    		*/
    		
    		/*WSRequestHolder holder = WS.url("http://example.com");*/
    		
    		/*WS.url("https://www.googleapis.com/oauth2/v4/token")
    		.setContentType("application/x-www-form-urlencoded")
    		.post("key1=value1&key2=value2");
    		*/
    		//redirect() ;
    		//redirect( GmailTokenURI + "?code=" + code ) ;
    		
    		//redirect( "http://aqueous-hamlet-7793.herokuapp.com/gmail/oauth2callback?code=" + code ) ;
    		
    		
    		WS.HttpResponse response = WS.url("https://www.googleapis.com/oauth2/v4/token")
				.setParameter("code", code)
				.setParameter("client_id", GmailClientID)
				.setParameter("client_secret", GmailClientSecret)
				.setParameter("redirect_uri", "http://aqueous-hamlet-7793.herokuapp.com/")
				.setParameter("grant_type", "authorization_code")
				.post() ;
			
			/*JsonElement jsonElt = response.getJson() ;
			JsonArray jsonArray = jsonElt.getAsJsonArray() ;*/
			/*JsonArray jsonArray = response.getJson().getAsJsonArray() ;
			
			for(int itElt=0 ; itElt < jsonArray.size() ; itElt++ ) {
			JsonElement e = jsonArray.get(itElt) ;
			flag += itElt + " : " + e ;
			}*/
			JsonElement jsonElt = response.getJson() ;
			//flag = "Array ? " + jsonElt.isJsonArray() + " ; Object ? " + jsonElt.isJsonObject() + " ; Primitive ? " + jsonElt.isJsonPrimitive() ;
			JsonObject jsonObject = jsonElt.getAsJsonObject() ;
			flag = jsonObject.get("access_token") ;
			
			
			//flag = jsonElt.toString() ;
			//flag = e.toString() ;
			
    	}
    	
    	render(flag);
    }

    public static void tryAuth(String code) {
    	
    	render(code);
    }

    public static void authOk(String access_token) {
    	
    	render(access_token);
    }

    public static void auth() {
        /*if (OAuth2.isCodeResponse()) {
            User u = connected();
            OAuth2.Response response = FACEBOOK.retrieveAccessToken(authURL());
            u.access_token = response.accessToken;
            u.save();
            index();
        }*/
        FACEBOOK.retrieveVerificationCode(authURL());
    }

    static String authURL() {
        //return play.mvc.Router.getFullUrl("Application.auth");
       	return "https://tranquil-sands-9268.herokuapp.com/application/tryAuth";
       //	https://graph.facebook.com/endpoint?key=value&amp;access_token=481183412053539
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
