import java.util.*;

class Student {
    private String id;
    private String lastName;
    private String firstName;
    private String middleName;
    private String address;
    private String gender;
    private String phoneNumber;
    private String emailAddress;
    private String dateOfBirth;
    private String course;
    private String dateOfEnrollment;
    private List<Subject> subjects;

    public Student(String id, String lastName, String firstName, String middleName, String address, String gender,
            String phoneNumber, String emailAddress, String dateOfBirth, String course, String dateOfEnrollment) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.address = address;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.dateOfBirth = dateOfBirth;
        this.course = course;
        this.dateOfEnrollment = dateOfEnrollment;
        this.subjects = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCourse() {
        return course;
    }

    public String getDateOfEnrollment() {
        return dateOfEnrollment;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public Subject getSubject(String name) {
        for (Subject subject : subjects) {
            if (subject.getName().equals(name)) {
                return subject;
            }
        }
        return null;
    }

    public void addSubject(Subject subject) {
        subjects.add(subject);
    }

    public void removeSubject(Subject subject) {
        subjects.remove(subject);
    }

    public void inputGrades(String subjectName, double prelims, double midterms, double finals) {
        for (Subject subject : subjects) {
            if (subject.getName().equals(subjectName)) {
                subject.setPrelims(prelims);
                subject.setMidterms(midterms);
                subject.setFinals(finals);
                double finalRating = (0.30 * prelims) + (0.30 * midterms) + (0.40 * finals);
                subject.setFinalRating(finalRating);
                break;
            }
        }
    }

    public void displaySubjects() {
        System.out.println("Subjects:");
        for (Subject subject : subjects) {
            System.out.printf("%s - Prelims: %.2f, Midterms: %.2f, Finals: %.2f, Final Rating: %.2f\n",
                    subject.getName(), subject.getPrelims(), subject.getMidterms(), subject.getFinals(),
                    subject.getFinalRating());
        }
    }

    @Override
    public String toString() {
        return "ID: " + id + "\n" +
                "Last Name: " + lastName + "\n" +
                "First Name: " + firstName + "\n" +
                "Middle Name: " + middleName + "\n" +
                "Address: " + address + "\n" +
                "Gender: " + gender + "\n" +
                "Phone Number: " + phoneNumber + "\n" +
                "Email Address: " + emailAddress + "\n" +
                "Date of Birth: " + dateOfBirth + "\n" +
                "Course: " + course + "\n" +
                "Date of Enrollment: " + dateOfEnrollment;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setEmail(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public void setDateOfEnrollment(String dateOfEnrollment) {
        this.dateOfEnrollment = dateOfEnrollment;
    }

    public Subject getSubjectByCourseNumber(String courseNumber) {
        for (Subject subject : subjects) {
            if (subject.getCourseNumber().equals(courseNumber)) {
                return subject;
            }
        }
        return null;
    }

}

class Subject {
    private String name;
    private String courseNumber;
    private String instructorName;
    private double prelims;
    private double midterms;
    private double finals;
    private double finalRating;
    private Grade grade;

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public Subject(String name, String courseNumber, String instructorName) {
        this.name = name;
        this.courseNumber = courseNumber;
        this.instructorName = instructorName;
    }

    public Grade getGrade() {
        return grade;
    }

    public Subject(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public double getPrelims() {
        return prelims;
    }

    public void setPrelims(double prelims) {
        this.prelims = prelims;
    }

    public double getMidterms() {
        return midterms;
    }

    public void setMidterms(double midterms) {
        this.midterms = midterms;
    }

    public double getFinals() {
        return finals;
    }

    public void setFinals(double finals) {
        this.finals = finals;
    }

    public double getFinalRating() {
        return finalRating;
    }

    public void setFinalRating(double finalRating) {
        this.finalRating = finalRating;
    }

    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    @Override
    public String toString() {
        return name;
    }
}

class Grade {
    private int prelims;
    private int midterms;
    private int finals;
    private double finalRating;

    public Grade(int prelims, int midterms, int finals) {
        this.prelims = prelims;
        this.midterms = midterms;
        this.finals = finals;
        this.finalRating = computeFinalRating(prelims, midterms, finals);
    }

    public Grade(int prelims, int midterms, int finals, double finalRating) {
        this.prelims = prelims;
        this.midterms = midterms;
        this.finals = finals;
        this.finalRating = finalRating;
    }

    public int getPrelims() {
        return prelims;
    }

    public int getMidterms() {
        return midterms;
    }

    public int getFinals() {
        return finals;
    }

    public double getFinalRating() {
        return finalRating;
    }

    public void setPrelims(int prelims) {
        this.prelims = prelims;
    }

    public void setMidterms(int midterms) {
        this.midterms = midterms;
    }

    public void setFinals(int finals) {
        this.finals = finals;
    }

    public void setFinalRating(double finalRating) {
        this.finalRating = finalRating;
    }

    private double computeFinalRating(int prelims, int midterms, int finals) {
        double weightedPrelims = prelims * 0.30;
        double weightedMidterms = midterms * 0.30;
        double weightedFinals = finals * 0.40;
        return weightedPrelims + weightedMidterms + weightedFinals;
    }
}