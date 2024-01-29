package com.ht.elearning.constants;

public class ErrorMessage {
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String ASSIGNEES_MUST_BE_SPECIFIED = "Assignees must be specified";
    public static final String DEADLINE_MUST_BE_IN_FUTURE = "Deadline must be in the future";
    public static final String REQUEST_NOT_ACCEPTABLE = "Request is not acceptable";
    public static final String CATEGORY_NOT_FOUND = "Category not found";
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_IS_NOT_PROVIDER = "You are not a provider of this class";
    public static final String USER_IS_NOT_MEMBER = "You are not a member of this class";
    public static final String USER_ALREADY_EXISTS = "User already exists";
    public static final String INVITATION_ALREADY_EXISTS = "Invitation already exists";
    public static final String INVITATION_NOT_FOUND = "Invitation not found";
    public static final String ILLEGAL_EMAILS = "Invalid email detected. Please check and try again";
    public static final String USER_ALREADY_JOINED = "User already joined";
    public static final String INVALID_PASSWORD = "Invalid password";
    public static final String ASSIGNMENT_NOT_FOUND = "Assignment not found";
    public static final String CLASSWORK_NOT_FOUND = "Classwork not found";
    public static final String NO_PERMISSION = "You don't have permission to do this";
    public static final String DEADLINE_PASSED = "Deadline passed";
    public static final String ASSIGNMENT_GRADED = "Assignment has been graded";
    public static final String CLASSROOM_NOT_FOUND = "Classroom not found";
    public static final String SOMETHING_WRONG = "Something went wrong. Please try again";
    public static final String BAD_REQUEST = "Bad request";
    public static final String FORBIDDEN = "Forbidden request";
    public static final String COMMENT_NOT_FOUND = "Comment not found";
    public static final String FILE_NOT_FOUND = "File not found";
    public static final String NOTIFICATION_NOT_FOUND = "Notification not found";
    public static final String POST_NOT_FOUND = "Post not found";
    public static final String TOKEN_NOT_FOUND = "Token not found";
    public static final String MEETING_TIME_IS_INVALID = "Meeting time is invalid";
    public static final String MEETING_TIME_IS_CONFLICT = "Meeting time is conflict";
    public static final String MEETING_NOT_FOUND = "Meeting not found";
    public static final String MEETING_ENDED_OR_NOT_START = "Meeting ended or does not start";

    public static String invalidGrade(int maxScore) {
        return "Grade must be between 0 and " + maxScore;
    }
}
