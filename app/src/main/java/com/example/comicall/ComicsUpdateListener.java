package com.example.comicall;

import java.util.List;

public interface ComicsUpdateListener {
    void updateComicsResultList(List<Comic> data);
    void setProgressBarVisibility(int visibility);
}
