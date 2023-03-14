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
            Memphis memphis = new Memphis();
            memphis.connect("<memphis-host>", "<application type username>", "<broker-token>").get();

            Producer producer = memphis.producer("<station-name>", "<producer-name>");

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
            memphis.close().get();
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
