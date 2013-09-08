package com.creepercountry.cctowns.objects.user;

public interface IFighter
{
    public void setLastPvP(long epoch);
    
    public long getLastPvP();

    public boolean hasPvPChamp();
    
    public void setPvPChamp(String name);
    
    public String getPvPChamp();

    public void setPvPLost(int amt);
    
    public int getPvPLost();

    public void setPvPWon(boolean won);

    public boolean getPvPWon();
}
