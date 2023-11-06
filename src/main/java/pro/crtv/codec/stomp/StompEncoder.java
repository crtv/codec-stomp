package pro.crtv.codec.stomp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static pro.crtv.codec.stomp.StompHeader.CONTENT_LENGTH;

public class StompEncoder {

    private static final byte LF = '\n';
    private static final byte COLON = ':';
    private static final byte NULL = '0';

    private static final Map<String, byte[]> cachedHeaderKeys = new HashMap<>();

    static {
        for (StompHeader header : StompHeader.values()) {
            cachedHeaderKeys.put(header.getKeyName(), encodeHeaderKey(header.getKeyName(), true));
        }
    }

    public byte[] encode(StompFrame stompFrame) {
        try {
            return doEncode(stompFrame);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed to encode given frame", e);
        }
    }

    private byte[] doEncode(StompFrame stompFrame) throws IOException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        arrayOutputStream.write(stompFrame.getCommand().name().getBytes(StandardCharsets.UTF_8));

        if (stompFrame.getHeaders() != null) {
            arrayOutputStream.write(encodeHeaders(stompFrame));
            arrayOutputStream.write(LF);
        }

        if (stompFrame.getPayload() != null) {
            arrayOutputStream.write(stompFrame.getPayload());
            arrayOutputStream.write(LF);
        }

        arrayOutputStream.write(NULL);
        return arrayOutputStream.toByteArray();
    }

    private byte[] encodeHeaders(StompFrame stompFrame) throws IOException {
        Map<String, String> headers = stompFrame.getHeaders();
        if (headers == null) {
            headers = new HashMap<>();
        }

        if (stompFrame.getPayload() != null) {
            headers.put(CONTENT_LENGTH.getKeyName(), Integer.toString(stompFrame.getPayload().length));
        }

        boolean shouldEscape = stompFrame.getCommand().shouldEscape();
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        for (Map.Entry<String, String> e : headers.entrySet()) {
            if (e.getKey() == null || e.getValue() == null) {
                continue;
            }

            arrayOutputStream.write(encodeHeaderKey(e.getKey(), shouldEscape));
            arrayOutputStream.write(COLON);
            arrayOutputStream.write(encodeHeaderValue(e.getValue(), shouldEscape));
        }

        return arrayOutputStream.toByteArray();
    }

    private static byte[] encodeHeaderKey(String key, boolean escape) {
        if (!escape) {
            return key.getBytes(StandardCharsets.UTF_8);
        }

        if (cachedHeaderKeys.containsKey(key)) {
            return cachedHeaderKeys.get(key);
        }

        return escape(key);
    }

    private static byte[] encodeHeaderValue(String value, boolean shouldEscape) {
        if (!shouldEscape) {
            return value.getBytes(StandardCharsets.UTF_8);
        }

        return escape(value);
    }

    private static byte[] escape(String data) {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        for (int i = 0; i < data.length(); i++) {
            // todo: https://stomp.github.io/stomp-specification-1.2.html#Value_Encoding
        }

        return arrayOutputStream.toByteArray();
    }
}
