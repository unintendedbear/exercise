package com.example.exercise.service;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.example.exercise.model.GroupResult;
import com.example.exercise.model.User;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;

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
		               return getUserListFromFile(userKey, getJSONIterator());
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
		    	  System.out.println("No users were found in the cache");
		         e.printStackTrace();
		      }

		return userList;
	}
	
	// Read the file "user.json" and convert the JSON object into Java object
	/**
	 * Service to return an iterator over a JSON array, avoiding loading the file more than once 
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public static Iterator<?> getJSONIterator() {
		
		// Step1: Read the user.json file
		
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
	
	/**
	 * Service to return a list of user ids 
	 * @return
	 */
	public static List<String> getUserIDs(Iterator<?> JSONIterator) {
		
		List<String> userIDs = new ArrayList<String>();
		
        while (JSONIterator.hasNext()) {
			JSONObject currentUser = (JSONObject) JSONIterator.next();
			userIDs.add(currentUser.get("id").toString());
		}
		
		return userIDs;
	}

	/**
	 * The most expensive service to obtain a user from the JSON
	 * @return
	 */
	public static User getUserListFromFile(String userKey, Iterator<?> JSONIterator) {
		
		// Step2: Convert the user JSON string to java object
	
	    Map<String, User> usersJSON = new HashMap<String, User>(); 
	
        while (JSONIterator.hasNext()) {
			JSONObject currentUser = (JSONObject) JSONIterator.next();
			User user = new User();
			user.setId(currentUser.get("id").toString());
			user.setFirstname(currentUser.get("firstname").toString());
			user.setLastname(currentUser.get("lastname").toString());
			user.setEmail(currentUser.get("email").toString());
			user.setMobile(currentUser.get("mobile").toString());
			user.setTown(currentUser.get("town").toString());
			Date dOB = new Date((Long)currentUser.get("dateOfBirth"));
			user.setDateOfBirth(dOB);
			usersJSON.put(user.getId(), user);
		}
	    
	    return usersJSON.get(userKey);		
	 }
	
	/**
	 * This method should be called by loading cache to load the user from file
	 * @return
	 * @throws JSONException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static List<User> loadUserIfCacheMiss() throws ExecutionException {
		return new ArrayList<User>();
	}
	
	/**
	 * Service to return a list of Sorted user 
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 * @throws JSONException
	 */
	public static List<GroupResult> loadSortedUserGroup() throws JSONException, IOException, ParseException, ExecutionException {
		
		List<GroupResult> groupList = new ArrayList<GroupResult>();
		// Step1: Load the users list
		List<User> userList = new ArrayList<User>();
		try {
			userList = loadUser();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		// Step2: Sort the user list
		
		// Write an algorithm here to sort the list of users 
		// First based on the first name and then last name
		// Eg. if the users are John Doe, John Wayne and Clint Eastwood, the list should be 
		// 1.Clint Eastwood, 
		// 2. John Doe
		// 3. John Wayne 
		Iterator<User> userIterator = userList.iterator(); // Obtaining all lastNames
		List<String> lastNames = new ArrayList<String>();
		while (userIterator.hasNext()) {
			User user = userIterator.next();
			lastNames.add(user.getLastname());
		}
		
		List<String> userLastNameDistinct = lastNames.stream().distinct().collect(Collectors.toList()); // Obtaining distinct lastNames
		Collections.sort(userLastNameDistinct); // Sorting the last names so that groupList is created orderly
		Iterator<String> lastNameIterator = userLastNameDistinct.iterator();
		while (lastNameIterator.hasNext()) { // For each unique lastName
			String lastName = lastNameIterator.next(); // Initialising GroupResult attributes
			int adult = 0;
			int children = 0;
			userIterator = userList.iterator();
			while (userIterator.hasNext()) { // For each user
				User user = userIterator.next();
				if (user.getLastname().contentEquals(lastName)) {
					if (getUserAge(user) >= 18) {
						adult++;
					} else {
						children++;
					}
					userIterator.remove();
				}
			}
			groupList.add(new GroupResult(lastName, adult, children)); // Adds resulting GroupResult to groupList
		}
		
		return groupList;
	}
	
	/**
	 * Service to return the age of a user 
	 * @return
	 */
	public static int getUserAge(User user) {
		
		LocalDate today = LocalDate.now();
		LocalDate userDayOfBirth = ( (java.sql.Date) user.getDateOfBirth()).toLocalDate();
		return Period.between(userDayOfBirth, today).getYears();
	}
	
	/**
	 * Exercise 2: Load list of user and find the one with email supplied
	 * @param email
	 * @return
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws JSONException 
	 */
	public static User getUserByEmail(final String email) throws JSONException, IOException, ParseException, ExecutionException {
		// Step1: Load the users
		List<User> userList = new ArrayList<User>();
		try {
			userList = loadUser();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} 
	
		// Step2. Filter the user based on email address, if more than one return the oldest
		List<User> foundUsers = userList.stream().filter(User->User.getEmail().contains(email)).collect(Collectors.toList());
		User foundUser = null;
		if (foundUsers.size() >= 1) {
			foundUser = foundUsers.get(0);
			for (User currentUser : foundUsers)  {
				if (getUserAge(currentUser) > getUserAge(foundUser)) {
					foundUser = currentUser;
				}
			}
		}
	
		return foundUser;
	}
}
