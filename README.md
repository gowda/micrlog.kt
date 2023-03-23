# Microlog

Implementation of [sample app](https://github.com/learnenough/rails_tutorial_sample_app_7th_ed) from [Rails tutorial](https://www.railstutorial.org/) using spring-boot with kotlin.

[![Tests](https://github.com/gowda/micrlog.kt/actions/workflows/test.yml/badge.svg)](https://github.com/gowda/micrlog.kt/actions/workflows/test.yml)

### Build

Setup `.env` file by setting up values for reference variables in `.env.example`.

```bash
./gradlew bootJar
```

### Run migration
```bash
./gradlew update
```

NOTE: migration will fail if `DB_PASSWORD` is blank.

### Run application
```bash
./gradlew bootRun
```

### Test
```bash
./gradlew test
```
