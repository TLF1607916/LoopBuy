package com.shiwu.admin.controller;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Mock ServletInputStream for testing
 */
public class MockServletInputStream extends ServletInputStream {
    
    private final ByteArrayInputStream inputStream;
    
    public MockServletInputStream(String content) {
        this.inputStream = new ByteArrayInputStream(content.getBytes());
    }
    
    @Override
    public boolean isFinished() {
        return inputStream.available() == 0;
    }
    
    @Override
    public boolean isReady() {
        return true;
    }
    
    @Override
    public void setReadListener(ReadListener readListener) {
        // Not implemented for testing
    }
    
    @Override
    public int read() throws IOException {
        return inputStream.read();
    }
}
