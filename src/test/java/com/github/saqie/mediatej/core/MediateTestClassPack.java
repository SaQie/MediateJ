package com.github.saqie.mediatej.core;

import com.github.saqie.mediatej.api.Command;
import com.github.saqie.mediatej.api.CommandHandler;
import com.github.saqie.mediatej.api.CommandValidator;
import com.github.saqie.mediatej.api.ErrorBuilder;

class MediateTestClassPack {

    public static class TestErrorBuilder implements ErrorBuilder {

        @Override
        public void build() {

        }
    }

    public static class TestCommand implements Command {

    }

    public static class TestCommandHandler implements CommandHandler<TestCommand> {

        @Override
        public void handle(TestCommand command) {

        }
    }

    public static class TestCommandValidator implements CommandValidator<TestCommand, TestErrorBuilder> {

        @Override
        public void validate(TestCommand command, TestErrorBuilder errorBuilder) {

        }
    }

    public static class TestCommandHandlerSecond implements CommandHandler<TestCommand> {

        @Override
        public void handle(TestCommand command) {

        }
    }

    public static class TestCommandValidatorSecond implements CommandValidator<SecondTestCommand, TestErrorBuilder> {

        @Override
        public void validate(SecondTestCommand command, TestErrorBuilder errorBuilder) {

        }
    }


    public static class TestCommandHandlerWithoutParameters implements CommandHandler {

        @Override
        public void handle(Command command) {

        }
    }

    public static class TestCommandValidatorWithoutParameters implements CommandValidator {

        @Override
        public void validate(Command command, ErrorBuilder errorBuilder) {

        }
    }

    public static class TestCommandValidatorWithGenericParameters<T extends Command, R extends ErrorBuilder> implements CommandValidator<T, R> {

        @Override
        public void validate(T command, R errorBuilder) {

        }
    }

    public static class TestCommandValidatorWithWrongCommandParameter<T extends Command> implements CommandValidator<T, TestErrorBuilder> {

        @Override
        public void validate(T command, TestErrorBuilder errorBuilder) {

        }
    }

    public static class SecondTestCommand implements Command {

    }

    public static class SecondTestCommandHandler implements CommandHandler<SecondTestCommand> {

        @Override
        public void handle(SecondTestCommand command) {

        }
    }

    public static class ThirdTestCommand implements Command {

    }

    public static class ThirdTestCommandValidator implements CommandValidator<ThirdTestCommand, TestErrorBuilder> {

        @Override
        public void validate(ThirdTestCommand command, TestErrorBuilder errorBuilder) {

        }
    }


}
