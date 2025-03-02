package teammates.ui.webapi;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelperExtension;
import teammates.logic.api.CoursesLogicAPI;
import teammates.logic.api.FeedbackSessionsLogicAPI;
import teammates.logic.api.InstructorsLogicAPI;
import teammates.logic.api.StudentsLogicAPI;

/**
 * SUT: {@link CreateAccountAction}.
 */
public class CreateAccountActionTest extends BaseActionTest<CreateAccountAction> {
    private final FeedbackSessionsLogicAPI feedbackSessionsLogic = FeedbackSessionsLogicAPI.inst();
    private final StudentsLogicAPI studentsLogic = StudentsLogicAPI.inst();
    private final InstructorsLogicAPI instructorsLogic = InstructorsLogicAPI.inst();
    private final CoursesLogicAPI coursesLogic = CoursesLogicAPI.inst();

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.ACCOUNT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Override
    @Test
    protected void testExecute() {
        String name = "Unregistered Instructor 1";
        String email = "unregisteredinstructor1@gmail.tmt";
        String institute = "TEAMMATES Test Institute 1";

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Null parameters");

        String[] nullParams = new String[] { Const.ParamsNames.REGKEY, null, };
        InvalidHttpParameterException ex = verifyHttpParameterFailure(nullParams);
        assertEquals("The [key] HTTP parameter is null.", ex.getMessage());

        verifyNoTasksAdded();

        ______TS("Normal case with valid timezone");
        String timezone = "Asia/Singapore";
        AccountRequestAttributes accountRequest = logic.getAccountRequest(email, institute);

        String[] params = new String[] {
                Const.ParamsNames.REGKEY, accountRequest.getRegistrationKey(),
                Const.ParamsNames.TIMEZONE, timezone,
        };
        CreateAccountAction a = getAction(params);
        getJsonResult(a);

        String courseId = generateNextDemoCourseId(email, FieldValidator.COURSE_ID_MAX_LENGTH);

        CourseAttributes course = coursesLogic.getCourse(courseId);
        assertNotNull(course);
        assertEquals("Sample Course 101", course.getName());
        assertEquals(institute, course.getInstitute());
        assertEquals(timezone, course.getTimeZone());

        ZoneId zoneId = ZoneId.of(timezone);
        List<FeedbackSessionAttributes> feedbackSessionsList = feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
        for (FeedbackSessionAttributes feedbackSession : feedbackSessionsList) {
            LocalTime actualStartTime = LocalTime.ofInstant(feedbackSession.getStartTime(), zoneId);
            LocalTime actualEndTime = LocalTime.ofInstant(feedbackSession.getEndTime(), zoneId);

            assertEquals(timezone, feedbackSession.getTimeZone());
            assertEquals(LocalTime.MIDNIGHT, actualStartTime);
            assertEquals(LocalTime.MIDNIGHT, actualEndTime);
        }

        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, email);
        assertEquals(email, instructor.getEmail());
        assertEquals(name, instructor.getName());

