package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.api.CoursesLogicAPI;
import teammates.ui.output.FeedbackSessionData;
import teammates.ui.output.FeedbackSessionsData;

/**
 * Get a list of feedback sessions.
 */
class GetFeedbackSessionsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (userInfo.isAdmin) {
            return;
        }

        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (!(entityType.equals(Const.EntityType.STUDENT) || entityType.equals(Const.EntityType.INSTRUCTOR))) {
            throw new UnauthorizedAccessException("entity type not supported.");
        }

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (entityType.equals(Const.EntityType.STUDENT)) {
            if (!userInfo.isStudent) {
                throw new UnauthorizedAccessException("User " + userInfo.getId()
                        + " does not have student privileges");
            }

            if (courseId != null) {
                CourseAttributes courseAttributes = coursesLogic.getCourse(courseId);
                gateKeeper.verifyAccessible(studentsLogic.getStudentForGoogleId(courseId, userInfo.getId()), courseAttributes);
            }
        } else {
            if (!userInfo.isInstructor) {
                throw new UnauthorizedAccessException("User " + userInfo.getId()
                        + " does not have instructor privileges");
            }

            if (courseId != null) {
                CourseAttributes courseAttributes = coursesLogic.getCourse(courseId);
                gateKeeper.verifyAccessible(instructorsLogic.getInstructorForGoogleId(courseId, userInfo.getId()), courseAttributes);
            }
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        List<FeedbackSessionAttributes> feedbackSessionAttributes;
        List<InstructorAttributes> instructors = new ArrayList<>();

        if (courseId == null) {
            if (entityType.equals(Const.EntityType.STUDENT)) {
                List<StudentAttributes> students = studentsLogic.getStudentsForGoogleId(userInfo.getId());
                feedbackSessionAttributes = new ArrayList<>();
                for (StudentAttributes student : students) {
                    String studentCourseId = student.getCourse();
                    String emailAddress = student.getEmail();
                    List<FeedbackSessionAttributes> sessions = feedbackSessionsLogic.getFeedbackSessionsForCourse(studentCourseId);

                    sessions = sessions.stream()
                        .map(session -> session.getCopyForStudent(emailAddress))
                        .collect(Collectors.toList());

                    feedbackSessionAttributes.addAll(sessions);
                }
            } else if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
                boolean isInRecycleBin = getBooleanRequestParamValue(Const.ParamsNames.IS_IN_RECYCLE_BIN);

                instructors = instructorsLogic.getInstructorsForGoogleId(userInfo.getId(), true);

                if (isInRecycleBin) {
                    feedbackSessionAttributes = feedbackSessionsLogic.getSoftDeletedFeedbackSessionsListForInstructors(instructors);
                } else {
                    feedbackSessionAttributes = feedbackSessionsLogic.getFeedbackSessionsListForInstructor(instructors);
                }
            } else {
                feedbackSessionAttributes = new ArrayList<>();
            }
        } else {
            feedbackSessionAttributes = feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
            if (entityType.equals(Const.EntityType.STUDENT) && !feedbackSessionAttributes.isEmpty()) {
                StudentAttributes student = studentsLogic.getStudentForGoogleId(courseId, userInfo.getId());
                assert student != null;
                String emailAddress = student.getEmail();
                feedbackSessionAttributes = feedbackSessionAttributes.stream()
                        .map(instructorSession -> instructorSession.getCopyForStudent(emailAddress))
                        .collect(Collectors.toList());
            } else if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
                instructors = Collections.singletonList(instructorsLogic.getInstructorForGoogleId(courseId, userInfo.getId()));
            }
        }

        if (entityType.equals(Const.EntityType.STUDENT)) {
            // hide session not visible to student
            feedbackSessionAttributes = feedbackSessionAttributes.stream()
                    .filter(FeedbackSessionAttributes::isVisible).collect(Collectors.toList());
        }

        Map<String, InstructorAttributes> courseIdToInstructor = new HashMap<>();
        instructors.forEach(instructor -> courseIdToInstructor.put(instructor.getCourseId(), instructor));

        FeedbackSessionsData responseData = new FeedbackSessionsData(feedbackSessionAttributes);
        if (entityType.equals(Const.EntityType.STUDENT)) {
            responseData.getFeedbackSessions().forEach(FeedbackSessionData::hideInformationForStudent);
        } else if (entityType.equals(Const.EntityType.INSTRUCTOR)) {
            responseData.getFeedbackSessions().forEach(session -> {
                InstructorAttributes instructor = courseIdToInstructor.get(session.getCourseId());
                if (instructor == null) {
                    return;
                }

                InstructorPermissionSet privilege =
                        constructInstructorPrivileges(instructor, session.getFeedbackSessionName());
                session.setPrivileges(privilege);
            });
        }
        return new JsonResult(responseData);
    }

}
