package org.example;

import org.example.db.TranslationEntity;
import org.example.db.TranslationsRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
public class AppTest {
	private static final Logger LOG = LoggerFactory.getLogger(AppTest.class);

	@Autowired
	private TranslationsRepository translationsRepository;

	@Before
	@Rollback(false)
	public void setUp() {
		LOG.info("*******************setUp*******************************");
		TranslationEntity translationEntity = new TranslationEntity("hi", "EN", "привет");
		LOG.info(translationEntity.toString());
		LOG.info("*******************setUp*******************************");
		translationsRepository.save(translationEntity);
	}

	@Test
	public void readTest() {
		LOG.info("*******************readTest*******************************");
		Iterable<TranslationEntity> allTranslations = translationsRepository.findAll();
		for (TranslationEntity entity : allTranslations) {
			LOG.info(entity.toString());
			Assert.assertEquals("hi", entity.getSource());
			Assert.assertEquals("EN", entity.getSourceLanguage());
			Assert.assertEquals("привет", entity.getRuTranslation());
			Assert.assertNotEquals("hi1", entity.getSource());
			Assert.assertNotEquals("EN1", entity.getSourceLanguage());
			Assert.assertNotEquals("привет1", entity.getRuTranslation());
		}
		LOG.info("*******************readTest*******************************");
	}
}
