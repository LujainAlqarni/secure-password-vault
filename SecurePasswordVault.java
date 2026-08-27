package securepasswordvault;
//----------------------GROUP.14----------------------//
//--------- STUDENT NAME ---------------------------//          
//         Shayma Aljuaid              
//         Lujanin Alqarni             
//---------------------------------------------------//

import java.io.*;
import java.security.*;
import java.util.*;
import javax.crypto.*;
import javax.crypto.spec.*;

public class SecurePasswordVault {

    //AES Final Key From DH and SHA-256
    static SecretKey aes_key;
    // Vault File Name 
    static final String file_name = "vault.bin";
    //Store each entry as a string in the format website|username|password|notes
    static List<String> entries = new ArrayList<>();

    static Scanner user_input = new Scanner(System.in);

    public static void main(String[] args) {

        System.out.println("====== Secure Password Vault System ======");

        try {

            // AES Key generation through Diffie-Hellman and SHA-256    
            aes_key = creatAESkey();

            while (true) {
                System.out.println("\n1) Create New Vault");
                System.out.println("2) Open Existing Vault");
                System.out.println("3) Exit");
                System.out.println("choose: ");
                String choose = user_input.nextLine();

                if (choose.equals("1")) {
                    createVault();
                } else if (choose.equals("2")) {
                    openVault();
                } else if (choose.equals("3")) {
                    System.out.println("Goodbye.");
                    break;
                } else {
                    System.out.println("Invalid choice");
                }
            }

        } catch (Exception ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }

    }

    // ==============================================================================
    // A Function to generate an AES Key through Diffie-Hellman and SHA-256
    // Implementation Step 1: Key Generation (Diffie–Hellman)
    // The resulting AES key "aes_key" is later used to encrypt and decrypt the vault
    // ==============================================================================
    static SecretKey creatAESkey() throws Exception {

        //Generate key pairs using the "DH" algorithm
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
        kpg.initialize(2048);
        KeyPair p1 = kpg.generateKeyPair();
        KeyPair p2 = kpg.generateKeyPair();

        //Produce a shared secret using KeyAgreement
        KeyAgreement ka1 = KeyAgreement.getInstance("DH");
        ka1.init(p1.getPrivate());
        ka1.doPhase(p2.getPublic(), true);
        byte[] shared_secret = ka1.generateSecret();

        //Apply SHA-256 via MessageDigest to obtain a fixed-length AES key
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] hashed = sha.digest(shared_secret);

