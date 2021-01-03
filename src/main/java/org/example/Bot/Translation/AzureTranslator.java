package org.example.Bot.Translation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.*;
import org.apache.commons.lang3.StringUtils;
import org.example.db.TranslationEntity;
import org.example.db.TranslationsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

@Component
public class AzureTranslator implements Translator {
	/*  Configure the local environment:
	 * Set the TRANSLATOR_TEXT_SUBSCRIPTION_KEY and TRANSLATOR_TEXT_ENDPOINT environment
	 * variables on your local machine using the appropriate method for your
	 * preferred shell (Bash, PowerShell, Command Prompt, etc.).
	 *
	 * For TRANSLATOR_TEXT_ENDPOINT, use the same region you used to get your
	 * subscription keys.
	 *
	 * If the environment variable is created after the application is launched
	 * in a console or with Visual Studio, the shell (or Visual Studio) needs to be
	 * closed and reloaded to take the environment variable into account.
	 */
	private static final String subscriptionKey = System.getenv("TRANSLATOR_TEXT_SUBSCRIPTION_KEY");
	private static final String endpoint = System.getenv("TRANSLATOR_TEXT_ENDPOINT");
	private final String url = endpoint + "/translate?api-version=3.0&to=ru";

	// Instantiates the OkHttpClient.
	private final OkHttpClient client = new OkHttpClient();

	private TranslationsRepository translationsRepository;

	// This function performs a POST request.
	public String PostString(String input) throws IOException {
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType,
				"[{\n\t\"Text\": \"" + input + "\"\n}]");
		Request request = new Request.Builder()
				.url(url).post(body)
				.addHeader("Ocp-Apim-Subscription-Key", subscriptionKey)
				.addHeader("Content-type", "application/json").build();
		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	@Override
	public String translate(String input) {
		String result = "";
		AzureTranslator translateRequest = new AzureTranslator();
		try {
			TranslationEntity translationEntity = translationsRepository.findBySourceIgnoreCase(input);
			String sourceLanguage;
			String translation;
			if (translationEntity == null) {
				//получаем респонс от Azure
				String response = translateRequest.PostString(input);
				//объясняем gson в какой тип десериализировать полученный ответ, массив json
				Type type = new TypeToken<List<TranslationAzureResponse>>(){}.getType();
				List<TranslationAzureResponse> translationAzureResponseList = new Gson().fromJson(response, type);
				//перебираем десериализированные ответы и склеиваем ответ
				for (TranslationAzureResponse translationAzureResponse : translationAzureResponseList) {
					TranslationAzureResponse.DetectedLanguage detectedLanguage = translationAzureResponse.getDetectedLanguage();
					sourceLanguage = detectedLanguage.getLanguage();
					List<TranslationAzureResponse.InnerTranslation> innerTranslationList = translationAzureResponse.getTranslations();
					for (TranslationAzureResponse.InnerTranslation innerTranslation : innerTranslationList) {
						translation = innerTranslation.getText();
						if (StringUtils.isEmpty(result)) {
							result = formPrettyResultString(sourceLanguage.toUpperCase(Locale.ROOT), translation);
						} else {
							result = result + "\n" + formPrettyResultString(sourceLanguage.toUpperCase(Locale.ROOT), translation);
						}
						translationsRepository.save(new TranslationEntity(input, sourceLanguage, translation));
					}
				}
			} else {
				sourceLanguage = translationEntity.getSourceLanguage();
				translation = translationEntity.getRuTranslation();
				result = formPrettyResultString(sourceLanguage.toUpperCase(Locale.ROOT), translation);
			}
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

	private static class TranslationAzureResponse {
		public TranslationAzureResponse.DetectedLanguage detectedLanguage;
		public List<TranslationAzureResponse.InnerTranslation> translations;

		public TranslationAzureResponse.DetectedLanguage getDetectedLanguage() {
			return detectedLanguage;
		}

		public void setDetectedLanguage(TranslationAzureResponse.DetectedLanguage detectedLanguage) {
			this.detectedLanguage = detectedLanguage;
		}

		public List<TranslationAzureResponse.InnerTranslation> getTranslations() {
			return translations;
		}

		public void setTranslations(List<TranslationAzureResponse.InnerTranslation> translations) {
			this.translations = translations;
		}

		private static class DetectedLanguage {
			public String language;
			public double score;

			public String getLanguage() {
				return language;
			}

			public void setLanguage(String language) {
				this.language = language;
			}

			public double getScore() {
				return score;
			}

			public void setScore(double score) {
				this.score = score;
			}
		}

		private static class InnerTranslation {
			public String getText() {
				return text;
			}

			public void setText(String text) {
				this.text = text;
			}

			public String getTo() {
				return to;
			}

			public void setTo(String to) {
				this.to = to;
			}

			public String text;
			public String to;
		}
	}
}
