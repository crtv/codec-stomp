package pro.crtv.codec.stomp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

class StompEncoderTest {

    private final StompEncoder stompEncoder = new StompEncoder();

    @Test
    void testEncode() {
        StompFrame stompFrame = new StompFrame(
                StompCommand.MESSAGE,
                Map.of(StompHeader.ID.getKeyName(), "123"),
                "test_payload".getBytes(StandardCharsets.UTF_8)
        );

        byte[] encodedMessage = stompEncoder.encode(stompFrame);

        String expected = "MESSAGE\ncontent-length:12\nid:123\n\ntest_payload\0";
        Assertions.assertEquals(expected, new String(encodedMessage));
    }
}
