package dev.memphis.sdk;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class Memphis {

	/**
	 *
	 * @param opts options used to customize behaviour of Memphis Client
	 * @return a Memphis Client object for communicating with Memphis Host
	 */
	public static MemphisConnection connect(ClientOptions opts) {
		try {
			return new MemphisConnection(opts);
		} catch (MemphisConnectException e) {
			throw new RuntimeException(e);
		}
	}
}
