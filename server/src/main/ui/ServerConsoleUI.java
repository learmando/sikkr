package main.ui;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import main.model.Contact;
import main.model.Message;
import main.util.InformationEvent;
import main.util.InformationListener;
import main.util.FileUtility;

public final class ServerConsoleUI implements InformationListener {
	
	/*
	 * ------------------------ Instance constants ------------------------
	 */
	
	private final Map<String, Contact> contacts;
	private final RSAPrivateKey privateKey;
	private final RSAPublicKey publicKey;
	private Logger logger;

	public ServerConsoleUI() {
		//TODO Possibly loading contacts upon initiation, keys from file
		final KeyPairGenerator keyGen;
		final KeyPair key;
		RSAPublicKey publicKey = null;
		RSAPrivateKey privateKey = null;
		contacts = Collections.synchronizedMap(new HashMap<String, Contact>());
		
		try {
			keyGen = KeyPairGenerator.getInstance("RSA");
	        keyGen.initialize(4096);
	        key = keyGen.genKeyPair();
	        publicKey = (RSAPublicKey) key.getPublic();
	        privateKey = (RSAPrivateKey) key.getPrivate();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		try {
			List<Contact> fromFile = FileUtility.readContacts();
			List<Message> msgFromFile = FileUtility.readMessages();
			fromFile.forEach((contact) -> contacts.put(contact.getNumber(), contact));
			msgFromFile.forEach(this::addMessageToContacts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		
		setupUserInterface();
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
	        public void run() {
	            try {
					FileUtility.saveMessages(contacts.values());
					FileUtility.saveContacts(contacts.values());
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    }, "Shutdown-thread"));
	}
	
	/*
	 * ------------------------ Public methods ----------------------------
	 */

	@Override
	public final synchronized void addContact(Contact c) {
		contacts.put(c.getNumber(), c);
	}
	
	@Override
	public final byte[] decrypt(byte[] input) {
		try {
			Cipher cipher = Cipher.getInstance("RSA/None/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(input);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException 
				| IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public final Contact getContactByNumber(String number) {
		return contacts.get(number);
	}	
	
	@Override
	public final int getPort() {
		return 997;
	}

	@Override
	public final RSAPublicKey getPublicKey() {
		return publicKey;
	}
	
	@Override
	public void sendInformation(InformationEvent e) {
		switch (e.getType()) {
		
		case LOG:
			logger.log(e.getLevel(), e.getLog());
			break;
		case THROWABLE:
			e.getThrowable().printStackTrace();
			break;
		}
	}
	
	/*
	 * ------------------------ Private methods ---------------------------
	 */
	
	private final void addMessageToContacts(Message message) {
		getContactByNumber(message.getSender()).sentMessages.add(message);
		getContactByNumber(message.getReciever()).recievedMessages.add(message);
	}
	
	private final void setupUserInterface() {
		logger = Logger.getGlobal();
	}

	@Override
	public int getObjectPort() {
		// TODO Auto-generated method stub
		return 998;
	}

	@Override
	public int getWritePort() {
		// TODO Auto-generated method stub
		return 999;
	}
	
}
