package com.example.exercise.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.exercise.model.AgeGroup;
import com.example.exercise.model.User;
import com.example.exercise.service.UserService;

@Controller
public class UserController {
	 
		/**
		 *  Default mapping for the page 
		 */
	    @RequestMapping(method = RequestMethod.GET, path="/")
	    public String mainPage() {
	    	return "mainPage";
	    }
	   
	    
	    /**
	     * Get request for /userList page 
	     * @param model
	     * @return
	     * @throws IOException
	     * @throws ParseException
	     * @throws ExecutionException 
	     */
	    @RequestMapping(method = RequestMethod.GET, path="/userList")
	    public String getUserList(final Model model) throws IOException, ParseException, ExecutionException{
	    	try {
	    		final List<User> users = UserService.loadUser();
				model.addAttribute("users", users);
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	return "userList";
	    }
	    
	    /**
	     * Get request for /sortedUserList page
	     * @param model
	     * @return
	     * @throws IOException
	     * @throws ParseException
	     */
	    @RequestMapping(method = RequestMethod.GET, path="/sortedUserlist")
	    public String getSortedUserList(final Model model) throws IOException, ParseException{
	    	try {
	    		final List<User> users = UserService.loadSortedUser();
				model.addAttribute("users", users);
			} catch (JSONException e) {
				e.printStackTrace();
			}
	    	return "userList";
	    }
	    
	    /**
	     * Get request for /checkUser 
	     * @param email
	     * @param model
	     * @return
	     * @throws JSONException
	     * @throws IOException
	     * @throws ParseException
	     */
	    @RequestMapping(method = RequestMethod.GET, path="/checkUser")
	    public String checkUser(@RequestParam(value="email") String email, final Model model) throws JSONException, IOException, ParseException {
	    	if(!email.contains("@")) {
	    		model.addAttribute("error", "This is not a valid email address");
	    	}else {
		    	final User user = UserService.getUserByEmail(email);
		    	model.addAttribute("error", null);
		    	model.addAttribute("user", user);
	    	}
	    	return "user";
	    }
	    
	    @RequestMapping(method = RequestMethod.GET, path="/ageBasedGroup")
	    public String ageBasedGrouping(final Model model) {
	    	final Map<AgeGroup, Integer> userCountByAgeGroup = UserService.getUserCountByAgeGroup();
	    	for(final Map.Entry<AgeGroup, Integer> entry: userCountByAgeGroup.entrySet()) {
	    		model.addAttribute(entry.getKey().name(), entry.getValue());
	    	}
	    	return "userGroup";
	    }
}
