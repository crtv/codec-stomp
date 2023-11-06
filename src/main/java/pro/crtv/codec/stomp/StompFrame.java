package pro.crtv.codec.stomp;

import java.util.Map;

public class StompFrame {

    private final StompCommand command;
    private final Map<String, String> headers;
    private final byte[] payload;

    public StompFrame(StompCommand command, Map<String, String> headers, byte[] payload) {
        this.command = command;
        this.headers = headers;
        this.payload = payload;
    }

    public StompCommand getCommand() {
        return command;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getPayload() {
        return payload;
    }
}
