import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.fluent.Request;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public class Chat {
	
	/**
	* dobimo osnovno stran
	*/
	public static void get_index(){
        try {
            String hello = Request.Get("http://chitchat.andrej.com")
                                  .execute()
                                  .returnContent().asString();
            //System.out.println(hello);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
	
	/**
	* funkcija, ki vrne seznam trenutnih uporabnikov.
	*/
	public static List<User> get_users(String name) {
		ObjectMapper mapper = new ObjectMapper(); //z mapperjem pretvarjamo iz in v Json.
		mapper.setDateFormat(new ISO8601DateFormat());
		try {
			String responseBody = Request.Get("http://chitchat.andrej.com/users").execute().returnContent().asString();
			TypeReference<List<User>> t = new TypeReference<List<User>>() { };
			List<User> asistenti2 = mapper.readValue(responseBody, t);
			int i = 0;
			while(i<asistenti2.size()){
				if (asistenti2.get(i).getUsername().equals(name)) {
					asistenti2.remove(i);
					break;
				}
				i++;
			}
			return asistenti2;
			
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<User>() ;
		}
	}
	
	/**
	* funkcija, ki dobi ime uporabnika (ki še ni vpisan) in ga vpiše. Èe je uporabnik
	* že vpisan, ga ne vpiše še enkrat. 
	*/
	public static Boolean log_in(String username) {  
	URI uri;
	try {
		uri = new URIBuilder("http://chitchat.andrej.com/users")
		          .addParameter("username", username)
		          .build();
		String responseBody;
		responseBody = Request.Post(uri)
		      .execute()
		      .returnContent()
		      .asString();
		//System.out.println(responseBody);
		return true;
		
	} catch (URISyntaxException e) {
		e.printStackTrace();
		return false;
		
	} catch (ClientProtocolException e) {
		e.printStackTrace();
		return false;
		
	} catch (IOException e) {
		e.printStackTrace();
		return false;
	}
	}
	
	/**
	* Funkcija izpiše uporabnika katerega ime podamo za parameter. 
	* Èe izpisujemo uporabnika, ki ni vpisan javi napako. 
	*/
	public static void log_out(String username){
		URI uri;
		try {
			uri = new URIBuilder("http://chitchat.andrej.com/users")
					.addParameter("username", username)
					.build();
			String responseBody = Request.Delete(uri)
					.execute()
					.returnContent()
					.asString();
			//System.out.println(responseBody);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	* Funkcija vzame dva parametra: ime uporabnika (ki mora biti prijavljen) 
	* in vsebino sporoèila. 
	* Sporoèilo pošlje vsem trenutnim uporabnikom. 
	*/
	public static void send (String me, String myMessage ){
		  URI uri;
		try {
			uri = new URIBuilder("http://chitchat.andrej.com/messages")
			          .addParameter("username", me)
			          .build();

			String message = "{ \"global\" : true, \"text\" : \""+myMessage+"\"}";
			  
			String responseBody = Request.Post(uri)
			          .bodyString(message, ContentType.APPLICATION_JSON)
			          .execute()
			          .returnContent()
			          .asString();
			//System.out.println(responseBody);
			
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	/**
	* Funkcija vzame tri parametre: ime uporabnika, ki pošilja sporoèilo (ki mora biti prijavljen), 
	* ime uporabnika, ki bo sporoèilo prejel in vsebino sporoèila. 
	*/
	public static void send (String me, String friend, String myMessage ){
		 URI uri;
			try {
				uri = new URIBuilder("http://chitchat.andrej.com/messages")
				          .addParameter("username", me)
				          .build();

				String message = "{ \"global\" : false, \"recipient\" : \""+friend+"\", \"text\" : \""+myMessage+"\"}";
				  
				String responseBody = Request.Post(uri)
				          .bodyString(message, ContentType.APPLICATION_JSON)
				          .execute()
				          .returnContent()
				          .asString();
				//System.out.println(responseBody);
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	/**
	* Funkcija vzame za parameter ime vpisanega uporabnika in izpiše vsa sporoèila, 
	* ki jih je ta uporabnik prejel od zadnjega klica te funkcije (oz. od vpisa v klepetalnik). 
	*/	
	public static List<Message> recive (String me) {
		 URI uri;
		 String napaka = "Prišlo je do napake pri prejemanju sporoèila.";
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(new ISO8601DateFormat());
		try {
			uri = new URIBuilder("http://chitchat.andrej.com/messages")
			          .addParameter("username", me)
			          .build();
			 String responseBody = Request.Get(uri)
                    .execute()
                    .returnContent()
                    .asString();
			TypeReference<List<Message>> t = new TypeReference<List<Message>>() { };
			List<Message> asistenti2 = mapper.readValue(responseBody, t);
			return asistenti2;
		} catch (URISyntaxException e) {
			e.printStackTrace();
			System.out.println (napaka);
			return new ArrayList<Message>();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			System.out.println (napaka);			
			return new ArrayList<Message>();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println (napaka);
			return new ArrayList<Message>();
		}		  
	}
	}

