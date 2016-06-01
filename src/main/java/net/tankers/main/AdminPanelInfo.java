package net.tankers.main;

/**
 * Created by Strongdoctor on 01-06-2016.
 */
public class AdminPanelInfo {
    private static String avgSessionTime, avgMatchTime, totalMatches, userNumber;

    public static String getAvgSessionTime() {
        return avgSessionTime;
    }

    public static void setAvgSessionTime(String avgSessionTime) {
        AdminPanelInfo.avgSessionTime = avgSessionTime;
    }

    public static String getAvgMatchTime() {
        return avgMatchTime;
    }

    public static void setAvgMatchTime(String avgMatchTime) {
        AdminPanelInfo.avgMatchTime = avgMatchTime;
    }

    public static String getTotalMatches() {
        return totalMatches;
    }

    public static void setTotalMatches(String totalMatches) {
        AdminPanelInfo.totalMatches = totalMatches;
    }

    public static String getUserNumber() {
        return userNumber;
    }

    public static void setUserNumber(String userNumber) {
        AdminPanelInfo.userNumber = userNumber;
    }
}
