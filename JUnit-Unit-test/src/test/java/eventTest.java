import com.inflectra.spiratest.addons.junitextension.SpiraTestCase;
import com.inflectra.spiratest.addons.junitextension.SpiraTestConfiguration;
import org.example.BasicData;
import org.example.Event;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpiraTestConfiguration(
//following are REQUIRED
        url = "https://rmit.spiraservice.net/",
        login = "s3904418",
        rssToken = "{7D805319-92EA-4A0F-B919-EBA1C365EBAB}",
        projectId = 42
)
class evenTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final File temporaryFile = new File("temp_student.txt");
    Event event;

    @BeforeEach
    public void setupEach() {
        this.event = new Event();
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    void tearDown() {
        if (temporaryFile.exists()) {
            temporaryFile.delete();
        }
        System.setOut(System.out);
    }

    @Test
    @DisplayName("Rewrite student file")
    @SpiraTestCase(testCaseId = 1379)

    void testRewriteStudentFile() throws IOException {
        // Create some sample student data
        //Id:7654324, Name:Student1, Password:p7654324#
        //Id:7654325, Name:Student2, Password:p7654325#
        //Id:7654326, Name:Student3, Password:p7654326#
        //Id:7654327, Name:Student4, Password:p7654327#
        //Id:7654329, Name:Student6, Password:p7654329#
        //Id:7654330, Name:Student7, Password:p7654330#
        //Id:7654331, Name:Student8, Password:p7654331#
        //Id:7654332, Name:Student9, Password:p7654332#

        ArrayList<BasicData> students = new ArrayList<>();

        students.add(new BasicData (3904418, "TestStewdent1","p3904418#"));
        Event.rewriteStudentFile();
        // Create a temporary copy of the student.txt file
        File originalStudentFile = new File("student.txt");
        File temporaryFile = new File("temp_student.txt");
        Files.copy(originalStudentFile.toPath(), temporaryFile.toPath());

        // Read file
        List<String> fileLines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(temporaryFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileLines.add(line);
            }
        }

        //delete the comment for testing other student data
//        assertTrue(fileLines.contains("Id:7654324, Name:Student1, Password:p7654324#"));
//        assertTrue(fileLines.contains("Id:7654325, Name:Student2, Password:p7654325#"));
//        assertTrue(fileLines.contains("Id:7654326, Name:Student3, Password:p7654326#"));
//        assertTrue(fileLines.contains("Id:7654330, Name:Student7, Password:p7654330#"));
        assertTrue(fileLines.contains("Id:3904418, Name:TestStewdent1, Password:p3904418#"));
    }




    //add student test
    @Test
    @DisplayName("Test Adding Student with 2 kind of conditions")
    @SpiraTestCase(testCaseId = 1378)
    public void testAddStudent() {
        System.setIn(new ByteArrayInputStream("491315\nstudentname2\np1234569#\n".getBytes()));
        System.setIn(System.in);
        assertAll("Add Student",
                () -> {
                    String result = event.AddStudent();
                    assertEquals("Student Added Successfully", result);
                },

                () ->
                {
                    boolean studentFound = event.searchStudentDetails(491315);
                    assertTrue(studentFound, "Added student shall be found in the student list");
                }


        );
    }

    //invalid password test when adding new student
    @Test
    @DisplayName("Test Adding Student with different type of conditions")
    @SpiraTestCase(testCaseId = 1377)
    public void testAddStudentInvalidPassword() {
        assertAll("Add student with wrong required password",
                () -> {
                    System.setIn(new ByteArrayInputStream("3904418\npasswordInvalid\ninvalid9#\n".getBytes()));
                    String result = event.AddStudent();
                    assertTrue(result.contains("First letter of the Password should be p"), "First letter of the Password should be p");
                    System.setIn(System.in);
                },
                () -> {
                    System.setIn(new ByteArrayInputStream("3904418\npasswordInvalid\npinvalid1\n".getBytes()));
                    String result = event.AddStudent();
                    assertTrue(result.contains("Last letter of the password should be #"), "Last letter of the password should be #");
                    System.setIn(System.in);
                },
                () -> {
                    System.setIn(new ByteArrayInputStream("3904418\npasswordInvalid\n123456\n".getBytes()));
                    String result = event.AddStudent();
                    assertTrue(result.contains("Password length should be 9"), "Password length should be 9");
                    System.setIn(System.in);
                }
        );

    }


    //login test

    @Test
    @DisplayName("Testing Admin Login")
    @SpiraTestCase(testCaseId = 1376)
    public void testAdminLogin() {
        assertAll("Admin Login",
                () -> {
                    ByteArrayInputStream success = new ByteArrayInputStream("Admin3\npass3\n".getBytes());
                    System.setIn(success);
                    assertTrue(event.AdminLogin(), "Login succeed");
                },
                () -> {
                    ByteArrayInputStream failure = new ByteArrayInputStream("Admin3\npass4\n".getBytes());
                    System.setIn(failure);
                    assertFalse(event.AdminLogin(), "Login failed");
                }
        );
    }

    @Test
    @DisplayName("Testing Student Login")
    @SpiraTestCase(testCaseId = 713)
    public void testStudentLogin() {
        assertAll("Student Login",
                () -> {
                    ByteArrayInputStream success = new ByteArrayInputStream("7654332\np7654332#\n".getBytes());
                    System.setIn(success);
                    assertTrue(event.StudentLogin(),"Login succeed");
                },
                () -> {
                    ByteArrayInputStream failure = new ByteArrayInputStream("7654332\n7654332\n".getBytes());
                    System.setIn(failure);
                    assertFalse(event.StudentLogin(), "Login failed");
                }
        );

    }

    //exist student test
    @Test
    @DisplayName("Testing Search Student with 2 conditions")
    @SpiraTestCase(testCaseId = 1380)
    public void testSearchStudentDetails() {
        assertAll("Search Student",
                () -> {
                    assertTrue(event.searchStudentDetails(7654332)); //exist student
                },
                () -> {
                    assertFalse(event.searchStudentDetails(999999)); //non exist
                }
        );

    }

    //test remove exist student
    @Test
    @DisplayName("Testing Remove Student with 2 conditions")
    @SpiraTestCase(testCaseId = 1381)

    public void testRemoveStudent() {
        assertAll("Remove Student",
                () -> {
                    assertTrue(event.removeStudent(491315)); //exist student
                },
                () -> {
                    assertFalse(event.removeStudent(888888)); //non exist
                }
        );

    }

    @Test
    @DisplayName("Testing View Student ")
    @SpiraTestCase(testCaseId = 1382)
    //@SpiraTestCase(testCaseId = 1579)

    public void testViewStudentDetails() {
        assertAll("View student data",
                () -> {
                    assertNotNull(outContent.toString()); //check if the data is null
                },
                () -> assertTrue(event.viewStudentDetails())
        );
    }

    @Test
    @DisplayName("Testing Student's event failure")
    @SpiraTestCase(testCaseId = 1383)
    public void testShowStudentEventsFail() {
        // suppose to be failed
        event.showStudentEvents();
        String expected = "Nothing is here";
        assertEquals(expected, outContent.toString());
    }
}

