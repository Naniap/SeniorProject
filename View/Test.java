package View;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String option = "UPDATE USERS:  test, lol";
		String originUser = option.split("UPDATE USERS: ")[1].split(",")[0].trim();
		String targetUser = option.split(",")[1].trim();
		System.out.println(targetUser);
	}

}
