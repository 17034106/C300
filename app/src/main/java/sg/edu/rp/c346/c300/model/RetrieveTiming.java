package sg.edu.rp.c346.c300.model;

public class RetrieveTiming {

    private String endTime;
    private String startTime;
    private int lastChangesInMin;

    public RetrieveTiming(String startTime, String endTime, int lastChangesInMin){
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastChangesInMin = lastChangesInMin;
    }


    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public int getLastChangesInMin() {
        return lastChangesInMin;
    }

    public void setLastChangesInMin(int lastChangesInMin) {
        this.lastChangesInMin = lastChangesInMin;
    }
}
