package co.com.screenplay.project.exceptions;

public class ExcInvalidArgument extends IllegalArgumentException {

    public ExcInvalidArgument() {
        super();
    }

    public ExcInvalidArgument(String message) {
        super(message);
    }

    public ExcInvalidArgument(String message, Throwable cause) {
        super(message, cause);
    }

}
