import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Arrays;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StudentDataManagementSystem {
    private static final String STUDENT_FILE = "Student/src/students.csv";
    private static final String SUBJECT_FILE = "Student/src/subjects.csv";
    private static final String GRADE_FILE = "Student/src/grades.csv";
    private static final int PAGE_SIZE = 10;
    private static final String EMAIL_REGEX = "^[^\\.][a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String DOB_REGEX = "^(0?[1-9]|[12][0-9]|3[01])[- \\/.](0?[1-9]|1[012])[- \\/.](19|20)\\d\\d$";
    private static final String ENROLLMENT_DATE_REGEX = "^(0?[1-9]|1[0-2])/(0?[1-9]|[12][0-9]|3[01])/2023$";
    private static final String PHONE_NUMBER_REGEX = "\\d{10}";
    private static List<Student> students = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveGrades();
        }));
        loadStudents();
        displayDateTime();
        System.out.println("\u001B[32mWelcome to Student Data Management System!\u001B[0m");
        while (true) {
            System.out.println("Select an option:");
            System.out.println("\u001B[36m[1] Show Students\u001B[0m");
            System.out.println("\u001B[36m[2] Add Students\u001B[0m");
            System.out.println("\u001B[36m[3] Search Students\u001B[0m");
            System.out.println("\u001B[31m[4] Exit\u001B[0m");
            int choice = inputInt("Enter choice: ");
            switch (choice) {
                case 1:
                    showStudents();
                    break;
                case 2:
                    addStudent();
                    break;
                case 3:
                    searchStudent();
                    break;
                case 4:
                    saveStudents();
                    clearScreen();
                    System.out.println("\u001B[32mThanks for using the program!\u001B[0m");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void displayDateTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");
        String formattedDateTime = currentDateTime.format(formatter);
        System.out.println("\u001B[32mCurrent Date and Time: " + formattedDateTime + "\u001B[0m");
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static int inputInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter an integer.");
            scanner.next();
        }
        int input = scanner.nextInt();
        scanner.nextLine(); // Consume newline character
        return input;
    }

    private static String inputString(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine();
    }

    private static void showStudents() {
        System.out.println("Select an option:");
        System.out.println("\u001B[36m[1] Show by ID\u001B[0m");
        System.out.println("\u001B[36m[2] Show Page No\u001B[0m");
        System.out.println("\u001B[31m[3] Back\u001B[0m");
        int choice = inputInt("Enter choice: ");
        switch (choice) {
            case 1:
                showStudentById();
                break;
            case 2:
                showStudentByPage();
                break;
            case 3:
                clearScreen();
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private static void showStudentById() {
        String id = inputString("Enter student ID: ");
        Student student = findStudentById(id);
        if (student != null) {
            System.out.println(student);
            showStudentSubjects(student);
            choices(student, id);
        } else {
            System.out.println("Student not found.");
            showStudents();
        }
    }

    private static void choices(Student student, String id) {
        System.out.println("Select an option:");
        System.out.println("\u001B[36m[1] Edit Student\u001B[0m");
        System.out.println("\u001B[36m[2] Delete Student\u001B[0m");
        System.out.println("\u001B[36m[3] Show Subjects\u001B[0m");
        System.out.println("\u001B[31m[4] Back\u001B[0m");
        int choice = inputInt("Enter choice: ");
        switch (choice) {
            case 1:
                editStudentPersonalDetails(student, id);
                break;
            case 2:
                deleteStudent(student, id);
                break;
            case 3:
                displaySubjects(id);
                break;
            case 4:
                clearScreen();
                showStudents();
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private static void showStudentByPage() {
        int numPages = (int) Math.ceil(students.size() / (double) PAGE_SIZE);
        int page = inputInt(String.format("Enter page no (1-%d): ", numPages));
        if (page >= 1 && page <= numPages) {
            int start = (page - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, students.size());

            // Define the column widths
            int idWidth = 7;
            int lastNameWidth = 15;
            int firstNameWidth = 15;
            int middleNameWidth = 15;
            int addressWidth = 27;
            int genderWidth = 6;
            int phoneNumberWidth = 12;
            int emailAddressWidth = 33;
            int dateOfBirthWidth = 13;
            int courseWidth = 6;
            int dateOfEnrollmentWidth = 18;

            // Print table headers
            System.out.format("+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+\n",
                    "-".repeat(idWidth + 2), "-".repeat(lastNameWidth + 2), "-".repeat(firstNameWidth + 2),
                    "-".repeat(middleNameWidth + 2),
                    "-".repeat(addressWidth + 2), "-".repeat(genderWidth + 2), "-".repeat(phoneNumberWidth + 2),
                    "-".repeat(emailAddressWidth + 2),
                    "-".repeat(dateOfBirthWidth + 2), "-".repeat(courseWidth + 2),
                    "-".repeat(dateOfEnrollmentWidth + 2));
            System.out.format(
                    "| %-" + idWidth + "s | %-" + lastNameWidth + "s | %-" + firstNameWidth + "s | %-" + middleNameWidth
                            + "s | %-" + addressWidth + "s | %-" + genderWidth + "s | %-" + phoneNumberWidth + "s | %-"
                            + emailAddressWidth + "s | %-" + dateOfBirthWidth + "s | %-" + courseWidth + "s | %-"
                            + dateOfEnrollmentWidth + "s |\n",
                    "ID", "Last Name", "First Name", "Middle Name", "Address", "Gender", "Phone Number",
                    "Email Address",
                    "Date of Birth", "Course", "Date of Enrollment");
            System.out.format("+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+\n",
                    "-".repeat(idWidth + 2), "-".repeat(lastNameWidth + 2), "-".repeat(firstNameWidth + 2),
                    "-".repeat(middleNameWidth + 2),
                    "-".repeat(addressWidth + 2), "-".repeat(genderWidth + 2), "-".repeat(phoneNumberWidth + 2),
                    "-".repeat(emailAddressWidth + 2),
                    "-".repeat(dateOfBirthWidth + 2), "-".repeat(courseWidth + 2),
                    "-".repeat(dateOfEnrollmentWidth + 2));

            // Print student info for current page
            for (int i = start; i < end; i++) {
                Student student = students.get(i);
                System.out.format(
                        "| %-" + idWidth + "s | %-" + lastNameWidth + "s | %-" + firstNameWidth + "s | %-"
                                + middleNameWidth + "s | %-" + addressWidth + "s | %-" + genderWidth + "s | %-"
                                + phoneNumberWidth + "s | %-" + emailAddressWidth + "s | %-" + dateOfBirthWidth
                                + "s | %-" + courseWidth + "s | %-" + dateOfEnrollmentWidth + "s |\n",
                        student.getId(), student.getLastName(), student.getFirstName(),
                        student.getMiddleName(), student.getAddress(), student.getGender(),
                        student.getPhoneNumber(), student.getEmailAddress(), student.getDateOfBirth(),
                        student.getCourse(), student.getDateOfEnrollment());
            }

            // Print table footer
            System.out.format("+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+\n",
                    "-".repeat(idWidth + 2), "-".repeat(lastNameWidth + 2), "-".repeat(firstNameWidth + 2),
                    "-".repeat(middleNameWidth + 2),
                    "-".repeat(addressWidth + 2), "-".repeat(genderWidth + 2), "-".repeat(phoneNumberWidth + 2),
                    "-".repeat(emailAddressWidth + 2),
                    "-".repeat(dateOfBirthWidth + 2), "-".repeat(courseWidth + 2),
                    "-".repeat(dateOfEnrollmentWidth + 2));

            // Print pagination options
            System.out.format("Page %d of %d\n", page, numPages);
            if (page == 1) {
                System.out.println("Select an option:");
                System.out.println("\u001B[36m[1] Next page\u001B[0m");
                System.out.println("\u001B[31m[2] Back\u001B[0m");
            } else if (page == numPages) {
                System.out.println("\u001B[36m[1] Previous page\u001B[0m");
                System.out.println("\u001B[31m[2] Back\u001B[0m");
            } else {
                System.out.println("\u001B[36m[1] Previous page\u001B[0m");
                System.out.println("\u001B[36m[2] Next page\u001B[0m");
                System.out.println("\u001B[31m[3] Back\u001B[0m");
            }

            // Get user choice and navigate to the corresponding page
            int choice = inputInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    if (page == 1) {
                        showStudentByPageWithOptions(page + 1);
                    } else {
                        showStudentByPageWithOptions(page - 1);
                    }
                    break;
                case 2:
                    if (page == 1 || page == numPages) {
                        clearScreen();
                        showStudents();
                        break;
                    }
                    showStudentByPageWithOptions(page + 1);
                    break;
                case 3:
                    clearScreen();
                    showStudents();
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        }
    }

    private static void showStudentByPageWithOptions(int currentPage) {
        int numPages = (int) Math.ceil(students.size() / (double) PAGE_SIZE);
        int page = currentPage;
        if (page >= 1 && page <= numPages) {
            currentPage = page;
            int start = (page - 1) * PAGE_SIZE;
            int end = Math.min(start + PAGE_SIZE, students.size());

            // Define the column widths
            int idWidth = 7;
            int lastNameWidth = 15;
            int firstNameWidth = 15;
            int middleNameWidth = 15;
            int addressWidth = 27;
            int genderWidth = 6;
            int phoneNumberWidth = 12;
            int emailAddressWidth = 33;
            int dateOfBirthWidth = 13;
            int courseWidth = 6;
            int dateOfEnrollmentWidth = 18;

            // Print table headers
            System.out.format("+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+\n",
                    "-".repeat(idWidth + 2), "-".repeat(lastNameWidth + 2), "-".repeat(firstNameWidth + 2),
                    "-".repeat(middleNameWidth + 2),
                    "-".repeat(addressWidth + 2), "-".repeat(genderWidth + 2), "-".repeat(phoneNumberWidth + 2),
                    "-".repeat(emailAddressWidth + 2),
                    "-".repeat(dateOfBirthWidth + 2), "-".repeat(courseWidth + 2),
                    "-".repeat(dateOfEnrollmentWidth + 2));
            System.out.format(
                    "| %-" + idWidth + "s | %-" + lastNameWidth + "s | %-" + firstNameWidth + "s | %-" + middleNameWidth
                            + "s | %-" + addressWidth + "s | %-" + genderWidth + "s | %-" + phoneNumberWidth + "s | %-"
                            + emailAddressWidth + "s | %-" + dateOfBirthWidth + "s | %-" + courseWidth + "s | %-"
                            + dateOfEnrollmentWidth + "s |\n",
                    "ID", "Last Name", "First Name", "Middle Name", "Address", "Gender", "Phone Number",
                    "Email Address",
                    "Date of Birth", "Course", "Date of Enrollment");
            System.out.format("+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+\n",
                    "-".repeat(idWidth + 2), "-".repeat(lastNameWidth + 2), "-".repeat(firstNameWidth + 2),
                    "-".repeat(middleNameWidth + 2),
                    "-".repeat(addressWidth + 2), "-".repeat(genderWidth + 2), "-".repeat(phoneNumberWidth + 2),
                    "-".repeat(emailAddressWidth + 2),
                    "-".repeat(dateOfBirthWidth + 2), "-".repeat(courseWidth + 2),
                    "-".repeat(dateOfEnrollmentWidth + 2));

            // Print student info for current page
            for (int i = start; i < end; i++) {
                Student student = students.get(i);
                System.out.format(
                        "| %-" + idWidth + "s | %-" + lastNameWidth + "s | %-" + firstNameWidth + "s | %-"
                                + middleNameWidth + "s | %-" + addressWidth + "s | %-" + genderWidth + "s | %-"
                                + phoneNumberWidth + "s | %-" + emailAddressWidth + "s | %-" + dateOfBirthWidth
                                + "s | %-" + courseWidth + "s | %-" + dateOfEnrollmentWidth + "s |\n",
                        student.getId(), student.getLastName(), student.getFirstName(),
                        student.getMiddleName(), student.getAddress(), student.getGender(),
                        student.getPhoneNumber(), student.getEmailAddress(), student.getDateOfBirth(),
                        student.getCourse(), student.getDateOfEnrollment());
            }

            // Print table footer
            System.out.format("+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+%s+\n",
                    "-".repeat(idWidth + 2), "-".repeat(lastNameWidth + 2), "-".repeat(firstNameWidth + 2),
                    "-".repeat(middleNameWidth + 2),
                    "-".repeat(addressWidth + 2), "-".repeat(genderWidth + 2), "-".repeat(phoneNumberWidth + 2),
                    "-".repeat(emailAddressWidth + 2),
                    "-".repeat(dateOfBirthWidth + 2), "-".repeat(courseWidth + 2),
                    "-".repeat(dateOfEnrollmentWidth + 2));

            // Print pagination options
            System.out.format("Page %d of %d\n", page, numPages);
            if (page == 1) {
                System.out.println("Select an option:");
                System.out.println("\u001B[36m[1] Next page\u001B[0m");
                System.out.println("\u001B[31m[2] Back\u001B[0m");
            } else if (page == numPages) {
                System.out.println("\u001B[36m[1] Previous page\u001B[0m");
                System.out.println("\u001B[31m[2] Back\u001B[0m");
            } else {
                System.out.println("\u001B[36m[1] Previous page\u001B[0m");
                System.out.println("\u001B[36m[2] Next page\u001B[0m");
                System.out.println("\u001B[31m[3] Back\u001B[0m");
            }

            // Get user choice and navigate to the corresponding page
            int choice = inputInt("Enter your choice: ");
            switch (choice) {
                case 1:
                    if (page == 1) {
                        showStudentByPageWithOptions(page + 1);
                    } else {
                        showStudentByPageWithOptions(page - 1);
                    }
                    break;
                case 2:
                    if (page == 1 || page == numPages) {
                        clearScreen();
                        showStudents();
                        break;
                    }
                    showStudentByPageWithOptions(page + 1);
                    break;
                case 3:
                    clearScreen();
                    showStudents();
                    break;
                default:
                    System.out.println("Invalid choice!");
                    break;
            }
        }
    }

    private static void showStudentSubjects(Student student) {
        System.out.println("Subjects:");
        for (Subject subject : student.getSubjects()) {
            System.out.println(subject);
        }
    }

    private static void addStudent() {
        System.out.println("Enter student details:");
        String lastName;
        while (true) {
            lastName = inputString("Last name: ");
            if (lastName.matches("^[A-Z][a-zA-Z]*$")) {
                break;
            } else {
                System.out.println(
                        "Invalid input. Last name must contain letters only and should be capitalized. Please try again.");
            }
        }

        String firstName;
        while (true) {
            firstName = inputString("First name: ");
            if (firstName.matches("^[A-Z][a-zA-Z]*$")) {
                break;
            } else {
                System.out.println(
                        "Invalid input. First name must contain letters only and should be capitalized. Please try again.");
            }
        }

        String middleName;
        while (true) {
            middleName = inputString("Middle name: ");
            if (middleName.matches("^[A-Z][a-zA-Z]*$")) {
                break;
            } else {
                System.out.println(
                        "Invalid input. Middle name must contain letters only and should be capitalized. Please try again.");
            }
        }

        String address;
        while (true) {
            address = inputString("Address: ");
            if (address.matches("^\\d*\\s*[A-Z][a-zA-Z]+\\s[A-Z][a-zA-Z]+\\s[A-Z][a-zA-Z]+$")) {
                break;
            } else {
                System.out.println(
                        "Invalid input. Address must be in the format: [number] [Street name] [City name]. Please try again.");
            }
        }

        String gender;
        while (true) {
            gender = inputString("Gender: ");
            gender = gender.toLowerCase();
            if (gender.matches("^(m|male)$")) {
                gender = "M";
                break;
            } else if (gender.matches("^(f|female)$")) {
                gender = "F";
                break;
            } else {
                System.out.println(
                        "Invalid input. Gender must be either 'M', 'F', 'male' or 'female'. Please try again.");
            }
        }

        String phoneNumber;
        while (true) {
            phoneNumber = inputString("Phone number: ");
            if (phoneNumber.matches(PHONE_NUMBER_REGEX)) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a valid phone number.");
            }
        }

        String email;
        while (true) {
            email = inputString("Email: ");
            if (email.matches(EMAIL_REGEX)) {
                break;
            } else {
                System.out.println("Invalid input. Please enter a valid email address.");
            }
        }

        String dateOfBirth;
        while (true) {
            dateOfBirth = inputString("Date of Birth (MM/DD/YYYY): ");
            if (dateOfBirth.matches(DOB_REGEX)) {
                dateOfBirth = dateOfBirth.replaceAll("\\b(0(?=[1-9]))", "");
                break;
            } else {
                System.out.println("Invalid input. Please enter a valid date of birth in the format MM/DD/YYYY.");
            }
        }

        String course;
        while (true) {
            course = inputString("Course: ");
            if (course.matches("^(BSCS|BSIT|BSCoE)$")) {
                break;
            } else {
                System.out.println(
                        "Invalid input. Course must be one of the following: BSCS, BSIT, BSCoE. Please try again.");
            }
        }

        String dateOfEnrollment;
        while (true) {
            dateOfEnrollment = inputString("Date of Enrollment (MM/DD/YYYY): ");
            if (dateOfEnrollment.matches("^(0?[1-9]|1[0-2])/(0?[1-9]|[12][0-9]|3[01])/(19|20)\\d\\d$")) {
                // Remove leading zeros
                dateOfEnrollment = dateOfEnrollment.replaceAll("\\b(0(?=[1-9]))", "");
                break;
            } else {
                System.out.println("Invalid input. Please enter a valid date from 01/30/2023 to 02/12/2023.");
            }
        }

        int id = generateStudentId();
        Student student = new Student(Integer.toString(id), lastName, firstName, middleName, address, gender,
                phoneNumber, email, dateOfBirth, course, dateOfEnrollment);
        students.add(student);
        System.out.printf("Student created with ID %s.%n", student.getId());

        // Write the new student data to a CSV file
        String csvFile = "Student/src/students.csv";
        try (FileWriter writer = new FileWriter(csvFile, true)) {
            String[] dateOfBirthArr = dateOfBirth.split("/");
            String formattedDateOfBirth = String.format("%d/%d/%d", Integer.parseInt(dateOfBirthArr[0]),
                    Integer.parseInt(dateOfBirthArr[1]), Integer.parseInt(dateOfBirthArr[2]));
            List<String> studentData = Arrays.asList(student.getId(), lastName, firstName, middleName, address, gender,
                    phoneNumber, email, formattedDateOfBirth, course, dateOfEnrollment);
            String studentDataString = String.join(",", studentData);
            writer.write(studentDataString + "\n");
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error writing student data to file: " + e.getMessage());
        }

        System.out.println("Select an option:");
        System.out.println("\u001B[31m[1] Back\u001B[0m");
        int choice = inputInt("Enter choice: ");
        switch (choice) {
            case 1:
                clearScreen();
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private static int generateStudentId() {
        int id;
        do {
            id = (int) (Math.random() * 9000000) + 1000000; // 7-digit number
        } while (findStudentById(Integer.toString(id)) != null);
        return id;
    }

    private static Student findStudentById(String id) {
        for (Student student : students) {
            if (student.getId().equals(id)) {
                return student;
            }
        }
        return null;
    }

    private static void searchStudent() {
        System.out.println("\u001B[36m[1] Search by ID\u001B[0m");
        System.out.println("\u001B[36m[2] Search by Last Name\u001B[0m");
        System.out.println("\u001B[36m[3] Search by First Name\u001B[0m");
        System.out.println("\u001B[31m[4] Back\u001B[0m");
        int choice = inputInt("Enter choice: ");
        switch (choice) {
            case 1:
                searchStudentById();
                break;
            case 2:
                searchStudentByLastName();
                break;
            case 3:
                searchStudentByFirstName();
                break;
            case 4:
                clearScreen();
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private static void searchStudentById() {
        String id = inputString("Enter student ID: ");
        Student student = findStudentById(id);
        if (student != null) {
            System.out.println(student);
            showStudentSubjects(student);
        } else {
            System.out.println("Student not found.");
        }
    }

    private static void searchStudentByLastName() {
        String lastName = inputString("Enter student last name: ");
        List<Student> matchingStudents = findStudentsByLastName(lastName);
        if (!matchingStudents.isEmpty()) {
            for (Student student : matchingStudents) {
                System.out.println(student);
                showStudentSubjects(student);
            }
        } else {
            System.out.println("No students found.");
        }
    }

    private static List<Student> findStudentsByLastName(String lastName) {
        List<Student> matchingStudents = new ArrayList<>();
        for (Student student : students) {
            if (student.getLastName().equals(lastName)) {
                matchingStudents.add(student);
            }
        }
        return matchingStudents;
    }

    private static void searchStudentByFirstName() {
        String firstName = inputString("Enter student first name: ");
        List<Student> matchingStudents = findStudentsByFirstName(firstName);
        if (!matchingStudents.isEmpty()) {
            for (Student student : matchingStudents) {
                System.out.println(student);
                showStudentSubjects(student);
            }
        } else {
            System.out.println("No students found.");
        }
    }

    private static List<Student> findStudentsByFirstName(String firstName) {
        List<Student> matchingStudents = new ArrayList<>();
        for (Student student : students) {
            if (student.getFirstName().equalsIgnoreCase(firstName)) {
                matchingStudents.add(student);
            }
        }
        return matchingStudents;
    }

    private static void editStudentPersonalDetails(Student student, String id) {
        String lastName = inputStringWithRegex(String.format("Last name [%s]: ", student.getLastName()),
                "^[A-Z][a-zA-Z]*$", true,
                "Invalid input. Last name must contain letters only and should be capitalized. Please try again.");
        student.setLastName(lastName.isBlank() ? student.getLastName() : lastName);

        String firstName = inputStringWithRegex(String.format("First name [%s]: ", student.getFirstName()),
                "^[A-Z][a-zA-Z]*$", true,
                "Invalid input. First name must contain letters only and should be capitalized. Please try again.");
        student.setFirstName(firstName.isBlank() ? student.getFirstName() : firstName);

        String middleName = inputStringWithRegex(String.format("Middle name [%s]: ", student.getMiddleName()),
                "^[A-Z][a-zA-Z]*$", true,
                "Invalid input. Middle name must contain letters only and should be capitalized. Please try again.");
        student.setMiddleName(middleName.isBlank() ? student.getMiddleName() : middleName);

        String address = inputStringWithRegex(String.format("Address [%s]: ", student.getAddress()),
                "^\\d*\\s*[A-Z][a-zA-Z]+\\s[A-Z][a-zA-Z]+\\s[A-Z][a-zA-Z]+$", true,
                "Invalid input. Address must be in the format: [number] [Street name] [City name]. Please try again.");
        student.setAddress(address.isBlank() ? student.getAddress() : address);

        String gender = inputStringWithRegex(String.format("Gender [%s]: ", student.getGender()),
                "^(m|M|male|Male|f|F|female|Female)$", true,
                "Invalid input. Gender must be either 'M', 'F', 'male' or 'female'. Please try again.");
        if (!gender.isBlank()) {
            if (gender.equalsIgnoreCase("m") || gender.equalsIgnoreCase("male")) {
                gender = "M";
            } else {
                gender = "F";
            }
        }
        student.setGender(gender.isBlank() ? student.getGender() : gender);

        String phoneNumber = inputStringWithRegex(String.format("Phone number [%s]: ", student.getPhoneNumber()),
                PHONE_NUMBER_REGEX, true, "Invalid input. Please enter a valid phone number.");
        student.setPhoneNumber(phoneNumber.isBlank() ? student.getPhoneNumber() : phoneNumber);

        String email = inputStringWithRegex(String.format("Email [%s]: ", student.getEmailAddress()), EMAIL_REGEX, true,
                "Invalid input. Please enter a valid email address.");
        student.setEmail(email.isBlank() ? student.getEmailAddress() : email);

        String dateOfBirth = inputStringWithRegex(String.format("Date of Birth [%s]: ", student.getDateOfBirth()),
                DOB_REGEX, true, "Invalid input. Please enter a valid date of birth (MM/DD/YYYY).");
        student.setDateOfBirth(dateOfBirth.isBlank() ? student.getDateOfBirth() : dateOfBirth);

        String course = inputStringWithRegex(String.format("Course [%s]: ", student.getCourse()), "^(BSCS|BSIT|BSCoE)$",
                true, "Invalid input. Please enter a valid course (BSCS, BSIT, BSCoE).");
        student.setCourse(course.isBlank() ? student.getCourse() : course);

        String dateOfEnrollment = inputStringWithRegex(
                String.format("Date of Enrollment [%s]: ", student.getDateOfEnrollment()), ENROLLMENT_DATE_REGEX, true,
                "Invalid input. Please enter a valid date of enrollment (MM/DD/YYYY) between 01/30/2023 and 02/12/2023.");
        if (!dateOfEnrollment.isBlank()) {
            String[] dateParts = dateOfEnrollment.split("/");
            int month = Integer.parseInt(dateParts[0]);
            int day = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);
            dateOfEnrollment = String.format("%d/%d/%d", month, day, year);
        }
        student.setDateOfEnrollment(dateOfEnrollment.isBlank() ? student.getDateOfEnrollment() : dateOfEnrollment);

        updateStudentFile(student);
        System.out.println("Personal details updated.");

        System.out.println("Select an option:");
        System.out.println("\u001B[31m[1] Back\u001B[0m");
        int choice = inputInt("Enter choice: ");
        switch (choice) {
            case 1:
                clearScreen();
                choices(student, id);
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private static String inputStringWithRegex(String prompt, String regex, boolean optional, String errorMessage) {
        while (true) {
            String input = inputString(prompt + (optional ? "(press Enter to skip) " : ""));
            if (input.isBlank()) {
                return ""; // Return empty string when input is blank
            } else if (input.isEmpty() || input.matches(regex)) {
                return input;
            } else {
                System.out.println(errorMessage);
            }
        }
    }

    private static void updateStudentFile(Student student) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(STUDENT_FILE));
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                String[] parts = line.split(",");
                if (parts[0].equals(student.getId())) {
                    String updatedLine = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", student.getId(),
                            student.getLastName(), student.getFirstName(), student.getMiddleName(),
                            student.getAddress(), student.getGender(), student.getPhoneNumber(),
                            student.getEmailAddress(), student.getDateOfBirth(), student.getCourse(),
                            student.getDateOfEnrollment());
                    lines.set(i, updatedLine);
                    Files.write(Paths.get(STUDENT_FILE), lines);
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("Error updating students file: " + e.getMessage());
        }
    }

    private static void addSubject(Student student, String id) {
        String subjectName = inputString("Enter subject name: ");
        String courseNumber;
        String instructorName;
        while (true) {
            courseNumber = inputStringWithRegex("Enter course number (6 digits): ", "^[0-9]{6}$", true,
                    "Invalid input. Course number must be a 6-digit number.");

            // Check if the course number already exists for a different subject for the
            // student
            boolean courseNumberExists = false;
            for (Subject subject : student.getSubjects()) {
                if (subject.getCourseNumber().equals(courseNumber) && !subject.getName().equals(subjectName)) {
                    courseNumberExists = true;
                    break;
                }
            }
            if (!courseNumberExists) {
                break;
            }
            System.out.println(
                    "A subject with this course number already exists for this student. Please enter a different course number.");
        }

        instructorName = inputStringWithRegex("Enter instructor name (2 or more words, capitalized): ",
                "^([A-Z][a-zA-Z]*\\s){1,}[A-Z][a-zA-Z]*$", true,
                "Invalid input. Instructor name must contain letters only, should be capitalized, and should have 2 or more words.");

        Subject subject = new Subject(subjectName, courseNumber, instructorName);
        student.addSubject(subject);
        System.out.printf("Subject %s added.%n", subjectName);

        // Open the subjects file in append mode and write the new subject to it
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SUBJECT_FILE, true));
            writer.write(String.format("%s,%s,%s,%s%n", student.getId(), subjectName, courseNumber, instructorName));
            writer.close();
        } catch (IOException e) {
            System.out.println("Error writing to subjects file.");
        }

        System.out.println("Select an option:");
        System.out.println("\u001B[31m[1] Back\u001B[0m");
        int choice = inputInt("Enter choice: ");
        switch (choice) {
            case 1:
                clearScreen();
                addSubjectChoices(student, id);
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private static void enterGrades(Student student, String id) {
        System.out.println("Enter grades for each subject:");
        try (PrintWriter writer = new PrintWriter(new FileWriter(GRADE_FILE, true))) {
            for (Subject subject : student.getSubjects()) {
                System.out.println(subject.getName() + ":");
                boolean validInput = false;
                while (!validInput) {
                    System.out.println("Enter 1 to edit grades or any key to skip:");
                    String choice = inputString("Choice: ");
                    if (choice.equals("1")) {
                        int prelims = inputInt("Prelims: ");
                        int midterms = inputInt("Midterms: ");
                        int finals = inputInt("Finals: ");
                        double finalRating = computeFinalRating(prelims, midterms, finals);
                        Grade grade = subject.getGrade();
                        if (grade != null) {
                            grade.setPrelims(prelims);
                            grade.setMidterms(midterms);
                            grade.setFinals(finals);
                            grade.setFinalRating(finalRating);

                            // Update the grades file
                            saveGrades();
                        } else {
                            grade = new Grade(prelims, midterms, finals, finalRating);
                            subject.setGrade(grade);

                            // Add new grades to the grades file
                            writer.println(String.format("%s,%s,%d,%d,%d,%.2f",
                                    student.getId(), subject.getName(), prelims, midterms, finals, finalRating));
                            writer.flush();
                        }
                        validInput = true;
                    } else {
                        validInput = true;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing grades file.");
        }
        System.out.println("Grades entered.");

        System.out.println("Select an option:");
        System.out.println("\u001B[31m[1] Back\u001B[0m");
        int choice = inputInt("Enter choice: ");
        switch (choice) {
            case 1:
                clearScreen();
                addSubjectChoices(student, id);
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private static double computeFinalRating(int prelims, int midterms, int finals) {
        return 0.30 * prelims + 0.30 * midterms + 0.40 * finals;
    }

    private static void deleteStudent(Student student, String id) {
        System.out.print("Are you sure you want to delete student " + student.getId() + "? (y/n): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("y")) {
            students.remove(student);
            System.out.println("Student deleted.");
            try {
                List<String> lines = Files.readAllLines(Paths.get(STUDENT_FILE));
                for (int i = 0; i < lines.size(); i++) {
                    if (lines.get(i).startsWith(student.getId() + ",")) {
                        lines.remove(i);
                        break;
                    }
                }
                Files.write(Paths.get(STUDENT_FILE), lines);
            } catch (IOException e) {
                System.out.println("Error deleting student from file.");
            }
            try {
                // Sleep for 1 second
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            clearScreen();
            showStudents();
        } else {
            System.out.println("Student not deleted.");
        }
    }

    private static void loadStudents() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(STUDENT_FILE));
            String line;
            boolean firstLine = true;
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                String[] parts = line.split(",");
                if (parts.length < 8) {
                    System.out.println("Invalid line in students file: " + line);
                    continue;
                }
                String id = parts[0];
                String lastName = parts[1];
                String firstName = parts[2];
                String middleName = parts[3];
                String address = parts[4];
                String gender = parts[5];
                String phoneNumber = parts[6];
                String emailAddress = parts[7];
                String dateOfBirth = parts[8];
                String course = parts[9];
                String dateOfEnrollment = parts[10];
                Student student = new Student(id, lastName, firstName, middleName, address, gender, phoneNumber,
                        emailAddress, dateOfBirth, course, dateOfEnrollment);
                students.add(student);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading students file: " + e.getMessage());
        }
        loadSubjects();
        loadGrades();
    }

    private static void saveStudents() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(STUDENT_FILE));
            writer.write("id,last_name,first_name,middle_name,address,gender,phone_number,email_address");
            writer.newLine();
            for (Student student : students) {
                String line = String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s", student.getId(), student.getLastName(),
                        student.getFirstName(), student.getMiddleName(), student.getAddress(), student.getGender(),
                        student.getPhoneNumber(), student.getEmailAddress(), student.getDateOfBirth(),
                        student.getCourse(), student.getDateOfEnrollment());
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving students file.");
        }
        saveSubjects();
        saveGrades();
    }

    private static void loadSubjects() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(SUBJECT_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    System.out.printf("Invalid line in subjects file: %s%n", line);
                    continue;
                }
                String studentId = parts[0];
                String subjectName = parts[1];
                String courseNumber = parts[2];
                String instructorName = parts[3];
                Student student = findStudentById(studentId);
                if (student != null) {
                    Subject subject = new Subject(subjectName);
                    subject.setCourseNumber(courseNumber);
                    subject.setInstructorName(instructorName);
                    student.addSubject(subject);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading subjects file.");
            e.printStackTrace();
        }
    }

    private static void displaySubjects(String studentId) {
        Student student = findStudentById(studentId);
        if (student == null) {
            System.out.printf("Student with ID %s not found.%n", studentId);
            choices(student, studentId);
        }

        List<Subject> subjects = student.getSubjects();
        if (subjects.isEmpty()) {
            System.out.printf("Student with ID %s has no subjects.%n", studentId);
        }

        int subjectWidth = 30;
        int courseNumberWidth = 12;
        int instructorWidth = 20;
        int prelimWidth = 8;
        int midtermWidth = 8;
        int finalWidth = 8;
        int ratingWidth = 12;

        // Print table headers
        System.out.format("+%s+%s+%s+%s+%s+%s+%s+\n",
                "-".repeat(subjectWidth + 2), "-".repeat(courseNumberWidth + 2), "-".repeat(instructorWidth + 2),
                "-".repeat(prelimWidth + 2), "-".repeat(midtermWidth + 2), "-".repeat(finalWidth + 2),
                "-".repeat(ratingWidth + 2));
        System.out.format("| %-" + subjectWidth + "s | %-" + courseNumberWidth + "s | %-" + instructorWidth + "s | %-"
                + prelimWidth + "s | %-" + midtermWidth + "s | %-" + finalWidth + "s | %-" + ratingWidth + "s |\n",
                "Subject", "Course No.", "Instructor", "Prelim", "Midterm", "Final", "Final Rating");
        System.out.format("+%s+%s+%s+%s+%s+%s+%s+\n",
                "-".repeat(subjectWidth + 2), "-".repeat(courseNumberWidth + 2), "-".repeat(instructorWidth + 2),
                "-".repeat(prelimWidth + 2), "-".repeat(midtermWidth + 2), "-".repeat(finalWidth + 2),
                "-".repeat(ratingWidth + 2));

        // Print subject info
        for (Subject subject : subjects) {
            Grade grade = subject.getGrade();
            if (grade != null) {
                int prelims = grade.getPrelims();
                int midterms = grade.getMidterms();
                int finals = grade.getFinals();
                double finalRating = computeFinalRating(prelims, midterms, finals);
                System.out.format(
                        "| %-" + subjectWidth + "s | %-" + courseNumberWidth + "s | %-" + instructorWidth + "s | %-"
                                + prelimWidth + "d | %-" + midtermWidth + "d | %-" + finalWidth + "d | %-" + ratingWidth
                                + ".2f |\n",
                        subject.getName(), subject.getCourseNumber(), subject.getInstructorName(), prelims, midterms,
                        finals, finalRating);
            } else {
                System.out.format(
                        "| %-" + subjectWidth + "s | %-" + courseNumberWidth + "s | %-" + instructorWidth + "s | %-"
                                + prelimWidth + "s | %-" + midtermWidth + "s | %-" + finalWidth + "s | %-" + ratingWidth
                                + "s |\n",
                        subject.getName(), subject.getCourseNumber(), subject.getInstructorName(), "-", "-", "-", "-");
            }
        }

        System.out.format("+%s+%s+%s+%s+%s+%s+%s+\n",
                "-".repeat(subjectWidth + 2), "-".repeat(courseNumberWidth + 2), "-".repeat(instructorWidth + 2),
                "-".repeat(prelimWidth + 2), "-".repeat(midtermWidth + 2), "-".repeat(finalWidth + 2),
                "-".repeat(ratingWidth + 2));

        addSubjectChoices(student, studentId);
    }

    private static void addSubjectChoices(Student student, String id) {
        // Print menu options
        System.out.println("Select an option:");
        System.out.println("\u001B[36m[1] Add subject\u001B[0m");
        System.out.println("\u001B[36m[2] Enter grades\u001B[0m");
        System.out.println("\u001B[36m[3] Delete subject\u001B[0m");
        System.out.println("\u001B[31m[4] Back\u001B[0m");
        int choice = inputInt("Enter choice: ");
        switch (choice) {
            case 1:
                addSubject(student, id);
                break;
            case 2:
                enterGrades(student, id);
                break;
            case 3:
                deleteSubject(student, id);
                break;
            case 4:
                clearScreen();
                choices(student, id);
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }

    }

    private static void deleteSubject(Student student, String id) {
        List<Subject> subjects = student.getSubjects();
        if (subjects.isEmpty()) {
            System.out.println("No subjects to delete.");
            return;
        }

        System.out.println("Select subject to delete:");
        for (int i = 0; i < subjects.size(); i++) {
            System.out.printf("[%d] %s\n", i + 1, subjects.get(i).getName());
        }
        int choice = inputInt("Enter choice: ");
        if (choice < 1 || choice > subjects.size()) {
            System.out.println("Invalid choice. Try again.");
            return;
        }

        Subject subject = subjects.get(choice - 1);
        subjects.remove(subject);

        // Delete grades from grades.csv file
        try {
            BufferedReader reader = new BufferedReader(new FileReader(GRADE_FILE));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                String studentId = parts[0];
                String subjectName = parts[1];
                if (!studentId.equals(student.getId()) || !subjectName.equals(subject.getName())) {
                    output.append(line).append("\n");
                }
            }
            reader.close();

            BufferedWriter writer = new BufferedWriter(new FileWriter(GRADE_FILE));
            writer.write(output.toString());
            writer.close();
        } catch (IOException e) {
            System.out.println("Error deleting grades file.");
        }
        saveStudents();
        System.out.printf("%s has been deleted.\n", subject.getName());
        System.out.println("Select an option:");
        System.out.println("\u001B[31m[1] Back\u001B[0m");
        int secondChoice = inputInt("Enter choice: ");
        switch (secondChoice) {
            case 1:
                clearScreen();
                addSubjectChoices(student, id);
                break;
            default:
                System.out.println("Invalid choice. Try again.");
        }
    }

    private static void loadGrades() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(GRADE_FILE));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                if (parts.length < 6) {
                    System.out.println("Invalid line format in grades.csv: " + line);
                    continue;
                }
                String studentId = parts[0];
                String subjectName = parts[1];
                int prelims = Integer.parseInt(parts[2]);
                int midterms = Integer.parseInt(parts[3]);
                int finals = Integer.parseInt(parts[4]);
                double finalRating = Double.parseDouble(parts[5]);
                Student student = findStudentById(studentId);
                if (student != null) {
                    Subject subject = student.getSubject(subjectName);
                    if (subject != null) {
                        Grade grade = new Grade(prelims, midterms, finals, finalRating);
                        subject.setGrade(grade);
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("Error loading grades file.");
        }
    }

    private static void saveSubjects() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(SUBJECT_FILE));
            for (Student student : students) {
                for (Subject subject : student.getSubjects()) {
                    String line = String.format("%s,%s,%s,%s", student.getId(), subject.getName(),
                            subject.getCourseNumber(), subject.getInstructorName());
                    writer.write(line);
                    writer.newLine();
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving subjects file.");
        }
    }

    private static void saveGrades() {
        try {
            List<String> newLines = new ArrayList<>();
            List<String> existingLines = Files.readAllLines(Paths.get(GRADE_FILE));
            // Iterate over all students and their subjects
            for (Student student : students) {
                for (Subject subject : student.getSubjects()) {
                    // Check if this subject already has a grade in the file
                    boolean found = false;
                    for (int i = 0; i < existingLines.size(); i++) {
                        String line = existingLines.get(i);
                        String[] parts = line.split(",");
                        if (parts[0].equals(student.getId()) && parts[1].equals(subject.getName())) {
                            // Found an existing grade for this subject, update it
                            line = String.format("%s,%s,%d,%d,%d,%.2f", student.getId(), subject.getName(),
                                    subject.getGrade().getPrelims(), subject.getGrade().getMidterms(),
                                    subject.getGrade().getFinals(), subject.getGrade().getFinalRating());
                            existingLines.set(i, line);
                            found = true;
                            break;
                        }
                    }
                    // If no existing grade was found, add a new line to the list of new lines
                    if (!found && subject.getGrade() != null) {
                        String line = String.format("%s,%s,%d,%d,%d,%.2f", student.getId(), subject.getName(),
                                subject.getGrade().getPrelims(), subject.getGrade().getMidterms(),
                                subject.getGrade().getFinals(), subject.getGrade().getFinalRating());
                        newLines.add(line);
                    }
                }
            }

            // Write all the new lines to the end of the existing file
            BufferedWriter writer = new BufferedWriter(new FileWriter(GRADE_FILE, true));
            for (String line : newLines) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();

            // Overwrite the existing file with the updated lines
            writer = new BufferedWriter(new FileWriter(GRADE_FILE));
            for (String line : existingLines) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving grades file.");
        }
    }
}