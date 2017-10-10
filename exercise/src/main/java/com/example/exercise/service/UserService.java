package com.example.exercise.service;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.example.exercise.model.AgeGroup;
import com.example.exercise.model.GroupResult;
import com.example.exercise.model.User;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.graph.Graph;

public class UserService {
	
	private static final String userKey = "STX-COMMUNE";
	
	// Create a loading cache here and load the user in the list if its empty. Use the key userKey	
	// Resource https://github.com/google/guava/wiki/CachesExplained
	//TODO: Implement a guava Loading cache to load the user list from FIle and store it in the cache

	/**
	 * Service to return a list of user 
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @throws JSONException
	 * @throws ExecutionException 
	 */
	public static List<User> loadUser() throws IOException, ParseException, JSONException, ExecutionException{
		
		List<User> userList = new ArrayList<User>();
		
		LoadingCache<String, User> usersCache = 
		         CacheBuilder.newBuilder()
		         .maximumSize(100)
		         .expireAfterAccess(30, TimeUnit.MINUTES)
		         .build(new CacheLoader<String, User>() {
		            
		            @Override
		            public User load(String userKey) throws Exception {
		               return getFromDatabase(userKey, getJSONIterator());
		            } 
		         });

		      try {
		    	  List<String> userIDs = getUserIDs(getJSONIterator());
		    	  Iterator<String> idIterator = userIDs.iterator();
		    	  while(idIterator.hasNext()) {
		    		  String id = idIterator.next();
		    		  userList.add(usersCache.get(id));
		    	  }
		         

		      } catch (ExecutionException e) {
		         e.printStackTrace();
		      }

		return userList;
	}
	

	
	private static Iterator<?> getJSONIterator() {
		
		JSONParser parser = new JSONParser();
	
        Object obj = null;
		try {
			obj = parser.parse(new FileReader("user.json"));
		} catch (IOException | ParseException e) {
			e.printStackTrace();
		}

        JSONArray userJSONArray = (JSONArray) obj;
        Iterator<?> JSONIterator = userJSONArray.iterator();
		
		return JSONIterator;
	}
	
	private static List<String> getUserIDs(Iterator<?> JSONIterator) {
		
		List<String> userIDs = new ArrayList<String>();
		
        while (JSONIterator.hasNext()) {
			JSONObject currentUser = (JSONObject) JSONIterator.next();
			userIDs.add(currentUser.get("id").toString());
		}
		
		return userIDs;
	}

	private static User getFromDatabase(String userKey, Iterator<?> JSONIterator) {
	
	    Map<String, User> database = new HashMap<String, User>(); 
	
        while (JSONIterator.hasNext()) {
			JSONObject currentUser = (JSONObject) JSONIterator.next();
			User user = new User();
			user.setId(currentUser.get("id").toString());
			user.setFirstname(currentUser.get("firstname").toString());
			user.setLastname(currentUser.get("lastname").toString());
			user.setEmail(currentUser.get("email").toString());
			user.setMobile(currentUser.get("mobile").toString());
			user.setTown(currentUser.get("town").toString());
			Date dOB = new Date((Long)currentUser.get("dateOfBirth")*1000);
			user.setDateOfBirth(dOB);
			database.put(user.getId(), user);
		}
	    
	    return database.get(userKey);		
	 }
	
	/**
	 * This method should be called by loading cache to load the user from file
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static List<User> loadUserIfCacheMiss() throws JSONException, IOException, ParseException{
		return new ArrayList<User>();
	}
	
	/**
	 * Service to return a list of Sorted user 
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @throws JSONException
	 */
	public static List<GroupResult> loadSortedUserGroup() throws IOException, ParseException, JSONException{
		
		// Step1: Load the users list
		
		// Step2: Sort the user list 

		// Write an algorithm here to sort the list of users 
		// First based on the first name and then last name
		// Eg. if the users are John Doe, John Wayne and Clint Eastwood, the list should be 
		// 1.Clint Eastwood, 
		// 2. John Doe
		// 3. John Wayne 
		
		return new ArrayList<>();
	}
	
	/**
	 * Exercise 2: Load list of user and find the one with email supplied
	 * @param email
	 * @return
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public static User getUserByEmail(final String email) throws JSONException, IOException, ParseException {
		// Step1: Load the users
		// TODO: write the code to load the users 
	
		// Step2. Filter the user based on email address, if more than one return the oldest
		// TODO:_ Filter using email 
		return null;
	}
	
	
	// Read the file "user.json" and convert the JSON object into Java object
	private static final List<User> getUserListFromFile() throws IOException, ParseException, JSONException{
		// Step1: Read the user.json file 
		
		// Step2: Convert the user JSON string to java object
		// Hint: You can look at ObjectMapper provided by Jackson library
		
		return new ArrayList<User>();
	}
}
