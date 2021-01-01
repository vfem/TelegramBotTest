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
		String result;
		try {
			TranslationEntity translationEntity = translationsRepository.findBySourceIgnoreCase(input);
			String sourceLanguage;
			String translation;
			if (translationEntity == null) {
				sourceLanguage = GoogleTranslate.detectLanguage(input).toUpperCase(Locale.ROOT);
				translation = GoogleTranslate.translate("ru", input);
				translationsRepository.save(new TranslationEntity(input, sourceLanguage, translation));
			} else {
				sourceLanguage = translationEntity.getSourceLanguage();
				translation = translationEntity.getRuTranslation();
			}
			result = formPrettyResultString(sourceLanguage, translation);
		} catch (IOException e) {
			e.printStackTrace();
			result = "Ошибка перевода, попробуйте снова";
		}

		return result;
	}

	@Autowired
	public void setTranslationsRepository(TranslationsRepository translationsRepository) {
		this.translationsRepository = translationsRepository;
	}
}
