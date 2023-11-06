package pro.crtv.codec.stomp;

public enum StompCommand {
    CONNECTED,
    STOMP,
    SUBSCRIBE,
    UNSUBSCRIBE,
    SEND,
    BEGIN,
    COMMIT,
    ABORT,
    ACK,
    NACK,
    DISCONNECT,
    CONNECT,
    MESSAGE,
    RECEIPT,
    ERROR;

    boolean shouldEscape() {
        return this != StompCommand.STOMP &&
                this != StompCommand.CONNECT &&
                this != StompCommand.CONNECTED;
    }
}
