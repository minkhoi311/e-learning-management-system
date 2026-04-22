package com.lmk.formatters;

import com.lmk.pojo.User;
import java.text.ParseException;
import java.util.Locale;
import org.springframework.format.Formatter;

public class UserFormatter implements Formatter<User> {

    @Override
    public String print(User user, Locale locale) {
        return String.valueOf(user.getId());
    }

    @Override
    public User parse(String userId, Locale locale) throws ParseException {
        User u = new User();
        u.setId(Integer.valueOf(userId));
        return u;
    }
}