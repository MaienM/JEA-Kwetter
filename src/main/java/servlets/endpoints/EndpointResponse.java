package servlets.endpoints;

public class EndpointResponse {
    public enum Status {
        OK(200),
        CREATED(201),
        NO_CONTENT(204),
        INVALID(400),
        NOT_FOUND(404),
        IM_A_TEAPOT(418),
        INTERNAL_SERVER_ERROR(500);

        private int code;

        Status(int code) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }
    }

    private Status status;
    private Object data;

    public EndpointResponse(Status status, Object data) {
        this.status = status;
        this.data = data;
    }

    public Status getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }
}
