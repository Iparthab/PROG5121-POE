QuickChat – Programming 1A Portfolio of Evidence (POE)

Student Information

Student Name: Ishaan Parthab
Student Number: ST10438649
Module: Programming 1A
Project Name: QuickChat
Programming Language: Java
Development Environment: Apache NetBeans IDE
Version Control: Git and GitHub

1. Project Overview

QuickChat is a Java-based messaging application developed as part of the Programming 1A Portfolio of Evidence (POE).

The application allows users to register an account, log in, create messages, send messages, disregard messages, and store messages for later use.

The project combines functionality developed during Assignment 1, Assignment 2, and the final POE.

2. Application Features

Part 1 – User Registration and Login

Register users with their first name and last name.

Validate usernames containing an underscore and no more than five characters.

Validate passwords containing at least eight characters, an uppercase letter, a number, and a special character.

Validate South African cellphone numbers using the international +27 format.

Allow registered users to log in using their credentials.

Display appropriate registration and login messages.

Part 2 – QuickChat Messaging

Allow users to specify how many messages they want to enter.

Generate unique message IDs.

Generate message hashes.

Validate recipient cellphone numbers.

Validate message lengths (maximum 250 characters).

Allow users to send, disregard, or store messages.

Display message information, including message ID, message hash, recipient, and message content.

Track the total number of messages sent.

Part 3 – Message Management

Maintain collections of sent, stored, and disregarded messages.

Display recently sent messages.

Display senders and recipients.

Identify the longest stored message.

Search for messages using a message ID.

Search for messages using a recipient number.

Delete messages using their message hashes.

Generate a report containing message details.

Store message information in a JSON file.

3. Technologies Used

Java

Apache NetBeans IDE

Java Collections (ArrayList)

Java Regular Expressions

JSON file storage

Git

GitHub

4. How to Run the Application

Download or clone this GitHub repository.

Open Apache NetBeans IDE.

Select File → Open Project.

Navigate to the downloaded QuickChat POE project.

Open the project.

Locate POE.java under Source Packages.

Run the project using the Run button or F6.

Follow the instructions displayed in the output console.

5. How to Use QuickChat

Register by entering your personal details.

Create a valid username and password.

Enter a South African cellphone number beginning with +27.

Log in using your registered credentials.

Specify how many messages you want to enter.

Select an option from the QuickChat menu.

Enter a recipient number and message.

Choose whether to send, disregard, or store the message.

Use the message management menu to search, view, or delete messages.

6. Testing

The application can be tested using the following scenarios:

Valid and invalid usernames.

Valid and invalid passwords.

Valid and invalid South African cellphone numbers.

Successful and unsuccessful login attempts.

Sending valid messages.

Rejecting invalid recipient numbers.

Rejecting messages exceeding 250 characters.

Storing and retrieving messages.

Searching for messages by ID and recipient.

Deleting messages using message hashes.

Displaying message reports.

7. Version Control

Git and GitHub were used to manage the development of this application.

The repository contains the project's source code and commit history documenting changes made during development.

8. Conclusion

The QuickChat application demonstrates the use of Java programming concepts, including object-oriented programming, input validation, arrays and collections, file handling, conditional statements, loops, and methods.

The final POE integrates the registration, messaging, and message management functionality developed throughout the module.
