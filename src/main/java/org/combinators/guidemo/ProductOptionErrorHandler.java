package org.combinators.guidemo;

import com.google.inject.ImplementedBy;

public interface ProductOptionErrorHandler {
    void handle(Exception e);
}
