package teammates.logic.api;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.*;
import teammates.logic.core.StudentsLogic;

import java.util.Collection;
import java.util.List;

public class StudentsLogicAPI {
    private static final StudentsLogicAPI instance = new StudentsLogicAPI();
    final StudentsLogic studentsLogic = StudentsLogic.inst();
    
    StudentsLogicAPI() {
        // prevent initialization
    }
    
    public static StudentsLogicAPI inst() {
        return instance;
    }

    /**
     * Search for students. Preconditions: all parameters are non-null.
     * @param instructors   a list of InstructorAttributes associated to a googleId,
     *                      used for filtering of search result
     * @return Null if no match found
     */
    public List<StudentAttributes> searchStudents(String queryString, List<InstructorAttributes> instructors)
            throws SearchServiceException {
        assert queryString != null;
        assert instructors != null;
        return studentsLogic.searchStudents(queryString, instructors);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @return Null if no match found.
     */
    public List<StudentAttributes> searchStudentsInWholeSystem(String queryString)
            throws SearchServiceException {
        assert queryString != null;

        return studentsLogic.searchStudentsInWholeSystem(queryString);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForRegistrationKey(String registrationKey) {
        assert registrationKey != null;
        return studentsLogic.getStudentForRegistrationKey(registrationKey);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForEmail(String courseId, String email) {
        assert courseId != null;
        assert email != null;

        return studentsLogic.getStudentForEmail(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
        assert courseId != null;
        assert googleId != null;

        return studentsLogic.getStudentForCourseIdAndGoogleId(courseId, googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Empty list if no match found.
     */
    public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
        assert googleId != null;
        return studentsLogic.getStudentsForGoogleId(googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        assert courseId != null;
        return studentsLogic.getStudentsForCourse(courseId);
    }

    /**
     * Resets the googleId associated with the student.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void resetStudentGoogleId(String originalEmail, String courseId) throws EntityDoesNotExistException {
        assert originalEmail != null;
        assert courseId != null;

        studentsLogic.resetStudentGoogleId(originalEmail, courseId);
    }

    /**
     * Regenerates the registration key for the student with email address {@code email} in course {@code courseId}.
     *
     * @return the student attributes with the new registration key.
     * @throws EntityAlreadyExistsException if the newly generated course student has the same registration key as the
     *          original one.
     * @throws EntityDoesNotExistException if the student does not exist.
     */
    public StudentAttributes regenerateStudentRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {

        assert courseId != null;
        assert email != null;

        return studentsLogic.regenerateStudentRegistrationKey(courseId, email);
    }

    /**
     * Creates a student.
     *
     * @return the created student.
     * @throws InvalidParametersException if the student is not valid.
     * @throws EntityAlreadyExistsException if the student already exists in the database.
     */
    public StudentAttributes createStudent(StudentAttributes student)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert student.getCourse() != null;
        assert student.getEmail() != null;

        return studentsLogic.createStudent(student);
    }

    /**
     * Updates a student by {@link StudentAttributes.UpdateOptions}.
     *
     * <p>If email changed, update by recreating the student and cascade update all responses
     * the student gives/receives as well as any deadline extensions given to the student.
     *
     * <p>If team changed, cascade delete all responses the student gives/receives within that team.
     *
     * <p>If section changed, cascade update all responses the student gives/receives.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated student
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the student cannot be found
     * @throws EntityAlreadyExistsException if the student cannot be updated
     *         by recreation because of an existent student
     */
    public StudentAttributes updateStudentCascade(StudentAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        assert updateOptions != null;

        return studentsLogic.updateStudentCascade(updateOptions);
    }

    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        assert courseId != null;
        return studentsLogic.getUnregisteredStudentsForCourse(courseId);
    }

    /**
     * Deletes a student cascade its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteStudentCascade(String courseId, String studentEmail) {
        assert courseId != null;
        assert studentEmail != null;

        studentsLogic.deleteStudentCascade(courseId, studentEmail);
    }

    /**
     * Deletes all the students in the course cascade their associated responses, deadline extensions and comments.
     *
     * <br/>Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteStudentsInCourseCascade(String courseId, int batchSize) {
        assert courseId != null;

        studentsLogic.deleteStudentsInCourseCascade(courseId, batchSize);
    }

    /**
     * Validates sections for any limit violations and teams for any team name violations.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see StudentsLogic#validateSectionsAndTeams(List, String)
     */
    public void validateSectionsAndTeams(List<StudentAttributes> studentList, String courseId) throws EnrollException {

        assert studentList != null;
        assert courseId != null;

        studentsLogic.validateSectionsAndTeams(studentList, courseId);
    }

    /**
     * Gets all students of a team.
     */
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        assert teamName != null;
        assert courseId != null;

        return studentsLogic.getStudentsForTeam(teamName, courseId);
    }

    /**
     * Creates or updates search document for the given student.
     *
     * @see StudentsLogic#putDocument(StudentAttributes)
     */
    public void putStudentDocument(StudentAttributes student) throws SearchServiceException {
        studentsLogic.putDocument(student);
    }

    public String getSectionForTeam(String courseId, String teamName) {
        assert courseId != null;
        assert teamName != null;
        return studentsLogic.getSectionForTeam(courseId, teamName);
    }

    /**
     * Verifies that all the given students exist in the given course.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @throws EntityDoesNotExistException If some student does not exist in the course.
     */
    public void verifyAllStudentsExistInCourse(String courseId, Collection<String> studentEmailAddresses)
            throws EntityDoesNotExistException {
        assert courseId != null;
        assert studentEmailAddresses != null;

        studentsLogic.verifyAllStudentsExistInCourse(courseId, studentEmailAddresses);
    }

    public boolean isStudentsInSameTeam(String courseId, String student1Email, String student2Email) {
        assert courseId != null;
        assert student1Email != null;
        assert student2Email != null;
        return studentsLogic.isStudentsInSameTeam(courseId, student1Email, student2Email);
    }
}
