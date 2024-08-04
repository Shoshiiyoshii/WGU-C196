package controllers.helpers;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.time.ZoneId;

import model.entities.Assessment;
import model.entities.Course;

public class NotificationScheduler {
    private static final int NOTIFICATION_TYPE_START = 0;
    private static final int NOTIFICATION_TYPE_END = 1;

    private final Context context;

    public NotificationScheduler(Context context) {
        this.context = context;
    }

    public void setCourseNotifications(Course course) {
        cancelNotificationsForCourse(course);

        scheduleNotification(course.getStartDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                "Course Starting", "Course " + course.getCourseName() + " starts today", course.getCourseId() * 10 + NOTIFICATION_TYPE_START);
        scheduleNotification(course.getEndDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                "Course Ending", "Course " + course.getCourseName() + " ends today", course.getCourseId() * 10 + NOTIFICATION_TYPE_END);
    }

    public void setAssessmentNotifications(Assessment assessment) {
        cancelNotificationsForAssessment(assessment);

        scheduleNotification(assessment.getAssessmentStartDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                "Assessment Starting", "Assessment " + assessment.getAssessmentName() + " starts today", assessment.getAssessmentId() * 10 + NOTIFICATION_TYPE_START);
        scheduleNotification(assessment.getAssessmentDueDate().atStartOfDay(ZoneId.systemDefault()).toEpochSecond() * 1000,
                "Assessment Due", "Assessment " + assessment.getAssessmentName() + " is due today", assessment.getAssessmentId() * 10 + NOTIFICATION_TYPE_END);
    }

    private void cancelNotificationsForCourse(Course course) {
        cancelNotification(course.getCourseId() * 10 + NOTIFICATION_TYPE_START);
        cancelNotification(course.getCourseId() * 10 + NOTIFICATION_TYPE_END);
    }

    private void cancelNotificationsForAssessment(Assessment assessment) {
        cancelNotification(assessment.getAssessmentId() * 10 + NOTIFICATION_TYPE_START);
        cancelNotification(assessment.getAssessmentId() * 10 + NOTIFICATION_TYPE_END);
    }

    private void cancelNotification(int notificationId) {
            Intent intent = new Intent(context, NotificationReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.cancel(pendingIntent);
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancel(notificationId);
            }
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
