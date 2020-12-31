package org.example.Bot.Translation;

import com.darkprograms.speech.translator.GoogleTranslate;
import org.example.db.TranslationEntity;
import org.example.db.TranslationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Locale;

@Component
public class GoogleCrutchTranslator implements Translator {
	private TranslationsRepository translationsRepository;

	//нормальный доступ для нас в google cloud закрыт использую библиотеку, котоарая используется как костыль
	//выполняется get запрос в гугл переводчик для браузера и с ответа выдёргивается перевод
	//работает так себе с редкими языками, но с английским языком вроде всё в порядке
	@Override
	public String translate(String input) {
		StringBuilder result = new StringBuilder();

		try {
			TranslationEntity translationEntity = translationsRepository.findBySourceIgnoreCase(input);

			if (translationEntity == null) {
				String sourceLanguage = GoogleTranslate.detectLanguage(input).toUpperCase(Locale.ROOT);
				String translation = GoogleTranslate.translate("ru", input);
				translationsRepository.save(new TranslationEntity(input, sourceLanguage, translation));

				result.append(translation);
				result.append("\nпереведено с ").append(sourceLanguage);
				result.append("\nсохранено в БД для кеширования");
			} else {
				result.append(translationEntity.getRuTranslation());
				result.append("\nпереведено с ").append(translationEntity.getSourceLanguage());
				result.append("\nнайдено в БД, запрос в API не выполнялся");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result.toString();
	}

	@Autowired
	public void setTranslationsRepository(TranslationsRepository translationsRepository) {
		this.translationsRepository = translationsRepository;
	}
}
