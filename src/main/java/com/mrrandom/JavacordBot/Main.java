package com.mrrandom.JavacordBot;


import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.io.IOException;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {

	
		
		DiscordApi api = new DiscordApiBuilder()  // logs in
			    //.setAccountType(AccountType.CLIENT) UNCOMMENT if you want this to be a selfbot, if commented, it will be a regular bot.
			    .setToken("NzAyNjc5Nzg4Mjc5ODI0Mzk1.XqD1Rg.8O_UcnP5dNV5dU_M2sUKroQhvu4") 
			    .login().join();
		
		
        //This listener does the busting
		api.addMessageCreateListener(new MessageCreateListener() {
			@Override
			public void onMessageCreate(MessageCreateEvent event) {
				if(event.isPrivateMessage()) {
					System.out.println("Recieved DM");
					try {
						User usr = event.getMessage().getAuthor().asUser().get();
						banUser(api, usr);
					} catch(NoSuchElementException e) {
						System.out.println("oOf. I tried, but i couldnt ban. Please check that i have the ban or admin perm.");
					}
				}
			}
		});
		
        System.out.println("Logged in! Part of "+api.getServers().size()+" servers");
        System.out.println("Invite link is: "+api.createBotInvite());
        
    }
	
	
	public static void banUser(DiscordApi api, User usr) {  //actually bans them everywhare + says it to bot feed
		System.out.println("Goodbye, "+usr.getDiscriminatedName());
		api.getServerTextChannelById(617028579611377674L).ifPresent(channel -> {
			  channel.sendMessage("Goodbye, " + usr.getDiscriminatedName()+":honey_pot:");
			});
		for(Server s : api.getServers()) {
			s.banUser(usr);
			try {
				logToFile(usr, api);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void logToFile(User usr, DiscordApi api) throws IOException  {
		
		FileWriter fwrite = new FileWriter("banlog.txt");
		BufferedWriter bw = new BufferedWriter(fwrite);
		
		try {
		      fwrite.write("User banned: "+usr.getDiscriminatedName());
		      bw.newLine();
		      fwrite.close();
		     
		    } catch (IOException e) {
		      System.out.println("oOf. something oOOOfed.");
		      e.printStackTrace();
		    }
	}
	
}
