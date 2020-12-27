package org.example.Bot.Translation;

import com.darkprograms.speech.translator.GoogleTranslate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
public class GoogleCrutchTranslator implements Translator {

	//нормальный доступ для нас в google cloud закрыт использую библиотеку, котоарая используется как костыль
	//выполняется get запрос в гугл переводчик для браузера и с ответа выдёргивается перевод
	//работает так себе с редкими языками, но с английским языком вроде всё в порядке
	@Override
	public String translate(String input) {
		StringBuilder result = new StringBuilder();

		try {
			result.append(GoogleTranslate.translate("ru", input));
			result.append("\nпереведено с ").append(GoogleTranslate.detectLanguage(input).toUpperCase(Locale.ROOT));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}
}
