#🔐 Secure Password Vault Using AES-CBC + Diffie-Hellman

##📖 Introduction
This project implements a Secure Password Vault using Java cryptography.

The system combines Diffie-Hellman, SHA-256, and AES-CBC to generate an encryption key and securely store password information.

The vault allows users to:

Create a new encrypted vault.
Open an existing vault.
Add, edit, delete, and list credentials.
Encrypt the vault using AES-CBC.
Decrypt the vault when it is opened.

##🎯 Project Purpose
The main purpose of the project is to demonstrate practical applications of cryptography, including:

Diffie-Hellman key exchange.
SHA-256 key derivation.
AES-256 symmetric encryption.
AES-CBC mode.
Initialization Vector (IV).
File encryption and decryption.
Secure password storage.

##🏗️ System Overview
The encryption process follows these steps:

Diffie-Hellman
      ↓
Shared Secret
      ↓
   SHA-256
      ↓
  AES-256 Key
      ↓
 AES-CBC Encryption
      ↓
 Encrypted Vault

When opening the vault, the process is reversed:

Encrypted Vault
      ↓
 AES-CBC Decryption
      ↓
 Plaintext Data
      ↓
 Password Entries

##🔑 Diffie-Hellman Key Generation
The program generates two Diffie-Hellman key pairs and uses KeyAgreement to produce a shared secret.

KeyPairGenerator kpg = KeyPairGenerator.getInstance("DH");
kpg.initialize(2048);

KeyPair p1 = kpg.generateKeyPair();
KeyPair p2 = kpg.generateKeyPair();

KeyAgreement ka1 = KeyAgreement.getInstance("DH");
ka1.init(p1.getPrivate());
ka1.doPhase(p2.getPublic(), true);

byte[] shared_secret = ka1.generateSecret();

The resulting shared secret is not used directly as the AES key.

##🔐 SHA-256 Key Derivation
SHA-256 is applied to the Diffie-Hellman shared secret to produce a fixed 256-bit value.

MessageDigest sha = MessageDigest.getInstance("SHA-256");
byte[] hashed = sha.digest(shared_secret);

return new SecretKeySpec(hashed, "AES");

The resulting 32-byte key is used as an AES-256 key.

##🔒 AES-CBC Encryption
The vault data is converted into bytes and encrypted using:

AES/CBC/PKCS5Padding

A random 16-byte IV is generated for every encryption:

byte[] iv = new byte[16];
new SecureRandom().nextBytes(iv);

The IV is stored at the beginning of the vault file, followed by the encrypted ciphertext.

vault.bin

[ 16-byte IV ][ Encrypted Ciphertext ]

##🔓 AES-CBC Decryption
When the user opens the vault, the program:

Reads the first 16 bytes as the IV.
Reads the remaining bytes as ciphertext.
Uses the AES key and IV to decrypt the data.
Converts the decrypted bytes back into text.
Restores the password entries.
Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

cipher.init(
    Cipher.DECRYPT_MODE,
    aes_key,
    ivSpec
);

byte[] plain_text = cipher.doFinal(encrypted_data);

##🗃️ Password Vault Entries
Each entry is stored in the following format:

Website|Username|Password|Notes

For example:

Google|student@gmail.com|MyPassword123|University account

The program stores all entries in an ArrayList:

static List<String> entries = new ArrayList<>();

##📋 Vault Operations
After opening the vault, the user can select:

----- Vault Menu -----

1) List entries
2) Add entry
3) Edit entry
4) Delete entry
5) Save and exit

This allows the user to manage stored credentials before saving them again in encrypted form.

##💾 File Storage
The encrypted vault is stored in:

vault.bin

The file contains:

16-byte IV
+
AES-CBC encrypted data

The actual passwords and notes are therefore not stored as readable plaintext in the vault file.

##🧩 Main Components
1. SecurePasswordVault
The main class controls the entire application.

It is responsible for:

Starting the program.
Generating the AES key.
Displaying the main menu.
Creating vaults.
Opening existing vaults.
Managing vault entries.
2. creatAESkey()
Generates the AES key using:

Diffie-Hellman → Shared Secret → SHA-256 → AES-256 Key

3. createVault()
Creates a new vault and allows the user to add password entries before encrypting and saving them.

4. openVault()
Loads and decrypts an existing vault and provides the user with options to manage its entries.

5. saveFile()
Encrypts the vault using AES-CBC and writes the IV and ciphertext to vault.bin.

6. loadFile()
Reads the IV and encrypted data from vault.bin, decrypts the data, and restores the entries.

##🛡️ Security Technologies Used
Diffie-Hellman
SHA-256
AES-256
AES-CBC
PKCS5 Padding
Random IV
SecureRandom
Java Cryptography Architecture (JCA)

##💻 Requirements
The project requires:

Java Development Kit (JDK)
Java Compiler (javac)
Java Runtime Environment
Java IDE or text editor
The project was developed using Java and the standard Java Cryptography libraries.

##⚙️ Technologies Used
Java
KeyPairGenerator
KeyAgreement
MessageDigest
SecretKey
Cipher
IvParameterSpec
SecureRandom
File I/O
ArrayList
##📌 Important Design Decisions
The project separates the cryptographic operations into three main stages:

1. Key Generation
       ↓
Diffie-Hellman + SHA-256

2. Encryption
       ↓
AES-256-CBC + Random IV

3. Decryption
       ↓
AES-256-CBC + Stored IV

Using a random IV ensures that encrypting the same plaintext again does not produce the same ciphertext when a new IV is used.

##📈 Expected Behavior
When the program starts, it displays:

====== Secure Password Vault System ======

1) Create New Vault
2) Open Existing Vault
3) Exit

The user can create a vault, enter credentials, and save them securely.

When the vault is opened later, the encrypted data is decrypted and the stored entries become available for viewing and editing.

##📚 Course Information
Course: Operating System
Semester: Fall 2025
Project: Secure Password Vault Using AES-CBC + Diffie-Hellman

##👥 Group Members
Shayma Aljuaid 
Lujain Alqarni

##📜 Academic Project
This project was developed as an academic project to demonstrate practical concepts in cryptography, secure data storage, key generation, symmetric encryption, and file protection using Java.

