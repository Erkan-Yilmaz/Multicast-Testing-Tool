package com.spam.mctool.controller;

import java.io.File;

public class Profile {
	private String name;
	private File path;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public File getPath() {
		return path;
	}
	public void setPath(File path) {
		this.path = path;
	}
}
