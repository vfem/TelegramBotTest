package org.example.dao;

import org.springframework.data.repository.CrudRepository;

public interface TranslationsRepository extends CrudRepository<TranslationEntity, Long> {

	TranslationEntity findBySourceIgnoreCase(String source);
}
