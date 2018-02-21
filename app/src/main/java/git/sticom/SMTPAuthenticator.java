package git.sticom;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Created by Ade on 08/02/2018.
 */

public class SMTPAuthenticator extends Authenticator {
    public SMTPAuthenticator() {

        super();
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        String username = "rosidinade4@gmail.com";
        String password = "realita28";
        if ((username != null) && (username.length() > 0) && (password != null)
                && (password.length() > 0)) {

            return new PasswordAuthentication(username, password);
        }

        return null;
    }
}
