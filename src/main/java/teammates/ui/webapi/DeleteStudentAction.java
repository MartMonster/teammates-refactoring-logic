package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.api.CoursesLogicAPI;

/**
 * Action: deletes a student from a course.
 */
class DeleteStudentAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (userInfo.isAdmin) {
            return;
        }

        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructor = instructorsLogic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, coursesLogic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentId = getRequestParamValue(Const.ParamsNames.STUDENT_ID);

        String studentEmail = null;
        if (studentId == null) {
            studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        } else {
            StudentAttributes student = studentsLogic.getStudentForGoogleId(courseId, studentId);
            if (student != null) {
                studentEmail = student.getEmail();
            }
        }

        // if student is not found, fail silently
        if (studentEmail != null) {
            studentsLogic.deleteStudentCascade(courseId, studentEmail);
        }

        return new JsonResult("Student is successfully deleted.");
    }

}
