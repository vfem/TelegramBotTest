package org.example.db;

import org.springframework.data.repository.CrudRepository;

public interface TranslationsRepository extends CrudRepository<TranslationEntity, Long> {

	TranslationEntity findBySourceIgnoreCase(String source);

}
