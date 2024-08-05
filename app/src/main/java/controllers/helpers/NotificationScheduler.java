package controllers.helpers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.time.ZoneId;

import model.entities.Assessment;
import model.entities.Course;

public class NotificationScheduler {
    // By multiplying the assessment or course ID by 10, I get a block of 10 notification ID's
    // that can be dedicated to objects with that ID. For example, if I have assessment with an ID of 3,
    // and a course with the ID of 3, I can assign notification ID's this way -> 3 * 10 = 30.
    // 30 + 0 and 30 + 1 are for notifications for a course with ID 3, 30 + 2 and 30 + 3
    // are for notifications for an assessment with ID 3, and 2 or 3 denote the type of notification.
    // This way I know what notification ID's will be, without having to update my database.
    private static final int NOTIFICATION_BLOCK = 10;
    private static final int NOTIFICATION_TYPE_COURSE_START = 0;
    private static final int NOTIFICATION_TYPE_COURSE_END = 1;
    private static final int NOTIFICATION_TYPE_ASSESSMENT_START = 2;
    private static final int NOTIFICATION_TYPE_ASSESSMENT_END = 3;

    private final Context context;

    public NotificationScheduler(Context context) {
        this.context = context;
    }

    public void setCourseNotifications(Course course) {
        scheduleNotification(course.getStartDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                "Course Starting", "Course " + course.getCourseName() +
                        " starts today", course.getCourseId() * NOTIFICATION_BLOCK + NOTIFICATION_TYPE_COURSE_START);
        scheduleNotification(course.getEndDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                "Course Ending", "Course " + course.getCourseName() + " ends today",
                course.getCourseId() * NOTIFICATION_BLOCK + NOTIFICATION_TYPE_COURSE_END);
    }

    public void setAssessmentNotifications(Assessment assessment) {
        scheduleNotification(assessment.getAssessmentStartDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                "Assessment Starting", "Assessment " + assessment.getAssessmentName() + " starts today",
                assessment.getAssessmentId() * NOTIFICATION_BLOCK + NOTIFICATION_TYPE_ASSESSMENT_START);
        scheduleNotification(assessment.getAssessmentDueDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                "Assessment Due", "Assessment " + assessment.getAssessmentName() +
                        " is due today", assessment.getAssessmentId() * NOTIFICATION_BLOCK + NOTIFICATION_TYPE_ASSESSMENT_END);
    }

    private void scheduleNotification(long triggerTime, String title, String notificationMsg, int notificationId) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("notificationMsg", notificationMsg);
        intent.putExtra("notificationId", notificationId);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
    }
}
