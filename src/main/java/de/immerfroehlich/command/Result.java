package de.immerfroehlich.command;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class Result {
	
	byte[] stdOut;
	List<String> stdErr;
	
	/**
	 * The Result should only be instanciated within this package.
	 * @param stdOut
	 */
	Result(byte[] stdOut, List<String> stdErr) {
		this.stdOut = stdOut;
		this.stdErr = stdErr;
	}

	public byte[] asByteArray() {
		return stdOut;
	}
	
	public List<String> asStringList() {
		ByteArrayInputStream is = new ByteArrayInputStream(stdOut);
		
		List<String> output;
		try {
			//TODO Detect the charset of the OS? Or leave it to the user of this library via parameter?
			output = IOUtils.readLines(is, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("Could not read from byte[] stdout. This should never have happened.", e);
		}
		return output;
	}
	
	public InputStream asInputStream() {
		byte[] byteResult = asByteArray();
		return new ByteArrayInputStream(byteResult);
	}
	
	public List<String> getStdErr() {
		return stdErr;
	}
	
	public boolean hasErrors() {
		//TODO: Should this be detected upon content of stderr or return code (which is OS specific).
		//At least cdparanoia is able to output regular messages to stderr.
		return stdErr.size() > 0;
	}
	
	public void printStdErr() {
		for(String error : getStdErr()) {
			System.err.println(error);
		}
	}
	
	public void printResult() {
		for(String result : asStringList()) {
			System.out.println(result);
		}
	}
}
