package com.mrrandom.JavacordBot;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

public class Main {
	public static void main(String[] args) throws FileNotFoundException {
		Scanner tokenScan = new Scanner(new File("token.txt"));
		String token = tokenScan.next(); tokenScan.close();
		//logging in takes a while
		DiscordApi api = new DiscordApiBuilder()
			    //.setAccountType(AccountType.CLIENT) UNCOMMENT if not selfbot
			    .setToken(token) //token of "Buster" the trivia buster. 
			    .login().join();
		
        //This listener does the busting
		api.addMessageCreateListener(new MessageCreateListener() {
					@Override
					public void onMessageCreate(MessageCreateEvent event) {
						if(event.isPrivateMessage()) {
							System.out.println("Recieved DM");
						}
					}
				});
        System.out.println("Logged in!");
        //api.updateStatus();
    }
}
