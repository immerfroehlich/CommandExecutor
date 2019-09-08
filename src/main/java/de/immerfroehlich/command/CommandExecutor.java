package de.immerfroehlich.command;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.io.IOUtils;

/**
 * Don't use the same instance of this class in multiple Threads.
 * @author andreas
 *
 */
public class CommandExecutor {
	
	public Result execute(Command command) {
		try {
			//TODO: Einen Thread aufzumachen ist offenbar eine kostspielige Operation.
			//Daher wäre es besser, dies nur einmal beim instantiieren der Klasse zu machen.
			//Problem ist, dann lässt sich der Executor nicht herunterfahren, was dazu führt,
			//dass Threads offen bleiben und das Java-Programm nicht beendet wird.
			ExecutorService executor = Executors.newFixedThreadPool(2);
			
			String[] commands = command.toArray();
			final Process process = Runtime.getRuntime().exec(commands);
			
			final InputStream in = process.getInputStream();
			final InputStream err = process.getErrorStream();
			
			Callable<byte[]> stdoutCallable = new Callable<byte[]>() {
				
				@Override
				public byte[] call() throws Exception {
					return IOUtils.toByteArray(in);
				}
			};
			
			Callable<List<String>> stderrCallable = new Callable<List<String>>() {
				
				@Override
				public List<String> call() throws Exception {
					return readFromInputStream(err);
				}
			};
			
			Future<byte[]> stdout = executor.submit(stdoutCallable);
			Future<List<String>> stderr = executor.submit(stderrCallable);
			
			int exitCode = process.waitFor();
			byte[] stdOut = stdout.get();
			List<String> stdErr = stderr.get();
			Result result = new Result(stdOut, stdErr);
			
			executor.shutdown();
			
			return result;
		}
		catch (IOException e) {
			String prog = command.getCommandWithoutParameters();
			throw new RuntimeException("Most likely you haven't installed \"" + prog + "\" you are calling. But it maybe a serious IO problem.", e);
		} catch (InterruptedException e) {
			throw new RuntimeException("The current thread was interrupted by another thread. Don't do this. It is not designed for it.");
		} catch (ExecutionException e) {
			throw new RuntimeException("This most likely is a bad programming error in this library. The Callables should not throw any exceptions.");
		}
	}
	
	private List<String> readFromInputStream(InputStream in) {
		List<String> output;
		try {
			output = IOUtils.readLines(in, StandardCharsets.UTF_8);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException("Could not read from stdout. This must be something really bad.", e);
		}
		return output;
	}
	
}
