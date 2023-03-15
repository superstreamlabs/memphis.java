import java.util.concurrent.ExecutionException;
import com.memphis.Headers;
import com.memphis.Memphis;
import com.memphis.MemphisConnectError;
import com.memphis.MemphisError;
import com.memphis.MemphisHeaderError;
import com.memphis.MemphisSchemaError;
import com.memphis.producer.Producer;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try {
            MemphisConnection memphisConnection = Memphis.connect("<memphis-host>", "<application type username>", "<broker-token>").get();

            Producer producer = memphisConnection.createProducer("<station-name>", "<producer-name>").get();

            Headers headers = new Headers();
            headers.add("key", "value");

            //Note that the produce method returns a CompletableFuture, so we need to call the get method on it to wait for the producer to finish producing the message.
            for (int i = 0; i < 5; i++) {
                producer.produce(
                    ("Message #" + i + ": Hello world").getBytes("utf-8"),
                    headers
                ).get();
            }

        } catch (MemphisConnectError | MemphisError | MemphisHeaderError | MemphisSchemaError | InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        } finally {
            memphisConnection.close().get();
        }
    }
}

