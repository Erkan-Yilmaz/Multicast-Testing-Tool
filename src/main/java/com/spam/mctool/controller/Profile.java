package com.spam.mctool.controller;

import java.io.File;

public class Profile {
	/**
	 * The profile name.
	 */
	private String name;

	/**
	 * The path to the profile.
	 */
	private File path;

	/*
	 * Default constructor
	 */
	public Profile(){
		this.name = null;
		this.path = new File("");
	}

	/**
	 * Constructor to initialize the profile directly with a ame and a path.
	 * @param name The name of the profile.
	 * @param path The path of the profile. Must not be null.
	 */
	public Profile(String name, File path){
		//set the name
		this.name = name;
		//set the path, the path must not be null
		if(path == null){
			throw new IllegalArgumentException();
		}
		else{
			this.path = path;
		}
	}

	/**
	 * Get the profile name.
	 * @return the name as string. Null if no name has been set.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the profile name.
	 * @param name The new profile name. Can be null.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the path to the profile file.
	 * @return The path to the profile file. Will never be null.
	 */
	public File getPath() {
		return path;
	}

	/**
	 * Set the path to the profile file.
	 * @param path The path to the profile file as path. Must not be null.
	 */
	public void setPath(File path) {
		if(path == null){
			throw new IllegalArgumentException();
		}
		this.path = path;
	}

	/**
	 * This function compares the path of two files and returns whether they are equal
	 * or not.
	 * @param testObject The profile to compare with.
	 * @return true if both share the same path. false if not.
	 */
	public boolean equals(Profile testObject) {
		//Test for correct input
		if(testObject == null){
			throw new IllegalArgumentException();
		}
		//Compare the Path
		return this.path.equals(testObject.getPath());

	}
}
