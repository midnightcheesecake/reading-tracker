# 📚 Reading Tracker

A simple Spring Boot application to track and manage reading items. This project showcases clean architecture, testing practices, and a modern CI/CD setup using GitHub Actions.

## Features

- Add, update, and delete reading items (e.g. books, articles)
- RESTful API design
- Modular package structure by domain
- Automated testing and CI with GitHub Actions

## Tech Stack

- Java 21
- Spring Boot
- Maven
- JUnit & Mockito
- GitHub Actions (CI)
- Developed in IntelliJ IDEA (Windows)

## Getting Started

### Prerequisites

- Java 21+
- Maven

### Clone and Run

```bash
git clone https://github.com/midnightcheesecake/reading-tracker.git
cd reading-tracker
./mvnw spring-boot:run
```

### Run Tests

```bash
./mvnw test
```

## Tests and CI

Unit and integration tests are automatically triggered via GitHub Actions for each push to `main`. The build must succeed before changed can be merged.

## Project Structure

Organized by domain for better separation of concerns:

```pgsql
readingtracker/
  readingitem/
    api/
    service/
    storage/
  user/
    api/
    service/
    storage/
  configuration/
  exception/
```

## License

This project is licensed under the [MIT License](LICENSE).
