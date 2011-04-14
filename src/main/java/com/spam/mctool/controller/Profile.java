package com.spam.mctool.controller;

import java.io.File;

public class Profile {
	private String name;
	private File path;
	
	/*
	 * Default constructor
	 */
	public Profile(){
		this.name = new String();
		this.path = new File("");
	}
	
	/*
	 * Constructor to initialize the profile directly
	 */
	public Profile(String name, File path){
		//set the name
		if(name == null){
			this.name = new String();
		}
		else{
			this.name = name;
		}
		//set the path
		if(path == null){
			//empty filename
			this.path = new File("");
		}
		else{
			this.path = path;
		}
	}
	
	/*
	 * Getter for attribute name
	 */
	public String getName() {
		return name;
	}
	
	/*
	 * Setter for attribute name
	 */
	public void setName(String name) {
		if(name == null){
			throw new IllegalArgumentException();
		}
		this.name = name;
	}
	
	/*
	 * Getter for attribute path
	 */
	public File getPath() {
		return path;
	}
	
	/*
	 * Setter for attribute path
	 */
	public void setPath(File path) {
		if(path == null){
			throw new IllegalArgumentException();
		}
		this.path = path;
	}
	
	/*
	 * This function compares the path of two files and returns whether they are equal
	 * or not
	 */
	public boolean equalPath(Profile testObject) {
		//Test for correct input
		if(testObject == null){
			throw new IllegalArgumentException();
		}
		//Compare the Path
		return this.path.equals(testObject.getPath());
		
	}
}
