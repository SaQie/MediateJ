
# MediateJ

Lightweight Java Command-Handler library


## Tech Stack

* Java
* JUnit


## Usage/Examples

* ***Command***


*Command is the basic interface for all Command classes. For example:*

```java
import com.github.saqie.mediatej.api.Command;

public class GithubCommand implements Command {

    private String url;

}
```

* ***CommandHandler***


*Command handler is our handler for a specific command; the handler will be executed when we send the proper command to MediateJ. For example:*


```java
import com.github.saqie.mediatej.api.CommandHandler;

public class GithubCommandHandler implements CommandHandler<GithubCommand> {
    
    @Override
    public void handle(GithubCommand command) {
        System.out.println("Github command handled !");
    }
}

```

* ***CommandValidator***

*Command validator is an optional class that will be executed before handling the proper command handler. To use CommandValidator, you have to define your own Validator that will implement the ErrorBuilder interface. For example:*

```java
import com.github.saqie.mediatej.api.ErrorBuilder;

public class MyValidator implements ErrorBuilder {

    // List of errors
    private final List<String> errors = new ArrayList<>();

    @Override
    public void build() {
        // Implement this method whatever you want
        // This method is always called if there is a CommandValidator for the command.
        if (!errors.isEmpty()){
            // Throw errors which are collected in errors list
            throw new RuntimeException();
        }
    }
}
```

*Then you have to register your own ErrorBuilder in **MediateConfigurer**, see below.*

*Once you have registered your own ErrorBuilder, you can define your CommandValidator for each command. For example:*

```java
import com.github.saqie.mediatej.api.CommandValidator;

public class GithubCommandValidator implements CommandValidator<GithubCommand, MyValidator> {

    @Override
    public void validate(GithubCommand command, MyValidator validator) {
        // Validate command here
    }
}
```

* ***MediateConfigurer***

*Mediate configurer is responsible for registering all our commands, command handlers, command validators, and Validator. For example:*

```java
        MediateJ mediateJ = new MediateConfigurer()
                .registerErrorBuilder(new MyValidator()) // Register validator
                .register(new GithubCommandHandler(), new GithubCommandValidator()) // Register handler with validator
                .register(new GithubCommandWithoutValidator()) // Register handler without validator
                .build(); // Build MediateJ that will allow us to send commands
```




* ***MediateCoreConfigurer***

*Mediate core configurer is responsible for defining the behaviour of the Validator and the behaviour of the application when two handlers are defined for one command. For example:*

```java
        MediateJ mediateJ = new MediateCoreConfigurer()
                .errorBuilderInstanceMode(ErrorBuilderInstanceMode.PER_SEND)
                .handlerConflictMode(HandlerConflictMode.THROW_EXCEPTION)
                .build() // Returns MediateConfigurer
                .register(new GithubCommandHandler())
                .build();
```

*ErrorBuilderInstanceMode:*
- PER_SEND -> *Means that every call to MediateJ creates a new instance of our Validator*
- ONE -> *Means that we will always have one instance of our Validator*

*HandlerConflictMode:*

- THROW_EXCEPTION - *Means that if we define more than one handler for one Command, MediateJ will throw an exception about that.*
- OVERRIDE - *Means that if we define more than one handler for one Command, MediateJ will override the command handler.*

*Remember that the .build() method from MediateCoreConfigurer returns MediateConfigurer. To get the MediateJ interface that allows you to send commands, use .build() on MediateConfigurer*


* ***MediateJ***

*MediateJ is an interface that allows us to send commands. For example:*

```java
mediateJ.send(new FirstCommand("Test"));
```


**Spring Boot** 

*We can use MediateJ with Spring; all we need to do is to make sure that we mark our handler and validator class as a spring bean (e.g., @Service, @Component) and then we can create our configuration class for MediateJ:*

```java
@Configuration
public class MediateJConfiguration {

    @Bean
    public <T extends Command, B extends ErrorBuilder> MediateJ mediateJ(List<CommandHandler<T>> commandHandlerList, List<CommandValidator<T, B>> validators) {
        return new MediateCoreConfigurer()
                .handlerConflictMode(HandlerConflictMode.OVERRIDE)
                .errorBuilderInstanceMode(ErrorBuilderInstanceMode.PER_SEND)
                .build()
                .registerErrorBuilder(new MyValidator())
                .register(commandHandlerList, validators)
                .build();
    }

}
```

*And then we can use our MediateJ whenever we want, for example:*

```java

@Controller
@AllArgsConstructor
public class Test {

    private final MediateJ mediateJ;

    @EventListener
    public void test(ApplicationReadyEvent event) {
        mediateJ.send(new FirstCommand());
    }

}


```



## Installation

You can use my MediateJ library with Maven:

```maven
  <dependency>
    <groupId>io.github.saqie</groupId>
    <artifactId>MediateJ</artifactId>
    <version>1.1</version>
  </dependency>
```
    
