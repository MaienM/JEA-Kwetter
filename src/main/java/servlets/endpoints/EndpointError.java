package servlets.endpoints;

public class EndpointError {
    public final int status;
    public final String message;

    public EndpointError(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
