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

        StompCommand command = readCommand(byteBuffer);
        Map<String, String> headers = readHeaders(byteBuffer, command);
        byte[] payload = readPayload(byteBuffer, headers);

        return new StompFrame(command, headers, payload);
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

    private Map<String, String> readHeaders(ByteBuffer byteBuffer, StompCommand command) {
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
                int colIndex = header.indexOf(':');

                if (colIndex <= 0) {
                    throw new IllegalStateException("Header entries must be in <key>:<value> format.");
                }

                headers.put(
                        unescape(header.substring(0, colIndex), command.shouldEscape()),
                        unescape(header.substring(colIndex + 1), command.shouldEscape())
                );

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

    private String unescape(String value, boolean shouldUnescape) {
        if (!shouldUnescape) {
            return value;
        }

        StringBuilder sb = new StringBuilder(value.length());
        int currentIndex = 0;

        while (true) {
            int nextIndex = value.indexOf('\\', currentIndex);
            if (nextIndex == -1) {
                sb.append(value, currentIndex, value.length());
                break;
            }

            sb.append(value, currentIndex, nextIndex);
            char nextChar = value.charAt(nextIndex + 1);

            if (nextChar == 'n') {
                sb.append('\n');
            } else if (nextChar == 'r') {
                sb.append('\r');
            } else if (nextChar == '\\') {
                sb.append('\\');
            } else if (nextChar == 'c') {
                sb.append(':');
            } else {
                throw new IllegalStateException("Unexpected escaped character occurred: " + nextChar);
            }

            currentIndex = nextIndex + 2;
        }

        return sb.toString();
    }

    private byte[] readPayload(ByteBuffer byteBuffer, Map<String, String> headers) {
        int contentLength = -1;
        if (headers.containsKey(StompHeader.CONTENT_LENGTH.getKeyName())) {
            try {
                contentLength = Integer.parseInt(headers.get(StompHeader.CONTENT_LENGTH.getKeyName()));
            } catch (NumberFormatException ignored) {
                // will try to read all bytes
            }
        }

        if (contentLength >= 0) {
            byte[] payload = new byte[contentLength];
            byteBuffer.get(payload);
            if (!byteBuffer.hasRemaining() || byteBuffer.get() != '\0') {
                throw new IllegalStateException("The body must by followed by the NULL octet.");
            }
            return payload;
        } else {
            ByteArrayOutputStream payload = new ByteArrayOutputStream();
            while (byteBuffer.hasRemaining()) {
                byte b = byteBuffer.get();
                if (b == '\0') {
                    return payload.toByteArray();
                } else {
                    payload.write(b);
                }
            }
        }

        throw new IllegalStateException("The body must by followed by the NULL octet.");
    }
}
