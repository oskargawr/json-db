package gawr.oskar.client;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

class ClientTest {

    private RequestSender mockRequestSender;

    @BeforeEach
    void setUp() {
        mockRequestSender = mock(RequestSender.class);
    }

    @Test
    void testClientConstructorWithRequestSender() {
        Client client = new Client(mockRequestSender);

        verify(mockRequestSender, times(1)).sendRequest();
    }


    @Test
    void testClientConstructorWithTypeIndexValue() {
        String type = "set";
        String index = "key1";
        String value = "value1";

        RequestSender spySender = spy(new RequestSender(new RequestBuilder(type, index, value)));

        Client client = new Client(spySender);

        verify(spySender, times(1)).sendRequest();
    }

    @Test
    void testClientConstructorWithFilePath() {
        String inFilePath = "path/to/request/file.json";
        Client client = new Client(inFilePath);
        
    }
}
