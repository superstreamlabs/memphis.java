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
            Memphis memphis = new Memphis();
            memphis.connect("<memphis-host>", "<application type username>", "<broker-token>").get();

            Consumer consumer = memphis.createConsumer("<station-name>", "<consumer-name>", "");
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
            });

            // Keep your main thread alive so the consumer will keep receiving data
            new java.util.concurrent.CountDownLatch(1).await();

        } catch (MemphisConnectError | MemphisError e) {
            System.out.println(e.getMessage());
        }
    }
}


/** CompletableFuture - As you can see, we using a get() in the end of some rows.
 * The reason is every object that have this get() method return 'CompletableFuture<Void>'.
 * This is a promise-like object that represents a computation that may not have completed yet,
 * but will eventually produce a result of type 'Void'.
 * When we call 'get()' on this 'CompletableFuture<Void>'', we are blocking the 
 * current thread and waiting for the function opreation to complete. 
 * The 'get()' method will return 'null' if the operation completes successfully,
 * or throw an exception if it fails.
 * 
 * CountDownLatch - we using the CountDownLatch method to make the program run always. 
 * with this method we can put the number 1 and in other uses we can use this variable 
 * to decrease the counter to zero by the .countDown() method
 */
