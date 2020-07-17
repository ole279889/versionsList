package team.mediasoft.study.java.ee.versionslist;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) {
        VersionList<String> l = new VersionList<String>();
        l.add("hfhg");
        System.out.println("added hfhg at " + new Date());
        sleep(5);
        l.add("kl;k");
        System.out.println("added kl;k at " + new Date());
        sleep(5);
        l.add("sdfs");
        System.out.println("added sdfs at " + new Date());
        sleep(5);
        l.add("vcbc");
        System.out.println("added vcbc at " + new Date());
        sleep(5);
        l.add("tryu");
        System.out.println("added tryu at " + new Date());
        sleep(5);
        l.add(1, "ffff");
        System.out.println("added ffff at position 1 at " + new Date());
        sleep(5);
        l.set(0, "gggg");
        System.out.println("set value to gggg at position 0 at " + new Date());
        sleep(5);
        l.remove("vcbc");
        System.out.println("removed vcbc at " + new Date());

        System.out.println("---------------------------------");

        ListIterator<String> litN = l.listIterator();
        while (litN.hasNext()) {
            String item = litN.next();
            System.out.println("l item => " + item);
        }

        System.out.println("---------------------------------");

        String sDate = "07/17/2020 16:00:30";
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date;
        try {
            date = formatter.parse(sDate);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            date = new Date();
        }

        List lst = l.getVersionByDate(date);

        ListIterator<String> litLst = lst.listIterator();
        while (litLst.hasNext()) {
            String item = litLst.next();
            System.out.println("result item => " + item);
        }
    }

    private static void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
            System.out.println("Awake after " + seconds + " seconds sleep..");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
