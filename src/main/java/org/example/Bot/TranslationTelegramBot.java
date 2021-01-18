package org.example.Bot;

import org.example.Bot.Translation.Translator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TranslationTelegramBot extends TelegramLongPollingBot {
	private static Logger log = LoggerFactory.getLogger(TranslationTelegramBot.class);
	private final String botName = System.getenv("botName");

	private final String botToken = System.getenv("botToken");

	private static final String[] commandArr = {"/start", "/translate + text"};

	private Translator translator;

	@Override
	public void onUpdateReceived(Update update) {
		ExecutorService executorService = Executors.newCachedThreadPool();
		executorService.execute(() -> {
			startAnswer(update);
			translatePhrase(update);
		});
	}

	private void translatePhrase(Update update) {
		log.info(Thread.currentThread().getName());
		if (update.getMessage().getText().startsWith("/translate ")) {
			String textToTranslate = update.getMessage().getText().split("/translate ")[1];
			SendMessage message = new SendMessage();
			message.setChatId(String.valueOf(update.getMessage().getChatId()));
			String translationText = translator.translate(textToTranslate);
			message.setText(translationText);
			try {
				execute(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		} else if ("/translate".equals(update.getMessage().getText())) {
			SendMessage message = new SendMessage();
			message.setChatId(String.valueOf(update.getMessage().getChatId()));
			message.setText("Пожалуйста используйте /translate + text");
			try {
				execute(message);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}
	}

	private void startAnswer(Update update) {
		log.info(Thread.currentThread().getName());
		if ("/start".equals(update.getMessage().getText())) {
			SendMessage message = new SendMessage();
			message.setChatId(String.valueOf(update.getMessage().getChatId()));
			message.setText("Hi! I'm Translation Bot\nYou can use following set of commands:");
			try {
				execute(message);
				for (String command : commandArr) {
					SendMessage messageCmd = new SendMessage();
					messageCmd.setChatId(String.valueOf(update.getMessage().getChatId()));
					messageCmd.setText(command);
					try {
						execute(messageCmd);
					} catch (TelegramApiException e) {
						e.printStackTrace();
					}
				}
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		}

	}

	@Autowired
	@Qualifier("googleCrutchTranslator")
	public void setTranslator(Translator translator) {
		this.translator = translator;
	}

	@Override
	public String getBotUsername() {
		return botName;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

}
