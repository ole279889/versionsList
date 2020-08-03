package team.mediasoft.study.java.ee.versionslist;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Test {
    public static void main(String[] args) {

        VersionList<String> l = new VersionList<String>();

        // region SET_VALUES
        l.add("hfhg");
        System.out.println("added hfhg at " + new Date());
        sleep(1);
        Date checkDate = new Date();
        l.add("kl;k");
        System.out.println("added kl;k at " + new Date());
        sleep(1);

        l.set(0, "gggg");
        System.out.println("set value to gggg at position 0 at " + new Date());
        sleep(1);

        l.add("sdfs");
        System.out.println("added sdfs at " + new Date());
        sleep(1);

        l.add("vcbc");
        System.out.println("added vcbc at " + new Date());
        sleep(1);

        l.add("tryu");
        System.out.println("added tryu at " + new Date());
        sleep(1);

        l.remove("vcbc");
        System.out.println("removed vcbc at " + new Date());
        sleep(1);

        l.add(1, "ffff");
        System.out.println("added ffff at position 1 at " + new Date());
        //endregion

        System.out.println("---------------------------------");

        ListIterator<String> litN = l.listIterator();
        while (litN.hasNext()) {
            String item = litN.next();
            System.out.println("l item => " + item);
        }

        System.out.println("---------------------------------");

        /*String sDate = "07/17/2020 18:21:10";
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date;
        try {
            date = formatter.parse(sDate);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            date = new Date();
        }*/

        List lst = l.getVersionByDate(checkDate);

        System.out.println("Check time: " + checkDate);

        ListIterator<String> litLst = lst.listIterator();
        while (litLst.hasNext()) {
            String item = litLst.next();
            System.out.println("result item => " + item);
        }

        l.stream().forEach(System.out::println);
        Spliterator<String> si1 = l.stream().spliterator();
        Spliterator<String> si2 = si1.trySplit();
        System.out.println("------------------------");
        si1.forEachRemaining(System.out::println);
        System.out.println("------------------------");
        si2.forEachRemaining(System.out::println);
    }

    private static void sleep(int seconds) {
        try {
            //TimeUnit.SECONDS.sleep(seconds);
            System.out.println("Awake after " + seconds + " seconds sleep..");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
}
