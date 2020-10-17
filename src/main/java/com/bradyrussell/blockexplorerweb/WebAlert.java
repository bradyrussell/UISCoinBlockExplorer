package com.bradyrussell.blockexplorerweb;

public class WebAlert {
    public String Content;
    public String AlertClass;

    public WebAlert(String content, String aClass) {
        Content = content;
        AlertClass = aClass;
    }

    public WebAlert(String content, AlertClasses aClass) {
        Content = content;
        AlertClass = "alert-"+aClass.name().toLowerCase();
    }

    enum AlertClasses{
        Primary,
        Secondary,
        Success,
        Danger,
        Warning,
        Info,
        Light,
        Dark,
        Link
    }
}
