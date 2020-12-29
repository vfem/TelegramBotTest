package org.example.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TranslationEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long translationId;

	private String source;

	private String sourceLanguage;

	private String ruTranslation;

	public Long getTranslationId() {
		return translationId;
	}

	public String getSource() {
		return source;
	}

	public String getSourceLanguage() {
		return sourceLanguage;
	}

	public String getRuTranslation() {
		return ruTranslation;
	}

	@Override
	public String toString() {
		return "Translation{" +
				"translationId=" + translationId +
				", source='" + source + '\'' +
				", sourceLanguage='" + sourceLanguage + '\'' +
				", ruTranslation='" + ruTranslation + '\'' +
				'}';
	}

	public TranslationEntity() {
	}

	public TranslationEntity(String source, String sourceLanguage, String ruTranslation) {
		this.source = source;
		this.sourceLanguage = sourceLanguage;
		this.ruTranslation = ruTranslation;
	}
}
