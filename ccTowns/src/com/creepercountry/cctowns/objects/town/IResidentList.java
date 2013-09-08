package com.creepercountry.cctowns.objects.town;

import java.util.List;

import com.creepercountry.cctowns.objects.user.User;



public interface IResidentList
{
	public List<User> getUsers();
	public boolean hasUser(String name);
}
