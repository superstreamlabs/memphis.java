import java.util.concurrent.ExecutionException;
import com.memphis.Memphis;
import com.memphis.MemphisConnectError;
import com.memphis.MemphisError;
import com.memphis.MemphisHeaderError;
import com.memphis.consumer.Consumer;
import com.memphis.message.Message;
import com.memphis.message.MessageHeaders;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        try {
            MemphisConnection memphisConnection = Memphis.connect("<memphis-host>", "<application type username>", "<broker-token>").get();

            Consumer consumer = memphisConnection.createConsumer("<station-name>", "<consumer-name>", "").get();
            consumer.setContext("key", "value");

            consumer.consume((msgs, error, context) -> {
                try {
                    for (Message msg : msgs) {
                        System.out.println("message: " + msg.getData());
                        msg.ack().get();
                        MessageHeaders headers = msg.getHeaders();
                    }
                    if (error != null) {
                        System.out.println(error);
                    }
                } catch (InterruptedException | ExecutionException | MemphisError | MemphisHeaderError e) {
                    System.out.println(e.getMessage());
                }
            }).get();

            // Keep your main thread alive so the consumer will keep receiving data
            new java.util.concurrent.CountDownLatch(1).await();

        } catch (MemphisConnectError | MemphisError e) {
            System.out.println(e.getMessage());
        }
    }
}
