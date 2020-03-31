# cov2words-backend

[![Build Status](https://travis-ci.com/cov2words/cov2words-backend.svg?branch=master)](https://travis-ci.com/cov2words/cov2words-backend)

## Prerequisites
- IntelliJ or Eclipse
  - Install/Use [Lombok Plugin](https://team.originstamp.com/confluence/x/5QCZ) 
- Apache Maven >= 3.x (included in IDE)
- OpenJDK 8

## Initialization

- Checkout repository via git
- In IntelliJ IDEA click `File > Open` and select repository's folder (**important**)
- Let IntelliJ do its magic to resolve Maven modules

## Configuration

- Configuration properties can be found in `resources/application.properties`
- `cov2words.languages`: comma separated list.
- `cov2words.word_length`: Determines the amount of words that are used for word pair generation.

## Deployment
Make sure you set the following environment variables before deploying.
- LETSENCRYPT_DOMAIN
- LETSENCRYPT_EMAIL

## Add new language

- Generate a file similar to `src/main/resources/wordlists/de.json`. Note that the file name is a 2 digit country code.
- Assuming you generate an `en.json`, do not forget to `en` to `cov2words.languages=de,en`

## Database migration

Database migration scripts can be found in `src/main/resources/db`

## Versioning Schema

We version our Java GitHub packages via git tags and use a semantic schema in the form:

**Major.Minor.Patch**

- **Major** when you make incompatible API changes,
- **Minor** when you add functionality in a backwards compatible manner, and
- **Patch** when you make backwards compatible bug fixes.

# Contributors

- Thomas Hepp

