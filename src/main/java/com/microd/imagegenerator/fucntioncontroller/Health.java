package com.microd.imagegenerator.fucntioncontroller;

import java.util.function.Supplier;

public class Health implements Supplier<String> {

    @Override
    public String get() {
        return "OK";
    }
}
