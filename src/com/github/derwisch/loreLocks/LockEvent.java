package com.github.derwisch.loreLocks;

public class LockEvent {
	public byte EventType = 0;          //0 = Undefined; 1 = FailEvent; 2 = SuccessEvent;
	public String EventPermission = ""; //Permission needed for this event
	public byte ActionType = 0;         //0 = Undefined; 1 = PlayerMessage; 2 = BroadCastMessage; 3 = PlayerCommand; 4 = ServerCommand
	public String ActionPayload = "";   //Message/Command to send
}
