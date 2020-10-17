package com.bradyrussell.blockexplorerweb;

public class WebAlert {
    public String Content;
    public String AlertClass;
    public String HTML;

    public WebAlert(String content, String aClass) {
        Content = content;
        AlertClass = aClass;
    }

    public WebAlert(String content, AlertClasses aClass) {
        Content = content;
        AlertClass = "alert-"+aClass.name().toLowerCase();
    }

    public WebAlert(String content, AlertClasses aClass, String HTML) {
        Content = content;
        AlertClass = "alert-"+aClass.name().toLowerCase();
        this.HTML = HTML;
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
