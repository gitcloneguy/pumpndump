package com.mrrandom.JavacordBot;

import java.util.ArrayList;

import org.javacord.api.entity.user.User;

public class UserJoining {
	private ArrayList<UserTimestamp> users;
	private long leeway;
	public UserJoining() {
		users = new ArrayList<UserTimestamp>();
		leeway = 40*1_000_000_000L; //40 seconds in nanoseconds
	}
	public UserJoining(long leeway) {
		this();
		this.leeway = leeway;
	}
	
	public void addUser(User user) {
		addUser(user, System.nanoTime());
	}
	public void addUser(User user, long timestamp) {
		users.add(new UserTimestamp(user, timestamp));
		cullUsers();
	}
	
	
	public User[] getUsersInLeeway(){
		cullUsers();
		User[] usrs = new User[users.size()];
		for(int i = 0; i < users.size(); i++)
			usrs[i] = users.get(i).getUser();
		
		return usrs;
	}
	
	public int getNumberOfUsersInLeeway() {
		cullUsers();
		return users.size();
	}
	
	public void cullUsers() {
		final long time = System.nanoTime();
		for(int i = users.size()-1; i >= 0; i--)
			if(time - users.get(i).getTimestamp() <= leeway)
				users.remove(i);
	}
	
	private class UserTimestamp {
		private final User user;
		private final long timestamp;
		public UserTimestamp(User usr) {
			user = usr;
			timestamp = System.nanoTime();
		}
		public UserTimestamp(User usr, long time) {
			user = usr;
			timestamp = time
		}
		public User getUser() {
			return user;
		}
		public long getTimestamp() {
			return timestamp;
		}
	}
}
