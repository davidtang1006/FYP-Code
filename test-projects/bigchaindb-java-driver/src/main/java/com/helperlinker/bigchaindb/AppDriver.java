package com.helperlinker.bigchaindb;

import com.helperlinker.bigchaindb.users.Employer;
import com.helperlinker.bigchaindb.users.Helper;
import com.helperlinker.bigchaindb.services.BigchainDBServices;
import com.helperlinker.bigchaindb.services.MongoDBServices;
import com.helperlinker.bigchaindb.services.SecurityServices;

import java.util.ArrayList;
import java.util.Scanner;

import org.bson.Document;

public class AppDriver {
	private static Scanner reader = new Scanner(System.in);

	/**
	 * An entry point to the console app
	 */
	public static void main(String[] args) {
		try {
			BigchainDBServices.setConfig();
			MongoDBServices.setConfig();

			createDummyAccounts();
			selectOptions();
		} finally {
			System.out.println();
			reader.close();
			MongoDBServices.cleanUp();
			System.out.print("The program may take some time to terminate.");
		}
	}

	private static void createDummyAccounts() {
		// Create dummy accounts if they do not exist
		if (helperLogin("A1234", "aaa") == false) {
			// ID card number, password, salt
			System.out.println();
			new Helper("A1234", "aaa", "a0eb205f1dfcc32fbb21c4febb8a4669");
			System.out.println();
			new Helper("B1234", "aaa", "45222d65d019849bc683cdd34e9b025b");
			System.out.println();
			new Helper("C1234", "aaa", "c08231a23b32ee95b895e5e58f5f743a");
		}

		// Create dummy accounts if they do not exist
		if (employerLogin("D1234", "aaa") == false) {
			// ID card number, hashed password, salt
			new Employer("D1234", "ebb3bf34282e8fdef0ef5a3e348ff7c2819de2019d1abadbe0d56726ebec5d63",
					"7207b41bf591564c44ffd6de075ef7cc"); // Password: aaa
			new Employer("E1234", "b09197233c12521ea4b5782325b406206f74ed50115bc864b7fd21577fc2157b",
					"fa46e2894da79afa9013b0e58222a7d7"); // Password: aaa
			new Employer("F1234", "5863956caa559d0c2bc20071f974e8eca514f83223523636af0bf7c2f3648af8",
					"32b3d04d49e8e63f5a3fabce813dd954"); // Password: aaa
		}
	}

	private static void selectOptions() {
		while (true) {
			System.out.println();
			System.out.println("Please select an option.");
			System.out.println("1. I am a helper.");
			System.out.println("2. I am an employer.");
			System.out.println("0. Exit.");

			int option = reader.nextInt();
			reader.nextLine();
			if (option == 1) {
				String idCardNum, pwd = "";
				Helper helper;
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

				System.out.println("Loading... Please wait.");
				helper = new Helper(idCardNum, pwd);
				while (true) {
					System.out.println();
					System.out.println("Please select an option.");
					System.out.println("1. Create/modify your profile for employment.");
					System.out.println("0. Log out.");

					option = reader.nextInt();
					reader.nextLine();
					if (option == 1) {
						editHelperProfile(helper);
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
						browseHelperList();
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
	}

	/**
	 * @param input User input
	 * @return 1 if the input is a yes, 2 if the input is a no, 0 otherwise
	 */
	private static int interpretYesAndNo(String input) {
		if (input.equals("Y") || input.equals("y")) {
			return 1;
		} else if (input.equals("N") || input.equals("n")) {
			return 2;
		}
		return 0;
	}

	/**
	 * @return true if the login is successful
	 */
	private static boolean helperLogin(String idCardNum, String pwd) {
		String[] list = Helper.getHashedPwdAndSalt(idCardNum);
		if (list == null) {
			return false;
		}
		return list[0].equals(SecurityServices.calculateHash(pwd, list[1], "SHA-256"));
	}

	/**
	 * @return true if the login is successful
	 */
	private static boolean employerLogin(String idCardNum, String pwd) {
		String[] list = Employer.getHashedPwdAndSalt(idCardNum);
		if (list == null) {
			return false;
		}
		return list[0].equals(SecurityServices.calculateHash(pwd, list[1], "SHA-256"));
	}

	private static void editHelperProfile(Helper helper) {
		System.out.println();
		System.out.print("Self-introduction: ");

		String selfIntro = reader.nextLine();

		while (true) {
			System.out.print("Do you want to save your profile? [Y/N] ");
			int response = interpretYesAndNo(reader.nextLine());
			if (response == 1) {
				helper.updateInfo(selfIntro);
				break;
			} else if (response == 2) {
				break;
			}
		}
	}

	private static void browseHelperList() {
		do {
			System.out.println();
			System.out.print("Keywords: (Enter nothing to exit. The result will be sorted by relevance.) ");
			String keywords = reader.nextLine();

			if (keywords.equals("")) {
				// Exit if the input is empty
				break;
			}

			ArrayList<Document> results = MongoDBServices.searchHelperLatestInfo(keywords);
			if (results.isEmpty()) {
				System.out.println("There are no results found. Please try other keywords.");
			} else {
				results.forEach(MongoDBServices.printBlock);
			}
		} while (true);
	}
}
