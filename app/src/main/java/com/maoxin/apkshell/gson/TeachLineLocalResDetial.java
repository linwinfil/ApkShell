package com.maoxin.apkshell.gson;

import java.util.List;

/**
 * 内置资源构图线data_json通用bean
 *
 * @author lmx
 * Created by lmx on 2018/4/12.
 */
public class TeachLineLocalResDetial
{
    public String art_id;
    public String name;
    public String stat_id;
    public List<Group> group;

    public static class Group
    {
        public String id;
        public String name;
        public String tj_id;
        public String cover;
        public String source;
        public String ratio;
        public String difficulty;
        public String is_prompt;
        public String prompt_pic;

        public List<Tag> tags;
        public List<Prompt> prompt;

        public static class Tag
        {
            public String id;
            public String content;
        }

        public static class Prompt
        {
            public String id;
            public String title;
            public String content;
        }
    }

}
