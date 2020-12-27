package org.example.Bot.Translation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.*;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Locale;

//todo добавить поддержку яндекс переводчика этот слабый очень
@Component
public class AzureTranslator implements Translator{
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
    private static String subscriptionKey = System.getenv("TRANSLATOR_TEXT_SUBSCRIPTION_KEY");
    private static String endpoint = System.getenv("TRANSLATOR_TEXT_ENDPOINT");
    String url = endpoint + "/translate?api-version=3.0&to=ru";

    // Instantiates the OkHttpClient.
    OkHttpClient client = new OkHttpClient();

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
        StringBuilder result = new StringBuilder();
        AzureTranslator translateRequest = new AzureTranslator();
        try {
            //получаем респонс от Azure
            String response = translateRequest.PostString(input);
            //объясняем gson в какой тип десериализировать полученный ответ, массив json
            Type type = new TypeToken<List<Translation>>(){}.getType();
            List<Translation> translationList = new Gson().fromJson(response, type);
            //перебираем десериализированные ответы и склеиваем ответ
            for (Translation translation : translationList) {
                Translation.DetectedLanguage detectedLanguage = translation.getDetectedLanguage();
                String sourceLanguage = detectedLanguage.getLanguage();
                List<Translation.InnerTranslation> innerTranslationList = translation.getTranslations();
                for (Translation.InnerTranslation innerTranslation : innerTranslationList) {
                    if (StringUtils.isEmpty(result.toString())) {
                        result.append(innerTranslation.getText()).append("\n\nПереведено с ").append(sourceLanguage.toUpperCase(Locale.ROOT));
                    } else {
                        result.append("\n").append(innerTranslation.getText()).append("\n\nПереведено с ").append(sourceLanguage.toUpperCase(Locale.ROOT));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
