package com.yunbiao.internetcafe_ai;

import android.app.Application;
import android.content.Context;

import com.elvishew.xlog.LogConfiguration;
import com.elvishew.xlog.LogLevel;
import com.elvishew.xlog.XLog;
import com.elvishew.xlog.interceptor.BlacklistTagsFilterInterceptor;
import com.elvishew.xlog.printer.AndroidPrinter;
import com.elvishew.xlog.printer.ConsolePrinter;
import com.elvishew.xlog.printer.Printer;
import com.elvishew.xlog.printer.file.FilePrinter;
import com.elvishew.xlog.printer.file.backup.NeverBackupStrategy;
import com.elvishew.xlog.printer.file.naming.DateFileNameGenerator;
import com.yunbiao.internetcafe_ai.common.RetryInterceptor;
import com.yunbiao.internetcafe_ai.utils.SpeechUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class APP extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initHttp();
        initLog();
        SpeechUtil.getInstance().init();
    }

    private void initHttp(){
        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(60 * 3, TimeUnit.SECONDS)
                .writeTimeout(60 * 3, TimeUnit.SECONDS)
                .readTimeout(60 * 3, TimeUnit.SECONDS)
                .addInterceptor(new RetryInterceptor())
                .retryOnConnectionFailure(true)
                .build();
        OkHttpUtils.initClient(build);
    }

    private void initLog(){
        LogConfiguration config = new LogConfiguration.Builder()
                .logLevel(LogLevel.ALL)            // Specify log level, logs below this level won't be printed, default: LogLevel.ALL
                .tag("MY_TAG")                                         // Specify TAG, default: "X-LOG"
                .t()                                                   // Enable thread info, disabled by default
                .st(2)                                                 // Enable stack trace info with depth 2, disabled by default
                .b()                                                   // Enable border, disabled by default
//                .jsonFormatter(new MyJsonFormatter())                  // Default: DefaultJsonFormatter
//                .xmlFormatter(new MyXmlFormatter())                    // Default: DefaultXmlFormatter
//                .throwableFormatter(new MyThrowableFormatter())        // Default: DefaultThrowableFormatter
//                .threadFormatter(new MyThreadFormatter())              // Default: DefaultThreadFormatter
//                .stackTraceFormatter(new MyStackTraceFormatter())      // Default: DefaultStackTraceFormatter
//                .borderFormatter(new MyBoardFormatter())               // Default: DefaultBorderFormatter
//                .addObjectFormatter(AnyClass.class,                    // Add formatter for specific class of object
//                        new AnyClassObjectFormatter())                     // Use Object.toString() by default
                .addInterceptor(new BlacklistTagsFilterInterceptor(    // Add blacklist tags filter
                        "blacklist1", "blacklist2", "blacklist3"))
//                .addInterceptor(new MyInterceptor())                   // Add a log interceptor
                .build();

        Printer androidPrinter = new AndroidPrinter();             // Printer that print the log using android.util.Log
        Printer consolePrinter = new ConsolePrinter();             // Printer that print the log to console using System.out
        Printer filePrinter = new FilePrinter                      // Printer that print the log to the file system
                .Builder("/sdcard/xlog/")                              // Specify the path to save log file
                .fileNameGenerator(new DateFileNameGenerator())        // Default: ChangelessFileNameGenerator("log")
                .backupStrategy(new NeverBackupStrategy())             // Default: FileSizeBackupStrategy(1024 * 1024)
//                .cleanStrategy(new FileLastModifiedCleanStrategy(MAX_TIME))     // Default: NeverCleanStrategy()
//                .flattener(new MyFlattener())                          // Default: DefaultFlattener
                .build();

        XLog.init(                                                 // Initialize XLog
                config,                                                // Specify the log configuration, if not specified, will use new LogConfiguration.Builder().build()
                androidPrinter,                                        // Specify printers, if no printer is specified, AndroidPrinter(for Android)/ConsolePrinter(for java) will be used.
                consolePrinter,
                filePrinter);
    }

    public static Context getAppContext(){
        return context;
    }
}
