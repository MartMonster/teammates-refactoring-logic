package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.AccountsLogicAPI;
import teammates.logic.api.InstructorsLogicAPI;

/**
 * Action: resets an account ID.
 */
class ResetAccountAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        if (studentEmail == null && instructorEmail == null) {
            throw new InvalidHttpParameterException("Either student email or instructor email has to be specified.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String wrongGoogleId = null;
        if (studentEmail != null) {
            StudentAttributes existingStudent = studentsLogic.getStudentForEmail(courseId, studentEmail);
            if (existingStudent == null) {
                throw new EntityNotFoundException("Student does not exist.");
            }
            wrongGoogleId = existingStudent.getGoogleId();

            try {
                studentsLogic.resetStudentGoogleId(studentEmail, courseId);
                taskQueuer.scheduleCourseRegistrationInviteToStudent(courseId, studentEmail, true);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        } else if (instructorEmail != null) {
            InstructorAttributes existingInstructor = instructorsLogic.getInstructorForEmail(courseId, instructorEmail);
            if (existingInstructor == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }
            wrongGoogleId = existingInstructor.getGoogleId();

            try {
                instructorsLogic.resetInstructorGoogleId(instructorEmail, courseId);
                taskQueuer.scheduleCourseRegistrationInviteToInstructor(null, instructorEmail, courseId, true);
            } catch (EntityDoesNotExistException e) {
                throw new EntityNotFoundException(e);
            }
        }

        if (wrongGoogleId != null
                && studentsLogic.getStudentsForGoogleId(wrongGoogleId).isEmpty()
                && instructorsLogic.getInstructorsForGoogleId(wrongGoogleId).isEmpty()) {
            accountsLogic.deleteAccountCascade(wrongGoogleId);
        }

        return new JsonResult("Account is successfully reset.");
    }

}
