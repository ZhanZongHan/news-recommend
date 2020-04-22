package com.zzh.tools;//package com.zzh.tools;
//
//
//import com.zzh.pojo.BrowsingLog;
//import com.zzh.pojo.News;
//import com.zzh.pojo.User;
//import org.junit.Test;
//
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
//import java.sql.Timestamp;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Random;
//import java.util.Scanner;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//public class DataHandle {
//    private static SimpleDateFormat simFormat = new SimpleDateFormat("yyyyMMdd");
//    private static SimpleDateFormat fullSimFormat = new SimpleDateFormat("yyyyMMdd HHmmss");
//
//    public static void main(String[] args) {
//        addLogs();
//    }
//
//    public static void addNewses() {
//        String[] patterns = new String[3];
//        patterns[0] = "<url>(.*)</url>";
//        patterns[1] = "<contenttitle>(.*)</contenttitle>";
//        patterns[2] = "<content>(.*)</content>";
//
//        Pattern[] p = new Pattern[3];
//        p[0] = Pattern.compile(patterns[0]);
//        p[1] = Pattern.compile(patterns[1]);
//        p[2] = Pattern.compile(patterns[2]);
//        News news = new News();
//
//        // 转时间格式
//
//        try (
//                FileInputStream inputStream = new FileInputStream("E:\\Thunder\\news_tensite_xml.full\\news_tensite_xml.dat");
//                Scanner sc = new Scanner(inputStream, "gbk")) {
//            int cnt = 1;
//            int newsCnt = 0;
//            Matcher matcher = null;
//            Date date = null;
//            String dateStr = null;
//            while (sc.hasNextLine()) {
//
//                String line = sc.nextLine();
//                int mod = cnt % 6;
//
//                switch (mod) {
//                    case 2:
//                        matcher = p[0].matcher(line);
//                        if (matcher.find())
//                            news.setUrl(matcher.group(1));
//                        dateStr = news.getUrl().split("/")[3];
//                        if (isLegalDate(dateStr)) {
//                            date = simFormat.parse(dateStr);
//                        }
//                        news.setPublicationDate(date);
//                        break;
//                    case 4:
//                        matcher = p[1].matcher(line);
//                        if (matcher.find())
//                            news.setTitle(matcher.group(1));
//                        break;
//                    case 5:
//                        matcher = p[2].matcher(line);
//                        if (matcher.find())
//                            news.setContent(matcher.group(1));
//                        break;
//                    default:
//                        break;
//                }
//                if (mod == 0) {
//                    addANews(news);
//                    newsCnt++;
//                    if (newsCnt % 100 == 0)
//                        System.out.println(newsCnt);
//                }
//                cnt++;
//            }
//
//            if (sc.ioException() != null) {
//                //occur error when scan
//                throw sc.ioException();
//            }
//        } catch (FileNotFoundException e) {
//            System.out.println("file is not found");
//        } catch (IOException e) {
//            System.out.println("file read occur error");
//        } catch (Exception e) {
//            System.out.println(news);
//            System.out.println("========================");
//            System.out.println(e);
//            System.out.println("occur error");
//        }
//    }
//
//    public static void addANews(News news) {
//        Connection conn = JdbcUtils.getConn();
//        String sql = "insert into news_info(title, content, publication_date, url)" +
//                " values(?, ?, ?, ?)";
//        try {
//            PreparedStatement ptmt = conn.prepareStatement(sql);
//            ptmt.setString(1, news.getTitle());
//            ptmt.setString(2, news.getContent());
//            ptmt.setTimestamp(3, new Timestamp(news.getPublicationDate().getTime()));
//            ptmt.setString(4, news.getUrl());
//
//            ptmt.execute();
//        } catch (SQLException e) {
//            System.out.println(news);
//            e.printStackTrace();
//        }
//    }
//
//    public static boolean isLegalDate(String date) {
//        if (date.length() != 8)
//            return false;
//        for (int i = 0; i < date.length(); i++) {
//            if (date.charAt(i) < '0' || date.charAt(i) > '9')
//                return false;
//        }
//        return true;
//    }
//
//    public static User randomUser() throws ParseException {
//        // 随机生成用户数据
//        User user = new User();
//        user.setAuthority(0);
//        user.setPassword("123456");
//        user.setBirthday(simFormat.parse(randomDate(1970, 2010)));
//        user.setGender(randomGender());
//        user.setUsername(randomUsername());
//        user.setRegisterDate(new Date());
//        return user;
//    }
//
//    public static void addUsers() {
//        // 添加10000用户
//        User user = null;
//        try {
//            for (int i = 0; i < 10000; i++) {
//                user = randomUser();
//                addAUser(user);
//            }
//        } catch (ParseException e) {
//            System.out.println(user);
//            e.printStackTrace();
//        }
//    }
//
//    public static void addAUser(User user) {
//        Connection conn = JdbcUtils.getConn();
//        String sql = "insert into user_info(username, password, register_date, gender, birthday, authority)" +
//                " values(?, ?, ?, ?, ?, ?)";
//        try {
//            PreparedStatement ptmt = conn.prepareStatement(sql);
//            ptmt.setString(1, user.getUsername());
//            ptmt.setString(2, user.getPassword());
//            ptmt.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
//            ptmt.setInt(4, user.getGender());
//            ptmt.setTimestamp(5, new Timestamp(user.getBirthday().getTime()));
//            ptmt.setInt(6, user.getAuthority());
//
//            ptmt.execute();
//        } catch (SQLException e) {
//            System.out.println(user);
//            e.printStackTrace();
//        }
//    }
//
//    private static Random random = new Random();
//
//    public static String randomUsername() {
//        // 随机名
//        int length = random.nextInt(5) + 5; // 长度5->10
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < length; i++) {
//            sb.append((char) (random.nextInt(26) + 65));
//        }
//        return sb.toString();
//    }
//
//    public static String randomDate(int startYear, int endYear) {
//        // 随机生成年份startYear~endYear的日期
//
//        int year = random.nextInt(endYear - startYear + 1) + startYear;   // 1970->2010
//        int month = random.nextInt(12) + 1; // 1->12
//        int day = random.nextInt(28) + 1; // 1->28
//        StringBuilder sb = new StringBuilder();
//        sb.append(year);
//        if (month < 10)
//            sb.append("0").append(month);
//        else
//            sb.append(month);
//        if (day < 10)
//            sb.append("0").append(day);
//        else
//            sb.append(day);
//        return sb.toString();
//    }
//
//    public static String randomTime() {
//        // 随机生成时分秒
//        int hour = random.nextInt(24);  // 0~23
//        int minute = random.nextInt(60);    // 0~59
//        int second = random.nextInt(60);    // 0~59
//        StringBuilder sb = new StringBuilder();
//        if (hour < 10)
//            sb.append("0").append(hour);
//        else
//            sb.append(hour);
//        if (minute < 10)
//            sb.append("0").append(minute);
//        else
//            sb.append(minute);
//        if (second < 10)
//            sb.append("0").append(second);
//        else
//            sb.append(second);
//        return sb.toString();
//    }
//
//    public static int randomGender() {
//        int n = random.nextInt(10);
//        // 70%为男 30%为女
//        return n < 7 ? 1 : 0;
//    }
//
//    public static void addLogs() {
//        // 随机30w条浏览记录
//        BrowsingLog log = null;
//        try {
//            for (int i = 0; i < 200000; i++) {
//                log = randomALog();
//                addALog(log);
//                if (i % 100 == 0)
//                    System.out.println(i);
//            }
//        } catch (ParseException e) {
//            System.out.println(log);
//            e.printStackTrace();
//        }
//
//    }
//
//    public static void addALog(BrowsingLog log) {
//        Connection conn = JdbcUtils.getConn();
//        String sql = "insert into log_info(user_id, news_id, browsing_date)" +
//                " values(?, ?, ?)";
//        try {
//            PreparedStatement ptmt = conn.prepareStatement(sql);
//            ptmt.setInt(1, log.getUserId());
//            ptmt.setInt(2, log.getNewsId());
//            ptmt.setTimestamp(3, new Timestamp(log.getBrowsingDate().getTime()));
//
//            ptmt.execute();
//        } catch (SQLException e) {
//            System.out.println(log);
//            e.printStackTrace();
//        }
//    }
//
//    public static BrowsingLog randomALog() throws ParseException {
//        BrowsingLog log = new BrowsingLog();
//        log.setUserId(randomId(10000)); // 随机生成1~10000的userId
//        log.setNewsId(randomId(30000)); // 随机生成1~30000的userId
//        // 随机生成2019~2020年里的具体时间
//        log.setBrowsingDate(fullSimFormat.parse(randomDate(2019, 2020) + " " + randomTime()));
//        return log;
//    }
//
//    public static Integer randomId(int rand) {
//        return (random.nextInt(rand) + 1);
//    }
//
//
//
//    @Test
//    public void test() {
//
//    }
//}
//
