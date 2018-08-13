package main;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;

@SuppressWarnings("serial")
public final class Command implements Serializable {
	
	String name; // The name the command is called with.
	LinkedList<File> apps; // The programs the command opens.
	
	Command() {
		this.name = "";
		this.apps = new LinkedList<File>();
	}
	
	Command(String name, LinkedList<File> apps) {
		this.name = name;
		this.apps = apps;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setApps(LinkedList<File> apps) {
		this.apps = apps;
	}
	
	public String getName() {
		return this.name;
	}
	
	public LinkedList<File> getApps() {
		return this.apps;
	}
	
	public String toString() {
		return this.name;
	}
}
