package de.immerfroehlich.command;

import java.util.ArrayList;

public class Command {
	
	ArrayList<String> tokens = new ArrayList<>();
	String basePath = null;
	byte[] stdin = null;
	
	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setCommand(String command) {
		tokens.add(command);		
	}
	
	public void addParameter(String parameter) {
		tokens.add(parameter);
	}
	
	public void setStdin(byte[] stdin) {
		this.stdin = stdin;
	}
	
	String[] toArray() {
		String[] array = new String[tokens.size()];
		return tokens.toArray(array);
	}

	@Override
	public String toString() {
		String assembledCommand = "";
		for(int i = 0; i < tokens.size(); i++) {
			if(i != 0) {
				assembledCommand += " ";
			}
			assembledCommand += tokens.get(i);
		}
		
		return assembledCommand;
	}

	String getCommandWithoutParameters() {
		return tokens.get(0);
	}
	
	boolean isBasePathSet() {
		return basePath != null;
	}
	
	boolean isStdinSet() {
		return stdin != null;
	}

}
