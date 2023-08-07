package com.programmerAbc;

import java.util.List;

public interface LoadDataResult<T> {
    void result(boolean result, String message, boolean isLast, List<T> resposneData);
}
