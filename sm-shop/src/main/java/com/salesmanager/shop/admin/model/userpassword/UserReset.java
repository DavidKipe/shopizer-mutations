package com.salesmanager.shop.admin.model.userpassword;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserReset
{
  final static String CHAR_LIST_WITHNUM = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
  final static String CHAR_LIST = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-";

  final static int RANDOM_STRING_LENGTH = 10;
  
  private static final Logger LOGGER = LoggerFactory.getLogger(UserReset.class);

  public static String generateRandomString()
  {
    StringBuilder randStr = new StringBuilder();
				System.out.println("$#7843#"); System.out.println("$#7842#"); System.out.println("$#7841#"); for (int i = 0; i < RANDOM_STRING_LENGTH; i++) {
      int number = getRandomNumber();
      char ch = CHAR_LIST_WITHNUM.charAt(number);
      randStr.append(ch);
    }
				System.out.println("$#7844#"); return randStr.toString();
  }
  
  public static String generateRandomString(int length)
  {
    StringBuilder randStr = new StringBuilder();
				System.out.println("$#7847#"); System.out.println("$#7846#"); System.out.println("$#7845#"); for (int i = 0; i < length; i++) {
      int number = getRandomNumber();
      char ch = CHAR_LIST.charAt(number);
      randStr.append(ch);
    }
				System.out.println("$#7848#"); return randStr.toString();
  }

  private static int getRandomNumber()
  {
    int randomInt = 0;
    Random randomGenerator;
    try {
      randomGenerator = SecureRandom.getInstanceStrong();
      randomInt = randomGenerator.nextInt(CHAR_LIST.length());
						System.out.println("$#7850#"); System.out.println("$#7849#"); if (randomInt - 1 == -1) {
								System.out.println("$#7851#"); return randomInt;
      }
    } catch (NoSuchAlgorithmException e) {
      LOGGER.error("Error while generating error", e);
    }

				System.out.println("$#7853#"); System.out.println("$#7852#"); return randomInt - 1;
  }

  
}