        return new SecretKeySpec(hashed, "AES"); // 32 bytes = 256-bit key
    }

    // =========================================================================
    // A Function to Create New Vault
    // =========================================================================
    static void createVault() throws Exception {
        entries.clear();
        System.out.println("\nCreating a NEW vault...");

        //Collect at least one credential from the user
        addEntryInteractive();

        //Collect more then one
        while (true) {
            System.out.println("Add another entry? (yes/no): ");
            String answer = user_input.nextLine();
            if (!answer.equalsIgnoreCase("yes")) {
                break;
            }
            addEntryInteractive();
        }

        saveFile();
        System.out.println("Vault created and encrypted into " + file_name);

    }

    static void addEntryInteractive() {
        System.out.println("Website: ");
        String website = user_input.nextLine();

        System.out.println("Username: ");
        String username = user_input.nextLine();

        System.out.println("Password: ");
        String password = user_input.nextLine();

        System.out.println("Notes: ");
        String notes = user_input.nextLine();

        String line = website + "|" + username + "|" + password + "|" + notes;
        entries.add(line);
        System.out.println("Entry added.");
    }

    // =================================================================================
    // A Function to Open an Existing Vault and Display Vault Menu(list/add/edit/delete)
    // =================================================================================
    static void openVault() throws Exception {
        File file = new File(file_name);
        if (!file.exists()) {
            System.out.println("vault.bin not found!");
            return;
        }

        loadFile(); //Read the stored IV and encrypted data

        System.out.println("\nVault loaded. Entries: " + entries.size());

        //Vault Menu
        while (true) {
            System.out.println("\n----- Vault Menu -----");
            System.out.println("1) List entries");
            System.out.println("2) Add entry");
            System.out.println("3) Edit entry");
            System.out.println("4) Delete entry");
            System.out.println("5) Save and exit");
            System.out.println("choose");
            String choose = user_input.nextLine();

            if (choose.equals("1")) {
                listEntries();
            } else if (choose.equals("2")) {
                addEntryInteractive();
            } else if (choose.equals("3")) {
                editEntry();
            } else if (choose.equals("4")) {
                deleteEntry();
            } else if (choose.equals("5")) {
                saveFile();
                System.out.println("Vault saved and encrypted. Back to main menu. ");
                break;
            } else {
                System.out.println("Invalid choice.");
            }
        }
    }

    //A Function to Display all Entries
    static void listEntries() {
        if (entries.isEmpty()) {
            System.out.println("No entries found.");
            return;
        }
        for (int i = 0; i < entries.size(); i++) {
            String[] p = entries.get(i).split("\\|", -1);
            System.out.println((i + 1) + ") Website: " + p[0]
                    + ", Username: " + p[1]
                    + ", Password: " + p[2]
                    + ", Notes: " + p[3]);
        }
    }

    //A Function to Edit The Entry
    static void editEntry() {
        if (entries.isEmpty()) {
            System.out.println("No entries to edit.");
            return;
        }
        listEntries();
        System.out.println("Enter entry number to edit: ");
        String entry_num = user_input.nextLine();
        try {
            int index = Integer.parseInt(entry_num) - 1;
            if (index < 0 || index >= entries.size()) {
                System.out.println("Invalid index. ");
                return;
            }

            String[] p = entries.get(index).split("\\|", -1);

            System.out.println("Leave field empty to keep old value.");

            System.out.println("New website (" + p[0] + "): ");
            String website = user_input.nextLine();
            if (!website.isEmpty()) {
                p[0] = website;
            }

            System.out.println("New username (" + p[1] + "): ");
            String username = user_input.nextLine();
            if (!username.isEmpty()) {
                p[1] = username;
            }

            System.out.println("New password (" + p[2] + "): ");
            String password = user_input.nextLine();
            if (!password.isEmpty()) {
                p[2] = password;
            }

            System.out.println("New notes (" + p[3] + "): ");
            String notes = user_input.nextLine();
            if (!notes.isEmpty()) {
                p[3] = notes;
            }

            String new_line = p[0] + "|" + p[1] + "|" + p[2] + "|" + p[3];
            entries.set(index, new_line);

            System.out.println("Entry updated");

        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }
    }

    //A Function to delete Entry
    static void deleteEntry() {
        if (entries.isEmpty()) {
            System.out.println("No entries to delete.");
            return;
        }
        listEntries();
        System.out.println("Enter entry number to delete: ");
        String entry_num = user_input.nextLine();
        try {
            int index = Integer.parseInt(entry_num) - 1;
            if (index < 0 || index >= entries.size()) {
                System.out.println("Invalid index. ");
                return;
            }
            entries.remove(index);
            System.out.println("Entry deleted.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
        }

    }

    // =========================================================================
    // A Function to Save the Vault in the file and AES-CBC Encryption
    // Implementation Step 2: Vault Encryption
    // This Function is resposible for encrypting and saving the vault
    // =========================================================================
    static void saveFile() throws Exception {
        
        //convert all entries into one text block(each entry on a new line)
        StringBuilder sb = new StringBuilder();
        for (String x : entries) {
            sb.append(x).append("\n");
        }
        
        // convert the text into bytes so we can encrypt it 
        byte[] plain_text = sb.toString().getBytes("UTF-8");

        //create a random 16-byte IV for AES-CBC
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        //AES-CBC Encryption
        //Encrypt the serialized data
        // creat the AES/CBC/PKCS5Padding cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // set the cipher to ENCRYPT_MODE using our ASE key and IV
        cipher.init(Cipher.ENCRYPT_MODE, aes_key, ivSpec);
        // encrypt the data ciphertext
        byte[] encrypted = cipher.doFinal(plain_text);

        //write IV first, than encrypted data into the file
        FileOutputStream fos = new FileOutputStream(file_name);
        fos.write(iv); // first 16 bytes == IV
        fos.write(encrypted); // rest == ciphertext
        fos.close();

    }

    // =========================================================================
    // A Function to Read the Vault From the file and AES-CBC Decryption
    // Implementation Step 3: Vault Decryption
    // This Function is resposible for loading and decrpting the vault
    // =========================================================================
    static void loadFile() throws Exception {
        //open the vault file to read from it
        FileInputStream fis = new FileInputStream(file_name);

        //Read the IV the first 16 byte 
        byte[] iv = new byte[16];
        int read = fis.read(iv);
        if (read != 16) {
            fis.close();
            throw new IOException("Invalid vault file(IV).");
        }
        
        // read the rest of the file encrypted data
        byte[] encrypted_data = fis.readAllBytes();
        fis.close();
        
        //build the IV object for AES-CBC
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        
        // creat the AES/CBC/PKCS5Padding cipher
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        // set the cipher to DECRYPT_MODE using our ASE key and IV
        cipher.init(Cipher.DECRYPT_MODE, aes_key, ivSpec);
        //decrypt the ciphertext to get the original plaintext
        byte[] plain_text = cipher.doFinal(encrypted_data);
        
        //convert the plaintext bytes back into text
        String all = new String(plain_text, "UTF-8");
        //split the text into lines(one entry per line)
        String[] lines = all.split("\n");

        entries.clear();
        for (String s : lines) {
            if (!s.trim().isEmpty()) {
                entries.add(s);
            }
        }

    }
}
