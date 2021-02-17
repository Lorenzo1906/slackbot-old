package com.example.slackbot;

import com.example.slackbot.bot.EchoBot;
import com.microsoft.bot.builder.Bot;
import com.microsoft.bot.integration.AdapterWithErrorHandler;
import com.microsoft.bot.integration.BotFrameworkHttpAdapter;
import com.microsoft.bot.integration.Configuration;
import com.microsoft.bot.integration.spring.BotController;
import com.microsoft.bot.integration.spring.BotDependencyConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({BotController.class})
public class SlackbotApplication extends BotDependencyConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(SlackbotApplication.class, args);
	}

	@Bean
	public Bot getBot() {
		return new EchoBot();
	}

	@Override
	public BotFrameworkHttpAdapter getBotFrameworkHttpAdaptor(Configuration configuration) {
		return new AdapterWithErrorHandler(configuration);
	}
}
