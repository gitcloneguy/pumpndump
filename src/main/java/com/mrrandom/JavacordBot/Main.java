package com.mrrandom.JavacordBot;


import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.event.server.member.ServerMemberJoinEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import org.javacord.api.listener.server.member.ServerMemberJoinListener;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		DiscordApi api = new DiscordApiBuilder()  // logs in
			    //.setAccountType(AccountType.CLIENT) UNCOMMENT if you want this to be a selfbot, if commented out, it will be a regular bot.
			    .setToken(getToken())
			    .login().join();

				//bans scrubs with !ban
		api.addMessageCreateListener(event -> {
			if (event.getMessageContent().startsWith("!ban") && !event.getMessage().getMentionedUsers().isEmpty()) {
			int f = 0;
		banUser(api, event.getMessage().getMentionedUsers().get(f));
			event.getChannel().sendMessage("The user has been banned");
		}
	});
        //This listener does the busting
		api.addMessageCreateListener(new MessageCreateListener() {
			@Override
			public void onMessageCreate(MessageCreateEvent event) {
				if(event.isPrivateMessage()) {
					System.out.println("Recieved DM");
					try {
						User usr = event.getMessage().getAuthor().asUser().get();
						banUser(api, usr);
					} 
					catch(Exception e) {
						System.out.println("oOf. I tried, but i couldnt ban. Please check that i have the ban or admin perm.");
					} 
				}
			}
		});
		
		final double minutes = 2.0;
		final double threshold = 10;
		
		HashMap<Server, UserJoining> joinManagers = new HashMap<Server, UserJoining>();
		api.addServerMemberJoinListener(new ServerMemberJoinListener() {
			@Override
			public void onServerMemberJoin(ServerMemberJoinEvent event) {
				Server s = event.getServer();
				if(!joinManagers.containsKey(s))
					joinManagers.put(s, new UserJoining((long) (minutes*60*1_000_000_000L)));
				
				
				UserJoining joinManager = joinManagers.get(s);
				
				joinManager.addUser(event.getUser());
				if(joinManager.getNumberOfUsersInLeeway() > threshold)
					for(User u : joinManager.getUsersInLeeway())
						banUser(api, u);
			}
		});
		
        System.out.println("Logged in! Part of "+api.getServers().size()+" servers");
		api.getServerTextChannelById(getChannel()).ifPresent(channel -> {
			  channel.sendMessage("I'm logged in!");
		});
        
    }


	
	public static void banUser(DiscordApi api, User usr) {  //actually bans them everywhere + says it to bot feed
		System.out.println("Goodbye, "+usr.getDiscriminatedName());
		api.getServerTextChannelById(getChannel()).ifPresent(channel -> {
			  channel.sendMessage("Goodbye, " + usr.getDiscriminatedName());
		});
		
		for(Server s : api.getServers()) {
			s.banUser(usr);
			try {
				logToFile(usr, api);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void logToFile(User usr, DiscordApi api) throws IOException  {
		FileWriter fwrite = new FileWriter("banlog.txt");
		BufferedWriter bw = new BufferedWriter(fwrite);
		
		try {
			  bw.newLine();
		      fwrite.write("User banned: "+usr.getDiscriminatedName());
		      bw.newLine();
		      fwrite.close();
		     
		    } 
		      catch (IOException e) {
		      System.out.println("oOf. something oOOOfed.");
		      e.printStackTrace();
		    }
	}
	
	public static String getToken() throws FileNotFoundException { //throw here, should crash if no token
		 File fi = new File("token.txt"); // put your token in token.txt
	     Scanner sc = new Scanner(fi);
	     String token = sc.nextLine();
	     sc.close();
	     return token;
	}
	
	public static String getChannel() { //do not through
		 File fi = new File("channel.txt"); // put your channel ID in channel.txt
		     try{Scanner sc = new Scanner(fi);
		     String channel = sc.nextLine();
		     sc.close();
		     return channel;
	     }
	     catch(FileNotFoundException e) {
	    	 return "561762018663858182"; //some channel in a server the bot is in to avoid errors
	     }
	}
	
	
}
