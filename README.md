# Getting Started
When working with **Spring AI** and LLMs, monitoring **token usage** quickly becomes essential.
While it is possible to integrate Spring AI with Prometheus and Grafana (see this video), such setups can often feel too heavy for simple experiments.

This starter library provides a lightweight way to **monitor token usage in Spring AI applications**.

It supports two modes of operation:

 - **In-memory** → ideal for testing without additional setup
 - **JDBC** → persists token usage in a relational database (tested with H2, PostgreSQL, and MariaDB)

⚠️ This is the first release. Currently, only the prompt feature is covered.

Additionally, an **INFO** log entry is generated after each AI request to provide visibility into request usage.

```shell
2025-08-19T16:51:44.079+02:00  INFO 72800 --- [spring-ai] [nio-8080-exec-3] d.m.a.s.impl.TokenCountingAdvisor        : Consumed token: TokenCounter[model=gpt-4o-2024-08-06, promptTokens=61, generationTokens=21, totalTokens=82]
```

## Installation
Clone this repository and build the library:
```shell
mvn clean install
```

Add the dependency into your Spring AI application.
```xml
    <dependency>
        <groupId>dev.mgu</groupId>
        <artifactId>spring-boot-starter-ai-supervision</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <scope>compile</scope>
    </dependency>
```

## Configuration

The library is auto-configured when present on the classpath.
By default, token usage is tracked in memory.

To enable JDBC-based tracking, configure the following property:

```yaml
mgu.ai-supervision.mode: jdbc
```
This requires a valid database configuration in your application.

### Schema Management

You can choose to let the library create the schema automatically:
```yaml
mgu.ai-supervision.schema.create: true
```

Alternatively, create the table manually:

```sql
CREATE TABLE IF NOT EXISTS TOKEN_TABLE (
    ai_model VARCHAR(255) NOT NULL UNIQUE,
    prompt_tokens INT,
    generation_tokens INT,
    total_tokens INT
);
```

## Endpoints
The library exposes the following REST endpoints:

 - GET /supervision/tokens → returns token counters
 - DELETE /supervision/tokens → resets all counters

The base path can be customized via:

```yaml
mgu.ai-supervision.controller: /my/custom/path
```

## Additional information

 - The library works out-of-the-box if you inject a ChatClient.Builder bean.
 - It is compatible with multiple LLM providers and custom ChatClient beans (see this [Medium article](https://medium.com/@marc.guerrini/springboot-ai-two-ai-models-64dbbbe88cb7)).
