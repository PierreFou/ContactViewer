package controllers;

import models.*;
import play.Logger;
import play.libs.OAuth2;
import play.libs.WS;
import play.mvc.Before;
import play.mvc.Controller;
//import play.libs.ws.*;
//import play.libs.WS;

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
    
    
    // Demande d'autorisation à Gmail
    public static void gmailAuth() {
    	/*
    	https://accounts.google.com/o/oauth2/v2/auth?
    	scope=email%20profile&
    	state=security_token%3D138r5719ru3e1%26url%3Dhttps://oa2cb.example.com/myHome&
    	redirect_uri=http://aqueous-hamlet-7793.herokuapp.com/&
    	response_type=code&
    	client_id=1012335406269-bbij7i5fc8ouhefgf6qlnnh878b80vm0.apps.googleusercontent.com
    	*/
    	String targetURL = "https://accounts.google.com/o/oauth2/v2/auth?scope=email%20profile&redirect_uri=" + 
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
    }
    
    public static void index() {
    	String state = params.get("state") ;
    	String code = params.get("code") ;
    	
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
    		
    		redirect( "http://aqueous-hamlet-7793.herokuapp.com/gmail/oauth2callback?code=" + code ) ;
    	}
    	
    	render();
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
