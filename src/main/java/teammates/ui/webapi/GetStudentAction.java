package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.api.CoursesLogicAPI;
import teammates.ui.output.StudentData;

/**
 * Get the information of a student inside a course.
 */
class GetStudentAction extends Action {

    /** Message indicating that a student not found. */
    static final String STUDENT_NOT_FOUND = "No student found";

    /** String indicating ACCESS is not given. */
    private static final String UNAUTHORIZED_ACCESS = "You are not allowed to view this resource!";

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes course = coursesLogic.getCourse(courseId);

        StudentAttributes student;

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String regKey = getRequestParamValue(Const.ParamsNames.REGKEY);

        if (studentEmail != null) {
            student = studentsLogic.getStudentForEmail(courseId, studentEmail);
            if (student == null || userInfo == null || !userInfo.isInstructor) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }

            InstructorAttributes instructor = instructorsLogic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(instructor, coursesLogic.getCourse(courseId), student.getSection(),
                    Const.InstructorPermissions.CAN_VIEW_STUDENT_IN_SECTIONS);
        } else if (regKey != null) {
            getUnregisteredStudent().orElseThrow(() -> new UnauthorizedAccessException(UNAUTHORIZED_ACCESS));
        } else {
            if (userInfo == null || !userInfo.isStudent) {
                throw new UnauthorizedAccessException(UNAUTHORIZED_ACCESS);
            }

            student = studentsLogic.getStudentForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(student, course);
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        StudentAttributes student;

        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        if (studentEmail == null) {
            student = getPossiblyUnregisteredStudent(courseId);
        } else {
            student = studentsLogic.getStudentForEmail(courseId, studentEmail);
        }

        if (student == null) {
            throw new EntityNotFoundException(STUDENT_NOT_FOUND);
        }

        StudentData studentData = new StudentData(student);
        if (userInfo != null && userInfo.isAdmin) {
            studentData.setKey(student.getKey());
            studentData.setGoogleId(student.getGoogleId());
        }

        if (studentEmail == null) {
            // hide information if not an instructor
            studentData.hideInformationForStudent();
            // add student institute
            studentData.setInstitute(coursesLogic.getCourseInstitute(courseId));
        }

        return new JsonResult(studentData);
    }
}
