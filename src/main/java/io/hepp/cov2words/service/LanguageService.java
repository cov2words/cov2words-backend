package io.hepp.cov2words.service;

import io.hepp.cov2words.common.dto.LanguageResponseDTO;
import io.hepp.cov2words.common.exceptions.language.UnknownLanguageException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service that is used for managing the available languages.
 *
 * @author Thomas Hepp, thomas@hepp.io
 */
@Slf4j(topic = "LanguageService")
@Service
public class LanguageService {

    @Getter
    private final List<String> languages;

    @Autowired
    public LanguageService(@Value("${cov2words.languages}") List<String> languages) {
        this.languages = languages;
    }

    /**
     * Returns available languages.
     */
    public LanguageResponseDTO getAvailableLanguages() {
        log.debug("Getting available languages");
        LanguageResponseDTO languages = new LanguageResponseDTO(new ArrayList<>());
        this.getLanguages().forEach(language -> languages.getLanguageCodes().add(language));
        return languages;
    }

    /**
     * Validates a given input language. Throws an exception if parameter is incorrect.
     */
    public void validateLanguage(String language) throws UnknownLanguageException {
        this.getLanguages()
                .stream()
                .filter(element -> element.equals(language))
                .findFirst()
                .orElseThrow(UnknownLanguageException::new);
    }
}
