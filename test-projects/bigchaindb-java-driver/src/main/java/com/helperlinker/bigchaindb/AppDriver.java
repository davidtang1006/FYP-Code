package com.helperlinker.bigchaindb;

import java.util.Scanner;

import com.helperlinker.bigchaindb.users.Employer;
import com.helperlinker.bigchaindb.users.Helper;
import com.helperlinker.bigchaindb.services.BigchainDBServices;
import com.helperlinker.bigchaindb.services.SecurityServices;

public class AppDriver {
	private static Scanner reader = new Scanner(System.in);

	/**
	 * An entry point to the console app
	 */
	public static void main(String[] args) {
		BigchainDBServices.setConfig();
		AppDriver appDriver = new AppDriver();

		appDriver.createDummyAccounts();
		appDriver.selectOptions();
	}

	private void createDummyAccounts() {
		// ID card number, hashed password, salt
		new Helper("A1234", "b8db2e4680c00f5075682be1179797bf29f15f7a2b87a6818eec3cf9e47955ff",
				"a0eb205f1dfcc32fbb21c4febb8a4669"); // Password: aaa
		System.out.println();
		new Helper("B1234", "0f2217e5473b6caf78c9589a71640f15bf09de0f8d54bca369a677d89d9623f7",
				"45222d65d019849bc683cdd34e9b025b"); // Password: aaa
		System.out.println();
		new Helper("C1234", "a4a359707ee7743bf2d56d95f1678fc2e5e9cc9450607a49eb20e1414887a62c",
				"c08231a23b32ee95b895e5e58f5f743a"); // Password: aaa

		new Employer("D1234", "ebb3bf34282e8fdef0ef5a3e348ff7c2819de2019d1abadbe0d56726ebec5d63",
				"7207b41bf591564c44ffd6de075ef7cc"); // Password: aaa
		new Employer("E1234", "b09197233c12521ea4b5782325b406206f74ed50115bc864b7fd21577fc2157b",
				"fa46e2894da79afa9013b0e58222a7d7"); // Password: aaa
		new Employer("F1234", "5863956caa559d0c2bc20071f974e8eca514f83223523636af0bf7c2f3648af8",
				"32b3d04d49e8e63f5a3fabce813dd954"); // Password: aaa
	}

	private void selectOptions() {
		while (true) {
			System.out.println();
			System.out.println("Please select an option.");
			System.out.println("1. I am a helper.");
			System.out.println("2. I am an employer.");

			int option = reader.nextInt();
			reader.nextLine();
			if (option == 1) {
				String idCardNum, pwd = "";
				do {
					System.out.println();
					System.out.print("Enter your ID card number (enter nothing to exit): ");
					idCardNum = reader.nextLine();

					if (idCardNum.equals("")) {
						break;
					}

					System.out.print("Enter your password (enter nothing to exit): ");
					pwd = reader.nextLine();

					if (pwd.equals("")) {
						break;
					}
				} while (!helperLogin(idCardNum, pwd));

				if (idCardNum.equals("") || pwd.equals("")) {
					continue;
				}

				while (true) {
					System.out.println();
					System.out.println("Please select an option.");
					System.out.println("1. Create/modify your profile for employment.");
					System.out.println("0. Log out.");

					option = reader.nextInt();
					reader.nextLine();
					if (option == 1) {
						editHelperProfile(idCardNum);
					}

					else if (option == 0) {
						break;
					}
				}
			}

			else if (option == 2) {
				String idCardNum, pwd = "";
				do {
					System.out.println();
					System.out.print("Enter your ID card number (enter nothing to exit): ");
					idCardNum = reader.nextLine();

					if (idCardNum.equals("")) {
						break;
					}

					System.out.print("Enter your password (enter nothing to exit): ");
					pwd = reader.nextLine();

					if (pwd.equals("")) {
						break;
					}
				} while (!employerLogin(idCardNum, pwd));

				if (idCardNum.equals("") || pwd.equals("")) {
					continue;
				}

				while (true) {
					System.out.println();
					System.out.println("Please select an option.");
					System.out.println("1. Browse the domestic helper list.");
					System.out.println("2. Provide/modify a feedback to the domestic helper hired from the platform.");
					System.out.println("0. Log out.");

					option = reader.nextInt();
					reader.nextLine();
					if (option == 1) {
						// TODO
					}

					else if (option == 2) {
						// TODO
					}

					else if (option == 0) {
						break;
					}
				}
			}

			else if (option == 0) {
				break;
			}
		}
		reader.close();
	}

	/**
	 * @return true if the login is successful
	 */
	private boolean helperLogin(String idCardNum, String pwd) {
		String[] list = Helper.getHashedPwdAndSalt(idCardNum);
		if (list[0] == null && list[1] == null) {
			return false;
		}
		return list[0].equals(SecurityServices.calculateHash(pwd, list[1], "SHA-256"));
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

	private void editHelperProfile(String idCardNum) {
		System.out.println();
		System.out.print("Self-introduction: ");

		String selfIntro = reader.nextLine();

		while (true) {
			System.out.print("Do you want to save your profile? [Y/N] ");
			int response = interpretYesAndNo(reader.nextLine());
			if (response == 1) {
				Helper.updateInfo(idCardNum, selfIntro);
				break;
			} else if (response == 2) {
				break;
			}
		}
	}

	/**
	 * @param input User input
	 * @return 1 if the input is a yes, 2 if the input is a no, 0 otherwise
	 */
	private int interpretYesAndNo(String input) {
		if (input.equals("Y") || input.equals("y")) {
			return 1;
		} else if (input.equals("N") || input.equals("n")) {
			return 2;
		}
		return 0;
	}
}