        List<StudentAttributes> studentList = studentsLogic.getStudentsForCourse(courseId);
        List<InstructorAttributes> instructorList = instructorsLogic.getInstructorsForCourse(courseId);
        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME,
                studentList.size() + instructorList.size());

        ______TS("Normal case with invalid timezone, timezone should default to UTC");

        email = "unregisteredinstructor2@gmail.tmt";
        institute = "TEAMMATES Test Institute 2";
        timezone = "InvalidTimezone";

        accountRequest = logic.getAccountRequest(email, institute);

        params = new String[] {
                Const.ParamsNames.REGKEY, accountRequest.getRegistrationKey(),
                Const.ParamsNames.TIMEZONE, timezone,
        };

        a = getAction(params);

        getJsonResult(a);

        courseId = generateNextDemoCourseId(email, FieldValidator.COURSE_ID_MAX_LENGTH);
        course = coursesLogic.getCourse(courseId);
        assertEquals(Const.DEFAULT_TIME_ZONE, course.getTimeZone());

        feedbackSessionsList = feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
        zoneId = ZoneId.of(Const.DEFAULT_TIME_ZONE);
        for (FeedbackSessionAttributes feedbackSession : feedbackSessionsList) {
            LocalTime actualStartTime = LocalTime.ofInstant(feedbackSession.getStartTime(), zoneId);
            LocalTime actualEndTime = LocalTime.ofInstant(feedbackSession.getEndTime(), zoneId);

            assertEquals(Const.DEFAULT_TIME_ZONE, feedbackSession.getTimeZone());
            assertEquals(LocalTime.MIDNIGHT, actualStartTime);
            assertEquals(LocalTime.MIDNIGHT, actualEndTime);
        }

        verifySpecifiedTasksAdded(Const.TaskQueue.SEARCH_INDEXING_QUEUE_NAME,
                studentList.size() + instructorList.size());

        ______TS("Error: registration key already used");
        verifyInvalidOperation(params);
        verifyNoTasksAdded();

        ______TS("Error: account request not found");

        params = new String[] { Const.ParamsNames.REGKEY, "unknownregkey", };
        verifyEntityNotFound(params);
        verifyNoTasksAdded();
    }

    @Override
    @Test
    protected void testAccessControl() {
        verifyAnyLoggedInUserCanAccess();
    }

    @Test
    public void testGenerateNextDemoCourseId() {
        testGenerateNextDemoCourseIdForLengthLimit(40);
        testGenerateNextDemoCourseIdForLengthLimit(20);
    }

    private void testGenerateNextDemoCourseIdForLengthLimit(int maximumIdLength) {
        String normalIdSuffix = ".gma-demo";
        String atEmail = "@gmail.tmt";
        int normalIdSuffixLength = normalIdSuffix.length(); // 9
        String strShortWithWordDemo =
                StringHelperExtension.generateStringOfLength((maximumIdLength - normalIdSuffixLength) / 2) + "-demo";
        String strWayShorterThanMaximum =
                StringHelperExtension.generateStringOfLength((maximumIdLength - normalIdSuffixLength) / 2);
        String strOneCharShorterThanMaximum =
                StringHelperExtension.generateStringOfLength(maximumIdLength - normalIdSuffixLength);
        String strOneCharLongerThanMaximum =
                StringHelperExtension.generateStringOfLength(maximumIdLength - normalIdSuffixLength + 1);
        assertEquals(strShortWithWordDemo + normalIdSuffix,
                generateNextDemoCourseId(strShortWithWordDemo + atEmail, maximumIdLength));
        assertEquals(strShortWithWordDemo + normalIdSuffix + "0",
                generateNextDemoCourseId(strShortWithWordDemo + normalIdSuffix, maximumIdLength));
        assertEquals(strShortWithWordDemo + normalIdSuffix + "1",
                generateNextDemoCourseId(strShortWithWordDemo + normalIdSuffix + "0", maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix,
                generateNextDemoCourseId(strWayShorterThanMaximum + atEmail, maximumIdLength));
        assertEquals(strOneCharShorterThanMaximum + normalIdSuffix,
                generateNextDemoCourseId(strOneCharShorterThanMaximum + atEmail, maximumIdLength));
        assertEquals(strOneCharLongerThanMaximum.substring(1) + normalIdSuffix,
                generateNextDemoCourseId(strOneCharLongerThanMaximum + atEmail, maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix + "0",
                generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix, maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix + "1",
                generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix + "0", maximumIdLength));
        assertEquals(strWayShorterThanMaximum + normalIdSuffix + "10",
                generateNextDemoCourseId(strWayShorterThanMaximum + normalIdSuffix + "9", maximumIdLength));
        assertEquals(strOneCharShorterThanMaximum.substring(2) + normalIdSuffix + "10",
                generateNextDemoCourseId(strOneCharShorterThanMaximum.substring(1) + normalIdSuffix + "9",
                        maximumIdLength));
    }

    private String generateNextDemoCourseId(String instructorEmailOrProposedCourseId, int maximumIdLength) {
        CreateAccountAction a = new CreateAccountAction();
        return a.generateNextDemoCourseId(instructorEmailOrProposedCourseId, maximumIdLength);
    }

}
