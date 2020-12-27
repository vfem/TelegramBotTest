package org.example.Bot;

import org.example.Bot.Translation.Translator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class TestTelegramPingPongBot extends TelegramLongPollingBot {
	private String botName = System.getenv("botName");

	private String botToken = System.getenv("botToken");

	private static final String[] commandArr = {"/start", "/translate + text"};

	@Autowired
	@Qualifier("googleCrutchTranslator")
	Translator translator;

	@Override
	public void onUpdateReceived(Update update) {
		startAnswer(update);
		translatePhrase(update);
	}

	//todo добавить реальный перевод который будет кешироваться во встроенной nosql/sql БД какой-нибудь
	//те поиск по уже переведённым фразам из бд и если нет такого, то запрос в апи переводчика
	private void translatePhrase(Update update) {
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


	@Override
	public String getBotUsername() {
		return botName;
	}

	@Override
	public String getBotToken() {
		return botToken;
	}

}
