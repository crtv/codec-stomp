package pro.crtv.codec.stomp;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StompDecoder {

    public StompFrame decode(byte[] message) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(message);

        return new StompFrame(
                readCommand(byteBuffer),
                readHeaders(byteBuffer),
                readPayload(byteBuffer)
        );
    }

    private StompCommand readCommand(ByteBuffer byteBuffer) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream(64);
        while (byteBuffer.hasRemaining()) {
            byte b = byteBuffer.get();

            // The frame starts with a command string terminated by an end-of-line (EOL),
            // which consists of an OPTIONAL carriage return (octet 13) followed by a REQUIRED line feed (octet 10).
            if (b == '\n') {
                if (byteBuffer.get() != '\r') {
                    byteBuffer.position(byteBuffer.position() - 1);
                } else if (!byteBuffer.hasRemaining() || byteBuffer.get() != '\n') {
                    throw new IllegalStateException("Optional \\r must be followed by required \\n");
                }

                break;
            } else {
                arrayOutputStream.write(b);
            }
        }

        return StompCommand.valueOf(arrayOutputStream.toString(StandardCharsets.UTF_8));
    }

    private Map<String, String> readHeaders(ByteBuffer byteBuffer) {
        // A blank line (i.e. an extra EOL) indicates the end of
        // the headers and the beginning of the body.
        if (byteBuffer.get() == '\n') {
            return Collections.emptyMap();
        }

        Map<String, String> headers = new HashMap<>();
        byteBuffer.position(byteBuffer.position() - 1);
        ByteArrayOutputStream headerLine = new ByteArrayOutputStream();
        while (byteBuffer.hasRemaining()) {
            byte b = byteBuffer.get();
            if (b == '\n') {
                String header = headerLine.toString(StandardCharsets.UTF_8);
                String[] parts = header.split(":");

                if (parts.length != 2) {
                    throw new IllegalStateException("Header entries must be in <key>:<value> format.");
                }

                headers.put(parts[0].strip(), parts[1]);
                headerLine.reset();

                // A blank line (i.e. an extra EOL) indicates the end of
                // the headers and the beginning of the body.
                if (byteBuffer.get() == '\n') {
                    break;
                } else {
                    byteBuffer.position(byteBuffer.position() - 1);
                }
            } else {
                headerLine.write(b);
            }
        }

        return headers;
    }


    private byte[] readPayload(ByteBuffer byteBuffer) {
        // todo: ...
        return new byte[0];
    }
}
