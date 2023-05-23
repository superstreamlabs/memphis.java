package dev.memphis.sdk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Memphis {

	/***
	 *
	 * @param opts options used to customize behaviour of Memphis Client
	 * @return a future that can be used to obtain a Memphis Client object for communicating with Memphis Host
	 */
	public static Future<MemphisConnection> connect(ClientOptions opts) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return new MemphisConnection(opts);
			} catch (MemphisConnectException e) {
				throw new RuntimeException(e);
			}
		});
	}
}
