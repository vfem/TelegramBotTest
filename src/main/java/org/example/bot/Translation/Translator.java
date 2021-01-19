package org.example.bot.Translation;

public interface Translator {
	String translate(String input);

	default String formPrettyResultString(String sourceLanguage, String translation) {
		return translation +
				"\nПереведено с " + sourceLanguage;
	}
}
