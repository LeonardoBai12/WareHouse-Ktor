# WareHouse-Ktor

WareHouse is a ware management Ktor server.

## Description

Warehouse offers the following features:
* Create, read, update and delete users.
* Create, read, update and delete wares.
* Deposit and withdraw wares, automatically updating their stocks.

## Technologies

The application is built using the following technologies:

* [Ktor](https://ktor.io) for asynchronous server application.
* [JaCoCo](https://github.com/jacoco/jacoco) for unit test coverage validation and report.
* [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) for asynchronous programming.
* [PostgreSQL](https://www.postgresql.org) for data management.
* [JUnit 5](https://junit.org/junit5/docs/current/user-guide) for unit testing.
* [MockK](https://mockk.io) for mocking objects in unit tests.
* [GitHub Actions](https://docs.github.com/pt/actions/learn-github-actions) for Continuous Integration/Continuous Deployment (CI/CD).
* [Dokka](https://github.com/Kotlin/dokka) for generating documentation.

## Quality Assurance

To ensure high-quality code, the following tools and processes are used before merging any pull requests:

* [Ktint](https://pinterest.github.io/ktlint/) is used to enforce code style guidelines.
* All unit tests are run to ensure code functionality and quality.
* Ensure unit test coverage never decreases by JaCoCo validation.

This process helps maintain code consistency and quality throughout the project.

# Current Test Coverage: 85%

## API Documentation

You can access the documentation of this API by [this link](https://documenter.getpostman.com/view/28162587/2sA3JGeihC).

## KDoc Documentation

The documentation is automatically generated and published for every push to the main branch.\
To access the documentation, download the _WareHouse-KDoc-Documentation_ file from the [Documentation](https://github.com/LeonardoBai12/WareHouse-Ktor/actions/workflows/documentation_workflow.yml) action artifacts.

## Coverage Report

A unit test coverage report is generated and published for every push to the main branch.\
To access the test coverage report, download the _WareHouse-Coverage-Report_ file from the [Coverage Report](https://github.com/LeonardoBai12/WareHouse-Ktor/actions/workflows/coverage_report_worflow.yml) action artifacts.
