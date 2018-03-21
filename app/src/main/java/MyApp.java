import android.app.Application;

/**
 * Created by kpraveen on 2018-03-21.
 */

public class MyApp extends Application {

        private String userName;

        public String getUserName(){
            return userName;
        }
        public void setUserName(String s){
            userName = s;
        }
}

