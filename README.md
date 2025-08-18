# Getting Started
Starting to work with spring-ai and LLM, we quickly have to track our token usage.
It is easy to integrate spring-ai with prometheus and grafana (see video), but we often do
not need so much product to do that.

This library allows you to monitor your token usage with spring-ai. 
You have the ability to track your token usage:
 - in-memory: just to let you test it without any further action
 - with jdbc: token usage is stored permanently into a database<br/>Tested with h2, Postgres and MariaDB

This is the first version, So far, only the prompt feature is covered.

## Configuration

The presence of the library into the classpath activates it. By default, you have the in-memory tracking
system.

To activate the JDBC tracking, set the parameter **mgu.ai-supervision.mode** to **jdbc**, it implies you have 
correct database configuration in your application.

In JDBC you can ask the system to create the table if it does not exists. Set **mgu.ai-supervision.schema.create** 
to **true** activates the feature. Otherwise you will have the create it manually:
```sql

  create table if not exists TOKEN_TABLE (
    ai_model VARCHAR(255) NOT NULL UNIQUE,
    prompt_tokens INT,
    generation_tokens INT,
    total_tokens INT
  );
```

## exposed endpoint 
GET /supervision/tokens

Ce endpoint peut être modifié au travers de la variable mgu.ai-supervision.controller

DELETE /supervision/tokens
n appel sur le même endpoint avec le 
verbe DELETE supprime toutes les données.


## TODO
- See what happens if we create ChatClient beans for multi LLM providers
- Test with posgres
- test with image or make sure to tell 0.0.1-SNAPSHOT is only for prompt
  it will not work for images because there is a price per image which depends on size and so one
- Test it with ChatMemory
- 
## In-Memory 
ok, logger à mettre


    @Bean
    public TokenCounterService tokenCounterService() {
        return new InMemoryCounter();
    }

## Database

    @Bean
    public TokenCounterService jdbcCounterService(DataSource dataSource) {
        return new DataSourceCounter(dataSource);
    }

Le paramètre mgu.ai-supervision.schema.create avec la valeur true lance un script de création de la table.

Le script a été testé sur des bases h2, Postgres, MariaDB