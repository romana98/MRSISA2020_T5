package com.project.tim05.dto;

import java.util.ArrayList;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

public class HallDTO {
   
	@NotBlank 
	@NotNull
	@Pattern(regexp="([A-Z][a-z]+[ ]*){1,}")
    private String name;
	 
	
	@NotNull
	@Min(value=0)
    private int number;
	
	@NotNull
	private int id;
	
	private String time;
	
	@NotNull
	private ArrayList<String> times = new ArrayList<String>();
	
	
	
    public ArrayList<String> getTimes() {
		return times;
	}

	public void setTimes(ArrayList<String> times) {
		this.times = times;
	}

	public HallDTO(){
    	
    }
	
    public HallDTO(String name, int number) {
		super();
		this.name = name;
		this.number = number;
		
	}
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}