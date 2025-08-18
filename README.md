# Getting Started
Starting to work with spring-ai and LLM, we quickly have to track our token usage.
It is easy to integrate spring-ai with prometheus and grafana (see [video](https://www.youtube.com/watch?v=pBVKkcBhw6I)). 
Most of the time, we don't need so much product to do that.

This library allows you to monitor your token usage with spring-ai. 
You have the ability to track your token usage:
 - **in-memory**: just to let you test it without any further action
 - with **jdbc**: token usage is stored permanently into a database<br/>Tested with h2, Postgres and MariaDB

This is the first version, So far, only the prompt feature is covered.

## How to do
Clone this repo.

Build the library mvn clean install

Add the dependency into your spring-ai application.
```xml
    <dependency>
        <groupId>dev.mgu</groupId>
        <artifactId>spring-boot-starter-ai-supervision</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <scope>compile</scope>
    </dependency>
```

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
**GET /supervision/tokens** returns the token counters

**DELETE /supervision/tokens** resets all counters.

The value of the exposed endpoint can be customized by the variable **mgu.ai-supervision.controller**

## Additional information

This library works if you inject a ChatClient.Builder bean. It also works when you work with multiple LLM providers
and manually define your ChatClient beans (see Medium [article](https://medium.com/@marc.guerrini/springboot-ai-two-ai-models-64dbbbe88cb7)).