package pro.crtv.codec.stomp;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StompDecoderTest {

    private final StompDecoder stompDecoder = new StompDecoder();

    @Test
    void testDecode() {
        String message = "DISCONNECT\n\r\nheader1:test1\nheader2:test2\nheader3:test3\n\ntest_payload\0";
        StompFrame stompFrame = stompDecoder.decode(message.getBytes());

        Assertions.assertEquals(StompCommand.DISCONNECT, stompFrame.getCommand());
        Assertions.assertEquals(3, stompFrame.getHeaders().size());
        Assertions.assertEquals("test1", stompFrame.getHeaders().get("header1"));
        Assertions.assertEquals("test2", stompFrame.getHeaders().get("header2"));
        Assertions.assertEquals("test3", stompFrame.getHeaders().get("header3"));
        Assertions.assertEquals("test_payload", new String(stompFrame.getPayload()));
    }
}
