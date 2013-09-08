package com.creepercountry.cctowns.objects.user;

public interface IDonator
{
	public long getPurchaseDate();
	
	public boolean hasPurchaseDate();
	
	public void setPurchaseDate(long expire);
    
	public String getDonationPackage();
	
	public boolean hasDonationPackage();
	
	public void setDonationPackage(String bundle);
	
	public void addFlyViolation(int amount);
    
	public int getFlyViolation();
    
	public void setFlyViolation(int amount);
    
	public void subtractFlyViolation(int amount);
    
	public void zeroFlyViolation();
    
	public boolean hasFlyViolation();
	
	public boolean getCanFly();
    
	public void setCanFly(boolean fly);
	
	public void setValid(boolean val);
	
	public boolean isValid();
	
	public void setTerm(long milli);
	
	public boolean hasTerm();
	
	public long getTerm();
}
