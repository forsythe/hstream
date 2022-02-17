package com.forsythe.pullstream;

/**
 * Represents a source that can provide inputs
 */
public interface Source {
    boolean hasNext();

    int getNext();
}
