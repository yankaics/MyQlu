package com.hugo.myqlu.utils;

import com.hugo.myqlu.bean.CourseBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * @auther Hugo
 * Created on 2016/4/23 8:56.
 */
public class ParseKbFromHtml {

    private static List<CourseBean> courseList = new ArrayList<>();

    public static List<CourseBean> getKB(String response) {
        response = response.replace("<br>", "hu");
        Document document = Jsoup.parse(response);
        Element table1 = document.getElementById("Table1");
        //拿到tbody
        Element tbody = table1.select("tbody").get(0);
        //去除前面两行 ，时间和早晨
        tbody.child(0).remove();
        tbody.child(0).remove();
        //去除上午，下午，晚上
        tbody.child(0).child(0).remove();
        tbody.child(4).child(0).remove();
        tbody.child(8).child(0).remove();
        //去除无用行之后，还剩余
        Elements trs = tbody.select("tr");
        int rowNum = trs.size();
        //用map储存数据
        for (int i = 0; i < rowNum; i++) {
            if (i % 2 == 0) {
                //拿到每一行
                Element tr = trs.get(i);
                //一共有多少列 -2 晚自习有三节课，手动去掉最后一行，即第十一节课
                int columnNum = tr.childNodeSize() - 2;
                for (int j = 1; j < columnNum - 1; j++) {
                    String timeDetail = null;
                    switch (i) {
                        case 0:
                            timeDetail = "第1,2节";
                            break;
                        case 2:
                            timeDetail = "第3,4节";
                            break;
                        case 4:
                            timeDetail = "第5,6节";
                            break;
                        case 6:
                            timeDetail = "第7,8节";
                            break;
                        case 8:
                            timeDetail = "第9,11节";
                    }

                    Element colum = tr.child(j);
                    if (colum.hasAttr("rowspan")) {
                        CourseBean course = new CourseBean();
                        String text = colum.text();
                        //基于java的web开发(JSP/Sevlet) 周三第5,6节{第1-16周} 尹红丽 1号公教楼602
                        String[] strings = text.split("hu");
                        System.out.println("text :" + text);
                        System.out.println("text的长度 :" + strings.length);
                        String name = "";
                        if (strings.length > 4) {
                            name = strings[0] + "-" + strings[1].substring(strings[1].indexOf("|") + 1, strings[1].indexOf("}"));
                            String name2 = strings[5] + "-" + strings[6].substring(strings[6].indexOf("|") + 1, strings[6].indexOf("}"));
                            name = name + "\n" + name2;
                        } else {
                            name = strings[0];
                            if (strings[1].contains("单周")) {
                                name = name + "-单周";
                            } else if (strings[1].contains("双周")) {
                                name = name + "-双周";
                            }
                        }
                        System.out.println("name :" + name);
                        course.setCourseName(name);
                        course.setCourseTime(String.valueOf(j));
                        course.setCourstTimeDetail(timeDetail);
                        course.setCourseTeacher(strings[2]);
                        course.setCourseLocation(strings[3]);
                        courseList.add(course);
                    }
                }
            }
        }

        for (CourseBean course : courseList) {
            System.out.println(course.toString());
        }
        return courseList;
    }

}