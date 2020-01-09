package com.helperlinker.bigchaindb;

import java.util.Scanner;

import com.helperlinker.bigchaindb.users.*;
import com.helperlinker.bigchaindb.services.*;

public class AppDriver {
	public static Scanner reader = new Scanner(System.in);

	/**
	 * An entry point to the console app
	 */
	public static void main(String[] args) {
		AppDriver appDriver = new AppDriver();

		appDriver.createDummyAccounts();
		appDriver.selectOptions();
	}

	private void createDummyAccounts() {
		// ID card number, hashed password, salt
		Helper helper1 = new Helper("A1234", "b8db2e4680c00f5075682be1179797bf29f15f7a2b87a6818eec3cf9e47955ff",
				"a0eb205f1dfcc32fbb21c4febb8a4669"); // Password: aaa
//		Helper helper2 = new Helper("B1234", "0f2217e5473b6caf78c9589a71640f15bf09de0f8d54bca369a677d89d9623f7",
//				"45222d65d019849bc683cdd34e9b025b"); // Password: aaa
//		Helper helper3 = new Helper("C1234", "a4a359707ee7743bf2d56d95f1678fc2e5e9cc9450607a49eb20e1414887a62c",
//				"c08231a23b32ee95b895e5e58f5f743a"); // Password: aaa
//
//		Employer employer1 = new Employer("D1234", "ebb3bf34282e8fdef0ef5a3e348ff7c2819de2019d1abadbe0d56726ebec5d63",
//				"7207b41bf591564c44ffd6de075ef7cc"); // Password: aaa
//		Employer employer2 = new Employer("E1234", "b09197233c12521ea4b5782325b406206f74ed50115bc864b7fd21577fc2157b",
//				"fa46e2894da79afa9013b0e58222a7d7"); // Password: aaa
//		Employer employer3 = new Employer("F1234", "5863956caa559d0c2bc20071f974e8eca514f83223523636af0bf7c2f3648af8",
//				"32b3d04d49e8e63f5a3fabce813dd954"); // Password: aaa
	}

	private void selectOptions() {
		System.out.println();
		System.out.println("Please select an option.");
		System.out.println("1. I am a helper.");
		System.out.println("2. I am an employer.");

		int option = reader.nextInt();
		reader.nextLine();
		if (option == 1) {
			String s;
			do {
				System.out.print("Enter your ID card number: ");
				s = reader.nextLine();
				System.out.print("Enter your password: ");
			} while (!helperLogin(s, reader.nextLine()));

			System.out.println("Please select an option.");
			System.out.println("1. Create your profile for employment.");
			System.out.println("2. Modify your profile for employment.");

			option = reader.nextInt();
			if (option == 1) {
				// TODO
			}

			else if (option == 2) {
				// TODO
			}
		}

		else if (option == 2) {
			String s;
			do {
				System.out.print("Enter your ID card number: ");
				s = reader.nextLine();
				System.out.print("Enter your password: ");
			} while (!employerLogin(s, reader.nextLine()));

			System.out.println("Please select an option.");
			System.out.println("1. Browse the domestic helper list.");
			System.out.println("2. Provide a feedback to the domestic helper hired from the platform.");
			System.out.println("3. Modify a feedback to the domestic helper hired from the platform.");

			option = reader.nextInt();
			if (option == 1) {
				// TODO
			}

			else if (option == 2) {
				// TODO
			}

			else if (option == 3) {
				// TODO
			}
		}
		reader.close();
	}

	/**
	 * @return true if the login is successful
	 */
	private boolean helperLogin(String idCardNum, String pwd) {
		Object[] list = Helper.getHashedPwdAndSalt(idCardNum);
		if (list == null) {
			return false;
		}
		return ((String) list[0]).equals(SecurityServices.calculateHash(pwd, (String) list[1], "SHA-256"));
	}

	/**
	 * @return true if the login is successful
	 */
	private boolean employerLogin(String idCardNum, String pwd) {
		String[] list = Employer.getHashedPwdAndSalt(idCardNum);
		if (list == null) {
			return false;
		}
		return list[0].equals(SecurityServices.calculateHash(pwd, list[1], "SHA-256"));
	}
}
