package org.grpc.client;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class GrpcClientApplication {
	private static final Logger logger = LogManager.getLogger(GrpcClientApplication.class);

	public static void main(String[] args) {
		OrderClient orderClient = new OrderClient();
		Scanner scanner = new Scanner(System.in);
		String input = scanner.next();
		int inputInt = Integer.parseInt(input);
		switch (inputInt){
			case 1:
				logger.info("Simple send/receive");
				orderClient.createOrderForGivenOrderId();
			break;
			case 2:
				logger.info("Server side streaming -> client");
				orderClient.serverStreaming();
			break;
			case 3:
				logger.info("Client side streaming -> server");
				orderClient.clientSideStreaming();
				break;
			case 4:
				logger.info("Bi-directional streaming ");
				orderClient.bistreaming();
				break;
		}
	}
}
