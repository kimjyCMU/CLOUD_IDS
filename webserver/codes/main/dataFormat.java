package main;

import java.util.*;
import java.lang.*;

public class dataFormat
{
	int TS;
	String addr;
	String value;
	String type;
	
	public dataFormat(int TS, String addr, String value, String type)
	{
		this.TS = TS;
		this.addr = addr;
		this.value = value;
		this.type = type;
	}
	
	public int getTS()
	{
		return TS;
	}
	
	public String getAddr()
	{
		return addr;
	}
	
	public String getValue()
	{
		return value;
	}
	
	public String getType()
	{
		return type;
	}
}