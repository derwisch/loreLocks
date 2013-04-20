package com.github.derwisch.loreLocks;

import org.bukkit.permissions.Permission;

public class Permissions {
	public static final Permission BYPASS = new Permission(Strings.BYPASS);
	public static final Permission PICK_ALL = new Permission(Strings.PICK_ALL);
	public static final Permission PICK_1 = new Permission(Strings.PICK_1);
	public static final Permission PICK_2 = new Permission(Strings.PICK_2);
	public static final Permission PICK_3 = new Permission(Strings.PICK_3);
	public static final Permission PICK_4 = new Permission(Strings.PICK_4);
	public static final Permission PICK_5 = new Permission(Strings.PICK_5);
	
	public class Strings {
		public static final String BYPASS = "lorelocks.bypass";
		public static final String PICK_ALL = "lorelocks.pick.*";
		public static final String PICK_1 = "lorelocks.pick.1";
		public static final String PICK_2 = "lorelocks.pick.2";
		public static final String PICK_3 = "lorelocks.pick.3";
		public static final String PICK_4 = "lorelocks.pick.4";
		public static final String PICK_5 = "lorelocks.pick.5";
	}

	public static Permission getPickPermission(int difficulty) {
		switch (difficulty) {
			case 1:
				return PICK_1;
			case 2:
				return PICK_2;
			case 3:
				return PICK_3;
			case 4:
				return PICK_4;
			case 5:
				return PICK_5;
			default:
				return PICK_ALL;
		}
	}
}
