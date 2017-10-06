package com.example.exercise.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import com.example.exercise.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
public class DateGenerator {
	private static final String lexiconUpr = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String lexiconLow = "abcdefghijklmnopqrstuvwxyz";
	private static final String lexiconNum = "0123456789";
	private static final Set<String> identifiers = new HashSet<String>();
	private static final java.util.Random rand = new java.util.Random();
	private static final List<String> towns = new ArrayList<String>(
			Arrays.asList("Lausanne", "Renens", "Ecublens", "Prilly", "Malley", "Cully", "Saint Sphorine", "Lutry"));
	
	
	public static void main(String [] args) throws IOException {
		List<User> userList = new ArrayList<User>();
		final ObjectMapper mapper = new ObjectMapper();
		for (int i = 0; i<100; i++) {
			final User u = getUser();
			userList.add(u);
		}
		mapper.writeValue(new File("user.json"), userList);
		final String userJson = mapper.writeValueAsString(userList);
		System.out.println(userJson);
	}
	
	private static User getUser() {
		final User user = new User();
		user.setId(UUID.randomUUID().toString());
		user.setFirstname(randomIdentifier(lexiconUpr));
		user.setLastname(randomIdentifier(lexiconUpr));
		user.setEmail(randomIdentifier(lexiconLow)+ "@gmail.com");
		user.setMobile(randomIdentifier(lexiconNum));
		user.setDateOfBirth(getRandomDate());
		user.setTown(towns.get(rand.nextInt(8)));
		return user;
	}
	private static Date getRandomDate() {
		final Calendar cal = Calendar.getInstance();
		int year = rand.nextInt(15)+ 1985;
		int date = rand.nextInt(25) + 1;
		int month = rand.nextInt(11) + 1;
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DATE, date);
		return cal.getTime();
	}
	
	
	public static String randomIdentifier(final String lexicon) {
	    StringBuilder builder = new StringBuilder();
	    while(builder.toString().length() == 0) {
	        int length = rand.nextInt(5)+5;
	        for(int i = 0; i < length; i++) {
	            builder.append(lexicon.charAt(rand.nextInt(lexicon.length())));
	        }
	        if(identifiers.contains(builder.toString())) {
	            builder = new StringBuilder();
	        }
	    }
	    return builder.toString();
	}
}
