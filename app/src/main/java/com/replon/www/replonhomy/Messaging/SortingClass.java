package com.replon.www.replonhomy.Messaging;

import com.replon.www.replonhomy.Messaging.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SortingClass {

    List<User> sortingClass = new ArrayList<>();

    public SortingClass(List<User> userSort) {
        this.sortingClass = userSort;
    }
    public List<User> getSortedByTime() {
        Collections.sort(sortingClass, User.timeCompare);
        return sortingClass;
    }

    public List<User> getSortedByFlat() {
        Collections.sort(sortingClass, User.flatCompare);
        return sortingClass;
    }

}