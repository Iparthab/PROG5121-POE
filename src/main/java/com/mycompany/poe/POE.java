package com.mycompany.poe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** QuickChat Programming 1A PoE: Parts 1, 2 and 3. */
public class POE {
    private static final Path JSON_FILE = Paths.get("storedMessages.json");
    private static final ArrayList<Message> sentMessages = new ArrayList<>();
    private static final ArrayList<Message> disregardedMessages = new ArrayList<>();
    private static final ArrayList<Message> storedMessages = new ArrayList<>();
    private static final ArrayList<String> messageHashes = new ArrayList<>();
    private static final ArrayList<String> messageIDs = new ArrayList<>();
    private static int totalMessagesSent = 0;
    private static int nextMessageNumber = 0;

    public static class Login {
        private final String firstName, lastName, username, password, cellPhoneNumber;
        public Login(String firstName, String lastName, String username, String password, String cellPhoneNumber) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.username = username;
            this.password = password;
            this.cellPhoneNumber = cellPhoneNumber;
        }
        public boolean checkUserName() { return username.contains("_") && username.length() <= 5; }
        public boolean checkPasswordComplexity() {
            return password.length() >= 8 && password.matches("(?s).*\\p{Lu}.*")
                    && password.matches("(?s).*\\d.*") && password.matches("(?s).*[\\p{Punct}].*");
        }
        // South African international mobile number: +27 followed by 9 digits.
        // Regex syntax reference: https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html
        public boolean checkCellPhoneNumber() { return cellPhoneNumber.matches("\\+27[0-9]{9}"); }
        public String registerUser() {
            if (!checkUserName()) return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
            if (!checkPasswordComplexity()) return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
            return "Username and password successfully captured.";
        }
        public String checkCellPhoneRegistration() {
            return checkCellPhoneNumber() ? "Cell phone number successfully added."
                    : "Cell phone number incorrectly formatted or does not contain international code.";
        }
        public boolean loginUser(String enteredUsername, String enteredPassword) {
            return username.equals(enteredUsername) && password.equals(enteredPassword);
        }
        public String returnLoginStatus(boolean loggedIn) {
            return loggedIn ? "Welcome " + firstName + ", " + lastName + " it is great to see you again."
                    : "Username or password incorrect, please try again.";
        }
        public String getSender() { return cellPhoneNumber; }
    }

    public static class Message {
        private final String messageID, recipient, message, messageHash, sender;
        private final int messageNumber;
        private final String status;
        private static final Random RANDOM = new Random();

        public Message(int messageNumber, String recipient, String message) {
            this(messageNumber, recipient, message, "", generateUniqueID(), "Draft");
        }
        private Message(int messageNumber, String recipient, String message, String sender, String id, String status) {
            this.messageNumber = messageNumber;
            this.recipient = recipient;
            this.message = message;
            this.sender = sender;
            this.messageID = id;
            this.status = status;
            this.messageHash = createMessageHash();
        }
        private static String generateUniqueID() {
            String id;
            do { id = String.valueOf(1000000000L + (long)(RANDOM.nextDouble() * 9000000000L)); }
            while (messageIDs.contains(id));
            return id;
        }
        public Message withSenderAndStatus(String sender, String status) {
            return new Message(messageNumber, recipient, message, sender, messageID, status);
        }
        public boolean checkMessageID() { return messageID.length() == 10 && messageID.matches("\\d{10}"); }
        public String checkRecipientCell() {
            return recipient.matches("\\+27[0-9]{9}") || recipient.matches("0[0-9]{9}")
                    ? "Cell phone number successfully captured."
                    : "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        public boolean isRecipientValid() { return recipient.matches("\\+27[0-9]{9}") || recipient.matches("0[0-9]{9}"); }
        public String checkMessageLength() {
            return message.length() <= 250 ? "Message ready to send."
                    : "Message exceeds 250 characters by " + (message.length() - 250) + "; please reduce the size.";
        }
        public String createMessageHash() {
            String trimmed = message.trim();
            String[] words = trimmed.isEmpty() ? new String[]{""} : trimmed.split("\\s+");
            return messageID.substring(0, 2) + ":" + messageNumber + ":"
                    + (words[0] + words[words.length - 1]).toUpperCase();
        }
        public String sentMessage(int choice) {
            switch (choice) {
                case 1: return "Message successfully sent.";
                case 2: return "Press 0 to delete the message.";
                case 3: return "Message successfully stored.";
                default: return "Invalid choice.";
            }
        }
        public String printMessage() {
            return "Message ID: " + messageID + "\nMessage Hash: " + messageHash
                    + "\nSender: " + sender + "\nRecipient: " + recipient + "\nMessage: " + message;
        }
        public String getMessageID() { return messageID; }
        public String getMessageHash() { return messageHash; }
        public String getRecipient() { return recipient; }
        public String getMessage() { return message; }
        public String getSender() { return sender; }
        public String getStatus() { return status; }
        public int getMessageNumber() { return messageNumber; }
    }

    private static void track(Message m) {
        messageIDs.add(m.getMessageID());
        messageHashes.add(m.getMessageHash());
    }
    public static void recordMessage(Message m, int choice, String sender) throws IOException {
        if (!m.isRecipientValid() || m.getMessage().length() > 250 || m.getMessage().trim().isEmpty())
            throw new IllegalArgumentException("Invalid recipient or message.");
        String status = choice == 1 ? "Sent" : choice == 2 ? "Disregarded" : choice == 3 ? "Stored" : "Invalid";
        if (status.equals("Invalid")) throw new IllegalArgumentException("Invalid choice.");
        Message recorded = m.withSenderAndStatus(sender, status);
        if (choice == 1) { sentMessages.add(recorded); totalMessagesSent++; }
        if (choice == 2) disregardedMessages.add(recorded);
        if (choice == 3) { storedMessages.add(recorded); saveStoredMessages(); }
        track(recorded);
        nextMessageNumber = Math.max(nextMessageNumber, m.getMessageNumber());
    }
    public static int returnTotalMessages() { return totalMessagesSent; }
    public static List<Message> getSentMessages() { return new ArrayList<>(sentMessages); }
    public static List<Message> getDisregardedMessages() { return new ArrayList<>(disregardedMessages); }
    public static List<Message> getStoredMessages() { return new ArrayList<>(storedMessages); }
    public static List<String> getMessageHashes() { return new ArrayList<>(messageHashes); }
    public static List<String> getMessageIDs() { return new ArrayList<>(messageIDs); }

    // JSON serialization and reading use only Java standard library (no extra dependency).
    // JSON format reference: https://www.json.org/json-en.html
    private static String quote(String value) {
        StringBuilder b = new StringBuilder("\"");
        for (char c : value.toCharArray()) {
            switch (c) {
                case '"': b.append("\\\""); break;
                case '\\': b.append("\\\\"); break;
                case '\n': b.append("\\n"); break;
                case '\r': b.append("\\r"); break;
                case '\t': b.append("\\t"); break;
                default:
                    if (c < 32) b.append(String.format("\\u%04x", (int)c));
                    else b.append(c);
            }
        }
        return b.append('"').toString();
    }
    public static void saveStoredMessages() throws IOException {
        StringBuilder json = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            if (i > 0) json.append(",\n");
            json.append("  {\"messageNumber\":").append(m.getMessageNumber())
                .append(",\"messageID\":").append(quote(m.getMessageID()))
                .append(",\"sender\":").append(quote(m.getSender()))
                .append(",\"recipient\":").append(quote(m.getRecipient()))
                .append(",\"message\":").append(quote(m.getMessage()))
                .append(",\"status\":").append(quote(m.getStatus())).append('}');
        }
        json.append("\n]\n");
        Files.write(JSON_FILE, json.toString().getBytes(StandardCharsets.UTF_8));
    }
    // Minimal JSON parser for the JSON array of flat objects written by saveStoredMessages.
    // Supports escaped quotes, backslashes, control characters and Unicode escapes.
    private static class JsonReader {
        private final String s; private int p;
        JsonReader(String s) { this.s = s; }
        void ws() { while (p < s.length() && Character.isWhitespace(s.charAt(p))) p++; }
        boolean consume(char c) { ws(); if (p < s.length() && s.charAt(p) == c) { p++; return true; } return false; }
        void expect(char c) throws IOException { if (!consume(c)) throw new IOException("Invalid JSON at position " + p + ": expected " + c); }
        String string() throws IOException {
            expect('"'); StringBuilder b = new StringBuilder();
            while (p < s.length()) {
                char c = s.charAt(p++);
                if (c == '"') return b.toString();
                if (c != '\\') { b.append(c); continue; }
                if (p >= s.length()) break;
                char e = s.charAt(p++);
                switch (e) {
                    case '"': b.append('"'); break; case '\\': b.append('\\'); break;
                    case '/': b.append('/'); break; case 'n': b.append('\n'); break;
                    case 'r': b.append('\r'); break; case 't': b.append('\t'); break;
                    case 'b': b.append('\b'); break; case 'f': b.append('\f'); break;
                    case 'u':
                        if (p + 4 > s.length()) throw new IOException("Bad Unicode escape");
                        try { b.append((char)Integer.parseInt(s.substring(p, p + 4), 16)); }
                        catch (NumberFormatException ex) { throw new IOException("Bad Unicode escape", ex); }
                        p += 4; break;
                    default: throw new IOException("Bad escape");
                }
            }
            throw new IOException("Unterminated JSON string");
        }
        String value() throws IOException {
            ws(); if (p < s.length() && s.charAt(p) == '"') return string();
            int start = p;
            while (p < s.length() && "0123456789-".indexOf(s.charAt(p)) >= 0) p++;
            if (start == p) throw new IOException("Invalid JSON value");
            return s.substring(start, p);
        }
    }
    public static void loadStoredMessages() throws IOException {
        storedMessages.clear();
        if (!Files.exists(JSON_FILE)) return;
        JsonReader r = new JsonReader(new String(Files.readAllBytes(JSON_FILE), StandardCharsets.UTF_8));
        r.expect('[');
        if (!r.consume(']')) {
            do {
                r.expect('{');
                java.util.Map<String, String> fields = new java.util.HashMap<>();
                if (!r.consume('}')) {
                    do { String key = r.string(); r.expect(':'); fields.put(key, r.value()); }
                    while (r.consume(','));
                    r.expect('}');
                }
                try {
                    Message m = new Message(Integer.parseInt(fields.get("messageNumber")),
                        fields.get("recipient"), fields.get("message"), fields.get("sender"),
                        fields.get("messageID"), "Stored");
                    storedMessages.add(m);
                    nextMessageNumber = Math.max(nextMessageNumber, m.getMessageNumber());
                } catch (RuntimeException ex) { throw new IOException("Invalid stored message record", ex); }
            } while (r.consume(','));
            r.expect(']');
        }
        r.ws();
        if (r.p != r.s.length()) throw new IOException("Unexpected data after JSON array");
        rebuildIndexes();
    }
    private static void rebuildIndexes() {
        messageIDs.clear(); messageHashes.clear();
        for (Message m : sentMessages) track(m);
        for (Message m : disregardedMessages) track(m);
        for (Message m : storedMessages) track(m);
    }
    public static String displayStoredSendersAndRecipients() {
        if (storedMessages.isEmpty()) return "No stored messages.";
        StringBuilder b = new StringBuilder();
        for (Message m : storedMessages) b.append("Sender: ").append(m.getSender())
            .append(" | Recipient: ").append(m.getRecipient()).append('\n');
        return b.toString();
    }
    public static String longestStoredMessage() {
        if (storedMessages.isEmpty()) return "No stored messages.";
        Message longest = storedMessages.get(0);
        for (Message m : storedMessages) if (m.getMessage().length() > longest.getMessage().length()) longest = m;
        return longest.getMessage();
    }
    public static String searchByMessageID(String id) {
        for (Message m : allMessages()) if (m.getMessageID().equals(id))
            return "Recipient: " + m.getRecipient() + "\nMessage: " + m.getMessage();
        return "Message ID not found.";
    }
    public static String searchByRecipient(String recipient) {
        StringBuilder b = new StringBuilder();
        for (Message m : allMessages()) if (m.getRecipient().equals(recipient))
            b.append("[").append(m.getStatus()).append("] ").append(m.getMessage()).append('\n');
        return b.length() == 0 ? "No messages found for this recipient." : b.toString();
    }
    private static ArrayList<Message> allMessages() {
        ArrayList<Message> all = new ArrayList<>();
        all.addAll(sentMessages); all.addAll(disregardedMessages); all.addAll(storedMessages);
        return all;
    }
    public static String deleteByHash(String hash) throws IOException {
        for (Message m : allMessages()) {
            if (m.getMessageHash().equalsIgnoreCase(hash)) {
                sentMessages.remove(m); disregardedMessages.remove(m);
                boolean wasStored = storedMessages.remove(m);
                if (wasStored) saveStoredMessages();
                if ("Sent".equals(m.getStatus())) totalMessagesSent--;
                rebuildIndexes();
                return "Message: \"" + m.getMessage() + "\" successfully deleted.";
            }
        }
        return "Message hash not found.";
    }
    public static String displayReport() {
        StringBuilder b = new StringBuilder("=== QUICKCHAT MESSAGE REPORT ===\n");
        for (Message m : allMessages()) b.append("Status: ").append(m.getStatus()).append('\n')
            .append(m.printMessage()).append("\n--------------------\n");
        b.append("Total messages sent: ").append(totalMessagesSent)
         .append("\nTotal disregarded: ").append(disregardedMessages.size())
         .append("\nTotal stored: ").append(storedMessages.size());
        return b.toString();
    }
    private static int readInt(Scanner scanner, String prompt) {
        while (true) {
            System.out.print(prompt);
            try { return Integer.parseInt(scanner.nextLine().trim()); }
            catch (NumberFormatException e) { System.out.println("Please enter a valid number."); }
        }
    }
    private static void storedMenu(Scanner scanner) {
        boolean back = false;
        while (!back) {
            System.out.println("\n=== Stored Messages ===");
            System.out.println("1. Show senders and recipients\n2. Longest stored message\n3. Search by message ID");
            System.out.println("4. Search by recipient\n5. Delete by message hash\n6. Full message report\n0. Back");
            switch (readInt(scanner, "Choose: ")) {
                case 1: System.out.println(displayStoredSendersAndRecipients()); break;
                case 2: System.out.println(longestStoredMessage()); break;
                case 3: System.out.print("Message ID: "); System.out.println(searchByMessageID(scanner.nextLine().trim())); break;
                case 4: System.out.print("Recipient: "); System.out.println(searchByRecipient(scanner.nextLine().trim())); break;
                case 5:
                    System.out.print("Message hash: ");
                    try { System.out.println(deleteByHash(scanner.nextLine().trim())); }
                    catch (IOException e) { System.out.println("Could not update JSON file: " + e.getMessage()); }
                    break;
                case 6: System.out.println(displayReport()); break;
                case 0: back = true; break;
                default: System.out.println("Invalid option.");
            }
        }
    }
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("--- Register a New Account ---");
        System.out.print("Enter First Name: "); String firstName = scanner.nextLine();
        System.out.print("Enter Last Name: "); String lastName = scanner.nextLine();
        System.out.print("Enter a Username: "); String username = scanner.nextLine();
        System.out.print("Enter a Password: "); String password = scanner.nextLine();
        System.out.print("Enter South African Cell Number (+27...): "); String cell = scanner.nextLine();
        Login login = new Login(firstName, lastName, username, password, cell);
        System.out.println(login.registerUser());
        System.out.println(login.checkCellPhoneRegistration());
        if (!(login.checkUserName() && login.checkPasswordComplexity() && login.checkCellPhoneNumber())) {
            System.out.println("Registration failed. Please restart and correct the details.");
            return;
        }
        System.out.println("\n--- Login ---");
        System.out.print("Username: "); String loginUsername = scanner.nextLine();
        System.out.print("Password: "); String loginPassword = scanner.nextLine();
        boolean loggedIn = login.loginUser(loginUsername, loginPassword);
        System.out.println(login.returnLoginStatus(loggedIn));
        if (!loggedIn) return;
        try { loadStoredMessages(); }
        catch (IOException e) {
            System.out.println("Cannot load stored messages: " + e.getMessage());
            System.out.println("Check storedMessages.json before continuing.");
            return;
        }
        System.out.println("\nWelcome to QuickChat.");
        int limit;
        do { limit = readInt(scanner, "How many messages would you like to enter? "); }
        while (limit < 0);
        int entered = 0;
        boolean running = true;
        while (running) {
            System.out.println("\n=== QuickChat Menu ===");
            System.out.println("1. Send Messages\n2. Show Recently Sent Messages\n3. Quit\n4. Stored Messages");
            switch (readInt(scanner, "Choose an option: ")) {
                case 1:
                    if (entered >= limit) { System.out.println("Message entry limit reached."); break; }
                    for (; entered < limit; entered++) {
                        System.out.println("\nMessage " + (entered + 1));
                        System.out.print("Enter recipient number: "); String recipient = scanner.nextLine().trim();
                        System.out.print("Enter message: "); String content = scanner.nextLine();
                        Message m = new Message(nextMessageNumber + 1, recipient, content);
                        System.out.println(m.checkRecipientCell());
                        System.out.println(m.checkMessageLength());
                        if (!m.isRecipientValid() || content.trim().isEmpty() || content.length() > 250) {
                            System.out.println("Invalid message. Please re-enter this message.");
                            entered--; // repeat this slot, without increasing the number of messages
                            continue;
                        }
                        System.out.println("1. Send Message\n2. Disregard Message\n3. Store Message");
                        int action = readInt(scanner, "Choose option: ");
                        if (action < 1 || action > 3) { System.out.println("Invalid choice. Please retry this message."); entered--; continue; }
                        if (action == 2) {
                            System.out.println(m.sentMessage(action));
                            if (readInt(scanner, "Enter 0 to confirm discard (other number cancels): ") != 0) {
                                System.out.println("Discard cancelled; retry this message."); entered--; continue;
                            }
                        }
                        try {
                            recordMessage(m, action, login.getSender());
                            System.out.println(m.sentMessage(action));
                            if (action == 1) System.out.println(sentMessages.get(sentMessages.size() - 1).printMessage());
                        } catch (IOException e) {
                            System.out.println("Unable to save stored message: " + e.getMessage());
                            entered--; // retry after fixing file permissions
                        }
                    }
                    System.out.println("Total messages sent: " + returnTotalMessages());
                    break;
                case 2:
                    if (sentMessages.isEmpty()) System.out.println("No recently sent messages.");
                    else for (Message m : sentMessages) System.out.println(m.printMessage() + "\n");
                    break;
                case 3: running = false; System.out.println("Exiting QuickChat..."); break;
                case 4: storedMenu(scanner); break;
                default: System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }
}
